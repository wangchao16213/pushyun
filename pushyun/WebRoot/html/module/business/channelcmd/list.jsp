<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.common.type.BusinessChannelCmdState"%>
<%@page import="com.common.type.BusinessChannelCmdCmd"%>
<%@page import="com.common.type.BusinessChannelCmdType"%>
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
<form data-toggle="ajaxsearch" id="businessChannelCmd-ajaxsearchForm" data-options="{searchDatagrid:$.CurrentNavtab.find('#datagrid-businessChannelCmd-filter')}">
   <div class="bjui-searchBar">
 			<label>渠道编码：</label>
            <input type="text" name="code" class="form-control" size="15">
            <label>渠道名称：</label>
            <input type="text" name="name" class="form-control" size="15">
       	 <button type="button" class="btn showMoreSearch" data-toggle="moresearch" data-name="custom" title="更多查询条件"><i class="fa fa-angle-double-up"></i></button>
 	 <button type="submit" class="btn-green" data-icon="search">搜索</button>
     <button type="reset" class="btn-orange" data-icon="times">重置</button>
 	 <div class="bjui-moreSearch" style="top: 27px; display: none;">
 	   <label>状态：</label>
            <% pageContext.setAttribute("statusList", BusinessChannelCmdState.values());%>
            <select name="state" >
				<option value="">--全部--</option>
				<c:forEach  items="${statusList}" var="item">
    				<option value="${item.code}" >${item.display}</option>
    			</c:forEach>
			</select>
 	 </div>   
 	  	   <div class="pull-right">  	
               
       	   </div>            
   </div>
</form>
</div>
<div class="bjui-pageContent">
    <table class="table table-bordered" id="datagrid-businessChannelCmd-filter" data-toggle="datagrid" data-options="{
        height: '100%',
        showToolbar: true,
        toolbarItem: 'del',
        dataUrl: '<%=basePath%>module/business/channelCmd',
        delUrl:'<%=basePath%>module/business/channelCmd?action=del',
        delPK:'id',
        delConfirm:true,
        dataType: 'json',
        jsonPrefix: '',
        showCheckboxcol: true,
        inlineEditMult:false,
        paging: {pageSize:20},
        linenumberAll: true,
        filterThead:false,
        hScrollbar:false
    }">
        <thead>
            <tr>
                <th data-options="{name:'id',align:'center',width:300,render:function(value,data){if(data.BusinessChannel){return data.BusinessChannel.code}}}">渠道编码</th>
                <th data-options="{name:'id',align:'center',width:300,render:function(value,data){if(data.BusinessChannel){return data.BusinessChannel.name}}}">渠道名称</th>
                <th data-options="{name:'cmd',align:'center',width:300,render:<mytags:display c="<%=BusinessChannelCmdCmd.class%>"/>}">命令</th>
                <th data-options="{name:'type',align:'center',width:300,render:<mytags:display c="<%=BusinessChannelCmdType.class%>"/>}">类型</th>
           		<th data-options="{name:'state',align:'center',width:300,render:<mytags:display c="<%=BusinessChannelCmdState.class%>"/>}">状态</th>
           		<th data-options="{name:'updatetime',align:'center',width:300}">更新时间</th>
            </tr>
        </thead>
    </table>
</div>
