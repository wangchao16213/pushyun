package com.common.service;

import java.util.Hashtable;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.SimpleExpression;

import com.bean.BaseRecord;
import com.common.web.Pages;
import com.common.web.PaginatedListHelper;




public interface BaseService {
	
	public BaseRecord findById(String id);
	
	public int deleteObject(BaseRecord record);
	
	public int deleteObject(List<BaseRecord> recordList);
	
	public void clearAllCache();
	
	public void setRecordClass(Class recordClass);
	
	public int saveObject(BaseRecord record);
	
	public int saveObject(List<BaseRecord> recordList);
	
	public int updateObject(BaseRecord record,boolean clearListCache);
	
	public PaginatedListHelper findList(List<Criterion> expList,List<Order> orders,Pages pages,DetachedCriteria dc);
	
	public List<BaseRecord> findList(List<Criterion> expList,List<Order> orders, int start, int length,DetachedCriteria dc);
	
	public void removeFromCache(String id, boolean realRemove, boolean isLocal);
	
	public void removeListCache(BaseRecord bt,boolean isLocal);

	public int getList(List<Criterion> expList,DetachedCriteria dc);
	
	public void setHibernateConfigFile(String hibernateConfigFile) ;
	
	public void setHashFieldsList(String hashFieldsList) ;
	
	public PaginatedListHelper findList(List<Criterion> expList,List<Order> orders,Projection project, Pages pages,DetachedCriteria dc) ;
	
	public int getList(List<Criterion> expList, Projection project,DetachedCriteria dc);
	
	public BaseRecord findByProperty(String fieldName,Object value);
	
}
