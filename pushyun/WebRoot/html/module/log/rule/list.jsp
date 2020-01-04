<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/mytags" prefix="mytags" %> 
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@include file="/html/session.inc"%>
<div class="bjui-pageHeader" style="background-color:#fefefe; border-bottom:none;">
<form data-toggle="ajaxsearch" id="logRuleStatistics-ajaxsearchForm" data-options="{searchDatagrid:$.CurrentNavtab.find('#datagrid-logRuleStatistics-filter')}">
   <div class="bjui-searchBar">
 			<label>渠道编码：</label>
            <input type="text" name="businessChannel.code" class="form-control" size="15">
             <label>精确匹配/模糊匹配：</label>
            <input type="text" name="exact|fuzzy" class="form-control" size="15">
           
       	 <button type="button" class="btn showMoreSearch" data-toggle="moresearch" data-name="custom" title="更多查询条件"><i class="fa fa-angle-double-up"></i></button>
 	 <button type="submit" class="btn-green" data-icon="search">搜索</button>
     <button type="reset" class="btn-orange" data-icon="times">重置</button>
 	 <div class="bjui-moreSearch" style="top: 27px; display: none;">
 	 <label>渠道名称：</label>
            <input type="text" name="businessChannel.name" class="form-control" size="15">
 	  <label>开始时间：</label>
            <input type="text" size="19" name="startTime" readonly="readonly" data-toggle="datepicker" data-pattern="yyyy-MM-dd HH:mm:ss" data-max-date="%y-%M-%d" value="" class="form-control" >
              <label>结束时间：</label>
            <input type="text" size="19" name="endTime" readonly="readonly" data-toggle="datepicker" data-pattern="yyyy-MM-dd HH:mm:ss" data-max-date="%y-%M-%d" value="" class="form-control" >  
 	 </div>   
 	  	   <div class="pull-right">  	
                <div class="btn-group">
                   <button type="button" class="btn-default dropdown-toggle" data-toggle="dropdown" data-icon="copy">复选框-批量操作<span class="caret"></span></button>
                    <ul class="dropdown-menu right" role="menu">
                    	<li><a href="javascript:void(0);" onclick="expAll('expLogRuleStatisticsForm','<%=basePath%>module/log/ruleStatistics?action=expAll','logRuleStatistics-ajaxsearchForm');" >导出全部</a></li>
                        <li><a href="javascript:void(0);" onclick="expSelected('datagrid-logRuleStatistics-filter','expLogRuleStatisticsForm','<%=basePath%>module/log/ruleStatistics?action=expSelected');" >导出选中</a></li>
                        <li><a href="javascript:void(0);" onclick="truncate('<%=basePath%>module/log/ruleStatistics?action=truncate','logRuleStatistics-ajaxsearchForm');" >清空</a></li>
                    </ul>
                </div>
        </div>            
   </div>
</form>
</div>
<div class="bjui-pageContent">
    <table class="table table-bordered" id="datagrid-logRuleStatistics-filter" data-toggle="datagrid" data-options="{
        height: '100%',
        showToolbar: true,
        toolbarItem: 'del',
        dataUrl: '<%=basePath%>module/log/ruleStatistics',
        delUrl:'<%=basePath%>module/log/ruleStatistics?action=del',
        delPK:'id',
        delConfirm:true,
        dataType: 'json',
        jsonPrefix: '',
        showCheckboxcol: true,
        inlineEditMult:false,
        paging: {pageSize:20},
        linenumberAll: true,
        filterThead:false,
        hScrollbar:true
    }">
        <thead>
            <tr>
            	<th data-options="{name:'sdate',align:'center',width:300}">年月日</th>
                <th data-options="{name:'BusinessChannel.code',align:'center',width:300}">渠道编码</th>
                <th data-options="{name:'BusinessChannel.name',align:'center',width:300}">渠道名称</th>
           		<th data-options="{name:'BusinessRule.exact',align:'center',width:300}">精确匹配</th>
                <th data-options="{name:'BusinessRule.fuzzy',align:'center',width:300}">模糊匹配</th>
                <th data-options="{name:'BusinessRuleDetail.content',align:'center',width:300}">推送信息</th>
           		<th data-options="{name:'num',align:'center',width:300}">数量</th>
           		<th data-options="{name:'updatetime',align:'center',width:300}">更新时间</th>
            </tr>
        </thead>
    </table>
</div>
