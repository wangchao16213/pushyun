<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.bean.BusinessTask"%>
<%@page import="com.common.type.BusinessTaskState"%>
<%@page import="com.common.type.BusinessTaskType"%>
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
<form data-toggle="ajaxsearch" id="businessTask-ajaxsearchForm" data-options="{searchDatagrid:$.CurrentNavtab.find('#datagrid-businessTask-filter')}">
   <div class="bjui-searchBar">
 			<label>渠道：</label>
            <input type="text" name="code|name" class="form-control" size="15">
 	   <label>类型：</label>
            <% pageContext.setAttribute("typeList", BusinessTaskType.values());%>
            <select name="type" >
				<option value="">--全部--</option>
				<c:forEach  items="${typeList}" var="item">
    				<option value="${item.code}" >${item.display}</option>
    			</c:forEach>
			</select>
       	 <button type="button" class="btn showMoreSearch" data-toggle="moresearch" data-name="custom" title="更多查询条件"><i class="fa fa-angle-double-up"></i></button>
 	 <button type="submit" class="btn-green" data-icon="search">搜索</button>
     <button type="reset" class="btn-orange" data-icon="times">重置</button>
 	 <div class="bjui-moreSearch" style="top: 27px; display: none;">
 	   <label>状态：</label>
            <% pageContext.setAttribute("statusList", BusinessTaskState.values());%>
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
<<script type="text/javascript">
<!--
function showDownloadfile(value,data){
		if(typeof(data.downloadfile) == 'undefined'){
			return;
		}
		var url="<%=basePath%>"+data.downloadfile;
		return "<a href='"+url+"' target='_blank'>数据文件</a>";
}
//-->
</script>


<div class="bjui-pageContent">
    <table class="table table-bordered" id="datagrid-businessTask-filter" data-toggle="datagrid" data-options="{
        height: '100%',
        showToolbar: true,
        toolbarItem: 'edit',
        dataUrl: '<%=basePath%>module/business/task',
        editMode: {dialog:{width:'800',height:500,title:'编辑信息',mask:true}},
        editUrl: '<%=basePath%>module/business/task?action=initpage&id={id}',
        delUrl:'<%=basePath%>module/business/task?action=del',
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
            	<th data-options="{name:'BusinessChannel',align:'center',width:300,render:function(value,data){if(typeof(data.BusinessChannel)=='object'){return data.BusinessChannel.code+'|'+data.BusinessChannel.name}}}">渠道</th>
                <th data-options="{name:'name',align:'center',width:300}">任务名称</th>
                <th data-options="{name:'type',align:'center',width:300,render:<mytags:display c="<%=BusinessTaskType.class%>"/>}">类型</th>
           		<th data-options="{name:'state',align:'center',width:300,render:<mytags:display c="<%=BusinessTaskState.class%>"/>}">状态</th>
           		<th data-options="{name:'uploadfile',align:'center',width:300}">上传文件</th>
           		<th data-options="{name:'downloadfile',align:'center',width:300,render:showDownloadfile}">下载文件</th>
           		<th data-options="{name:'remark',align:'center',width:300}">明细</th>
           		<th data-options="{name:'updatetime',align:'center',width:300}">更新时间</th>
           		<th data-options="{name:'content',align:'center',width:300}">条件</th>
            </tr>
        </thead>
    </table>
</div>
