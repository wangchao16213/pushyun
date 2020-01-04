package com.module.business.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;

import com.bean.BusinessChannel;
import com.bean.BusinessRule;
import com.bean.BusinessTask;
import com.google.gson.JsonObject;

public interface RuleService {
	
	
	public String createCsvFile(
			List<Object> expportList,String rootPath,String buildPath);
	
	public String creatExcelFile(List<Object> expportList,String rootPath,String buildPath);
	
	public String mergeFile(String rootPath,String buildPath,String filePath);
	
	public String createApiFile(String txt,String rootPath,String buildPath);
	
	public List<Object> getExpportList(BusinessChannel businessChannel);
	
	public List<Object> getExpportListFromFile(String filePath);
	
	
	public void setJsonList(JsonObject jsonObject,BusinessRule reqRule,HttpServletRequest request);
	public void setExpList(List<Criterion> expList,JsonObject jsonObject,DetachedCriteria dc);
	
	public void setExpList(List<Criterion> expList,BusinessRule reqRule,
			HttpServletRequest request,DetachedCriteria dc);

	public Map<String,Object> saveExcel(
			String ext,String filePath,
			BusinessChannel businessChannel,String userId,boolean preload,
			Map<String, BusinessRule> exactBusinessRuleMap,
			Map<String, BusinessRule> fuzzyBusinessRuleMap,BusinessTask businessTask);
	
	public Map<String,Object> saveCsv(
			String ext,String filePath,
			BusinessChannel businessChannel,String userId,boolean preload,
			Map<String, BusinessRule> exactBusinessRuleMap,
			Map<String, BusinessRule> fuzzyBusinessRuleMap,BusinessTask businessTask);
	
}
