package com.common.type;


public enum DataStatisticsState implements EnumMessage{
	
	//01-正常 02-已删除
	
	normal("01", "正常"),
	stop("00", "异常"),
	delete("02", "删除");
	
	
	String code;
	String display;
	
	private DataStatisticsState(String code, String display) {
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
