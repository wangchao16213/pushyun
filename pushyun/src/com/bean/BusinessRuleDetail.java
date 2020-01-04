package com.bean;

import java.util.Date;

/**
 * BusinessRuleDetail entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class BusinessRuleDetail extends com.bean.BaseRecord implements
		java.io.Serializable {

	// Fields

	private String id;
	private BusinessRule BusinessRule;
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

	// Constructors

	/** default constructor */
	public BusinessRuleDetail() {
	}

	/** full constructor */
	public BusinessRuleDetail(BusinessRule BusinessRule,String ratekey, Integer pushrate,String content,
			String type, Integer num,String remark, String state, Date createtime,
			Date updatetime, String createuserid, String updateuserid) {
		this.BusinessRule = BusinessRule;
		this.ratekey=ratekey;
		this.pushrate = pushrate;
		this.content = content;
		this.type = type;
		this.num=num;
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

	public Integer getPushrate() {
		return this.pushrate;
	}

	public void setPushrate(Integer pushrate) {
		this.pushrate = pushrate;
	}
	
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
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

	public BusinessRule getBusinessRule() {
		return BusinessRule;
	}

	public void setBusinessRule(BusinessRule businessRule) {
		BusinessRule = businessRule;
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


}