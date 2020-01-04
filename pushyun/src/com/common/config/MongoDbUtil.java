package com.common.config;

import java.net.UnknownHostException;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;

public class MongoDbUtil {
	
	private static MongoDbUtil mdh;
	
	private static MongoClient mongoClient ;
	
	private static MongoDatabase mongoDatabase;
	
	public static MongoDbUtil getInstance() throws UnknownHostException,MongoException {
		if (mdh == null) {
			synchronized (MongoDbUtil.class) {
				mongoClient=new MongoClient(ConfigLoader.getInstance().getProps(Config.system_config).getProperty(Config.mongo_db_url));
				mongoDatabase=mongoClient.getDatabase(ConfigLoader.getInstance().getProps(Config.system_config).getProperty(Config.mongo_db_name));
				mdh = new MongoDbUtil();
			}
		}
		return mdh;
	}
	
	public MongoClient getMongo() {
		return mongoClient;
	}
	
	public MongoDatabase getMongodb(){
		return mongoDatabase;
	}
	
}
