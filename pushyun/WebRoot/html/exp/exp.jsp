<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    <title>${msg}</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script>
		
	</script>
  </head>
  
  <body>
<div class="bjui-pageContent">
	<form  method="post" id="impChannelDataForm" enctype="multipart/form-data"  class="pageForm" >
      
        <div class="form-group" >
            <label for="j_pwschange_newpassword" class="control-label ">${msg}</label><br/><br/>
        	<a href="<%=basePath%>${fileUrl}" target="_blank">数据下载</a>
        </div>
	</form>
</div>
<div class="bjui-pageFooter">
    <ul>
        <li><button type="button" id="impChannelClose" class="btn-close">关闭</button></li>
    </ul>
</div>
  </body>
</html>
