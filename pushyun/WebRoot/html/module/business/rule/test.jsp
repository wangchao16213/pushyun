<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.common.config.SpringConfig"%>
<%@page import="com.common.service.BaseService"%>
<%@page import="com.bean.BusinessChannel"%>
<%@page import="org.apache.commons.httpclient.HttpClient"%>
<%@page import="org.apache.commons.httpclient.methods.GetMethod"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="org.apache.commons.httpclient.HttpStatus"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="net.sf.json.JSONObject"%>
<%@page import="com.google.gson.JsonObject"%>
<%@page import="com.google.gson.JsonParser"%>
<%@page import="com.google.gson.JsonArray"%>
<%@page import="com.common.tools.StrUtil"%>
<%@page import="com.common.type.BusinessRulePushrate"%>
<%@page import="com.common.type.BusinessRuleDetailType"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String businessChannelId=request.getParameter("businessChannel.id");
BaseService businessChannelService=(BaseService) SpringConfig.getInstance().getService(BusinessChannel.class);
BusinessChannel businessChannel=(BusinessChannel)businessChannelService.findById(businessChannelId);
String request_host=request.getParameter("host");
String request_exact_fuzzy=request.getParameter("exact|fuzzy");
String request_urlfilter=request.getParameter("urlfilter");
String request_businessRuleDetailPushrate = request.getParameter("BusinessRuleDetail.pushrate");
String request_businessRuleDetailType = request.getParameter("BusinessRuleDetail.type");
String request_businessRuleDetailContent = request.getParameter("BusinessRuleDetail.content");
String request_blankrule = request.getParameter("blankrule");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    <title><%=businessChannel.getCode() %>|<%=businessChannel.getName()%>规则测试</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
  </head>
  <body>
  	<%
  	 HttpClient httpClient = new HttpClient();
	 httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(30000);    
	 httpClient.getHttpConnectionManager().getParams().setSoTimeout(30000);  
	 GetMethod getMethod = null;  
	 int statusCode;
	 BufferedReader in=null;
	 try{
		 String getUrl=String.format("%sapi/get_control?pointid=%s&sign=%s&version=1.0.1",basePath,businessChannel.getCode(),businessChannel.getSign());
		 getMethod = new GetMethod(getUrl);
         statusCode = httpClient.executeMethod(getMethod);
         if (statusCode != HttpStatus.SC_OK) {
        	out.println("请求异常");
	     }else{
	    	 in = new BufferedReader(new InputStreamReader(getMethod.getResponseBodyAsStream(), getMethod.getResponseCharSet()));
			 StringBuffer resultBuffer = new StringBuffer();
			 String str = null;
			 while ((str = in.readLine()) != null) {
				resultBuffer.append(str);
			 }
			 if(StringUtils.isBlank(resultBuffer.toString())){
				 out.println("请求异常");
			 }else{
			 JsonObject jsonObject=null;
				 try{
					 jsonObject= new JsonParser().parse(resultBuffer.toString()).getAsJsonObject();
					 JsonArray jsonArray = jsonObject.get("data").getAsJsonArray();
					 JsonArray filterArray=new JsonArray();
					 for(int i=0;i<jsonArray.size();i++){
						 JsonObject dataJsonObject=jsonArray.get(i).getAsJsonObject();
						 String exact=dataJsonObject.get("exact").getAsString();
						 String fuzzy=dataJsonObject.get("fuzzy").getAsString();
						 String urlfilter=dataJsonObject.get("urlfilter").getAsString();
						 String host=dataJsonObject.get("host").getAsString();
						 boolean isfilter=false;
						 if(StringUtils.isNotBlank(request_exact_fuzzy)){
							 if(exact.indexOf(request_exact_fuzzy)==-1
									 &&fuzzy.indexOf(request_exact_fuzzy)==-1){
								 isfilter=true;
							 }
						 }

						 if(StringUtils.isNotBlank(request_host)){
							 if(host.indexOf(request_host)==-1){
								 isfilter=true;
							 }
						 }
						 if(StringUtils.isNotBlank(request_urlfilter)){
							 if(urlfilter.indexOf(request_urlfilter)==-1){
								 isfilter=true;
							 }
						 }
						 JsonArray accountArray= dataJsonObject.get("account").getAsJsonArray();
						 boolean isaccord =false;
						  for(int j=0;j<accountArray.size();j++){
							  	 JsonObject accountJsonObject=accountArray.get(j).getAsJsonObject();
							  	 String pushtype=accountJsonObject.get("pushtype").getAsString();
						         int newpushrate=accountJsonObject.get("pushrate").getAsInt();
							     String pushcontent=accountJsonObject.get("pushcontent").getAsString();
							     if(StringUtils.isNotBlank(request_businessRuleDetailPushrate)){
							    	 if(Integer.parseInt(request_businessRuleDetailPushrate)
							    			 ==newpushrate){
							    		 isaccord=true;
							    	 }else{
							    		 isaccord=false&isaccord;
							    	 }
							     }
							     if(StringUtils.isNotBlank(request_businessRuleDetailType)){
							    	 if(pushtype.equals(request_businessRuleDetailType)){
							    		 isaccord=true;
							    	 }else{
							    		 isaccord=false&isaccord;
							    	 }
							     }
							     if(StringUtils.isNotBlank(request_businessRuleDetailContent)){
							    	 if(pushcontent.indexOf(request_businessRuleDetailContent)!=-1){
							    		 isaccord=true;
							    	 }else{
							    		 isaccord=false&isaccord;
							    	 }
							     }							     
						  }
						  if(StringUtils.isNotBlank(request_businessRuleDetailPushrate)
								  ||StringUtils.isNotBlank(request_businessRuleDetailType)
								  ||StringUtils.isNotBlank(request_businessRuleDetailContent)){
							  if(!isaccord){
								  isfilter=true;
							  }
						  }
						  if(!isfilter){
							  filterArray.add(dataJsonObject);
						  }
					 }
					  out.println("分析情况");
					  out.println("<br>");
					  out.println("状态："+(jsonObject.get("code").getAsInt()==-1?"未生成文件":"正常"));
					  out.println("<br>");
					  out.println("协议数量："+jsonObject.get("num").getAsInt());
					  out.println("   |实际数量："+filterArray.size());
					  out.println("<br>");
					  out.println("明细如下");
					  out.println("<br>");
					  int exactNum=0;
					  int hostfuzzyNum=0;
					  int nohostfuzzyNum=0;
					  int manyAccoutNum=0;
					  Map<Integer,Integer> oldPushrateMap=new HashMap<Integer,Integer>();
					  Map<Integer,Integer> newPushrateMap=new HashMap<Integer,Integer>();
					  Map<String,Integer> pushtypeMap=new HashMap<String,Integer>();
					  Map<String,Integer> pushcontentMap=new HashMap<String,Integer>();
					  for(int i=0;i<filterArray.size();i++){
						  JsonObject dataJsonObject=filterArray.get(i).getAsJsonObject();
						  String exact=dataJsonObject.get("exact").getAsString();
						  String fuzzy=dataJsonObject.get("fuzzy").getAsString();
						  String host=dataJsonObject.get("host").getAsString();
						  int oldpushrate=dataJsonObject.get("pushrate").getAsInt();
						  if(StringUtils.isNotBlank(exact)){
							  exactNum++;
						  }else if(StringUtils.isNotBlank(fuzzy)){
						  	 if(StringUtils.isNotBlank(host)){
						  		  hostfuzzyNum++;
						  	 }else{
						  		 nohostfuzzyNum++;
						  	 }
						  }
						  if(oldPushrateMap.containsKey(oldpushrate)){
							  oldPushrateMap.put(oldpushrate,oldPushrateMap.get(oldpushrate)+1);
						  }else{
							  oldPushrateMap.put(oldpushrate,1);
						  }
						 JsonArray accountArray= dataJsonObject.get("account").getAsJsonArray();
						 for(int j=0;j<accountArray.size();j++){
						  	 JsonObject accountJsonObject=accountArray.get(j).getAsJsonObject();
						  	 String pushtype=accountJsonObject.get("pushtype").getAsString();
					         int newpushrate=accountJsonObject.get("pushrate").getAsInt();
						     String pushcontent=accountJsonObject.get("pushcontent").getAsString();
						     if(pushtypeMap.containsKey(pushtype)){
						    	 pushtypeMap.put(pushtype,pushtypeMap.get(pushtype)+1);
						     }else{
						    	 pushtypeMap.put(pushtype,1);
						     }
						  	if(newPushrateMap.containsKey(newpushrate)){
						  		newPushrateMap.put(newpushrate,newPushrateMap.get(newpushrate)+1);
						  	}else{
						  		 newPushrateMap.put(newpushrate,1);
						  	}
						  	if(pushcontentMap.containsKey(pushcontent)){
						  		pushcontentMap.put(pushcontent,pushcontentMap.get(pushcontent)+1);
						  	}else{
						  		pushcontentMap.put(pushcontent,1);
						  	}
						 }
						 if(accountArray.size()>1){
						 	manyAccoutNum++;
						 }
					  }
					  out.println("精确匹配:"+exactNum);
					   out.println("   |    ");
					  out.println("带域名模糊匹配:"+hostfuzzyNum);
					   out.println("   |    ");
					   out.println("不带域名模糊匹配:"+nohostfuzzyNum);
					    out.println("<br>");
					    if(manyAccoutNum>0){
					 	   out.println("超过二个推送内容:"+manyAccoutNum);
					 	  out.println("<br>");
					    }
					    for(Integer oldpushrate:oldPushrateMap.keySet()){
					    	out.println(String.format("旧接口:%s,数量:%s",
					    			StrUtil.getDisplay(BusinessRulePushrate.class.getName(),oldpushrate+""),oldPushrateMap.get(oldpushrate)));
					    	out.println("<br>");
					    }
					    for(Integer newpushrate:newPushrateMap.keySet()){
					    	out.println(String.format("新接口:%s,数量:%s",
					    			StrUtil.getDisplay(BusinessRulePushrate.class.getName(),newpushrate+""),newPushrateMap.get(newpushrate)));
					    	out.println("<br>");
					    }
					    for(String pushtype:pushtypeMap.keySet()){
					    	out.println(String.format("推送类型:%s,数量:%s",
					    			StrUtil.getDisplay(BusinessRuleDetailType.class.getName(),pushtype+""),pushtypeMap.get(pushtype)));
					    	out.println("<br>");
					    }
					    for(String pushcontent:pushcontentMap.keySet()){
					    	if(pushcontentMap.get(pushcontent)>1){
					    		out.println(String.format("重复推送内容:<textarea rows=10 cols=80>%s</textarea>,数量:%s",
					    				pushcontent,pushcontentMap.get(pushcontent)));
						    	out.println("<br>");
					    	}
					    }
				 }catch (Exception e) {
					e.printStackTrace();
				 }
			 }
			
	     }
	 }catch(Exception e){
		 e.printStackTrace();
	 }finally{
		 if(getMethod!=null){
			 getMethod.releaseConnection();
		 }
		 if(in!=null){
			 in.close();
		 }
	 }
  	%>
  	<a href="<%=basePath%>api/get_control?pointid=<%=businessChannel.getCode() %>&sign=<%=businessChannel.getSign() %>&version=1.0.1" target="_blank">查看明细</a>
  </body>
</html>
