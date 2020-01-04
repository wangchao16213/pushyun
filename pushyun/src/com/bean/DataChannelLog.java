package com.bean;

import java.util.Date;

/**
 * DataChannelLog entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class DataChannelLog extends com.bean.BaseRecord implements
		java.io.Serializable {

	// Fields

	private String id;
	private BusinessChannel BusinessChannel;
	private String sdate;
	private String ip;
	private String replyrecord;
	private String remark;
	private String state;
	private Date createtime;
	private Date updatetime;
	private String createuserid;
	private String updateuserid;

	// Constructors

	/** default constructor */
	public DataChannelLog() {
	}

	/** full constructor */
	public DataChannelLog(BusinessChannel BusinessChannel, String sdate, String ip,
			String replyrecord, String remark, String state, Date createtime,
			Date updatetime, String createuserid, String updateuserid) {
		this.BusinessChannel = BusinessChannel;
		this.sdate = sdate;
		this.ip = ip;
		this.replyrecord = replyrecord;
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

	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getReplyrecord() {
		return this.replyrecord;
	}

	public void setReplyrecord(String replyrecord) {
		this.replyrecord = replyrecord;
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

}