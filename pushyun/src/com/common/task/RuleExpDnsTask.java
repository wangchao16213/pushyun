package com.common.task;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;

import com.bean.BaseRecord;
import com.bean.BusinessDns;
import com.bean.BusinessTask;
import com.common.config.SpringConfig;
import com.common.service.BaseService;
import com.common.tools.DateUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.module.business.service.DnsService;

public class RuleExpDnsTask implements Runnable{
	
	private final Logger logger = Logger.getLogger(RuleExpDnsTask.class.getName());
	
	private Semaphore semaphore;
	
	private int perPageNum ;
	
	private int spage ;
	
	private BusinessTask businessTask;
	
	private String rootPath;
	
	private String buildPath;
	
	private String pathName;
	
	private CountDownLatch latch;
	
	public RuleExpDnsTask(CountDownLatch latch,Semaphore semaphore,int perPageNum ,int spage,
			BusinessTask businessTask,String rootPath,String buildPath,String pathName){
		this.latch=latch;
		this.semaphore=semaphore;
		this.perPageNum=perPageNum;
		this.spage=spage;
		this.businessTask=businessTask;
		this.rootPath=rootPath;
		this.buildPath=buildPath;
		this.pathName=pathName;
	}

	@Override
	public void run() {
		BaseService businessDnsService=(BaseService) SpringConfig.getInstance().getService(BusinessDns.class);
		DnsService dnsService=(DnsService)  SpringConfig.getInstance().getService(DnsService.class);
		try {
			semaphore.acquire();
			logger.info(String.format("exp start :%s  id:%s", DateUtil.getDateTime(new Date()),businessTask.getId()));
			List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
			List<Order> orders = new ArrayList<Order>();// 排序条件
			DetachedCriteria dc=DetachedCriteria.forClass(BusinessDns.class);
			JsonObject jsonObject=new JsonObject();
			if(StringUtils.isNotBlank(businessTask.getContent())){
				jsonObject=new JsonParser().parse(businessTask.getContent()).getAsJsonObject();
			}
			dnsService.setExpList(expList, jsonObject, dc);
			List<BaseRecord> list =businessDnsService.findList(expList, orders, spage, perPageNum,dc);
			if (list == null || list.size() == 0) {
				return;
			}
			logger.info(String.format("exp end :%s  id:%s", DateUtil.getDateTime(new Date()),businessTask.getId()));
			List<Object> expportList = new ArrayList<Object>();
			expportList.addAll(list);
			dnsService.creatExcelFile(expportList, rootPath, buildPath+File.separator+pathName);
		} catch (Exception e) {
			logger.error("", e);
		}finally{  
			semaphore.release();  
			latch.countDown();
		}
	}

}
