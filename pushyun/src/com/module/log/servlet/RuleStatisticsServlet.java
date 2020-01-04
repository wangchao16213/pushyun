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
import com.bean.LogRuleStatistics;
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
import com.module.gson.adapter.GsonTypeAdapter;
import com.module.gson.bean.FieldBean;
import com.module.gson.template.FieldTemplateBean;

public class RuleStatisticsServlet extends Base{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LogRuleStatistics reqRuleStatistics=getReqRuleStatistics(request);
		int page = StringUtils.isNotBlank(request.getParameter("pageCurrent"))?
				Integer.parseInt(request.getParameter("pageCurrent")):1;//当前页数
		int pageSize=StringUtils.isNotBlank(request.getParameter("pageSize"))?
				Integer.parseInt(request.getParameter("pageSize")):20;//当前页数
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		DetachedCriteria dc=DetachedCriteria.forClass(LogRuleStatistics.class);
		setExpList(expList,reqRuleStatistics,request,dc);
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
		PaginatedListHelper logRuleStatisticsPlh= getBaseService(LogRuleStatistics.class).findList(expList, orders, pages, dc);
		Page<Object> data = new Page<Object>(logRuleStatisticsPlh.getList(),
				pages.getPage(), pages.getPerPageNum(), pages.getAllPage(), pages.getTotalNum());
		Map<String,FieldBean> adapterMap=new HashMap<String,FieldBean>();
		adapterMap.put("com.bean.LogRuleStatistics.sdate", FieldTemplateBean.getStrTemplate("sdate"));
		adapterMap.put("com.bean.LogRuleStatistics.BusinessChannel", FieldTemplateBean.getStrTemplate("BusinessChannel"));
		adapterMap.put("com.bean.LogRuleStatistics.BusinessRule", FieldTemplateBean.getStrTemplate("BusinessRule"));
		adapterMap.put("com.bean.LogRuleStatistics.BusinessRuleDetail", FieldTemplateBean.getStrTemplate("BusinessRuleDetail"));
		adapterMap.put("com.bean.BusinessChannel.code", FieldTemplateBean.getStrTemplate("BusinessChannel.code"));
		adapterMap.put("com.bean.BusinessChannel.name", FieldTemplateBean.getStrTemplate("BusinessChannel.name"));
		adapterMap.put("com.bean.BusinessRule.exact", FieldTemplateBean.getStrTemplate("BusinessRule.exact"));
		adapterMap.put("com.bean.BusinessRule.fuzzy", FieldTemplateBean.getStrTemplate("BusinessRule.fuzzy"));
		adapterMap.put("com.bean.BusinessRuleDetail.content}", FieldTemplateBean.getStrTemplate("BusinessRuleDetail.content"));
		adapterMap.put("com.bean.LogRuleStatistics.num", FieldTemplateBean.getStrTemplate("num"));
		adapterMap.put("com.bean.LogRuleStatistics.updatetime", FieldTemplateBean.getStrTemplate("updatetime"));
		Gson gson = new GsonBuilder().registerTypeAdapter(LogRuleStatistics.class, new GsonTypeAdapter<LogRuleStatistics>(adapterMap)).create();
		
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
			LogRuleStatistics logRuleStatistics=(LogRuleStatistics) getBaseService(LogRuleStatistics.class).findById(id);
			state=getBaseService(LogRuleStatistics.class).deleteObject(logRuleStatistics);
		}
		HttpJson json=null;
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "", true,"datagrid-logRuleStatistics-filter");
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"datagrid-logRuleStatistics-filter");
		}
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
	}
	
	
	public void truncate(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		LogRuleStatistics reqRuleStatistics=getReqRuleStatistics(request);
		int state=Constants.STATE_OPERATOR_LOST;
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		DetachedCriteria dc=DetachedCriteria.forClass(LogRuleStatistics.class);
		setExpList(expList,reqRuleStatistics,request,dc);
		int total = getBaseService(LogRuleStatistics.class).getList(expList, dc);
		List<Object> deleteList = new ArrayList<Object>();
		Pages totalpages = new Pages();
		totalpages.setPerPageNum(5000);// 分页查询
		totalpages.setTotalNum(total);
		totalpages.executeCount();
		for (int i = 0; i < totalpages.getAllPage(); i++) {
			Pages pages = new Pages();
			pages.setPerPageNum(totalpages.getPerPageNum());
			pages.setSpage(i * totalpages.getPerPageNum());// 列表开始位置
			List<BaseRecord> list = getBaseService(LogRuleStatistics.class).findList(
					expList, orders, pages.getSpage(), pages.getPerPageNum(),
					dc);
			if (list == null || list.size() == 0) {
				break;
			}
			deleteList.addAll(list);
		}
		HttpJson json=null;
		if(deleteList.size()==0){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "没有需要处理的数据!", false,"datagrid-logRuleStatistics-filter");
			this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
			return;
		}
		for(Object o:deleteList){
			state=getBaseService(LogRuleStatistics.class).deleteObject((BaseRecord)o);
		}
		
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "", true,"datagrid-logRuleStatistics-filter");
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"datagrid-logRuleStatistics-filter");
		}
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
	}
	
	public void expAll(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LogRuleStatistics reqRuleStatistics=getReqRuleStatistics(request);
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		DetachedCriteria dc=DetachedCriteria.forClass(LogRuleStatistics.class);
		setExpList(expList, reqRuleStatistics, request,dc);
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
		int total = getBaseService(LogRuleStatistics.class).getList(expList,dc);
		List<Object> expportList = new ArrayList<Object>();
		Pages totalpages = new Pages();
		totalpages.setPerPageNum(5000);// 分页查询
		totalpages.setTotalNum(total);
		totalpages.executeCount();
		for (int i = 0; i < totalpages.getAllPage(); i++) {
			Pages pages = new Pages();
			pages.setPerPageNum(totalpages.getPerPageNum());
			pages.setSpage(i * totalpages.getPerPageNum());// 列表开始位置
			List<BaseRecord> list = getBaseService(LogRuleStatistics.class)
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
		PaginatedListHelper logRuleStatisticsPlh = getBaseService(LogRuleStatistics.class).findList(expList, orders, null, null);
		String fileUrl = createExpFile(logRuleStatisticsPlh.getList());
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
		int count=8;
		int i=0;
		try {
			String[] headers = new String[count];
			headers[++i-1] = "年月日";
			headers[++i-1] = "所属渠道编码";
			headers[++i-1] = "所属渠道名称";
			headers[++i-1] = "精确匹配";
			headers[++i-1] = "模糊匹配";
			headers[++i-1] = "推送信息";
			headers[++i-1] = "数量";
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
					LogRuleStatistics logRuleStatistics = (LogRuleStatistics) o;
					contents[++i-1] = logRuleStatistics.getSdate();
					if(logRuleStatistics.getBusinessChannel()!=null){
						contents[++i-1] = logRuleStatistics.getBusinessChannel().getCode();
						contents[++i-1] = logRuleStatistics.getBusinessChannel().getName();
					}else{
						++i;
						++i;
					}
					contents[++i-1] = logRuleStatistics.getBusinessRule().getExact();
					contents[++i-1] = logRuleStatistics.getBusinessRule().getFuzzy();
					contents[++i-1] = logRuleStatistics.getBusinessRuleDetail().getContent();
					contents[++i-1] = logRuleStatistics.getNum().toString();
					contents[++i-1] = DateUtil.getDateTime(logRuleStatistics.getUpdatetime());
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
	
	
	private void setExpList(List<Criterion> expList,LogRuleStatistics reqRuleStatistics,HttpServletRequest request,DetachedCriteria dc){
		if(StringUtils.isNotBlank(reqRuleStatistics.getBusinessChannel().getCode())){
			expList.add(Restrictions.like("BusinessChannel.code", reqRuleStatistics.getBusinessChannel().getCode(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqRuleStatistics.getBusinessChannel().getName())){
			expList.add(Restrictions.like("BusinessChannel.name", reqRuleStatistics.getBusinessChannel().getName(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqRuleStatistics.getRemark())){
			expList.add(Restrictions.like("remark", reqRuleStatistics.getRemark(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqRuleStatistics.getBusinessChannel().getCode())
				||StringUtils.isNotBlank(reqRuleStatistics.getBusinessChannel().getName())){
			if(dc!=null){
				dc.createAlias("BusinessChannel", "BusinessChannel");
			}
		}
		String exactfuzzy = request.getParameter("exact|fuzzy");
		if(StringUtils.isNotBlank(exactfuzzy)){
			if(dc!=null){
				dc.createAlias("BusinessRule", "BusinessRule");
			}
			expList.add(Restrictions.or(
					Restrictions.like("BusinessRule.fuzzy", exactfuzzy,MatchMode.ANYWHERE),
							Restrictions.like("BusinessRule.exact", exactfuzzy,MatchMode.ANYWHERE)));
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
	private LogRuleStatistics getReqRuleStatistics(HttpServletRequest request){
		LogRuleStatistics  logRuleStatistics= new LogRuleStatistics();
		BusinessChannel businessChannel=new BusinessChannel();
		logRuleStatistics.setBusinessChannel(businessChannel);
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
			CBeanUtils.populate(logRuleStatistics,newMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return logRuleStatistics;
	}	

}
