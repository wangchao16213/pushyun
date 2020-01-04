<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>   
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>  
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@include file="/html/session.inc"%>
  <div class="bjui-row col-1">
                <label class="row-label">渠道编码</label>
                <div class="row-input required">
             	   <input type="text" name="code" style="width:400px;" value="${businessChannelinfoForm.code}" data-rule="required">
                </div>
                <label class="row-label">渠道名称</label>
                <div class="row-input required">
             	   <input type="text" name="name" style="width:400px;" value="${businessChannelinfoForm.name}" data-rule="required">
                </div>
                <label class="row-label">标识</label>
                <div class="row-input required">
             	   <input type="text" name="sign" style="width:400px;" value="${businessChannelinfoForm.sign}" data-rule="required">
                </div>
                <label class="row-label">版本</label>
                <div class="row-input ">
             	   <input type="text" name="version" style="width:400px;" value="${businessChannelinfoForm.version}" >
                </div>
                <label class="row-label">下发通道MAC</label>
                <div class="row-input required">
             	   <input type="text" name="sendermac" style="width:400px;" value="${businessChannelinfoForm.sendermac}" data-rule="required">
                </div>
                <label class="row-label">下发通道名称</label>
                <div class="row-input required">
             	   <input type="text" name="sendername" style="width:400px;" value="${businessChannelinfoForm.sendername}" data-rule="required">
                </div>
                <label class="row-label">镜像名称(逗号隔开)</label>
                <div class="row-input required">
             	   <input type="text" name="sniffernames" style="width:400px;" value="${businessChannelinfoForm.sniffernames}" data-rule="required">
                </div>
                <label class="row-label">网关MAC</label>
                <div class="row-input required">
             	   <input type="text" name="routermac" style="width:400px;" value="${businessChannelinfoForm.routermac}" data-rule="required">
                </div>
                <label class="row-label">服务器地址</label>
                <div class="row-input required">
             	   <input type="text" name="serveraddress" style="width:400px;" value="${businessChannelinfoForm.serveraddress}" data-rule="required">
                </div>
                <label class="row-label">运行线程</label>
                <div class="row-input required">
             	   <input type="text" name="threadnum" style="width:400px;" value="${businessChannelinfoForm.threadnum}" data-rule="required;integer">
                </div>
                <label class="row-label">Host匹配线程数</label>
                <div class="row-input required">
             	   <input type="text" name="hostthreadnum" style="width:400px;" value="${businessChannelinfoForm.hostthreadnum}" data-rule="required;integer">
                </div>
                <label class="row-label">无Host匹配线程数</label>
                <div class="row-input required">
             	   <input type="text" name="nohostthreadnum" style="width:400px;" value="${businessChannelinfoForm.nohostthreadnum}" data-rule="required;integer">
                </div>
                <c:forEach  items="${businessChannelDaemonList}" var="detail">
               		 <label class="row-label" id="${detail.id}_1">程序信息</label>
                		<div class="row-input" id="${detail.id}_2">
                				------------------------------------------------
                		</div>
                	<label class="row-label" id="${detail.id}_3">运行程序</label>
                	<div class="row-input required"  id="${detail.id}_4">
                		 <input type="text" name="exec" style="width:400px;" value="${detail.exec}" data-rule="required">
               		 </div>	
               		 <label class="row-label" id="${detail.id}_5">是否解压</label>
               		 <div class="row-input required" id="${detail.id}_6">
             	   		<select name="unzip" data-toggle="selectpicker" data-rule="required" >
                         <option style="width:300px;height:100%" value="">请选择</option>
                         <c:forEach  items="${unzipList}" var="item">
    						<option value="${item.code}" <c:if test="${detail.unzip eq item.code}">selected="selected"</c:if>>${item.display}</option>
    					 </c:forEach>
                    </select>
               	   </div>
                	<label class="row-label" id="${detail.id}_7">链接地址</label>
               		 <div class="row-input required" id="${detail.id}_8">
             	  		<input type="text" name="url" style="width:400px;" value="${detail.url}" data-rule="required">
             	  		 <input type='button' name='button' value='删除' onclick='deleteDaemon("${detail.id}");'/>
             	   </div>
        		 </c:forEach>
                <label class="row-label" id="add_button"></label>
             	   <div class="row-input required">
             	  		<input type="button" name="button" value="新增程序配置" onclick="addDaemon();"/>
             	</div>
             	<label class="row-label">接口文件</label>
                <div class="row-input ">
             	   <input type="text" name="fileaddress" style="width:400px;" value="${businessChannelinfoForm.fileaddress}" >
                </div>
                <label class="row-label">推送服务器地址</label>
                <div class="row-input ">
             	   <input type="text" name="pushserveraddress" style="width:400px;" value="${businessChannelinfoForm.pushserveraddress}" >
                </div>
                <label class="row-label">DNS服务器地址</label>
                <div class="row-input ">
             	   <input type="text" name="dnsserveraddress" style="width:400px;" value="${businessChannelinfoForm.dnsserveraddress}" >
                </div>
                <label class="row-label">备注</label>
                <div class="row-input ">
             	   <input type="text" name="remark" style="width:400px;" value="${businessChannelinfoForm.remark}" >
                </div>
                <label class="row-label">状态</label>
                <div class="row-input required">
                    <select name="state" data-toggle="selectpicker" data-rule="required" >
                         <option style="width:300px;height:100%" value="">请选择</option>
                         <c:forEach  items="${statusList}" var="item">
    						<option value="${item.code}" <c:if test="${businessChannelinfoForm.state eq item.code}">selected="selected"</c:if>>${item.display}</option>
    					 </c:forEach>
                    </select>
                </div>

            </div>
<script type="text/javascript">
<!--
	function addDaemon(){
		var id=Date.parse(new Date());
		$("#add_button").before($("<label class='row-label' id='"+id+"_1'>程序信息</label><div class='row-input' id='"+id+"_2'>------------------------------------------------</div><label class='row-label' id='"+id+"_3'>运行程序</label><div class='row-input required' id='"+id+"_4'><input type='text' name='exec' style='width:400px;' ></div><label class='row-label' id='"+id+"_5'>是否解压</label><div class='row-input required' id='"+id+"_6'><select name='unzip' data-toggle='selectpicker' data-rule='required' ><option style='width:300px;height:100%' value=''>请选择</option><c:forEach  items='${unzipList}' var='item'><option value='${item.code}'>${item.display}</option></c:forEach></select></div><label class='row-label' id='"+id+"_7'>链接地址</label><div class='row-input required' id='"+id+"_8'><input type='text' name='url' style='width:400px;' ><input type='button' name='button' value='删除' onclick='deleteDaemon(\""+id+"\");'/></div>"));
	}
	function deleteDaemon(_id){
		for(var i=1;i<=8;i++){
			$("#"+_id+"_"+i).remove();
		}
	}
//-->
</script>
