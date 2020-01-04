package com.common.config;

import java.util.HashMap;

import org.hibernate.CacheMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * 
 * 
 * 可以根据不同的Hibernate.cfg.xml配置不同的数据库，简单实现按表拆分的分布式。<br/>
 * @author 
 * @see 
 */
public class HibernateUtil {
	public  static HashMap<String,ThreadLocal<Session>> threadLocalMap  =  new HashMap<String,ThreadLocal<Session>>();   

	private static HashMap<String,SessionFactory> sessionFactoryMap = new HashMap<String,SessionFactory>();;

	/**
     * SessionFactory 池
     * @param configFile hibernate配置文件名，如/hibernate.cfg.xml
     * @return
     */
    private static SessionFactory getSessionFactory(String configFile){
    	if(sessionFactoryMap.containsKey(configFile)){
    		return (SessionFactory)sessionFactoryMap.get(configFile);
    	}else{
    		try {
    			System.out.println("building sessionfactory...");
    			Configuration cfg = new Configuration();
    			cfg.configure(configFile);
    			SessionFactory sessionFactory = cfg.buildSessionFactory();
    			sessionFactoryMap.put(configFile, sessionFactory);
    			return sessionFactory;
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	return null;
    }
 
    public static Session currentSession(String configFile) throws HibernateException{
    	ThreadLocal<Session> tl = threadLocalMap.get(configFile);
    	if(tl==null){
    		tl = new ThreadLocal<Session>();
    		threadLocalMap.put(configFile, tl);
    	}
    	
    	Session session = (Session) tl.get();
    	

		if (session == null || !session.isOpen()) {
			SessionFactory sessionFactory = getSessionFactory(configFile);
			session = (sessionFactory != null) ? sessionFactory.openSession()
					: null;
			tl.set(session);
		}
		
		//去掉hibernate自己的缓存，否则分布式时会不正常
    	session.setCacheMode(CacheMode.IGNORE);
    	session.clear();
		return session;
			
    }

    public static void closeSession(String configFile) throws HibernateException{
    	ThreadLocal<Session> tl = threadLocalMap.get(configFile);
    	if(tl==null){
    		tl = new ThreadLocal<Session>();
    		threadLocalMap.put(configFile, tl);
    	}
    	
    	Session session = (Session) tl.get();
    	tl.set(null);

        if (session != null) {
            session.close();
        }
    }

}
