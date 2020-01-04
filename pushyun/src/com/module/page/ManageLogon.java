package com.module.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.bean.BusinessChannel;
import com.bean.ManageMenu;
import com.bean.ManageUser;
import com.common.comm.Constants;
import com.common.comm.UserSession;
import com.common.servlet.Base;
import com.common.tools.MD5Encrypt;
import com.common.tools.WebUtil;
import com.common.type.BusinessChannelState;
import com.common.web.HttpJson;
import com.common.web.PaginatedListHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



public class ManageLogon extends Base {

	private static final long serialVersionUID = 1L;

	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username=request.getParameter("username");
		String password=request.getParameter("password");
		HttpJson json = null;
		if(StringUtils.isBlank(username)
				||StringUtils.isBlank(password)){
			json = new HttpJson(HttpJson.STATE_FAIL, "用户或密码不能为空", true,"");
			this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
			return;
		}
		ManageUser manageUser=(ManageUser) getBaseService(ManageUser.class).findByProperty("username", username);
		if(manageUser==null){
			json = new HttpJson(HttpJson.STATE_FAIL, "用户名不存在", true,"");
		}else if(MD5Encrypt.MD5(password).equals(manageUser.getPasswd())){
			HttpSession session =request.getSession();
			UserSession userSession=new UserSession();
			userSession.setUsers(manageUser);
			session.setAttribute(Constants.SESSION_USER_CODE, userSession);
			String logonurl="html/page/manager.jsp";
			Cookie visitorCookie=new Cookie(Constants.SESSION_USER_URL,logonurl);  
			visitorCookie.setMaxAge(30*24*60*60); 
			visitorCookie.setPath("/");
			response.addCookie(visitorCookie);  
			json = new HttpJson(HttpJson.STATE_SUCCESS, "html/module/index.jsp",true,"");
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "用户密码错误", true,"");
		}
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
	}
	
	public void modify(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		String id=request.getParameter("id");
		ManageUser manageUser=(ManageUser) getBaseService(ManageUser.class).findById(id);
		String password=request.getParameter("password");
		HttpJson json = null;
		if(StringUtils.isBlank(password)){
			json = new HttpJson(HttpJson.STATE_FAIL, "密码不能为空", true,"");
			this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
			return;
		}
		if(!MD5Encrypt.MD5(password).equals(manageUser.getPasswd())){
			json = new HttpJson(HttpJson.STATE_FAIL, "密码错误", true,"");
		}else{
			String newpassword=request.getParameter("newpassword");
			manageUser.setPasswd(MD5Encrypt.MD5(newpassword));
			manageUser.setUpdatetime(new Date());
			manageUser.setUpdateuserid(getManageUser(request).getId());
			getBaseService(ManageUser.class).updateObject(manageUser,true);
			json = new HttpJson(HttpJson.STATE_SUCCESS, "修改成功", true,"");
		}
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
	}
	
	
	public void menulist(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session =request.getSession();
		Object object=session.getAttribute(Constants.SESSION_USER_CODE);
		if(object ==null){
			return;
		}
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		orders.add(Order.asc("code"));
		orders.add(Order.asc("seq"));
		PaginatedListHelper  parentlyqMenuPlh=getBaseService(ManageMenu.class).findList(expList,orders, null, null);
		if(parentlyqMenuPlh.getList()==null
				||parentlyqMenuPlh.getList().size()==0){
			return;
		}
		List<ManageMenu> parentlyqMenuList=new ArrayList<ManageMenu>();
		for(Object o:parentlyqMenuPlh.getList()){
			ManageMenu  parentManageMenu=(ManageMenu)o;
			if(StringUtils.isBlank(parentManageMenu.getParentid())){
				parentlyqMenuList.add(parentManageMenu);
			}
		}
		List resultlist=new LinkedList();
		for(ManageMenu parentManageMenu:parentlyqMenuList){
			Hashtable hash=new Hashtable();
			hash.put("name", parentManageMenu.getName());
			if(parentManageMenu.getCode().equals("100")){
				expList = new ArrayList<Criterion>();// 查询条件
				orders = new ArrayList<Order>();// 排序条件
				expList.add(Restrictions.eq("state", BusinessChannelState.normal.getCode()));
				orders.add(Order.asc("name"));
				orders.add(Order.asc("createtime"));
				PaginatedListHelper businessChannelPlh=getBaseService(BusinessChannel.class).findList(expList,orders, null, null);
				if(businessChannelPlh.getList()==null
						||businessChannelPlh.getList().size()==0){
					continue;
				}
				List children=new LinkedList();
				for(Object obj:businessChannelPlh.getList()){
					BusinessChannel businessChannel=(BusinessChannel)obj;
					Hashtable childhash=new Hashtable();
					childhash.put("id", "channel"+businessChannel.getId());
					childhash.put("name", businessChannel.getName()+"规则管理");
					childhash.put("target", "navtab");
					childhash.put("url", "html/module/business/rule/list.jsp?businessChannel.id="+businessChannel.getId());
					children.add(childhash);
				}
				hash.put("children", children);
				resultlist.add(hash);
			}else if(parentManageMenu.getCode().equals("500")){
				expList = new ArrayList<Criterion>();// 查询条件
				orders = new ArrayList<Order>();// 排序条件
				expList.add(Restrictions.eq("state", BusinessChannelState.normal.getCode()));
				orders.add(Order.asc("name"));
				orders.add(Order.asc("createtime"));
				PaginatedListHelper businessChannelPlh=getBaseService(BusinessChannel.class).findList(expList,orders, null, null);
				if(businessChannelPlh.getList()==null
						||businessChannelPlh.getList().size()==0){
					continue;
				}
				List children=new LinkedList();
				for(Object obj:businessChannelPlh.getList()){
					BusinessChannel businessChannel=(BusinessChannel)obj;
					Hashtable childhash=new Hashtable();
					childhash.put("id", "channel"+businessChannel.getId());
					childhash.put("name", businessChannel.getName()+"DNS管理");
					childhash.put("target", "navtab");
					childhash.put("url", "html/module/business/dns/list.jsp?businessChannel.id="+businessChannel.getId());
					children.add(childhash);
				}
				hash.put("children", children);
				resultlist.add(hash);
			}else if(parentManageMenu.getCode().equals("900")){
				expList = new ArrayList<Criterion>();// 查询条件
				orders = new ArrayList<Order>();// 排序条件
				expList.add(Restrictions.eq("state", BusinessChannelState.normal.getCode()));
				orders.add(Order.asc("name"));
				orders.add(Order.asc("createtime"));
				PaginatedListHelper businessChannelPlh=getBaseService(BusinessChannel.class).findList(expList,orders, null, null);
				if(businessChannelPlh.getList()==null
						||businessChannelPlh.getList().size()==0){
					continue;
				}
				List children=new LinkedList();
				for(Object obj:businessChannelPlh.getList()){
					BusinessChannel businessChannel=(BusinessChannel)obj;
					Hashtable childhash=new Hashtable();
					childhash.put("id", "channel"+businessChannel.getId());
					childhash.put("name", businessChannel.getName()+"白名单管理");
					childhash.put("target", "navtab");
					childhash.put("url", "html/module/business/whitelist/list.jsp?businessChannel.id="+businessChannel.getId());
					children.add(childhash);
				}
				hash.put("children", children);
				resultlist.add(hash);
			}else{
				expList = new ArrayList<Criterion>();// 查询条件
				orders = new ArrayList<Order>();// 排序条件
				expList.add(Restrictions.eq("parentid", parentManageMenu.getId()));
				orders.add(Order.asc("seq"));
				PaginatedListHelper manageMenuPlh=getBaseService(ManageMenu.class).findList(expList,orders, null, null);
				if(manageMenuPlh.getList()==null
						||manageMenuPlh.getList().size()==0){
					continue;
				}
				List children=new LinkedList();
				for(Object obj:manageMenuPlh.getList()){
					ManageMenu manageMenu=(ManageMenu)obj;
					Hashtable childhash=new Hashtable();
					childhash.put("id", "menu"+manageMenu.getId());
					childhash.put("name", manageMenu.getName());
					childhash.put("target", "navtab");
					childhash.put("url", manageMenu.getUrl());
					children.add(childhash);
				}
				hash.put("children", children);
				resultlist.add(hash);
			}
		}
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		gson.toJson(resultlist);
		this.sendWeb(gson.toJson(resultlist), "application/json; charset=utf-8",request, response);
	}

	public void out(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		HttpSession session =request.getSession();
		session.removeAttribute(Constants.SESSION_USER_CODE);
		Cookie [] pageCookies=request.getCookies();
		String url="html/page/manager.jsp";
		if(pageCookies!=null
				&&pageCookies.length>0){
			for(Cookie cookie:pageCookies){
				if(Constants.SESSION_USER_URL.equals(cookie.getName())){	
					url=cookie.getValue();
					break;
				}
			}
		}
		response.sendRedirect(WebUtil.getServerUrl(request)+"/"+url); 
		return;
	}
	public void keepsession(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		this.sendWeb("", "application/json; charset=utf-8",request, response);
	}
}
