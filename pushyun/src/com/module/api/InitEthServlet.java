package com.module.api;
import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.bean.BusinessChannel;
import com.common.servlet.Base;
import com.common.tools.DateUtil;
import com.common.tools.StrUtil;
import com.common.type.BusinessChannelCmdType;
import com.common.type.BusinessChannelOnlinestate;
import com.common.type.BusinessChannelState;
import com.google.gson.JsonObject;
import com.module.business.service.ChannelCmdService;
import com.module.data.service.ChannelLogService;
import com.spacesat.util.json.JSONArray;
import com.spacesat.util.json.JSONException;
import com.spacesat.util.json.JSONObject;

public class InitEthServlet extends Base{

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
			businessChannel=new BusinessChannel();
			businessChannel.setCode(sign);
			businessChannel.setName(sign);
			businessChannel.setSign(sign);
			businessChannel.setCreatetime(new Date());
			businessChannel.setCreateuserid(getManageUser(request).getId());
			businessChannel.setVersion(version);
			businessChannel.setUpdateuserid(businessChannel.getCreateuserid());
			businessChannel.setUpdatetime(businessChannel.getCreatetime());
			businessChannel.setOnlinestate(BusinessChannelOnlinestate.online.getCode());
			businessChannel.setState(BusinessChannelState.normal.getCode());
			businessChannel.setRemark("InitEthServlet自动新增");
			getBaseService(BusinessChannel.class).saveObject(businessChannel);
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
		if(StringUtils.isBlank(businessChannel.getSendermac())
				||StringUtils.isBlank(businessChannel.getSendername())
				||StringUtils.isBlank(businessChannel.getSniffernames())
				||StringUtils.isBlank(businessChannel.getRoutermac())){
			try {
				jsonObject.put("code",-1);//-1 参数错误 
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
		try {
			jsonObject.put("code",0);
			jsonObject.put("msg", "succ");
			JSONObject dataObject=new JSONObject();
			dataObject.put("routermac", businessChannel.getRoutermac());
			dataObject.put("server", businessChannel.getServeraddress());
			if(StringUtils.isNotBlank(businessChannel.getPushserveraddress())){
				dataObject.put("pushserver", businessChannel.getPushserveraddress());
			}else{
				dataObject.put("pushserver", "");
			}
			dataObject.put("threadCount", businessChannel.getThreadnum());
			dataObject.put("fuzzy_with_host_thread_count", businessChannel.getHostthreadnum()!=null?businessChannel.getHostthreadnum():0);
			dataObject.put("fuzzy_without_host_thread_count", businessChannel.getNohostthreadnum()!=null?businessChannel.getNohostthreadnum():0);
			JSONObject if_ether_senderObject=new JSONObject();
			if_ether_senderObject.put("mac", businessChannel.getSendermac());
			if_ether_senderObject.put("name", businessChannel.getSendername());
			dataObject.put("if_ether_sender", if_ether_senderObject);
			JSONArray if_ether_snifferArray=new JSONArray();
			for(String name:businessChannel.getSniffernames().split(",")){
				JSONObject if_ether_snifferObject=new JSONObject();
				if_ether_snifferObject.put("name", name);
				if_ether_snifferArray.put(if_ether_snifferObject);
			}
			dataObject.put("if_ether_sender", if_ether_senderObject);
			dataObject.put("if_ether_sniffer", if_ether_snifferArray);
			if(StringUtils.isNotBlank(businessChannel.getDnsserveraddress())){
				dataObject.put("dns_location_server", businessChannel.getDnsserveraddress());
			}else{
				dataObject.put("dns_location_server", "");
			}
			jsonObject.put("data", dataObject);
			String cmd=((ChannelCmdService)getService(ChannelCmdService.class)).getChannelCmd(businessChannel,BusinessChannelCmdType.other.getCode());
			if(StringUtils.isNotBlank(cmd)){
				jsonObject.put("cmd", cmd);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		((ChannelLogService)getService(ChannelLogService.class)).saveChannelLog(businessChannel, 
				getRemortIP(request), request.getRequestURI()+"?"+request.getQueryString(), 
				jsonObject.toString());
		this.sendWeb(jsonObject.toString(), "application/json;charset=utf-8", request, response);
		return ;
	}

	
	
}
