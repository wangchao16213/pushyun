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
	<form  method="post" id="taskDataForm" enctype="multipart/form-data"  class="pageForm" >
        <div class="form-group" >
            <label  id="task_msg">${msg}</label><br/>
            <label  id="task_detail"></label>
        	 <label  id="task_files"></label>
        </div>
	</form>
</div>
<div class="bjui-pageFooter">
    <ul>
        <li><button type="button" id="impChannelClose" class="btn-close">关闭</button></li>
    </ul>
</div>
  </body>
  <script type="text/javascript">
  function queryTask(){
  	var ids="${ids}";
  	var url="<%=basePath%>module/business/task?action=query";
  	BJUI.ajax('doajax', {
       url: url,
       data:{ids:ids},
       loadingmask: false,
       okCallback: function(json, options) {
       		if(json.code==-1){
       			alert(json.msg);
       		}else if(json.code==1){
       			if (typeof(json.files) != "undefined") { 
       				for(var i=0;i<json.files.split(",").length;i++){
       					var file=json.files.split(",")[i];
       					if(file==""){
       						continue;
       					}
       					document.getElementById('task_files').innerHTML+="<a href='"+file+"' target='_blank'>数据下载</a><br/>";
       				}
       			}
       			document.getElementById('task_msg').innerHTML=json.msg;
       			document.getElementById('task_detail').innerHTML=json.detail;
       			
       		}else{
       			document.getElementById('task_msg').innerHTML=json.msg;
       			setTimeout("queryTask();",5000);
       		}
       }
  	})
  }
  queryTask();
  </script>
</html>
