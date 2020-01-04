package com.module.manage.servlet;

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


import com.bean.ManageUser;
import com.common.comm.Constants;
import com.common.servlet.Base;
import com.common.tools.CBeanUtils;
import com.common.tools.MD5Encrypt;
import com.common.tools.StrUtil;
import com.common.type.ManageUserState;
import com.common.web.HttpJson;
import com.common.web.Page;
import com.common.web.Pages;
import com.common.web.PaginatedListHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;




public class UserServlet extends Base {

	private static final long serialVersionUID = 1L;


	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ManageUser reqUser=getReqUser(request);
		int page = StringUtils.isNotBlank(request.getParameter("pageCurrent"))?
				Integer.parseInt(request.getParameter("pageCurrent")):1;//当前页数
		int pageSize=StringUtils.isNotBlank(request.getParameter("pageSize"))?
				Integer.parseInt(request.getParameter("pageSize")):20;//当前页数
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		setExpList(expList,reqUser);
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
		PaginatedListHelper manageUserPlh=getBaseService(ManageUser.class).findList(expList, orders, pages, null);
		Page<Object> data = new Page<Object>(manageUserPlh.getList(),
				pages.getPage(), pages.getPerPageNum(), pages.getAllPage(), pages.getTotalNum());
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();   
		this.sendWeb(gson.toJson(data), "application/json; charset=utf-8", request, response);
	}
	
	public void initpage(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		ManageUser reqUser=getReqUser(request);
		String url="";
		if(StringUtils.isNotBlank(reqUser.getId())){
			ManageUser manageUser=(ManageUser) getBaseService(ManageUser.class).findById(reqUser.getId());
			request.setAttribute("manageUserForm", manageUser);
			url="/html/module/manage/user/modify.jsp";
		}else{
			url="/html/module/manage/user/add.jsp";
		}
		request.setAttribute("statusList", ManageUserState.values());
		request.getRequestDispatcher(url).forward(request, response);
	}
	
	public void add(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		ManageUser reqUser=getReqUser(request);
		ManageUser checkManageUser=(ManageUser) getBaseService(ManageUser.class).findByProperty("username", reqUser.getUsername());
		HttpJson json=null;
		if(checkManageUser!=null){
			json = new HttpJson(HttpJson.STATE_FAIL, "登录名已经存在", false,"datagrid-manageUser-filter");
			this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
			return;
		}
		reqUser.setPasswd(MD5Encrypt.MD5(reqUser.getPasswd()));
		reqUser.setCreateuserid(getManageUser(request).getId());
		reqUser.setUpdateuserid(reqUser.getCreateuserid());
		reqUser.setCreatetime(new Date());
		reqUser.setUpdatetime(reqUser.getCreatetime());
		int state=getBaseService(ManageUser.class).saveObject(reqUser);
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "", true,"datagrid-manageUser-filter");
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"datagrid-manageUser-filter");
		}
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
	}
	
	public void modify(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		ManageUser reqUser=getReqUser(request);
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.eq("username", reqUser.getUsername()));
		expList.add(Restrictions.ne("id", reqUser.getId()));
		PaginatedListHelper checkManageUserPlh=getBaseService(ManageUser.class).findList(expList, orders, null, null);
		HttpJson json=null;
		if(checkManageUserPlh.getList()!=null
				&&checkManageUserPlh.getList().size()>0){
			json = new HttpJson(HttpJson.STATE_FAIL, "编码重复", false,"datagrid-manageUser-filter");
			this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
			return;
		}
		ManageUser user=(ManageUser) getBaseService(ManageUser.class).findById(reqUser.getId());
		org.springframework.beans.BeanUtils.copyProperties(reqUser, user, StrUtil.getNullPropertyNames(reqUser));
		user.setUpdatetime(new Date());
		user.setUpdateuserid(getManageUser(request).getId());
		int state=getBaseService(ManageUser.class).updateObject(user,true);
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "", true,"datagrid-manageUser-filter");
			this.sendWeb(new Gson().toJson(user), "application/json; charset=utf-8", request, response);
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"datagrid-manageUser-filter");
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
			ManageUser manageUser=(ManageUser) getBaseService(ManageUser.class).findById(id);
			manageUser.setUpdatetime(new Date());
			manageUser.setUpdateuserid(getManageUser(request).getId());
			manageUser.setState(ManageUserState.delete.getCode());
			state=getBaseService(ManageUser.class).updateObject(manageUser,true);
		}
		HttpJson json=null;
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "完成添加", true,"datagrid-manageUser-filter");
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"datagrid-manageUser-filter");
		}
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
	}
	
	
	private void setExpList(List<Criterion> expList,ManageUser reqManageUser){
		if(StringUtils.isNotBlank(reqManageUser.getUsername())){
			expList.add(Restrictions.like("username", reqManageUser.getUsername(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqManageUser.getState())){
			expList.add(Restrictions.eq("state", reqManageUser.getState()));
		}else{
			expList.add(Restrictions.ne("state", ManageUserState.delete.getCode()));
		}
	}

	
	@SuppressWarnings("unchecked")
	private ManageUser getReqUser(HttpServletRequest request){
		ManageUser manageUser = new ManageUser(); 
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
			CBeanUtils.populate(manageUser,newMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return manageUser;
	}	
}
