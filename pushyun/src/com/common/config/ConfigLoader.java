package com.common.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;
import java.util.Properties;
/**
 * 
 * <br/>配置文件加载实现类<br/>
 * <br/>conf/sys.properties<br/>
 * @author 
 * @see 
 */
public class ConfigLoader {
	private ConfigLoader() {
	}

	private static ConfigLoader singleton = null;

	/**
	 * save all the config files' properties;
	 */
	private Hashtable configProps = new Hashtable();

	/**
	 * save all the config files 'last modify time;
	 */
	private Hashtable modifyHash = new Hashtable();

	public static synchronized ConfigLoader getInstance() {
		if (singleton == null) {
			singleton = new ConfigLoader();
		}
		return singleton;
	}

	/**
	 * 从用户目录变量定义的路径中加载配置文件
	 * 
	 * @param fileName
	 *            ---config file name
	 * @return
	 */
	public Properties getFromUserDir(String fileName) throws IOException {
		Properties props = null;
		String userDir = System.getProperties().getProperty("user.dir");
		if (userDir != null && userDir.trim().length() != 0) {
			String cfgPath = userDir
					+ System.getProperties().getProperty("file.separator")
					+ "conf";
			File configFile = new File(cfgPath, fileName);
			if (configFile.exists()) {
				props = new Properties();
				props.load(new FileInputStream(configFile));
				if (props != null) {
					String filePath = configFile.getCanonicalPath();
					System.getProperties().setProperty("javapt." + fileName,
							filePath);
				}
			}
		}

		return props;

	}

	/**
	 * 取得配置文件的属性
	 * 
	 * @param configFile
	 * @return
	 */
	public Properties getProps(String configFile) {
		try {
			if (configFile == null || configFile.length() == 0) {
				throw new IllegalArgumentException(
						"file name must not be empty or null!");
			}
			// if (!configFile.endsWith(".conf"))
			// configFile = configFile.trim() + ".conf";
			Properties props = (Properties) configProps.get(configFile);
			if (props != null) {
				long lastTime = 0;
				Long lastLong = (Long) modifyHash.get(configFile);
				if (lastLong == null)
					return this.initialProps(configFile);
				else
					lastTime = lastLong.longValue();
				long nowTime = getFile(configFile).lastModified();
				if (nowTime > lastTime) {
					// logger.info("the " + configFile + " has changed!");
					System.err.println("the " + configFile + " has changed!");
					return initialProps(configFile);
				} else
					return props;
			} else {
				props = this.initialProps(configFile);
				return props;
			}
		} catch (Exception ex) {
			return null;
		}
	}

	private Properties initialProps(String configFile) throws IOException {
		Properties props = this.loadFile(configFile);
		long modifyTime = this.getFile(configFile).lastModified();
		modifyHash.put(configFile, (Long) new Long(modifyTime));// 存放更新时间
		configProps.put(configFile, props);
		return props;

	}

	/**
	 * load指定的输入流
	 * 
	 * @param InputStream
	 *            ----- 输入流
	 * @return ----a Properties object contain all the configuration properties;
	 */
	public Properties loadFile(InputStream input) throws IOException {
		Properties props = new Properties();
		try {
			props.load(input);
			input.close();
			input = null;
			return props;
		} catch (Exception e) {
			throw new IOException("there is a error when load the input !");
		}
	}

	/**
	 * load指定配置文件
	 * 
	 * @param name
	 *            -------文件名
	 * @return
	 * @throws IOException
	 */
	public Properties loadFile(String configFile) throws IOException {
		Properties props = this.getFromUserDir(configFile);
		if (props != null)
			return props;
		URL url = this.getClass().getClassLoader().getResource(configFile);
		if (url == null) {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			url = loader.getResource(configFile);
		}

		if (url != null) {
			return loadConfigFile(url.openStream());
		}
		return props;

	}

	/**
	 * load input stream to Property object
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public Properties loadConfigFile(InputStream is) throws IOException {
		Properties props = new Properties();
		try {
			props.load(is);
			return props;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				is.close();
				is = null;
			} catch (Exception e) {
				
			}
		}
		return props;

	}

	/**
	 * get the File object of the assigned file name String;
	 * 
	 * @param fileName
	 * @return
	 */
	public File getFile(String fileName) throws IOException {
		//String filePath = null;
		String filePath = null ;
		if(System.getProperty("user.dir") != null){
		    String userDir = System.getProperty("user.dir");
			filePath =  userDir+ System.getProperties().getProperty("file.separator")+"conf"+System.getProperties().getProperty("file.separator")+fileName;
			//System.out.println(filePath);
			File rtFile = new File(filePath);
			if(rtFile.exists()){
				return rtFile;
			}
		}
		URL url = this.getClass().getClassLoader().getResource(fileName);
		if (url == null) {
			// lets try the threads class loader
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			url = loader.getResource(fileName);
		}
		if (url != null) {
			filePath = url.getFile();
			// System.getProperties().setProperty( fileName, filePath);
		} else
			throw new IOException("could not found the config file:" + fileName
					+ "!");
		return new File(filePath);
	}

	public static void main(String[] args) {
//		ConfigLoader cl = ConfigLoader.getInstance();
//		try {
//			String centerIP = ConfigLoader.getInstance().getProps(
//					SysConstants.PLATFORMCONFIGFILE).getProperty("mc.ip",
//					"127.0.0.2").trim();
//			System.out.println("centerIP is :" + centerIP);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
		/*System.out.println(Thread.currentThread().getContextClassLoader().getResource("")); 

		System.out.println(ConfigLoader.class.getClassLoader().getResource("")); 

		System.out.println(ClassLoader.getSystemResource("")); 

		System.out.println(ConfigLoader.class.getResource("")); 

		System.out.println(ConfigLoader.class.getResource("/")); //Class文件所在路径 

		System.out.println(new File("/").getAbsolutePath()); 

		System.out.println(System.getProperty("user.dir")); */
	}
}
