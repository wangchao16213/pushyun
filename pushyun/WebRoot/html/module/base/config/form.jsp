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
             	   <input type="text" name="code" style="width:400px;" value="${baseConfigForm.code}" data-rule="required">
                </div>
                <label class="row-label">名称</label>
                <div class="row-input required">
             	   <input type="text" name="name" style="width:400px;" value="${baseConfigForm.name}" data-rule="required">
                </div>
                <label class="row-label">值</label>
                <div class="row-input required">
             	   <input type="text" name="value" style="width:400px;" value="${baseConfigForm.value}" >
                </div>
                <label class="row-label">备注</label>
                <div class="row-input ">
             	   <input type="text" name="remark" style="width:400px;" value="${baseConfigForm.remark}" >
                </div>
                <label class="row-label">状态</label>
                <div class="row-input required">
                    <select name="state" data-toggle="selectpicker" data-rule="required" >
                         <option style="width:300px;height:100%" value="">请选择</option>
                         <c:forEach  items="${statusList}" var="item">
    						<option value="${item.code}" <c:if test="${baseConfigForm.state eq item.code}">selected="selected"</c:if>>${item.display}</option>
    					 </c:forEach>
                    </select>
                </div>
            </div>

