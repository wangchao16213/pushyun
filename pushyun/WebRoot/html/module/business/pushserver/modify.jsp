<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@include file="/html/session.inc"%>
<div class="bjui-pageContent">
        <form action="<%=path%>/module/business/pushserver?action=modify" data-alertmsg="false" class="datagrid-edit-form" data-toggle="validate" data-data-type="json"> 
        <input type="hidden" name="id" value="${businessPushserverForm.id}" >
<jsp:include page="form.jsp"></jsp:include>
        </form>
</div>
<div class="bjui-pageFooter">
    <ul>
        <li><button type="button" class="btn-close btn" data-icon="close">取消</button></li>
        <li><button type="submit" class="btn btn-default" data-icon="save" >保存</button></li>
    </ul>
</div>


