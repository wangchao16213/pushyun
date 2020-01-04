<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html style="width:100%;height:100%">
<head>
  <meta charset="UTF-8">
  <title></title>
  <script type="text/javascript" src="<%=basePath%>/html/task/js/createTable.js"></script>
    <script src="<%=basePath%>B-JUI/js/jquery-1.11.3.min.js"></script>
	<script src="<%=basePath%>B-JUI/js/jquery.cookie.js"></script>
</head>
<body style="width:100%;height:100%;margin:0px;">
<div id="run_task" style="padding: 10px;"></div>
<div id="wait_task" style="padding: 10px;"></div>
<script>
       function getWaitTask(){
     	$.ajax({
    		url:'<%=basePath%>module/business/task?action=time',
 	  	 	type:'POST', 
  	 		data:{},
  	   		timeout:10000,   
   			dataType:'json',    
    		success:function(d,textStatus,jqXHR){
    			var tableObject = new Object(); 
    			tableObject.dom="wait_task";
    			var headerArray=new Array();
    			var headerObject = new Object(); 
    			headerObject.value="渠道";
    			headerArray[0]=headerObject;
    			headerObject = new Object(); 
    			headerObject.value="任务名称";
    			headerArray[1]=headerObject;
    			headerObject = new Object(); 
    			headerObject.value="状态";
    			headerArray[2]=headerObject;
    			headerObject = new Object(); 
    			headerObject.value="时间";
    			headerArray[3]=headerObject;
    			tableObject.head=headerArray;
    			var bodyArray=new Array();
    			if(d.code==1){
    				for(var i=0;i<d.data.length;i++){
    					var rowArray=new Array();
    					rowArray[0]=d.data[i].BusinessChannel.code+"|"+d.data[i].BusinessChannel.name;
    					rowArray[1]=d.data[i].name;
    					rowArray[2]=d.data[i].state;
    					rowArray[3]=d.data[i].updatetime;
    					bodyArray[i]=rowArray;
    				}
    			}
    			tableObject.body=bodyArray;
    			new CreateTable(tableObject);
    			setTimeout("getWaitTask()", 1000);
   			},
   			complete : function(XMLHttpRequest,status){
   				if(status=='timeout'){
 　　　　　			setTimeout("getWaitTask()", 1000);
	　　　		}
   			}
	 	})  
     }
  getWaitTask();
</script>
</body>
</html>
