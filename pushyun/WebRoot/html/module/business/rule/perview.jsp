<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.common.config.SpringConfig"%>
<%@page import="com.common.service.BaseService"%>
<%@page import="com.bean.BusinessRule"%>
<%@page import="com.bean.BusinessRuleDetail"%>
<%@page import="org.hibernate.criterion.Criterion"%>
<%@page import="org.hibernate.criterion.Order"%>
<%@page import="org.hibernate.criterion.Restrictions"%>
<%@page import="com.common.web.PaginatedListHelper"%>
<%@page import="com.common.type.BusinessRuleDetailType"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>   
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/mytags" prefix="mytags" %> 
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String id=request.getParameter("id");
BaseService businessRuleService=(BaseService)SpringConfig.getInstance().getService(BusinessRule.class);
BaseService businessRuleDetailService=(BaseService)SpringConfig.getInstance().getService(BusinessRuleDetail.class);
BusinessRule businessRule=(BusinessRule)businessRuleService.findById(id);
List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
List<Order> orders = new ArrayList<Order>();// 排序条件
expList.add(Restrictions.eq("BusinessRule.id", id));
orders.add(Order.asc("id"));
PaginatedListHelper businessRuleDetailPlh=businessRuleDetailService.findList(expList, orders, null, null);
if(businessRuleDetailPlh.getList()==null
		||businessRuleDetailPlh.getList().size()==0){
	return;
}
pageContext.setAttribute("businessRuleDetailList", businessRuleDetailPlh.getList());
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'perview.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
  </head>
  <body>
  	<%
  		if(businessRuleDetailPlh.getList().size()==1){
  			BusinessRuleDetail businessRuleDetail=(BusinessRuleDetail)businessRuleDetailPlh.getList().get(0);
  			if(businessRuleDetail.getType().equals(BusinessRuleDetailType.link.getCode())){
  				response.sendRedirect(businessRuleDetail.getContent());
  				return;
  			}else if(businessRuleDetail.getType().equals(BusinessRuleDetailType.js.getCode())){
  				out.println("<script type='text/javascript'>");
  				out.println(businessRuleDetail.getContent());
  				out.println("</script>");
  				return;
  			}
  			out.println(businessRuleDetail.getContent());
  		}else{
  	%>
    	<c:forEach  items="${businessRuleDetailList}" var="item">
    		<textarea rows=10 cols=70>${item.content}</textarea><a href="<%=basePath%>select?action=previewrule&id=<%=id%>&detailid=${item.id}" target="_blank">预览</a>
    		</br></br>
    	</c:forEach>
    	<% }%>
  </body>
</html>
