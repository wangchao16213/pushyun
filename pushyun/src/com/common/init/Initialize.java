package com.common.init;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;


import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.bean.BaseConfig;
import com.bean.BaseRecord;
import com.bean.BusinessChannel;
import com.bean.BusinessRule;
import com.bean.BusinessRuleDetail;
import com.bean.BusinessTask;
import com.bean.ManageMenu;
import com.bean.ManageUser;
import com.common.comm.Constants;
import com.common.config.SpringConfig;
import com.common.jms.JmsListener;
import com.common.jms.JmsPool;
import com.common.service.BaseService;
import com.common.task.DayTask;
import com.common.task.MinuteTask;
import com.common.task.RuleMainTask;
import com.common.tools.MD5Encrypt;
import com.common.type.BaseConfigCode;
import com.common.type.BaseConfigState;
import com.common.type.BusinessChannelState;
import com.common.type.BusinessRulePushrate;
import com.common.type.BusinessRuleState;
import com.common.type.BusinessTaskState;
import com.common.type.BusinessTaskType;
import com.common.type.EnumMessage;
import com.common.type.ManageMenuState;
import com.common.type.ManageUserState;
import com.common.web.Pages;
import com.common.web.PaginatedListHelper;
import com.module.business.service.RuleService;
import com.mongodb.annotations.ThreadSafe;



public class Initialize  implements ServletContextListener{
	
	private final Logger logger = Logger.getLogger(Initialize.class.getName());
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		 
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ServletContext servletContext=servletContextEvent.getServletContext();  
		Map<Class, Map<String, EnumMessage>> map=Constants.ENUM_MAP;

////		initPushrate();
////		initRatekey();
		initConfig();
		logger.info("initUser");
		initUser(); 
		initMenu();
		logger.info("initMenu");
		initRule(servletContextEvent);
		initTask();
		
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		try {
			Scheduler scheduler = schedulerFactory.getScheduler();
			scheduler.getContext().put("servletContext", servletContextEvent.getServletContext());
			JobDetail minuteDetail = new JobDetail("minuteDetail", "jobDetailGroup",MinuteTask.class);
			JobDetail dayDetail = new JobDetail("dayDetail", "jobDetailGroup",DayTask.class);
			CronTrigger minuteTrigger = new CronTrigger("minuteTrigger","triggerGroup");
			CronTrigger dayTrigger = new CronTrigger("dayTrigger","triggerGroup");
			try {
				CronExpression minuteCexp = new CronExpression("0 0/10 * * * ?");
				minuteTrigger.setCronExpression(minuteCexp);
				CronExpression dayCexp = new CronExpression("0 0 0 * * ?");
				dayTrigger.setCronExpression(dayCexp);
			} catch (ParseException e) {
				e.printStackTrace();
			} 
			scheduler.scheduleJob(dayDetail, dayTrigger);
			scheduler.scheduleJob(minuteDetail, minuteTrigger);
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		JmsListener pushcountListener = new JmsListener();
		pushcountListener.setListening(true);
		pushcountListener.setQueueName(Constants.TASK_JMS_PUSHCOUNT_QUEUE_NAME);
		pushcountListener.setSleepTime(100);
		new Thread(pushcountListener).start();
		
		String rootPath = "build";
		String buildPath = servletContextEvent.getServletContext().getRealPath(rootPath);// 生成路径
		RuleMainTask ruleMainTask=new RuleMainTask( rootPath, buildPath);
		new Thread(ruleMainTask).start();
	}

	
	private void initConfig(){
		BaseService baseConfigService=(BaseService) SpringConfig.getInstance().getService(BaseConfig.class);
		for(BaseConfigCode baseConfigCode:BaseConfigCode.values()){
			BaseConfig baseConfig=(BaseConfig) baseConfigService.findByProperty("code", baseConfigCode.getCode());
			if(baseConfig!=null){
				continue;
			}
			baseConfig=new BaseConfig();
			baseConfig.setCode(baseConfigCode.getCode());
			baseConfig.setName(baseConfigCode.getDisplay());
			baseConfig.setValue(baseConfigCode.getValue());
			baseConfig.setState(BaseConfigState.normal.getCode());
			baseConfigService.saveObject(baseConfig);
		}
	}
	
