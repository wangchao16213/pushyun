<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.common.type.ManageUserState"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>   
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/mytags" prefix="mytags" %>   
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@include file="/html/session.inc"%>
<div class="bjui-pageHeader" style="background-color:#fefefe; border-bottom:none;">
<form data-toggle="ajaxsearch" data-options="{searchDatagrid:$.CurrentNavtab.find('#datagrid-manageUser-filter')}">
  <div class="bjui-searchBar">
  <label>登录名：</label>
            <input type="text" name="code" class="form-control" size="15">
            <label>状态：</label>
            <% pageContext.setAttribute("statusList", ManageUserState.values());%>
            <select name="state">
				<option value="">--全部--</option>
				<c:forEach  items="${statusList}" var="item">
    				<option value="${item.code}">${item.display}</option>
    			</c:forEach>
			</select>
   <button type="button" class="btn showMoreSearch" data-toggle="moresearch" data-name="custom" title="更多查询条件"><i class="fa fa-angle-double-up"></i></button>
 	 <button type="submit" class="btn-green" data-icon="search">搜索</button>
     <button type="reset" class="btn-orange" data-icon="times">重置</button>
 	 <div class="bjui-moreSearch" style="top: 27px; display: none;">
 	 </div>
  </div>
    
</form>
</div>
<div class="bjui-pageContent">
    <table class="table table-bordered" id="datagrid-manageUser-filter" data-toggle="datagrid" data-options="{
        height: '100%',
        showToolbar: true,
        toolbarItem: 'add,edit,del',
        dataUrl: '<%=basePath%>module/manage/user',
        dataType: 'json',
        jsonPrefix: '',
        editMode: {dialog:{width:'800',height:500,title:'编辑信息',mask:true}},
        editUrl: '<%=basePath%>module/manage/user?action=initpage&id={id}',
        delUrl:'<%=basePath%>module/manage/user?action=del',
        delPK:'id',
        delConfirm:true,
        showCheckboxcol: true,
        inlineEditMult:false,
        paging: {pageSize:20},
        linenumberAll: true,
        filterThead:false,
        hScrollbar:true
    }">
        <thead>
            <tr>
                <th data-options="{name:'username',align:'center',width:300}">登录名</th>
                <th data-options="{name:'email',align:'center',width:300}">邮箱</th>
           		<th data-options="{name:'state',align:'center',width:300,render:<mytags:display c="<%=ManageUserState.class%>"/>}">状态</th>
           		<th data-options="{name:'updatetime',align:'center',width:300}">更新时间</th>
            </tr>
        </thead>
    </table>
</div>
