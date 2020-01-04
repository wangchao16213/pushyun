package com.common.type;


public enum BusinessChannelCmdState implements EnumMessage{
	
	
	
	succ("01", "已发送"),
	wait("00", "等待下发"),
	delete("02", "删除");
	
	
	String code;
	String display;
	
	private BusinessChannelCmdState(String code, String display) {
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
