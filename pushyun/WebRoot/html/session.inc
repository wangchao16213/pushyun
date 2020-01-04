<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.common.comm.Constants"%>
<%@page import="com.common.comm.UserSession"%>

<%
UserSession userSession=(UserSession)session.getAttribute(Constants.SESSION_USER_CODE);
if(userSession==null){
	%>
	<script type="text/javascript">
	function getCookie(name)
	{
		var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
		if(arr=document.cookie.match(reg)){
			return unescape(arr[2]).replace(/\"/g, "");
		}else{
			return "";
		}	
	}
	var url=getCookie("<%=Constants.SESSION_USER_URL%>");
	if(url==""){
		window.location.href = "<%=basePath%>html/page/manager.jsp";
	}else{
		window.location.href = "<%=basePath%>"+url;
	}
	</script>
<% 
	return;
}
%>

