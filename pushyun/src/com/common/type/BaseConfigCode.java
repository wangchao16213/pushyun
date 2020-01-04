package com.common.type;


public enum BaseConfigCode implements EnumMessage{
	
	
	taskthreadnum("taskthreadnum", "任务线程数量","4"),
	expperpagenum("expperpagenum", "导出每页数量","20000");
	
	
	String code;
	String display;
	String value;
	
	private BaseConfigCode(String code, String display,String value) {
		this.code = code;
		this.display = display;
		this.value=value;
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
