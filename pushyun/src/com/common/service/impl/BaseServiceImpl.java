package com.common.service.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.exception.MemcachedException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.hibernate.criterion.Subqueries;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.impl.CriteriaImpl.OrderEntry;

import com.bean.BaseRecord;
import com.common.comm.Constants;
import com.common.config.Config;
import com.common.config.ConfigLoader;
import com.common.config.HibernateUtil;
import com.common.config.MemcachedUtil11411;
import com.common.service.BaseService;
import com.common.tools.DateUtil;
import com.common.tools.StrUtil;
import com.common.web.Pages;
import com.common.web.PaginatedListHelper;
public class BaseServiceImpl implements BaseService {

	private final Logger logger = Logger.getLogger(BaseServiceImpl.class
			.getName());

	// private CommDAO commDAO;

	/**
	 * hibernate 配置文件，可以实现数据库按表拆分的分布式
	 */
	private String hibernateConfigFile = null;
	/**
	 * 该manager管理的是哪个class
	 */
	private Class recordClass = null;
	/**
	 * applicationContext.xml里配置的二级散列缓存字段，如果有多个用;隔开。一般用userId做散列，2个字段散列足够了！
	 */
	private String hashFieldsList = null;

	/**
	 * 二级散列缓存的字段列表
	 */
	private String[] hashFields = null;


	/**
	 * 得到需要散列的字段数组
	 * 
	 * @return
	 */
	private String[] getHashFields() {
		if (hashFieldsList == null) {
			return null;
		} else {
			if (hashFields == null) {
				hashFields = hashFieldsList.split(";");
			}
			return hashFields;
		}
	}

	/**
	 * 删除所有缓存！！！！！！！！！！！<br/> 删除所有缓存，尽供后台调用，前台页面别瞎用！<br/>
	 */
	public void clearAllCache() {
	
	}

	/**
	 * 根据id获取记录。第一步从本机内存中取，如果没有则转向memcached server获取，<br/> 如果memcached
	 * server也没有才从数据库中获取，这样可以大大减轻数据库服务器的压力。<br/>
	 * 
	 * @param id
	 *            记录的id
	 * @return BaseRecord对象
	 */
	public BaseRecord findById(String id) {

		BaseRecord br = null;

		// 第二步：去memcached server中查找
		if (StrUtil.getBoolean(ConfigLoader.getInstance().getProps(
				Config.system_config).getProperty(Config.IS_USE_MEMCACHED))) {
			br = getFromMemCachedServer(recordClass.getName() + "#" + id);
			if (br != null) {
				return br;
			}
		}

		// 第三步：读取数据库
		Session s = HibernateUtil.currentSession(hibernateConfigFile);
		try {
			// 第三步：读取数据库
			br = (BaseRecord) s.get(recordClass, id);
//			System.out.println(br);
		} catch (HibernateException he) {
			he.printStackTrace();
		} finally {
			HibernateUtil.closeSession(hibernateConfigFile);
		}
		// br = commDAO.findId(recordClass, id);

		if (br == null) {
			return null;
		}
		// System.out.println("时间---"+br.getBr_updateDate());
	

		// 第五步：放入memcached缓存中
		set2MemCachedServer(br);
		return br;
	}

	@Override
	public PaginatedListHelper findList(List<Criterion> expList,
			List<Order> orders, Pages pages,DetachedCriteria dc) {
		PaginatedListHelper pl = new PaginatedListHelper();
		if(pages==null){
			pages=new Pages();
			pages.setPerPageNum(9999);
		}
		if (pages.getTotalNum() == -1) {
			pages.setTotalNum(this.getList(expList,dc));
		}
		pages.executeCount();
		List l = this.findList(expList, orders, pages.getSpage(), pages
				.getPerPageNum(),dc);
		pl.setList(l);
		pl.setFullListSize(pages.getTotalNum());
		pl.setObjectsPerPage(pages.getPerPageNum());
		pl.setPageNumber(pages.getPage());
		return pl;
	}

