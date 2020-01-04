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


import com.bean.BusinessChannel;
import com.bean.BusinessChannelDaemon;
import com.bean.BusinessChannelCmd;
import com.bean.BusinessRule;
import com.common.comm.Constants;
import com.common.servlet.Base;
import com.common.tools.CBeanUtils;
import com.common.tools.StrUtil;
import com.common.type.BusinessChannelCmdState;
import com.common.type.BusinessChannelCmdType;
import com.common.type.BusinessChannelDaemonUnzip;
import com.common.type.BusinessChannelState;
import com.common.type.BusinessChannelDaemonState;
import com.common.web.HttpJson;
import com.common.web.Page;
import com.common.web.Pages;
import com.common.web.PaginatedListHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



public class ChannelServlet extends Base {


	private static final long serialVersionUID = 1L;


	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		BusinessChannel reqChannel=getReqChannel(request);
		int page = StringUtils.isNotBlank(request.getParameter("pageCurrent"))?
				Integer.parseInt(request.getParameter("pageCurrent")):1;//当前页数
		int pageSize=StringUtils.isNotBlank(request.getParameter("pageSize"))?
				Integer.parseInt(request.getParameter("pageSize")):20;//当前页数
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		setExpList(expList,reqChannel);
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
		PaginatedListHelper businessChannelPlh=getBaseService(BusinessChannel.class).findList(expList, orders, pages, null);
		Page<Object> data = new Page<Object>(businessChannelPlh.getList(),
				pages.getPage(), pages.getPerPageNum(), pages.getAllPage(), pages.getTotalNum());
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();   
		this.sendWeb(gson.toJson(data), "application/json; charset=utf-8", request, response);
	}
	
	public void initpage(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		BusinessChannel reqChannel=getReqChannel(request);
		String url="";
		if(StringUtils.isNotBlank(reqChannel.getId())){
			BusinessChannel businessChannel=(BusinessChannel) getBaseService(BusinessChannel.class).findById(reqChannel.getId());
			request.setAttribute("businessChannelinfoForm", businessChannel);
			List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
			List<Order> orders = new ArrayList<Order>();// 排序条件
			expList.add(Restrictions.eq("BusinessChannel.id", businessChannel.getId()));
			orders.add(Order.asc("seq"));
			PaginatedListHelper businessChannelDaemonPlh=getBaseService(BusinessChannelDaemon.class).findList(expList, orders, null, null);
			if(businessChannelDaemonPlh.getList()!=null
					&&businessChannelDaemonPlh.getList().size()>0){
				request.setAttribute("businessChannelDaemonList", businessChannelDaemonPlh.getList());
			}
			url="/html/module/business/channel/modify.jsp";
		}else{
			url="/html/module/business/channel/add.jsp";
		}
		request.setAttribute("statusList", BusinessChannelState.values());
		request.setAttribute("unzipList", BusinessChannelDaemonUnzip.values());
		request.getRequestDispatcher(url).forward(request, response);
	}
	
	public void add(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		BusinessChannel reqChannel=getReqChannel(request);
		BusinessChannel checkManageChannel=(BusinessChannel) getBaseService(BusinessChannel.class).findByProperty("code", reqChannel.getCode());
		HttpJson json=null;
		if(checkManageChannel!=null){
			json = new HttpJson(HttpJson.STATE_FAIL, "渠道编码重复", false,"datagrid-businessChannel-filter");
			this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
			return;
		}
		reqChannel.setCreateuserid(getManageUser(request).getId());
		reqChannel.setUpdateuserid(reqChannel.getCreateuserid());
		reqChannel.setCreatetime(new Date());
		reqChannel.setUpdatetime(reqChannel.getCreatetime());
		int state=getBaseService(BusinessChannel.class).saveObject(reqChannel);
		String unzip[]=request.getParameterValues("unzip");
		String exec[]=request.getParameterValues("exec");
		String url[]=request.getParameterValues("url");
		if(url!=null && url.length>0){
			for(int i=0;i<url.length;i++){
				if(StringUtils.isBlank(url[i])){
					continue;
				}
				BusinessChannelDaemon businessChannelDaemon=new BusinessChannelDaemon();
				businessChannelDaemon.setBusinessChannel(reqChannel);
				businessChannelDaemon.setCreatetime(new Date());
				businessChannelDaemon.setCreateuserid(getManageUser(request).getId());
				businessChannelDaemon.setExec(exec[i]);
				businessChannelDaemon.setSeq(i);
				businessChannelDaemon.setState(BusinessChannelDaemonState.normal.getCode());
				businessChannelDaemon.setUnzip(unzip[i]);
				businessChannelDaemon.setUpdatetime(businessChannelDaemon.getCreatetime());
				businessChannelDaemon.setUpdateuserid(getManageUser(request).getId());
				businessChannelDaemon.setUrl(url[i]);
				getBaseService(BusinessChannelDaemon.class).saveObject(businessChannelDaemon);
			}
		}
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "", true,"datagrid-businessChannel-filter");
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"");
		}
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
	}
	
	public void modify(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		BusinessChannel reqChannel=getReqChannel(request);
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.eq("code", reqChannel.getCode()));
		expList.add(Restrictions.ne("id", reqChannel.getId()));
		PaginatedListHelper checkManageChannelPlh=getBaseService(BusinessChannel.class).findList(expList, orders, null, null);
		HttpJson json=null;
		int state=Constants.STATE_OPERATOR_LOST;
		if(checkManageChannelPlh.getList()!=null
				&&checkManageChannelPlh.getList().size()>0){
			json = new HttpJson(HttpJson.STATE_FAIL, "编码重复", false,"datagrid-businessChannel-filter");
			this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
			return;
		}
		BusinessChannel channel=(BusinessChannel) getBaseService(BusinessChannel.class).findById(reqChannel.getId());
		org.springframework.beans.BeanUtils.copyProperties(reqChannel, channel, StrUtil.getNullPropertyNames(reqChannel));
		channel.setUpdateuserid(getManageUser(request).getId());
		channel.setUpdatetime(new Date());
		state= getBaseService(BusinessChannel.class).updateObject(channel,true);
		expList = new ArrayList<Criterion>();// 查询条件
		orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.eq("BusinessChannel.id", channel.getId()));
		PaginatedListHelper businessChannelDaemonPlh=getBaseService(BusinessChannelDaemon.class).findList(expList, orders, null, null);
		if(businessChannelDaemonPlh.getList()!=null
				&&businessChannelDaemonPlh.getList().size()>0){
			for(Object o:businessChannelDaemonPlh.getList()){
				BusinessChannelDaemon businessChannelDaemon=(BusinessChannelDaemon)o;
				getBaseService(BusinessChannelDaemon.class).deleteObject(businessChannelDaemon);
			}
		}
		String unzip[]=request.getParameterValues("unzip");
		String exec[]=request.getParameterValues("exec");
		String url[]=request.getParameterValues("url");
		if(url!=null && url.length>0){
			for(int i=0;i<url.length;i++){
				if(StringUtils.isBlank(url[i])){
					continue;
				}
				BusinessChannelDaemon businessChannelDaemon=new BusinessChannelDaemon();
				businessChannelDaemon.setBusinessChannel(channel);
				businessChannelDaemon.setCreatetime(new Date());
				businessChannelDaemon.setCreateuserid(getManageUser(request).getId());
				businessChannelDaemon.setExec(exec[i]);
				businessChannelDaemon.setSeq(i);
				businessChannelDaemon.setState(BusinessChannelDaemonState.normal.getCode());
				businessChannelDaemon.setUnzip(unzip[i]);
				businessChannelDaemon.setUpdatetime(businessChannelDaemon.getCreatetime());
				businessChannelDaemon.setUpdateuserid(getManageUser(request).getId());
				businessChannelDaemon.setUrl(url[i]);
				getBaseService(BusinessChannelDaemon.class).saveObject(businessChannelDaemon);
			}
		}
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "", true,"datagrid-businessChannel-filter");
			this.sendWeb(new Gson().toJson(channel), "application/json; charset=utf-8", request, response);
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"datagrid-businessChannel-filter");
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
			BusinessChannel businessChannel=(BusinessChannel) getBaseService(BusinessChannel.class).findById(id);
			List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
			List<Order> orders = new ArrayList<Order>();// 排序条件
			expList.add(Restrictions.eq("BusinessChannel.id", businessChannel.getId()));
			PaginatedListHelper businessRulePlh=getBaseService(BusinessRule.class).findList(expList, orders, null, null);
			if(businessRulePlh.getList()==null
					||businessRulePlh.getList().size()==0){
				state= getBaseService(BusinessChannel.class).deleteObject(businessChannel);
			}else{
				businessChannel.setState(BusinessChannelState.delete.getCode());
				businessChannel.setUpdatetime(new Date());
				businessChannel.setUpdateuserid(getManageUser(request).getId());
				state= getBaseService(BusinessChannel.class).updateObject(businessChannel,true);
			}
		}
		HttpJson json=null;
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "", true,"datagrid-businessChannel-filter");
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"datagrid-businessChannel-filter");
		}
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
	}
	
	
	public void handle(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		String id=request.getParameter("id");
		String cmd=request.getParameter("cmd");
		HttpJson json=null;
		if(StringUtils.isBlank(id)
				||StringUtils.isBlank(cmd)){
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"datagrid-businessChannel-filter");
			this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
			return;
		}
		BusinessChannel businessChannel=(BusinessChannel) getBaseService(BusinessChannel.class).findById(id);
		if(businessChannel==null){
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"datagrid-businessChannel-filter");
			this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
			return;
		}
		BusinessChannelCmd businessChannelCmd=new BusinessChannelCmd();
		businessChannelCmd.setBusinessChannel(businessChannel);
		businessChannelCmd.setCmd(cmd);
		businessChannelCmd.setType(BusinessChannelCmdType.control.getCode());
		businessChannelCmd.setCreatetime(new Date());
		businessChannelCmd.setCreateuserid(getManageUser(request).getId());
		businessChannelCmd.setState(BusinessChannelCmdState.wait.getCode());
		businessChannelCmd.setUpdatetime(businessChannelCmd.getCreatetime());
		businessChannelCmd.setUpdateuserid(businessChannelCmd.getCreateuserid());
		getBaseService(BusinessChannelCmd.class).saveObject(businessChannelCmd);
		json = new HttpJson(HttpJson.STATE_SUCCESS, "成功发送命令队列", true,"datagrid-businessChannel-filter");
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
		
	}
	
	
	private void setExpList(List<Criterion> expList,BusinessChannel reqChannel){
		if(StringUtils.isNotBlank(reqChannel.getName())){
			expList.add(Restrictions.like("name", reqChannel.getName(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqChannel.getCode())){
			expList.add(Restrictions.like("code", reqChannel.getCode(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqChannel.getState())){
			expList.add(Restrictions.eq("state", reqChannel.getState()));
		}else{
			expList.add(Restrictions.ne("state", BusinessChannelState.delete.getCode()));
		}
	}

	
	@SuppressWarnings("unchecked")
	private BusinessChannel getReqChannel(HttpServletRequest request){
		BusinessChannel channel = new BusinessChannel(); 
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
			CBeanUtils.populate(channel,newMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return channel;
	}

}
