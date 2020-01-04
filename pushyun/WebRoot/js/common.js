function findgrid_manageChannelinfo(obj) {
  		BJUI.findgrid({
       group: 'manageChannelinfo',
       include: 'name:name,id:id',
       dialogOptions: {title:'查找渠道'},
       gridOptions: {
          	 local: 'remote',
          	 dataUrl: 'module/manage/channel',
          	 columns: [
          		 {name:'code', label:'编码'},
         		 {name:'name', label:'名称'},
          		 {name:'updatetime', label:'更新日期'}
       		]
       	}
   	})
}
	
function findgrid_routerDevice(obj,formId) {
  		BJUI.findgrid({
       group: 'routerDevice',
       include: 'name:name,id:id',
       dialogOptions: {title:'查找设备'},
       gridOptions: {
          	 local: 'remote',
          	 dataUrl: 'module/router/device?'+$('#'+formId).serialize(),
          	 columns: [
          		  {name:'code', label:'编码'},
           	  {name:'name', label:'名称'},
         	 	  {name:'mac', label:'MAC'},
          		  {name:'updatetime', label:'更新日期',quickfilter:false}
       		]
       	}
   	})
}

function findgrid_adsenseWxmp(obj) {
  		BJUI.findgrid({
       group: 'adsenseWxmp',
       include: 'name:name,id:id',
       dialogOptions: {title:'查找公众号'},
       gridOptions: {
          	 local: 'remote',
          	 dataUrl: 'module/adsense/wxmp',
          	 columns: [
          		  {name:'code', label:'公众号'},
           		  {name:'name', label:'名称'},
         	 	  {name:'appid', label:'appid'},
          		  {name:'updatetime', label:'更新日期',quickfilter:false}
       		]
       	}
   	})
}

function findgrid_adsenseSoftware(obj) {
  		BJUI.findgrid({
       group: 'adsenseSoftware',
       include: 'name:name,id:id',
       dialogOptions: {title:'查找软件'},
       gridOptions: {
          	 local: 'remote',
          	 dataUrl: 'module/adsense/software',
          	 columns: [
          		  {name:'code', label:'编码'},
           		  {name:'name', label:'名称'},
         	 	  {name:'url', label:'推广地址'},
          		  {name:'updatetime', label:'更新日期',quickfilter:false}
       		]
       	}
   	})
}

function findgrid_dataVisitor(obj) {
  		BJUI.findgrid({
       group: 'dataVisitor',
       include: 'mac:mac,id:id',
       dialogOptions: {title:'查找访客'},
       gridOptions: {
          	 local: 'remote',
          	 dataUrl: 'module/data/visitor',
          	 columns: [
          		  {name:'id', label:'ID'},
           		  {name:'name', label:'名称'},
         	 	  {name:'mac', label:'mac'},
          		  {name:'updatetime', label:'更新日期',quickfilter:false}
       		]
       	}
   	})
}

function findgrid_adsenseAdPool(obj) {
  		BJUI.findgrid({
       group: 'adsenseAdPool',
       include: 'name:name,id:id',
       dialogOptions: {title:'查找广告'},
       gridOptions: {
          	 local: 'remote',
          	 dataUrl: 'module/adsense/adPool',
          	 columns: [
          		  {name:'code', label:'编码'},
           		  {name:'name', label:'名称'},
         	 	  {name:'url', label:'url'},
          		  {name:'updatetime', label:'更新日期',quickfilter:false}
       		]
       	}
   	})
}



function showRuleDetail(value,data){
	if (typeof(data.id) == "undefined") { 
		return ;
	}
	var url ="select?action=getruledetails&id="+data.id;
	var html="<div id='ruleDetail"+data.id+"'></div><script>$.get('"+url+"',function(d){document.getElementById('ruleDetail"+data.id+"').innerHTML=d});<\/script>";
	return html;
}

function handleChannel(cmd,id){
    BJUI.ajax('doajax', {
       url: 'module/business/channel?action=handle&id='+id+'&cmd='+cmd,
       loadingmask: true,
       okCallback: function(json, options) {
       }
  	})
}

function showChannelButton(value,data){
	var html="";
	html=html+"<button type='button' class='btn-green' data-icon='search' onclick='handleChannel(\"start\",\""+data.id+"\");'>启动</button>";
	html=html+"<button type='button' class='btn-green' data-icon='edit' onclick='handleChannel(\"restart\",\""+data.id+"\");'>重启</button>";
	html=html+"<button type='button' class='btn btn-red' data-icon='times' onclick='handleChannel(\"stop\",\""+data.id+"\");'>关闭</button>";
	return html;
}

function copyRule(id,businessChannelId){
	$(document).dialog({id:'copybusinessRule'+businessChannelId+'Form',width:800,height:650, 
		url:'module/business/rule?action=initcopy&id='+id, 
		title:'复制规则',mask:true,
		onClose:function(){
			
		}
	});
}

function copyDns(id,businessChannelId){
	$(document).dialog({id:'copybusinessDns'+businessChannelId+'Form',width:800,height:650, 
		url:'module/business/dns?action=initcopy&id='+id, 
		title:'复制DNS',mask:true,
		onClose:function(){
			
		}
	});
}

function previewRule(id){
	window.open('html/module/business/rule/perview.jsp?id='+id,'_blank');
}

