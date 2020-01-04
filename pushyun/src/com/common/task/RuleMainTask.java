package com.common.task;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContextEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.bean.BaseConfig;
import com.bean.BaseRecord;
import com.bean.BusinessChannel;
import com.bean.BusinessTask;
import com.bean.ManageMenu;
import com.bean.ManageUser;
import com.common.config.SpringConfig;
import com.common.service.BaseService;
import com.common.tools.MD5Encrypt;
import com.common.type.BaseConfigCode;
import com.common.type.BaseConfigState;
import com.common.type.BusinessChannelState;
import com.common.type.BusinessTaskState;
import com.common.type.BusinessTaskType;
import com.common.type.ManageMenuState;
import com.common.type.ManageUserState;
import com.common.web.PaginatedListHelper;

public class RuleMainTask implements Runnable{
	
	private final Logger logger = Logger.getLogger(RuleMainTask.class.getName());
	
	private String rootPath;
	
	private String buildPath;
	
	
	public RuleMainTask(String rootPath,String buildPath){
		this.rootPath=rootPath;
		this.buildPath=buildPath;
	}
	

	@Override
	public void run() {
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		while(true){
			try{
				getBusinessTask();
			}catch (Exception e) {
				logger.error("", e);
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	


	
	private void getBusinessTask(){
		BaseService baseConfigService=(BaseService) SpringConfig.getInstance().getService(BaseConfig.class);
		BaseService businessTaskService=(BaseService) SpringConfig.getInstance().getService(BusinessTask.class);
		BaseConfig baseConfig=(BaseConfig) baseConfigService.findByProperty("code", BaseConfigCode.taskthreadnum.getCode());
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.eq("state", BusinessTaskState.run.getCode()));
		List<BaseRecord> list=businessTaskService.findList(expList, orders, 0, 9999, null);
		int threadnum=0;
		List<String> runBusinessTaskIdList=new ArrayList<String>();
		if(list!=null
				&&list.size()>0){
			threadnum=list.size();
			for(Object o:list){
				BusinessTask businessTask=(BusinessTask)o;
				runBusinessTaskIdList.add(businessTask.getBusinessChannel().getId());
			}
		}
		if(threadnum<Integer.parseInt(baseConfig.getValue())){
			expList = new ArrayList<Criterion>();// 查询条件
			orders = new ArrayList<Order>();// 排序条件
			expList.add(Restrictions.eq("state", BusinessTaskState.wait.getCode()));
			if(runBusinessTaskIdList.size()>0){
				expList.add(Restrictions.not(Restrictions.in("BusinessChannel.id", runBusinessTaskIdList)));
			}
			orders.add(Order.asc("updatetime"));
			list=businessTaskService.findList(expList, orders, 0, 1, null);
			if(list==null
					||list.size()==0){
				sleep(2);
				return;
			}
			BusinessTask businessTask=(BusinessTask)list.get(0);
			reset(businessTask);
			businessTask.setState(BusinessTaskState.run.getCode());
			businessTask.setUpdatetime(new Date());
			businessTaskService.updateObject(businessTask, true);
			logger.info(String.format("threadnum:%s,start:%s,code|name:%s",
					threadnum ,businessTask.getType(),
					businessTask.getBusinessChannel().getCode()+"|"+businessTask.getBusinessChannel().getName()));
			RuleChildTask ruleChildTask=new RuleChildTask(rootPath,buildPath,businessTask.getId());
			new Thread(ruleChildTask).start();
		}else{
			sleep(5);
			return;
		}
	}
	
	private void reset(BusinessTask businessTask){
		BaseService businessTaskService=(BaseService) SpringConfig.getInstance().getService(BusinessTask.class);
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.eq("state", BusinessTaskState.wait.getCode()));
		expList.add(Restrictions.ne("id", businessTask.getId()));
		expList.add(Restrictions.eq("BusinessChannel.id", businessTask.getBusinessChannel().getId()));
		expList.add(Restrictions.eq("type", businessTask.getType()));
		if(businessTask.getType().equals(BusinessTaskType.exp.getCode())
				||businessTask.getType().equals(BusinessTaskType.reset.getCode())
				||businessTask.getType().equals(BusinessTaskType.truncate.getCode())){
			expList.add(Restrictions.eq("content", businessTask.getContent()));
		}
		if(businessTask.getType().equals(BusinessTaskType.imp.getCode())){
			expList.add(Restrictions.eq("uploadfile", businessTask.getUploadfile()));
		}
		List<BaseRecord> baseRecordList=businessTaskService.findList(expList, orders, 0, 9999, null);
		if(baseRecordList==null
				||baseRecordList.size()==0){
			return;
		}
		for(Object o:baseRecordList){
			BusinessTask task=(BusinessTask)o;
			task.setState(BusinessTaskState.stop.getCode());
			task.setUpdatetime(new Date());
			businessTaskService.updateObject(task, true);
		}
	}
	

	private void sleep(int second){
		try {
			Thread.sleep(second*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
