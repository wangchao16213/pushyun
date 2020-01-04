package com.common.task;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.bean.BaseConfig;
import com.bean.BaseRecord;
import com.bean.BusinessChannel;
import com.bean.BusinessDns;
import com.bean.BusinessRule;
import com.bean.BusinessRuleDetail;
import com.bean.BusinessTask;
import com.bean.BusinessWhitelist;
import com.common.comm.Constants;
import com.common.config.SpringConfig;
import com.common.service.BaseService;
import com.common.tools.DateUtil;
import com.common.tools.StrUtil;
import com.common.tools.ZipUtils;
import com.common.type.BaseConfigCode;
import com.common.type.BusinessDnsState;
import com.common.type.BusinessRuleDetailState;
import com.common.type.BusinessRuleState;
import com.common.type.BusinessTaskState;
import com.common.type.BusinessTaskType;
import com.common.web.Pages;
import com.common.web.PaginatedListHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.module.business.service.DnsService;
import com.module.business.service.RuleService;
import com.spacesat.util.json.JSONArray;
import com.spacesat.util.json.JSONException;
import com.spacesat.util.json.JSONObject;

public class RuleChildTask implements Runnable{
	
	private final Logger logger = Logger.getLogger(RuleChildTask.class.getName());
	
	private String rootPath;
	
	private String buildPath;
	
	private String businessTaskId;
	
	public RuleChildTask(String rootPath,String buildPath,String businessTaskId){
		this.rootPath=rootPath;
		this.buildPath=buildPath;
		this.businessTaskId=businessTaskId;
	}

	@Override
	public void run() {
		BaseService businessTaskService=(BaseService) SpringConfig.getInstance().getService(BusinessTask.class);
		BusinessTask businessTask=(BusinessTask) businessTaskService.findById(businessTaskId);
		if(!businessTask.getState().equals(BusinessTaskState.run.getCode())){
			return;
		}
		if(businessTask.getType().equals(BusinessTaskType.reset.getCode())){
			resetTask(businessTask);
		}
		if(businessTask.getType().equals(BusinessTaskType.exp.getCode())){
			expTask(businessTask);
		}
		if(businessTask.getType().equals(BusinessTaskType.imp.getCode())){
			impTask(businessTask);
		}
		if(businessTask.getType().equals(BusinessTaskType.truncate.getCode())){
			truncateTask(businessTask);
		}
		if(businessTask.getType().equals(BusinessTaskType.api.getCode())){
			apiTask(businessTask);
		}
		if(businessTask.getType().equals(BusinessTaskType.reset_dns.getCode())){
			resetDnsTask(businessTask);
		}
		if(businessTask.getType().equals(BusinessTaskType.exp_dns.getCode())){
			expDnsTask(businessTask);
		}
		if(businessTask.getType().equals(BusinessTaskType.imp_dns.getCode())){
			impDnsTask(businessTask);
		}
		if(businessTask.getType().equals(BusinessTaskType.truncate_dns.getCode())){
			truncateDnsTask(businessTask);
		}
	}
	
