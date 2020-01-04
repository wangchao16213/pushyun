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
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.bean.BusinessChannel;
import com.bean.BusinessTask;
import com.common.comm.Constants;
import com.common.servlet.Base;
import com.common.tools.CBeanUtils;
import com.common.tools.DateUtil;
import com.common.tools.StrUtil;
import com.common.tools.WebUtil;
import com.common.type.BusinessTaskState;
import com.common.type.BusinessTaskType;
import com.common.web.HttpJson;
import com.common.web.Page;
import com.common.web.Pages;
import com.common.web.PaginatedListHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class TaskServlet extends Base{

	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		BusinessTask reqTask=getReqTask(request);
		int page = StringUtils.isNotBlank(request.getParameter("pageCurrent"))?
				Integer.parseInt(request.getParameter("pageCurrent")):1;//当前页数
		int pageSize=StringUtils.isNotBlank(request.getParameter("pageSize"))?
				Integer.parseInt(request.getParameter("pageSize")):20;//当前页数
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		DetachedCriteria dc=DetachedCriteria.forClass(BusinessTask.class);
		setExpList(expList,reqTask,request,dc);
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
		PaginatedListHelper businessTaskPlh= getBaseService(BusinessTask.class).findList(expList, orders, pages, dc);
		Page<Object> data = new Page<Object>(businessTaskPlh.getList(),
				pages.getPage(), pages.getPerPageNum(), pages.getAllPage(), pages.getTotalNum());
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();   
		
		this.sendWeb(gson.toJson(data), "application/json; charset=utf-8", request, response);
	}
	
	public void initpage(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		BusinessTask reqTask=getReqTask(request);
		String url="";
		if(StringUtils.isNotBlank(reqTask.getId())){
			BusinessTask businessTask=(BusinessTask) getBaseService(BusinessTask.class).findById(reqTask.getId());
			request.setAttribute("businessTaskForm", businessTask);
			BusinessChannel businessChannel=(BusinessChannel) getBaseService(BusinessChannel.class).findById(businessTask.getBusinessChannel().getId());
			request.setAttribute("businessChannelForm", businessChannel);
			url="/html/module/business/task/modify.jsp";
		}
		request.setAttribute("statusList", BusinessTaskState.values());
		request.getRequestDispatcher(url).forward(request, response);
	}
	public void modify(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		BusinessTask reqTask=getReqTask(request);
		HttpJson json=null;
		BusinessTask businessTask=(BusinessTask) getBaseService(BusinessTask.class).findById(reqTask.getId());
		org.springframework.beans.BeanUtils.copyProperties(reqTask, businessTask, StrUtil.getNullPropertyNames(reqTask));
		businessTask.setUpdatetime(new Date());
		int state=getBaseService(BusinessTask.class).updateObject(businessTask, true);
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "完成添加", true,"datagrid-businessTask-filter");
			this.sendWeb(new Gson().toJson(businessTask), "application/json; charset=utf-8", request, response);
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"datagrid-businessTask-filter");
			this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
		}
	}
	
	
	public void query(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		String ids = request.getParameter("ids");
		String code = request.getParameter("code");
		JsonObject jsonObject=new JsonObject();
		if(StringUtils.isBlank(ids)
				&&StringUtils.isBlank(code)){
			jsonObject.addProperty("msg", "参数错误");
			jsonObject.addProperty("code", -1);
			this.sendWeb(jsonObject.toString(), "application/json; charset=utf-8", request, response);
			return;
		}
		if(StringUtils.isNotBlank(code)){
			List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
			List<Order> orders = new ArrayList<Order>();// 排序条件
			expList.add(Restrictions.eq("code", code));
			PaginatedListHelper businessTaskPlh=getBaseService(BusinessTask.class).findList(expList, orders, null, null);
			StringBuffer sb=new StringBuffer();
			if(businessTaskPlh.getList()!=null
					&&businessTaskPlh.getList().size()>0){
				for(Object o:businessTaskPlh.getList()){
					BusinessTask businessTask=(BusinessTask)o;
					sb.append(businessTask.getId());
					sb.append(",");
				}
				ids=sb.toString();
			}
		}
		if(StringUtils.isBlank(ids)){
			jsonObject.addProperty("code", -1);
			this.sendWeb(jsonObject.toString(), "application/json; charset=utf-8", request, response);
			return;
		}
		boolean isFinish=true;
		StringBuffer msgsb=new StringBuffer();
		StringBuffer detailsb=new StringBuffer();
		StringBuffer filesb=new StringBuffer();
		for(int i=0;i<ids.split(",").length;i++){
			String id=ids.split(",")[i];
			if(StringUtils.isBlank(id)){
				continue;
			}
			BusinessTask businessTask=(BusinessTask) getBaseService(BusinessTask.class).findById(id);
			if(businessTask==null){
				continue;
			}
			if(!businessTask.getState().equals(BusinessTaskState.finish.getCode())){
				isFinish=false;
			}
			msgsb.append(businessTask.getName());
			msgsb.append(" ");
			msgsb.append(i+1);
			msgsb.append(" ");
			msgsb.append(StrUtil.getDisplay(BusinessTaskState.class.getName(),businessTask.getState()));
			msgsb.append(" ");
			if(businessTask.getState().equals(BusinessTaskState.finish.getCode())){
				if(businessTask.getType().equals(BusinessTaskType.exp.getCode())){
					filesb.append(WebUtil.getServerUrl(request));
					filesb.append("/");
					filesb.append(businessTask.getDownloadfile());
					filesb.append(",");
				}
				detailsb.append(businessTask.getRemark());
			}
		}
		jsonObject.addProperty("msg", msgsb.toString());
		if(isFinish){
			if(StringUtils.isNotBlank(filesb.toString())){
				jsonObject.addProperty("files", filesb.toString());
			}
			jsonObject.addProperty("detail", detailsb.toString());
			jsonObject.addProperty("code", 1);
		}else{
			jsonObject.addProperty("code", 0);
		}
		this.sendWeb(jsonObject.toString(), "application/json; charset=utf-8", request, response);
		return;
	}
	
	public void time(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.or(
				Restrictions.eq("state", BusinessTaskState.run.getCode()),
						Restrictions.eq("state", BusinessTaskState.wait.getCode())));
		orders.add(Order.asc("updatetime"));
		PaginatedListHelper businessTaskPlh=getBaseService(BusinessTask.class).findList(expList, orders, null, null);
		JsonObject jsonObject=new JsonObject();
		if(businessTaskPlh.getList()!=null
				&&businessTaskPlh.getList().size()>0){
			jsonObject.addProperty("code", 1);
			JsonArray jsonArray=new JsonArray();
			for(Object o:businessTaskPlh.getList()){
				JsonObject object=new JsonObject();
				BusinessTask businessTask=(BusinessTask)o;
				BusinessChannel businessChannel=(BusinessChannel) getBaseService(BusinessChannel.class).findById(businessTask.getBusinessChannel().getId());
				JsonObject businessChannelObject=new JsonObject();
				businessChannelObject.addProperty("code", businessChannel.getCode());
				businessChannelObject.addProperty("name", businessChannel.getName());
				object.add("BusinessChannel", businessChannelObject);
				object.addProperty("name", businessTask.getName());
				object.addProperty("state",StrUtil.getDisplay(BusinessTaskState.class.getName(), businessTask.getState()));
				object.addProperty("updatetime", DateUtil.getDateTime(businessTask.getUpdatetime()));
				jsonArray.add(object);
			}
			jsonObject.add("data", jsonArray);
		}else{
			jsonObject.addProperty("code", 0);
		}
		this.sendWeb(jsonObject.toString(), "application/json; charset=utf-8", request, response);
		return ;
	}
	
	
	private void setExpList(List<Criterion> expList,BusinessTask reqTask,
			HttpServletRequest request,DetachedCriteria dc){
		String codename = request.getParameter("code|name");
		if(StringUtils.isNotBlank(codename)){
			dc.createAlias("BusinessChannel", "BusinessChannel");
			expList.add(Restrictions.or(
					Restrictions.like("BusinessChannel.code", codename,MatchMode.ANYWHERE),
							Restrictions.like("BusinessChannel.name", codename,MatchMode.ANYWHERE)));
		}
		if(StringUtils.isNotBlank(reqTask.getRemark())){
			expList.add(Restrictions.like("remark", reqTask.getRemark(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqTask.getType())){
			expList.add(Restrictions.eq("type", reqTask.getType()));
		}
		if(StringUtils.isNotBlank(reqTask.getState())){
			expList.add(Restrictions.eq("state", reqTask.getState()));
		}else{
			expList.add(Restrictions.or(
					Restrictions.eq("state", BusinessTaskState.run.getCode()),
							Restrictions.eq("state", BusinessTaskState.wait.getCode())));
		}
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		if(StringUtils.isNotBlank(startTime)){
			expList.add(Restrictions.ge("updatetime", DateUtil.stringToDate(startTime)));
		}
		if(StringUtils.isNotBlank(endTime)){
			expList.add(Restrictions.le("updatetime", DateUtil.stringToDate(endTime)));
		}
	}

	
	@SuppressWarnings("unchecked")
	private BusinessTask getReqTask(HttpServletRequest request){
		BusinessTask  businessTask= new BusinessTask(); 
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
			CBeanUtils.populate(businessTask,newMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return businessTask;
	}	


}
