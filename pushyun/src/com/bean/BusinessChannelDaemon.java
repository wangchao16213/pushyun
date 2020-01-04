package com.bean;

import java.util.Date;

/**
 * BusinessChannelDaemon entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class BusinessChannelDaemon extends com.bean.BaseRecord implements
		java.io.Serializable {

	// Fields

	private String id;
	private BusinessChannel BusinessChannel;
	private String exec;
	private String unzip;
	private String url;
	private Integer seq;
	private String remark;
	private String state;
	private Date createtime;
	private Date updatetime;
	private String createuserid;
	private String updateuserid;

	// Constructors

	/** default constructor */
	public BusinessChannelDaemon() {
	}

	/** full constructor */
	public BusinessChannelDaemon(BusinessChannel BusinessChannel, String exec,
			String unzip, String url, Integer seq, String remark, String state,
			Date createtime, Date updatetime, String createuserid,
			String updateuserid) {
		this.BusinessChannel = BusinessChannel;
		this.exec = exec;
		this.unzip = unzip;
		this.url = url;
		this.seq = seq;
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

	public BusinessChannel getBusinessChannel() {
		return BusinessChannel;
	}

	public void setBusinessChannel(BusinessChannel businessChannel) {
		BusinessChannel = businessChannel;
	}

	public String getExec() {
		return this.exec;
	}

	public void setExec(String exec) {
		this.exec = exec;
	}

	public String getUnzip() {
		return this.unzip;
	}

	public void setUnzip(String unzip) {
		this.unzip = unzip;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
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

}