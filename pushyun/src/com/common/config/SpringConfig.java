package com.common.config;

import java.util.HashMap;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.quartz.SchedulerException;

import com.common.service.BaseService;









/**
 * 
 * <br/> 简单的类装配实现<br/>
 * <br/>获取applicationContext.xml配置文件信息，实例化配置类 <br/>
 * @author 
 * @see 
 */

public class SpringConfig {

	private static SpringConfig me = null;

	public static SpringConfig getInstance() {
		if (me == null) {
			synchronized (SpringConfig.class) {
				if (me == null) {
					me = new SpringConfig();
				}
			}
		}
		return me;
	}
	
	/**
	 * 配置对象
	 */
	private static Configuration configuration = null ;
	
	/**
	 * 管理Manager的MAP
	 */
	private static HashMap<Class,Object> MANAGER_MAP = new HashMap<Class,Object>(); 

	private SpringConfig() {
		DefaultConfigurationBuilder   builder = new DefaultConfigurationBuilder();   
        try 
        {
        	configuration = builder.build(SpringConfig.class.getResourceAsStream("/applicationContext.xml"));
        	for(int i=0;i<configuration.getChildren("bean").length;i++){
        		Configuration child = configuration.getChildren("bean")[i];
        		String beanClassName = child.getAttribute("id");
        		String managerClassName = child.getAttribute("class");
        		String managerTypeName = child.getAttribute("type");
        		Class beanClass = Class.forName(beanClassName);
        		if(managerTypeName.equals("bean")){
        			MANAGER_MAP.put(beanClass, Class.forName(managerClassName).newInstance());
        		}else if(managerTypeName.equals("base")){
        			Object o=null;
        			try{
        				System.out.println(managerClassName);
        				 o=Class.forName(managerClassName).newInstance();
        			}catch (Exception e) {
						e.printStackTrace();
					}
        			if(o instanceof BaseService){
        				BaseService manager = (BaseService)o;
                		manager.setRecordClass(beanClass);
                		Configuration[] property = child.getChildren("property");
                		if(property!=null&&property.length>0){
                			for(int j=0;j<property.length;j++){
                				if("hibernateConfigFile".equals(property[j].getAttribute("name"))){
                					manager.setHibernateConfigFile(property[j].getChild("value").getValue());
                				}
                				System.out.println("property:name="+property[j].getAttribute("name")+" value="+property[j].getChild("value").getValue());
                			}
                		}
                		MANAGER_MAP.put(beanClass, manager);
        			}
        		}
        	}
		}
        catch(Exception ex){
        	ex.printStackTrace();
        } 
	}

	/**
	 * 根据className得到一个数据库管理类，必须是BaseDbManager的子类
	 * 
	 * @param className
	 *            class 名如 com.aa.bb.cc
	 * @return
	 */
	public Object getService(Class classname) {
		return MANAGER_MAP.get(classname);
	}
	
	public HashMap getService() {
		return MANAGER_MAP;
	}
}
