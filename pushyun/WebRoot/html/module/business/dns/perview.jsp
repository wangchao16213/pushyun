<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.common.config.SpringConfig"%>
<%@page import="com.common.service.BaseService"%>
<%@page import="com.bean.BusinessDns"%>
<%@page import="org.hibernate.criterion.Criterion"%>
<%@page import="org.hibernate.criterion.Order"%>
<%@page import="org.hibernate.criterion.Restrictions"%>
<%@page import="com.common.web.PaginatedListHelper"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>   
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/mytags" prefix="mytags" %> 
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String id=request.getParameter("id");
BaseService businessDnsService=(BaseService)SpringConfig.getInstance().getService(BusinessDns.class);
BusinessDns businessDns=(BusinessDns)businessDnsService.findById(id);
if(businessDns==null){
	return;
}
pageContext.setAttribute("businessDnsForm", businessDns);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'perview.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
  </head>
  <body>
  	  <textarea rows='10' cols='40' name='content' data-rule='required'>${businessDnsForm.content}</textarea>
  </body>
</html>
