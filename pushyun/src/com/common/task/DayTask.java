package com.common.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.bean.BusinessChannel;
import com.bean.BusinessRule;
import com.bean.BusinessRuleDetail;
import com.bean.DataChannelLog;
import com.bean.DataStatistics;
import com.bean.LogRuleStatistics;
import com.bean.LogSnifferStatistics;
import com.common.config.SpringConfig;
import com.common.service.BaseService;
import com.common.tools.DateUtil;
import com.common.type.BusinessChannelOnlinestate;
import com.common.web.PaginatedListHelper;

public class DayTask implements Job{
	
	private final Logger logger = Logger.getLogger(DayTask.class.getName());

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try{
			updateBusinessChannel();
		}catch (Exception e) {
			logger.error("",e);
		}
		try{
			deleteDataStatistics();
		}catch (Exception e) {
			logger.error("",e);
		}
		try{
			deleteDataChannelLog();
		}catch (Exception e) {
			logger.error("",e);
		}
		try{
			deleteLogSnifferStatistics();
		}catch (Exception e) {
			logger.error("",e);
		}
		try{
			deleteLogRuleStatistics();
		}catch (Exception e) {
			logger.error("",e);
		}
	}
	
	private void deleteLogRuleStatistics(){
		BaseService logRuleStatisticsService=(BaseService) SpringConfig.getInstance().getService(LogRuleStatistics.class);
		Calendar calendar = Calendar.getInstance();  
		calendar.setTime(new Date());//month 为指定月份任意日期  
	    calendar.add(Calendar.DATE, -10);
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		expList.add(Restrictions.le("updatetime", calendar.getTime()));
		for(;;){
			PaginatedListHelper logRuleStatisticsPlh=logRuleStatisticsService.findList(expList, null, null, null);
			if(logRuleStatisticsPlh==null
					||logRuleStatisticsPlh.getList()==null
					||logRuleStatisticsPlh.getList().size()==0){
				return;
			}
			logRuleStatisticsService.deleteObject(logRuleStatisticsPlh.getList());
		}
	}
	
	private void deleteLogSnifferStatistics(){
		BaseService logSnifferStatisticsService=(BaseService) SpringConfig.getInstance().getService(LogSnifferStatistics.class);
		Calendar calendar = Calendar.getInstance();  
		calendar.setTime(new Date());//month 为指定月份任意日期  
	    calendar.add(Calendar.DATE, -10);
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		expList.add(Restrictions.le("updatetime", calendar.getTime()));
		for(;;){
			PaginatedListHelper logSnifferStatisticsPlh=logSnifferStatisticsService.findList(expList, null, null, null);
			if(logSnifferStatisticsPlh==null
					||logSnifferStatisticsPlh.getList()==null
					||logSnifferStatisticsPlh.getList().size()==0){
				return;
			}
			logSnifferStatisticsService.deleteObject(logSnifferStatisticsPlh.getList());
		}
	}
	
	private void deleteDataChannelLog(){
		BaseService dataChannelLogService=(BaseService) SpringConfig.getInstance().getService(DataChannelLog.class);
		Calendar calendar = Calendar.getInstance();  
		calendar.setTime(new Date());//month 为指定月份任意日期  
	    calendar.add(Calendar.DATE, -10);
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		expList.add(Restrictions.le("updatetime", calendar.getTime()));
		for(;;){
			PaginatedListHelper dataChannelLogPlh=dataChannelLogService.findList(expList, null, null, null);
			if(dataChannelLogPlh==null
					||dataChannelLogPlh.getList()==null
					||dataChannelLogPlh.getList().size()==0){
				return;
			}
			dataChannelLogService.deleteObject(dataChannelLogPlh.getList());
		}
	}
	
	private void deleteDataStatistics(){
		BaseService dataStatisticsService=(BaseService) SpringConfig.getInstance().getService(DataStatistics.class);
		Calendar calendar = Calendar.getInstance();  
		calendar.setTime(new Date());//month 为指定月份任意日期  
	    calendar.add(Calendar.DATE, -10);
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		expList.add(Restrictions.le("updatetime", calendar.getTime()));
		for(;;){
			PaginatedListHelper dataStatisticsPlh=dataStatisticsService.findList(expList, null, null, null);
			if(dataStatisticsPlh==null
					||dataStatisticsPlh.getList()==null
					||dataStatisticsPlh.getList().size()==0){
				return;
			}
			dataStatisticsService.deleteObject(dataStatisticsPlh.getList());
		}
	}
	
	private void updateBusinessChannel(){
		BaseService businessChannelService=(BaseService) SpringConfig.getInstance().getService(BusinessChannel.class);
		BaseService businessRuleService=(BaseService) SpringConfig.getInstance().getService(BusinessRule.class);
		BaseService businessRuleDetailService=(BaseService) SpringConfig.getInstance().getService(BusinessRuleDetail.class);
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		PaginatedListHelper businessChannelPlh=businessChannelService.findList(expList, orders, null, null);
		if(businessChannelPlh.getList()==null
				||businessChannelPlh.getList().size()==0){
			return;
		}

		for(Object o:businessChannelPlh.getList()){
			BusinessChannel businessChannel=(BusinessChannel)o;
			expList = new ArrayList<Criterion>();// 查询条件
			orders = new ArrayList<Order>();// 排序条件
			expList.add(Restrictions.eq("BusinessChannel.id", businessChannel.getId()));
			PaginatedListHelper businessRulePlh=businessRuleService.findList(expList, orders, null, null);
			if(businessRulePlh.getList()==null
					||businessRulePlh.getList().size()==0){
				continue;
			}
			for(Object object:businessRulePlh.getList()){
				BusinessRule businessRule=(BusinessRule)object;
				expList = new ArrayList<Criterion>();// 查询条件
				orders = new ArrayList<Order>();// 排序条件
				expList.add(Restrictions.eq("BusinessRule.id", businessRule.getId()));
				PaginatedListHelper businessRuleDetailPlh=businessRuleDetailService.findList(expList, orders, null, null);
				if(businessRuleDetailPlh.getList()!=null
						&&businessRuleDetailPlh.getList().size()>0){
					for(Object obj:businessRuleDetailPlh.getList()){
						BusinessRuleDetail businessRuleDetail=(BusinessRuleDetail)obj;
						businessRuleDetail.setNum(0);
						businessRuleDetail.setUpdatetime(new Date());
						businessRuleDetailService.updateObject(businessRuleDetail,true);
					}	
				}
				businessRule.setNum(0);
				businessRule.setUpdatetime(new Date());
				businessRuleService.updateObject(businessRule,true);
			}
		}
	}

}
