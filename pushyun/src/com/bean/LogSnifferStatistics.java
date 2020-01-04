package com.bean;

import java.util.Date;

/**
 * LogSnifferStatistics entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class LogSnifferStatistics extends com.bean.BaseRecord implements
		java.io.Serializable {

	// Fields

	private String id;
	private BusinessChannel BusinessChannel;
	private String sdate;
	private String sniffername;
	private Float traffic;
	private Long matchrulenum;
	private Long totalnum;
	private Date reporttime;
	private String remark;
	private String state;
	private Date createtime;
	private Date updatetime;
	private String createuserid;
	private String updateuserid;

	// Constructors

	/** default constructor */
	public LogSnifferStatistics() {
	}

	/** full constructor */
	public LogSnifferStatistics(BusinessChannel BusinessChannel, String sdate,
			String sniffername, Float traffic, Long matchrulenum,
			Long totalnum, Date reporttime, String remark, String state,
			Date createtime, Date updatetime, String createuserid,
			String updateuserid) {
		this.BusinessChannel = BusinessChannel;
		this.sdate = sdate;
		this.sniffername = sniffername;
		this.traffic = traffic;
		this.matchrulenum = matchrulenum;
		this.totalnum = totalnum;
		this.reporttime = reporttime;
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

	public String getSniffername() {
		return this.sniffername;
	}

	public void setSniffername(String sniffername) {
		this.sniffername = sniffername;
	}

	public Float getTraffic() {
		return this.traffic;
	}

	public void setTraffic(Float traffic) {
		this.traffic = traffic;
	}

	public Long getMatchrulenum() {
		return this.matchrulenum;
	}

	public void setMatchrulenum(Long matchrulenum) {
		this.matchrulenum = matchrulenum;
	}

	public Long getTotalnum() {
		return this.totalnum;
	}

	public void setTotalnum(Long totalnum) {
		this.totalnum = totalnum;
	}

	public Date getReporttime() {
		return this.reporttime;
	}

	public void setReporttime(Date reporttime) {
		this.reporttime = reporttime;
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