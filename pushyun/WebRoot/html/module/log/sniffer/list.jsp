<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/mytags" prefix="mytags" %> 
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@include file="/html/session.inc"%>
<div class="bjui-pageHeader" style="background-color:#fefefe; border-bottom:none;">
<form data-toggle="ajaxsearch" id="logSnifferStatistics-ajaxsearchForm" data-options="{searchDatagrid:$.CurrentNavtab.find('#datagrid-logSnifferStatistics-filter')}">
   <div class="bjui-searchBar">
 			<label>渠道编码：</label>
            <input type="text" name="businessChannel.code" class="form-control" size="15">
            <label>渠道名称：</label>
            <input type="text" name="businessChannel.name" class="form-control" size="15">
       	 <button type="button" class="btn showMoreSearch" data-toggle="moresearch" data-name="custom" title="更多查询条件"><i class="fa fa-angle-double-up"></i></button>
 	 <button type="submit" class="btn-green" data-icon="search">搜索</button>
     <button type="reset" class="btn-orange" data-icon="times">重置</button>
 	 <div class="bjui-moreSearch" style="top: 27px; display: none;">
 	  <label>开始时间：</label>
            <input type="text" size="19" name="startTime" readonly="readonly" data-toggle="datepicker" data-pattern="yyyy-MM-dd HH:mm:ss" data-max-date="%y-%M-%d" value="" class="form-control" >
              <label>结束时间：</label>
            <input type="text" size="19" name="endTime" readonly="readonly" data-toggle="datepicker" data-pattern="yyyy-MM-dd HH:mm:ss" data-max-date="%y-%M-%d" value="" class="form-control" >  
 	 </div>   
 	  	   <div class="pull-right">  	
                <div class="btn-group">
                   <button type="button" class="btn-default dropdown-toggle" data-toggle="dropdown" data-icon="copy">复选框-批量操作<span class="caret"></span></button>
                    <ul class="dropdown-menu right" role="menu">
                    	<li><a href="javascript:void(0);" onclick="expAll('expLogSnifferStatisticsForm','<%=basePath%>module/log/snifferStatistics?action=expAll','logSnifferStatistics-ajaxsearchForm');" >导出全部</a></li>
                        <li><a href="javascript:void(0);" onclick="expSelected('datagrid-logSnifferStatistics-filter','expLogSnifferStatisticsForm','<%=basePath%>module/log/snifferStatistics?action=expSelected');" >导出选中</a></li>
                        <li><a href="javascript:void(0);" onclick="truncate('<%=basePath%>module/log/snifferStatistics?action=truncate','logSnifferStatistics-ajaxsearchForm');" >清空</a></li>
                    </ul>
                </div>
        </div>            
   </div>
</form>
</div>
<div class="bjui-pageContent">
    <table class="table table-bordered" id="datagrid-logSnifferStatistics-filter" data-toggle="datagrid" data-options="{
        height: '100%',
        showToolbar: true,
        toolbarItem: 'del',
        dataUrl: '<%=basePath%>module/log/snifferStatistics',
        delUrl:'<%=basePath%>module/log/snifferStatistics?action=del',
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
                <th data-options="{name:'',align:'center',width:300,render:function(value,data){if(data.BusinessChannel){return data.BusinessChannel.code}}}">渠道编码</th>
                <th data-options="{name:'',align:'center',width:300,render:function(value,data){if(data.BusinessChannel){return data.BusinessChannel.name}}}">渠道名称</th>
                <th data-options="{name:'sniffername',align:'center',width:300}">网卡</th>
                <th data-options="{name:'traffic',align:'center',width:300}">流量</th>
                <th data-options="{name:'matchrulenum',align:'center',width:300}">匹配规则数量</th>
                <th data-options="{name:'totalnum',align:'center',width:300}">处理总量</th>
           		<th data-options="{name:'reporttime',align:'center',width:300}">上报时间</th>
           		<th data-options="{name:'updatetime',align:'center',width:300}">更新时间</th>
            </tr>
        </thead>
    </table>
</div>
