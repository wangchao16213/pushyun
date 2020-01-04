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
  				<label class="row-label">域名</label>
                <div class="row-input ">
             	   <input type="text" name="host" style="width:400px;" value="${businessRuleForm.host}" >
                </div>
                <label class="row-label">精确匹配</label>
                <div class="row-input required">
             	   <input type="text" name="exact" style="width:400px;" value="${businessRuleForm.exact}" >
                </div>
                <label class="row-label">模糊匹配</label>
                <div class="row-input required">
             	   <input type="text" name="fuzzy" style="width:400px;" value="${businessRuleForm.fuzzy}" >
                </div>
                <label class="row-label">过滤条件</label>
                <div class="row-input ">
             	   <input type="text" name="urlfilter" style="width:400px;" value="${businessRuleForm.urlfilter}" >
                </div>
                <label class="row-label">备注</label>
                <div class="row-input ">
             	   <input type="text" name="remark" style="width:400px;" value="${businessRuleForm.remark}" >
                </div>
                <label class="row-label">状态</label>
                <div class="row-input required">
                    <select name="state" data-toggle="selectpicker" data-rule="required" >
                         <option style="width:300px;height:100%" value="">请选择</option>
                         <c:forEach  items="${statusList}" var="item">
    						<option value="${item.code}" <c:if test="${businessRuleForm.state eq item.code}">selected="selected"</c:if>>${item.display}</option>
    					 </c:forEach>
                    </select>
                </div>
               	 <c:forEach  items="${businessRuleDetailList}" var="detail">
               		 <label class="row-label" id="${detail.id}_1">账号信息</label>
                		<div class="row-input" id="${detail.id}_2">
                				------------------------------------------------
                		</div>
                	 <label class="row-label" id="${detail.id}_3">频率关键词</label>
               		 <div class="row-input " id="${detail.id}_4">
             	  		 <input type="text" name="ratekey" style="width:400px;" value="${detail.ratekey}" >
               		 </div>	
                	<label class="row-label" id="${detail.id}_5">推送频率</label>
                	<div class="row-input required"  id="${detail.id}_6">
                		<select name="pushrate" data-toggle="selectpicker" data-rule="required">
                        	 <option style="width:300px;height:100%" value="">请选择</option>
                        	 <c:forEach  items="${pushrateList}" var="item">
    							<option value="${item.code}" <c:if test="${detail.pushrate eq item.code}">selected="selected"</c:if>>${item.display}</option>
    						 </c:forEach>
                    	</select>
               		 </div>	
               		 <label class="row-label" id="${detail.id}_7">推送类型</label>
               		 <div class="row-input required" id="${detail.id}_8">
             	   		<select name="type" data-toggle="selectpicker" data-rule="required" >
                         <option style="width:300px;height:100%" value="">请选择</option>
                         <c:forEach  items="${typeList}" var="item">
    						<option value="${item.code}" <c:if test="${detail.type eq item.code}">selected="selected"</c:if>>${item.display}</option>
    					 </c:forEach>
                    </select>
               	   </div>
                	<label class="row-label" id="${detail.id}_9">推送内容</label>
               		 <div class="row-input required" id="${detail.id}_10">
             	  		 <textarea rows='10' cols='40' name='content' data-rule='required'>${detail.content}</textarea>
             	  		 <input type='button' name='button' value='删除' onclick='deletepush("${detail.id}");'/>
             	   </div>
        		 </c:forEach>
             	   <label class="row-label" id="add_button"></label>
             	   <div class="row-input required">
             	  		<input type="button" name="button" value="新增推送信息" onclick="addpush();"/>
             	   </div>
            </div>
<script type="text/javascript">
<!--
	function addpush(){
		var id=Date.parse(new Date());
			$("#add_button").before($("<label class='row-label' id='"+id+"_1'>推送信息</label><div class='row-input' id='"+id+"_2'>------------------------------------------------</div><label class='row-label' id='"+id+"_3'>频率关键词</label><div class='row-input' id='"+id+"_4'><input type='text' name='ratekey' style='width:400px;' ></div><label class='row-label' id='"+id+"_5'>推送频率</label><div class='row-input required'  id='"+id+"_6'><select name='pushrate' data-toggle='selectpicker' data-rule='required'><option style='width:300px;height:100%' value=''>请选择</option><c:forEach  items='${pushrateList}' var="item"><option value='${item.code}'>${item.display}</option></c:forEach></select></div><label class='row-label' id='"+id+"_7'>推送类型</label><div class='row-input required' id='"+id+"_8'><select name='type' data-toggle='selectpicker' data-rule='required' ><option style='width:300px;height:100%' value=''>请选择</option><c:forEach  items='${typeList}' var='item'><option value='${item.code}'>${item.display}</option></c:forEach></select></div><label class='row-label' id='"+id+"_9'>推送内容</label><div class='row-input required' id='"+id+"_10'> <textarea rows='10' cols='40' name='content' data-rule='required'></textarea><input type='button' name='button' value='删除' onclick='deletepush(\""+id+"\");'/></div>"));
		}
	function deletepush(_id){
		for(var i=1;i<=10;i++){
			$("#"+_id+"_"+i).remove();
		}
	}
//-->
</script>
