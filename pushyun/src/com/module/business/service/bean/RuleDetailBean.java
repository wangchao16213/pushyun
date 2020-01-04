package com.module.business.service.bean;

import java.util.Date;


public class RuleDetailBean {
	
	private String id;
	private String businessRuleId;
	private String ratekey;
	private Integer pushrate;
	private String content;
	private String type;
	private Integer num;
	private String remark;
	private String state;
	private Date createtime;
	private Date updatetime;
	private String createuserid;
	private String updateuserid;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBusinessRuleId() {
		return businessRuleId;
	}
	public void setBusinessRuleId(String businessRuleId) {
		this.businessRuleId = businessRuleId;
	}
	public String getRatekey() {
		return ratekey;
	}
	public void setRatekey(String ratekey) {
		this.ratekey = ratekey;
	}
	public Integer getPushrate() {
		return pushrate;
	}
	public void setPushrate(Integer pushrate) {
		this.pushrate = pushrate;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public String getCreateuserid() {
		return createuserid;
	}
	public void setCreateuserid(String createuserid) {
		this.createuserid = createuserid;
	}
	public String getUpdateuserid() {
		return updateuserid;
	}
	public void setUpdateuserid(String updateuserid) {
		this.updateuserid = updateuserid;
	}
}
