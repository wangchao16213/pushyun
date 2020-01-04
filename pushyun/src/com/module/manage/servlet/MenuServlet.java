package com.module.manage.servlet;

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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.bean.ManageMenu;
import com.common.comm.Constants;
import com.common.servlet.Base;
import com.common.tools.CBeanUtils;
import com.common.tools.StrUtil;
import com.common.type.ManageMenuState;
import com.common.web.HttpJson;
import com.common.web.Page;
import com.common.web.Pages;
import com.common.web.PaginatedListHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



public class MenuServlet extends Base {

	private static final long serialVersionUID = 1L;


	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ManageMenu reqMenu=getReqMenu(request);
		int page = StringUtils.isNotBlank(request.getParameter("pageCurrent"))?
				Integer.parseInt(request.getParameter("pageCurrent")):1;//当前页数
		int pageSize=StringUtils.isNotBlank(request.getParameter("pageSize"))?
				Integer.parseInt(request.getParameter("pageSize")):20;//当前页数
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		setExpList(expList, reqMenu);
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
		PaginatedListHelper manageMenuPlh=getBaseService(ManageMenu.class).findList(expList, orders, pages, null);
		Page<Object> data = new Page<Object>(manageMenuPlh.getList(),
				pages.getPage(), pages.getPerPageNum(), pages.getAllPage(), pages.getTotalNum());
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();   
		this.sendWeb(gson.toJson(data), "application/json; charset=utf-8", request, response);
	}
	
	public void initpage(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		ManageMenu reqMenu=getReqMenu(request);
		String url="";
		if(StringUtils.isNotBlank(reqMenu.getId())){
			ManageMenu menu=(ManageMenu) getBaseService(ManageMenu.class).findById(reqMenu.getId());
			request.setAttribute("manageMenuForm", menu);
			url="/html/module/manage/menu/modify.jsp";
		}else{
			url="/html/module/manage/menu/add.jsp";
		}
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件

		PaginatedListHelper parentManageMenuPlh=getBaseService(ManageMenu.class).findList(expList, orders, null, null);
		List<ManageMenu> parentManageMenuList=new ArrayList<ManageMenu>();
		for(Object o:parentManageMenuPlh.getList()){
			ManageMenu manageMenu=(ManageMenu)o;
			if(StringUtils.isBlank(manageMenu.getParentid())){
				parentManageMenuList.add(manageMenu);
			}
		}
		request.setAttribute("parentManageMenuList", parentManageMenuList);
		request.setAttribute("statusList", ManageMenuState.values());
		request.getRequestDispatcher(url).forward(request, response);
	}
	
	public void add(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		ManageMenu reqMenu=getReqMenu(request);
		reqMenu.setCreateuserid(getManageUser(request).getId());
		reqMenu.setUpdateuserid(reqMenu.getCreateuserid());
		reqMenu.setCreatetime(new Date());
		reqMenu.setUpdatetime(reqMenu.getCreatetime());
		int state=getBaseService(ManageMenu.class).saveObject(reqMenu);
		HttpJson json=null;
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "", true,"datagrid-manageMenu-filter");
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"datagrid-manageMenu-filter");
		}
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
	}
	
	public void modify(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		ManageMenu reqMenu=getReqMenu(request);
		ManageMenu menu=(ManageMenu) getBaseService(ManageMenu.class).findById(reqMenu.getId());
		org.springframework.beans.BeanUtils.copyProperties(reqMenu, menu, StrUtil.getNullPropertyNames(reqMenu));
		menu.setUpdatetime(new Date());
		menu.setUpdateuserid(getManageUser(request).getId());
		int state=getBaseService(ManageMenu.class).updateObject(menu,true);
		HttpJson json=null;
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "", true,"datagrid-manageMenu-filter");
			this.sendWeb(new Gson().toJson(menu), "application/json; charset=utf-8", request, response);
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"datagrid-manageMenu-filter");
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
			ManageMenu manageMenu=(ManageMenu) getBaseService(ManageMenu.class).findById(id);
			state=getBaseService(ManageMenu.class).deleteObject(manageMenu);
		}
		HttpJson json=null;
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "", true,"datagrid-manageMenu-filter");
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"datagrid-manageMenu-filter");
		}
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
	}
	
	
	private void setExpList(List<Criterion> expList,ManageMenu ManageMenu){
		if(StringUtils.isNotBlank(ManageMenu.getName())){
			expList.add(Restrictions.like("name", ManageMenu.getName(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(ManageMenu.getCode())){
			expList.add(Restrictions.like("code", ManageMenu.getCode(),MatchMode.ANYWHERE));
		}
	}

	
	@SuppressWarnings("unchecked")
	private ManageMenu getReqMenu(HttpServletRequest request){
		ManageMenu menu = new ManageMenu(); 
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
			CBeanUtils.populate(menu,newMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return menu;
	}	
}
