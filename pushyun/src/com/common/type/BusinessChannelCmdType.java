package com.common.type;


public enum BusinessChannelCmdType implements EnumMessage{
	

	
	control("01", "控制命令"),
	other("00", "其他命令"); 
	
	
	
	String code;
	String display;
	
	private BusinessChannelCmdType(String code, String display) {
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
