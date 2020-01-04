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
			  	<label class="row-label">上级菜单</label>
                <div class="row-input ">
                    <select name="parentid" data-toggle="selectpicker"  >
                         <option style="width:300px;height:100%" value="">请选择</option>
                         <c:forEach  items="${parentManageMenuList}" var="item">
    						<option value="${item.id}" <c:if test="${manageMenuForm.parentid eq item.id}">selected="selected"</c:if>>${item.name}</option>
    					 </c:forEach>
                    </select>
                </div>
                 <label class="row-label">菜单编码</label>
                <div class="row-input required">
             	   <input type="text" name="code" style="width:400px;" value="${manageMenuForm.code}" data-rule="required">
                </div>
                <label class="row-label">菜单名称</label>
                <div class="row-input required">
             	   <input type="text" name="name" style="width:400px;" value="${manageMenuForm.name}" data-rule="required">
                </div>
                 <label class="row-label">菜单链接</label>
                <div class="row-input ">
                	<input type="text" name="url" style="width:400px;" value="${manageMenuForm.url}" >
                </div>
                <label class="row-label">序号</label>
                <div class="row-input required">
                	<input type="text" name="seq" style="width:400px;" value="${manageMenuForm.seq}" data-rule="required">
                </div>
                <label class="row-label">状态</label>
                <div class="row-input required">
                    <select name="state" data-toggle="selectpicker" data-rule="required" >
                         <option style="width:300px;height:100%" value="">请选择</option>
                         <c:forEach  items="${statusList}" var="item">
    						<option value="${item.code}" <c:if test="${manageMenuForm.state eq item.code}">selected="selected"</c:if>>${item.display}</option>
    					 </c:forEach>
                    </select>
                </div>
                
            </div>

