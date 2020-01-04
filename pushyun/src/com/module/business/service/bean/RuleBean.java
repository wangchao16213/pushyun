package com.module.business.service.bean;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.bean.BusinessChannel;

public class RuleBean {
	
	private String id;
	private String businessChannelId;
	private String host;
	private String exact;
	private String fuzzy;
	private Integer pushrate;
	private String urlfilter;
	private String ratekey;
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
	public String getBusinessChannelId() {
		return businessChannelId;
	}
	public void setBusinessChannelId(String businessChannelId) {
		this.businessChannelId = businessChannelId;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getExact() {
		return exact;
	}
	public void setExact(String exact) {
		this.exact = exact;
	}
	public String getFuzzy() {
		return fuzzy;
	}
	public void setFuzzy(String fuzzy) {
		this.fuzzy = fuzzy;
	}
	public Integer getPushrate() {
		return pushrate;
	}
	public void setPushrate(Integer pushrate) {
		this.pushrate = pushrate;
	}
	public String getUrlfilter() {
		return urlfilter;
	}
	public void setUrlfilter(String urlfilter) {
		this.urlfilter = urlfilter;
	}
	public String getRatekey() {
		return ratekey;
	}
	public void setRatekey(String ratekey) {
		this.ratekey = ratekey;
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
