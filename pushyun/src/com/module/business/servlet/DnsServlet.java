package com.module.business.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;


import com.bean.BaseRecord;
import com.bean.BusinessChannel;
import com.bean.BusinessDns;
import com.bean.BusinessTask;
import com.common.comm.Constants;
import com.common.servlet.Base;
import com.common.tools.CBeanUtils;
import com.common.tools.DateUtil;
import com.common.tools.StrUtil;
import com.common.tools.WebUtil;
import com.common.tools.ZipUtils;
import com.common.type.BusinessDnsState;
import com.common.type.BusinessTaskState;
import com.common.type.BusinessTaskType;
import com.common.web.HttpJson;
import com.common.web.Page;
import com.common.web.Pages;
import com.common.web.PaginatedListHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.module.business.service.DnsService;

public class DnsServlet extends Base{

	private static final long serialVersionUID = 1L;

	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		BusinessDns reqDns=getReqDns(request);
		int page = StringUtils.isNotBlank(request.getParameter("pageCurrent"))?
				Integer.parseInt(request.getParameter("pageCurrent")):1;//当前页数
		int pageSize=StringUtils.isNotBlank(request.getParameter("pageSize"))?
				Integer.parseInt(request.getParameter("pageSize")):20;//当前页数
		String businessChannelId=request.getParameter("businessChannel.id");		
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		DetachedCriteria dc=DetachedCriteria.forClass(BusinessDns.class);
		((DnsService)getService(DnsService.class)).setExpList(expList,reqDns,request,dc);
		expList.add(Restrictions.eq("BusinessChannel.id", businessChannelId));
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
		PaginatedListHelper businessDnsPlh= getBaseService(BusinessDns.class).findList(expList, orders, pages, dc);
		Page<Object> data = new Page<Object>(businessDnsPlh.getList(),
				pages.getPage(), pages.getPerPageNum(), pages.getAllPage(), pages.getTotalNum());
		this.sendWeb(getGson().toJson(data), "application/json; charset=utf-8", request, response);
	}
	
	
	
	
	public void initpage(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		BusinessDns reqDns=getReqDns(request);
		String url="";
		if(StringUtils.isNotBlank(reqDns.getId())){
			BusinessDns businessDns=(BusinessDns) getBaseService(BusinessDns.class).findById(reqDns.getId());
			request.setAttribute("businessDnsForm", businessDns);
			url="/html/module/business/dns/modify.jsp";
		}else{
			request.setAttribute("businessDnsForm", reqDns);
			url="/html/module/business/dns/add.jsp";
		}
		request.setAttribute("statusList", BusinessDnsState.values());
		request.getRequestDispatcher(url).forward(request, response);
	}
	
	public void initcopy(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		BusinessDns reqDns=getReqDns(request);
		BusinessDns businessDns = (BusinessDns) getBaseService(BusinessDns.class).findById(reqDns.getId());
		request.setAttribute("businessDnsForm", businessDns);
		String url = "/html/module/business/dns/add.jsp";
		request.setAttribute("statusList", BusinessDnsState.values());
		request.getRequestDispatcher(url).forward(request, response);
	}
	
	
	public void add(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		BusinessDns reqDns=getReqDns(request);
		HttpJson json=null;
		if(StringUtils.isNotBlank(reqDns.getHost())){
			List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
			List<Order> orders = new ArrayList<Order>();// 排序条件
			expList.add(Restrictions.eq("host", reqDns.getHost()));
			expList.add(Restrictions.eq("BusinessChannel.id", reqDns.getBusinessChannel().getId()));
			PaginatedListHelper checkbusinessRulePlh=getBaseService(BusinessDns.class).findList(expList, orders, null, null);
			if(checkbusinessRulePlh.getList()!=null
					&&checkbusinessRulePlh.getList().size()>0){
				json = new HttpJson(HttpJson.STATE_FAIL, "DNS重复", false,String.format("datagrid-businessDns%s-filter", reqDns.getBusinessChannel().getId()));
				this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
				return;
			}
		}
		reqDns.setCreateuserid(getManageUser(request).getId());
		reqDns.setUpdateuserid(reqDns.getCreateuserid());
		reqDns.setCreatetime(new Date());
		reqDns.setUpdatetime(reqDns.getCreatetime());
		String contents[]=request.getParameterValues("content");
		String types[]=request.getParameterValues("type");
		BusinessChannel businessChannel=(BusinessChannel) getBaseService(BusinessChannel.class).findById(reqDns.getBusinessChannel().getId());
		reqDns.setBusinessChannel(businessChannel);
		int state= getBaseService(BusinessDns.class).saveObject(reqDns);
		saveBusinessTask(businessChannel,BusinessTaskType.api,null,null,null,request);
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "", true,String.format("datagrid-businessDns%s-filter", reqDns.getBusinessChannel().getId()));
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,String.format("datagrid-businessDns%s-filter", reqDns.getBusinessChannel().getId()));
		}
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
	}
	
	public void modify(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		BusinessDns reqDns=getReqDns(request);
		HttpJson json=null;
		if(StringUtils.isNotBlank(reqDns.getHost())){
			List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
			List<Order> orders = new ArrayList<Order>();// 排序条件
			expList.add(Restrictions.eq("host", reqDns.getHost()));
			expList.add(Restrictions.eq("BusinessChannel.id", reqDns.getBusinessChannel().getId()));
			expList.add(Restrictions.ne("id", reqDns.getId()));
			PaginatedListHelper checkBusinessRulePlh=getBaseService(BusinessDns.class).findList(expList, orders, null, null);
			if(checkBusinessRulePlh.getList()!=null
					&&checkBusinessRulePlh.getList().size()>0){
				json = new HttpJson(HttpJson.STATE_FAIL, "规则重复", false,String.format("datagrid-businessDns%s-filter", reqDns.getBusinessChannel().getId()));
				this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
				return;
			}
		}
		BusinessDns dns=(BusinessDns) getBaseService(BusinessDns.class).findById(reqDns.getId());
		org.springframework.beans.BeanUtils.copyProperties(reqDns, dns, StrUtil.getNullPropertyNames(reqDns));
		dns.setUpdateuserid(getManageUser(request).getId());
		dns.setUpdatetime(new Date());
		BusinessChannel businessChannel=(BusinessChannel) getBaseService(BusinessChannel.class).findById(dns.getBusinessChannel().getId());
		dns.setBusinessChannel(businessChannel);
		int state=getBaseService(BusinessDns.class).updateObject(dns,true);
		saveBusinessTask(businessChannel,BusinessTaskType.api,null,null,null,request);
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "完成添加", true,String.format("datagrid-businessDns%s-filter", reqDns.getBusinessChannel().getId()));
			this.sendWeb(getGson().toJson(dns), "application/json; charset=utf-8", request, response);
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,String.format("datagrid-businessDns%s-filter", reqDns.getBusinessChannel().getId()));
			this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
		}
	}
	
	public void del(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		BusinessDns reqDns=getReqDns(request);
		String ids=request.getParameter("id");
		if(StringUtils.isBlank(ids)){
			return;
		}
		int state=Constants.STATE_OPERATOR_LOST;
		for(String id:ids.split(",")){
			if(StringUtils.isBlank(ids)){
				continue;
			}
			BusinessDns businessDns=(BusinessDns)getBaseService(BusinessDns.class).findById(id);
			state=getBaseService(BusinessDns.class).deleteObject(businessDns);
		}
		BusinessChannel businessChannel=(BusinessChannel) getBaseService(BusinessChannel.class).findById(reqDns.getBusinessChannel().getId());
		saveBusinessTask(businessChannel,BusinessTaskType.api,null,null,null,request);
		HttpJson json=null;
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "完成添加", true,String.format("datagrid-businessDns%s-filter", reqDns.getBusinessChannel().getId()));
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,String.format("datagrid-businessDns%s-filter", reqDns.getBusinessChannel().getId()));
		}
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
	}
	
	public void expAll(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		BusinessDns reqDns=getReqDns(request);
		String ext=request.getParameter("ext");
//		RuleService ruleService=(RuleService) getService(RuleService.class);
//		String rootPath = "build";
//		String buildPath = this.getServletContext().getRealPath(rootPath);// 生成路径
//		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
//		List<Order> orders = new ArrayList<Order>();// 排序条件
//		DetachedCriteria dc=DetachedCriteria.forClass(BusinessRule.class);
//		setExpList(expList, reqRule, request,dc);
//		String reqOrder = request.getParameter("orders");
//		if (StringUtils.isBlank(reqOrder)) {
//			orders.add(Order.desc("updatetime"));
//		} else {
//			String[] orderStrs = reqOrder.split(",");
//			for (String order : orderStrs) {
//				if (order.indexOf("asc") != -1) {
//					orders.add(Order.asc(order.replaceAll("asc", "").trim()));
//				} else if (order.indexOf("desc") != -1) {
//					orders.add(Order.desc(order.replaceAll("desc", "").trim()));
//				}
//			}
//		}
//		int total = getBaseService(BusinessRule.class).getList(expList, dc);
//		List<Object> expportList = new ArrayList<Object>();
//		Pages totalpages = new Pages();
//		totalpages.setPerPageNum(10000);// 分页查询
//		totalpages.setTotalNum(total);
//		totalpages.executeCount();
//		for (int i = 0; i < totalpages.getAllPage(); i++) {
//			Pages pages = new Pages();
//			pages.setPerPageNum(totalpages.getPerPageNum());
//			pages.setSpage(i * totalpages.getPerPageNum());// 列表开始位置
//			List<BaseRecord> list = getBaseService(BusinessRule.class).findList(
//					expList, orders, pages.getSpage(), pages.getPerPageNum(),
//					dc);
//			if (list == null || list.size() == 0) {
//				break;
//			}
//			expportList.addAll(list);
//		}
		BusinessChannel businessChannel=(BusinessChannel) getBaseService(BusinessChannel.class).findById(reqDns.getBusinessChannel().getId());
		JsonObject jsonObject=new JsonObject();
		((DnsService)getService(DnsService.class)).setJsonList(jsonObject, reqDns, request);
		BusinessTask businessTask=null;
		if(ext.equals("xlsx")){
			businessTask=saveBusinessTask(businessChannel,BusinessTaskType.exp_dns,null,jsonObject,null,request);
//			fileUrl = ruleService.creatExcelFile(expportList, rootPath, buildPath);
		}else if(ext.equals("csv")){
			businessTask=saveBusinessTask(businessChannel,BusinessTaskType.exp_dns,null,jsonObject,null,request);
//			fileUrl = ruleService.createCsvFile(expportList, rootPath, buildPath);
		}
//		request.setAttribute("fileUrl", fileUrl);
		request.setAttribute("ids", businessTask.getId());
		request.setAttribute("msg", "导出规则数据");
		request.getRequestDispatcher("/html/task/task.jsp").forward(request,response);
	}


	public void expSelected(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		DnsService dnsService=(DnsService) getService(DnsService.class);
		String ext=request.getParameter("ext");
		String expids = request.getParameter("expids");
		if (StringUtils.isBlank(expids)) {
			request.setAttribute("msg", "没有数据!");
			request.getRequestDispatcher("/html/exp/exp.jsp").forward(request,
					response);
			return;
		}
		String rootPath = "build";
		String buildPath = this.getServletContext().getRealPath(rootPath);// 生成路径
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.in("id", expids.split(",")));
		PaginatedListHelper businessDnsPlh = getBaseService(BusinessDns.class).findList(expList, orders, null, null);
		String fileUrl ="";
		if(ext.equals("xlsx")){
			fileUrl = dnsService.creatExcelFile(businessDnsPlh.getList(), rootPath, buildPath);
		}else if(ext.equals("csv")){
			fileUrl = dnsService.createCsvFile(businessDnsPlh.getList(), rootPath, buildPath);
		}
		request.setAttribute("fileUrl", fileUrl);
		request.setAttribute("msg", String.format("DNS%s数据", ""));
		request.getRequestDispatcher("/html/exp/exp.jsp").forward(request,response);
	}
	
	public void startSelected(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String ids = request.getParameter("ids");
		BusinessDns reqDns=getReqDns(request);
		HttpJson json=null;
		int state=Constants.STATE_OPERATOR_LOST;
		if (StringUtils.isBlank(ids)) {
			json = new HttpJson(HttpJson.STATE_FAIL, "请选择", false,String.format("datagrid-businessDns%s-filter", reqDns.getBusinessChannel().getId()));
			this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
		}
		for(String id:ids.split(",")){
			BusinessDns businessDns=(BusinessDns) getBaseService(BusinessDns.class).findById(id);
			businessDns.setState(BusinessDnsState.normal.getCode());
			businessDns.setUpdatetime(new Date());
			businessDns.setUpdateuserid(getManageUser(request).getId());
			state=getBaseService(BusinessDns.class).updateObject(businessDns,true);
		}
		
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "完成修改", true,String.format("datagrid-businessDns%s-filter", reqDns.getBusinessChannel().getId()));
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,String.format("datagrid-businessDns%s-filter", reqDns.getBusinessChannel().getId()));
		}
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
		
	}
	
	
	public void stopSelected(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String ids = request.getParameter("ids");
		BusinessDns reqDns=getReqDns(request);
		HttpJson json=null;
		int state=Constants.STATE_OPERATOR_LOST;
		if (StringUtils.isBlank(ids)) {
			json = new HttpJson(HttpJson.STATE_FAIL, "请选择", false,String.format("datagrid-businessDns%s-filter", reqDns.getBusinessChannel().getId()));
			this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
		}
		for(String id:ids.split(",")){
			BusinessDns businessDns=(BusinessDns) getBaseService(BusinessDns.class).findById(id);
			businessDns.setState(BusinessDnsState.stop.getCode());
			businessDns.setUpdatetime(new Date());
			businessDns.setUpdateuserid(getManageUser(request).getId());
			state=getBaseService(BusinessDns.class).updateObject(businessDns,true);
		}
		
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "完成修改", true,String.format("datagrid-businessDns%s-filter", reqDns.getBusinessChannel().getId()));
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,String.format("datagrid-businessDns%s-filter", reqDns.getBusinessChannel().getId()));
		}
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
	}
	
	
	public void initImp(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		BusinessDns reqDns=getReqDns(request);
		String code=UUID.randomUUID().toString();
		request.setAttribute("action", WebUtil.getServerUrl(request)+"/module/business/dns?action=imp&businessChannel.id="+reqDns.getBusinessChannel().getId()+"&code="+code);
		request.setAttribute("code", code);
		request.setAttribute("templeturl", WebUtil.getServerUrl(request)+"/templet/dnstemplet.xls");
		request.getRequestDispatcher("/html/imp/imp.jsp").forward(request,response);
	}
	
	public void imp(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String businessChannelId=request.getParameter("businessChannel.id");
		String code=request.getParameter("code");
		FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        HttpJson json=null;
        Map<String,Object> returnMap=new HashMap<String, Object>();
        try {
            List  items = upload.parseRequest(request);
            InputStream is = null;
            Iterator iter = items.iterator();
            String name="";
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                if (!item.isFormField()) {
                    is = item.getInputStream();
                    name=item.getName();
                }
            }
            String ext = name.substring(name.lastIndexOf("."));  
            int size=is.available();
            if (is == null
            		||size==0) {
            	json = new HttpJson(HttpJson.STATE_FAIL, "没有数据需要导入", false,String.format("datagrid-businessRule%s-filter", businessChannelId));
            	this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
				return ;
			}
        	String rootPath = "upload";
    		String uploadPath = this.getServletContext().getRealPath(rootPath);// 生成路径
    		StringBuffer filePathsb = new StringBuffer();
    		filePathsb.append(uploadPath);
    		File uploadPathFile = new File(uploadPath);
    		if (!uploadPathFile.exists()) {
    			uploadPathFile.mkdirs();
    		}
    		String uploadName =  (UUID.randomUUID() + ext).replaceAll("-", "");//生成文件名称
    		OutputStream out = null;
    		try{
    			out = new FileOutputStream(filePathsb.toString() + File.separator+ uploadName);  
    			byte[] b = new byte[10240];  
    	        int len = -1;  
    	        while((len = is.read(b))!=-1)   {  
    	           out.write(b,0,len);          
    	        }  
    		}catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(out!=null){
					out.close();
					out=null;
				}
				if(is!=null){
					is.close();
					is=null;
				}
			}
			BusinessChannel businessChannel=(BusinessChannel) getBaseService(BusinessChannel.class).findById(businessChannelId);
