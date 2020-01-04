package com.common.tools;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.ConvertUtils;

public class CBeanUtils {
	
	public static void populate(Object bean, Map map){
	    try {
	        //处理时间格式
	        DateConverter dateConverter = new DateConverter();
	        //注册格式
	        ConvertUtils.register(dateConverter, Date.class);
	        //封装数据
	        org.apache.commons.beanutils.BeanUtils.populate(bean, map);
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
}
