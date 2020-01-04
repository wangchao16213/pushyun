package com.module.business.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.bean.BaseRecord;
import com.bean.BusinessChannel;
import com.bean.BusinessChannelCmd;
import com.common.config.SpringConfig;
import com.common.service.BaseService;
import com.common.type.BusinessChannelCmdState;
import com.common.type.BusinessChannelCmdType;
import com.common.web.Pages;
import com.common.web.PaginatedListHelper;
import com.module.business.service.ChannelCmdService;

public class ChannelCmdServiceImpl implements ChannelCmdService{

	@Override
	public String getChannelCmd(BusinessChannel businessChannel,String type) {
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.eq("BusinessChannel.id", businessChannel.getId()));
		expList.add(Restrictions.eq("state", BusinessChannelCmdState.wait.getCode()));
		expList.add(Restrictions.eq("type", type));
		orders.add(Order.desc("updatetime"));
		BaseService businessChannelCmdService=(BaseService) SpringConfig.getInstance().getService(BusinessChannelCmd.class);
		Pages pages = new Pages();
		pages.setPage(1);
		pages.setPerPageNum(1);
		PaginatedListHelper businessChannelCmdPlh=businessChannelCmdService.findList(expList, orders, pages, null);
		if(businessChannelCmdPlh.getList()==null
				||businessChannelCmdPlh.getList().size()==0){
			return null;
		}
		BusinessChannelCmd businessChannelCmd=((BusinessChannelCmd)businessChannelCmdPlh.getList().get(0));
		String cmd=businessChannelCmd.getCmd();
		expList = new ArrayList<Criterion>();// 查询条件
		orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.eq("BusinessChannel.id", businessChannel.getId()));
		expList.add(Restrictions.eq("state", BusinessChannelCmdState.wait.getCode()));
		expList.add(Restrictions.eq("type", type));
		expList.add(Restrictions.eq("cmd", cmd));
		List<BaseRecord> businessChannelCmdList=businessChannelCmdService.findList(expList, orders, 0,9999, null);
		for(Object o:businessChannelCmdList){
			BusinessChannelCmd channelCmd=(BusinessChannelCmd)o;
			channelCmd.setUpdatetime(new Date());
			channelCmd.setState(BusinessChannelCmdState.succ.getCode());
			businessChannelCmdService.updateObject(channelCmd,true);
		}
		return cmd;
	}

}
