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
import com.bean.BusinessRule;
import com.bean.BusinessRuleDetail;
import com.bean.BusinessTask;
import com.common.config.SpringConfig;
import com.common.service.BaseService;
import com.common.task.RuleExpTask;
import com.common.tools.DateUtil;
import com.common.tools.StrUtil;
import com.common.type.BaseConfigCode;
import com.common.type.BusinessRuleDetailState;
import com.common.type.BusinessRuleDetailType;
import com.common.type.BusinessRulePushrate;
import com.common.type.BusinessRuleState;
import com.common.type.BusinessTaskState;
import com.common.web.Pages;
import com.common.web.PaginatedListHelper;
import com.google.gson.JsonObject;
import com.module.business.service.RuleService;

public class RuleServiceImpl implements RuleService{
	
	private final Logger logger = Logger.getLogger(RuleServiceImpl.class.getName());

	@Override
	public Map<String,Object> saveExcel(String ext, String filePath,
			BusinessChannel businessChannel,String userId,boolean preload,
			Map<String, BusinessRule> exactBusinessRuleMap,Map<String, BusinessRule> fuzzyBusinessRuleMap,
			BusinessTask businessTask) {
		 BaseService businessChannelService=(BaseService) SpringConfig.getInstance().getService(BusinessChannel.class);
		 BaseService businessTaskService=(BaseService) SpringConfig.getInstance().getService(BusinessTask.class);
		 Workbook workbook =null;
		 Map<String,Object> returnMap=new HashMap<String, Object>();
		 returnMap.put("succ",0);
		 returnMap.put("error",0);
	     returnMap.put("same",0);
	     returnMap.put("msg","");
	     returnMap.put("BusinessRuleList",new ArrayList<BusinessRule>());
	     returnMap.put("BusinessRuleDetailList",new ArrayList<BusinessRuleDetail>());
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
     	    List<BusinessRuleDetail> totalBusinessRuleDetailList=new ArrayList<BusinessRuleDetail>();
     	    List<BusinessRule> totalBusinessRuleList=new ArrayList<BusinessRule>();
     		for (int k = 1; k < rowNum; k++) { // 循环第一张sheet的所有行,从第二行开始
     			if((totalsucc+totalerror+totalsame)%100==0){
     				businessTask =(BusinessTask) businessTaskService.findById(businessTask.getId());
     				if(!businessTask.getState().equals(BusinessTaskState.run.getCode())){
     					return returnMap;
     				}
     			}
     			row = sheet.getRow(k);  // 得到当前行的所有单元格
     			BusinessRule businessRule = new BusinessRule();
     			BusinessChannel channel = new BusinessChannel();
     			Map<Integer, BusinessRuleDetail> businessRuleDetailMap = new HashMap<Integer, BusinessRuleDetail>();
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
     					businessRule.setHost(content);
     				}
     				if (value.equals("精确匹配")
     						||value.equals("域名地址")) {
     					businessRule.setExact(content);
     				}
     				if (value.equals("模糊匹配")
     						||value.equals("匹配域名")) {
     					businessRule.setFuzzy(content);
     				}
     				if (value.equals("推送频率")
     						||value.equals("推送策略")) {
     					String pushrate=StrUtil.getCode(BusinessRulePushrate.class.getName(),content);
     					if(StringUtils.isBlank(pushrate)){
     						businessRule.setPushrate(Integer.parseInt(BusinessRulePushrate.minute5.getCode()));
     					}else{
     						businessRule.setPushrate(Integer.parseInt(pushrate));
     					}
     				}
     				if (value.equals("过滤条件")) {
     					businessRule.setUrlfilter(content);
     				}
     				if (value.equals("符合条件")) {
     					urlaccord=content;
     				}
     				if (value.equals("频率关键词")) {
     					businessRule.setRatekey(content);
     				}
     				if (value.equals("备注")) {
     					businessRule.setRemark(content);
     				}
     				if (value.equals("状态")) {
     					if(StringUtils.isBlank(content)){
     						businessRule.setState(BusinessRuleState.normal.getCode());
     					}else{
     						businessRule.setState(StrUtil.getCode(
     								BusinessRuleState.class.getName(), content));
     					}
     				}
     				if (value.equals("推送类型")) {
     					if(StringUtils.isNotBlank(content)){
     						BusinessRuleDetail businessRuleDetail = new BusinessRuleDetail();
     						if (businessRuleDetailMap.containsKey(1)) {
     							businessRuleDetail = businessRuleDetailMap.get(1);
     						}
     						if(content.equals("返回完整页面")){
     							businessRuleDetail.setType(BusinessRuleDetailType.html.getCode());
     						}else if(content.equals("js代码")){
     							businessRuleDetail.setType(BusinessRuleDetailType.js.getCode());
     						}else if(content.equals("统计流量")){
     							businessRuleDetail.setType(BusinessRuleDetailType.count.getCode());
     						}else if(content.equals("js代码(动态)")){
     							businessRuleDetail.setType(BusinessRuleDetailType.js.getCode());
     						}else if(content.equals("直接跳转")){
     							businessRuleDetail.setType(BusinessRuleDetailType.link.getCode());
     						}
     						businessRuleDetailMap.put(1, businessRuleDetail);
     					}
     				}
     				if (value.equals("返回页面")) {
     					if(StringUtils.isNotBlank(content)){
     						BusinessRuleDetail businessRuleDetail = new BusinessRuleDetail();
     						if (businessRuleDetailMap.containsKey(1)) {
     							businessRuleDetail = businessRuleDetailMap.get(1);
     						}
     						businessRuleDetail.setContent(content);
     						businessRuleDetailMap.put(1, businessRuleDetail);
     					}
     				}
     				if (value.equals("跳转链接")) {
     					if(StringUtils.isNotBlank(content)){
     						BusinessRuleDetail businessRuleDetail = new BusinessRuleDetail();
     						if (businessRuleDetailMap.containsKey(1)) {
     							businessRuleDetail = businessRuleDetailMap.get(1);
     						}
     						businessRuleDetail.setContent(content);
     						businessRuleDetailMap.put(1, businessRuleDetail);
     					}
     				}
     				for (int n = 1; n <= (columnNum - 8); n++) {
     					BusinessRuleDetail businessRuleDetail = new BusinessRuleDetail();
     					if (value.equals("频率关键词" + n)) {
     						if (businessRuleDetailMap.containsKey(n)) {
     							businessRuleDetail = businessRuleDetailMap
     									.get(n);
     						}
     						businessRuleDetail.setRatekey(content);
     						businessRuleDetailMap.put(n, businessRuleDetail);
     					}
     					if (value.equals("推送类型" + n)) {
     						if (businessRuleDetailMap.containsKey(n)) {
     							businessRuleDetail = businessRuleDetailMap
     									.get(n);
     						}
     						businessRuleDetail.setType(StrUtil.getCode(
     								BusinessRuleDetailType.class.getName(),
     								content));
     						businessRuleDetailMap.put(n, businessRuleDetail);
     					}
     					if (value.equals("推送频率" + n)) {
     						if (businessRuleDetailMap.containsKey(n)) {
     							businessRuleDetail = businessRuleDetailMap.get(n);
     						}
     						if(StringUtils.isNotBlank(content)){
     							businessRuleDetail.setPushrate(Integer.parseInt(StrUtil.getCode(BusinessRulePushrate.class.getName(),content)));
     							businessRuleDetailMap.put(n, businessRuleDetail);
     						}
     					}
     					if (value.equals("推送内容" + n)) {
     						if (businessRuleDetailMap.containsKey(n)) {
     							businessRuleDetail = businessRuleDetailMap
     									.get(n);
     						}
     						businessRuleDetail.setContent(content);
     						businessRuleDetailMap.put(n, businessRuleDetail);
     					}
     				}
     			}
     			if(StringUtils.isNotBlank(businessRule.getFuzzy())){
     				businessRule.setExact("");
     			}
     			if(StringUtils.isNotBlank(urlaccord)){
     				if(StringUtils.isNotBlank(businessRule.getFuzzy())){
     					if(businessRule.getFuzzy().indexOf("[?]")==-1){
     						if(businessRule.getFuzzy().indexOf("?")!=-1){
     							businessRule.setFuzzy(businessRule.getFuzzy().replaceAll("\\?", "[\\?]"));
     						}
     					}
     					businessRule.setFuzzy(businessRule.getFuzzy()+"*"+urlaccord+"*");
     				}
     			}else{
     				if(StringUtils.isNotBlank(businessRule.getFuzzy())){
     					if(businessRule.getFuzzy().indexOf("*")==-1){
     						businessRule.setFuzzy(businessRule.getFuzzy()+"*");
     					}
     					if(businessRule.getFuzzy().indexOf("[?]")==-1){
     						if(businessRule.getFuzzy().indexOf("?")!=-1){
     							businessRule.setFuzzy(businessRule.getFuzzy().replaceAll("\\?", "[\\?]"));
     						}
     					}
     				}
     			}
     			if(businessChannel==null){
     				if (StringUtils.isNotBlank(channel.getCode())){
     					businessChannel=(BusinessChannel) businessChannelService.findByProperty("code", channel.getCode());
     				}
     			}
     			Map<String,Object> returnObjMap=getBusinessRuleAndBusinessRuleDetail(businessRule,businessChannel,
     					 userId, preload,businessRuleDetailMap,exactBusinessRuleMap,fuzzyBusinessRuleMap);
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
     			BusinessRule rule=(BusinessRule) returnObjMap.get("BusinessRule");
     			totalBusinessRuleList.add(rule);
     			List<BusinessRuleDetail> businessRuleDetailList=(List<BusinessRuleDetail>) returnObjMap.get("BusinessRuleDetailList");
     			totalBusinessRuleDetailList.addAll(businessRuleDetailList);
     		}
     		returnMap.put("succ",totalsucc);
    		returnMap.put("error",totalerror);
    		returnMap.put("same",totalsame);
    		returnMap.put("msg",totalmsgsb.toString());
    		returnMap.put("BusinessRuleDetailList",totalBusinessRuleDetailList);
    		returnMap.put("BusinessRuleList",totalBusinessRuleList);
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
			BusinessChannel businessChannel,String userId,boolean preload,
			Map<String, BusinessRule> exactBusinessRuleMap,Map<String, BusinessRule> fuzzyBusinessRuleMap,
			BusinessTask businessTask) {
		 BaseService businessChannelService=(BaseService) SpringConfig.getInstance().getService(BusinessChannel.class);
		 BaseService businessTaskService=(BaseService) SpringConfig.getInstance().getService(BusinessTask.class);
		 Map<String,Object> returnMap=new HashMap<String, Object>();
		 returnMap.put("succ",0);
		 returnMap.put("error",0);
	     returnMap.put("same",0);
	     returnMap.put("msg","");
	     returnMap.put("BusinessRuleList",new ArrayList<BusinessRule>());
	     returnMap.put("BusinessRuleDetailList",new ArrayList<BusinessRuleDetail>());
	     int	totalsucc=0;
		  int	totalerror=0;
		  int	totalsame=0;
		  StringBuffer totalmsgsb=new StringBuffer();
		  List<BusinessRuleDetail> totalBusinessRuleDetailList=new ArrayList<BusinessRuleDetail>();
		  List<BusinessRule> totalBusinessRuleList=new ArrayList<BusinessRule>();
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
				  BusinessRule businessRule = new BusinessRule();
				  BusinessChannel channel = new BusinessChannel();
				  Map<Integer, BusinessRuleDetail> businessRuleDetailMap = new HashMap<Integer, BusinessRuleDetail>();
				  String urlaccord="";
				  for(int i=0;i<line.length;i++){
					  if (headers[i].equals("渠道编码")) {
							channel.setCode(line[i]);
						}
						if (headers[i].equals("域名")
								||headers[i].equals("主机地址")) {
							businessRule.setHost(line[i]);
						}
						if (headers[i].equals("精确匹配")
								||headers[i].equals("域名地址")) {
							businessRule.setExact(line[i]);
						}
						if (headers[i].equals("模糊匹配")
								||headers[i].equals("匹配域名")) {
							businessRule.setFuzzy(line[i]);
						}
						if (headers[i].equals("推送频率")
								||headers[i].equals("推送策略")) {
							String pushrate=StrUtil.getCode(BusinessRulePushrate.class.getName(),line[i]);
							if(StringUtils.isBlank(pushrate)){
								businessRule.setPushrate(Integer.parseInt(BusinessRulePushrate.minute5.getCode()));
							}else{
								businessRule.setPushrate(Integer.parseInt(pushrate));
							}
						}
						if (headers[i].equals("过滤条件")) {
							businessRule.setUrlfilter(line[i]);
						}
						if (headers[i].equals("符合条件")) {
							urlaccord=line[i];
						}
						if (headers[i].equals("频率关键词")) {
							businessRule.setRatekey(line[i]);
						}
						if (headers[i].equals("备注")) {
							businessRule.setRemark(line[i]);
						}
						if (headers[i].equals("状态")) {
							if(StringUtils.isBlank(line[i])){
								businessRule.setState(BusinessRuleState.normal.getCode());
							}else{
								businessRule.setState(StrUtil.getCode(
										BusinessRuleState.class.getName(), line[i]));
							}
							
						}
						if (headers[i].equals("推送类型")) {
							if(StringUtils.isNotBlank(line[i])){
								BusinessRuleDetail businessRuleDetail = new BusinessRuleDetail();
								if (businessRuleDetailMap.containsKey(1)) {
									businessRuleDetail = businessRuleDetailMap.get(1);
								}
								if(line[i].equals("返回完整页面")){
									businessRuleDetail.setType(BusinessRuleDetailType.html.getCode());
								}else if(line[i].equals("js代码")){
									businessRuleDetail.setType(BusinessRuleDetailType.js.getCode());
								}else if(line[i].equals("统计流量")){
									businessRuleDetail.setType(BusinessRuleDetailType.count.getCode());
								}else if(line[i].equals("js代码(动态)")){
									businessRuleDetail.setType(BusinessRuleDetailType.js.getCode());
								}else if(line[i].equals("直接跳转")){
									businessRuleDetail.setType(BusinessRuleDetailType.link.getCode());
								}
								businessRuleDetailMap.put(1, businessRuleDetail);
							}
						}
						if (headers[i].equals("返回页面")) {
							if(StringUtils.isNotBlank(line[i])){
								BusinessRuleDetail businessRuleDetail = new BusinessRuleDetail();
								if (businessRuleDetailMap.containsKey(1)) {
									businessRuleDetail = businessRuleDetailMap.get(1);
								}
								businessRuleDetail.setContent(line[i]);
								businessRuleDetailMap.put(1, businessRuleDetail);
							}
						}
						if (headers[i].equals("跳转链接")) {
							if(StringUtils.isNotBlank(line[i])){
								BusinessRuleDetail businessRuleDetail = new BusinessRuleDetail();
								if (businessRuleDetailMap.containsKey(1)) {
									businessRuleDetail = businessRuleDetailMap.get(1);
								}
								businessRuleDetail.setContent(line[i]);
								businessRuleDetailMap.put(1, businessRuleDetail);
							}
						}
						for (int n = 1; n <= (headers.length - 8); n++) {
							BusinessRuleDetail businessRuleDetail = new BusinessRuleDetail();
							if (headers[i].equals("频率关键词" + n)) {
								if (businessRuleDetailMap.containsKey(n)) {
									businessRuleDetail = businessRuleDetailMap
											.get(n);
								}
								businessRuleDetail.setRatekey(line[i]);
								businessRuleDetailMap.put(n, businessRuleDetail);
							}
							if (headers[i].equals("推送类型" + n)) {
								if (businessRuleDetailMap.containsKey(n)) {
									businessRuleDetail = businessRuleDetailMap
											.get(n);
								}
								businessRuleDetail.setType(StrUtil.getCode(
										BusinessRuleDetailType.class.getName(),
										line[i]));
								businessRuleDetailMap.put(n, businessRuleDetail);
							}
							if (headers[i].equals("推送频率" + n)) {
								if (businessRuleDetailMap.containsKey(n)) {
									businessRuleDetail = businessRuleDetailMap.get(n);
								}
								if(StringUtils.isNotBlank(line[i])){
									businessRuleDetail.setPushrate(Integer.parseInt(StrUtil.getCode(BusinessRulePushrate.class.getName(),line[i])));
									businessRuleDetailMap.put(n, businessRuleDetail);
								}
							}
							if (headers[i].equals("推送内容" + n)) {
								if (businessRuleDetailMap.containsKey(n)) {
									businessRuleDetail = businessRuleDetailMap
											.get(n);
								}
								businessRuleDetail.setContent(line[i]);
								businessRuleDetailMap.put(n, businessRuleDetail);
							}
						}
				  }
					if(businessChannel==null){
						if (StringUtils.isNotBlank(channel.getCode())){
							businessChannel=(BusinessChannel) businessChannelService.findByProperty("code", channel.getCode());
						}
					}
					Map<String,Object> returnObjMap=getBusinessRuleAndBusinessRuleDetail(businessRule,businessChannel,
							 userId,preload,businessRuleDetailMap,exactBusinessRuleMap,fuzzyBusinessRuleMap);
					
					
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
					BusinessRule rule=(BusinessRule) returnObjMap.get("BusinessRule");
					totalBusinessRuleList.add(rule);
					List<BusinessRuleDetail> businessRuleDetailList=(List<BusinessRuleDetail>) returnObjMap.get("BusinessRuleDetailList");
					totalBusinessRuleDetailList.addAll(businessRuleDetailList);
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
		 returnMap.put("BusinessRuleDetailList",totalBusinessRuleDetailList);
		 returnMap.put("BusinessRuleList",totalBusinessRuleList);
		 return returnMap;
	}
	
	
	private Map<String,Object> getBusinessRuleAndBusinessRuleDetail(
			BusinessRule businessRule,BusinessChannel businessChannel,
			String userId,boolean preload,Map<Integer, BusinessRuleDetail> businessRuleDetailMap,
			Map<String, BusinessRule> exactBusinessRuleMap,
			Map<String, BusinessRule> fuzzyBusinessRuleMap){
		BaseService businessRuleService=(BaseService) SpringConfig.getInstance().getService(BusinessRule.class);
		Map<String,Object>  returnMap=new HashMap<String, Object>();
		returnMap.put("succ",0);
		returnMap.put("error",0);
		returnMap.put("same",0);
		returnMap.put("msg","");
		if(businessChannel==null){
			return returnMap;
		}
		if (StringUtils.isBlank(businessRule.getExact())
				&&StringUtils.isBlank(businessRule.getFuzzy())) {
			return returnMap;
		}
		if (businessRuleDetailMap.size()==0) {
			return returnMap;
		}
		if(!preload){
			if (StringUtils.isNotBlank(businessRule.getExact())){
				List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
				List<Order> orders = new ArrayList<Order>();// 排序条件
				expList.add(Restrictions.eq("BusinessChannel.id", businessChannel.getId()));
				expList.add(Restrictions.eq("exact", businessRule.getExact()));
				List<BaseRecord> checkBusinessRuleList=businessRuleService.findList(expList, orders, 0, 1, null);
				if(checkBusinessRuleList!=null
						&&checkBusinessRuleList.size()>0){
					returnMap.put("same",1);
					returnMap.put("msg",businessRule.getExact());
					return returnMap;
				}
			}
			if (StringUtils.isNotBlank(businessRule.getFuzzy())){
				List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
				List<Order> orders = new ArrayList<Order>();// 排序条件
				expList.add(Restrictions.eq("BusinessChannel.id", businessChannel.getId()));
				expList.add(Restrictions.eq("fuzzy", businessRule.getFuzzy()));
				List<BaseRecord> checkBusinessRuleList=businessRuleService.findList(expList, orders, 0, 1, null);
				if(checkBusinessRuleList!=null
						&&checkBusinessRuleList.size()>0){
					returnMap.put("same",1);
					returnMap.put("msg",businessRule.getExact());
					return returnMap;
				}
			}
		}else{
			if (StringUtils.isNotBlank(businessRule.getExact())){
				String key=String.format("%s_%s", businessRule.getExact(),businessChannel.getId());
				if(exactBusinessRuleMap.containsKey(key)){
					returnMap.put("same",1);
					returnMap.put("msg",businessRule.getExact());
					return returnMap;
				}else{
					exactBusinessRuleMap.put(key, businessRule);
				}
			}
			if (StringUtils.isNotBlank(businessRule.getFuzzy())){
				String key="";
				if(StringUtils.isNotBlank(businessRule.getHost())){
					key=String.format("%s_%s_%s", businessRule.getHost(),businessRule.getFuzzy(),businessChannel.getId());
				}else{
					key=String.format("%s_%s", businessRule.getFuzzy(),businessChannel.getId());
				}
				if(fuzzyBusinessRuleMap.containsKey(key)){
					returnMap.put("same",1);
					returnMap.put("msg",businessRule.getFuzzy());
					return returnMap;
				}else{
					fuzzyBusinessRuleMap.put(key, businessRule);
				}
			}
		}
		
		businessRule.setBusinessChannel(businessChannel);
		businessRule.setCreateuserid(userId);
		businessRule.setUpdateuserid(businessRule.getCreateuserid());
		businessRule.setCreatetime(new Date());
		businessRule.setUpdatetime(businessRule.getCreatetime());
		returnMap.put("BusinessRule", businessRule);
		List<BusinessRuleDetail> businessRuleDetailList=new ArrayList<BusinessRuleDetail>();
		for(Integer n:businessRuleDetailMap.keySet()){
			BusinessRuleDetail businessRuleDetail=businessRuleDetailMap.get(n);
			if (StringUtils.isBlank(businessRuleDetail.getType())
					||StringUtils.isBlank(businessRuleDetail.getContent())) {
				continue;
			}
			if (StringUtils.isBlank(businessRuleDetail.getRatekey())) {
				if (StringUtils.isNotBlank(businessRule.getRatekey())) {
					businessRuleDetail.setRatekey(businessRule.getRatekey());
				}
			}
			if(businessRuleDetail.getPushrate()==null){
				businessRuleDetail.setPushrate(businessRule.getPushrate());
			}
			businessRuleDetail.setBusinessRule(businessRule);
			businessRuleDetail.setCreateuserid(userId);
			businessRuleDetail.setUpdateuserid(businessRule.getCreateuserid());
			businessRuleDetail.setCreatetime(new Date());
			businessRuleDetail.setUpdatetime(businessRule.getCreatetime());
			businessRuleDetail.setState(BusinessRuleDetailState.normal.getCode());
			businessRuleDetailList.add(businessRuleDetail);
		}
		returnMap.put("succ",1);
		returnMap.put("BusinessRuleDetailList", businessRuleDetailList);
		return returnMap;
	}
	
	@Override
	public String creatExcelFile(List<Object> expportList,String rootPath,String buildPath) {
		BaseService businessRuleDetailService=(BaseService) SpringConfig.getInstance().getService(BusinessRuleDetail.class);
		StringBuffer filePathsb = new StringBuffer();
		filePathsb.append(rootPath);
		File buildPathFile = new File(buildPath);
		if (!buildPathFile.exists()) {
			buildPathFile.mkdirs();
		}
		XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("规则数据");
        XSSFRow row = sheet.createRow(0);
        int i=0;
        int maxrule=0;
		for (Object o : expportList) {
			BusinessRule businessRule = (BusinessRule) o;
			List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
			List<Order> orders = new ArrayList<Order>();// 排序条件
			expList.add(Restrictions.eq("BusinessRule.id", businessRule.getId()));
			orders.add(Order.asc("id"));
			PaginatedListHelper businessRuleDetailPlh=businessRuleDetailService.findList(expList, orders, null, null);
			if(businessRuleDetailPlh.getList()==null
					||businessRuleDetailPlh.getList().size()==0){
				continue;
			}
			if(maxrule==0){
				maxrule=businessRuleDetailPlh.getList().size();
			}else if(businessRule.getBusinessRuleDetails().size()>maxrule){
				maxrule=businessRuleDetailPlh.getList().size();
			}
		}
        XSSFCell cell = row.createCell(++i-1);
        cell.setCellValue("渠道编码");
        cell = row.createCell(++i-1);
        cell.setCellValue("渠道名称");
        cell = row.createCell(++i-1);
        cell.setCellValue("域名");
        cell = row.createCell(++i-1);
        cell.setCellValue("精确匹配");
        cell = row.createCell(++i-1);
        cell.setCellValue("模糊匹配");
        cell = row.createCell(++i-1);
        cell.setCellValue("过滤条件");
        cell = row.createCell(++i-1);
        cell.setCellValue("新版本-老版本转换频率");
		for(int j=0;j<maxrule;j++){
			 cell = row.createCell(++i-1);
		     cell.setCellValue(String.format("频率关键词%s", (j+1)));
		     cell = row.createCell(++i-1);
		     cell.setCellValue(String.format("推送频率%s", (j+1)));
		     cell = row.createCell(++i-1);
		     cell.setCellValue(String.format("推送类型%s", (j+1))); 
		     cell = row.createCell(++i-1);
		     cell.setCellValue(String.format("推送内容%s", (j+1))); 
		}
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
				BusinessRule businessRule = (BusinessRule)expportList.get(h);
				cell = row.createCell(++i-1);
			    cell.setCellValue( businessRule.getBusinessChannel().getCode());
			    cell = row.createCell(++i-1);
			    cell.setCellValue( businessRule.getBusinessChannel().getName());
			    cell = row.createCell(++i-1);
			    cell.setCellValue(businessRule.getHost());
			    cell = row.createCell(++i-1);
			    cell.setCellValue(businessRule.getExact());
			    cell = row.createCell(++i-1);
			    cell.setCellValue(businessRule.getFuzzy());
			    cell = row.createCell(++i-1);
			    cell.setCellValue(businessRule.getUrlfilter());
				int pushrate=0;
				List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
				List<Order> orders = new ArrayList<Order>();// 排序条件
				expList.add(Restrictions.eq("BusinessRule.id", businessRule.getId()));
				orders.add(Order.asc("id"));
				PaginatedListHelper businessRuleDetailPlh=businessRuleDetailService.findList(expList, orders, null, null);
				if(businessRuleDetailPlh.getList()!=null
						&&businessRuleDetailPlh.getList().size()>0){
					for(Object object:businessRuleDetailPlh.getList()){
						BusinessRuleDetail businessRuleDetail=(BusinessRuleDetail)object;
						if(pushrate==0){
							pushrate=businessRuleDetail.getPushrate();
						}else{
							if(pushrate>businessRuleDetail.getPushrate()){
								pushrate=businessRuleDetail.getPushrate();
							}
						}
					}
				}
				cell = row.createCell(++i-1);
				cell.setCellValue(String.valueOf(pushrate));
				if(businessRuleDetailPlh.getList()!=null
						&&businessRuleDetailPlh.getList().size()>0){
					for(Object object:businessRuleDetailPlh.getList()){
						BusinessRuleDetail businessRuleDetail=(BusinessRuleDetail)object;
						cell = row.createCell(++i-1);
						cell.setCellValue( businessRuleDetail.getRatekey());
						cell = row.createCell(++i-1);
						cell.setCellValue(StrUtil.getDisplay(BusinessRulePushrate.class.getName(), businessRuleDetail.getPushrate().toString()));
						cell = row.createCell(++i-1);
						cell.setCellValue( StrUtil.getDisplay(BusinessRuleDetailType.class.getName(), businessRuleDetail.getType()));
						cell = row.createCell(++i-1);
						cell.setCellValue( businessRuleDetail.getContent());
					}
				}
				int listsize=0;
				if(businessRuleDetailPlh.getList()!=null
						&&businessRuleDetailPlh.getList().size()>0){
					listsize=businessRuleDetailPlh.getList().size();
				}
				for(int k=0;k<(maxrule-listsize);k++){
					cell = row.createCell(++i-1);
					cell.setCellValue("");
					cell = row.createCell(++i-1);
					cell.setCellValue("");
					cell = row.createCell(++i-1);
					cell.setCellValue("");
					cell = row.createCell(++i-1);
					cell.setCellValue("");
				}
				cell = row.createCell(++i-1);
				cell.setCellValue(businessRule.getRemark());
				cell = row.createCell(++i-1);
				cell.setCellValue(StrUtil.getDisplay(BusinessRuleState.class.getName(), businessRule.getState()));
				cell = row.createCell(++i-1);
				cell.setCellValue(DateUtil.getDateTime(businessRule.getUpdatetime()));
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
			BusinessRule businessRule = (BusinessRule) o;
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
			headers[++i-1] = "精确匹配";
			headers[++i-1] = "模糊匹配";
			headers[++i-1] = "过滤条件";
			headers[++i-1] = "新版本-老版本转换频率";
			for(int j=0;j<maxrule;j++){
				 headers[++i-1] = String.format("频率关键词%s", (j+1));
				 headers[++i-1] = String.format("推送频率%s", (j+1));
				 headers[++i-1] = String.format("推送类型%s", (j+1));
				 headers[++i-1] = String.format("推送内容%s", (j+1));
			}
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
				BusinessRule businessRule = (BusinessRule)o;
				String[] contents = new String[count];
				for(int j=0;j<count;j++){
					contents[j]="";
				}
				contents[++i-1] = businessRule.getBusinessChannel().getCode();
				contents[++i-1] = businessRule.getBusinessChannel().getName();
				contents[++i-1] = businessRule.getHost();
				contents[++i-1] = businessRule.getExact();
				contents[++i-1] = businessRule.getFuzzy();
				contents[++i-1] = businessRule.getUrlfilter();
				int pushrate=0;
				if(businessRule.getBusinessRuleDetails()!=null
						&&businessRule.getBusinessRuleDetails().size()>0){
					for(Object object:businessRule.getBusinessRuleDetails()){
						BusinessRuleDetail businessRuleDetail=(BusinessRuleDetail)object;
						if(pushrate==0){
							pushrate=businessRuleDetail.getPushrate();
						}else{
							if(pushrate>businessRuleDetail.getPushrate()){
								pushrate=businessRuleDetail.getPushrate();
							}
						}
					}
				}
				contents[++i-1] = String.valueOf(pushrate);
				for(Object object:businessRule.getBusinessRuleDetails()){
					BusinessRuleDetail businessRuleDetail=(BusinessRuleDetail)object;
					contents[++i-1] = businessRuleDetail.getRatekey();
					contents[++i-1] = StrUtil.getDisplay(BusinessRulePushrate.class.getName(), businessRuleDetail.getPushrate().toString());
					contents[++i-1] = StrUtil.getDisplay(BusinessRuleDetailType.class.getName(), businessRuleDetail.getType());
					contents[++i-1] =businessRuleDetail.getContent();
				}
				for(int k=0;k<(maxrule-businessRule.getBusinessRuleDetails().size());k++){
					++i;
					++i;
					++i;
					++i;
				}
				contents[++i-1] =businessRule.getRemark();
				contents[++i-1] =StrUtil.getDisplay(BusinessRuleState.class.getName(), businessRule.getState());
				contents[++i-1] =DateUtil.getDateTime(businessRule.getUpdatetime());
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
		BaseService businessRuleService=(BaseService) SpringConfig.getInstance().getService(BusinessRule.class);
		BaseService baseConfigService=(BaseService) SpringConfig.getInstance().getService(BaseConfig.class);
		BaseService businessTaskService=(BaseService) SpringConfig.getInstance().getService(BusinessTask.class);
		BaseConfig baseConfig=(BaseConfig) baseConfigService.findByProperty("code", BaseConfigCode.expperpagenum.getCode());
		List<Object> expportList = new ArrayList<Object>();
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.eq("BusinessChannel.id", businessChannel.getId()));
		expList.add(Restrictions.eq("state", BusinessRuleState.normal.getCode()));
		int total = businessRuleService.getList(expList, null);
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
			List<BaseRecord> list = businessRuleService.findList(
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
		BaseService businessRuleService=(BaseService) SpringConfig.getInstance().getService(BusinessRule.class);
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
	    	  BusinessRule businessRule= (BusinessRule) businessRuleService.findById(id);
	    	   if(businessRule==null){
	    		   continue;
	    	   }
	    	   expportList.add(businessRule);
	    }
       return expportList;
	}
	
	@Override
	public void setJsonList(JsonObject jsonObject,BusinessRule reqRule,HttpServletRequest request){
		if(StringUtils.isNotBlank(reqRule.getExact())){
			jsonObject.addProperty("exact", reqRule.getExact());
		}
		if(StringUtils.isNotBlank(reqRule.getFuzzy())){
			jsonObject.addProperty("fuzzy", reqRule.getFuzzy());
		}
		String exactfuzzy = request.getParameter("exact|fuzzy");
		if(StringUtils.isNotBlank(exactfuzzy)){
			jsonObject.addProperty("exactfuzzy", exactfuzzy);
		}
		if(StringUtils.isNotBlank(reqRule.getState())){
			jsonObject.addProperty("state", reqRule.getState());
		}
		if(StringUtils.isNotBlank(reqRule.getHost())){
			jsonObject.addProperty("host", reqRule.getHost());
		}
		if(StringUtils.isNotBlank(reqRule.getUrlfilter())){
			jsonObject.addProperty("urlfilter", reqRule.getUrlfilter());
		}
		if(reqRule.getPushrate()!=null
				&&reqRule.getPushrate()!=-1){
			jsonObject.addProperty("pushrate", reqRule.getPushrate());
		}
		if(StringUtils.isNotBlank(reqRule.getBusinessChannel().getId())){
			jsonObject.addProperty("BusinessChannel.id", reqRule.getBusinessChannel().getId());
		} 
		String blankrule = request.getParameter("blankrule");
		if(StringUtils.isNotBlank(blankrule)){
			jsonObject.addProperty("blankrule", blankrule);
		}
		String businessRuleDetailPushrate = request.getParameter("BusinessRuleDetail.pushrate");
		String businessRuleDetailType = request.getParameter("BusinessRuleDetail.type");
		String businessRuleDetailContent = request.getParameter("BusinessRuleDetail.content");
		if(StringUtils.isNotBlank(businessRuleDetailPushrate)){
			jsonObject.addProperty("BusinessRuleDetails.pushrate", Integer.parseInt(businessRuleDetailPushrate));
		}
		if(StringUtils.isNotBlank(businessRuleDetailType)){
			jsonObject.addProperty("BusinessRuleDetails.type", businessRuleDetailType);
		}
		if(StringUtils.isNotBlank(businessRuleDetailContent)){
			jsonObject.addProperty("BusinessRuleDetails.content", businessRuleDetailContent);
		}
	}
	
	@Override
	public void setExpList(List<Criterion> expList,BusinessRule reqRule,
			HttpServletRequest request,DetachedCriteria dc){
		if(StringUtils.isNotBlank(reqRule.getExact())){
			expList.add(Restrictions.like("exact", reqRule.getExact(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqRule.getFuzzy())){
			expList.add(Restrictions.like("fuzzy", reqRule.getFuzzy(),MatchMode.ANYWHERE));
		}
		String exactfuzzy = request.getParameter("exact|fuzzy");
		if(StringUtils.isNotBlank(exactfuzzy)){
			expList.add(Restrictions.or(
					Restrictions.like("fuzzy", exactfuzzy,MatchMode.ANYWHERE),
							Restrictions.like("exact", exactfuzzy,MatchMode.ANYWHERE)));
		}
		if(StringUtils.isNotBlank(reqRule.getState())){
			expList.add(Restrictions.eq("state", reqRule.getState()));
		}
		if(StringUtils.isNotBlank(reqRule.getHost())){
			expList.add(Restrictions.like("host", reqRule.getHost(),MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(reqRule.getUrlfilter())){
			expList.add(Restrictions.like("urlfilter", reqRule.getUrlfilter(),MatchMode.ANYWHERE));
		}
		if(reqRule.getPushrate()!=null
				&&reqRule.getPushrate()!=-1){
			expList.add(Restrictions.eq("pushrate", reqRule.getPushrate()));
		}
		if(StringUtils.isNotBlank(reqRule.getBusinessChannel().getId())){
			expList.add(Restrictions.eq("BusinessChannel.id", reqRule.getBusinessChannel().getId()));
		} 
		String blankrule = request.getParameter("blankrule");
		if(StringUtils.isNotBlank(blankrule)){
			expList.add(Restrictions.eq(blankrule, ""));
		}
		String businessRuleDetailPushrate = request.getParameter("BusinessRuleDetail.pushrate");
		String businessRuleDetailType = request.getParameter("BusinessRuleDetail.type");
		String businessRuleDetailContent = request.getParameter("BusinessRuleDetail.content");
		if(StringUtils.isNotBlank(businessRuleDetailPushrate)||
				StringUtils.isNotBlank(businessRuleDetailType)
				||StringUtils.isNotBlank(businessRuleDetailContent)){
			dc.createAlias("BusinessRuleDetails", "BusinessRuleDetails");
			dc.setResultTransformer(dc.DISTINCT_ROOT_ENTITY); 
		}
		if(StringUtils.isNotBlank(businessRuleDetailPushrate)){
			expList.add(Restrictions.eq("BusinessRuleDetails.pushrate", Integer.parseInt(businessRuleDetailPushrate)));
		}
		if(StringUtils.isNotBlank(businessRuleDetailType)){
			expList.add(Restrictions.eq("BusinessRuleDetails.type", businessRuleDetailType));
		}
		if(StringUtils.isNotBlank(businessRuleDetailContent)){
			expList.add(Restrictions.like("BusinessRuleDetails.content", businessRuleDetailContent,MatchMode.ANYWHERE));
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
		if(jsonObject.has("blankrule")){
			expList.add(Restrictions.eq(jsonObject.get("blankrule").getAsString(), ""));
		}
		if(jsonObject.has("BusinessRuleDetails.pushrate")||
				jsonObject.has("BusinessRuleDetails.type")
				||jsonObject.has("BusinessRuleDetails.content")){
			dc.createAlias("BusinessRuleDetails", "BusinessRuleDetails");
		}
		if(jsonObject.has("BusinessRuleDetail.pushrate")){
			expList.add(Restrictions.eq("BusinessRuleDetails.pushrate", jsonObject.get("BusinessRuleDetails.pushrate").getAsInt()));
		}
		if(jsonObject.has("BusinessRuleDetails.type")){
			expList.add(Restrictions.eq("BusinessRuleDetails.type", jsonObject.get("BusinessRuleDetails.type").getAsString()));
		}
		if(jsonObject.has("BusinessRuleDetails.content")){
			expList.add(Restrictions.like("BusinessRuleDetails.content", jsonObject.get("BusinessRuleDetails.content").getAsString(),MatchMode.ANYWHERE));
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
