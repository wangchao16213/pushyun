package com.module.business.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.bean.BaseConfig;
import com.bean.BaseRecord;
import com.bean.BusinessChannel;
import com.bean.BusinessDns;
import com.bean.BusinessTask;
import com.common.config.SpringConfig;
import com.common.service.BaseService;
import com.common.task.RuleExpTask;
import com.common.tools.DateUtil;
import com.common.tools.StrUtil;
import com.common.type.BaseConfigCode;
import com.common.type.BusinessDnsState;
import com.common.type.BusinessTaskState;
import com.common.web.Pages;
import com.common.web.PaginatedListHelper;
import com.google.gson.JsonObject;
import com.module.business.service.DnsService;

public class DnsServiceImpl implements DnsService{
	
	private final Logger logger = Logger.getLogger(DnsServiceImpl.class.getName());

	@Override
	public Map<String,Object> saveExcel(String ext, String filePath,
			BusinessChannel businessChannel,String userId,boolean preload,Map<String,BusinessDns> hostBusinessDnsMap,
			BusinessTask businessTask) {
		 BaseService businessChannelService=(BaseService) SpringConfig.getInstance().getService(BusinessChannel.class);
		 BaseService businessTaskService=(BaseService) SpringConfig.getInstance().getService(BusinessTask.class);
		 Workbook workbook =null;
		 Map<String,Object> returnMap=new HashMap<String, Object>();
		 returnMap.put("succ",0);
		 returnMap.put("error",0);
	     returnMap.put("same",0);
	     returnMap.put("msg","");
	     returnMap.put("BusinessDnsList",new ArrayList<BusinessDns>());
	     FileInputStream in=null;
         try {
             if(".xls".equals(ext)){  
            	in=new FileInputStream(filePath);
             	workbook = new HSSFWorkbook(in);  
             }else if(".xlsx".equals(ext)){  
            	in=new FileInputStream(filePath);
             	workbook = new XSSFWorkbook(in);  
             }else{  
             	return returnMap;
             }  
             if(workbook==null){
     			return returnMap;
              }
             Sheet sheet = workbook.getSheetAt(0);
             if (sheet == null) {
             	 return returnMap;
     		}
             List<String> headlist = new ArrayList<String>();
     		int rowNum = sheet.getLastRowNum()+1;// 第一张sheet的总行数
     		Row row=sheet.getRow(0);
     		int columnNum=row.getPhysicalNumberOfCells();  
     	    for (int i = 0; i < columnNum; i++) {  
     	       headlist.add(getCellFormatValue(row.getCell(i)).toString());
     	    }  
     	    int	totalsucc=0;
     	    int	totalerror=0;
     	    int	totalsame=0;
     	    StringBuffer totalmsgsb=new StringBuffer();
     	    List<BusinessDns> totalBusinessRuleList=new ArrayList<BusinessDns>();
     		for (int k = 1; k < rowNum; k++) { // 循环第一张sheet的所有行,从第二行开始
     			if((totalsucc+totalerror+totalsame)%100==0){
     				businessTask =(BusinessTask) businessTaskService.findById(businessTask.getId());
     				if(!businessTask.getState().equals(BusinessTaskState.run.getCode())){
     					return returnMap;
     				}
     			}
     			row = sheet.getRow(k);  // 得到当前行的所有单元格
     			BusinessDns businessDns = new BusinessDns();
     			BusinessChannel channel = new BusinessChannel();
     			String urlaccord="";
     			for (int j = 0; j < headlist.size(); j++) { // 对每个单元格进行循环
     				String value = (String) headlist.get(j);// 读取当前单元格的值
     				String content = "";
     				try {
     					Cell cell =row.getCell(j);
     					if(cell!=null){
     						cell.setCellType(CellType.STRING);
     					}
     					content=getCellFormatValue(cell).toString().trim();  
     				} catch (Exception e) {
     					e.printStackTrace();
     					continue;
     				}
     				if (value.equals("渠道编码")) {
     					channel.setCode(content);
     				}
     				if (value.equals("域名")
     						||value.equals("主机地址")) {
     					businessDns.setHost(content);
     				}
     				if (value.equals("备注")) {
     					businessDns.setRemark(content);
     				}
     				if (value.equals("状态")) {
     					if(StringUtils.isBlank(content)){
     						businessDns.setState(BusinessDnsState.normal.getCode());
     					}else{
     						businessDns.setState(StrUtil.getCode(
     								BusinessDnsState.class.getName(), content));
     					}
     				}
     				if (value.equals("推送内容")) {
     					businessDns.setContent(content);
 					}
     			}
     			if(businessChannel==null){
     				if (StringUtils.isNotBlank(channel.getCode())){
     					businessChannel=(BusinessChannel) businessChannelService.findByProperty("code", channel.getCode());
     				}
     			}
     			Map<String,Object> returnObjMap=getBusinessDns(businessDns,businessChannel,userId, preload,hostBusinessDnsMap);
     			int	succ=Integer.parseInt(returnObjMap.get("succ").toString());
     			int error=Integer.parseInt(returnObjMap.get("error").toString());
     			int same=Integer.parseInt(returnObjMap.get("same").toString());
     			String msg=returnObjMap.get("msg").toString();
     			totalsucc=totalsucc+succ;
     			totalsame=totalsame+same;
     			totalerror=totalerror+error;
     			if(StringUtils.isNotBlank(msg)){
     				totalmsgsb.append(",");
     				totalmsgsb.append(msg);
     			}
     			if(error==1
     					||same==1
     					||succ==0){
     				continue;
     			}
     			BusinessDns dns=(BusinessDns) returnObjMap.get("BusinessDns");
     			totalBusinessRuleList.add(dns);
     		}
     		returnMap.put("succ",totalsucc);
    		returnMap.put("error",totalerror);
    		returnMap.put("same",totalsame);
    		returnMap.put("msg",totalmsgsb.toString());
    		returnMap.put("BusinessDnsList",totalBusinessRuleList);
         } catch (Exception e) {  
         	e.printStackTrace();
         }  finally{
			if(workbook!=null){
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				workbook=null;
			}
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				in=null;
			}
		}
		return returnMap;
	}
	
	
	public Map<String,Object> saveCsv(String ext, String filePath,
			BusinessChannel businessChannel,String userId,boolean preload,Map<String,BusinessDns> hostBusinessDnsMap,
			BusinessTask businessTask) {
		 BaseService businessChannelService=(BaseService) SpringConfig.getInstance().getService(BusinessChannel.class);
		 BaseService businessTaskService=(BaseService) SpringConfig.getInstance().getService(BusinessTask.class);
		 Map<String,Object> returnMap=new HashMap<String, Object>();
		 returnMap.put("succ",0);
		 returnMap.put("error",0);
	     returnMap.put("same",0);
	     returnMap.put("msg","");
	     returnMap.put("BusinessRuleList",new ArrayList<BusinessDns>());
	     int	totalsucc=0;
		  int	totalerror=0;
		  int	totalsame=0;
		  StringBuffer totalmsgsb=new StringBuffer();
		  List<BusinessDns> totalBusinessDnsList=new ArrayList<BusinessDns>();
		  Reader reader =null;
		  CSVReader csvReader=null;
		  FileInputStream in=null;
		  BOMInputStream bs =null;
		  try{
			  in= new FileInputStream(filePath);
			  bs=new BOMInputStream(in);
			  reader = new InputStreamReader(bs, "GB2312");  
			  csvReader = new CSVReader(reader);  
			  String[] headers = csvReader.readNext();  
			  if(headers == null 
					  || headers.length == 0){  
				  return returnMap;
			  }
			  List<String[]> list = csvReader.readAll();  
			  for(String[] line : list){  
				  if((totalsucc+totalerror+totalsame)%100==0){
						businessTask =(BusinessTask) businessTaskService.findById(businessTask.getId());
						if(!businessTask.getState().equals(BusinessTaskState.run.getCode())){
							return returnMap;
						}
					}
				  BusinessDns businessDns = new BusinessDns();
				  BusinessChannel channel = new BusinessChannel();
				  String urlaccord="";
				  for(int i=0;i<line.length;i++){
					  if (headers[i].equals("渠道编码")) {
							channel.setCode(line[i]);
						}
						if (headers[i].equals("域名")
								||headers[i].equals("主机地址")) {
							businessDns.setHost(line[i]);
						}
						if (headers[i].equals("备注")) {
							businessDns.setRemark(line[i]);
						}
						if (headers[i].equals("状态")) {
							if(StringUtils.isBlank(line[i])){
								businessDns.setState(BusinessDnsState.normal.getCode());
							}else{
								businessDns.setState(StrUtil.getCode(
										BusinessDnsState.class.getName(), line[i]));
							}
							
						}
						if (headers[i].equals("推送内容")) {
							businessDns.setContent(line[i]);
						}
				 	}
					if(businessChannel==null){
						if (StringUtils.isNotBlank(channel.getCode())){
							businessChannel=(BusinessChannel) businessChannelService.findByProperty("code", channel.getCode());
						}
					}
					Map<String,Object> returnObjMap=getBusinessDns(businessDns,businessChannel,userId,preload,hostBusinessDnsMap);
					int	succ=Integer.parseInt(returnObjMap.get("succ").toString());
					int error=Integer.parseInt(returnObjMap.get("error").toString());
					int same=Integer.parseInt(returnObjMap.get("same").toString());
					String msg=returnObjMap.get("msg").toString();
					totalsucc=totalsucc+succ;
					totalsame=totalsame+same;
					totalerror=totalerror+error;
					if(StringUtils.isNotBlank(msg)){
						totalmsgsb.append(",");
						totalmsgsb.append(msg);
					}
					if(error==1
							||same==1
							||succ==0){
						continue;
					}
					BusinessDns dns=(BusinessDns) returnObjMap.get("BusinessDns");
					totalBusinessDnsList.add(dns);
		      }  
		  }catch (Exception e) {
			e.printStackTrace();
		  }finally{
			  if(csvReader!=null){
				  try {
					csvReader.close();
					csvReader=null;
				} catch (IOException e) {
					e.printStackTrace();
				}  
			  }
			  if(reader!=null){
				  try {
					  reader.close();
					  reader=null;
				} catch (IOException e) {
					e.printStackTrace();
				}  
			  }
			  if(in!=null){
				  try {
					  in.close();
					  in=null;
				} catch (IOException e) {
					e.printStackTrace();
				}  
			  }
			  if(bs!=null){
				  try {
					  bs.close();
					  bs=null;
				} catch (IOException e) {
					e.printStackTrace();
				}  
			  }
		  }
	     returnMap.put("succ",totalsucc);
		 returnMap.put("error",totalerror);
		 returnMap.put("same",totalsame);
		 returnMap.put("msg",totalmsgsb.toString());
		 return returnMap;
	}
	
	
	private Map<String,Object> getBusinessDns(
			BusinessDns businessDns,BusinessChannel businessChannel,String userId,boolean preload,
			Map<String,BusinessDns> hostBusinessRuleMap){
		BaseService businessDnsService=(BaseService) SpringConfig.getInstance().getService(BusinessDns.class);
		Map<String,Object>  returnMap=new HashMap<String, Object>();
		returnMap.put("succ",0);
		returnMap.put("error",0);
		returnMap.put("same",0);
		returnMap.put("msg","");
		if(businessChannel==null){
			return returnMap;
		}
		if(!preload){
			if (StringUtils.isNotBlank(businessDns.getHost())){
				List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
				List<Order> orders = new ArrayList<Order>();// 排序条件
				expList.add(Restrictions.eq("BusinessChannel.id", businessChannel.getId()));
				expList.add(Restrictions.eq("host", businessDns.getHost()));
				List<BaseRecord> checkBusinessDnsList=businessDnsService.findList(expList, orders, 0, 1, null);
				if(checkBusinessDnsList!=null
						&&checkBusinessDnsList.size()>0){
					returnMap.put("same",1);
					returnMap.put("msg",businessDns.getHost());
					return returnMap;
				}
			}
			
		}else{
			if (StringUtils.isNotBlank(businessDns.getHost())){
				String key=String.format("%s_%s", businessDns.getHost(),businessChannel.getId());
				if(hostBusinessRuleMap.containsKey(key)){
					returnMap.put("same",1);
					returnMap.put("msg",businessDns.getHost());
					return returnMap;
				}else{
					hostBusinessRuleMap.put(key, businessDns);
				}
			}
		}
		businessDns.setBusinessChannel(businessChannel);
		businessDns.setCreateuserid(userId);
		businessDns.setUpdateuserid(businessDns.getCreateuserid());
		businessDns.setCreatetime(new Date());
		businessDns.setUpdatetime(businessDns.getCreatetime());
		returnMap.put("BusinessDns", businessDns);
		returnMap.put("succ",1);
		return returnMap;
	}
	
	@Override
	public String creatExcelFile(List<Object> expportList,String rootPath,String buildPath) {
		StringBuffer filePathsb = new StringBuffer();
		filePathsb.append(rootPath);
		File buildPathFile = new File(buildPath);
		if (!buildPathFile.exists()) {
			buildPathFile.mkdirs();
		}
		XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("DNS数据");
        XSSFRow row = sheet.createRow(0);
        int i=0;
		
        XSSFCell cell = row.createCell(++i-1);
        cell.setCellValue("渠道编码");
        cell = row.createCell(++i-1);
        cell.setCellValue("渠道名称");
        cell = row.createCell(++i-1);
        cell.setCellValue("域名");
        cell = row.createCell(++i-1);
	     cell.setCellValue("推送内容"); 
		cell = row.createCell(++i-1);
        cell.setCellValue("备注");
        cell = row.createCell(++i-1);
        cell.setCellValue("状态");
        cell = row.createCell(++i-1);
        cell.setCellValue("更新时间");
        FileOutputStream outputStream = null;
		String expName =  (UUID.randomUUID() + ".xlsx").replaceAll("-", "");//生成文件名称
		try {
			outputStream = new FileOutputStream(buildPath + File.separator+ expName);
			filePathsb.append("/");
			filePathsb.append(expName);
			for(int h=0;h<expportList.size();h++){
				i=0;
				row = sheet.createRow(h+1);
				BusinessDns businessDns = (BusinessDns)expportList.get(h);
				cell = row.createCell(++i-1);
			    cell.setCellValue( businessDns.getBusinessChannel().getCode());
			    cell = row.createCell(++i-1);
			    cell.setCellValue( businessDns.getBusinessChannel().getName());
			    cell = row.createCell(++i-1);
			    cell.setCellValue(businessDns.getHost());
			    cell = row.createCell(++i-1);
				cell.setCellValue( businessDns.getContent());
				cell = row.createCell(++i-1);
				cell.setCellValue(businessDns.getRemark());
				cell = row.createCell(++i-1);
				cell.setCellValue(StrUtil.getDisplay(BusinessDnsState.class.getName(), businessDns.getState()));
				cell = row.createCell(++i-1);
				cell.setCellValue(DateUtil.getDateTime(businessDns.getUpdatetime()));
			}
			wb.write(outputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if (wb != null) {
					wb.close();
					wb=null;
				}
				if (outputStream != null) {
					outputStream.close();
					outputStream=null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return filePathsb.toString();
	}
	
	
	@Override
	public String createCsvFile(List<Object> expportList,String rootPath,String buildPath) {
		StringBuffer filePathsb = new StringBuffer();
		filePathsb.append(rootPath);
		File buildPathFile = new File(buildPath);
		if (!buildPathFile.exists()) {
			buildPathFile.mkdirs();
		}
		Workbook wwb = null;
		Sheet ws=null;
		String expName =  (UUID.randomUUID() + ".csv").replaceAll("-", "");//生成文件名称
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
		int maxrule=0;
		for (Object o : expportList) {
			BusinessDns businessRule = (BusinessDns) o;
			if(businessRule.getBusinessRuleDetails()==null
					||businessRule.getBusinessRuleDetails().size()==0){
				continue;
			}
			if(maxrule==0){
				maxrule=businessRule.getBusinessRuleDetails().size();
			}else if(businessRule.getBusinessRuleDetails().size()>maxrule){
				maxrule=businessRule.getBusinessRuleDetails().size();
			}
		}
		int count=10+maxrule*4;
		int i=0;
		try {
			String[] headers = new String[count];
			headers[++i-1] = "渠道编码";
			headers[++i-1] = "渠道名称";
			headers[++i-1] = "域名";
			headers[++i-1] = "推送内容";
			headers[++i-1] = "备注";
			headers[++i-1] = "状态";
			headers[++i-1] = "更新时间";
			writer.writeNext(headers);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			List<String[]> writeList = new ArrayList<String[]>();
			for (Object o : expportList) {
				i=0;
				BusinessDns businessDns = (BusinessDns)o;
				String[] contents = new String[count];
				for(int j=0;j<count;j++){
					contents[j]="";
				}
				contents[++i-1] = businessDns.getBusinessChannel().getCode();
				contents[++i-1] = businessDns.getBusinessChannel().getName();
				contents[++i-1] = businessDns.getHost();
				contents[++i-1] = businessDns.getContent();
				contents[++i-1] = businessDns.getRemark();
				contents[++i-1] = StrUtil.getDisplay(BusinessDnsState.class.getName(), businessDns.getState());
				contents[++i-1] = DateUtil.getDateTime(businessDns.getUpdatetime());
				writeList.add(contents);
			}
			writer.writeAll(writeList);
			writeList.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if (wwb != null) {
					wwb.close();
					wwb=null;
				}
				if (ws != null) {
					ws=null;
				}
				if (outputStreamWriter != null) {
					outputStreamWriter.close();
					outputStreamWriter=null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return filePathsb.toString();
	}
	
	public String createApiFile(String txt,String rootPath,String buildPath) {
		StringBuffer filePathsb = new StringBuffer();
		filePathsb.append(rootPath);
		File buildPathFile = new File(buildPath);
		if (!buildPathFile.exists()) {
			buildPathFile.mkdirs();
		}
		String expName =  (UUID.randomUUID() + ".txt").replaceAll("-", "");//生成文件名称
	 	filePathsb.append(File.separator);
		filePathsb.append(expName);
		FileWriter fw =null; 
		BufferedWriter bw=null;
		try {
			fw = new FileWriter(new File(buildPath + File.separator+ expName), true);
		    bw = new BufferedWriter(fw);
		    bw.write(txt);
		    bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(fw!=null){
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fw=null;
			}
			if(bw!=null){
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				bw=null;
			}
		}
		return filePathsb.toString();
	}
	
	@Override
	public String mergeFile(String rootPath,String buildPath,String filePath) {
		List<String> fileList =new ArrayList<String>();
		StrUtil.getFiles(filePath, fileList);
		if(fileList.size()==0){
			return "";
		}
		StringBuffer sb=new StringBuffer();
		for(String s:fileList){
			sb.append(StrUtil.readTxtFile(s));
		}
		return createApiFile(sb.toString(), rootPath, buildPath);
	}
	
	@Override
	public List<Object> getExpportList(BusinessChannel businessChannel){
		BaseService businessDnsService=(BaseService) SpringConfig.getInstance().getService(BusinessDns.class);
		BaseService baseConfigService=(BaseService) SpringConfig.getInstance().getService(BaseConfig.class);
		BaseService businessTaskService=(BaseService) SpringConfig.getInstance().getService(BusinessTask.class);
		BaseConfig baseConfig=(BaseConfig) baseConfigService.findByProperty("code", BaseConfigCode.expperpagenum.getCode());
		List<Object> expportList = new ArrayList<Object>();
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.eq("BusinessChannel.id", businessChannel.getId()));
		expList.add(Restrictions.eq("state", BusinessDnsState.normal.getCode()));
		int total = businessDnsService.getList(expList, null);
		if(total==0){
			return expportList;
		}
		Pages totalpages = new Pages();
		totalpages.setPerPageNum(Integer.parseInt(baseConfig.getValue()));// 分页查询
		totalpages.setTotalNum(total);
		totalpages.executeCount();
		for (int i = 0; i < totalpages.getAllPage(); i++) {
			Pages pages = new Pages();
			pages.setPerPageNum(totalpages.getPerPageNum());
			pages.setSpage(i * totalpages.getPerPageNum());// 列表开始位置
			List<BaseRecord> list = businessDnsService.findList(
					expList, orders, pages.getSpage(), pages.getPerPageNum(),null);
			if (list == null || list.size() == 0) {
				break;
			}
			expportList.addAll(list);
		}
		return expportList;
	}
	
	@Override
	public List<Object> getExpportListFromFile(String filePath){
		BaseService businessDnsService=(BaseService) SpringConfig.getInstance().getService(BusinessDns.class);
		List<Object> expportList = new ArrayList<Object>();
	    String result=StrUtil.readTxtFile(filePath);
	    if(StringUtils.isBlank(result)){
	    	 return expportList;
	    }
	   String[] ids= result.split(",");
	   Date startDate=new Date();
	    for(int i=0;i<ids.length;i++){
	    	String id=ids[i];
	    	 if(StringUtils.isBlank(id)){
	    		   continue;
	    	   }
	    	 if(i%1000==0){
	    		 logger.info(String.format("expportListFromFile:%s,end:%s", 
	    					DateUtil.getDateTime(startDate, "HH:mm:ss"),DateUtil.getDateTime(new Date(), "HH:mm:ss")));
	    	 }
	    	  BusinessDns businessDns= (BusinessDns) businessDnsService.findById(id);
	    	   if(businessDns==null){
	    		   continue;
	    	   }
	    	   expportList.add(businessDns);
	    }
       return expportList;
	}
	
	@Override
	public void setJsonList(JsonObject jsonObject,BusinessDns reqDns,HttpServletRequest request){
		if(StringUtils.isNotBlank(reqDns.getExact())){
			jsonObject.addProperty("exact", reqDns.getExact());
		}
		if(StringUtils.isNotBlank(reqDns.getFuzzy())){
			jsonObject.addProperty("fuzzy", reqDns.getFuzzy());
		}
		String exactfuzzy = request.getParameter("exact|fuzzy");
		if(StringUtils.isNotBlank(exactfuzzy)){
			jsonObject.addProperty("exactfuzzy", exactfuzzy);
		}
		if(StringUtils.isNotBlank(reqDns.getState())){
			jsonObject.addProperty("state", reqDns.getState());
		}
		if(StringUtils.isNotBlank(reqDns.getHost())){
			jsonObject.addProperty("host", reqDns.getHost());
		}
		if(StringUtils.isNotBlank(reqDns.getUrlfilter())){
			jsonObject.addProperty("urlfilter", reqDns.getUrlfilter());
		}
		if(reqDns.getPushrate()!=null
				&&reqDns.getPushrate()!=-1){
			jsonObject.addProperty("pushrate", reqDns.getPushrate());
		}
		if(StringUtils.isNotBlank(reqDns.getBusinessChannel().getId())){
			jsonObject.addProperty("BusinessChannel.id", reqDns.getBusinessChannel().getId());
		} 
	}
	
	@Override
	public void setExpList(List<Criterion> expList,BusinessDns reqDns,
			HttpServletRequest request,DetachedCriteria dc){
		if(StringUtils.isNotBlank(reqDns.getExact())){
			expList.add(Restrictions.like("exact", reqDns.getExact(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqDns.getFuzzy())){
			expList.add(Restrictions.like("fuzzy", reqDns.getFuzzy(),MatchMode.ANYWHERE));
		}
		String exactfuzzy = request.getParameter("exact|fuzzy");
		if(StringUtils.isNotBlank(exactfuzzy)){
			expList.add(Restrictions.or(
					Restrictions.like("fuzzy", exactfuzzy,MatchMode.ANYWHERE),
							Restrictions.like("exact", exactfuzzy,MatchMode.ANYWHERE)));
		}
		if(StringUtils.isNotBlank(reqDns.getState())){
			expList.add(Restrictions.eq("state", reqDns.getState()));
		}
		if(StringUtils.isNotBlank(reqDns.getHost())){
			expList.add(Restrictions.like("host", reqDns.getHost(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqDns.getUrlfilter())){
			expList.add(Restrictions.like("urlfilter", reqDns.getUrlfilter(),MatchMode.ANYWHERE));
		}
		if(reqDns.getPushrate()!=null
				&&reqDns.getPushrate()!=-1){
			expList.add(Restrictions.eq("pushrate", reqDns.getPushrate()));
		}
		if(StringUtils.isNotBlank(reqDns.getBusinessChannel().getId())){
			expList.add(Restrictions.eq("BusinessChannel.id", reqDns.getBusinessChannel().getId()));
		} 
	}
	
	@Override
	public void setExpList(List<Criterion> expList,JsonObject jsonObject,DetachedCriteria dc){
		if(jsonObject.has("exact")){
			expList.add(Restrictions.like("exact", jsonObject.get("exact").getAsString(),MatchMode.ANYWHERE));
		}
		if(jsonObject.has("fuzzy")){
			expList.add(Restrictions.like("fuzzy", jsonObject.get("fuzzy").getAsString(),MatchMode.ANYWHERE));
		}
		if(jsonObject.has("exactfuzzy")){
			expList.add(Restrictions.or(
					Restrictions.like("fuzzy", jsonObject.get("exactfuzzy").getAsString(),MatchMode.ANYWHERE),
							Restrictions.like("exact", jsonObject.get("exactfuzzy").getAsString(),MatchMode.ANYWHERE)));
		}
		if(jsonObject.has("state")){
			expList.add(Restrictions.eq("state", jsonObject.get("state").getAsString()));
		}
		if(jsonObject.has("host")){
			expList.add(Restrictions.like("host",jsonObject.get("host").getAsString(),MatchMode.ANYWHERE));
		}
		if(jsonObject.has("urlfilter")){
			expList.add(Restrictions.like("urlfilter", jsonObject.get("urlfilter").getAsString(),MatchMode.ANYWHERE));
		}
		if(jsonObject.has("pushrate")){
			expList.add(Restrictions.eq("pushrate", jsonObject.get("pushrate").getAsInt()));
		}
		if(jsonObject.has("BusinessChannel.id")){
			expList.add(Restrictions.eq("BusinessChannel.id", jsonObject.get("BusinessChannel.id").getAsString()));
		} 
	}
	
	
	   /** 
     *  
     * 根据Cell类型设置数据 
     *  
     * @param cell 
     * @return 
     * @author zengwendong 
     */  
    private Object getCellFormatValue(Cell cell) {  
        Object cellvalue = "";  
        if (cell != null) {  
            // 判断当前Cell的Type  
        	 if(cell.getCellTypeEnum() == CellType.STRING){
        		 cellvalue=cell.getStringCellValue() ;
             } else if(cell.getCellTypeEnum() == CellType.NUMERIC){
            	 cellvalue=cell.getNumericCellValue();
             } 
        } 
        return cellvalue;  
    }  
	
}
