package com.common.config;
/**
 * 
 * <br/>配置文件参数<br/>
 * <br/>conf/sys.properties<br/>
 * @author 
 * @see 
 */
public class Config {

	public static final String system_config = "sys.properties";

	/**
	 * 程序是否存在验证端口
	 */
	public static final String server_verify_port = "server_verify_port";

	public static final String queue_send_core = "queue_send_core";

	public static final String queue_send_gw = "queue_send_gw";
	
	public static final String queue_send_timely = "queue_send_timely";
	
	
	/**
	 * jms pool name
	 */
	public static final String jms_pool_name = "jms_pool_name";
	/**
	 * jms池的最大值
	 */
	public static final String jms_pool_maxPoolSize = "jms_pool_maxPoolSize";

	/**
	 * jms池的最小值
	 */
	public static final String jms_pool_minPoolSize = "jms_pool_minPoolSize";

	/**
	 * 超时
	 */
	public static final String jms_pool_timeout = "jms_pool_timeout";

	/**
	 * jms url地址
	 */
	public static final String jms_pool_url = "jms_pool_url";

	/**
	 * jms 用户名
	 */
	public static final String jms_pool_user = "jms_pool_user";
	/**
	 * jms 密码
	 */
	public static final String jms_pool_password = "jms_pool_password";

	public static final String om_cache_lenght = "OM_CACHE_LENGTH";

	// 公用列表缓存长度，因为公用列表比较小，而且需要经常遍历，所以稍微小点。
	public static final String list_cache_lenght = "LIST_CACHE_LENGTH";

	// 公用长度缓存最大长度，也需要遍历，设置小一点。
	public static final String length_cache_lenght = "LENGTH_CACHE_LENGTH";

	// 二级列表缓存最大长度，是根据特定字段来做的散列缓存，不需要遍历，可以设置大一点。
	public static final String secondary_cache_lenght = "SECONDARY_CACHE_LENGTH";

	// 是否使用分布式，如果为true，则会执行分布式清除缓存操作
	public static final String IS_USE_DISTRIBUTED_DB_CACHE = "IS_USE_DISTRIBUTED_DB_CACHE";
	
	
	// 是否使用本地缓存
	public static final String IS_USE_LOCAL = "IS_USE_LOCAL";

	// 是否使用远程memcached缓存，不是分布式不要打开。
	public static final String IS_USE_MEMCACHED = "IS_USE_MEMCACHED";
	
	

	// 分布式清除缓存的UDP服务器端口，服务器的该端口和MEMCACHED_SERVER_PORT端口都要打开
	public static final String udp_server_port = "UDP_SERVER_PORT";

	// 需要分布式清除缓存的其他服务器IP，多服务器用;隔开,例如127.0.0.1:90;127.0.0.1:9090
	public static final String notify_ip_list = "NOTIFY_IP_LIST";

	// 远程缓存保留时间，240小时
	public static final String MEMCACHED_EXPIRE_TIME = "MEMCACHED_EXPIRE_TIME";

	// 远程缓存memcached server的IP
	public static final String MEMCACHED_SERVER = "MEMCACHED_SERVER";

	// 远程缓存memcached server的端口
	public static final String MEMCACHED_SERVER_PORT = "MEMCACHED_SERVER_PORT";

	
	 /**
	  * 每月启动规则
	  */
	 public static final String  month_cexp_rule="month_cexp_rule";
	 
	 /**
	  * 分钟启动规则
	  */
	 public static final String  minute_cexp_rule="minute_cexp_rule";
	 
	 /**
	  * 小时启动规则
	  */
	 public static final String  hour_cexp_rule="hour_cexp_rule";
	 
	 /**
	  * 每天启动规则
	  */
	 public static final String  day_cexp_rule="day_cexp_rule";
	 
	 
	 public static final String histrack_cexp_rule="histrack_cexp_rule";
	 
	 public static final String operation_cexp_rule="operation_cexp_rule";
	 
	 
	 public static final String record_cexp_rule="record_cexp_rule";
	 
	 
	 public static final String  camera_save_url="camera_save_url";
	 
	 public static final String  upload_save_url="upload_save_url";
	 
	 public static final String per_page_num = "per_page_num";
	 
	public static final String histrack_dir = "histrack_dir";

	public static final String IS_CORRECT = "IS_CORRECT";
	
	public static final String code_cache_num = "code_cache_num";
	
	public static final String trackbuffer_cache_num = "trackbuffer_cache_num";
	
	public static final String trackbuffer_cache_time = "trackbuffer_cache_time";
	
	public static final String update_cache_time = "update_cache_time";
	
	public static final String server_ip="server_ip";
	
	public static final String server_port="server_port";
	
	public static final String server_action="server_action";
	
	 public static final String  email_address="email.address";
	 
	 public static final String  email_username="email.username";
	 
	 public static final String  email_password="email.password";
	 
	 public static final String  email_smtp="email.smtp";
	 
	 public static final String  email_port="email.port";

	 public static final String  jmx_server_url="jmx.server.url";
	 
	 public static final String  jms_queue_num="jms.queue.num";
	 
	 
	 public static final String mongo_db_url = "mongo_db_url";
	 
	 public static final String mongo_db_name = "mongo_db_name";
	 
	 public static final String mongo_db_user = "mongo_db_user";
	
	 public static final String mongo_db_pwd = "mongo_db_pwd";
	
}
