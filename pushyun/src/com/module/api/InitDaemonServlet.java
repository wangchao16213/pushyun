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
import com.bean.BusinessChannelDaemon;
import com.common.servlet.Base;
import com.common.tools.DateUtil;
import com.common.tools.StrUtil;
import com.common.tools.WebUtil;
import com.common.type.BusinessChannelDaemonUnzip;
import com.common.type.BusinessChannelOnlinestate;
import com.common.type.BusinessChannelState;
import com.common.type.BusinessChannelDaemonState;
import com.common.web.PaginatedListHelper;
import com.module.data.service.ChannelLogService;
import com.spacesat.util.json.JSONArray;
import com.spacesat.util.json.JSONException;
import com.spacesat.util.json.JSONObject;

public class InitDaemonServlet extends Base{

	private static final long serialVersionUID = 1L;

	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String pointid=request.getParameter("pointid");
		String sign=request.getParameter("sign");
		String version=request.getParameter("version");
		JSONObject jsonObject=new JSONObject();
		if(StringUtils.isBlank(sign)){
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
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.eq("BusinessChannel.id", businessChannel.getId()));
		expList.add(Restrictions.eq("state", BusinessChannelDaemonState.normal.getCode()));
		orders.add(Order.asc("seq"));
		PaginatedListHelper businessChannelDaemonPlh=getBaseService(BusinessChannelDaemon.class).findList(expList, orders, null, null);
		if(businessChannelDaemonPlh.getList()==null
				||businessChannelDaemonPlh.getList().size()==0){
			((ChannelLogService)getService(ChannelLogService.class)).saveChannelLog(businessChannel, 
					getRemortIP(request), request.getRequestURI()+"?"+request.getQueryString(), 
					jsonObject.toString());
			this.sendWeb(jsonObject.toString(), "application/json;charset=utf-8", request, response);
			return ;
		}
		try{
			JSONArray applicationsArray=new JSONArray();
			for(Object o:businessChannelDaemonPlh.getList()){
				BusinessChannelDaemon businessChannelDaemon=(BusinessChannelDaemon)o;
				JSONObject applicationObject=new JSONObject();
				JSONObject jsobject=new JSONObject();
				applicationObject.put("exec", businessChannelDaemon.getExec());
				if(businessChannelDaemon.getUnzip().equals(BusinessChannelDaemonUnzip.normal.getCode())){
					applicationObject.put("unzip", true);
				}else{
					applicationObject.put("unzip", false);
				}
				if(businessChannelDaemon.getUrl().startsWith("http")){
					applicationObject.put("url", businessChannelDaemon.getUrl());
				}else{
					applicationObject.put("url", WebUtil.getServerUrl(request)+businessChannelDaemon.getUrl());
				}
				jsobject.put("application", applicationObject);
				applicationsArray.put(jsobject);
			}
			jsonObject.put("applications", applicationsArray);
			((ChannelLogService)getService(ChannelLogService.class)).saveChannelLog(businessChannel, 
					getRemortIP(request), request.getRequestURI()+"?"+request.getQueryString(), 
					jsonObject.toString());
			this.sendWeb(jsonObject.toString(), "application/json;charset=utf-8", request, response);
			return ;
		}catch (Exception e) {
			e.printStackTrace();
		}	
	}

}
