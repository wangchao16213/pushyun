package com.common.type;


public enum BusinessTaskState implements EnumMessage{
	
	
	//01-正常 02-已删除
	
	finish("01", "完成"),
	stop("00", "被相同任务获取"),
	wait("02", "等待"),
	run("03", "正在执行"),
	pause("04", "暂停"),
	error("05", "错误");
	
	String code;
	String display;
	
	private BusinessTaskState(String code, String display) {
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
