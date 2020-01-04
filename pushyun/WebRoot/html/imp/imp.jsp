<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>   
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>  
<div class="bjui-pageContent">
<form action="${action}" enctype="multipart/form-data" data-toggle="validate" data-close-current="true">
    <div class="bjui-row col-1">
        <label class="row-label">导入模板</label>
        <div class="row-input">
            <a href="${templeturl}" data-toggle="ajaxdownload">模板文件下载</a>
        </div>
        <label class="row-label">上传文件</label>
        <div class="row-input"><input type="file" name="upfile" accept="application/vnd.ms-excel,.xls,.xlsx,.csv,.zip" data-rule="required"></div>
    </div>
    <div class="form-group" >
            <label  id="task_msg"></label><br/>
            <label  id="task_detail"></label>
        	 <label  id="task_files"></label>
        </div>
</form>

</div>
<div class="bjui-pageFooter">
    <ul>
        <li><button type="button" class="btn-close btn" data-icon="close">取消</button></li>
        <li><button type="submit" id="query_button" class="btn btn-default" data-icon="save" onclick="queryTask();">保存</button></li>
    </ul>
</div>
  <script type="text/javascript">
  	var code="${code}";
  	function queryTask(){
  		$("#query_button").attr("disabled", "true");
  		var url="<%=basePath%>module/business/task?action=query";
  		BJUI.ajax('doajax', {
      	 url: url,
      	 data:{code:code},
      	 loadingmask: false,
        	okCallback: function(json, options) {
       		if(json.code==1){
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
       		}else if(json.code==0){
       			document.getElementById('task_msg').innerHTML=json.msg;
       			setTimeout("queryTask();",5000);
       		}else{
       			document.getElementById('task_msg').innerHTML='正在执行，请等待';
       			setTimeout("queryTask();",5000);
       		}
         }
  		})
  	}
  	 
  </script>
</html>
