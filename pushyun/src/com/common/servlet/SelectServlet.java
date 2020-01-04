package com.common.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.bean.BusinessRule;
import com.bean.BusinessRuleDetail;
import com.common.tools.EnumUtil;
import com.common.tools.StrUtil;
import com.common.type.BusinessRuleDetailType;
import com.common.type.BusinessRulePushrate;
import com.common.type.EnumMessage;
import com.common.web.PaginatedListHelper;
import com.spacesat.util.json.JSONArray;
import com.spacesat.util.json.JSONException;
import com.spacesat.util.json.JSONObject;

public class SelectServlet extends Base {


	private static final long serialVersionUID = 1L;

	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
	}
	
	public void getfields(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException, ClassNotFoundException, 
				NoSuchFieldException, SecurityException, NoSuchMethodException, 
				IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String id=request.getParameter("id");
		if(StringUtils.isBlank(id)){
			return;
		}
		String className=request.getParameter("className");
		String[] fields=request.getParameter("fields").split("_");
		Class c=Class.forName(className);
		Object o=getBaseService(c).findById(id);
		StringBuffer sb=new StringBuffer();
		if(o==null){
			this.sendWeb(sb.toString(), "text/html; charset=utf-8", request, response);	
			return ;
		}
		for(int i=0;i<fields.length;i++){
			if(i>0){
				sb.append("|");	
			}
			Method m=c.getMethod("get"+fields[i], null);
			Object object=m.invoke(o, null);
			if(object!=null){
				sb.append(object.toString());
			}
		}
		this.sendWeb(sb.toString(), "text/html; charset=utf-8", request, response);	
	}

	public void enumtype(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException, ClassNotFoundException {
		String className=request.getParameter("className");
		Class c=Class.forName(className);
		Map<String, EnumMessage> enumMap=EnumUtil.getEnumValues(c);
		JSONArray array=new JSONArray();
		for(String code:enumMap.keySet()){
			JSONObject object=new JSONObject();
			try {
				object.put("code", code);
				object.put("display", enumMap.get(code).getDisplay());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			array.put(object);
		}
		this.sendWeb(array.toString(), "application/json; charset=utf-8", request, response);	
	}
	
	public void getruledetails(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException, ClassNotFoundException {
		String id=request.getParameter("id");
		List<Criterion> expList = new ArrayList<Criterion>();// 查询条件
		List<Order> orders = new ArrayList<Order>();// 排序条件
		expList.add(Restrictions.eq("BusinessRule.id", id));
		orders.add(Order.asc("id"));
		PaginatedListHelper businessRuleDetailPlh=getBaseService(BusinessRuleDetail.class).findList(expList, orders, null, null);
		StringBuffer sb=new StringBuffer();
		if(businessRuleDetailPlh.getList()==null
				||businessRuleDetailPlh.getList().size()==0){
			this.sendWeb(sb.toString(), "text/html; charset=utf-8", request, response);	
			return;
		}
		for(Object o:businessRuleDetailPlh.getList()){
			BusinessRuleDetail businessRuleDetail=(BusinessRuleDetail)o;
			sb.append(StrUtil.getDisplay(BusinessRulePushrate.class.getName(), businessRuleDetail.getPushrate().toString()));
			if(StringUtils.isNotBlank(businessRuleDetail.getRatekey())){
				sb.append("<br>");
				sb.append("频率关键词:"+businessRuleDetail.getRatekey());
			}
			
			sb.append("<br>");
			if(businessRuleDetail.getNum()!=null){
				sb.append("次数:");
				sb.append(businessRuleDetail.getNum());
				sb.append("  ");
			}
			sb.append(StrUtil.getDisplay(BusinessRuleDetailType.class.getName(), businessRuleDetail.getType()));
			sb.append("<br>");
			sb.append(String.format("<textarea rows='5' cols='40' name='content' data-rule='required'>%s</textarea>", businessRuleDetail.getContent()));
			sb.append("<br>");
		}
		this.sendWeb(sb.toString(), "text/html; charset=utf-8", request, response);	
		return;
	}
	
	public void previewrule(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException, ClassNotFoundException {
		String id=request.getParameter("id");
		String detailid=request.getParameter("detailid");
		BusinessRule businessRule=(BusinessRule) getBaseService(BusinessRule.class).findById(id);
		BusinessRuleDetail businessRuleDetail=(BusinessRuleDetail) getBaseService(BusinessRuleDetail.class).findById(detailid);
		if(businessRuleDetail.getType().equals(BusinessRuleDetailType.link.getCode())){
			response.sendRedirect(businessRuleDetail.getContent());
			return;
		}else if(businessRuleDetail.getType().equals(BusinessRuleDetailType.js.getCode())){
			StringBuffer sb=new StringBuffer();
			sb.append("<script type='text/javascript'>");
			sb.append(businessRuleDetail.getContent());
			sb.append("</script>");
			this.sendWeb(sb.toString(), "text/html; charset=utf-8", request, response);	
			return ;
  		}
		this.sendWeb(businessRuleDetail.getContent(), "text/html; charset=utf-8", request, response);	
		return ;
	}

}
