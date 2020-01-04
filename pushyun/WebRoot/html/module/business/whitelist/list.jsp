<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>   
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/mytags" prefix="mytags" %> 
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String businessChannelId=request.getParameter("businessChannel.id");
%>
<%@include file="/html/session.inc"%>
<div class="bjui-pageHeader" style="background-color:#fefefe; border-bottom:none;">
<form data-toggle="ajaxsearch" data-options="{searchDatagrid:$.CurrentNavtab.find('#datagrid-businessWhitelist<%=businessChannelId%>-filter')}">
  <div class="bjui-searchBar">
  <label>账号：</label>
            <input type="text" name="code" class="form-control" size="15">
            <label>IP：</label>
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
                        <li><a href="javascript:void(0);" onclick="expAll('expbusinessWhitelist<%=businessChannelId%>Form','<%=basePath%>module/business/whitelist?action=expAll&businessChannel.id=<%=businessChannelId%>&ext=xlsx','businessWhitelist<%=businessChannelId%>-ajaxsearchForm');" >导出全部</a></li>
                        <li><a href="javascript:void(0);" onclick="expSelected('datagrid-businessWhitelist<%=businessChannelId%>-filter','expbusinessWhitelistForm','<%=basePath%>module/business/whitelist?action=expSelected&businessChannel.id=<%=businessChannelId%>&ext=xlsx');" >导出选中</a></li>
                   		<li><a href="javascript:void(0);" onclick="initImp('impbusinessWhitelist<%=businessChannelId%>Form','<%=basePath%>module/business/whitelist?action=initImp&businessChannel.id=<%=businessChannelId%>');" >导入</a></li>
                  	    <li><a href="javascript:void(0);" onclick="truncate('truncatebusinessWhitelist<%=businessChannelId%>Form','<%=basePath%>module/business/whitelist?action=truncate&businessChannel.id=<%=businessChannelId%>','businessWhitelist<%=businessChannelId%>-ajaxsearchForm');" >清空</a></li>
                    </ul>
                </div>
            </div>  
  </div> 
</form>
</div>
<div class="bjui-pageContent">
    <table class="table table-bordered" id="datagrid-businessWhitelist<%=businessChannelId%>-filter" data-toggle="datagrid" data-options="{
        height: '100%',
        showToolbar: true,
        toolbarItem: 'add,edit,del',
        dataUrl: '<%=basePath%>module/business/whitelist?businessChannel.id=<%=businessChannelId%>',
        dataType: 'json',
        jsonPrefix: '',
        editMode: {dialog:{width:'800',height:500,title:'编辑信息',mask:true}},
        editUrl: '<%=basePath%>module/business/whitelist?action=initpage&id={id}&businessChannel.id=<%=businessChannelId%>',
        delUrl:'<%=basePath%>module/business/whitelist?action=del&businessChannel.id=<%=businessChannelId%>',
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
                <th data-options="{name:'account',align:'center',width:300}">账号</th>
                <th data-options="{name:'ip',align:'center',width:300}">IP</th>
           		<th data-options="{name:'remark',align:'center',width:300}">备注</th>
           		<th data-options="{name:'updatetime',align:'center',width:300}">更新时间</th>
            </tr>
        </thead>
    </table>
</div>
