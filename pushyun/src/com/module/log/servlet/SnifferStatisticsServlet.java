package com.module.log.servlet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import au.com.bytecode.opencsv.CSVWriter;

import com.bean.BaseRecord;
import com.bean.BusinessChannel;
import com.bean.LogSnifferStatistics;
import com.common.comm.Constants;
import com.common.servlet.Base;
import com.common.tools.CBeanUtils;
import com.common.tools.DateUtil;
import com.common.web.HttpJson;
import com.common.web.Page;
import com.common.web.Pages;
import com.common.web.PaginatedListHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SnifferStatisticsServlet extends Base{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LogSnifferStatistics reqSnifferStatistics=getReqSnifferStatistics(request);
		int page = StringUtils.isNotBlank(request.getParameter("pageCurrent"))?
				Integer.parseInt(request.getParameter("pageCurrent")):1;//当前页数
		int pageSize=StringUtils.isNotBlank(request.getParameter("pageSize"))?
				Integer.parseInt(request.getParameter("pageSize")):20;//当前页数
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		DetachedCriteria dc=DetachedCriteria.forClass(LogSnifferStatistics.class);
		setExpList(expList,reqSnifferStatistics,request, dc);
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
		PaginatedListHelper logSnifferStatisticsPlh= getBaseService(LogSnifferStatistics.class).findList(expList, orders, pages, dc);
		Page<Object> data = new Page<Object>(logSnifferStatisticsPlh.getList(),
				pages.getPage(), pages.getPerPageNum(), pages.getAllPage(), pages.getTotalNum());
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();   
		
		this.sendWeb(gson.toJson(data), "application/json; charset=utf-8", request, response);
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
			LogSnifferStatistics logSnifferStatistics=(LogSnifferStatistics) getBaseService(LogSnifferStatistics.class).findById(id);
			state=getBaseService(LogSnifferStatistics.class).deleteObject(logSnifferStatistics);
		}
		HttpJson json=null;
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "", true,"datagrid-logSnifferStatistics-filter");
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"datagrid-logSnifferStatistics-filter");
		}
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
	}
	
	
	public void truncate(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		LogSnifferStatistics reqSnifferStatistics=getReqSnifferStatistics(request);
		int state=Constants.STATE_OPERATOR_LOST;
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		DetachedCriteria dc=DetachedCriteria.forClass(LogSnifferStatistics.class);
		setExpList(expList,reqSnifferStatistics,request,dc);
		int total = getBaseService(LogSnifferStatistics.class).getList(expList, dc);
		List<Object> deleteList = new ArrayList<Object>();
		Pages totalpages = new Pages();
		totalpages.setPerPageNum(5000);// 分页查询
		totalpages.setTotalNum(total);
		totalpages.executeCount();
		for (int i = 0; i < totalpages.getAllPage(); i++) {
			Pages pages = new Pages();
			pages.setPerPageNum(totalpages.getPerPageNum());
			pages.setSpage(i * totalpages.getPerPageNum());// 列表开始位置
			List<BaseRecord> list = getBaseService(LogSnifferStatistics.class).findList(
					expList, orders, pages.getSpage(), pages.getPerPageNum(),
					null);
			if (list == null || list.size() == 0) {
				break;
			}
			deleteList.addAll(list);
		}
		HttpJson json=null;
		if(deleteList.size()==0){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "没有需要处理的数据!", false,"datagrid-logSnifferStatistics-filter");
			this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
			return;
		}
		for(Object o:deleteList){
			state=getBaseService(LogSnifferStatistics.class).deleteObject((BaseRecord)o);
		}
		
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "", true,"datagrid-logSnifferStatistics-filter");
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"datagrid-logSnifferStatistics-filter");
		}
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
	}
	
	public void expAll(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LogSnifferStatistics reqSnifferStatistics=getReqSnifferStatistics(request);
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		DetachedCriteria dc=DetachedCriteria.forClass(LogSnifferStatistics.class);
		setExpList(expList, reqSnifferStatistics, request,dc);
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
		int total = getBaseService(LogSnifferStatistics.class).getList(expList,dc);
		List<Object> expportList = new ArrayList<Object>();
		Pages totalpages = new Pages();
		totalpages.setPerPageNum(5000);// 分页查询
		totalpages.setTotalNum(total);
		totalpages.executeCount();
		for (int i = 0; i < totalpages.getAllPage(); i++) {
			Pages pages = new Pages();
			pages.setPerPageNum(totalpages.getPerPageNum());
			pages.setSpage(i * totalpages.getPerPageNum());// 列表开始位置
			List<BaseRecord> list = getBaseService(LogSnifferStatistics.class)
					.findList(expList, orders, pages.getSpage(),
							pages.getPerPageNum(), dc);
			if (list == null || list.size() == 0) {
				break;
			}
			expportList.addAll(list);
		}
		String fileUrl = createExpFile(expportList);
		request.setAttribute("fileUrl", fileUrl);
		request.setAttribute("msg", String.format("请求%s数据", ""));
		request.getRequestDispatcher("/html/exp/exp.jsp").forward(request,
				response);
	}

	public void expSelected(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String expids = request.getParameter("expids");
		if (StringUtils.isBlank(expids)) {
			request.setAttribute("msg", "没有数据!");
			request.getRequestDispatcher("/html/exp/exp.jsp").forward(request,
					response);
			return;
		}
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.in("id", expids.split(",")));
		PaginatedListHelper logSnifferStatisticsPlh = getBaseService(LogSnifferStatistics.class).findList(expList, orders, null, null);
		String fileUrl = createExpFile(logSnifferStatisticsPlh.getList());
		request.setAttribute("fileUrl", fileUrl);
		request.setAttribute("msg", String.format("请求%s数据", ""));
		request.getRequestDispatcher("/html/exp/exp.jsp").forward(request,response);
	}

	private String createExpFile(List<Object> expportList) {
		String rootPath = "build";
		String buildPath = this.getServletContext().getRealPath(rootPath);// 生成路径
		StringBuffer filePathsb = new StringBuffer();
		filePathsb.append(rootPath);
		File buildPathFile = new File(buildPath);
		if (!buildPathFile.exists()) {
			buildPathFile.mkdirs();
		}
		String expName = (UUID.randomUUID() + ".csv").replaceAll("-", "");// 生成文件名称
		String charsetName = "GB2312";// 文件编码
		CSVWriter writer = null;
		OutputStreamWriter outputStreamWriter = null;
		try {
			outputStreamWriter = new OutputStreamWriter(FileUtils
					.openOutputStream(new File(buildPath + File.separator
							+ expName)), charsetName);
			writer = new CSVWriter(outputStreamWriter);
			filePathsb.append("/");
			filePathsb.append(expName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int count=9;
		int i=0;
		try {
			String[] headers = new String[count];
			headers[++i-1] = "年月日";
			headers[++i-1] = "所属渠道编码";
			headers[++i-1] = "所属渠道名称";
			headers[++i-1] = "网卡";
			headers[++i-1] = "流量";
			headers[++i-1] = "匹配规则数量";
			headers[++i-1] = "处理总量";
			headers[++i-1] = "上报时间";
			headers[++i-1] = "更新时间";
			writer.writeNext(headers);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			List<String[]> writeList = new ArrayList<String[]>();
			if (expportList != null && expportList.size() > 0) {
				for (Object o : expportList) {
					i=0;
					String[] contents = new String[count];
					for(int j=0;j<count;j++){
						contents[j]="";
					}
					LogSnifferStatistics logSnifferStatistics = (LogSnifferStatistics) o;
					contents[++i-1] = logSnifferStatistics.getSdate();
					if(logSnifferStatistics.getBusinessChannel()!=null){
						contents[++i-1] = logSnifferStatistics.getBusinessChannel().getCode();
						contents[++i-1] = logSnifferStatistics.getBusinessChannel().getName();
					}else{
						++i;
						++i;
					}
					contents[++i-1] = logSnifferStatistics.getSniffername();
					contents[++i-1] = logSnifferStatistics.getTraffic().toString();
					contents[++i-1] = logSnifferStatistics.getMatchrulenum().toString();
					contents[++i-1] = logSnifferStatistics.getTotalnum().toString();
					contents[++i-1] = DateUtil.getDateTime(logSnifferStatistics.getReporttime());
					contents[++i-1] = DateUtil.getDateTime(logSnifferStatistics.getUpdatetime());
					writeList.add(contents);
				}
			}
			writer.writeAll(writeList);
			writeList.clear();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
				if (outputStreamWriter != null) {
					outputStreamWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return filePathsb.toString();
	}
	
	
	private void setExpList(List<Criterion> expList,LogSnifferStatistics reqSnifferStatistics,HttpServletRequest request,DetachedCriteria dc){
		if(StringUtils.isNotBlank(reqSnifferStatistics.getBusinessChannel().getCode())
				||StringUtils.isNotBlank(reqSnifferStatistics.getBusinessChannel().getName())){
			expList.add(Restrictions.like("BusinessChannel.code", reqSnifferStatistics.getBusinessChannel().getCode(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqSnifferStatistics.getBusinessChannel().getName())){
			expList.add(Restrictions.like("BusinessChannel.name", reqSnifferStatistics.getBusinessChannel().getName(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqSnifferStatistics.getBusinessChannel().getCode())
				||StringUtils.isNotBlank(reqSnifferStatistics.getBusinessChannel().getName())){
			if(dc!=null){
				dc.createAlias("BusinessChannel", "BusinessChannel");
			}
		}
		if(StringUtils.isNotBlank(reqSnifferStatistics.getRemark())){
			expList.add(Restrictions.like("remark", reqSnifferStatistics.getRemark(),MatchMode.ANYWHERE));
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
	private LogSnifferStatistics getReqSnifferStatistics(HttpServletRequest request){
		LogSnifferStatistics  logSnifferStatistics= new LogSnifferStatistics();
		BusinessChannel businessChannel=new BusinessChannel();
		logSnifferStatistics.setBusinessChannel(businessChannel);
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
			CBeanUtils.populate(logSnifferStatistics,newMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return logSnifferStatistics;
	}	

}