//			Map<String, BusinessRule> exactBusinessRuleMap=new HashMap<String, BusinessRule>();
//			Map<String, BusinessRule> fuzzyBusinessRuleMap=new HashMap<String, BusinessRule>();
//			boolean preload=false;
//			if(".xls".equals(ext)
//					 ||".xlsx".equals(ext)){  
//				if(size>2097152){
//					preload=true;
//				}
//			}else if(".zip".equals(ext)){  
//				if(size>524288){
//					preload=true;
//				}
//			}else{  
//				if(size>2097152){
//					preload=true;
//				}
//			}
//			if(preload){
//				List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
//				List<Order> orders = new ArrayList<Order>();// 排序条件
//				expList.add(Restrictions.eq("BusinessChannel.id", businessChannelId));
//				int total = getBaseService(BusinessRule.class).getList(expList, null);
//				Pages totalpages = new Pages();
//				totalpages.setPerPageNum(10000);// 分页查询
//				totalpages.setTotalNum(total);
//				totalpages.executeCount();
//				for (int i = 0; i < totalpages.getAllPage(); i++) {
//					Pages pages = new Pages();
//					pages.setPerPageNum(totalpages.getPerPageNum());
//					pages.setSpage(i * totalpages.getPerPageNum());// 列表开始位置
//					List<BaseRecord> list = getBaseService(BusinessRule.class).findList(
//							expList, orders, pages.getSpage(), pages.getPerPageNum(),null);
//					if (list == null || list.size() == 0) {
//						break;
//					}
//					for(Object o:list){
//						BusinessRule businessRule=(BusinessRule)o;
//						if(StringUtils.isNotBlank(businessRule.getExact())){
//							exactBusinessRuleMap.put(businessRule.getExact()+"_"+businessRule.getBusinessChannel().getId(), businessRule);
//						}
//						if(StringUtils.isNotBlank(businessRule.getFuzzy())){
//							if(StringUtils.isNotBlank(businessRule.getHost())){
//								fuzzyBusinessRuleMap.put(String.format("%s_%s_%s", businessRule.getHost(),businessRule.getFuzzy(),businessRule.getBusinessChannel().getId()), businessRule);
//							}else{
//								fuzzyBusinessRuleMap.put(String.format("%s_%s", businessRule.getFuzzy(),businessRule.getBusinessChannel().getId()), businessRule);
//							}
//						}
//					}
//				}
//			}
			String userId=getManageUser(request).getId();
			 if(".xls".equals(ext)
					 ||".xlsx".equals(ext)){  
				 saveBusinessTask(businessChannel,BusinessTaskType.imp_dns,filePathsb.toString() + File.separator+ uploadName,null,code,request);
				 
//				 returnMap=ruleService.saveExcel(ext, filePathsb.toString() + File.separator+ uploadName, businessChannel,
//						 userId, exactBusinessRuleMap,fuzzyBusinessRuleMap);
             }else if(".zip".equals(ext)){  
            	String zipPath =UUID.randomUUID().toString();//生成文件名称
            	File zipPathFile = new File(filePathsb.toString() + File.separator+zipPath );
        		if (!zipPathFile.exists()) {
        			zipPathFile.mkdirs();
        		}
             	ZipUtils.unZip(filePathsb.toString() + File.separator+ uploadName, filePathsb.toString()+ File.separator+zipPath);
             	List<String> fileList =new ArrayList<String>();
             	StrUtil.getFiles(filePathsb.toString()+ File.separator+zipPath, fileList);
//       	        int	totalsucc=0;
//   		        int	totalerror=0;
//   		        int	totalsame=0;
//   		        StringBuffer totalmsgsb=new StringBuffer();
//   		        List<BusinessRuleDetail> totalBusinessRuleDetailList=new ArrayList<BusinessRuleDetail>();
//   		        List<BusinessRule> totalBusinessRuleList=new ArrayList<BusinessRule>();
             	for(String s:fileList){
             		String zipext = s.substring(s.lastIndexOf("."));  
             		Map<String,Object> returnObjMap=null;
             		if(".xls".equals(zipext)
       					 ||".xlsx".equals(zipext)){  
             			saveBusinessTask(businessChannel,BusinessTaskType.imp_dns,s,null,code,request);
//             			returnObjMap=ruleService.saveExcel(zipext, s, businessChannel,
//       						 userId, exactBusinessRuleMap,fuzzyBusinessRuleMap);
             		}else if(".csv".equals(zipext)){  
             			saveBusinessTask(businessChannel,BusinessTaskType.imp_dns,s,null,code,request);
//             			returnObjMap=ruleService.saveCsv(zipext, s, businessChannel,
//          						 userId, exactBusinessRuleMap,fuzzyBusinessRuleMap);
             		}else{
             			continue;
             		}
//             		int	succ=Integer.parseInt(returnObjMap.get("succ").toString());
//					int error=Integer.parseInt(returnObjMap.get("error").toString());
//					int same=Integer.parseInt(returnObjMap.get("same").toString());
//					String msg=returnObjMap.get("msg").toString();
//					totalsucc=totalsucc+succ;
//					totalsame=totalsame+same;
//					totalerror=totalerror+error;
//					if(StringUtils.isNotBlank(msg)){
//						totalmsgsb.append(",");
//						totalmsgsb.append(msg);
//					}
//					List<BusinessRule> businessRuleList=(List<BusinessRule>) returnObjMap.get("BusinessRuleList");
//					totalBusinessRuleList.addAll(businessRuleList);
//					List<BusinessRuleDetail> businessRuleDetailList=(List<BusinessRuleDetail>) returnObjMap.get("BusinessRuleDetailList");
//					totalBusinessRuleDetailList.addAll(businessRuleDetailList);
             	}
//             	 returnMap.put("succ",totalsucc);
//        		 returnMap.put("error",totalerror);
//        		 returnMap.put("same",totalsame);
//        		 returnMap.put("msg",totalmsgsb.toString());
//        		 returnMap.put("BusinessRuleDetailList",totalBusinessRuleDetailList);
//        		 returnMap.put("BusinessRuleList",totalBusinessRuleList);
             }else if(".csv".equals(ext)){  
            	saveBusinessTask(businessChannel,BusinessTaskType.imp_dns,filePathsb.toString() + File.separator+ uploadName,null,code,request);
//            	 returnMap=ruleService.saveCsv(ext, filePathsb.toString() + File.separator+ uploadName, businessChannel,
//  						 userId, exactBusinessRuleMap,fuzzyBusinessRuleMap);
             }else{  
            	json = new HttpJson(HttpJson.STATE_FAIL, "导入出错", false,String.format("datagrid-businessRule%s-filter", businessChannelId));
             	this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
 				return ; 
             }  
