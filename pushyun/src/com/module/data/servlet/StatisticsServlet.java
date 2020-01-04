package com.module.data.servlet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
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
import com.bean.BusinessRule;
import com.bean.DataStatistics;
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
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.module.gson.adapter.GsonTypeAdapter;
import com.module.gson.bean.FieldBean;
import com.module.gson.template.FieldTemplateBean;

public class StatisticsServlet extends Base{

	private static final long serialVersionUID = 1L;

	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		DataStatistics reqStatistics=getReqStatistics(request);
		int page = StringUtils.isNotBlank(request.getParameter("pageCurrent"))?
				Integer.parseInt(request.getParameter("pageCurrent")):1;//当前页数
		int pageSize=StringUtils.isNotBlank(request.getParameter("pageSize"))?
				Integer.parseInt(request.getParameter("pageSize")):20;//当前页数
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		DetachedCriteria dc=DetachedCriteria.forClass(DataStatistics.class);
		setExpList(expList,reqStatistics,request,dc);
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
		PaginatedListHelper dataStatisticsPlh= getBaseService(DataStatistics.class).findList(expList, orders, pages, dc);
		Page<Object> data = new Page<Object>(dataStatisticsPlh.getList(),
				pages.getPage(), pages.getPerPageNum(), pages.getAllPage(), pages.getTotalNum());
		Map<String,FieldBean> adapterMap=new HashMap<String,FieldBean>();
		adapterMap.put("com.bean.DataStatistics.sdate", FieldTemplateBean.getStrTemplate("sdate"));
		adapterMap.put("com.bean.DataStatistics.BusinessChannel", FieldTemplateBean.getStrTemplate("BusinessChannel"));
		adapterMap.put("com.bean.BusinessChannel.code", FieldTemplateBean.getStrTemplate("BusinessChannel.code"));
		adapterMap.put("com.bean.BusinessChannel.name", FieldTemplateBean.getStrTemplate("BusinessChannel.name"));
		adapterMap.put("com.bean.DataStatistics.exact", FieldTemplateBean.getStrTemplate("exact"));
		adapterMap.put("com.bean.DataStatistics.fuzzy", FieldTemplateBean.getStrTemplate("fuzzy"));
		adapterMap.put("com.bean.DataStatistics.BusinessRule", FieldTemplateBean.getStrTemplate("BusinessRule"));
		adapterMap.put("com.bean.BusinessRule.urlfilter", FieldTemplateBean.getStrTemplate("BusinessRule.urlfilter"));
		adapterMap.put("com.bean.DataStatistics.count", FieldTemplateBean.getStrTemplate("count"));
		adapterMap.put("com.bean.DataStatistics.updatetime", FieldTemplateBean.getStrTemplate("updatetime"));
		Gson gson = new GsonBuilder().registerTypeAdapter(DataStatistics.class, new GsonTypeAdapter<DataStatistics>(adapterMap)).create();
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
			DataStatistics dataStatistics=(DataStatistics) getBaseService(DataStatistics.class).findById(id);
			state=getBaseService(DataStatistics.class).deleteObject(dataStatistics);
		}
		HttpJson json=null;
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "", true,"datagrid-dataStatistics-filter");
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"datagrid-dataStatistics-filter");
		}
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
	}
	
	
	public void expAll(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		DataStatistics reqStatistics=getReqStatistics(request);
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		DetachedCriteria dc=DetachedCriteria.forClass(DataStatistics.class);
		setExpList(expList, reqStatistics, request,dc);
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
		int total = getBaseService(DataStatistics.class).getList(expList,dc);
		List<Object> expportList = new ArrayList<Object>();
		Pages totalpages = new Pages();
		totalpages.setPerPageNum(5000);// 分页查询
		totalpages.setTotalNum(total);
		totalpages.executeCount();
		for (int i = 0; i < totalpages.getAllPage(); i++) {
			Pages pages = new Pages();
			pages.setPerPageNum(totalpages.getPerPageNum());
			pages.setSpage(i * totalpages.getPerPageNum());// 列表开始位置
			List<BaseRecord> list = getBaseService(DataStatistics.class).findList(expList, orders, pages.getSpage(),pages.getPerPageNum(), dc);
			if (list == null || list.size() == 0) {
				break;
			}
			expportList.addAll(list);
		}
		String fileUrl = createExpFile(expportList);
		request.setAttribute("fileUrl", fileUrl);
		request.setAttribute("msg", String.format("统计%s数据", ""));
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
		PaginatedListHelper dataStatisticsPlh = getBaseService(DataStatistics.class).findList(expList, orders, null, null);
		String fileUrl = createExpFile(dataStatisticsPlh.getList());
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
		int count=7;
		int i=0;
		try {
			String[] headers = new String[count];
			headers[++i-1] = "年月日";
			headers[++i-1] = "所属渠道编码";
			headers[++i-1] = "所属渠道名称";
			headers[++i-1] = "精确匹配";
			headers[++i-1] = "模糊匹配";
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
					DataStatistics dataStatistics = (DataStatistics) o;
					contents[++i-1] = dataStatistics.getSdate();
					if(dataStatistics.getBusinessChannel()!=null){
						contents[++i-1] = dataStatistics.getBusinessChannel().getCode();
						contents[++i-1] = dataStatistics.getBusinessChannel().getName();
					}else{
						++i;
						++i;
					}
					contents[++i-1] = dataStatistics.getExact();
					contents[++i-1] = dataStatistics.getFuzzy();
					contents[++i-1] = dataStatistics.getCount().toString();
					contents[++i-1] = DateUtil.getDateTime(dataStatistics.getUpdatetime());
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
	
	
	private void setExpList(List<Criterion> expList,DataStatistics reqStatistics,HttpServletRequest request,DetachedCriteria dc){
		if(StringUtils.isNotBlank(reqStatistics.getBusinessChannel().getCode())){
			expList.add(Restrictions.like("BusinessChannel.code", reqStatistics.getBusinessChannel().getCode(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqStatistics.getBusinessChannel().getName())){
			expList.add(Restrictions.like("BusinessChannel.name", reqStatistics.getBusinessChannel().getName(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqStatistics.getBusinessRule().getUrlfilter())){
			if(dc!=null){
				dc.createAlias("BusinessRule", "BusinessRule");
			}
			expList.add(Restrictions.like("BusinessRule.urlfilter", reqStatistics.getBusinessRule().getUrlfilter(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqStatistics.getRemark())){
			expList.add(Restrictions.like("remark", reqStatistics.getRemark(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqStatistics.getBusinessChannel().getCode())
				||StringUtils.isNotBlank(reqStatistics.getBusinessChannel().getName())){
			if(dc!=null){
				dc.createAlias("BusinessChannel", "BusinessChannel");
			}
		}
		String exactfuzzy = request.getParameter("exact|fuzzy");
		if(StringUtils.isNotBlank(exactfuzzy)){
			expList.add(Restrictions.or(
					Restrictions.like("fuzzy", exactfuzzy,MatchMode.ANYWHERE),
							Restrictions.like("exact", exactfuzzy,MatchMode.ANYWHERE)));
		}
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		if(StringUtils.isNotBlank(startDate)){
			expList.add(Restrictions.ge("sdate", startDate));
		}
		if(StringUtils.isNotBlank(endDate)){
			expList.add(Restrictions.le("sdate", endDate));
		}
	}

	
	@SuppressWarnings("unchecked")
	private DataStatistics getReqStatistics(HttpServletRequest request){
		DataStatistics  dataStatistics= new DataStatistics();
		BusinessChannel businessChannel=new BusinessChannel();
		BusinessRule businessRule=new BusinessRule();
		dataStatistics.setBusinessRule(businessRule);
		dataStatistics.setBusinessChannel(businessChannel);
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
			CBeanUtils.populate(dataStatistics,newMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataStatistics;
	}	
}
