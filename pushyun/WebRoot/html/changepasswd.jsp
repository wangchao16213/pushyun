<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@include file="/html/session.inc"%>
<div class="bjui-pageContent">
        <form action="<%=path%>/page/managelogon?action=modify" id="goodsForm" data-alertmsg="false" class="datagrid-add-form" data-toggle="validate" data-data-type="json"> 
			<div class="bjui-row col-1">
				<label class="row-label">旧密码</label>
				<div class="row-input required">
					<input type="password" name="password" value="" data-rule="required">
				</div>
				<label class="row-label">新密码</label>
				<div class="row-input required">
					<input type="password" id="j_user_pass" name="newpassword" value="" data-rule="新密码:required">
				</div>
				<label class="row-label">确认新密码</label>
				<div class="row-input required">
					<input type="password" id="j_user_pass2" name="renewpassword" value=""  data-rule="required;match(#j_user_pass)">
				</div>
				
			</div>

			<input type="hidden" name="username" value="<%=((UserSession)session.getAttribute(Constants.SESSION_USER_CODE)).getUsers().getUsername()%>" >
			<input type="hidden" name="id" value="<%=((UserSession)session.getAttribute(Constants.SESSION_USER_CODE)).getUsers().getId()%>" >
        </form>
</div>
<div class="bjui-pageFooter">
    <ul>
        <li><button type="button" class="btn-close btn" data-icon="close">取消</button></li>
        <li><button type="submit" class="btn btn-default" data-icon="save" >保存</button></li>
    </ul>
</div>