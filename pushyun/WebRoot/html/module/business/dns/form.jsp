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
             	   <input type="text" name="host" style="width:400px;" value="${businessDnsForm.host}" >
                </div>
                <label class="row-label">备注</label>
                <div class="row-input ">
             	   <input type="text" name="remark" style="width:400px;" value="${businessDnsForm.remark}" >
                </div>
                <label class="row-label">状态</label>
                <div class="row-input required">
                    <select name="state" data-toggle="selectpicker" data-rule="required" >
                         <option style="width:300px;height:100%" value="">请选择</option>
                         <c:forEach  items="${statusList}" var="item">
    						<option value="${item.code}" <c:if test="${businessDnsForm.state eq item.code}">selected="selected"</c:if>>${item.display}</option>
    					 </c:forEach>
                    </select>
                </div>
               	
                <label class="row-label" >推送内容</label>
               		<div class="row-input required" >
             	  		 <textarea rows='10' cols='40' name='content' data-rule='required'>${businessDnsForm.content}</textarea>
             	 </div>
   
            </div>

