package com.module.gson.template;

import com.module.gson.bean.FieldBean;

public class FieldTemplateBean {

	public static FieldBean getStrTemplate(String fieldDisplay){
		FieldBean fieldBean=new FieldBean();
		fieldBean.setFieldDisplay(fieldDisplay);
		return fieldBean;
	}
	

}
