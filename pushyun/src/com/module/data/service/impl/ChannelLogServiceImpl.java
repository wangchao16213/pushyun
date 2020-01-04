package com.module.data.service.impl;

import java.io.IOException;
import java.util.Date;
import org.apache.log4j.Logger;
import com.bean.BusinessChannel;
import com.bean.DataChannelLog;
import com.common.config.SpringConfig;
import com.common.service.BaseService;
import com.common.tools.DateUtil;
import com.common.type.DataChannelLogState;
import com.module.data.service.ChannelLogService;


public class ChannelLogServiceImpl implements ChannelLogService{
	
	private final Logger logger = Logger.getLogger(ChannelLogServiceImpl.class.getName());

	@Override
	public int saveChannelLog(BusinessChannel businessChannel,String ip,
			String queryString,String replyrecord) throws IOException {
		BaseService dataChannelLogService=(BaseService) SpringConfig.getInstance().getService(DataChannelLog.class);
		DataChannelLog dataChannelLog=new DataChannelLog();
		dataChannelLog.setBusinessChannel(businessChannel);
		dataChannelLog.setIp(ip);
		dataChannelLog.setSdate(DateUtil.getDate(new Date()));
		dataChannelLog.setState(DataChannelLogState.normal.getCode());
		if(replyrecord.length()>500){
			replyrecord=replyrecord.substring(0, 500);
		}
		dataChannelLog.setReplyrecord(replyrecord);
		dataChannelLog.setRemark(queryString);
		dataChannelLog.setCreatetime(new Date());
		dataChannelLog.setUpdatetime(dataChannelLog.getCreatetime());
		int state=dataChannelLogService.saveObject(dataChannelLog);
		return state;
	}


}
