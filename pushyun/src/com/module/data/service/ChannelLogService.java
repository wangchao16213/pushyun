package com.module.data.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.bean.BusinessChannel;
import com.bean.DataChannelLog;

public interface ChannelLogService {
	
	@SuppressWarnings("unchecked")
	public int saveChannelLog(BusinessChannel businessChannel,String ip,
			String queryString,String replyrecord) throws IOException;
}
