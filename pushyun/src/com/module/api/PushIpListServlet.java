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

import com.bean.BusinessChannel;
import com.bean.BusinessWhitelist;
import com.common.servlet.Base;
import com.common.tools.StrUtil;
import com.common.web.PaginatedListHelper;
import com.module.data.service.ChannelLogService;
import com.spacesat.util.json.JSONArray;
import com.spacesat.util.json.JSONException;
import com.spacesat.util.json.JSONObject;

public class PushIpListServlet extends Base{


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
				jsonObject.put("msg", "lack of information");
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
	    try {
			JSONObject dataObject=new JSONObject(sb.toString());
			if(!dataObject.has("data")){
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
			JSONArray dataArray=dataObject.getJSONArray("data");
			Date d=new Date();
			for(int i=0;i<dataArray.length();i++){
				JSONObject object=dataArray.getJSONObject(i);
				String ip=object.getString("IP");
				String account=object.getString("User_Name");
				List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
				List<Order> orders = new ArrayList<Order>();// 排序条件
				expList.add(Restrictions.eq("BusinessChannel.id", businessChannel.getId()));
				expList.add(Restrictions.eq("account", account));
				PaginatedListHelper businessWhitelistPlh=getBaseService(BusinessWhitelist.class).findList(expList, orders, null, null);
				if(businessWhitelistPlh.getList()==null
						||businessWhitelistPlh.getList().size()==0){
					BusinessWhitelist businessWhitelist=new BusinessWhitelist();
					businessWhitelist.setBusinessChannel(businessChannel);
					businessWhitelist.setAccount(account);
					businessWhitelist.setCreatetime(new Date());
					businessWhitelist.setIp(ip);
					businessWhitelist.setUpdatetime(businessWhitelist.getCreatetime());
					getBaseService(BusinessWhitelist.class).saveObject(businessWhitelist);
				}else{
					for(Object o:businessWhitelistPlh.getList()){
						BusinessWhitelist businessWhitelist=(BusinessWhitelist)o;
						businessWhitelist.setUpdatetime(new Date());
						businessWhitelist.setIp(ip);
						getBaseService(BusinessWhitelist.class).updateObject(businessWhitelist, true);
					}
				}
			}
	    } catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			jsonObject.put("code",0);
			jsonObject.put("msg", "succ");
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