	@Override
	public PaginatedListHelper findList(List<Criterion> expList,
			List<Order> orders, Projection project, Pages pages,DetachedCriteria dc) {
		PaginatedListHelper pl = new PaginatedListHelper();
		if (pages == null) {
			List l = this.findList(expList, orders, project, 0, 9999, dc);
			pl.setList(l);
			return pl;
		}
		if (pages.getTotalNum() == -1) {
			pages.setTotalNum(this.getList(expList, project,dc));
		}
		pages.executeCount();
		List l = this.findList(expList, orders, project, pages.getSpage(),
				pages.getPerPageNum(),dc);
		pl.setList(l);
		pl.setFullListSize(pages.getTotalNum());
		pl.setObjectsPerPage(pages.getPerPageNum());
		pl.setPageNumber(pages.getPage());
		return pl;
	}
	/**
	 * 每个表必须有id这个字段，每个类必须有id这个field。自定义条件查询列表,理论上这个方法<br>
	 * 可以满足所有需求,特别注意缓存key的拼法！！！！！<br>
	 * 在memcached缓存上存的则不是List而是由#分开的id列表，如：13#14#25#256#887#987<br/>
	 * key是象s10l20,createTime desc$age<90#aget>80#pid=12343#这样的字符串<br>
	 * 
	 * @param expList
	 *            查询条件
	 * @param orders
	 *            排序
	 * @param start
	 *            开始位置
	 * @param length
	 *            获取长度
	 * @return List 数据库记录
	 */
	public List<BaseRecord> findList(List<Criterion> expList,
			List<Order> orders, int start, int length,DetachedCriteria dc) {

		List<String> fList = new ArrayList<String>();// field set
		if (expList != null) {
			for (int i = 0; i < expList.size(); i++) {
				Criterion s1 = (Criterion) expList.get(i);
				fList.add(s1.toString());
			}
		}
		Collections.sort(fList);
		StringBuffer keyBuffer = new StringBuffer();
		keyBuffer.append("s").append(start).append("l").append(length).append(
				",");

		if (orders != null) {
			for (int i = 0; i < orders.size(); i++) {
				keyBuffer.append(orders.get(i).toString());
			}
		}
		// 注意，$之前的字符都不需要参与到清除缓存的计算，清除缓存只需要计算$后面的条件！！！
		keyBuffer.append("$");

		// 二级缓存的key,如userId=78998
		String secondaryCacheKey = null;
		String[] secondaryFields = getHashFields();

		for (int i = 0; i < fList.size(); i++) {
			String condition = (String) fList.get(i);
			keyBuffer.append(condition).append("#");
		}

		if (secondaryFields != null && secondaryFields.length > 0) {
			for (int j = 0; j < secondaryFields.length; j++) {
				if (secondaryCacheKey == null) {
					for (int i = 0; i < fList.size(); i++) {
						String condition = (String) fList.get(i);
						if (condition.startsWith(secondaryFields[j] + "=")) {// 条件包含二级缓存的字段则用二级缓存
							secondaryCacheKey = condition;
							break;
						}
					}
				} else {
					break;
				}
			}
		}

		String key = keyBuffer.toString();// 这就是列表的缓存key


		// 第二步 从数据库中查找
		try {
			Session s = HibernateUtil.currentSession(hibernateConfigFile);
			Criteria c = null;
			if (dc == null) {
				c = s.createCriteria(recordClass);
			} else {
				c = dc.getExecutableCriteria(s);
			}
			if (expList != null && expList.size() > 0) {
				for (int i = 0; i < expList.size(); i++)
					c.add(expList.get(i)); // 加入查询条件
			}
			if (orders != null && orders.size() > 0) {
				for (int i = 0; i < orders.size(); i++)
					c.addOrder(orders.get(i)); // 加入排序字段
			}
			if (start >= 0 || length > 0) {
				c.setFirstResult(start);
				c.setMaxResults(length);
			}
			List list = c.list();
			if (list.size() == 0) {
				return null;
			}
	
			ArrayList<BaseRecord> oList = new ArrayList<BaseRecord>();
			for (int i = 0; i < list.size(); i++) {
				BaseRecord br = findById(((BaseRecord) list.get(i)).getBr_id());
				if (br != null)
					oList.add(br);
			}
			return oList;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			HibernateUtil.closeSession(hibernateConfigFile);
		}
		/* } */
	}

