package com.common.task;

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
import com.bean.BusinessRule;
import com.bean.BusinessRuleDetail;
import com.bean.BusinessTask;
import com.common.config.SpringConfig;
import com.common.service.BaseService;
import com.common.tools.DateUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.module.business.service.RuleService;

public class RuleResetTask implements Runnable{
	
	private final Logger logger = Logger.getLogger(RuleResetTask.class.getName());
	
	private Semaphore semaphore;
	
	private int perPageNum ;
	
	private int spage ;
	
	private BusinessTask businessTask;
	
	private CountDownLatch latch;
	
	public RuleResetTask(CountDownLatch latch,Semaphore semaphore,int perPageNum ,int spage,BusinessTask businessTask){
		this.latch=latch;
		this.semaphore=semaphore;
		this.perPageNum=perPageNum;
		this.spage=spage;
		this.businessTask=businessTask;
	}

	@Override
	public void run() {
		BaseService businessRuleService=(BaseService) SpringConfig.getInstance().getService(BusinessRule.class);
		RuleService ruleService=(RuleService)  SpringConfig.getInstance().getService(RuleService.class);
		BaseService businessRuleDetailService=(BaseService) SpringConfig.getInstance().getService(BusinessRuleDetail.class);
		try{
			logger.info(String.format("reset start :%s  id:%s", DateUtil.getDateTime(new Date()),businessTask.getId()));
			List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
			List<Order> orders = new ArrayList<Order>();// 排序条件
			DetachedCriteria dc=DetachedCriteria.forClass(BusinessRule.class);
			JsonObject jsonObject=new JsonObject();
			if(StringUtils.isNotBlank(businessTask.getContent())){
				jsonObject=new JsonParser().parse(businessTask.getContent()).getAsJsonObject();
			}
			ruleService.setExpList(expList, jsonObject, dc);
			List<BaseRecord> list = businessRuleService.findList(expList, orders, spage,perPageNum, dc);
			if (list == null || list.size() == 0) {
				return;
			}
			for (Object o : list) {
				BusinessRule businessRule=(BusinessRule)o;
				businessRule.setNum(0);
				businessRule.setUpdatetime(new Date());
				businessRuleService.updateObject(businessRule,true);
				if(businessRule.getBusinessRuleDetails()==null
						||businessRule.getBusinessRuleDetails().size()==0){
						continue;
				}
				for(Object object:businessRule.getBusinessRuleDetails()){
					BusinessRuleDetail businessRuleDetail=(BusinessRuleDetail)object;
					businessRuleDetail.setNum(0);
					businessRuleDetail.setUpdatetime(new Date());
					businessRuleDetailService.updateObject(businessRuleDetail,true);
				}
			}
			logger.info(String.format("reset end :%s  id:%s", DateUtil.getDateTime(new Date()),businessTask.getId()));
		}catch (Exception e) {
			logger.error("", e);
		}finally{  
			semaphore.release();  
			latch.countDown();
		}
	}
}
