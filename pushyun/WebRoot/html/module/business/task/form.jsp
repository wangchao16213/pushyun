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
  				<label class="row-label">渠道</label>
                <div class="row-input ">
             	   ${businessChannelForm.code}| ${businessChannelForm.name}
                </div>
                <label class="row-label">任务名称</label>
                <div class="row-input required">
             	   ${businessTaskForm.name}
                </div>
                <label class="row-label">状态</label>
                <div class="row-input required">
                    <select name="state" data-toggle="selectpicker" data-rule="required" >
                         <option style="width:300px;height:100%" value="">请选择</option>
                         <c:forEach  items="${statusList}" var="item">
    						<option value="${item.code}" <c:if test="${businessTaskForm.state eq item.code}">selected="selected"</c:if>>${item.display}</option>
    					 </c:forEach>
                    </select>
                </div>
            </div>

