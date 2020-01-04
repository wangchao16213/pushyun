package com.common.type;


public enum BusinessTaskType implements EnumMessage{
	
	
	api("01", "生成接口文件任务"),
	imp("02", "导入任务"),
	exp("03", "导出任务"),
	truncate("04", "清空任务"),
	reset("05", "清零任务"),
	imp_dns("06", "DNS导入任务"),
	exp_dns("07", "DNS导出任务"),
	truncate_dns("08", "DNS清空任务"),
	reset_dns("09", "DNS清零任务");
	
	
	
	
	String code;
	String display;
	
	private BusinessTaskType(String code, String display) {
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
