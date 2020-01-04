package com.common.type;


public enum BusinessRuleDetailType implements EnumMessage{
	

	
	html("html", "返回完整页面"),
	link("link", "直接跳转"),
	js("js", "js代码(包括动态)"),
	count("count", "统计流量"); 
	
	
	
	String code;
	String display;
	
	private BusinessRuleDetailType(String code, String display) {
		this.code = code;
		this.display = display;
	}
	
	@Override
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}
	
	@Override
	public String toString() {		
		return display;
	}
}
