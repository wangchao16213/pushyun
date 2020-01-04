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
                <label class="row-label">编码</label>
                <div class="row-input required">
             	   <input type="text" name="code" style="width:400px;" value="${businessPushserverForm.code}" data-rule="required">
                </div>
                <label class="row-label">名称</label>
                <div class="row-input required">
             	   <input type="text" name="name" style="width:400px;" value="${businessPushserverForm.name}" data-rule="required">
                </div>
                <label class="row-label">标识</label>
                <div class="row-input required">
             	   <input type="text" name="sign" style="width:400px;" value="${businessPushserverForm.sign}" data-rule="required">
                </div>
                <label class="row-label">版本</label>
                <div class="row-input ">
             	   <input type="text" name="version" style="width:400px;" value="${businessPushserverForm.version}" >
                </div>
                <label class="row-label">下发通道MAC</label>
                <div class="row-input required">
             	   <input type="text" name="sendermac" style="width:400px;" value="${businessPushserverForm.sendermac}" data-rule="required">
                </div>
                <label class="row-label">下发通道名称</label>
                <div class="row-input required">
             	   <input type="text" name="sendername" style="width:400px;" value="${businessPushserverForm.sendername}" data-rule="required">
                </div>
                <label class="row-label">网关MAC</label>
                <div class="row-input required">
             	   <input type="text" name="routermac" style="width:400px;" value="${businessPushserverForm.routermac}" data-rule="required">
                </div>
                <label class="row-label">服务器地址</label>
                <div class="row-input required">
             	   <input type="text" name="serveraddress" style="width:400px;" value="${businessPushserverForm.serveraddress}" data-rule="required">
                </div>
                <label class="row-label">备注</label>
                <div class="row-input ">
             	   <input type="text" name="remark" style="width:400px;" value="${businessPushserverForm.remark}" >
                </div>
                <label class="row-label">状态</label>
                <div class="row-input required">
                    <select name="state" data-toggle="selectpicker" data-rule="required" >
                         <option style="width:300px;height:100%" value="">请选择</option>
                         <c:forEach  items="${statusList}" var="item">
    						<option value="${item.code}" <c:if test="${businessPushserverForm.state eq item.code}">selected="selected"</c:if>>${item.display}</option>
    					 </c:forEach>
                    </select>
                </div>
            </div>

