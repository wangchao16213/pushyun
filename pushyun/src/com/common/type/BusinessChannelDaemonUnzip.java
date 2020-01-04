package com.common.type;


public enum BusinessChannelDaemonUnzip implements EnumMessage{
	
	
	normal("01", "需要解压"),
	stop("00", "不需要解压");
	
	String code;
	String display;
	
	private BusinessChannelDaemonUnzip(String code, String display) {
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