	/**
	 * 根据条件得到数据库记录的长度，Integer对象可以直接存在memcached缓存中，所以没有必要序列化！
	 * 
	 * @param cs
	 *            查询条件
	 * @return 长度
	 */
	public int getList(List<Criterion> expList,DetachedCriteria dc) {
		List<String> fList = new ArrayList<String>();
		if (expList != null) {
			for (int i = 0; i < expList.size(); i++) {
				Criterion s1 = (Criterion) expList.get(i);
				fList.add(s1.toString());
			}
		}
		Collections.sort(fList);
		StringBuffer keyBuffer = new StringBuffer();
		keyBuffer.append("$");

		// 二级缓存的key,如userId=78998
		String secondaryCacheKey = null;
		String[] secondaryFields = getHashFields();

		for (int i = 0; i < fList.size(); i++) {
			String condition = (String) fList.get(i);
			keyBuffer.append(condition).append("#");
		}

		if (secondaryFields != null && secondaryFields.length > 0) {
			for (int j = 0; j < secondaryFields.length; j++) {
				if (secondaryCacheKey == null) {
					for (int i = 0; i < fList.size(); i++) {
						String condition = (String) fList.get(i);
						if (condition.startsWith(secondaryFields[j] + "=")) {// 条件包含二级缓存的字段则用二级缓存
							secondaryCacheKey = condition;
							break;
						}
					}
				} else {
					break;
				}
			}
		}

		String key = keyBuffer.toString();


		// 第二步 从数据库中获取

		try {
			Session s = HibernateUtil.currentSession(hibernateConfigFile);
			Criteria c = null;
			Projection projection = null;
			CriteriaImpl impl = null;
			List<OrderEntry> orderEntries = null;
			if (dc == null) {
				c = s.createCriteria(recordClass);
			} else {
				c = dc.getExecutableCriteria(s);
				impl = (CriteriaImpl) c;
				// 先把Projection和OrderBy条件取出来,清空两者来执行Count操作
				projection = impl.getProjection();
				try {
					orderEntries = (List) com.common.tools.BeanUtils.forceGetProperty(impl, "orderEntries");
					com.common.tools.BeanUtils.forceSetProperty(impl,"orderEntries", new ArrayList());
				} catch (Exception e) {
					throw new InternalError(" Runtime Exception impossibility throw ");
				}
			}
			c.setProjection(Projections.rowCount());
			if (expList != null && expList.size() > 0) {
				for (int i = 0; i < expList.size(); i++)
					c.add(expList.get(i)); // 加入查询条件
			}
			Integer lenInteger = (Integer) c.uniqueResult();


			if (dc != null) {
				c.setProjection(projection);
				if (projection == null) {
					c.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
				}
				try {
					com.common.tools.BeanUtils.forceSetProperty(impl,"orderEntries", orderEntries);
				} catch (Exception e) {
					throw new InternalError(" Runtime Exception impossibility throw ");
				}
			}
			return lenInteger.intValue();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			HibernateUtil.closeSession(hibernateConfigFile);
		}
		/* } */
		return 0;
	}
	
