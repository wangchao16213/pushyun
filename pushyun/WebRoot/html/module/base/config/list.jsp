<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.common.type.BaseConfigState"%>  
<%@ taglib uri="/mytags" prefix="mytags" %> 
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@include file="/html/session.inc"%>
<div class="bjui-pageHeader" style="background-color:#fefefe; border-bottom:none;">
<form data-toggle="ajaxsearch" data-options="{searchDatagrid:$.CurrentNavtab.find('#datagrid-baseConfig-filter')}">
<div class="bjui-searchBar">  
 <label>编码：</label>
            <input type="text" name="code" class="form-control" size="15">
            <label>名称：</label>
            <input type="text" name="name" class="form-control" size="15">
            <button type="button" class="btn showMoreSearch" data-toggle="moresearch" data-name="custom" title="更多查询条件"><i class="fa fa-angle-double-up"></i></button>
 	 <button type="submit" class="btn-green" data-icon="search">搜索</button>
     <button type="reset" class="btn-orange" data-icon="times">重置</button>
 	 <div class="bjui-moreSearch" style="top: 27px; display: none;">
 	 </div>
 	  <div class="pull-right">  	
                <div class="btn-group">
                   <button type="button" class="btn-default dropdown-toggle" data-toggle="dropdown" data-icon="copy">复选框-批量操作<span class="caret"></span></button>
                    <ul class="dropdown-menu right" role="menu">
                        <li><a href="javascript:void(0);" onclick="handle('<%=basePath%>module/router/device?action=deleteRepeatAndErrorRouteDevice','确认处理设备重复或错误数据');" >处理设备重复或错误数据</a></li>
                        <li><a href="javascript:void(0);" onclick="handle('<%=basePath%>module/router/device?action=updateK2MacRouteDevice','确认更新K2的MAC');" >更新K2的MAC</a></li>
                    	
                    </ul>
                </div>
          </div>   
</div> 
</form>
</div>
<div class="bjui-pageContent">
    <table class="table table-bordered" id="datagrid-baseConfig-filter" data-toggle="datagrid" data-options="{
        height: '100%',
        showToolbar: true,
        toolbarItem: 'add,edit,del',
        dataUrl: '<%=basePath%>module/base/config',
        dataType: 'json',
        jsonPrefix: '',
        editMode: {dialog:{width:'800',height:500,title:'编辑信息',mask:true}},
        editUrl: '<%=basePath%>module/base/config?action=initpage&id={id}',
        delUrl:'<%=basePath%>module/base/config?action=del',
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
                <th data-options="{name:'code',align:'center',width:300}">编码</th>
                <th data-options="{name:'name',align:'center',width:300}">名称</th>
                <th data-options="{name:'value',align:'center',width:300}">值</th>
           		<th data-options="{name:'state',align:'center',width:300,render:<mytags:display c="<%=BaseConfigState.class%>"/>}">状态</th>
            	<th data-options="{name:'updatetime',align:'center',width:300}">更新时间</th>
            </tr>
        </thead>
    </table>
</div>
