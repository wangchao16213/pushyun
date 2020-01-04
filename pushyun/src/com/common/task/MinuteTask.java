package com.common.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.bean.BaseRecord;
import com.bean.BusinessChannel;
import com.common.config.SpringConfig;
import com.common.service.BaseService;
import com.common.tools.DateUtil;
import com.common.type.BusinessChannelOnlinestate;
import com.common.web.Pages;
import com.common.web.PaginatedListHelper;

public class MinuteTask implements Job{
	
	private final Logger logger = Logger.getLogger(MinuteTask.class.getName());

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try{
			checkBusinessChannel();
		}catch (Exception e) {
			logger.error("",e);
		}
	}
	
	private void checkBusinessChannel(){
		BaseService businessChannelService=(BaseService) SpringConfig.getInstance().getService(BusinessChannel.class);
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.eq("onlinestate", BusinessChannelOnlinestate.online.getCode()));
		PaginatedListHelper businessChannelPlh=businessChannelService.findList(expList, orders, null, null);
		if(businessChannelPlh.getList()==null
				||businessChannelPlh.getList().size()==0){
			return;
		}
		for(Object o:businessChannelPlh.getList()){
			BusinessChannel businessChannel=(BusinessChannel)o;
			double iResult=DateUtil.minuSecond(businessChannel.getUpdatetime(), new Date());
			if(iResult>=5400){
				businessChannel.setOnlinestate(BusinessChannelOnlinestate.offline.getCode());
				businessChannel.setUpdatetime(new Date());
				businessChannelService.updateObject(businessChannel,true);
			}
		}
	}

	
	
}
