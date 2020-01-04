package com.common.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;

import com.bean.BaseRecord;
import com.bean.BusinessDns;
import com.bean.BusinessTask;
import com.common.comm.Constants;
import com.common.config.SpringConfig;
import com.common.service.BaseService;
import com.common.tools.DateUtil;
import com.common.web.Pages;
import com.common.web.PaginatedListHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.module.business.service.DnsService;

public class RuleTruncateDnsTask implements Runnable{
	
	private final Logger logger = Logger.getLogger(RuleTruncateDnsTask.class.getName());
	
	
	private CountDownLatch latch;
	
	private int perPageNum ;
	
	private int spage ;
	
	private BusinessTask businessTask;
	
	public RuleTruncateDnsTask(CountDownLatch latch,
			int perPageNum ,int spage,BusinessTask businessTask){
		this.latch=latch;
		this.perPageNum=perPageNum;
		this.spage=spage;
		this.businessTask=businessTask;
	}
	
	
	@Override
	public void run() {
		DnsService dnsService=(DnsService)  SpringConfig.getInstance().getService(DnsService.class);
		BaseService businessDnsService=(BaseService) SpringConfig.getInstance().getService(BusinessDns.class);
		try{
			int state = Constants.STATE_OPERATOR_LOST;
			logger.info(String.format("truncate start :%s  id:%s", DateUtil.getDateTime(new Date()),businessTask.getId()));
			List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
			List<Order> orders = new ArrayList<Order>();// 排序条件
			DetachedCriteria dc=DetachedCriteria.forClass(BusinessDns.class);
			JsonObject jsonObject=new JsonParser().parse(businessTask.getContent()).getAsJsonObject();
			dnsService.setExpList(expList, jsonObject, dc);
			List<BaseRecord> list = businessDnsService.findList(expList, orders, spage,perPageNum, dc);
			if (list == null 
					|| list.size() == 0) {
				 return;
			}
			logger.info(String.format("truncate end :%s  id:%s", DateUtil.getDateTime(new Date()),businessTask.getId()));
			List<BaseRecord> businessDnsList=new ArrayList<BaseRecord>();
			for(Object o:list){
				BusinessDns businessDns=(BusinessDns)o;
				businessDnsList.add(businessDns);
			}
			if(businessDnsList.size()>0){
				state=businessDnsService.deleteObject(businessDnsList);
			}
		}catch (Exception e) {
			logger.error("",e);
		}finally{
			latch.countDown();
		}
	}

}
