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
                <label class="row-label">登录名</label>
                <div class="row-input required">
             	   <input type="text" name="username" style="width:400px;" value="${manageUserForm.username}" data-rule="required" <c:if test="${not empty manageUserForm}">readonly="readonly"</c:if>>
                </div>
                <c:if test="${empty manageUserForm}">
                	<label class="row-label">登录密码</label>
					<div class="row-input">
						<input type="password"  id="j_user_pass" name="passwd" data-rule="登录密码:required">
					</div>
					<label class="row-label">确认登录密码</label>
					<div class="row-input">
						<input type="password" id="j_user_pass2" name=""  data-rule="required;match(#j_user_pass)">
					</div>
                </c:if>
                
                <label class="row-label">邮件</label>
                <div class="row-input ">
             	   <input type="text" name="email" style="width:400px;" value="${manageUserForm.email}" >
                </div>
               	
                 <label class="row-label">备注</label>
                <div class="row-input required">
             	   <input type="text" name="remark" style="width:400px;" value="${manageUserForm.remark}" >
                </div>
                <label class="row-label">状态</label>
                <div class="row-input required">
                    <select name="state" data-toggle="selectpicker" data-rule="required" >
                         <option style="width:300px;height:100%" value="">请选择</option>
                         <c:forEach  items="${statusList}" var="item">
    						<option value="${item.code}" <c:if test="${manageUserForm.state eq item.code}">selected="selected"</c:if>>${item.display}</option>
    					 </c:forEach>
                    </select>
                </div>
                
            </div>

