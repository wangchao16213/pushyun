package com.module.business.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;

import com.bean.BusinessChannel;
import com.bean.BusinessDns;
import com.bean.BusinessTask;
import com.google.gson.JsonObject;

public interface DnsService {
	
	
	public String createCsvFile(
			List<Object> expportList,String rootPath,String buildPath);
	
	public String creatExcelFile(List<Object> expportList,String rootPath,String buildPath);
	
	public String mergeFile(String rootPath,String buildPath,String filePath);
	
	public String createApiFile(String txt,String rootPath,String buildPath);
	
	public List<Object> getExpportList(BusinessChannel businessChannel);
	
	public List<Object> getExpportListFromFile(String filePath);
	
	
	public void setJsonList(JsonObject jsonObject,BusinessDns reqDns,HttpServletRequest request);
	public void setExpList(List<Criterion> expList,JsonObject jsonObject,DetachedCriteria dc);
	
	public void setExpList(List<Criterion> expList,BusinessDns reqDns,
			HttpServletRequest request,DetachedCriteria dc);

	public Map<String,Object> saveExcel(
			String ext,String filePath,
			BusinessChannel businessChannel,String userId,boolean preload,Map<String,BusinessDns> hostBusinessDnsMap,BusinessTask businessTask);
	
	public Map<String,Object> saveCsv(
			String ext,String filePath,
			BusinessChannel businessChannel,String userId,boolean preload,Map<String,BusinessDns> hostBusinessDnsMap,BusinessTask businessTask);
	
}
