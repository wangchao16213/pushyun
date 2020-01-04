<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.common.type.BusinessRuleState"%>
<%@page import="com.common.type.BusinessRuleDetailType"%>
<%@page import="com.common.type.BusinessRulePushrate"%>
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
<form data-toggle="ajaxsearch" id="businessRule<%=businessChannelId%>-ajaxsearchForm" data-options="{searchDatagrid:$.CurrentNavtab.find('#datagrid-businessRule<%=businessChannelId%>-filter')}">
  <div class="bjui-searchBar">
  <label>域名：</label>
            <input type="text" name="host" class="form-control" size="15">
  <label>精确匹配/模糊匹配：</label>
            <input type="text" name="exact|fuzzy" class="form-control" size="15">
  <button type="button" class="btn showMoreSearch" data-toggle="moresearch" data-name="custom" title="更多查询条件"><i class="fa fa-angle-double-up"></i></button>
 	 <button type="submit" class="btn-green" data-icon="search">搜索</button>
     <button type="reset" class="btn-orange" data-icon="times">重置</button>
 	 <div class="bjui-moreSearch" style="top: 27px; display: none;">
 	 	 
            <label>过滤条件：</label>
            <input type="text" name="urlfilter" class="form-control" size="15">
            <label>推送频率：</label>
            <%
            	pageContext.setAttribute("pushrateList",BusinessRulePushrate.values());
            %>
            <select name="BusinessRuleDetail.pushrate" >
				<option value="">--全部--</option>
				<c:forEach  items="${pushrateList}" var="item">
    				<option value="${item.code}" >${item.display}</option>
    			</c:forEach>
			</select>
			 <label>状态：</label>
            <% pageContext.setAttribute("stateList", BusinessRuleState.values());%>
            <select name="state" >
				<option value="">--全部--</option>
				<c:forEach  items="${stateList}" var="item">
    				<option value="${item.code}" >${item.display}</option>
    			</c:forEach>
			</select>
			<div style="padding-top: 5px">
			<label>推送类型：</label>
            <% pageContext.setAttribute("detailTypeList", BusinessRuleDetailType.values());%>
            <select name="BusinessRuleDetail.type" >
				<option value="">--全部--</option>
				<c:forEach  items="${detailTypeList}" var="item">
    				<option value="${item.code}" >${item.display}</option>
    			</c:forEach>
			</select>
				<label>推送内容：</label>
           <input type="text" name="BusinessRuleDetail.content" class="form-control" size="15">
           <label>过滤空数据：</label>
            <select name="blankrule" >
				<option value="">--不过滤--</option>
    			<option value="host" >空域名</option>
    			
			</select>
			</div>
			
 	 </div>
 	           <div class="pull-right">  	
                <div class="btn-group">
                   <button type="button" class="btn-default dropdown-toggle" data-toggle="dropdown" data-icon="copy">复选框-批量操作<span class="caret"></span></button>
                    <ul class="dropdown-menu right" role="menu">
                        <li><a href="javascript:void(0);" onclick="expAll('expbusinessRule<%=businessChannelId%>Form','<%=basePath%>module/business/rule?action=expAll&businessChannel.id=<%=businessChannelId%>&ext=xlsx','businessRule<%=businessChannelId%>-ajaxsearchForm');" >导出全部</a></li>
                        <li><a href="javascript:void(0);" onclick="expSelected('datagrid-businessRule<%=businessChannelId%>-filter','expbusinessRule<%=businessChannelId%>Form','<%=basePath%>module/business/rule?action=expSelected&businessChannel.id=<%=businessChannelId%>&ext=xlsx');" >导出选中</a></li>
                   		<li><a href="javascript:void(0);" onclick="startSelected('datagrid-businessRule<%=businessChannelId%>-filter','startbusinessRule<%=businessChannelId%>Form','<%=basePath%>module/business/rule?action=startSelected&businessChannel.id=<%=businessChannelId%>');" >批量启用</a></li>
                   		<li><a href="javascript:void(0);" onclick="stopSelected('datagrid-businessRule<%=businessChannelId%>-filter','stopbusinessRule<%=businessChannelId%>Form','<%=basePath%>module/business/rule?action=stopSelected&businessChannel.id=<%=businessChannelId%>');" >批量停用</a></li>
                   		<li><a href="javascript:void(0);" onclick="initImp('impbusinessRule<%=businessChannelId%>Form','<%=basePath%>module/business/rule?action=initImp&businessChannel.id=<%=businessChannelId%>');" >导入</a></li>
                   		<li><a href="javascript:void(0);" onclick="resetStatistics('resetbusinessRule<%=businessChannelId%>Form','<%=basePath%>module/business/rule?action=reset&businessChannel.id=<%=businessChannelId%>','businessRule<%=businessChannelId%>-ajaxsearchForm');" >统计清零</a></li>
                  	    <li><a href="javascript:void(0);" onclick="truncate('truncatebusinessRule<%=businessChannelId%>Form','<%=basePath%>module/business/rule?action=truncate&businessChannel.id=<%=businessChannelId%>','businessRule<%=businessChannelId%>-ajaxsearchForm');" >清空</a></li>
                  	    <li><a href="javascript:void(0);" onclick="testRule('<%=basePath%>html/module/business/rule/test.jsp?businessChannel.id=<%=businessChannelId%>','businessRule<%=businessChannelId%>-ajaxsearchForm');" >测试规则</a></li>
                    </ul>
                </div>
            </div>  
  </div> 
</form>
</div>

<div class="bjui-pageContent">
    <table class="table table-bordered" id="datagrid-businessRule<%=businessChannelId%>-filter" data-toggle="datagrid" data-options="{
        height: '100%',
        showToolbar: true,
        toolbarItem: 'add,edit,del',
        dataUrl: '<%=basePath%>module/business/rule?businessChannel.id=<%=businessChannelId%>',
        dataType: 'json',
        jsonPrefix: '',
        editMode: {dialog:{width:'800',height:500,title:'编辑信息',mask:true}},
        editUrl: '<%=basePath%>module/business/rule?action=initpage&id={id}&businessChannel.id=<%=businessChannelId%>',
        delUrl:'<%=basePath%>module/business/rule?action=del&businessChannel.id=<%=businessChannelId%>',
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
                <th data-options="{name:'exact',align:'center',width:300}">精确匹配</th>
                <th data-options="{name:'fuzzy',align:'center',width:300}">模糊匹配</th>
                <th data-options="{name:'id',align:'center',width:300,render:showRuleButton}">操作</th>
                <th data-options="{name:'num',align:'center',width:100}">次数</th>
                <th data-options="{name:'id',align:'center',width:450,render:showRuleDetail}">推送信息</th>
                <th data-options="{name:'urlfilter',align:'center',width:450}">过滤条件</th>
                <th data-options="{name:'state',align:'center',width:300,render:<mytags:display c="<%=BusinessRuleState.class%>"/>}">状态</th>
                <th data-options="{name:'updatetime',align:'center',width:300}">更新时间</th>
            </tr>
        </thead>
    </table>
</div>