	/**
	 * 此处返回的List是Long对象或者Iterator对象，而不是BaseRecord!!!!!!!!!!<br/> 遍历该List方法<br/>
	 * Iterator iterator = list.iterator();<br/> while(iterator.hasNext()) {<br/>
	 * Object[] o = (Object[]) iterator.next();<br/> //...<br/> }<br/>
	 * 
	 * @param expList
	 * @param orders
	 * @param project
	 *            包含sum count group等复杂组合查询条件的Projection(s)
	 * @param start
	 * @param length
	 * @return List
	 */
	public List findList(List<Criterion> expList, List<Order> orders,
			Projection project, int start, int length,DetachedCriteria dc) {
		List<String> fList = new ArrayList<String>();// field set
		if (expList != null) {
			for (int i = 0; i < expList.size(); i++) {
				Criterion s1 = (Criterion) expList.get(i);
				fList.add(s1.toString());
			}
		}
		Collections.sort(fList);
		StringBuffer keyBuffer = new StringBuffer();
		keyBuffer.append("2s").append(start).append("l").append(length).append(
				",");

		if (orders != null) {
			for (int i = 0; i < orders.size(); i++) {
				keyBuffer.append(orders.get(i).toString());
			}
		}
		// 注意，$之前的字符都不需要参与到清除缓存的计算，清除缓存只需要计算$后面的条件！！！
		keyBuffer.append("$");

		for (int i = 0; i < fList.size(); i++) {
			keyBuffer.append(fList.get(i)).append("#");
		}

		String key = keyBuffer.toString();// 这就是列表的缓存key


		// 第二步 查询数据库
		Session s = HibernateUtil.currentSession(hibernateConfigFile);
		try {
			Criteria ct = null;
			if (dc == null) {
				ct = s.createCriteria(recordClass).setProjection(project);
			} else {
				ct = dc.getExecutableCriteria(s).setProjection(project);
			}
			if (expList != null && expList.size() > 0) {
				for (int i = 0; i < expList.size(); i++)
					ct.add(expList.get(i)); // 加入查询条件
			}
			if (orders != null && orders.size() > 0) {
				for (int i = 0; i < orders.size(); i++)
					ct.addOrder(orders.get(i)); // 加入排序字段
			}
			if (length == 9999) {

			} else if (start >= 0 || length > 0) {
				ct.setFirstResult(start);
				ct.setMaxResults(length);
			}

			List list = ct.list();


			return list;
		} catch (HibernateException e) {
			e.printStackTrace();
			return null;
		} finally {
			HibernateUtil.closeSession(hibernateConfigFile);
		}
	}

	/**
	 * 如果要用distinct,project必须是Projections.countDistinct("bbsThemeId");的形式
	 * 
	 * @param expList
	 * @param project
	 *            包含sum count group等复杂组合查询条件的Projection(s)
	 * @return int
	 */
	public int getList(List<Criterion> expList, Projection project,DetachedCriteria dc) {
		List<String> fList = new ArrayList<String>();// field set
		if (expList != null) {
			for (int i = 0; i < expList.size(); i++) {
				Criterion s1 = (Criterion) expList.get(i);
				fList.add(s1.toString());
			}
		}
		Collections.sort(fList);
		StringBuffer keyBuffer = new StringBuffer();
		keyBuffer.append("2$");

		for (int i = 0; i < fList.size(); i++) {
			keyBuffer.append(fList.get(i)).append("#");
		}
		String key = keyBuffer.toString();
		project.toString();


		// 第二步 读数据库
		Session s = HibernateUtil.currentSession(hibernateConfigFile);
		try {
			Criteria ct = null;
			if (dc == null) {
				ct = s.createCriteria(recordClass).setProjection(project);
			} else {
				ct = dc.getExecutableCriteria(s).setProjection(project);
			}
			if (expList != null && expList.size() > 0) {
				for (int i = 0; i < expList.size(); i++)
					ct.add(expList.get(i)); // 加入查询条件
			}
			Integer lenInteger = Integer.valueOf(ct.list().size());
//			System.out.println(ct.list().size());
			return lenInteger.intValue();
//			return 0;
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			HibernateUtil.closeSession(hibernateConfigFile);
		}

		return 0;
	}