	private void impTask(BusinessTask mainTask){
		Date startDate=new Date();
		BaseService businessTaskService=(BaseService) SpringConfig.getInstance().getService(BusinessTask.class);
		BaseService businessChannelService=(BaseService) SpringConfig.getInstance().getService(BusinessChannel.class);
		BaseService businessRuleService=(BaseService) SpringConfig.getInstance().getService(BusinessRule.class);
		BaseService businessRuleDetailService=(BaseService) SpringConfig.getInstance().getService(BusinessRuleDetail.class);
		RuleService ruleService=(RuleService) SpringConfig.getInstance().getService(RuleService.class);
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.eq("code", mainTask.getCode()));
		List<BaseRecord> businessTaskList=businessTaskService.findList(expList, orders, 0, 9999, null);
		if(businessTaskList==null
				||businessTaskList.size()==0){
			return;
		}
		boolean preload=false;
		long totalsize=0;
		for(Object o:businessTaskList){
			BusinessTask task=(BusinessTask)o;
			File f=new File(task.getUploadfile());
			long size= f.length();
			totalsize=totalsize+size;
		}
		if(totalsize>507152){ 
			preload=true;
		}
		Map<String, BusinessRule> exactBusinessRuleMap=new HashMap<String, BusinessRule>();
		Map<String, BusinessRule> fuzzyBusinessRuleMap=new HashMap<String, BusinessRule>();
		if(preload){
			expList = new ArrayList<Criterion>();// 查询条件
			orders = new ArrayList<Order>();// 排序条件
			expList.add(Restrictions.eq("BusinessChannel.id", mainTask.getBusinessChannel().getId()));
			int total =businessRuleService.getList(expList, null);
			
			Pages totalpages = new Pages();
			totalpages.setPerPageNum(10000);// 分页查询
			totalpages.setTotalNum(total);
			totalpages.executeCount();
			for (int i = 0; i < totalpages.getAllPage(); i++) {
				logger.info(String.format("imppreload start :%s  id:%s", DateUtil.getDateTime(new Date()),mainTask.getId()));
				Pages pages = new Pages();
				pages.setPerPageNum(totalpages.getPerPageNum());
				pages.setSpage(i * totalpages.getPerPageNum());// 列表开始位置
				List<BaseRecord> list = businessRuleService.findList(
						expList, orders, pages.getSpage(), pages.getPerPageNum(),null);
				if (list == null || list.size() == 0) {
					break;
				}
				for(Object o:list){
					BusinessRule businessRule=(BusinessRule)o;
					if(StringUtils.isNotBlank(businessRule.getExact())){
						exactBusinessRuleMap.put(businessRule.getExact()+"_"+businessRule.getBusinessChannel().getId(), businessRule);
					}
					if(StringUtils.isNotBlank(businessRule.getFuzzy())){
						if(StringUtils.isNotBlank(businessRule.getHost())){
							fuzzyBusinessRuleMap.put(String.format("%s_%s_%s", businessRule.getHost(),businessRule.getFuzzy(),businessRule.getBusinessChannel().getId()), businessRule);
						}else{
							fuzzyBusinessRuleMap.put(String.format("%s_%s", businessRule.getFuzzy(),businessRule.getBusinessChannel().getId()), businessRule);
						}
					}
				}
				logger.info(String.format("imppreload end :%s  id:%s", DateUtil.getDateTime(new Date()),mainTask.getId()));
			}
		}
		for(Object o:businessTaskList){
			BusinessTask task=(BusinessTask)o;
			if(task.getState().equals(BusinessTaskState.pause.getCode())){
				return;
			}
			task.setState(BusinessTaskState.run.getCode());
			task.setUpdatetime(new Date());
			businessTaskService.updateObject(task, true);
			logger.info(String.format("imp start :%s  id:%s", DateUtil.getDateTime(new Date()),task.getId()));
			String ext = task.getUploadfile().substring(task.getUploadfile().lastIndexOf("."));  
			Map<String,Object> returnMap=new HashMap<String, Object>();
			BusinessChannel businessChannel=(BusinessChannel) businessChannelService.findById(task.getBusinessChannel().getId());
			if(".xls".equals(ext)
					 ||".xlsx".equals(ext)){  
				 returnMap=ruleService.saveExcel(ext,task.getUploadfile(), businessChannel,
						 task.getCreateuserid(), preload,exactBusinessRuleMap,fuzzyBusinessRuleMap, task);
			}else if(".csv".equals(ext)){
				 returnMap=ruleService.saveCsv(ext, task.getUploadfile(),
						 businessChannel,task.getCreateuserid(), preload,exactBusinessRuleMap,fuzzyBusinessRuleMap,task);
			}else{
				task.setState(BusinessTaskState.error.getCode());
			    task.setUpdatetime(new Date());
				businessTaskService.updateObject(task, true);
				continue;
			}
			logger.info(String.format("imp end :%s  id:%s", DateUtil.getDateTime(new Date()),task.getId()));
			String msg=(String) returnMap.get("msg");
		    if(msg.length()>2000){
		       msg=msg.substring(0, 2000);
		    }
		    int succ=Integer.parseInt(returnMap.get("succ").toString());
		    int same=Integer.parseInt(returnMap.get("same").toString());
		    int error=Integer.parseInt(returnMap.get("error").toString());
		    businessRuleService.saveObject((List<BaseRecord>)returnMap.get("BusinessRuleList"));
		    businessRuleDetailService.saveObject((List<BaseRecord>)returnMap.get("BusinessRuleDetailList"));
		    Date endDate=new Date();
		    task.setState(BusinessTaskState.finish.getCode());
		    task.setUpdatetime(new Date());
		    task.setRemark(String.format("开始:%s,结束:%s<br>成功%s,相似%s,失败%s,%s",
					DateUtil.getDateTime(startDate,"HH:mm:ss"),DateUtil.getDateTime(endDate,"HH:mm:ss"),
					succ,same,error,msg));
			businessTaskService.updateObject(task, true); 
			saveBusinessTask(businessChannel,BusinessTaskType.api,null,null,task.getCreateuserid());
		}
	}
	
	private void impDnsTask(BusinessTask mainTask){
		Date startDate=new Date();
		BaseService businessTaskService=(BaseService) SpringConfig.getInstance().getService(BusinessTask.class);
		BaseService businessChannelService=(BaseService) SpringConfig.getInstance().getService(BusinessChannel.class);
		BaseService businessDnsService=(BaseService) SpringConfig.getInstance().getService(BusinessDns.class);
		DnsService dnsService=(DnsService) SpringConfig.getInstance().getService(DnsService.class);
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.eq("code", mainTask.getCode()));
		List<BaseRecord> businessTaskList=businessTaskService.findList(expList, orders, 0, 9999, null);
		if(businessTaskList==null
				||businessTaskList.size()==0){
			return;
		}
		boolean preload=false;
		long totalsize=0;
		for(Object o:businessTaskList){
			BusinessTask task=(BusinessTask)o;
			File f=new File(task.getUploadfile());
			long size= f.length();
			totalsize=totalsize+size;
		}
		if(totalsize>507152){ 
			preload=true;
		}
		Map<String, BusinessDns> hostBusinessDnsMap=new HashMap<String, BusinessDns>();
		if(preload){
			expList = new ArrayList<Criterion>();// 查询条件
			orders = new ArrayList<Order>();// 排序条件
			expList.add(Restrictions.eq("BusinessChannel.id", mainTask.getBusinessChannel().getId()));
			int total =businessDnsService.getList(expList, null);
			
			Pages totalpages = new Pages();
			totalpages.setPerPageNum(10000);// 分页查询
			totalpages.setTotalNum(total);
			totalpages.executeCount();
			for (int i = 0; i < totalpages.getAllPage(); i++) {
				logger.info(String.format("imppreload start :%s  id:%s", DateUtil.getDateTime(new Date()),mainTask.getId()));
				Pages pages = new Pages();
				pages.setPerPageNum(totalpages.getPerPageNum());
				pages.setSpage(i * totalpages.getPerPageNum());// 列表开始位置
				List<BaseRecord> list = businessDnsService.findList(
						expList, orders, pages.getSpage(), pages.getPerPageNum(),null);
				if (list == null || list.size() == 0) {
					break;
				}
				for(Object o:list){
					BusinessDns businessDns=(BusinessDns)o;
					if(StringUtils.isNotBlank(businessDns.getHost())){
						hostBusinessDnsMap.put(businessDns.getHost()+"_"+businessDns.getBusinessChannel().getId(), businessDns);
					}
				}
				logger.info(String.format("imppreload end :%s  id:%s", DateUtil.getDateTime(new Date()),mainTask.getId()));
			}
		}
		for(Object o:businessTaskList){
			BusinessTask task=(BusinessTask)o;
			if(task.getState().equals(BusinessTaskState.pause.getCode())){
				return;
			}
			task.setState(BusinessTaskState.run.getCode());
			task.setUpdatetime(new Date());
			businessTaskService.updateObject(task, true);
			logger.info(String.format("imp start :%s  id:%s", DateUtil.getDateTime(new Date()),task.getId()));
			String ext = task.getUploadfile().substring(task.getUploadfile().lastIndexOf("."));  
			Map<String,Object> returnMap=new HashMap<String, Object>();
			BusinessChannel businessChannel=(BusinessChannel) businessChannelService.findById(task.getBusinessChannel().getId());
			if(".xls".equals(ext)
					 ||".xlsx".equals(ext)){  
				 returnMap=dnsService.saveExcel(ext,task.getUploadfile(), businessChannel,
						 task.getCreateuserid(), preload,hostBusinessDnsMap, task);
			}else if(".csv".equals(ext)){
				 returnMap=dnsService.saveCsv(ext, task.getUploadfile(),
						 businessChannel,task.getCreateuserid(), preload,hostBusinessDnsMap,task);
			}else{
				task.setState(BusinessTaskState.error.getCode());
			    task.setUpdatetime(new Date());
				businessTaskService.updateObject(task, true);
				continue;
			}
			logger.info(String.format("imp end :%s  id:%s", DateUtil.getDateTime(new Date()),task.getId()));
			String msg=(String) returnMap.get("msg");
		    if(msg.length()>2000){
		       msg=msg.substring(0, 2000);
		    }
		    int succ=Integer.parseInt(returnMap.get("succ").toString());
		    int same=Integer.parseInt(returnMap.get("same").toString());
		    int error=Integer.parseInt(returnMap.get("error").toString());
		    businessDnsService.saveObject((List<BaseRecord>)returnMap.get("BusinessDnsList"));
		    Date endDate=new Date();
		    task.setState(BusinessTaskState.finish.getCode());
		    task.setUpdatetime(new Date());
		    task.setRemark(String.format("开始:%s,结束:%s<br>成功%s,相似%s,失败%s,%s",
					DateUtil.getDateTime(startDate,"HH:mm:ss"),DateUtil.getDateTime(endDate,"HH:mm:ss"),
					succ,same,error,msg));
			businessTaskService.updateObject(task, true); 
			saveBusinessTask(businessChannel,BusinessTaskType.api,null,null,task.getCreateuserid());
		}
	}
	
	
	private void truncateTask(BusinessTask businessTask){
		Date startDate=new Date();
		BaseService businessTaskService=(BaseService) SpringConfig.getInstance().getService(BusinessTask.class);
		BaseService businessChannelService=(BaseService) SpringConfig.getInstance().getService(BusinessChannel.class);
		RuleService ruleService=(RuleService)  SpringConfig.getInstance().getService(RuleService.class);
		BaseService businessRuleService=(BaseService) SpringConfig.getInstance().getService(BusinessRule.class);
		BaseService baseConfigService=(BaseService) SpringConfig.getInstance().getService(BaseConfig.class);
		BaseConfig expperpagenumConfig=(BaseConfig) baseConfigService.findByProperty("code", BaseConfigCode.expperpagenum.getCode());
		BaseConfig taskthreadnumConfig=(BaseConfig) baseConfigService.findByProperty("code", BaseConfigCode.taskthreadnum.getCode());
		Map<String,String> businessChannelMap=new HashMap<String, String>();
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		DetachedCriteria dc=DetachedCriteria.forClass(BusinessRule.class);
		JsonObject jsonObject=new JsonParser().parse(businessTask.getContent()).getAsJsonObject();
		ruleService.setExpList(expList, jsonObject, dc);
		for(;;){
			int total = businessRuleService.getList(expList, dc);
			if(total==0){
				break;
			}
			businessTask =(BusinessTask) businessTaskService.findById(businessTask.getId());
			if(!businessTask.getState().equals(BusinessTaskState.run.getCode())){
				return;
			}
			Pages totalpages = new Pages();
			totalpages.setPerPageNum(Integer.parseInt(expperpagenumConfig.getValue()));// 分页查询
			totalpages.setTotalNum(total);
			totalpages.executeCount();
			int latchnum=Integer.parseInt(taskthreadnumConfig.getValue())/2;
			if(totalpages.getAllPage()<latchnum){
				latchnum=totalpages.getAllPage();
			}
			CountDownLatch latch = new CountDownLatch(latchnum);
			for (int i = 0; i < latchnum; i++) {
				RuleTruncateTask ruleTruncateTask=new RuleTruncateTask( latch,
						totalpages.getPerPageNum() ,i * totalpages.getPerPageNum(),businessTask);
				new Thread(ruleTruncateTask).start();
			}
			try {
				latch.await();
			} catch (InterruptedException e) {
				logger.error("",e);
			}
		}
		Date endDate=new Date();
		logger.info(String.format("start:%s,end:%s id:%s", 
				DateUtil.getDateTime(startDate, "HH:mm:ss"),DateUtil.getDateTime(endDate, "HH:mm:ss"),businessTask.getId()));
		businessTask.setRemark(String.format("开始:%s,结束:%s", 
				DateUtil.getDateTime(startDate, "HH:mm:ss"),DateUtil.getDateTime(endDate, "HH:mm:ss")));
		businessTask.setState(BusinessTaskState.finish.getCode());
	    businessTask.setUpdatetime(new Date());
		businessTaskService.updateObject(businessTask, true); 
		saveBusinessTask((BusinessChannel)businessChannelService.findById(businessTask.getBusinessChannel().getId()),BusinessTaskType.api,null,null,businessTask.getCreateuserid());

	}
	
	
	private void truncateDnsTask(BusinessTask businessTask){
		Date startDate=new Date();
		BaseService businessTaskService=(BaseService) SpringConfig.getInstance().getService(BusinessTask.class);
		BaseService businessChannelService=(BaseService) SpringConfig.getInstance().getService(BusinessChannel.class);
		DnsService dnsService=(DnsService)  SpringConfig.getInstance().getService(DnsService.class);
		BaseService businessDnsService=(BaseService) SpringConfig.getInstance().getService(BusinessDns.class);
		BaseService baseConfigService=(BaseService) SpringConfig.getInstance().getService(BaseConfig.class);
		BaseConfig expperpagenumConfig=(BaseConfig) baseConfigService.findByProperty("code", BaseConfigCode.expperpagenum.getCode());
		BaseConfig taskthreadnumConfig=(BaseConfig) baseConfigService.findByProperty("code", BaseConfigCode.taskthreadnum.getCode());
		Map<String,String> businessChannelMap=new HashMap<String, String>();
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		DetachedCriteria dc=DetachedCriteria.forClass(BusinessDns.class);
		JsonObject jsonObject=new JsonParser().parse(businessTask.getContent()).getAsJsonObject();
		dnsService.setExpList(expList, jsonObject, dc);
		for(;;){
			int total = businessDnsService.getList(expList, dc);
			if(total==0){
				break;
			}
			businessTask =(BusinessTask) businessTaskService.findById(businessTask.getId());
			if(!businessTask.getState().equals(BusinessTaskState.run.getCode())){
				return;
			}
			Pages totalpages = new Pages();
			totalpages.setPerPageNum(Integer.parseInt(expperpagenumConfig.getValue()));// 分页查询
			totalpages.setTotalNum(total);
			totalpages.executeCount();
			int latchnum=Integer.parseInt(taskthreadnumConfig.getValue())/2;
			if(totalpages.getAllPage()<latchnum){
				latchnum=totalpages.getAllPage();
			}
			CountDownLatch latch = new CountDownLatch(latchnum);
			for (int i = 0; i < latchnum; i++) {
				RuleTruncateDnsTask ruleTruncateDnsTask=new RuleTruncateDnsTask( latch,
						totalpages.getPerPageNum() ,i * totalpages.getPerPageNum(),businessTask);
				new Thread(ruleTruncateDnsTask).start();
			}
			try {
				latch.await();
			} catch (InterruptedException e) {
				logger.error("",e);
			}
		}
		Date endDate=new Date();
		logger.info(String.format("start:%s,end:%s id:%s", 
				DateUtil.getDateTime(startDate, "HH:mm:ss"),DateUtil.getDateTime(endDate, "HH:mm:ss"),businessTask.getId()));
		businessTask.setRemark(String.format("开始:%s,结束:%s", 
				DateUtil.getDateTime(startDate, "HH:mm:ss"),DateUtil.getDateTime(endDate, "HH:mm:ss")));
		businessTask.setState(BusinessTaskState.finish.getCode());
	    businessTask.setUpdatetime(new Date());
		businessTaskService.updateObject(businessTask, true); 
		saveBusinessTask((BusinessChannel)businessChannelService.findById(businessTask.getBusinessChannel().getId()),BusinessTaskType.api,null,null,businessTask.getCreateuserid());
	}
	
	
	private void resetTask(BusinessTask businessTask){
		Date startDate=new Date();
		BaseService businessTaskService=(BaseService) SpringConfig.getInstance().getService(BusinessTask.class);
		RuleService ruleService=(RuleService)  SpringConfig.getInstance().getService(RuleService.class);
		BaseService businessRuleService=(BaseService) SpringConfig.getInstance().getService(BusinessRule.class);
		BaseService baseConfigService=(BaseService) SpringConfig.getInstance().getService(BaseConfig.class);
		BaseConfig expperpagenumConfig=(BaseConfig) baseConfigService.findByProperty("code", BaseConfigCode.expperpagenum.getCode());
		BaseConfig taskthreadnumConfig=(BaseConfig) baseConfigService.findByProperty("code", BaseConfigCode.taskthreadnum.getCode());
		int state = Constants.STATE_OPERATOR_LOST;
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		DetachedCriteria dc=DetachedCriteria.forClass(BusinessRule.class);
		JsonObject jsonObject=new JsonParser().parse(businessTask.getContent()).getAsJsonObject();
		ruleService.setExpList(expList, jsonObject, dc);
		int total = businessRuleService.getList(expList, dc);
		Pages totalpages = new Pages();
		totalpages.setPerPageNum(Integer.parseInt(expperpagenumConfig.getValue()));// 分页查询
		totalpages.setTotalNum(total);
		totalpages.executeCount();
		int semaphorenum=Integer.parseInt(taskthreadnumConfig.getValue())/2;
		if(totalpages.getAllPage()<semaphorenum){
			semaphorenum=totalpages.getAllPage();
		}
		CountDownLatch latch = new CountDownLatch(totalpages.getAllPage());
	    Semaphore semaphore = new Semaphore(semaphorenum);  
		for (int i = 0; i < totalpages.getAllPage(); i++) {
			businessTask =(BusinessTask) businessTaskService.findById(businessTask.getId());
			if(!businessTask.getState().equals(BusinessTaskState.run.getCode())){
				return;
			}
			RuleResetTask ruleResetTask=new RuleResetTask(latch,semaphore,
					totalpages.getPerPageNum() ,i * totalpages.getPerPageNum(),businessTask);
			new Thread(ruleResetTask).start();
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			logger.error("",e);
		}
		Date endDate=new Date();
		logger.info(String.format("start:%s,end:%s id:%s", 
				DateUtil.getDateTime(startDate, "HH:mm:ss"),DateUtil.getDateTime(endDate, "HH:mm:ss"),businessTask.getId()));
		businessTask.setRemark(String.format("开始:%s,结束:%s", 
				DateUtil.getDateTime(startDate, "HH:mm:ss"),DateUtil.getDateTime(endDate, "HH:mm:ss")));
		businessTask.setState(BusinessTaskState.finish.getCode());
	    businessTask.setUpdatetime(new Date());
		businessTaskService.updateObject(businessTask, true); 
	}
	
	private void resetDnsTask(BusinessTask businessTask){
		Date startDate=new Date();
		BaseService businessTaskService=(BaseService) SpringConfig.getInstance().getService(BusinessTask.class);
		DnsService dnsService=(DnsService)  SpringConfig.getInstance().getService(DnsService.class);
		BaseService businessDnsService=(BaseService) SpringConfig.getInstance().getService(BusinessDns.class);
		BaseService baseConfigService=(BaseService) SpringConfig.getInstance().getService(BaseConfig.class);
		BaseConfig expperpagenumConfig=(BaseConfig) baseConfigService.findByProperty("code", BaseConfigCode.expperpagenum.getCode());
		BaseConfig taskthreadnumConfig=(BaseConfig) baseConfigService.findByProperty("code", BaseConfigCode.taskthreadnum.getCode());
		int state = Constants.STATE_OPERATOR_LOST;
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		DetachedCriteria dc=DetachedCriteria.forClass(BusinessDns.class);
		JsonObject jsonObject=new JsonParser().parse(businessTask.getContent()).getAsJsonObject();
		dnsService.setExpList(expList, jsonObject, dc);
		int total = businessDnsService.getList(expList, dc);
		Pages totalpages = new Pages();
		totalpages.setPerPageNum(Integer.parseInt(expperpagenumConfig.getValue()));// 分页查询
		totalpages.setTotalNum(total);
		totalpages.executeCount();
		int semaphorenum=Integer.parseInt(taskthreadnumConfig.getValue())/2;
		if(totalpages.getAllPage()<semaphorenum){
			semaphorenum=totalpages.getAllPage();
		}
		CountDownLatch latch = new CountDownLatch(totalpages.getAllPage());
	    Semaphore semaphore = new Semaphore(semaphorenum);  
		for (int i = 0; i < totalpages.getAllPage(); i++) {
			businessTask =(BusinessTask) businessTaskService.findById(businessTask.getId());
			if(!businessTask.getState().equals(BusinessTaskState.run.getCode())){
				return;
			}
			RuleResetDnsTask ruleResetDnsTask=new RuleResetDnsTask(latch,semaphore,
					totalpages.getPerPageNum() ,i * totalpages.getPerPageNum(),businessTask);
			new Thread(ruleResetDnsTask).start();
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			logger.error("",e);
		}
		Date endDate=new Date();
		logger.info(String.format("start:%s,end:%s id:%s", 
				DateUtil.getDateTime(startDate, "HH:mm:ss"),DateUtil.getDateTime(endDate, "HH:mm:ss"),businessTask.getId()));
		businessTask.setRemark(String.format("开始:%s,结束:%s", 
				DateUtil.getDateTime(startDate, "HH:mm:ss"),DateUtil.getDateTime(endDate, "HH:mm:ss")));
		businessTask.setState(BusinessTaskState.finish.getCode());
	    businessTask.setUpdatetime(new Date());
		businessTaskService.updateObject(businessTask, true); 
	}
	
	
	
	private void expTask(BusinessTask mainTask){
		Date startDate=new Date();
		BaseService businessTaskService=(BaseService) SpringConfig.getInstance().getService(BusinessTask.class);
		BaseService businessRuleService=(BaseService) SpringConfig.getInstance().getService(BusinessRule.class);
		RuleService ruleService=(RuleService)  SpringConfig.getInstance().getService(RuleService.class);
		BaseService baseConfigService=(BaseService) SpringConfig.getInstance().getService(BaseConfig.class);
		BaseConfig expperpagenumConfig=(BaseConfig) baseConfigService.findByProperty("code", BaseConfigCode.expperpagenum.getCode());
		BaseConfig taskthreadnumConfig=(BaseConfig) baseConfigService.findByProperty("code", BaseConfigCode.taskthreadnum.getCode());
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		DetachedCriteria dc=DetachedCriteria.forClass(BusinessRule.class);
		JsonObject jsonObject=new JsonObject();
		if(StringUtils.isNotBlank(mainTask.getContent())){
			jsonObject=new JsonParser().parse(mainTask.getContent()).getAsJsonObject();
		}
		String pathName =  UUID.randomUUID().toString();
		File allPathFile = new File(buildPath+File.separator+pathName);
		if (!allPathFile.exists()) {
			allPathFile.mkdirs();
		}
		ruleService.setExpList(expList, jsonObject, dc);
		int total = businessRuleService.getList(expList, dc);
		Pages totalpages = new Pages();
		totalpages.setPerPageNum(Integer.parseInt(expperpagenumConfig.getValue()));// 分页查询
		totalpages.setTotalNum(total);
		totalpages.executeCount();
		int semaphorenum=Integer.parseInt(taskthreadnumConfig.getValue())/2;
		if(totalpages.getAllPage()<semaphorenum){
			semaphorenum=totalpages.getAllPage();
		}
		CountDownLatch latch = new CountDownLatch(totalpages.getAllPage());
	    Semaphore semaphore = new Semaphore(semaphorenum);  
		for (int i = 0; i < totalpages.getAllPage(); i++) {
			mainTask =(BusinessTask) businessTaskService.findById(mainTask.getId());
			if(!mainTask.getState().equals(BusinessTaskState.run.getCode())){
				return;
			}
			RuleExpTask ruleExpTask=new RuleExpTask(latch,semaphore,
					totalpages.getPerPageNum() ,i * totalpages.getPerPageNum(),mainTask,
					 rootPath, buildPath, pathName);
			new Thread(ruleExpTask).start();
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			logger.error("",e);
		}
		List<String> fileList =new ArrayList<String>();
		StrUtil.getFiles(buildPath+File.separator+pathName, fileList);
		String[] files=new String[fileList.size()];
		for(int i=0;i<fileList.size();i++){
			File f=new File(fileList.get(i));
			files[i]=pathName+File.separator+f.getName();
		}
		String zipName=ZipUtils.zip(buildPath, files, DateUtil.getDate(new Date()));
		Date endDate=new Date();
		logger.info(String.format("start:%s,end:%s id:%s", 
				DateUtil.getDateTime(startDate, "HH:mm:ss"),DateUtil.getDateTime(endDate, "HH:mm:ss"),mainTask.getId()));
		mainTask.setRemark(String.format("开始:%s,结束:%s,导出%s",
					DateUtil.getDateTime(startDate, "HH:mm:ss"),DateUtil.getDateTime(endDate, "HH:mm:ss"),total));
		mainTask.setDownloadfile(rootPath+"/"+zipName);
		mainTask.setState(BusinessTaskState.finish.getCode());
		mainTask.setUpdatetime(new Date());
		businessTaskService.updateObject(mainTask, true); 
	}
	
	private void expDnsTask(BusinessTask mainTask){
		Date startDate=new Date();
		BaseService businessTaskService=(BaseService) SpringConfig.getInstance().getService(BusinessTask.class);
		BaseService businessDnsService=(BaseService) SpringConfig.getInstance().getService(BusinessDns.class);
		DnsService dnsService=(DnsService)  SpringConfig.getInstance().getService(DnsService.class);
		BaseService baseConfigService=(BaseService) SpringConfig.getInstance().getService(BaseConfig.class);
		BaseConfig expperpagenumConfig=(BaseConfig) baseConfigService.findByProperty("code", BaseConfigCode.expperpagenum.getCode());
		BaseConfig taskthreadnumConfig=(BaseConfig) baseConfigService.findByProperty("code", BaseConfigCode.taskthreadnum.getCode());
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		DetachedCriteria dc=DetachedCriteria.forClass(BusinessDns.class);
		JsonObject jsonObject=new JsonObject();
		if(StringUtils.isNotBlank(mainTask.getContent())){
			jsonObject=new JsonParser().parse(mainTask.getContent()).getAsJsonObject();
		}
		String pathName =  UUID.randomUUID().toString();
		File allPathFile = new File(buildPath+File.separator+pathName);
		if (!allPathFile.exists()) {
			allPathFile.mkdirs();
		}
		dnsService.setExpList(expList, jsonObject, dc);
		int total = businessDnsService.getList(expList, dc);
		Pages totalpages = new Pages();
		totalpages.setPerPageNum(Integer.parseInt(expperpagenumConfig.getValue()));// 分页查询
		totalpages.setTotalNum(total);
		totalpages.executeCount();
		int semaphorenum=Integer.parseInt(taskthreadnumConfig.getValue())/2;
		if(totalpages.getAllPage()<semaphorenum){
			semaphorenum=totalpages.getAllPage();
		}
		CountDownLatch latch = new CountDownLatch(totalpages.getAllPage());
	    Semaphore semaphore = new Semaphore(semaphorenum);  
		for (int i = 0; i < totalpages.getAllPage(); i++) {
			mainTask =(BusinessTask) businessTaskService.findById(mainTask.getId());
			if(!mainTask.getState().equals(BusinessTaskState.run.getCode())){
				return;
			}
			RuleExpDnsTask ruleExpDnsTask=new RuleExpDnsTask(latch,semaphore,
					totalpages.getPerPageNum() ,i * totalpages.getPerPageNum(),mainTask,
					 rootPath, buildPath, pathName);
			new Thread(ruleExpDnsTask).start();
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			logger.error("",e);
		}
		List<String> fileList =new ArrayList<String>();
		StrUtil.getFiles(buildPath+File.separator+pathName, fileList);
		String[] files=new String[fileList.size()];
		for(int i=0;i<fileList.size();i++){
			File f=new File(fileList.get(i));
			files[i]=pathName+File.separator+f.getName();
		}
		String zipName=ZipUtils.zip(buildPath, files, DateUtil.getDate(new Date()));
		Date endDate=new Date();
		logger.info(String.format("start:%s,end:%s id:%s", 
				DateUtil.getDateTime(startDate, "HH:mm:ss"),DateUtil.getDateTime(endDate, "HH:mm:ss"),mainTask.getId()));
		mainTask.setRemark(String.format("开始:%s,结束:%s,导出%s",
					DateUtil.getDateTime(startDate, "HH:mm:ss"),DateUtil.getDateTime(endDate, "HH:mm:ss"),total));
		mainTask.setDownloadfile(rootPath+"/"+zipName);
		mainTask.setState(BusinessTaskState.finish.getCode());
		mainTask.setUpdatetime(new Date());
		businessTaskService.updateObject(mainTask, true); 
	}
	
	
	private void apiTask(BusinessTask businessTask){
		Date startDate=new Date();
		BaseService businessTaskService=(BaseService) SpringConfig.getInstance().getService(BusinessTask.class);
		BaseService businessDnsService=(BaseService) SpringConfig.getInstance().getService(BusinessDns.class);
		BaseService businessRuleService=(BaseService) SpringConfig.getInstance().getService(BusinessRule.class);
		BaseService businessRuleDetailService=(BaseService) SpringConfig.getInstance().getService(BusinessRuleDetail.class);
		RuleService ruleService=(RuleService) SpringConfig.getInstance().getService(RuleService.class);
		BaseService businessChannelService=(BaseService) SpringConfig.getInstance().getService(BusinessChannel.class);
		BaseService baseConfigService=(BaseService) SpringConfig.getInstance().getService(BaseConfig.class);
		BaseConfig expperpagenumConfig=(BaseConfig) baseConfigService.findByProperty("code", BaseConfigCode.expperpagenum.getCode());
		BaseConfig taskthreadnumConfig=(BaseConfig) baseConfigService.findByProperty("code", BaseConfigCode.taskthreadnum.getCode());
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.eq("BusinessChannel.id", businessTask.getBusinessChannel().getId()));
		expList.add(Restrictions.eq("state", BusinessRuleState.normal.getCode()));
		int total = businessRuleService.getList(expList, null);
		List<Object> expportList = new ArrayList<Object>();
		if(total>0){
			Pages totalpages = new Pages();
			totalpages.setPerPageNum(Integer.parseInt(expperpagenumConfig.getValue()));// 分页查询
			totalpages.setTotalNum(total);
			totalpages.executeCount();
			StringBuffer filePathsb = new StringBuffer();
			filePathsb.append(rootPath);
			File buildPathFile = new File(buildPath);
			if (!buildPathFile.exists()) {
				buildPathFile.mkdirs();
			}
			String pathName =  UUID.randomUUID().toString();
			File allPathFile = new File(buildPath+File.separator+pathName);
			if (!allPathFile.exists()) {
				allPathFile.mkdirs();
			}
			int semaphorenum=Integer.parseInt(taskthreadnumConfig.getValue())/2;
			if(totalpages.getAllPage()<semaphorenum){
				semaphorenum=totalpages.getAllPage();
			}
			CountDownLatch latch = new CountDownLatch(totalpages.getAllPage());
		    Semaphore semaphore = new Semaphore(semaphorenum);  
			for (int i = 0; i < totalpages.getAllPage(); i++) {
				businessTask =(BusinessTask) businessTaskService.findById(businessTask.getId());
				if(!businessTask.getState().equals(BusinessTaskState.run.getCode())){
					return;
				}
				RuleApiTask ruleApiTask=new RuleApiTask(latch,semaphore,
						totalpages.getPerPageNum() ,i * totalpages.getPerPageNum(),businessTask,
						 rootPath, buildPath, pathName);
				new Thread(ruleApiTask).start();
			}
			try {
				latch.await();
			} catch (InterruptedException e) {
				logger.error("",e);
			}
			String filePath=ruleService.mergeFile(rootPath,buildPath ,buildPath+File.separator+pathName);
			String rootPath = "build";
			expportList=ruleService.getExpportListFromFile(buildPath+filePath.replaceAll(rootPath, ""));
		}
		JSONObject jsonObject=new JSONObject();
		int i=0;
		try {
			jsonObject.put("code",0);
			jsonObject.put("msg", "succ");
			JSONArray dataArray=new JSONArray();
			for(Object o:expportList){
				BusinessRule businessRule=(BusinessRule)o;
				if(i%100==0){
					businessTask =(BusinessTask) businessTaskService.findById(businessTask.getId());
					if(!businessTask.getState().equals(BusinessTaskState.run.getCode())){
						return;
					}
				}
				expList = new ArrayList<Criterion>();// 查询条件
				orders = new ArrayList<Order>();// 排序条件
				expList.add(Restrictions.eq("BusinessRule.id", businessRule.getId()));
				orders.add(Order.asc("id"));
				PaginatedListHelper businessRuleDetailPlh=businessRuleDetailService.findList(expList, orders, null, null);
				if(businessRuleDetailPlh.getList()==null
						||businessRuleDetailPlh.getList().size()==0){
					continue;
				}
				JSONObject dataObject=new JSONObject();
				dataObject.put("host", businessRule.getHost());
				dataObject.put("exact", businessRule.getExact());
				dataObject.put("fuzzy", businessRule.getFuzzy());
				dataObject.put("objectid", businessRule.getId());
				dataObject.put("urlfilter", businessRule.getUrlfilter().trim());
				dataObject.put("ratekey", businessRule.getRatekey());
				JSONArray accountArray=new JSONArray();
				int pushrate=0;
				for(Object object:businessRuleDetailPlh.getList()){
						BusinessRuleDetail businessRuleDetail=(BusinessRuleDetail)object;
						if(!businessRuleDetail.getState().equals(BusinessRuleDetailState.normal.getCode())){
							continue;
						}
						JSONObject accountObject=new JSONObject();
						accountObject.put("ratekey", businessRuleDetail.getRatekey());
						accountObject.put("accountid", businessRuleDetail.getId());
						accountObject.put("pushcontent", businessRuleDetail.getContent());
						accountObject.put("pushtype", businessRuleDetail.getType());
						accountObject.put("pushrate", businessRuleDetail.getPushrate());
						if(pushrate==0){
							pushrate=businessRuleDetail.getPushrate();
						}else{
							if(pushrate>businessRuleDetail.getPushrate()){
								pushrate=businessRuleDetail.getPushrate();
							}
						}
						accountArray.put(accountObject);
				}
				if(accountArray.length()==0){
					continue;
				}
				i++;
				dataObject.put("pushrate", pushrate);
				dataObject.put("account", accountArray);
				dataArray.put(dataObject);
			}
			expList = new ArrayList<Criterion>();// 查询条件
			orders = new ArrayList<Order>();// 排序条件
			expList.add(Restrictions.eq("BusinessChannel.id", businessTask.getBusinessChannel().getId()));
			expList.add(Restrictions.eq("state", BusinessDnsState.normal.getCode()));
			total = businessDnsService.getList(expList, null);
			JSONArray dnsArray=new JSONArray();
			if(total>0){
				Pages totalpages = new Pages();
				totalpages.setPerPageNum(Integer.parseInt(expperpagenumConfig.getValue()));// 分页查询
				totalpages.setTotalNum(total);
				totalpages.executeCount();
				for (int j = 0; j < totalpages.getAllPage(); j++) {
					Pages pages = new Pages();
					pages.setPerPageNum(totalpages.getPerPageNum());
					pages.setSpage(j * totalpages.getPerPageNum());// 列表开始位置
					List<BaseRecord> list = businessDnsService.findList(expList, orders, pages.getSpage(), pages.getPerPageNum(),null);
					if (list == null || list.size() == 0) {
						break;
					}
					for(Object o:list){
						BusinessDns businessDns=(BusinessDns)o;
						JSONObject dnsObject=new JSONObject();
						dnsObject.put("src_domain", businessDns.getHost());
						dnsObject.put("dst_domain", businessDns.getContent());
						dnsObject.put("objectid", businessDns.getId());
						dnsArray.put(dnsObject);
					}
				}
			}
			jsonObject.put("dns_data", dnsArray);
			jsonObject.put("data", dataArray);
			jsonObject.put("num", i);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String filePath=ruleService.createApiFile(jsonObject.toString(), rootPath, buildPath);
		BusinessChannel businessChannel=(BusinessChannel) businessChannelService.findById(businessTask.getBusinessChannel().getId());
		businessChannel.setFileaddress(filePath);
		businessChannel.setUpdatetime(new Date());
		businessChannelService.updateObject(businessChannel, true);
		Date endDate=new Date();
		logger.info(String.format("start:%s,end:%s id:%s", 
				DateUtil.getDateTime(startDate, "HH:mm:ss"),DateUtil.getDateTime(endDate, "HH:mm:ss"),businessTask.getId()));
		businessTask.setRemark(String.format("开始:%s,结束:%s,条数:%s", 
				DateUtil.getDateTime(startDate,"HH:mm:ss"),DateUtil.getDateTime(endDate,"HH:mm:ss"),i));
		businessTask.setState(BusinessTaskState.finish.getCode());
		businessTask.setUpdatetime(new Date());
		businessTaskService.updateObject(businessTask, true);
	}
	
	
	private void saveBusinessTask(BusinessChannel businessChannel,
			BusinessTaskType businessTaskType,String uploadFilePath,JsonObject jsonObject ,String userId){
		BaseService businessTaskService=(BaseService) SpringConfig.getInstance().getService(BusinessTask.class);
		BusinessTask businessTask=new BusinessTask();
		businessTask.setBusinessChannel(businessChannel);
		businessTask.setCreatetime(new Date());
		businessTask.setCreateuserid(userId);
		businessTask.setUpdateuserid(businessTask.getCreateuserid());
		businessTask.setName(businessTaskType.getDisplay());
		businessTask.setState(BusinessTaskState.wait.getCode());
		businessTask.setTasktime(new Date());
		businessTask.setType(businessTaskType.getCode());
		if(StringUtils.isNotBlank(uploadFilePath)){
			businessTask.setUploadfile(uploadFilePath);
		}
		if(jsonObject!=null){
			businessTask.setContent(jsonObject.toString());
		}
		businessTask.setUpdatetime(new Date());
		businessTaskService.saveObject(businessTask);
	}

}
