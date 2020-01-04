package com.module.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.bean.BusinessChannel;
import com.bean.BusinessChannelCmd;
import com.common.config.SpringConfig;
import com.common.service.BaseService;
import com.common.servlet.Base;
import com.common.tools.DateUtil;
import com.common.tools.StrUtil;
import com.common.type.BusinessChannelCmdState;
import com.common.type.BusinessChannelCmdType;
import com.common.type.BusinessChannelOnlinestate;
import com.common.type.BusinessChannelState;
import com.common.web.Pages;
import com.common.web.PaginatedListHelper;
import com.module.business.service.ChannelCmdService;
import com.module.data.service.ChannelLogService;
import com.spacesat.util.json.JSONException;
import com.spacesat.util.json.JSONObject;

public class CommandServlet extends Base{


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
				jsonObject.put("cmd","none");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			((ChannelLogService)getService(ChannelLogService.class)).saveChannelLog(null, 
					getRemortIP(request), request.getRequestURI()+"?"+request.getQueryString(),jsonObject.toString());
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
			businessChannel.setRemark("GetControlServlet自动新增");
			getBaseService(BusinessChannel.class).saveObject(businessChannel);
			((ChannelLogService)getService(ChannelLogService.class)).saveChannelLog(businessChannel, 
					getRemortIP(request), request.getRequestURI()+"?"+request.getQueryString(),jsonObject.toString());
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
		String cmd=((ChannelCmdService)getService(ChannelCmdService.class)).getChannelCmd(businessChannel,BusinessChannelCmdType.control.getCode());
		if(StringUtils.isNotBlank(cmd)){
			try {
				jsonObject.put("cmd",cmd);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			try {
				jsonObject.put("cmd","none");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		((ChannelLogService)getService(ChannelLogService.class)).saveChannelLog(businessChannel, 
				getRemortIP(request), request.getRequestURI()+"?"+request.getQueryString(),jsonObject.toString());
		this.sendWeb(jsonObject.toString(), "application/json;charset=utf-8", request, response);
		return ;
	}

}
