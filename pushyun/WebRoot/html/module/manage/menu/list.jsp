<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.bean.ManageMenu"%>
<%@page import="com.common.type.ManageMenuState"%>  
<%@ taglib uri="/mytags" prefix="mytags" %> 
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@include file="/html/session.inc"%>
<div class="bjui-pageHeader" style="background-color:#fefefe; border-bottom:none;">
<form data-toggle="ajaxsearch" data-options="{searchDatagrid:$.CurrentNavtab.find('#datagrid-manageMenu-filter')}">
 <div class="bjui-searchBar">
  	 <label>菜单编码：</label>
            <input type="text" name="code" class="form-control" size="15">
 	 <label>菜单名称：</label>
            <input type="text" name="name" class="form-control" size="15">
            <label>菜单链接：</label>
            <input type="text" name="url" class="form-control" size="15">
            <button type="button" class="btn showMoreSearch" data-toggle="moresearch" data-name="custom" title="更多查询条件"><i class="fa fa-angle-double-up"></i></button>
 	 <button type="submit" class="btn-green" data-icon="search">搜索</button>
     <button type="reset" class="btn-orange" data-icon="times">重置</button>
 	 <div class="bjui-moreSearch" style="top: 27px; display: none;">
 	 </div>
 </div>  
</form>
</div>
<script type="text/javascript">
	function showParentName(value,data){
		if (typeof(data.parentid) == "undefined") { 
			return ;
		}
		var url ="<%=basePath%>select?action=getfields&className=<%=ManageMenu.class.getName()%>&id="+data.parentid+"&fields=Name";
		var html="<div id='parentid"+data.id+"'></div><script>$.get('"+url+"',function(d){document.getElementById('parentid"+data.id+"').innerText=d});<\/script>";
		return html;
	}
</script>
<div class="bjui-pageContent">
    <table class="table table-bordered" id="datagrid-manageMenu-filter" data-toggle="datagrid" data-options="{
        height: '100%',
        showToolbar: true,
        toolbarItem: 'add,edit,del',
        dataUrl: '<%=basePath%>module/manage/menu',
        dataType: 'json',
        jsonPrefix: '',
        editMode: {dialog:{width:'800',height:500,title:'编辑信息',mask:true}},
        editUrl: '<%=basePath%>module/manage/menu?action=initpage&id={id}',
        delUrl:'<%=basePath%>module/manage/menu?action=del',
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
            	 <th data-options="{name:'code',align:'center',width:300}">菜单编码</th>
                <th data-options="{name:'name',align:'center',width:300}">菜单名称</th>
                <th data-options="{name:'url',align:'center',width:300}">菜单链接</th>
                <th data-options="{name:'seq',align:'center',width:300}">序列</th> 
                <th data-options="{name:'parentid',align:'center',width:300,render:showParentName}">上级菜单</th>
           		<th data-options="{name:'state',align:'center',width:300,render:<mytags:display c="<%=ManageMenuState.class%>"/>}">状态</th>
           		<th data-options="{name:'updatetime',align:'center',width:300}">更新时间</th>	
            </tr>
        </thead>
    </table>
</div>
