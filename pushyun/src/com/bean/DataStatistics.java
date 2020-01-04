package com.bean;

import java.util.Date;

/**
 * DataStatistics entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class DataStatistics extends com.bean.BaseRecord implements
		java.io.Serializable {

	// Fields

	private String id;
	private BusinessChannel BusinessChannel;
	private String sdate;
	private BusinessRule BusinessRule;
	private String exact;
	private String fuzzy;
	private Integer count;
	private String remark;
	private String state;
	private Date createtime;
	private Date updatetime;
	private String createuserid;
	private String updateuserid;

	// Constructors

	/** default constructor */
	public DataStatistics() {
	}

	/** full constructor */
	public DataStatistics(BusinessChannel BusinessChannel, String sdate,
			BusinessRule BusinessRule, String exact, String fuzzy, Integer count,String remark,
			String state, Date createtime, Date updatetime,
			String createuserid, String updateuserid) {
		this.BusinessChannel = BusinessChannel;
		this.sdate = sdate;
		this.BusinessRule = BusinessRule;
		this.exact = exact;
		this.fuzzy = fuzzy;
		this.count=count;
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

	
	public String getExact() {
		return this.exact;
	}

	public void setExact(String exact) {
		this.exact = exact;
	}

	public String getFuzzy() {
		return this.fuzzy;
	}

	public void setFuzzy(String fuzzy) {
		this.fuzzy = fuzzy;
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

	public BusinessChannel getBusinessChannel() {
		return BusinessChannel;
	}

	public void setBusinessChannel(BusinessChannel businessChannel) {
		BusinessChannel = businessChannel;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public BusinessRule getBusinessRule() {
		return BusinessRule;
	}

	public void setBusinessRule(BusinessRule businessRule) {
		BusinessRule = businessRule;
	}

}