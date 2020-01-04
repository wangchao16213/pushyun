package com.common.type;


public enum BusinessChannelOnlinestate implements EnumMessage{
	
	//01-在线 00-离线
	
	online("01", "在线"),
	offline("00", "离线");
	
	
	String code;
	String display;
	
	private BusinessChannelOnlinestate(String code, String display) {
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
