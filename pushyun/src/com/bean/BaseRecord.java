package com.bean;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Set;


public class BaseRecord implements java.io.Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4743774164631200584L;

	private String br_id;
	
	private long br_createTime;

	private String br_createDate;

	private long br_updateTime;

	private String br_updateDate;

	private String br_type;

	public String toString() {
		Object[] args = null;
		Method[] ms = this.getClass().getMethods();
		StringBuffer sb = new StringBuffer();
		sb.append(this.getClass().getName()).append(":");
//		System.out.println(this.getClass().getName());
		for (int i = 0; i < ms.length; i++) {
			if (ms[i].getName().startsWith("get")
					&& !ms[i].getName().equals("getClass")) {
				try {
					String fieldName = ms[i].getName().substring(3);// 
					fieldName = Character.toLowerCase(fieldName.charAt(0))+ fieldName.substring(1);
//					System.out.println(ms[i].getReturnType());
					if (ms[i].getReturnType().equals(String.class)
							|| ms[i].getReturnType().equals(Float.class)
							|| ms[i].getReturnType().equals(Long.class)
							||ms[i].getReturnType().equals(Date.class)) {
						sb.append(fieldName + "=").append(ms[i].invoke(this, args))
						.append(";");
					}
//					System.out.println(fieldName + "=");
				
				} catch (Exception e) {
					 e.printStackTrace();
				}
			}
		}

		return sb.toString().replaceAll("\\s", "");

	}


	public String getBr_id() {
		return br_id;
	}

	public void setBr_id(String br_id) {
		this.br_id = br_id;
	}


	public long getBr_createTime() {
		return br_createTime;
	}


	public void setBr_createTime(long br_createTime) {
		this.br_createTime = br_createTime;
	}


	public String getBr_createDate() {
		return br_createDate;
	}


	public void setBr_createDate(String br_createDate) {
		this.br_createDate = br_createDate;
	}


	public long getBr_updateTime() {
		return br_updateTime;
	}


	public void setBr_updateTime(long br_updateTime) {
		this.br_updateTime = br_updateTime;
	}


	public String getBr_updateDate() {
		return br_updateDate;
	}


	public void setBr_updateDate(String br_updateDate) {
		this.br_updateDate = br_updateDate;
	}


	public String getBr_type() {
		return br_type;
	}


	public void setBr_type(String br_type) {
		this.br_type = br_type;
	}


}
