package com.common.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class WebUtil {
	
	/***
	 * 得到服务器地址
	 * @param request
	 * @return
	 */
	public static String getServerUrl(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer();
		sb.append(request.getScheme());
		sb.append("://");
		sb.append(request.getServerName());
		sb.append(":");
		sb.append(request.getServerPort());
		sb.append(request.getContextPath());
		return sb.toString();
	}
	
	
	public static String getHost(String url){
		  if(url==null||url.trim().equals("")){
		   return "";
		  }
		  String host = "";
		  Pattern p =  Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");
		  Matcher matcher = p.matcher(url);  
		  if(matcher.find()){
			  host = matcher.group();  
		  }
		  return host;
	 }
	
	public static String[] getHostAndUri(String url){
		String [] hostAndUri=new String[2];
		if(StringUtils.isBlank(url)){
			hostAndUri[0]="";
			hostAndUri[1]="";
			return hostAndUri;
		}
		String host=getHost(url);
		hostAndUri[0]=host;
		if(url.startsWith("http://")){
			hostAndUri[1]=url.replaceAll("http://"+host, "");
		}else if(url.startsWith("https://")){
			hostAndUri[1]=url.replaceAll("https://"+host, "");
		}
		return hostAndUri;
	}
	
	public static void main(String[] arg){
		System.out.println(WebUtil.getHost("http://bbs.dcc.org/index.jsp?111"));
	}
	
}
