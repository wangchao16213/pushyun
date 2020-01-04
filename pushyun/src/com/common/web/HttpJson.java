/**
 * 
 */
package com.common.web;

import java.io.Serializable;


/**
 * http请求接口返回json对象
 *
 */
public class HttpJson implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 成功 */
	public static final String STATE_SUCCESS = "200";
	/** 失败 */
	public static final String STATE_FAIL = "300";
	/** 没授权 */
	public static final String STATE_NO_PERMISSION = "301";
	/** 登录已失效 */
	public static final String STATE_LOGIN_DISABLED = "302";
	/** 程序异常 */
	public static final String STATE_EXCEPTION = "500";
	/** 请求成功提示信息 */
	public static final String MSG_SUCCESS = "操作成功!";
	/** 请求失败提示信息 */
	public static final String MSG_FAIL = "操作失败!";
	/** 程序异常提示信息 */
	public static final String MSG_ERROR = "服务器异常!";
	/** 请求返回代码 */
	private String statusCode;
	/** 请求返回信息描述 */
	private String message=MSG_SUCCESS;
	/** 请求返回数据 */
	private boolean closeCurrent;
	
	private String datagrid;
	
	
	
	
	public HttpJson() {
		super();
	}
	public HttpJson(String code, String message, boolean closeCurrent,String datagrid) {
		super();
		this.statusCode = code;
		this.message = message;
		this.closeCurrent = closeCurrent;
		this.datagrid=datagrid;
	}
	public void setAttrs(String code, String message, boolean closeCurrent,String datagrid){
		this.statusCode = code;
		this.message = message;
		this.closeCurrent = closeCurrent;
		this.datagrid=datagrid;
	}
	public void setAttrs(String code, String message){
		this.statusCode = code;
		this.message = message;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String code) {
		this.statusCode = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isCloseCurrent() {
		return closeCurrent;
	}
	public void setCloseCurrent(boolean closeCurrent) {
		this.closeCurrent = closeCurrent;
	}
	public String getDatagrid() {
		return datagrid;
	}
	public void setDatagrid(String datagrids) {
		this.datagrid = datagrids;
	}

}
