package com.module.business.servlet;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;


import com.bean.BaseRecord;
import com.bean.BusinessChannel;
import com.bean.BusinessWhitelist;
import com.common.comm.Constants;
import com.common.servlet.Base;
import com.common.tools.CBeanUtils;
import com.common.tools.DateUtil;
import com.common.tools.MD5Encrypt;
import com.common.tools.StrUtil;
import com.common.tools.WebUtil;
import com.common.tools.ZipUtils;
import com.common.web.HttpJson;
import com.common.web.Page;
import com.common.web.Pages;
import com.common.web.PaginatedListHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;



public class WhitelistServlet extends Base {


	private static final long serialVersionUID = 1L;


	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		BusinessWhitelist reqWhitelist=getReqWhitelist(request);
		int page = StringUtils.isNotBlank(request.getParameter("pageCurrent"))?
				Integer.parseInt(request.getParameter("pageCurrent")):1;//当前页数
		int pageSize=StringUtils.isNotBlank(request.getParameter("pageSize"))?
				Integer.parseInt(request.getParameter("pageSize")):20;//当前页数		
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		setExpList(expList,reqWhitelist);
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
		PaginatedListHelper businessChannelPlh=getBaseService(BusinessWhitelist.class).findList(expList, orders, pages, null);
		Page<Object> data = new Page<Object>(businessChannelPlh.getList(),
				pages.getPage(), pages.getPerPageNum(), pages.getAllPage(), pages.getTotalNum());
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();   
		this.sendWeb(gson.toJson(data), "application/json; charset=utf-8", request, response);
	}
	
	public void initpage(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		BusinessWhitelist reqWhitelist=getReqWhitelist(request);
		String url="";
		if(StringUtils.isNotBlank(reqWhitelist.getId())){
			BusinessWhitelist businessWhitelist=(BusinessWhitelist) getBaseService(BusinessWhitelist.class).findById(reqWhitelist.getId());
			request.setAttribute("businessWhitelistForm", businessWhitelist);
			url="/html/module/business/whitelist/modify.jsp";
		}else{
			request.setAttribute("businessWhitelistForm", reqWhitelist);
			url="/html/module/business/whitelist/add.jsp";
		}
		request.getRequestDispatcher(url).forward(request, response);
	}
	
	public void add(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		BusinessWhitelist reqWhitelist=getReqWhitelist(request);
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.eq("account",reqWhitelist.getAccount()));
		expList.add(Restrictions.eq("ip",reqWhitelist.getIp()));
		PaginatedListHelper checkBusinessWhitelistPlh=getBaseService(BusinessWhitelist.class).findList(expList, orders, null, null);
		HttpJson json=null;
		if(checkBusinessWhitelistPlh.getList()!=null
				&&checkBusinessWhitelistPlh.getList().size()>0){
			json = new HttpJson(HttpJson.STATE_FAIL, "账号和IP已经存在", false,String.format("datagrid-businessWhitelist%s-filter", reqWhitelist.getBusinessChannel().getId()));
			this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
			return;
		}
		reqWhitelist.setCreateuserid(getManageUser(request).getId());
		reqWhitelist.setUpdateuserid(reqWhitelist.getCreateuserid());
		reqWhitelist.setCreatetime(new Date());
		reqWhitelist.setUpdatetime(reqWhitelist.getCreatetime());
		int state=getBaseService(BusinessWhitelist.class).saveObject(reqWhitelist);
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "", true,String.format("datagrid-businessWhitelist%s-filter", reqWhitelist.getBusinessChannel().getId()));
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,"");
		}
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
	}
	
	public void modify(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		BusinessWhitelist reqWhitelist=getReqWhitelist(request);
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.eq("account",reqWhitelist.getAccount()));
		expList.add(Restrictions.eq("ip",reqWhitelist.getIp()));
		expList.add(Restrictions.ne("id", reqWhitelist.getId()));
		PaginatedListHelper checkBusinessWhitelistPlh=getBaseService(BusinessWhitelist.class).findList(expList, orders, null, null);
		HttpJson json=null;
		int state=Constants.STATE_OPERATOR_LOST;
		if(checkBusinessWhitelistPlh.getList()!=null
				&&checkBusinessWhitelistPlh.getList().size()>0){
			json = new HttpJson(HttpJson.STATE_FAIL, "账号和IP已经存在", false,String.format("datagrid-businessWhitelist%s-filter", reqWhitelist.getBusinessChannel().getId()));
			this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
			return;
		}
		BusinessWhitelist whitelist=(BusinessWhitelist) getBaseService(BusinessWhitelist.class).findById(reqWhitelist.getId());
		org.springframework.beans.BeanUtils.copyProperties(reqWhitelist, whitelist, StrUtil.getNullPropertyNames(reqWhitelist));
		whitelist.setUpdateuserid(getManageUser(request).getId());
		whitelist.setUpdatetime(new Date());
		state= getBaseService(BusinessWhitelist.class).updateObject(whitelist,true);
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "", true,String.format("datagrid-businessWhitelist%s-filter", reqWhitelist.getBusinessChannel().getId()));
			this.sendWeb(new Gson().toJson(whitelist), "application/json; charset=utf-8", request, response);
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,String.format("datagrid-businessWhitelist%s-filter", reqWhitelist.getBusinessChannel().getId()));
			this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
		}
	}
	
	public void del(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		BusinessWhitelist reqWhitelist=getReqWhitelist(request);
		String ids=request.getParameter("id");
		if(StringUtils.isBlank(ids)){
			return;
		}
		int state=Constants.STATE_OPERATOR_LOST;
		for(String id:ids.split(",")){
			if(StringUtils.isBlank(ids)){
				continue;
			}
			BusinessWhitelist businessWhitelist=(BusinessWhitelist) getBaseService(BusinessWhitelist.class).findById(id);
			state= getBaseService(BusinessWhitelist.class).deleteObject(businessWhitelist);
		}
		HttpJson json=null;
		if(state==Constants.STATE_OPERATOR_SUCC){
			json = new HttpJson(HttpJson.STATE_SUCCESS, "", true,String.format("datagrid-businessWhitelist%s-filter", reqWhitelist.getBusinessChannel().getId()));
		}else{
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,String.format("datagrid-businessWhitelist%s-filter", reqWhitelist.getBusinessChannel().getId()));
		}
		this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
	}
	
	public void expAll(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		BusinessWhitelist reqWhitelist=getReqWhitelist(request);
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		DetachedCriteria dc=DetachedCriteria.forClass(BusinessWhitelist.class);
		setExpList(expList, reqWhitelist);
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
		int total = getBaseService(BusinessWhitelist.class).getList(expList, dc);
		List<Object> expportList = new ArrayList<Object>();
		Pages totalpages = new Pages();
		totalpages.setPerPageNum(10000);// 分页查询
		totalpages.setTotalNum(total);
		totalpages.executeCount();
		for (int i = 0; i < totalpages.getAllPage(); i++) {
			Pages pages = new Pages();
			pages.setPerPageNum(totalpages.getPerPageNum());
			pages.setSpage(i * totalpages.getPerPageNum());// 列表开始位置
			List<BaseRecord> list = getBaseService(BusinessWhitelist.class).findList(
					expList, orders, pages.getSpage(), pages.getPerPageNum(),dc);
			if (list == null || list.size() == 0) {
				break;
			}
			expportList.addAll(list);
		}
		String fileUrl = createExpFile(expportList);
		request.setAttribute("fileUrl", fileUrl);
		request.setAttribute("msg", String.format("白名单%s数据", ""));
		request.getRequestDispatcher("/html/exp/exp.jsp").forward(request,response);
	}


	public void expSelected(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String expids = request.getParameter("expids");
		if (StringUtils.isBlank(expids)) {
			request.setAttribute("msg", "没有数据!");
			request.getRequestDispatcher("/html/exp/exp.jsp").forward(request,response);
			return;
		}
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.in("id", expids.split(",")));
		PaginatedListHelper businessWhitelistPlh = getBaseService(BusinessWhitelist.class).findList(expList, orders, null, null);
		String fileUrl = createExpFile(businessWhitelistPlh.getList());
		request.setAttribute("fileUrl", fileUrl);
		request.setAttribute("msg", String.format("白名单%s数据", ""));
		request.getRequestDispatcher("/html/exp/exp.jsp").forward(request,response);
	}

	public void initImp(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		BusinessWhitelist reqWhitelist=getReqWhitelist(request);
		request.setAttribute("action", WebUtil.getServerUrl(request)+"/module/business/whitelist?action=imp&businessChannel.id="+reqWhitelist.getBusinessChannel().getId());
		request.setAttribute("templeturl", WebUtil.getServerUrl(request)+"/templet/whitelisttemplet.xls");
		request.getRequestDispatcher("/html/imp/imp.jsp").forward(request,response);
	}

	public void imp(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		String businessChannelId=request.getParameter("businessChannel.id");
		FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        HttpJson json=null;
        int succ = 0,error = 0,same=0;
        StringBuffer sb=new StringBuffer();
        try {
            List  items = upload.parseRequest(request);
            InputStream is = null;
            Iterator iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                if (!item.isFormField()) {
                    is = item.getInputStream();
                }
            }
            if (is == null
            		||is.available()==0) {
            	json = new HttpJson(HttpJson.STATE_FAIL, "没有数据需要导入", false,String.format("datagrid-businessWhitelist%s-filter", businessChannelId));
            	this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
				return ;
			}
            Workbook workbook = Workbook.getWorkbook(is);
            Sheet sheet = workbook.getSheet(0);
            if (sheet == null) {
            	json = new HttpJson(HttpJson.STATE_FAIL, "没有数据需要导入", false,String.format("datagrid-businessWhitelist%s-filter", businessChannelId));
            	this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
				return ;
			}
            BusinessChannel businessChannel=(BusinessChannel) getBaseService(BusinessChannel.class).findById(businessChannelId);
			List<String> headlist = new ArrayList<String>();
			int rowNum = sheet.getRows();// 第一张sheet的总行数
			int columnNum=sheet.getColumns();
			Cell[] cells = sheet.getRow(0);// 第一张sheet的第一行
			if (cells != null && cells.length > 0) {
				for (int i = 0; i < cells.length; i++) { // 对每个单元格进行循环
					headlist.add(cells[i].getContents().trim());// 读取第一张Sheet的第一行的当前单元格的值
				}
			}
			for (int k = 1; k < rowNum; k++) { // 循环第一张sheet的所有行,从第二行开始
				cells = sheet.getRow(k);// 得到当前行的所有单元格
				if (cells == null || cells.length == 0) {	
					continue;
				}
				BusinessWhitelist businessWhitelist=new BusinessWhitelist();
				for (int j = 0; j < headlist.size(); j++) { // 对每个单元格进行循环
					String value = (String) headlist.get(j);// 读取当前单元格的值
					String content = "";
					try {
						if (cells.length < (j + 1)) {
							continue;
						}
						if (cells[j] == null) {
							continue;
						}
						content = cells[j].getContents().trim();
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
					if (value.equals("账号")) {
						businessWhitelist.setAccount(content);
					}
					if (value.equalsIgnoreCase("ip")) {
						businessWhitelist.setIp(content);
					}
				}
				
				if(StringUtils.isBlank(businessWhitelist.getAccount())
						&&StringUtils.isBlank(businessWhitelist.getIp())){
					error++;
				}else{
					if(StringUtils.isBlank(businessWhitelist.getAccount())){
						businessWhitelist.setAccount("12345678");
					}
					if(StringUtils.isBlank(businessWhitelist.getIp())){
						businessWhitelist.setIp("127.0.0.1");
					}
					businessWhitelist.setBusinessChannel(businessChannel);
					businessWhitelist.setCreateuserid(getManageUser(request).getId());
					businessWhitelist.setUpdateuserid(businessWhitelist.getCreateuserid());
					businessWhitelist.setCreatetime(new Date());
					businessWhitelist.setUpdatetime(businessWhitelist.getCreatetime());
					int state= getBaseService(BusinessWhitelist.class).saveObject(businessWhitelist);
					if(state==Constants.STATE_OPERATOR_SUCC){
						succ++;
					}else{
						error++;
					}
				}
			}
         }catch (Exception e) {
            e.printStackTrace();
            json = new HttpJson(HttpJson.STATE_FAIL, "导入失败", false,String.format("datagrid-businessWhitelist%s-filter", businessChannelId));
        	this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
			return ;
        }
        json = new HttpJson(HttpJson.STATE_SUCCESS, String.format("成功导入%s,相似%s,失败%s,%s", succ,same,error,sb.toString()), true,String.format("datagrid-businessWhitelist%s-filter", businessChannelId));
     	this.sendWeb(new Gson().toJson(json), "application/json; charset=utf-8", request, response);
		return ;
	}
	
	public void truncate(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		BusinessWhitelist reqWhitelist=getReqWhitelist(request);
		int state = Constants.STATE_OPERATOR_LOST;
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		setExpList(expList, reqWhitelist);
		for (;;) {
			PaginatedListHelper businessWhitelistPlh=getBaseService(BusinessWhitelist.class).findList(expList, orders, null, null);
			if(businessWhitelistPlh.getList()==null
					||businessWhitelistPlh.getList().size()==0){
				break;
			}
			for (Object o : businessWhitelistPlh.getList()) {
				state=getBaseService(BusinessWhitelist.class).deleteObject((BaseRecord)o);
			}
		}
		HttpJson json = null;
		if (state == Constants.STATE_OPERATOR_SUCC) {
			json = new HttpJson(HttpJson.STATE_SUCCESS, "", true,String.format("datagrid-businessWhitelist%s-filter", reqWhitelist.getBusinessChannel().getId()));
		} else {
			json = new HttpJson(HttpJson.STATE_FAIL, "系统异常", false,String.format("datagrid-businessWhitelist%s-filter", reqWhitelist.getBusinessChannel().getId()));
		}
		this.sendWeb(new Gson().toJson(json),"application/json; charset=utf-8", request, response);
	}
	
	
	private void setExpList(List<Criterion> expList,BusinessWhitelist reqWhitelist){
		if(StringUtils.isNotBlank(reqWhitelist.getBusinessChannel().getId())){
			expList.add(Restrictions.eq("BusinessChannel.id", reqWhitelist.getBusinessChannel().getId()));
		}
		if(StringUtils.isNotBlank(reqWhitelist.getAccount())){
			expList.add(Restrictions.like("account", reqWhitelist.getAccount(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqWhitelist.getIp())){
			expList.add(Restrictions.like("ip", reqWhitelist.getIp(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqWhitelist.getState())){
			expList.add(Restrictions.eq("state", reqWhitelist.getState()));
		}
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
		WritableWorkbook wwb = null;
		String expName =  UUID.randomUUID() + ".xls";//生成文件名称
		try {
			wwb = Workbook.createWorkbook(new File(buildPath + File.separator+ expName));
			WritableSheet ws = wwb.createSheet("白名单信息", 0);
			int j=0;
			Label labelItem = new Label(++j-1, 0, "账号");
			ws.addCell(labelItem);
			labelItem = new Label(++j-1, 0, "IP");
			ws.addCell(labelItem);
			labelItem = new Label(++j-1, 0, "备注");
			ws.addCell(labelItem);
			labelItem = new Label(++j-1, 0, "更新时间");
			ws.addCell(labelItem);
			for (int i = 0; i < expportList.size(); i++) {
				BusinessWhitelist businessWhitelist = (BusinessWhitelist) expportList.get(i);
				j=0;
				labelItem = new Label(++j-1, i + 1, businessWhitelist.getAccount());
				ws.addCell(labelItem);
				labelItem = new Label(++j-1, i + 1, businessWhitelist.getIp());
				ws.addCell(labelItem);
				labelItem = new Label(++j-1, i + 1, businessWhitelist.getRemark());
				ws.addCell(labelItem);
				labelItem = new Label(++j-1, i + 1, DateUtil.getDateTime(businessWhitelist.getUpdatetime()));
				ws.addCell(labelItem);
			}
			wwb.write();
			filePathsb.append("/");
			filePathsb.append(expName);
		}  catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(wwb!=null){
					wwb.close();
				}
			}  catch (Exception e) {
				e.printStackTrace();
			}
		}
		return filePathsb.toString();
	}

	
	@SuppressWarnings("unchecked")
	private BusinessWhitelist getReqWhitelist(HttpServletRequest request){
		BusinessWhitelist whitelist = new BusinessWhitelist(); 
		whitelist.setBusinessChannel(new BusinessChannel());
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
			CBeanUtils.populate(whitelist,newMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return whitelist;
	}

}
