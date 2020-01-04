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

import com.bean.BaseRecord;
import com.bean.BusinessChannel;
import com.bean.BusinessWhitelist;
import com.common.servlet.Base;
import com.common.tools.StrUtil;
import com.common.web.Pages;
import com.module.data.service.ChannelLogService;
import com.spacesat.util.json.JSONArray;
import com.spacesat.util.json.JSONException;
import com.spacesat.util.json.JSONObject;

public class GetBlackListsServlet extends Base{

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
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.eq("BusinessChannel.id", businessChannel.getId()));
		int total=getBaseService(BusinessWhitelist.class).getList(expList, null);
		List<Object> expportList = new ArrayList<Object>();
		Pages totalpages = new Pages();
		totalpages.setPerPageNum(5000);// 分页查询
		totalpages.setTotalNum(total);
		totalpages.executeCount();
		for (int i = 0; i < totalpages.getAllPage(); i++) {
			Pages pages = new Pages();
			pages.setPerPageNum(totalpages.getPerPageNum());
			pages.setSpage(i * totalpages.getPerPageNum());// 列表开始位置
			List<BaseRecord> list = getBaseService(BusinessWhitelist.class).findList(expList, orders, pages.getSpage(), pages.getPerPageNum(),null);
			if (list == null || list.size() == 0) {
				break;
			}
			expportList.addAll(list);
		}
		try{
			JSONArray blacklistsArray=new JSONArray();
			for(Object o:expportList){
				BusinessWhitelist businessWhitelist=(BusinessWhitelist)o;
				JSONObject blacklistsObject=new JSONObject();
				blacklistsObject.put("IP", businessWhitelist.getIp());
				blacklistsObject.put("User_Name", businessWhitelist.getAccount());
				blacklistsArray.put(blacklistsObject);
			}
			jsonObject.put("code",0);
			jsonObject.put("msg", "succ");
			jsonObject.put("blacklists", blacklistsArray);
			jsonObject.put("num", expportList.size());
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