	private void initTask(){
		BaseService businessTaskService=(BaseService) SpringConfig.getInstance().getService(BusinessTask.class);
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.eq("state", BusinessTaskState.run.getCode()));
		List<BaseRecord> list=businessTaskService.findList(expList, orders, 0, 9999, null);
		if(list==null
				||list.size()==0){
			return;
		}
		for(Object o:list){
			BusinessTask businessTask=(BusinessTask)o;
			businessTask.setState(BusinessTaskState.wait.getCode());
			businessTask.setUpdatetime(new Date());
			businessTaskService.updateObject(businessTask, true); 
		}
	}
	
	private void initRule(ServletContextEvent servletContextEvent){
		BaseService businessChannelService=(BaseService) SpringConfig.getInstance().getService(BusinessChannel.class);
		BaseService businessTaskService=(BaseService) SpringConfig.getInstance().getService(BusinessTask.class);
		String rootPath = "build";
		String buildPath = servletContextEvent.getServletContext().getRealPath(rootPath);// 生成路径
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.eq("state", BusinessChannelState.normal.getCode()));
		PaginatedListHelper businessChannelPlh=businessChannelService.findList(expList, orders, null, null);
		if(businessChannelPlh==null||businessChannelPlh.getList()==null){
			return;
		}
		for(Object o:businessChannelPlh.getList()){
			BusinessChannel businessChannel=(BusinessChannel)o;
			if(StringUtils.isNotBlank(businessChannel.getFileaddress())){
				String filePath=buildPath+businessChannel.getFileaddress().replaceAll(rootPath, "");
				File pathFile = new File(filePath);
	    		if (pathFile.exists()) {
	    			continue;
	    		}
			}
			BusinessTask businessTask=new BusinessTask();
			businessTask.setBusinessChannel(businessChannel);
			businessTask.setCreatetime(new Date());
			businessTask.setName(BusinessTaskType.api.getDisplay());
			businessTask.setState(BusinessTaskState.wait.getCode());
			businessTask.setTasktime(new Date());
			businessTask.setType(BusinessTaskType.api.getCode());
			businessTask.setUpdatetime(new Date());
			businessTaskService.saveObject(businessTask);
		}
	}
	
	private void initRatekey(){
		BaseService businessRuleService= (BaseService) SpringConfig.getInstance().getService(BusinessRule.class);
		BaseService businessRuleDetailService= (BaseService) SpringConfig.getInstance().getService(BusinessRuleDetail.class);
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		orders.add(Order.asc("id"));
		int total = businessRuleDetailService.getList(expList, null);
		Pages totalpages = new Pages();
		totalpages.setPerPageNum(10000);// 分页查询
		totalpages.setTotalNum(total);
		totalpages.executeCount();
		for (int i = 0; i < totalpages.getAllPage(); i++) {
			Pages pages = new Pages();
			pages.setPerPageNum(totalpages.getPerPageNum());
			pages.setSpage(i * totalpages.getPerPageNum());// 列表开始位置
			List<BaseRecord> list = businessRuleDetailService.findList(expList, orders, pages.getSpage(), pages.getPerPageNum(),null);
			if (list == null || list.size() == 0) {
				break;
			}
			for(Object o:list){
				BusinessRuleDetail businessRuleDetail=(BusinessRuleDetail)o;
				if(StringUtils.isBlank(businessRuleDetail.getRatekey())){
					BusinessRule businessRule=(BusinessRule) businessRuleService.findById(businessRuleDetail.getBusinessRule().getId());
					if(StringUtils.isNotBlank(businessRule.getRatekey())){
						businessRuleDetail.setRatekey(businessRule.getRatekey());
						businessRuleDetailService.updateObject(businessRuleDetail,true);
					}
				}
			}
		}
	}
	
	private void initPushrate(){
		BaseService businessRuleService= (BaseService) SpringConfig.getInstance().getService(BusinessRule.class);
		BaseService businessRuleDetailService= (BaseService) SpringConfig.getInstance().getService(BusinessRuleDetail.class);
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		orders.add(Order.asc("id"));
		int total = businessRuleDetailService.getList(expList, null);
		Pages totalpages = new Pages();
		totalpages.setPerPageNum(5000);// 分页查询
		totalpages.setTotalNum(total);
		totalpages.executeCount();
		for (int i = 0; i < totalpages.getAllPage(); i++) {
			Pages pages = new Pages();
			pages.setPerPageNum(totalpages.getPerPageNum());
			pages.setSpage(i * totalpages.getPerPageNum());// 列表开始位置
			List<BaseRecord> list = businessRuleDetailService.findList(expList, orders, pages.getSpage(), pages.getPerPageNum(),null);
			if (list == null || list.size() == 0) {
				break;
			}
			for(Object o:list){
				BusinessRuleDetail businessRuleDetail=(BusinessRuleDetail)o;
				if(businessRuleDetail.getPushrate()==null){
					BusinessRule businessRule=(BusinessRule) businessRuleService.findById(businessRuleDetail.getBusinessRule().getId());
					businessRuleDetail.setPushrate(businessRule.getPushrate());
					businessRuleDetailService.updateObject(businessRuleDetail,true);
				}
			}
		}
	}
	
	private void initUser(){
		BaseService usersService= (BaseService) SpringConfig.getInstance().getService(ManageUser.class);
		ManageUser manageUser=(ManageUser) usersService.findByProperty("username", "admin");
		if(manageUser!=null){
			return;
		}
		manageUser=new ManageUser();
		manageUser.setPasswd(MD5Encrypt.MD5("123456"));
		manageUser.setUsername("admin");
		manageUser.setCreatetime(new Date());
		manageUser.setUpdatetime(manageUser.getCreatetime());
		manageUser.setState(ManageUserState.normal.getCode());
		usersService.saveObject(manageUser);
	}
	private void initMenu(){
		BaseService menuService= (BaseService) SpringConfig.getInstance().getService(ManageMenu.class);
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		PaginatedListHelper manageMenuPlh=menuService.findList(expList,orders, null, null);
		if(manageMenuPlh.getList()!=null
				&&manageMenuPlh.getList().size()>0){
			return;
		}
		ManageMenu manageMenu100 =getManageMenu("100","规则管理",null,1,null);
		menuService.saveObject(manageMenu100);
		ManageMenu manageMenu200 =getManageMenu("200","数据管理",null,1,null);
		menuService.saveObject(manageMenu200);
		ManageMenu manageMenu200100 =getManageMenu("200100","统计数据管理","html/module/data/statistics/list.jsp",1,manageMenu200.getId());
		menuService.saveObject(manageMenu200100);
		manageMenu200100 =getManageMenu("200200","请求数据管理","html/module/data/channellog/list.jsp",1,manageMenu200.getId());
		menuService.saveObject(manageMenu200100);
		
		ManageMenu manageMenu300 =getManageMenu("300","渠道管理",null,1,null);
		menuService.saveObject(manageMenu300);
		ManageMenu manageMenu300100 =getManageMenu("300100","渠道信息管理","html/module/business/channel/list.jsp",1,manageMenu300.getId());
		menuService.saveObject(manageMenu300100);
		manageMenu300100 =getManageMenu("300200","命令队列管理","html/module/business/channelcmd/list.jsp",2,manageMenu300.getId());
		menuService.saveObject(manageMenu300100);
		
		ManageMenu manageMenu400 =getManageMenu("400","系统管理",null,1,null);
		menuService.saveObject(manageMenu400);
		ManageMenu manageMenu400100 =getManageMenu("400100","用户管理","html/module/manage/user/list.jsp",1,manageMenu400.getId());
		menuService.saveObject(manageMenu400100);
		manageMenu400100 =getManageMenu("400200","菜单管理","html/module/manage/menu/list.jsp",2,manageMenu400.getId());
		menuService.saveObject(manageMenu400100);
		ManageMenu manageMenu500 =getManageMenu("500","DNS管理",null,2,null);
		menuService.saveObject(manageMenu500);
	}
	
	
	private ManageMenu getManageMenu(String code,String name,String url,int seq,String parentid){
		ManageMenu manageMenu=new ManageMenu();
		manageMenu.setCode(code);
		manageMenu.setCreatetime(new Date());
		manageMenu.setName(name);
		manageMenu.setSeq(seq);
		if(StringUtils.isNotBlank(url)){
			manageMenu.setUrl(url);
		}
		if(StringUtils.isNotBlank(parentid)){
			manageMenu.setParentid(parentid);
		}
		manageMenu.setState(ManageMenuState.normal.getCode());
		manageMenu.setUpdatetime(manageMenu.getCreatetime());
		return manageMenu;
	}
	
	
	

}
