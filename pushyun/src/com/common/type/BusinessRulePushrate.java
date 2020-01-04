package com.common.type;


public enum BusinessRulePushrate implements EnumMessage{
	
	
	
	all("0", "全推送"),
	second1("1", "每秒推送(带cookie判断)"),
	second10("10", "每10秒推送(带cookie判断)"),
	second60("60", "1分钟推送(带cookie判断)"),
	minute5("300", "5分钟推送(带cookie判断)"),
	minute30("1800", "30分钟推送(带cookie判断)"),
	hour1("3600", "1小时推送(带cookie判断)"),
	hour12("43200", "12小时推送(带cookie判断)"),
	hour24("86400", "每天推送(带cookie判断)");
	
	
	String code;
	String display;
	
	private BusinessRulePushrate(String code, String display) {
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
