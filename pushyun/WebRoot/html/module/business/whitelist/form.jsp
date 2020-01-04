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
                <label class="row-label">账号</label>
                <div class="row-input">
             	   <input type="text" name="account" style="width:400px;" value="${businessWhitelistForm.account}" >
                </div>
                <label class="row-label">IP</label>
                <div class="row-input">
             	   <input type="text" name="ip" style="width:400px;" value="${businessWhitelistForm.ip}" >
                </div>
                <label class="row-label">备注</label>
                <div class="row-input ">
             	   <input type="text" name="remark" style="width:400px;" value="${businessWhitelistForm.remark}" >
                </div>
            </div>