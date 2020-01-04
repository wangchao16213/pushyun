package com.common.type;


public enum BusinessChannelCmdCmd implements EnumMessage{
	
	//01-正常 02-已删除
	
	restart("restart", "重启"),
	stop("stop", "关闭"),
	start("start", "启动");
	
	
	String code;
	String display;
	
	private BusinessChannelCmdCmd(String code, String display) {
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
