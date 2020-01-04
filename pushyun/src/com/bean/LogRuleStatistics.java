package com.bean;

import java.util.Date;

/**
 * LogRuleStatistics entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class LogRuleStatistics extends com.bean.BaseRecord implements
		java.io.Serializable {

	// Fields

	private String id;
	private BusinessChannel BusinessChannel;
	private BusinessRuleDetail BusinessRuleDetail;
	private BusinessRule BusinessRule;
	private String sdate;
	private Integer num;
	private String remark;
	private String state;
	private Date createtime;
	private Date updatetime;
	private String createuserid;
	private String updateuserid;

	// Constructors

	/** default constructor */
	public LogRuleStatistics() {
	}

	/** full constructor */
	public LogRuleStatistics(BusinessChannel BusinessChannel,BusinessRuleDetail BusinessRuleDetail,
			BusinessRule BusinessRule, String sdate, Integer num, String remark,
			String state, Date createtime, Date updatetime,
			String createuserid, String updateuserid) {
		this.BusinessChannel=BusinessChannel;
		this.BusinessRuleDetail = BusinessRuleDetail;
		this.BusinessRule = BusinessRule;
		this.sdate = sdate;
		this.num = num;
		this.remark = remark;
		this.state = state;
		this.createtime = createtime;
		this.updatetime = updatetime;
		this.createuserid = createuserid;
		this.updateuserid = updateuserid;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
		super.setBr_id(id);
	}

	public String getSdate() {
		return this.sdate;
	}

	public void setSdate(String sdate) {
		this.sdate = sdate;
	}

	public Integer getNum() {
		return this.num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Date getCreatetime() {
		return this.createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Date getUpdatetime() {
		return this.updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public String getCreateuserid() {
		return this.createuserid;
	}

	public void setCreateuserid(String createuserid) {
		this.createuserid = createuserid;
	}

	public String getUpdateuserid() {
		return this.updateuserid;
	}

	public void setUpdateuserid(String updateuserid) {
		this.updateuserid = updateuserid;
	}

	public BusinessRuleDetail getBusinessRuleDetail() {
		return BusinessRuleDetail;
	}

	public void setBusinessRuleDetail(BusinessRuleDetail businessRuleDetail) {
		BusinessRuleDetail = businessRuleDetail;
	}

	public BusinessRule getBusinessRule() {
		return BusinessRule;
	}

	public void setBusinessRule(BusinessRule businessRule) {
		BusinessRule = businessRule;
	}

	public BusinessChannel getBusinessChannel() {
		return BusinessChannel;
	}

	public void setBusinessChannel(BusinessChannel businessChannel) {
		BusinessChannel = businessChannel;
	}

}