//			if(!returnMap.containsKey("BusinessRuleList")){
//				json = new HttpJson(HttpJson.STATE_FAIL, "导入出错", false,String.format("datagrid-businessRule%s-filter", businessChannelId));
//             	this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
// 				return ; 
//			}
//			getBaseService(BusinessRule.class).saveObject((List<BaseRecord>)returnMap.get("BusinessRuleList"));
//			getBaseService(BusinessRuleDetail.class).saveObject((List<BaseRecord>)returnMap.get("BusinessRuleDetailList"));
         }catch (Exception e) {
            e.printStackTrace();
            json = new HttpJson(HttpJson.STATE_FAIL, "导入失败", false,String.format("datagrid-businessRule%s-filter", businessChannelId));
        	this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
			return ;
        }
//        String msg=(String) returnMap.get("msg");
//        if(msg.length()>2000){
//        	msg=msg.substring(0, 2000);
//        }
//        int succ=Integer.parseInt(returnMap.get("succ").toString());
//        int same=Integer.parseInt(returnMap.get("same").toString());
//        int error=Integer.parseInt(returnMap.get("error").toString());
//        json = new HttpJson(HttpJson.STATE_SUCCESS,
//        		String.format("成功导入%s,相似%s,失败%s,%s", succ,same,error,msg), 
//        		true,String.format("datagrid-businessRule%s-filter", businessChannelId));
        json = new HttpJson(HttpJson.STATE_EXCEPTION, "上传成功,排队处理中", false,String.format("datagrid-businessRule%s-filter", businessChannelId));
     	this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
		return ;
	}

	
	
	public void truncate(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		BusinessDns reqDns=getReqDns(request);
		int state = Constants.STATE_OPERATOR_LOST;
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		DetachedCriteria dc=DetachedCriteria.forClass(BusinessDns.class);
		((DnsService)getService(DnsService.class)).setExpList(expList, reqDns, request,dc);
//		int total = getBaseService(BusinessRule.class).getList(expList, dc);
		List<Object> deleteList = new ArrayList<Object>();
//		Pages totalpages = new Pages();
//		totalpages.setPerPageNum(10000);// 分页查询
//		totalpages.setTotalNum(total);
//		totalpages.executeCount();
//		for (int i = 0; i < totalpages.getAllPage(); i++) {
//			Pages pages = new Pages();
//			pages.setPerPageNum(totalpages.getPerPageNum());
//			pages.setSpage(i * totalpages.getPerPageNum());// 列表开始位置
//			List<BaseRecord> list = getBaseService(BusinessRule.class).findList(
//					expList, orders, pages.getSpage(),pages.getPerPageNum(), dc);
//			if (list == null || list.size() == 0) {
//				break;
//			}
//			deleteList.addAll(list);
//		}
		
		
		HttpJson json = null;
//		if (deleteList.size() == 0) {
//			json = new HttpJson(HttpJson.STATE_SUCCESS, "没有需要处理的数据!", false,String.format("datagrid-businessRule%s-filter", reqRule.getBusinessChannel().getId()));
//			this.sendWeb(new Gson().toJson(json),"application/json; charset=utf-8", request, response);
//			return;
//		}
//		for (Object o : deleteList) {
//			BusinessRule businessRule=(BusinessRule)o;
//			state = getBaseService(BusinessRule.class).deleteObject(businessRule);
//			if(businessRule.getBusinessRuleDetails()==null
//					||businessRule.getBusinessRuleDetails().size()==0){
//				continue;
//			}
//			for(Object object:businessRule.getBusinessRuleDetails()){
//				BusinessRuleDetail businessRuleDetail=(BusinessRuleDetail)object;
//				state=getBaseService(BusinessRuleDetail.class).deleteObject(businessRuleDetail);
//			}
//		}
		
		BusinessChannel businessChannel=(BusinessChannel) getBaseService(BusinessChannel.class).findById(reqDns.getBusinessChannel().getId());
		JsonObject jsonObject=new JsonObject();
		((DnsService)getService(DnsService.class)).setJsonList(jsonObject, reqDns, request);
		BusinessTask businessTask=saveBusinessTask(businessChannel,BusinessTaskType.truncate_dns,null,jsonObject,null,request);
		request.setAttribute("ids", businessTask.getId());
		request.setAttribute("msg", "清空数据");
		request.getRequestDispatcher("/html/task/task.jsp").forward(request,response);
	}
	
	
	public void reset(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		BusinessDns reqDns=getReqDns(request);
		int state = Constants.STATE_OPERATOR_LOST;
//		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
//		List<Order> orders = new ArrayList<Order>();// 排序条件
//		DetachedCriteria dc=DetachedCriteria.forClass(BusinessRule.class);
//		setExpList(expList, reqRule, request,dc);
////		int total = getBaseService(BusinessRule.class).getList(expList, dc);
//		List<Object> updateList = new ArrayList<Object>();
//		Pages totalpages = new Pages();
//		totalpages.setPerPageNum(10000);// 分页查询
//		totalpages.setTotalNum(total);
//		totalpages.executeCount();
//		for (int i = 0; i < totalpages.getAllPage(); i++) {
//			Pages pages = new Pages();
//			pages.setPerPageNum(totalpages.getPerPageNum());
//			pages.setSpage(i * totalpages.getPerPageNum());// 列表开始位置
//			List<BaseRecord> list = getBaseService(BusinessRule.class).findList(
//					expList, orders, pages.getSpage(),pages.getPerPageNum(), dc);
//			if (list == null || list.size() == 0) {
//				break;
//			}
//			updateList.addAll(list);
//		}
		HttpJson json = null;
//		if (updateList.size() == 0) {
//			json = new HttpJson(HttpJson.STATE_SUCCESS, "没有需要处理的数据!", false,String.format("datagrid-businessRule%s-filter", reqRule.getBusinessChannel().getId()));
//			this.sendWeb(new Gson().toJson(json),"application/json; charset=utf-8", request, response);
//			return;
//		}
//		for (Object o : updateList) {
//			BusinessRule businessRule=(BusinessRule)o;
//			businessRule.setNum(0);
//			businessRule.setUpdatetime(new Date());
//			state = getBaseService(BusinessRule.class).updateObject(businessRule,true);
//			if(businessRule.getBusinessRuleDetails()==null
//					||businessRule.getBusinessRuleDetails().size()==0){
//				continue;
//			}
//			for(Object object:businessRule.getBusinessRuleDetails()){
//				BusinessRuleDetail businessRuleDetail=(BusinessRuleDetail)object;
//				businessRuleDetail.setNum(0);
//				businessRuleDetail.setUpdatetime(new Date());
//				state=getBaseService(BusinessRuleDetail.class).updateObject(businessRuleDetail,true);
//			}
//		}
		BusinessChannel businessChannel=(BusinessChannel) getBaseService(BusinessChannel.class).findById(reqDns.getBusinessChannel().getId());
		JsonObject jsonObject=new JsonObject();
		((DnsService)getService(DnsService.class)).setJsonList(jsonObject, reqDns, request);
		BusinessTask businessTask=saveBusinessTask(businessChannel,BusinessTaskType.reset_dns,null,jsonObject,null,request);
		request.setAttribute("ids", businessTask.getId());
		request.setAttribute("msg", "重置数据");
		request.getRequestDispatcher("/html/task/task.jsp").forward(request,response);
	}

	
	@SuppressWarnings("unchecked")
	private BusinessDns getReqDns(HttpServletRequest request){
		BusinessDns businessDns = new BusinessDns(); 
		businessDns.setBusinessChannel(new BusinessChannel());
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
			CBeanUtils.populate(businessDns,newMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return businessDns;
	}	
	
	private Gson getGson(){
		Gson gson = new GsonBuilder().registerTypeAdapter(BusinessDns.class, new JsonSerializer<BusinessDns>() {
			public JsonElement serialize(BusinessDns src, Type typeOfSrc,
                    JsonSerializationContext context) {
                JsonObject o=new JsonObject();
                if(src.getBusinessChannel()!=null){
                	JsonObject businessObject=new JsonObject();
                	businessObject.addProperty("code", src.getBusinessChannel().getCode());
                	businessObject.addProperty("name", src.getBusinessChannel().getName());
                	o.add("BusinessChannel", businessObject);
                }
                o.addProperty("host",  src.getHost());
                o.addProperty("exact", src.getExact());
                o.addProperty("fuzzy", src.getFuzzy());
                o.addProperty("id", src.getId());
                o.addProperty("urlfilter", src.getUrlfilter());
                o.addProperty("content", src.getContent());
                o.addProperty("state", src.getState());
                o.addProperty("updatetime", DateUtil.getDateTime(src.getUpdatetime()));
                return o;
            }
        }).create();   
		return gson;
	}
	
	private BusinessTask saveBusinessTask(BusinessChannel businessChannel,
			BusinessTaskType businessTaskType,String uploadFilePath,
			JsonObject jsonObject ,String code,HttpServletRequest request){
		BusinessTask businessTask=new BusinessTask();
		if(StringUtils.isNotBlank(code)){
			businessTask.setCode(code);
		}
		businessTask.setBusinessChannel(businessChannel);
		businessTask.setCreatetime(new Date());
		businessTask.setCreateuserid(getManageUser(request).getId());
		businessTask.setUpdateuserid(businessTask.getCreateuserid());
		businessTask.setName(businessTaskType.getDisplay());
		businessTask.setState(BusinessTaskState.wait.getCode());
		businessTask.setTasktime(new Date());
		businessTask.setType(businessTaskType.getCode());
		if(StringUtils.isNotBlank(uploadFilePath)){
			businessTask.setUploadfile(uploadFilePath);
		}
		if(jsonObject!=null){
			businessTask.setContent(jsonObject.toString());
		}
		businessTask.setUpdatetime(new Date());
		getBaseService(BusinessTask.class).saveObject(businessTask);
		return businessTask;
	}

}
