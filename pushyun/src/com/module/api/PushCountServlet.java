package com.module.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.bean.BaseRecord;
import com.bean.BusinessChannel;
import com.bean.BusinessRule;
import com.bean.BusinessRuleDetail;
import com.bean.DataStatistics;
import com.bean.LogRuleStatistics;
import com.bean.LogSnifferStatistics;
import com.common.comm.Constants;
import com.common.service.SendService;
import com.common.servlet.Base;
import com.common.tools.DateUtil;
import com.common.tools.StrUtil;
import com.common.type.BusinessChannelCmdType;
import com.common.type.BusinessChannelOnlinestate;
import com.common.type.BusinessChannelState;
import com.common.type.DataStatisticsState;
import com.common.type.LogRuleStatisticsState;
import com.common.type.LogSnifferStatisticsState;
import com.common.web.PaginatedListHelper;
import com.module.business.service.ChannelCmdService;
import com.module.data.service.ChannelLogService;
import com.spacesat.util.json.JSONArray;
import com.spacesat.util.json.JSONException;
import com.spacesat.util.json.JSONObject;

public class PushCountServlet extends Base{

	private static final long serialVersionUID = 1L;

	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String pointid=request.getParameter("pointid");
		String sign=request.getParameter("sign");
		String version=request.getParameter("version");
		JSONObject jsonObject=new JSONObject();
		if(StringUtils.isBlank(sign)){
			try {
				jsonObject.put("code",-1);
				jsonObject.put("msg", "sign is empty");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			((ChannelLogService)getService(ChannelLogService.class)).saveChannelLog(null, 
					getRemortIP(request), request.getRequestURI()+"?"+request.getQueryString(), 
					jsonObject.toString());
			this.sendWeb(jsonObject.toString(), "application/json;charset=utf-8", request, response);
			return ;
		}
		sign=StrUtil.getMacBySetFormat(sign);
		BusinessChannel businessChannel=(BusinessChannel) getBaseService(BusinessChannel.class).findByProperty("sign", sign);
		if(businessChannel==null){
			((ChannelLogService)getService(ChannelLogService.class)).saveChannelLog(businessChannel, 
					getRemortIP(request), request.getRequestURI()+"?"+request.getQueryString(), 
					jsonObject.toString());
			this.sendWeb(jsonObject.toString(), "application/json;charset=utf-8", request, response);
			return ;
		}else{
			if(businessChannel.getOnlinestate().equals(BusinessChannelOnlinestate.offline.getCode())){
				businessChannel.setUpdateuserid(getManageUser(request).getId());
				businessChannel.setUpdatetime(new Date());
				businessChannel.setOnlinestate(BusinessChannelOnlinestate.online.getCode());
				getBaseService(BusinessChannel.class).updateObject(businessChannel,true);
			}else{
				double iResult=DateUtil.minuSecond(businessChannel.getUpdatetime(), new Date());
				if(iResult>=1800){
					businessChannel.setUpdateuserid(getManageUser(request).getId());
					businessChannel.setUpdatetime(new Date());
					businessChannel.setOnlinestate(BusinessChannelOnlinestate.online.getCode());
					getBaseService(BusinessChannel.class).updateObject(businessChannel,true);
				}
			}
		}
		BufferedReader br=null;
		StringBuffer sb = new StringBuffer();  
		try{
			br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream(), "utf-8"));  
			String temp;  
			while ((temp = br.readLine()) != null) {  
		        sb.append(temp);  
		    }
		}catch (Exception e) {  
	        e.printStackTrace();    
	    }finally{
	    	if(br!=null){
	    		br.close();
	    	}
	    }
		if(StringUtils.isBlank(sb.toString())){
			try {
				jsonObject.put("code",-1);
				jsonObject.put("msg", "lack of information");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			((ChannelLogService)getService(ChannelLogService.class)).saveChannelLog(businessChannel, 
					getRemortIP(request), request.getRequestURI()+"?"+request.getQueryString(), 
					jsonObject.toString());
			this.sendWeb(jsonObject.toString(), "application/json;charset=utf-8", request, response);
			return ;
		}
		((SendService)getService(SendService.class)).sendQueue(Constants.TASK_JMS_PUSHCOUNT_QUEUE_NAME,sb.toString());
		try {
			jsonObject.put("code",0);
			jsonObject.put("msg", "succ");
			String cmd=((ChannelCmdService)getService(ChannelCmdService.class)).getChannelCmd(businessChannel,BusinessChannelCmdType.other.getCode());
			if(StringUtils.isNotBlank(cmd)){
				jsonObject.put("cmd", cmd);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		((ChannelLogService)getService(ChannelLogService.class)).saveChannelLog(businessChannel, 
				getRemortIP(request), request.getRequestURI()+"?"+request.getQueryString(), jsonObject.toString());
		this.sendWeb(jsonObject.toString(), "application/json;charset=utf-8", request, response);
		return ;
	}

}
