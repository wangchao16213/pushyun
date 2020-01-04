<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.common.type.BusinessPushserverState"%>  
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
<form data-toggle="ajaxsearch" data-options="{searchDatagrid:$.CurrentNavtab.find('#datagrid-businessPushserver-filter')}">
  <div class="bjui-searchBar">
  <label>编码：</label>
            <input type="text" name="code" class="form-control" size="15">
            <label>名称：</label>
            <input type="text" name="name" class="form-control" size="15">
  <button type="button" class="btn showMoreSearch" data-toggle="moresearch" data-name="custom" title="更多查询条件"><i class="fa fa-angle-double-up"></i></button>
 	 <button type="submit" class="btn-green" data-icon="search">搜索</button>
     <button type="reset" class="btn-orange" data-icon="times">重置</button>
 	 <div class="bjui-moreSearch" style="top: 27px; display: none;">
 	 	 <label>状态：</label>
            <% pageContext.setAttribute("statusList", BusinessPushserverState.values());%>
            <select name="state" >
				<option value="">--全部--</option>
				<c:forEach  items="${statusList}" var="item">
    				<option value="${item.code}" >${item.display}</option>
    			</c:forEach>
			</select>
 	 </div>
  </div> 
</form>
</div>
<div class="bjui-pageContent">
    <table class="table table-bordered" id="datagrid-businessPushserver-filter" data-toggle="datagrid" data-options="{
        height: '100%',
        showToolbar: true,
        toolbarItem: 'add,edit,del',
        dataUrl: '<%=basePath%>module/business/pushserver',
        dataType: 'json',
        jsonPrefix: '',
        editMode: {dialog:{width:'800',height:500,title:'编辑信息',mask:true}},
        editUrl: '<%=basePath%>module/business/pushserver?action=initpage&id={id}',
        delUrl:'<%=basePath%>module/business/pushserver?action=del',
        delPK:'id',
        delConfirm:true,
        showCheckboxcol: true,
        inlineEditMult:false,
        paging: {pageSize:20},
        linenumberAll: true,
        filterThead:false,
        hScrollbar:false
    }">
        <thead>
            <tr>
                <th data-options="{name:'code',align:'center',width:300}">编码</th>
                <th data-options="{name:'name',align:'center',width:300}">名称</th>
                <th data-options="{name:'sign',align:'center',width:300}">标识</th>
                <th data-options="{name:'version',align:'center',width:300}">版本</th>
                <th data-options="{name:'sendermac',align:'center',width:300}">下发通道MAC</th>
                <th data-options="{name:'sendername',align:'center',width:300}">下发通道名称</th>
                <th data-options="{name:'routermac',align:'center',width:300}">网关MAC</th>
                <th data-options="{name:'serveraddress',align:'center',width:300}">服务器地址</th>
           		<th data-options="{name:'state',align:'center',width:300,render:<mytags:display c="<%=BusinessPushserverState.class%>"/>}">状态</th>
           		<th data-options="{name:'remark',align:'center',width:300}">备注</th>
           		<th data-options="{name:'updatetime',align:'center',width:300}">更新时间</th>
            </tr>
        </thead>
    </table>
</div>
