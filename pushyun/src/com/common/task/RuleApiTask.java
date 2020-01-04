package com.common.task;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.bean.BaseRecord;
import com.bean.BusinessRule;
import com.bean.BusinessTask;
import com.common.config.SpringConfig;
import com.common.service.BaseService;
import com.common.tools.DateUtil;
import com.common.type.BusinessRuleState;
import com.common.web.Pages;
import com.module.business.service.RuleService;

public class RuleApiTask implements Runnable{
	
private final Logger logger = Logger.getLogger(RuleApiTask.class.getName());
	
	private Semaphore semaphore;
	
	private int perPageNum ;
	
	private int spage ;
	
	private BusinessTask businessTask;
	
	private String rootPath;
	
	private String buildPath;
	
	private String pathName;
	
	private CountDownLatch latch;

	public RuleApiTask(CountDownLatch latch,Semaphore semaphore,int perPageNum ,int spage,
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
		BaseService businessRuleService=(BaseService) SpringConfig.getInstance().getService(BusinessRule.class);
		RuleService ruleService=(RuleService)  SpringConfig.getInstance().getService(RuleService.class);
		try {
			semaphore.acquire();
			List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
			List<Order> orders = new ArrayList<Order>();// 排序条件
			expList.add(Restrictions.eq("BusinessChannel.id", businessTask.getBusinessChannel().getId()));
			expList.add(Restrictions.eq("state", BusinessRuleState.normal.getCode()));
			logger.info(String.format("api start :%s  id:%s", DateUtil.getDateTime(new Date()),businessTask.getId()));
			Pages pages = new Pages();
			pages.setPerPageNum(perPageNum);
			pages.setSpage(spage);// 列表开始位置
			List<BaseRecord> list = businessRuleService.findList(
					expList, orders, pages.getSpage(), pages.getPerPageNum(),null);
			if (list == null || list.size() == 0) {
				return;
			}
			logger.info(String.format("api end :%s  id:%s", DateUtil.getDateTime(new Date()),businessTask.getId()));
			StringBuilder resultsb= new StringBuilder();
			for(Object o:list){
				BusinessRule businessRule=(BusinessRule)o;
				resultsb.append(businessRule.getId());
				resultsb.append(",");
			}
			ruleService.createApiFile(resultsb.toString(), rootPath, buildPath+File.separator+pathName);
		} catch (Exception e) {
			logger.error("", e);
		}finally{  
			semaphore.release();  
			latch.countDown();
		}	
	}

}