	/**
	 * 根据某一个字段的值来获取对象
	 * 
	 * @param fieldName
	 *            字段名
	 * @param value
	 *            字段值
	 * @return BaseRecord 对象
	 */
	public BaseRecord findByProperty(String fieldName, Object value) {
		ArrayList<Criterion> list = new ArrayList<Criterion>();
		list.add(Restrictions.eq(fieldName, value));

		List<BaseRecord> list2 = findList(list, null, 0, 1,null);
		if (list2 == null || list2.size() == 0)
			return null;
		return list2.get(0);
	}
	/**
	 * 从数据库中删除数据，如果有必要，可以重写该方法删除缓存中的纪录和列表中的list缓存！
	 * 
	 * @param br
	 *            BaseRecord
	 * @return boolean
	 */
	public int deleteObject(BaseRecord br) {
		int state = Constants.STATE_OPERATOR_LOST;
		try {
			Session s = HibernateUtil.currentSession(hibernateConfigFile);
			Transaction tx = s.beginTransaction();
			s.delete(br);
			tx.commit();
		} catch (HibernateException e) {
			e.printStackTrace();
			return state;
		} finally {
			HibernateUtil.closeSession(hibernateConfigFile);
		}

		// 删除memcached缓存
		if (StrUtil.getBoolean(ConfigLoader.getInstance().getProps(
				Config.system_config).getProperty(Config.IS_USE_MEMCACHED))) {
			if (MemcachedUtil11411.getMemCachedClient() != null) {
				try {
					MemcachedUtil11411.getMemCachedClient().delete(recordClass.getName() + "#" + br.getBr_id());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// 删除列表缓存
		removeListCache(br, true);

		// 分布式删除对象缓存
		removeFromCache(br.getBr_id()+"", true, true);
		state = Constants.STATE_OPERATOR_SUCC;
		return state;
	}
	
	public int deleteObject(List<BaseRecord> recordList) {
		int state = Constants.STATE_OPERATOR_LOST;
		try {
			Session s = HibernateUtil.currentSession(hibernateConfigFile);
			Transaction tx = s.beginTransaction();
			for(BaseRecord br:recordList){
				try{
					s.delete(br);
				}catch (Exception e) {
					e.printStackTrace();
				}
				// 删除memcached缓存
				if (StrUtil.getBoolean(ConfigLoader.getInstance().getProps(
						Config.system_config).getProperty(Config.IS_USE_MEMCACHED))) {
					if (MemcachedUtil11411.getMemCachedClient() != null) {
						try {
							MemcachedUtil11411.getMemCachedClient().delete(recordClass.getName() + "#" + br.getBr_id());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				// 删除列表缓存
				removeListCache(br, true);
				// 分布式删除对象缓存
				removeFromCache(br.getBr_id()+"", true, true);
			}
			tx.commit();
		} catch (HibernateException e) {
			e.printStackTrace();
			return state;
		} finally {
			HibernateUtil.closeSession(hibernateConfigFile);
		}
		state = Constants.STATE_OPERATOR_SUCC;
		return state;
	}
	

	/**
	 * 创建一个数据库记录，并把对象放入本机缓存和memcached缓存。
	 * 
	 * @param record
	 *            BaseRecord
	 * @return BaseRecord 返回数据库中的对象，
	 */
	public int saveObject(BaseRecord record) {
		int state = Constants.STATE_OPERATOR_LOST;
		record.setBr_createDate(DateUtil.getNoDateTime(new Date()));
		record.setBr_createTime(System.currentTimeMillis());
		record.setBr_updateDate(record.getBr_createDate());
		record.setBr_updateTime(System.currentTimeMillis());
		Session s = HibernateUtil.currentSession(hibernateConfigFile);
		try {
			Transaction tx = s.beginTransaction();
			s.save(record);
			tx.commit();
			// 放入memcached缓存中
			 set2MemCachedServer(record);


			// 删除列表缓存
			removeListCache(record, true);

		} catch (HibernateException e) {
			e.printStackTrace();
			return state;
		} finally {
			HibernateUtil.closeSession(hibernateConfigFile);
		}
		state = Constants.STATE_OPERATOR_SUCC;
		return state;
	}
	
	@Override
	public int saveObject(List<BaseRecord> recordList) {
		int state = Constants.STATE_OPERATOR_LOST;
		
		Session s = HibernateUtil.currentSession(hibernateConfigFile);
		try {
			Transaction tx = s.beginTransaction();
			for(BaseRecord record:recordList){
				record.setBr_createDate(DateUtil.getNoDateTime(new Date()));
				record.setBr_createTime(System.currentTimeMillis());
				record.setBr_updateDate(record.getBr_createDate());
				record.setBr_updateTime(System.currentTimeMillis());
				set2MemCachedServer(record);
				// 删除列表缓存
				removeListCache(record, true);
				s.save(record);
			}
			tx.commit();
			// 放入memcached缓存中

		} catch (HibernateException e) {
			e.printStackTrace();
			return state;
		} finally {
			HibernateUtil.closeSession(hibernateConfigFile);
		}
		state = Constants.STATE_OPERATOR_SUCC;
		return state;
	}
	

	/**
	 * 更新一个数据库对象。如修改一个用户昵称时，不会影响任何排序，那么就不需要清除列表缓存。
	 * 
	 * @param record
	 *            要更新的对象
	 * @param clearListCache
	 *            true表示需要清除列表缓存 false表示不需要
	 * @return boolean
	 */
	public int updateObject(BaseRecord record, boolean clearListCache) {
		int state = Constants.STATE_OPERATOR_LOST;
		record.setBr_updateDate(DateUtil.getNoDateTime(new Date()));
		record.setBr_updateTime(System.currentTimeMillis());
		Session s = HibernateUtil.currentSession(hibernateConfigFile);
		try {
			Transaction ts = s.beginTransaction();
			s.update(record);
			ts.commit();
			// 重新设置memcached server的缓存
			 set2MemCachedServer(record);
			// 分布式删除对象缓存
			removeFromCache(record.getBr_id()+"", false, true);
			// 删除列表缓存
			if (clearListCache) {
				removeListCache(record, true);
			}
		} catch (HibernateException e) {
			e.printStackTrace();
			return state;
		} finally {
			HibernateUtil.closeSession(hibernateConfigFile);
		}
		state = Constants.STATE_OPERATOR_SUCC;
		return state;
	}

	/**
	 * 本地（localhost）调用，在jsp中调用isLocal一律用true。<br/>
	 * 自动删除列表缓存，列表缓存的key必须是由字段名称=字段值组成，如#boardId=1#threadId=3#state=1#<br>
	 * 所以删除时只要利用要删除的对象的字段值组成一个条件字符串，再看key中的条件是否满足这些条件就可以<br>
	 * 决定是否要删除这些缓存List<br>
	 * 这是一个比较好的自动删除缓存的办法<br>
	 * 
	 * @param bt
	 *            BaseRecord对象，如User
	 * @param isLocal
	 *            是否本地调用.
	 */
	public void removeListCache(BaseRecord bt, boolean isLocal) {
		if (isLocal) {
			// 分布式清除缓存
			if (StrUtil.getBoolean(ConfigLoader.getInstance().getProps(
					Config.system_config).getProperty(
					Config.IS_USE_DISTRIBUTED_DB_CACHE))) {
				String s123 = recordClass.getName() + "#removeFromListCache#"
						+ bt.getBr_id();
//				UdpSenderUtil.getInstance().sendAll(s123);
			}
		}
	}

	/**
	 * 自动删除列表缓存，列表缓存的key必须是由字段名称=字段值组成，如#boardId=1#threadId=3#state=1#<br>
	 * 所以删除时只要利用要删除的对象的字段值组成一个条件字符串，再看key中的条件是否满足这些条件就可以<br>
	 * 决定是否要删除这些缓存List<br>
	 * 这是一个比较好的自动删除缓存的办法<br>
	 * 暂时支持like语句，大于(>)语句，小于(<)语句，等于(=)语句的自动清除<br/>
	 * 已经实现了分布式清除缓存的功能！！！！！！！！！！！！！
	 * 
	 * @param bt
	 *            BaseRecord对象，如User
	 * @param rMap
	 *            缓存
	 */
	private void removeListCache2(BaseRecord bt, Map rMap,
			HashMap<String, String> fieldMap) {
		// System.out.println(rMap.size());
		if (rMap != null)
			rMap.clear();
	}
	
	/** 非即时更新数据库对象。<br/> 
	  * 利用缓存更新数据库，在压力特别大的时候用,比如在更新帖子的点击次数，这种情况没必要立即更新而且更新频繁所以采用缓存<br> 
	  * 这种情况一般不更新缓存，因为一般这种点击次数的修改不会影响排列次序，如果做影响排列顺序的修改（如优先级）则<br> 
	  * 必须用update()方法！<br> 
	  * @param record 需要update的对象 * @return boolean 
	  */ 
	public boolean putToUpdateMap(BaseRecord br){ 
		
		// 放入memcached缓存中
		 set2MemCachedServer(br);
		return true;
	}

	

	/**
	 * 分布式从缓存中去掉对象，下次读取就会从memcached读取或者从数据库读取。在jsp或其他地方调用isLocal一律用true。
	 * 
	 * @param id
	 *            the id
	 * @param realRemove
	 *            是否真的删除，当删除数据库时调用
	 * @param isLocal
	 *            是否是本地调用
	 */
	public void removeFromCache(String id, boolean realRemove, boolean isLocal) {
		if (isLocal) {
			// 分布式清除缓存，发UDP报文，通知其他服务器删除缓存
			if (StrUtil.getBoolean(ConfigLoader.getInstance().getProps(
					Config.system_config).getProperty(
					Config.IS_USE_DISTRIBUTED_DB_CACHE))) {
				String s = recordClass.getName() + "#removeFromCache#" + id;
//				UdpSenderUtil.getInstance().sendAll(s);
			}
		}

		if (realRemove || !isLocal) {
		}
	}

	/**
	 * 从memcached server获取对象，对象必须实现java.io.Serializable。
	 * 
	 * @param key
	 *            远程的key。
	 * @return BaseRecord对象
	 */
	public BaseRecord getFromMemCachedServer(String key) {
		if (key == null)
			return null;
		if (StrUtil.getBoolean(ConfigLoader.getInstance().getProps(
				Config.system_config).getProperty(Config.IS_USE_MEMCACHED))) {
			if (MemcachedUtil11411.getMemCachedClient() != null) {
				try {
					Object ret = MemcachedUtil11411.getMemCachedClient().get(key);
					if (ret != null) {
						BaseRecord br = (BaseRecord) ret;
						if (br != null) {
							return br;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 把om对象放入memcached server。key是像com.chongai.om.Node#13434这样的字符串,后面的数字是对应的id
	 * 
	 * @param om
	 *            BaseRecord对象，可以是任何继承BaseRecord的对象
	 */
	public void set2MemCachedServer(BaseRecord br) {
		if (br == null)
			return;
		if (StrUtil.getBoolean(ConfigLoader.getInstance().getProps(
				Config.system_config).getProperty(Config.IS_USE_MEMCACHED))) {
			if (MemcachedUtil11411.getMemCachedClient() != null) {
				try {
					MemcachedUtil11411.getMemCachedClient().set(br.getClass().getName() + "#" + br.getBr_id(), 
							Integer.parseInt(ConfigLoader.getInstance().getProps(
							Config.system_config).getProperty(Config.MEMCACHED_EXPIRE_TIME)), br);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String getHibernateConfigFile() {
		return hibernateConfigFile;
	}

	public void setHibernateConfigFile(String hibernateConfigFile) {
		this.hibernateConfigFile = hibernateConfigFile;
	}

	public String getHashFieldsList() {
		return hashFieldsList;
	}

	public void setHashFieldsList(String hashFieldsList) {
		this.hashFieldsList = hashFieldsList;
	}

	public Class getRecordClass() {
		return recordClass;
	}

	public void setRecordClass(Class recordClass) {
		this.recordClass = recordClass;
	}
	
	




	
}
