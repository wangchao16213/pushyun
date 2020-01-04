package com.module.business.servlet;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;



import com.bean.BusinessPushserver;
import com.common.comm.Constants;
import com.common.servlet.Base;
import com.common.tools.CBeanUtils;
import com.common.tools.DateUtil;
import com.common.tools.MD5Encrypt;
import com.common.tools.StrUtil;
import com.common.type.BusinessPushserverState;
import com.common.web.HttpJson;
import com.common.web.Page;
import com.common.web.Pages;
import com.common.web.PaginatedListHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



public class PushserverServlet extends Base {


	private static final long serialVersionUID = 1L;


	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		BusinessPushserver reqPushserver=getReqPushserver(request);
		int page = StringUtils.isNotBlank(request.getParameter("pageCurrent"))?
				Integer.parseInt(request.getParameter("pageCurrent")):1;//当前页数
		int pageSize=StringUtils.isNotBlank(request.getParameter("pageSize"))?
				Integer.parseInt(request.getParameter("pageSize")):20;//当前页数
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		setExpList(expList,reqPushserver);
		String reqOrder = request.getParameter("orders");
		if (StringUtils.isBlank(reqOrder)) {
			orders.add(Order.desc("updatetime"));
		} else {
			String[] orderStrs = reqOrder.split(",");
			for (String order : orderStrs) {
				if (order.indexOf("asc") != -1) {
					orders.add(Order.asc(order.replaceAll("asc", "").trim()));
				} else if (order.indexOf("desc") != -1) {
					orders.add(Order.desc(order.replaceAll("desc", "").trim()));
				}
			}
		}
		Pages pages = new Pages();
		pages.setPage(page);
		pages.setPerPageNum(pageSize);
		PaginatedListHelper businessPushserverPlh=getBaseService(BusinessPushserver.class).findList(expList, orders, pages, null);
		Page<Object> data = new Page<Object>(businessPushserverPlh.getList(),
				pages.getPage(), pages.getPerPageNum(), pages.getAllPage(), pages.getTotalNum());
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();   
		this.sendWeb(gson.toJson(data), "application/json; charset=utf-8", request, response);
	}
	
	public void initpage(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		BusinessPushserver reqPushserver=getReqPushserver(request);
		String url="";
		if(StringUtils.isNotBlank(reqPushserver.getId())){
			BusinessPushserver BusinessPushserver=(BusinessPushserver) getBaseService(BusinessPushserver.class).findById(reqPushserver.getId());
			request.setAttribute("businessPushserverForm", BusinessPushserver);
			url="/html/module/business/pushserver/modify.jsp";
		}else{
			url="/html/module/business/pushserver/add.jsp";
		}
		request.setAttribute("statusList", BusinessPushserverState.values());
		request.getRequestDispatcher(url).forward(request, response);
	}
	
	public void add(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		BusinessPushserver reqPushserver=getReqPushserver(request);
		BusinessPushserver checkBusinessPushserver=(BusinessPushserver) getBaseService(BusinessPushserver.class).findByProperty("code", reqPushserver.getCode());
		HttpJson json=null;
		if(checkBusinessPushserver!=null){
			json = new HttpJson(HttpJson.STATE_FAIL, "编码重复", false,"datagrid-businessPushserver-filter");
			this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
			return;
		}
		reqPushserver.setCreateuserid(getManageUser(request).getId());
		reqPushserver.setUpdateuserid(reqPushserver.getCreateuserid());
		reqPushserver.setCreatetime(new Date());
		reqPushserver.setUpdatetime(reqPushserver.getCreatetime());
		int state=getBaseService(BusinessPushserver.class).saveObject(reqPushserver);
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "", true,"datagrid-businessPushserver-filter");
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"");
		}
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
	}
	
	public void modify(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		BusinessPushserver reqPushserver=getReqPushserver(request);
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.eq("code", reqPushserver.getCode()));
		expList.add(Restrictions.ne("id", reqPushserver.getId()));
		PaginatedListHelper checkBusinessPushserverPlh=getBaseService(BusinessPushserver.class).findList(expList, orders, null, null);
		HttpJson json=null;
		int state=Constants.STATE_OPERATOR_LOST;
		if(checkBusinessPushserverPlh.getList()!=null
				&&checkBusinessPushserverPlh.getList().size()>0){
			json = new HttpJson(HttpJson.STATE_FAIL, "编码重复", false,"datagrid-businessPushserver-filter");
			this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
			return;
		}
		BusinessPushserver pushserver=(BusinessPushserver) getBaseService(BusinessPushserver.class).findById(reqPushserver.getId());
		org.springframework.beans.BeanUtils.copyProperties(reqPushserver, pushserver, StrUtil.getNullPropertyNames(reqPushserver));
		pushserver.setUpdateuserid(getManageUser(request).getId());
		pushserver.setUpdatetime(new Date());
		state= getBaseService(BusinessPushserver.class).updateObject(pushserver,true);
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "", true,"datagrid-businessPushserver-filter");
			this.sendWeb(new Gson().toJson(pushserver), "application/json; charset=utf-8", request, response);
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"datagrid-businessPushserver-filter");
			this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
		}
	}
	
	public void del(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		String ids=request.getParameter("id");
		if(StringUtils.isBlank(ids)){
			return;
		}
		int state=Constants.STATE_OPERATOR_LOST;
		for(String id:ids.split(",")){
			if(StringUtils.isBlank(ids)){
				continue;
			}
			BusinessPushserver BusinessPushserver=(BusinessPushserver) getBaseService(BusinessPushserver.class).findById(id);
			state= getBaseService(BusinessPushserver.class).deleteObject(BusinessPushserver);
		}
		HttpJson json=null;
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "", true,"datagrid-businessPushserver-filter");
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"datagrid-businessPushserver-filter");
		}
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
	}
	
	
	
	private void setExpList(List<Criterion> expList,BusinessPushserver reqPushserver){
		if(StringUtils.isNotBlank(reqPushserver.getName())){
			expList.add(Restrictions.like("name", reqPushserver.getName(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqPushserver.getCode())){
			expList.add(Restrictions.like("code", reqPushserver.getCode(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqPushserver.getState())){
			expList.add(Restrictions.eq("state", reqPushserver.getState()));
		}
	}

	
	@SuppressWarnings("unchecked")
	private BusinessPushserver getReqPushserver(HttpServletRequest request){
		BusinessPushserver pushserver = new BusinessPushserver(); 
		Map newMap=new HashMap();
		newMap.putAll(request.getParameterMap());  
		Iterator<Map.Entry<Object, Object>> it = newMap.entrySet().iterator();  
        while(it.hasNext()){  
            Map.Entry<Object, Object> entry=it.next();  
            Object o=entry.getKey();  
            if(o instanceof String){
				if(o.toString().indexOf("operator")!=-1){
					it.remove();
				}
				if(o.toString().indexOf("[")!=-1){
					it.remove();
				}
			}  
        }  
		try {
			CBeanUtils.populate(pushserver,newMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pushserver;
	}

}
