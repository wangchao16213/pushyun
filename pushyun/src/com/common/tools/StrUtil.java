package com.common.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Inflater;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;

import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.bean.BaseRecord;
import com.common.config.Config;
import com.common.config.ConfigLoader;
import com.common.type.EnumMessage;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
/**
 * 
 * 字符串工具类<br/>
 * @author 
 * @see 
 */
public class StrUtil {
	
	public static String readTxtFile(String filePath){
		 StringBuilder resultsb= new StringBuilder();
		 BufferedReader br=null;
	     try{
	        br = new BufferedReader(new FileReader(filePath));
	        String s = null;
	        while((s = br.readLine())!=null){
	        	resultsb.append(s);
	        }  
	      }catch(Exception e){
	            e.printStackTrace();
	      }finally{
	    	  if(br!=null){
	    		  try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    		  br=null;
	    	  }
	      }
	     return resultsb.toString();
	}
	
	
	public static void getFiles(String filePath,List<String> fileList){
		File root = new File(filePath);
		File[] files = root.listFiles();
		 for(File file:files){     
		    if(file.isDirectory()){
		       getFiles(file.getAbsolutePath(),fileList);
		    }else{
		      fileList.add(file.getAbsolutePath()); 
		    }     
		 }
	}
	
	
	 public static List<List<String>> segmentationList(List<String> targe, int size) {  
	     List<List<String>> listArr = new ArrayList<List<String>>();
		// 获取被拆分的数组个数
		int arrSize = targe.size() % size == 0 ? targe.size() / size : targe
				.size()
				/ size + 1;
		for (int i = 0; i < arrSize; i++) {
			List<String> sub = new ArrayList<String>();
			// 把指定索引数据放入到list中
			for (int j = i * size; j <= size * (i + 1) - 1; j++) {
				if (j <= targe.size() - 1) {
					sub.add(targe.get(j));
				}
			}
			listArr.add(sub);
		}
		return listArr;  
	}  
	
	
	public static String getHost(String url) {  
	    if (!(StringUtils.startsWithIgnoreCase(url, "http://") || StringUtils  
	            .startsWithIgnoreCase(url, "https://"))) {  
	        url = "http://" + url;  
	    }  
	  
	    String returnVal = StringUtils.EMPTY;  
	    try {  
	        Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");  
	        Matcher m = p.matcher(url);  
	        if (m.find()) {  
	            returnVal = m.group();  
	        }  
	    } catch (Exception e) {  
	    	
	    }  
	    if ((StringUtils.endsWithIgnoreCase(returnVal, ".html") || StringUtils  
	            .endsWithIgnoreCase(returnVal, ".htm"))) {  
	        returnVal = StringUtils.EMPTY;  
	    }  
	    return returnVal;  
	}  
	
	
	public static String getMacBySetFormat(String orgmac){
		return orgmac.toLowerCase().replaceAll("-", "");
	}
	
	
	public static String getDisplay(String className,String code){
		Class c =null;
		try {
			c = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Map<String, EnumMessage> enumMap=EnumUtil.getEnumValues(c);
		for(String s:enumMap.keySet()){
			if(s.equals(code)){
				return enumMap.get(code).getDisplay();
			}
		}
		return "";
	}
	
	public static String getCode(String className,String display){
		if(StringUtils.isBlank(display)){
			return "";
		}
		Class c =null;
		try {
			c = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Map<String, EnumMessage> enumMap=EnumUtil.getEnumValues(c);
		for(String s:enumMap.keySet()){
			String d=enumMap.get(s).getDisplay();
			if(d.equals(display)){
				return s;
			}
		}
		return "";
	}
	
	
	
	public static String getStartIp(String ip){
		if(StringUtils.isBlank(ip)){
			return "";
		}
		String[]  ips =ip.split("\\.");
		if(ips.length<4){
			return "";
		}
		return ips[0]+"."+ips[1]+"."+ips[2]+".0";
	}
	
	public static String getEndIp(String ip){
		if(StringUtils.isBlank(ip)){
			return "";
		}
		String[]  ips =ip.split("\\.");
		if(ips.length<4){
			return "";
		}
		return ips[0]+"."+ips[1]+"."+ips[2]+".255";
	}
	
	
	 public static String[] getNullPropertyNames(Object source) {
			final BeanWrapper src = new BeanWrapperImpl(source);
			java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
			Set<String> emptyNames = new HashSet<String>();
			for (java.beans.PropertyDescriptor pd : pds) {
				try{
					Object srcValue = src.getPropertyValue(pd.getName());
					if (srcValue == null){
						emptyNames.add(pd.getName());
					}	
				}catch (Exception e) {
				}
			}
			String[] result = new String[emptyNames.size()];
			return emptyNames.toArray(result);
		}
	
	

	
	 public static boolean isChinese(char c) {    
		    Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);    
		    if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS 
		    		|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
	                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A 
	                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
	                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION 
	                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
	                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {   
		      return true;    
		    }    
		    return false;    
	 }    
	 
	 public static String string2Unicode(String s){
		char[] ch = s.toCharArray();
		StringBuffer sb=new StringBuffer();
		for (int i = 0; i < ch.length; i++) {
		     char c = ch[i];
		     if (StrUtil.isChinese(c)){
		         sb.append("\\u" + Integer.toString(c, 16));
		     }else{
		         sb.append(c);
		     }
		}
		return sb.toString();
	}
   
	 public static boolean isMessyCode(String strName) {    
		   Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");    
		   Matcher m = p.matcher(strName);    
		   String after = m.replaceAll("");    
		   String temp = after.replaceAll("\\p{P}", "");    
		   char[] ch = temp.trim().toCharArray();      
		   for (int i = 0; i < ch.length; i++) {    
		   char c = ch[i];    
		   if (!Character.isLetterOrDigit(c)) {    
		        if (!isChinese(c)) {    
		        	return true;     
		        }    
		      }    
		   }    
		  return false;      
	}    
	 
	
	
	
	
	
	public static String getMemcachedKey(String itemId,String area,String type){
		String key=String.format("%s_%s_%s", itemId,area,type);
		return key;
	}
	
	/**
	 * Change string path with correct file separator
	 * @param path
	 * @return
	 */
	public static String getStringWithFileSeparator(String path){
		String s="";
		for(int i=0; i<path.length(); i++)
			if(path.charAt(i)=='\\'){
				s+=File.separator;
			}else{
				s+=path.charAt(i);
			}

		return s;
	}
	
	public static void cycleDirectory(File f,List<String> filePathList){
		//判断传入对象是否为一个文件夹对象
		if(!f.isDirectory()){
			return ;
		}
		else{
			File[] t = f.listFiles();
			for(int i=0;i<t.length;i++){
				//判断文件列表中的对象是否为文件夹对象，如果是则执行tree递归，直到把此文件夹中所有文件输出为止
				if(t[i].isDirectory()){
					cycleDirectory(t[i],filePathList);
				}else{
					filePathList.add(t[i].getPath());
				}
			}
		}
		return ;
	}
	
	public static Date getEndTime(Date currDateTime,String timeunits,Float feetime){
		Calendar celendar = Calendar.getInstance(); // 当时的日期和时间
		celendar.setTime(currDateTime);
		if (timeunits.equals("day")) {
			celendar.set(Calendar.DATE, celendar.get(Calendar.DATE) + feetime.intValue());
		}else if (timeunits.equals("month")) {
			celendar.set(Calendar.MONTH, celendar.get(Calendar.MONTH) +feetime.intValue());
		}else if (timeunits.equals("year")) {
			celendar.set(Calendar.YEAR, celendar.get(Calendar.YEAR) + feetime.intValue());
		}
		return celendar.getTime();
	}
	
	
	/**
	 * 固定长度字符串
	 * @param text
	 * @param num
	 * @return
	 */
	public static String[] getFixedText(String text,int num){
		List<String> fixedList=new ArrayList<String>();
		if(text.length()<=num){
			fixedList.add(text);
		}else{
			int smsnum=text.length()%num==0?text.length()/num:(text.length()/num+1);
			for(int i=0;i<smsnum;i++){
				if((i+1)*num>text.length()){
					fixedList.add(text.substring(i*num));
				}else{
					fixedList.add(text.substring(i*num, (i+1)*num));
				}
			}
		}
		return fixedList.toArray(new String[fixedList.size()]);
	}
	
	
	public static void printObjectField(Object object) {
		try {
			StringBuilder sb = new StringBuilder();
			Field[] fields = object.getClass().getDeclaredFields();
			for (Field field : fields) {
				try {
					Object value = field.get(object);
					if (value != null) {
						sb.append(field.getName() + "\t" + value);
						sb.append("\t\n");
					}
				} catch (Exception e) {

				}
			}
			System.out.println(sb.toString());
		} catch (Exception e) {

		}
	}
	
	public static Object getter(Object obj, String attr) {// 调用getter方法
		try {
			Method method = obj.getClass().getMethod("get" + initStr(attr));// 此方法不需要参数，如：getName(),getAge()
			return method.invoke(obj);
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
	}
	public static String initStr(String old) {// 单词首字母大写
		String str = old.substring(0, 1).toUpperCase() + old.substring(1);
		return str;
	}
	
	
	public static String[] getBaseRecordId(List<Object> objs){
		String[] result=new String[objs.size()];
		try{
			for(int i=0;i<objs.size();i++){
				BaseRecord br=(BaseRecord)objs.get(i);
				result[i]=br.getBr_id()+"";
			}
		} catch (Exception e) { 
			e.printStackTrace(); 
		} 
		return result;
	}
	
	/**
	 * 
	 * @param keybyte 为加密密钥，长度为24字节
	 * @param src 解压ZLIB压缩
	 * @return
	 */
	public static byte[] decryptMode(byte[] keybyte, byte[] src) { 
		String Algorithm = "DESede"; //定义 加密算法,可用 
		try { 
			//生成密钥 
			SecretKey deskey = new SecretKeySpec(keybyte, Algorithm); 
			//解密 
			Cipher c1 = Cipher.getInstance(Algorithm); 
			c1.init(Cipher.DECRYPT_MODE, deskey); 
			return c1.doFinal(src); 
		} catch (Exception e3) { 
			e3.printStackTrace(); 
		} 
		return null; 
	} 
	/**
	 * 解压ZLIB压缩
	 * @param input
	 * @return
	 */
	public static byte[] getExtractForZLIB(byte[] input){
		byte[] output = new byte[0];
		Inflater decompresser = new Inflater();
		decompresser.reset();
		decompresser.setInput(input);
		ByteArrayOutputStream o = new ByteArrayOutputStream(input.length);
		try {
			byte[] buf = new byte[1024];
			while (!decompresser.finished()) {
				int i = decompresser.inflate(buf);
				o.write(buf, 0, i);
			}
			output = o.toByteArray();
		} catch (Exception e) {
			output = input;
			e.printStackTrace();
		} finally {
			try {
				o.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		decompresser.end();
		return output;
	}
	
	public static String getStr(String input,int length,String type) {
		int sig=length-input.length();
		StringBuffer sb=new StringBuffer();
		if(type.equals("pre")){
			for(int i=0;i<sig;i++){
				sb.append("0");
			}
			sb.append(input);
		}else{
			return input;
		}
		return sb.toString();
	}
	


	/**
	 * 半角转全角
	 * 
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	/**
	 * 得到操作动作
	 * @param url
	 * @return
	 */
	public static String getAction(String url) {
		String[] temp = url.split("/");
		return temp[temp.length - 1];
	}

	
	public static String getObject(Object object, int point) {
		String result = String.valueOf(object);
		if (result.length() >= point) {
			result = result.substring(0, point);
		}
		return result;
	}

	/**
	 * 功能:转换context第一个字母为大小,并且在context前面加上regex
	 * 
	 * @param context
	 * @param regex
	 * @return
	 */
	public static String getUpperCase(String context, String regex) {
		StringBuffer sb = new StringBuffer();
		String upper = context.substring(0, 1).toUpperCase();
		sb.append(regex);
		sb.append(upper);
		sb.append(context.substring(1, context.length()));
		return sb.toString();
	}

	/**
	 * 返回当前的年份
	 * @return
	 */
	public static int getCurrYear() {
		String temp = new SimpleDateFormat("yyyy").format(new Date());
		return Integer.parseInt(temp);
	}

	/**
	 * 
	 * @return
	 */
	public static String getCurrTime() {
		return new SimpleDateFormat("HH:mm:ss").format(new Date());
	}

	/**
	 * 生成随机数
	 * 
	 * @param head
	 * @param context
	 * @return
	 */
	public static String getGsid(int min, int max) {
		String s = "";
		try {
			Random random = new Random();
			s = String.valueOf(random.nextInt(max) % (max - min + 1) + min);
		} catch (Exception e) {

		}
		return s;
	}

	/**
	 * 得到编码的级别
	 * 三位为一个级别
	 * @param d
	 * @return
	 */
	public static int getLevel(String code) {
		int result = 1;
		if (StringUtils.isBlank(code)) {
			return result;
		}
		int codelength = code.length() - 3;
		return codelength / 3 + 1;
	}

	/**
	 * 转化图像大小
	 * @param fileInput
	 * @param fileOutput
	 * @param formatName
	 * @param width
	 * @param height
	 */
	public static void convertImage(String fileInput, String fileOutput,
			String formatName, int width, int height) {
		try {
			File fi = new File(fileInput); // 大图文件
			File fo = new File(fileOutput); // 将要转换出的小图文件
			int nw = width; // 定义宽
			int nh = height; // 定义高
			AffineTransform transform = new AffineTransform();
			BufferedImage bis = ImageIO.read(fi);
			int w = bis.getWidth();
			int h = bis.getHeight();

			double sx = (double) nw / w;
			double sy = (double) nh / h;
			// 判断是横向图形还是坚向图形
			if (w > h) // 横向图形
			{
				if ((int) (sx * h) > nh) // 比较高不符合高度要求,就按高度比例
				{
					sx = sy;
					nw = (int) (w * sx);
				} else {
					sy = sx;
					nh = (int) (h * sy);
				}
			} else {
				if ((int) (sy * w) > nw) {
					sy = sx;
					nh = (int) (h * sy);
				} else {
					sx = sy;
					nw = (int) (w * sx);
				}
			}

			transform.setToScale(sx, sy);
			AffineTransformOp ato = new AffineTransformOp(transform, null);
			BufferedImage bid = new BufferedImage(nw, nh,
					BufferedImage.TYPE_3BYTE_BGR);
			ato.filter(bis, bid);
			ImageIO.write(bid, formatName, fo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 计算中文和英文所占位数
	 * @param fData
	 * @return
	 */
	public static int dataLength(String fData) {
		int intLength = 0;
		for (int i = 0; i < fData.length(); i++) {
			if ((fData.charAt(i) < 0) || (fData.charAt(i) > 255)) {
				intLength = intLength + 2;
			} else {
				intLength = intLength + 1;
			}
		}
		return intLength;
	}

	public static boolean getBoolean(String s) {
		if ("true".equalsIgnoreCase(s))
			return true;
		return false;
	}

	public static boolean checkMobile(String mobile) {
		boolean result = false;
		if (StringUtils.isBlank(mobile)) {
			return result;
		}
		String pattern = "^[1][0-9][0-9]{9}";
		if (mobile.matches(pattern)) {
			return true;
		}
		pattern = "^0\\d$";
		if (mobile.matches(pattern)) {
			return true;
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	public static String getAppPath(Class cls) {
		// 检查用户传入的参数是否为空
		if (cls == null)
			throw new java.lang.IllegalArgumentException("参数不能为空！");
		ClassLoader loader = cls.getClassLoader();
		// 获得类的全名，包括包名
		String clsName = cls.getName() + ".class";
		// 获得传入参数所在的包
		Package pack = cls.getPackage();
		String path = "";
		// 如果不是匿名包，将包名转化为路径
		if (pack != null) {
			String packName = pack.getName();
			// 此处简单判定是否是Java基础类库，防止用户传入JDK内置的类库
			if (packName.startsWith("java.") || packName.startsWith("javax."))
				throw new java.lang.IllegalArgumentException("不要传送系统类！");
			// 在类的名称中，去掉包名的部分，获得类的文件名
			clsName = clsName.substring(packName.length() + 1);
			// 判定包名是否是简单包名，如果是，则直接将包名转换为路径，
			if (packName.indexOf(".") < 0)
				path = packName + "/";
			else {// 否则按照包名的组成部分，将包名转换为路径
				int start = 0, end = 0;
				end = packName.indexOf(".");
				while (end != -1) {
					path = path + packName.substring(start, end) + "/";
					start = end + 1;
					end = packName.indexOf(".", start);
				}
				path = path + packName.substring(start) + "/";
			}
		}
		// 调用ClassLoader的getResource方法，传入包含路径信息的类文件名
		java.net.URL url = loader.getResource(path + clsName);
		// 从URL对象中获取路径信息
		String realPath = url.getPath();
		// 去掉路径信息中的协议名"file:"
		int pos = realPath.indexOf("file:");
		if (pos > -1)
			realPath = realPath.substring(pos + 5);
		// 去掉路径信息最后包含类文件信息的部分，得到类所在的路径
		pos = realPath.indexOf(path + clsName);
		realPath = realPath.substring(0, pos - 1);
		// 如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名
		if (realPath.endsWith("!"))
			realPath = realPath.substring(0, realPath.lastIndexOf("/"));
		/*------------------------------------------------------------  
		 ClassLoader的getResource方法使用了utf-8对路径信息进行了编码，当路径  
		  中存在中文和空格时，他会对这些字符进行转换，这样，得到的往往不是我们想要  
		  的真实路径，在此，调用了URLDecoder的decode方法进行解码，以便得到原始的  
		  中文及空格路径  
		-------------------------------------------------------------*/
		try {
			realPath = java.net.URLDecoder.decode(realPath, "utf-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return realPath;
	}

	public static String getGsid(Date d) {
		return new SimpleDateFormat("ddHHmmssMM").format(d);
	}

	/**
	 * 获取数字随机数
	 * 
	 * @param size
	 *            随机数个数
	 * @return 指定个数的数字随机数的字符串
	 */
	public static String getRandom(int size) {
		Random random = new Random();
		StringBuilder sb = new StringBuilder(size);
		for (int i = 0; i < size; i++) {
			sb.append(random.nextInt(9));
		}
		return sb.toString();
	}

	/**
	 * 根据随即码以及宽高绘制图片函数
	 * 
	 * @param g
	 *            Graphics
	 * @param width
	 *            图片宽度
	 * @param height
	 *            图片高度
	 * @param randomCode
	 *            随机码
	 */
	public static void drawRandomPicture(Graphics g, int width, int height,
			String randomCode) {
		g.setColor(randColor(200, 250));
		g.fillRect(0, 0, width, height);
		g.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		g.setColor(randColor(160, 200));

		Random random = new Random(System.currentTimeMillis());

		// ?随机产生155条干扰线，使图像中的验证码不易被识别
		for (int i = 0; i < 155; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(12);
			int yl = random.nextInt(12);
			g.drawLine(x, y, x + xl, y + yl);
		}

		// ?将验证码显示在图像中
		for (int i = 0; i < 4; i++) {
			g.setColor(randColor(20, 130));
			g.drawString(randomCode.substring(i, i + 1), 13 * i + 6, 16);
		}
	}

	/**
	 * 获得随机色
	 * 
	 * @param fc
	 *            前景色
	 * @param bc
	 *            背景色
	 * @return 随机色
	 */
	private static Color randColor(int fc, int bc) {
		Random random = new Random(System.currentTimeMillis());
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String getLike(String str) {
		if (StringUtils.isBlank(str)) {
			return str;
		}
		return str.replaceAll(" ", "%");
	}
	
	/**
	 * 得到指定属性名称的值
	 * @param o
	 * @param filedName
	 * @return
	 */
	 @SuppressWarnings("rawtypes")
	public static Object getFileValue(Object o, String filedName)
	  {
	    Class classType = o.getClass();
	    Field fild = null;
	    Object fildValue = null;
	    try
	    {
	      fild = classType.getDeclaredField(filedName);
	      fildValue = fild.get(o);
	    }
	    catch (Exception ex)
	    {
	      ex.printStackTrace();
	    }
	    return fildValue;
	  }
	
	 public static String getServerUrl(String ip,String port,String webapp){
		 StringBuffer sb = new StringBuffer();
		 sb.append("http://");
		 sb.append(ip);
		 sb.append(":");
		 sb.append(port);
		 String[] s=webapp.split("/");
		 if(s.length==2){
			 sb.append(webapp);
		 }else{
			 sb.append("/");
			 sb.append(s[1]);
		 }
		 return sb.toString();
	 }
	 
	public static List<Object[]> getNotifyInetAddressList() {
		String c = ConfigLoader.getInstance().getProps(Config.system_config)
				.getProperty(Config.notify_ip_list);
		if (c == null || c.length() == 0)
			return null;
		String[] ips = c.split(";");
		List<Object[]> list = new ArrayList<Object[]>();
		if (ips == null || ips.length == 0)
			return null;

		for (int i = 0; i < ips.length; i++) {
			try {
				Object[] ip = new Object[2];
				ip[0] = InetAddress.getByName(ips[i].split(":")[0]);
				ip[1] = ips[i].split(":")[1];
				list.add(ip);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
			 
	public static String getTableName(String pre,String add, Date date) {
		StringBuffer sb = new StringBuffer();
		Calendar celendar = Calendar.getInstance(); // 当时的日期和时间
		celendar.setTime(date);
		sb.append(pre);
		sb.append("_");
		if(StringUtils.isNotBlank(add)){
			sb.append(add);
			sb.append("_");
		}
		sb.append(DateUtil.getDateTime(date, "yyyyMMdd"));
		return sb.toString();
	}
	
	public static String getChannelNo(String url) {
		if(url.indexOf("?")!=-1){
			String[] s1=url.split("\\?");
			if(s1[1].length()<5){
				return "guanfang";
			}
			if(s1[1].indexOf("&")!=-1){
				String[] s2=s1[1].split("&");
				return s2[0];
			}
			return s1[1];
		}
		return "guanfang";
	}
	


	
	
		
	public static String getUV(String ip,String userAgent) {
		return String.format("%s%s", ip.replaceAll("\\.", "_"),userAgent.replaceAll(" ", "").replaceAll("\\.", "_"));
	}
	
	public static String joinUrl(String curl, String file) {
		URL url = null;
		String q = "";
		try {
			url = new URL(new URL(curl), file);
			q = url.toExternalForm();
		} catch (MalformedURLException e) {

		}
		url = null;
		if (q.indexOf("#") != -1)
			q = q.replaceAll("^(.+?)#.*?$", "$1");
		return q;
	}
	
	
    //DBObject转换成JavaBean
	//测试已通过
	public static <T> T dbObject2Bean(DBObject dbObject, T bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {  
            if (bean == null) {  //测试已通过
                return null;  
            }  
            Field[] fields = bean.getClass().getDeclaredFields();  
            for (Field field : fields) {  
                String varName = field.getName();  
                if(varName.equals("id")){
                	Object object = dbObject.get("_id");  
                	 if(object!=null){
                      	BeanUtils.setProperty(bean, varName, object.toString());  
                      }
                }else{
                	Object object = dbObject.get(varName);  
                	 if(object!=null){
                     	BeanUtils.setProperty(bean, varName, object);  
                     }
                }
            }  
            return bean;  
     }
	


    @SuppressWarnings("unchecked")
	public static <T> DBObject bean2DBObject(T bean) throws IllegalArgumentException, IllegalAccessException {
		if (bean == null) {
			return null;
		}
		DBObject dbObject = new BasicDBObject();
		// 获取对象对应类中的所有属性域
		Field[] fields = bean.getClass().getDeclaredFields();
		for (Field field : fields) {
			// 获取属性名
			String varName = field.getName();
			// 修改访问控制权限
			boolean accessFlag = field.isAccessible();
			if (!accessFlag) {
				field.setAccessible(true);
			}
			Object param = field.get(bean);
			if (param == null) {
				continue;
			} else if (param instanceof Integer) {// 判断变量的类型
				int value = ((Integer) param).intValue();
				dbObject.put(varName, value);
			} else if (param instanceof String) {
				String value = (String) param;
				dbObject.put(varName, value);
			} else if (param instanceof Double) {
				double value = ((Double) param).doubleValue();
				dbObject.put(varName, value);
			} else if (param instanceof Float) {
				float value = ((Float) param).floatValue();
				dbObject.put(varName, value);
			} else if (param instanceof Long) {
				long value = ((Long) param).longValue();
				dbObject.put(varName, value);
			} else if (param instanceof Boolean) {
				boolean value = ((Boolean) param).booleanValue();
				dbObject.put(varName, value);
			} else if (param instanceof Date) {
				Date value = (Date) param;
				dbObject.put(varName, value);
			} else if (param instanceof List) {
				List<Object> list = (List<Object>) param;
				dbObject.put(varName, list);
			} else if (param instanceof Map) {
				Map<Object, Object> map = (Map<Object, Object>) param;
				dbObject.put(varName, map);
			}
			// 恢复访问控制权限
			field.setAccessible(accessFlag);
		}
		dbObject.put("_id", ((BaseRecord)bean).getBr_id());
		return dbObject;
	}
    

    public static JsonConfig getJsonConfig(){
		JsonConfig jsonConfig=new JsonConfig();
		 PropertyFilter filter = new PropertyFilter() {
	          public boolean apply(Object object, String fieldName, Object fieldValue) {
	        	  return null == fieldValue;
	          }
	    };
	    jsonConfig.setJsonPropertyFilter(filter);
	    return jsonConfig;
	}

		
	public static void main(String[] args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		BigDecimal d=new BigDecimal(1).multiply(new BigDecimal(50)).divide(new BigDecimal(1000));;
//		String s=new ObjectId().toString();
//		System.out.println(s);
//		System.out.println(d.floatValue());
//		
//		try {
//			Class c = TAllUrlMIsad.class;
////			Object b = c.newInstance();
//		
//			Method m = c.getMethod("getDisplay" ,String.class);
//			System.out.println(m);
//			String value =  (String) m.invoke(null, "99");
//			System.out.println(value);
//		}catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
}
