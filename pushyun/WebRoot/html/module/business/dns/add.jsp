<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>   
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>  
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@include file="/html/session.inc"%>
<div class="bjui-pageContent">
        <form action="<%=path%>/module/business/dns?action=add" data-alertmsg="false" class="datagrid-add-form" data-toggle="validate" data-data-type="json"> 
<input type="hidden" name="businessChannel.id" value="${businessDnsForm.businessChannel.id}" >
<jsp:include page="form.jsp"></jsp:include>
        </form>
</div>
<div class="bjui-pageFooter">
    <ul>
        <li><button type="button" class="btn-close btn" data-icon="close">取消</button></li>
        <li><button type="submit" class="btn btn-default" data-icon="save" >保存</button></li>
    </ul>
</div>