function previewDns(id){
	window.open('html/module/business/dns/perview.jsp?id='+id,'_blank');
}

function testRule(url,formId){
	window.open(url+"&"+$('#'+formId).serialize(),'_blank');
}

function showRuleButton(value,data){
	var html="";
	if (typeof(data.id) == "undefined") { 
		return html;
	}
	if (typeof(data.BusinessChannel) == "undefined") { 
		return html;
	}
	var html="";
	html=html+"<button type='button' class='btn-green' data-icon='search' onclick='copyRule(\""+data.id+"\",\""+data.BusinessChannel.id+"\");'>复制</button></br>";
	html=html+"<button type='button' class='btn-green' data-icon='search' onclick='previewRule(\""+data.id+"\");'>预览</button>";
	return html;
}

function showDnsButton(value,data){
	var html="";
	if (typeof(data.id) == "undefined") { 
		return html;
	}
	if (typeof(data.BusinessChannel) == "undefined") { 
		return html;
	}
	var html="";
	html=html+"<button type='button' class='btn-green' data-icon='search' onclick='copyDns(\""+data.id+"\",\""+data.BusinessChannel.id+"\");'>复制</button></br>";
	html=html+"<button type='button' class='btn-green' data-icon='search' onclick='previewDns(\""+data.id+"\");'>预览</button>";
	return html;
}


function expSelected(gridId,dialogId,url){
	var ids="";
	var selectedDatas=$("#"+gridId).data('selectedDatas');
	if(typeof(selectedDatas)=="undefined"){ 
		$(this).alertmsg('info', "请选择要导出的数据!");
		return;
	}
	if(selectedDatas.length==0){ 
		$(this).alertmsg('info', "请选择要导出的数据!");
		return;
	}
	for(var i=0;i<selectedDatas.length;i++){
		ids=ids+selectedDatas[i].id+",";
	}
	$(document).dialog({id:dialogId,width:350,height:300, 
		url:url, 
		data: {expids:ids} ,
		title:'导出数据',mask:true,
		onClose:function(){
		}
	});
}
function expAll(dialogId,url,formId){
	$(document).dialog({id:dialogId,width:350,height:300, 
		url:url, 
		data:$('#'+formId).serializeObject(),
		title:'导出数据',mask:true,
		onClose:function(){
		}
	});
}

function initImp(dialogId,url){
	$(document).dialog({id:dialogId,width:500,height:400, 
		url:url, 
		data:'',
		title:'导入数据',mask:true,
		onClose:function(json){
			
		}
	});
}


function truncate(dialogId,url,formId){
	if(!confirm("确定要清空吗,数据不可恢复?")){
		return false;
	}
	$(document).dialog({id:dialogId,width:500,height:400, 
		url:url, 
		data:$('#'+formId).serializeObject(),
		title:'清空数据',mask:true,
		onClose:function(){
			$.CurrentNavtab.navtab("refresh");
		}
	});
}

function resetStatistics(dialogId,url,formId){
	if(!confirm("确定统计要清零吗,数据不可恢复?")){
		return false;
	}
	$(document).dialog({id:dialogId,width:500,height:400, 
		url:url, 
		data:$('#'+formId).serializeObject(),
		title:'重置数据',mask:true,
		onClose:function(){
			$.CurrentNavtab.navtab("refresh");
		}
	});
}


function startSelected(gridId,dialogId,url){
	var ids="";
	var selectedDatas=$("#"+gridId).data('selectedDatas');
	if(typeof(selectedDatas)=="undefined"){ 
		$(this).alertmsg('info', "请选择要启用的数据!");
		return;
	}
	if(selectedDatas.length==0){ 
		$(this).alertmsg('info', "请选择要启用的数据!");
		return;
	}
	for(var i=0;i<selectedDatas.length;i++){
		ids=ids+selectedDatas[i].id+",";
	}
	BJUI.ajax('doajax', {
       url: url,
       data:{ids:ids},
       loadingmask: true,
       okCallback: function(json, options) {
       	$.CurrentNavtab.navtab("refresh");
       }
  	})
}

function stopSelected(gridId,dialogId,url){
	var ids="";
	var selectedDatas=$("#"+gridId).data('selectedDatas');
	if(typeof(selectedDatas)=="undefined"){ 
		$(this).alertmsg('info', "请选择要停用的数据!");
		return;
	}
	if(selectedDatas.length==0){ 
		$(this).alertmsg('info', "请选择要停用的数据!");
		return;
	}
	for(var i=0;i<selectedDatas.length;i++){
		ids=ids+selectedDatas[i].id+",";
	}
	BJUI.ajax('doajax', {
       url: url,
       data:{ids:ids},
       loadingmask: true,
       okCallback: function(json, options) {
       	$.CurrentNavtab.navtab("refresh");
       }
  	})
}



function checkHtml(htmlStr) {
  	var reg = /<[^>]+>/g;
  	 return reg.test(htmlStr);
}
function showContent(value,data){
		if(value==""){
			return;
		}
		if(checkHtml(value)){
			return "<textarea rows=5 cols=30>"+value+"</textarea>";
		}
		return value;
}

function showTextArea(value,data){
		if(value==""){
			return;
		}
		return "<textarea rows=5 cols=30>"+value+"</textarea>";
}

