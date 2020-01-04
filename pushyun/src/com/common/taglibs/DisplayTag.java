package com.common.taglibs;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.swing.Spring;

import org.apache.commons.lang.StringUtils;
import org.jboss.util.propertyeditor.ClassEditor;

import com.common.tools.EnumUtil;
import com.common.type.EnumMessage;



public class DisplayTag implements Tag{
	
	 private PageContext pageContext;  

	 private Tag tag;  
	 
	 private Class c;

	@Override
	public int doEndTag() throws JspException {	
		try{
			JspWriter out =pageContext.getOut();
			Map<String, EnumMessage> enumMap= EnumUtil.getEnumValues(c);
			out.print(getJsFunction(enumMap));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return Tag.EVAL_PAGE;  
	}


	private String getJsFunction(Map<String, EnumMessage> enumMap){
		StringBuffer sb=new StringBuffer();
		sb.append("function(value){");
		sb.append("var renders=[];");
		for(String code:enumMap.keySet()){
			sb.append(String.format("renders[renders.length]={code:'%s',display:'%s'};",code,enumMap.get(code).getDisplay()));
		}
		sb.append("for(var i=0;i<renders.length;i++){");
		sb.append("if(renders[i].code==value){");
		sb.append("return renders[i].display;");
		sb.append("}}}");	
		return sb.toString();
	}

	@Override
	public int doStartTag() throws JspException {
		 return Tag.SKIP_BODY;  
	}

	@Override
	public Tag getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPageContext(PageContext pc) {
		 this.pageContext = pc;  
	}

	@Override
	public void setParent(Tag t) {
		this.tag = t;  
	}

	public Class getC() {
		return c;
	}

	public void setC(Class c) {
		this.c = c;
	}



}
