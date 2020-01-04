package com.bean;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * BusinessRule entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class BusinessRule extends com.bean.BaseRecord implements
		java.io.Serializable {

	// Fields

	private String id;
	private BusinessChannel BusinessChannel;
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
	private Set BusinessRuleDetails = new HashSet(0);

	// Constructors

	/** default constructor */
	public BusinessRule() {
	}

	/** full constructor */
	public BusinessRule(BusinessChannel BusinessChannel, String host, String exact,
			String fuzzy, Integer pushrate,String urlfilter,
			String ratekey, Integer num,String remark, String state, Date createtime,
			Date updatetime, String createuserid, String updateuserid,Set BusinessRuleDetails) {
		this.BusinessChannel = BusinessChannel;
		this.host = host;
		this.exact = exact;
		this.fuzzy = fuzzy;
		this.pushrate = pushrate;
		this.urlfilter = urlfilter;
		this.ratekey = ratekey;
		this.num=num;
		this.remark = remark;
		this.state = state;
		this.createtime = createtime;
		this.updatetime = updatetime;
		this.createuserid = createuserid;
		this.updateuserid = updateuserid;
		this.BusinessRuleDetails=BusinessRuleDetails;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
		super.setBr_id(id);
	}


	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
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

	public Integer getPushrate() {
		return this.pushrate;
	}

	public void setPushrate(Integer pushrate) {
		this.pushrate = pushrate;
	}

	public String getUrlfilter() {
		return this.urlfilter;
	}

	public void setUrlfilter(String urlfilter) {
		this.urlfilter = urlfilter;
	}

	public String getRatekey() {
		return this.ratekey;
	}

	public void setRatekey(String ratekey) {
		this.ratekey = ratekey;
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

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public Set getBusinessRuleDetails() {
		return BusinessRuleDetails;
	}

	public void setBusinessRuleDetails(Set businessRuleDetails) {
		BusinessRuleDetails = businessRuleDetails;
	}





}