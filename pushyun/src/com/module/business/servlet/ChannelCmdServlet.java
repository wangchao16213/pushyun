package com.module.business.servlet;

import java.io.IOException;
import java.util.ArrayList;
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
import com.bean.BusinessChannelCmd;
import com.common.comm.Constants;
import com.common.servlet.Base;
import com.common.tools.CBeanUtils;
import com.common.tools.DateUtil;
import com.common.type.BusinessChannelCmdCmd;
import com.common.type.BusinessChannelCmdState;
import com.common.web.HttpJson;
import com.common.web.Page;
import com.common.web.Pages;
import com.common.web.PaginatedListHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ChannelCmdServlet extends Base{
	
	private static final long serialVersionUID = 1L;
	

	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		BusinessChannelCmd reqChannelCmd=getReqChannelCmd(request);
		int page = StringUtils.isNotBlank(request.getParameter("pageCurrent"))?
				Integer.parseInt(request.getParameter("pageCurrent")):1;//当前页数
		int pageSize=StringUtils.isNotBlank(request.getParameter("pageSize"))?
				Integer.parseInt(request.getParameter("pageSize")):20;//当前页数
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		setExpList(expList,reqChannelCmd,request);
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
		PaginatedListHelper businessChannelCmdPlh= getBaseService(BusinessChannelCmd.class).findList(expList, orders, pages, null);
		Page<Object> data = new Page<Object>(businessChannelCmdPlh.getList(),
				pages.getPage(), pages.getPerPageNum(), pages.getAllPage(), pages.getTotalNum());
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();   
		
		this.sendWeb(gson.toJson(data), "application/json; charset=utf-8", request, response);
	}
	
	private void setExpList(List<Criterion> expList,BusinessChannelCmd reqChannelCmd,HttpServletRequest request){
		if(StringUtils.isNotBlank(reqChannelCmd.getBusinessChannel().getCode())){
			expList.add(Restrictions.like("code", reqChannelCmd.getBusinessChannel().getCode(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqChannelCmd.getBusinessChannel().getName())){
			expList.add(Restrictions.like("name", reqChannelCmd.getBusinessChannel().getName(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqChannelCmd.getRemark())){
			expList.add(Restrictions.like("remark", reqChannelCmd.getRemark(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqChannelCmd.getState())){
			expList.add(Restrictions.eq("state", reqChannelCmd.getState()));
		}else{
			expList.add(Restrictions.eq("state", BusinessChannelCmdState.wait.getCode()));
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
	
	public void del(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		BusinessChannelCmd reqChannelCmd=getReqChannelCmd(request);
		String ids=request.getParameter("id");
		if(StringUtils.isBlank(ids)){
			return;
		}
		int state=Constants.STATE_OPERATOR_LOST;
		for(String id:ids.split(",")){
			if(StringUtils.isBlank(ids)){
				continue;
			}
			BusinessChannelCmd businessChannelCmd=(BusinessChannelCmd) getBaseService(BusinessChannelCmd.class).findById(id);
			state=getBaseService(BusinessChannelCmd.class).deleteObject(businessChannelCmd);
		}
		HttpJson json=null;
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "", true,"datagrid-businessChannelCmd-filter");
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"datagrid-businessChannelCmd-filter");
		}
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
	}

	
	@SuppressWarnings("unchecked")
	private BusinessChannelCmd getReqChannelCmd(HttpServletRequest request){
		BusinessChannelCmd  businessChannelCmd= new BusinessChannelCmd(); 
		BusinessChannel businessChannel=new BusinessChannel();
		businessChannelCmd.setBusinessChannel(businessChannel);
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
			CBeanUtils.populate(businessChannelCmd,newMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return businessChannelCmd;
	}	

}
