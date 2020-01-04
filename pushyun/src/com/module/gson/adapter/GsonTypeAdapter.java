package com.module.gson.adapter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.common.tools.DateUtil;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.module.gson.bean.FieldBean;

public class GsonTypeAdapter<T> extends TypeAdapter<T> {
	
	private Map<String,FieldBean> adapterMap;
	
	public GsonTypeAdapter(Map<String,FieldBean> adapterMap){
		this.adapterMap=adapterMap;
	}
    public T read(JsonReader reader) throws IOException {
        return null;
    }

    public void write(JsonWriter writer, T obj) throws IOException {
        if (obj == null) {
            writer.nullValue();
            return;
        }
        if(adapterMap==null){
        	adapterMap=new HashMap<String, FieldBean>();
        }
        writer.beginObject(); 
        setField(obj,writer,adapterMap);
        writer.endObject();
    }
	private void setField(Object obj,JsonWriter writer,Map<String,FieldBean> adapterMap){
		  Field[] field = obj.getClass().getDeclaredFields();
	        try{
	        	for (int j = 0; j < field.length; j++) { 
	        		String name = field[j].getName(); // 获取属性的名字
	        		if(!adapterMap.containsKey(obj.getClass().getName()+"."+name)){
	        			continue;
	        		}
	        		FieldBean fieldBean=adapterMap.get(obj.getClass().getName()+"."+name);
	        		String upperName=name.substring(0, 1).toUpperCase()+name.substring(1);
		        	String type = field[j].getGenericType().toString(); // 获取属性的类型
		        	if (type.equals("class java.lang.String")) {
		        		 Method m = obj.getClass().getMethod("get" +upperName);
		        		 String value = (String) m.invoke(obj);
		        		 if (value != null) {
		        			 writer.name(fieldBean.getFieldDisplay()).value(value);
		        		 }
		        	}	
		        	if (type.equals("class java.lang.Integer")) {
		        		  Method m = obj.getClass().getMethod("get" + upperName);
		        		  Integer value = (Integer) m.invoke(obj);
		                  if (value != null) {
		                	  writer.name(fieldBean.getFieldDisplay()).value(value);
		                  }
		        	}
		        	if (type.equals("class java.util.Date")) {
		        		Method m = obj.getClass().getMethod("get" + upperName);
	                    Date value = (Date) m.invoke(obj);
	                    if (value != null) {
	                    	writer.name(fieldBean.getFieldDisplay()).value(DateUtil.getDateTime(value));
	                    }
		        	}
		        	if(type.indexOf("class com.bean.")!=-1){
		        		Method m = obj.getClass().getMethod("get" + upperName);
		        		Object value = (Object) m.invoke(obj);
		        		if (value != null) {
		        			 setField(value,writer,adapterMap);
		        		}
		        	}
	        	}
	        }catch (Exception e) {
				e.printStackTrace();
			}
	}
    
    
}
