package com.module.base.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
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

import com.bean.BaseConfig;
import com.common.comm.Constants;
import com.common.servlet.Base;
import com.common.tools.CBeanUtils;
import com.common.tools.StrUtil;
import com.common.type.BaseConfigState;
import com.common.web.HttpJson;
import com.common.web.Page;
import com.common.web.Pages;
import com.common.web.PaginatedListHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



public class BaseConfigServlet extends Base {


	private static final long serialVersionUID = 1L;


	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		BaseConfig reqBase=getReqBase(request);
		int page = StringUtils.isNotBlank(request.getParameter("pageCurrent"))?
				Integer.parseInt(request.getParameter("pageCurrent")):1;//当前页数
		int pageSize=StringUtils.isNotBlank(request.getParameter("pageSize"))?
				Integer.parseInt(request.getParameter("pageSize")):20;//当前页数
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		setExpList(expList,reqBase);
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
		PaginatedListHelper baseConfigPlh=getBaseService(BaseConfig.class).findList(expList, orders, pages, null);
		Page<Object> data = new Page<Object>(baseConfigPlh.getList(),
				pages.getPage(), pages.getPerPageNum(), pages.getAllPage(), pages.getTotalNum());
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();   
		this.sendWeb(gson.toJson(data), "application/json; charset=utf-8", request, response);
	}
	
	public void initpage(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		BaseConfig reqBase=getReqBase(request);
		String url="";
		if(StringUtils.isNotBlank(reqBase.getId())){
			BaseConfig baseConfig=(BaseConfig)  getBaseService(BaseConfig.class).findById(reqBase.getId());
			request.setAttribute("baseConfigForm", baseConfig);
			url="/html/module/base/config/modify.jsp";
		}else{
			url="/html/module/base/config/add.jsp";
		}
		request.setAttribute("statusList", BaseConfigState.values());
		request.getRequestDispatcher(url).forward(request, response);
	}
	
	public void add(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		BaseConfig reqBaseConfig=getReqBase(request);
		BaseConfig checkBaseConfig=(BaseConfig) getBaseService(BaseConfig.class).findByProperty("code", reqBaseConfig.getCode());
		HttpJson json=null;
		if(checkBaseConfig!=null){
			json = new HttpJson(HttpJson.STATE_FAIL, "编码重复", false,"datagrid-baseConfig-filter");
			this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
			return;
		}
		reqBaseConfig.setCreateuserid(getManageUser(request).getId());
		reqBaseConfig.setUpdateuserid(reqBaseConfig.getCreateuserid());
		reqBaseConfig.setCreatetime(new Date());
		reqBaseConfig.setUpdatetime(reqBaseConfig.getCreatetime());
		int state= getBaseService(BaseConfig.class).saveObject(reqBaseConfig);
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "", true,"datagrid-baseConfig-filter");
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"datagrid-baseConfig-filter");
		}
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
	}
	
	public void modify(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		BaseConfig reqBase=getReqBase(request);
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.eq("code", reqBase.getCode()));
		expList.add(Restrictions.ne("id", reqBase.getId()));
		PaginatedListHelper checkBaseConfigPlh=getBaseService(BaseConfig.class).findList(expList, orders, null, null);
		HttpJson json=null;
		if(checkBaseConfigPlh.getList()!=null
				&&checkBaseConfigPlh.getList().size()>0){
			json = new HttpJson(HttpJson.STATE_FAIL, "编码重复", false,"datagrid-baseConfig-filter");
			this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
			return;
		}
		BaseConfig base=(BaseConfig)  getBaseService(BaseConfig.class).findById(reqBase.getId());
		org.springframework.beans.BeanUtils.copyProperties(reqBase, base, StrUtil.getNullPropertyNames(reqBase));
		base.setUpdateuserid(getManageUser(request).getId());
		base.setUpdatetime(new Date());
		int state= getBaseService(BaseConfig.class).updateObject(base,true);
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "完成添加", true,"datagrid-baseConfig-filter");
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();   
			this.sendWeb(gson.toJson(base), "application/json; charset=utf-8", request, response);
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"datagrid-baseConfig-filter");
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
			BaseConfig baseConfig=(BaseConfig) getBaseService(BaseConfig.class).findById(id);
			state=getBaseService(BaseConfig.class).deleteObject(baseConfig);
		}
		HttpJson json=null;
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "完成添加", true,"datagrid-baseConfig-filter");
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"datagrid-baseConfig-filter");
		}
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
	}
	
	
	private void setExpList(List<Criterion> expList,BaseConfig reqBase){
		if(StringUtils.isNotBlank(reqBase.getName())){
			expList.add(Restrictions.like("name", reqBase.getName(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqBase.getCode())){
			expList.add(Restrictions.like("code", reqBase.getCode(),MatchMode.ANYWHERE));
		}
	}

	
	@SuppressWarnings("unchecked")
	private BaseConfig getReqBase(HttpServletRequest request){
		BaseConfig base = new BaseConfig(); 
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
			CBeanUtils.populate(base,newMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return base;
	}	
}
