package com.common.config;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

/**
 * 
 * 
 * 处理pushurl缓存
 * @author 
 * @see 
 */
public class MemcachedUtil11411 {

	/**
	 * memcached client
	 */
	private static  MemcachedClient memcachedClient = null;
	
	
	private static Configuration configuration = null ;

	static {
		DefaultConfigurationBuilder   builder = new DefaultConfigurationBuilder();   
		try {
			configuration = builder.build(SpringConfig.class.getResourceAsStream("/memcached11411.xml"));
		}  catch (Exception e) {
			e.printStackTrace();
		}
		for(int i=0;i<configuration.getChildren("socketpool").length;i++){
			Configuration socketpool = configuration.getChildren("socketpool")[i];
			Configuration[] servers = socketpool.getChildren("servers");
			try {
				MemcachedClientBuilder memcachedClientBuilder= new XMemcachedClientBuilder(AddrUtil.getAddresses(servers[0].getValue()));
				memcachedClientBuilder.setConnectionPoolSize(100);
				memcachedClientBuilder.setFailureMode(false);
				memcachedClientBuilder.setOpTimeout(10000);
				memcachedClient=memcachedClientBuilder.build();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 得到一个memcached client
	 * 
	 * @return
	 */
	public static MemcachedClient getMemCachedClient() {
		return memcachedClient;
	}


}
