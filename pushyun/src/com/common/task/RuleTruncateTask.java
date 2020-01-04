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
import com.bean.BusinessRule;
import com.bean.BusinessRuleDetail;
import com.bean.BusinessTask;
import com.common.comm.Constants;
import com.common.config.SpringConfig;
import com.common.service.BaseService;
import com.common.tools.DateUtil;
import com.common.web.Pages;
import com.common.web.PaginatedListHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.module.business.service.RuleService;

public class RuleTruncateTask implements Runnable{
	
	private final Logger logger = Logger.getLogger(RuleTruncateTask.class.getName());
	
	
	private CountDownLatch latch;
	
	private int perPageNum ;
	
	private int spage ;
	
	private BusinessTask businessTask;
	
	public RuleTruncateTask(CountDownLatch latch,
			int perPageNum ,int spage,BusinessTask businessTask){
		this.latch=latch;
		this.perPageNum=perPageNum;
		this.spage=spage;
		this.businessTask=businessTask;
	}
	
	
	@Override
	public void run() {
		RuleService ruleService=(RuleService)  SpringConfig.getInstance().getService(RuleService.class);
		BaseService businessRuleDetailService=(BaseService) SpringConfig.getInstance().getService(BusinessRuleDetail.class);
		BaseService businessRuleService=(BaseService) SpringConfig.getInstance().getService(BusinessRule.class);
		try{
			int state = Constants.STATE_OPERATOR_LOST;
			logger.info(String.format("truncate start :%s  id:%s", DateUtil.getDateTime(new Date()),businessTask.getId()));
			List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
			List<Order> orders = new ArrayList<Order>();// 排序条件
			DetachedCriteria dc=DetachedCriteria.forClass(BusinessRule.class);
			JsonObject jsonObject=new JsonParser().parse(businessTask.getContent()).getAsJsonObject();
			ruleService.setExpList(expList, jsonObject, dc);
			List<BaseRecord> list = businessRuleService.findList(expList, orders, spage,perPageNum, dc);
			if (list == null 
					|| list.size() == 0) {
				 return;
			}
			logger.info(String.format("truncate end :%s  id:%s", DateUtil.getDateTime(new Date()),businessTask.getId()));
			List<BaseRecord> businessRuleDetailList=new ArrayList<BaseRecord>();
			List<BaseRecord> businessRuleList=new ArrayList<BaseRecord>();
			for(Object o:list){
				BusinessRule businessRule=(BusinessRule)o;
				if(businessRule.getBusinessRuleDetails()!=null
						&&businessRule.getBusinessRuleDetails().size()>0){
					for(Object object:businessRule.getBusinessRuleDetails()){
						BusinessRuleDetail businessRuleDetail=(BusinessRuleDetail)object;
						businessRuleDetailList.add(businessRuleDetail);
					}
				}
				businessRuleList.add(businessRule);
			}
			if(businessRuleDetailList.size()>0){
				state=businessRuleDetailService.deleteObject(businessRuleDetailList);
			}
			if(businessRuleList.size()>0){
				state=businessRuleService.deleteObject(businessRuleList);
			}
		}catch (Exception e) {
			logger.error("",e);
		}finally{
			latch.countDown();
		}
	}

}
