<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.common.type.BusinessDnsState"%>
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
<form data-toggle="ajaxsearch" id="businessDns<%=businessChannelId%>-ajaxsearchForm" data-options="{searchDatagrid:$.CurrentNavtab.find('#datagrid-businessDns<%=businessChannelId%>-filter')}">
  <div class="bjui-searchBar">
  <label>域名：</label>
            <input type="text" name="host" class="form-control" size="15">
  <label>推送内容：</label>
           <input type="text" name="content" class="form-control" size="15">
  <button type="button" class="btn showMoreSearch" data-toggle="moresearch" data-name="custom" title="更多查询条件"><i class="fa fa-angle-double-up"></i></button>
 	 <button type="submit" class="btn-green" data-icon="search">搜索</button>
     <button type="reset" class="btn-orange" data-icon="times">重置</button>
 	 <div class="bjui-moreSearch" style="top: 27px; display: none;">
			 <label>状态：</label>
            <% pageContext.setAttribute("stateList", BusinessDnsState.values());%>
            <select name="state" >
				<option value="">--全部--</option>
				<c:forEach  items="${stateList}" var="item">
    				<option value="${item.code}" >${item.display}</option>
    			</c:forEach>
			</select>
			
 	 </div>
 	           <div class="pull-right">  	
                <div class="btn-group">
                   <button type="button" class="btn-default dropdown-toggle" data-toggle="dropdown" data-icon="copy">复选框-批量操作<span class="caret"></span></button>
                    <ul class="dropdown-menu right" role="menu">
                        <li><a href="javascript:void(0);" onclick="expAll('expbusinessDns<%=businessChannelId%>Form','<%=basePath%>module/business/dns?action=expAll&businessChannel.id=<%=businessChannelId%>&ext=xlsx','businessDns<%=businessChannelId%>-ajaxsearchForm');" >导出全部</a></li>
                        <li><a href="javascript:void(0);" onclick="expSelected('datagrid-businessDns<%=businessChannelId%>-filter','expbusinessDns<%=businessChannelId%>Form','<%=basePath%>module/business/dns?action=expSelected&businessChannel.id=<%=businessChannelId%>&ext=xlsx');" >导出选中</a></li>
                   		<li><a href="javascript:void(0);" onclick="startSelected('datagrid-businessDns<%=businessChannelId%>-filter','startbusinessDns<%=businessChannelId%>Form','<%=basePath%>module/business/dns?action=startSelected&businessChannel.id=<%=businessChannelId%>');" >批量启用</a></li>
                   		<li><a href="javascript:void(0);" onclick="stopSelected('datagrid-businessDns<%=businessChannelId%>-filter','stopbusinessDns<%=businessChannelId%>Form','<%=basePath%>module/business/dns?action=stopSelected&businessChannel.id=<%=businessChannelId%>');" >批量停用</a></li>
                   		<li><a href="javascript:void(0);" onclick="initImp('impbusinessDns<%=businessChannelId%>Form','<%=basePath%>module/business/dns?action=initImp&businessChannel.id=<%=businessChannelId%>');" >导入</a></li>
                   		<li><a href="javascript:void(0);" onclick="resetStatistics('resetbusinessDns<%=businessChannelId%>Form','<%=basePath%>module/business/dns?action=reset&businessChannel.id=<%=businessChannelId%>','businessDns<%=businessChannelId%>-ajaxsearchForm');" >统计清零</a></li>
                  	    <li><a href="javascript:void(0);" onclick="truncate('truncatebusinessDns<%=businessChannelId%>Form','<%=basePath%>module/business/dns?action=truncate&businessChannel.id=<%=businessChannelId%>','businessDns<%=businessChannelId%>-ajaxsearchForm');" >清空</a></li>
                  	    <li><a href="javascript:void(0);" onclick="testRule('<%=basePath%>html/module/business/dns/test.jsp?businessChannel.id=<%=businessChannelId%>','businessDns<%=businessChannelId%>-ajaxsearchForm');" >测试规则</a></li>
                    </ul>
                </div>
            </div>  
  </div> 
</form>
</div>

<div class="bjui-pageContent">
    <table class="table table-bordered" id="datagrid-businessDns<%=businessChannelId%>-filter" data-toggle="datagrid" data-options="{
        height: '100%',
        showToolbar: true,
        toolbarItem: 'add,edit,del',
        dataUrl: '<%=basePath%>module/business/dns?businessChannel.id=<%=businessChannelId%>',
        dataType: 'json',
        jsonPrefix: '',
        editMode: {dialog:{width:'800',height:500,title:'编辑信息',mask:true}},
        editUrl: '<%=basePath%>module/business/dns?action=initpage&id={id}&businessChannel.id=<%=businessChannelId%>',
        delUrl:'<%=basePath%>module/business/dns?action=del&businessChannel.id=<%=businessChannelId%>',
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
                <th data-options="{name:'BusinessChannel',align:'center',width:300,render:function(value,data){if(typeof(data.BusinessChannel)=='object'){return data.BusinessChannel.code+'|'+data.BusinessChannel.name}}}">渠道</th>
                <th data-options="{name:'host',align:'center',width:300}">域名</th>
                <th data-options="{name:'id',align:'center',width:300,render:showDnsButton}">操作</th>
                <th data-options="{name:'num',align:'center',width:100}">次数</th>
                <th data-options="{name:'content',align:'center',width:450}">推送信息</th>
                <th data-options="{name:'state',align:'center',width:300,render:<mytags:display c="<%=BusinessDnsState.class%>"/>}">状态</th>
                <th data-options="{name:'updatetime',align:'center',width:300}">更新时间</th>
            </tr>
        </thead>
    </table>
</div>
