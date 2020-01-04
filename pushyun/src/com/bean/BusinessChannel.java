package com.bean;

import java.util.Date;

/**
 * BusinessChannel entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class BusinessChannel extends com.bean.BaseRecord implements
		java.io.Serializable {

	// Fields

	private String id;
	private String code;
	private String name;
	private String sign;
	private String version;
	private String sendermac;
	private String sendername;
	private String sniffernames;
	private String routermac;
	private String serveraddress;
	private Integer threadnum;
	private Integer hostthreadnum;
	private Integer nohostthreadnum;
	private String onlinestate;
	private Float traffic;
	private Long matchrulenum;
	private Long totalnum;
	private String fileaddress;
	private String pushserveraddress;
	private String dnsserveraddress;
	private String remark;
	private String state;
	private Date createtime;
	private Date updatetime;
	private String createuserid;
	private String updateuserid;

	// Constructors

	/** default constructor */
	public BusinessChannel() {
	}

	/** full constructor */
	public BusinessChannel(String code, String name, String sign,
			String version, String sendermac, String sendername,
			String sniffernames, String routermac, String serveraddress,
			Integer threadnum,Integer hostthreadnum,Integer nohostthreadnum, String onlinestate, 
			Float traffic,Long matchrulenum,Long totalnum,String fileaddress,String pushserveraddress,
			String dnsserveraddress,String remark, String state,
			Date createtime, Date updatetime, String createuserid,
			String updateuserid) {
		this.code = code;
		this.name = name;
		this.sign = sign;
		this.version = version;
		this.sendermac = sendermac;
		this.sendername = sendername;
		this.sniffernames = sniffernames;
		this.routermac = routermac;
		this.serveraddress = serveraddress;
		this.threadnum = threadnum;
		this.hostthreadnum = hostthreadnum;
		this.nohostthreadnum = nohostthreadnum;
		this.onlinestate = onlinestate;
		this.traffic=traffic;
		this.matchrulenum=matchrulenum;
		this.totalnum=totalnum;
		this.fileaddress=fileaddress;
		this.pushserveraddress=pushserveraddress;
		this.dnsserveraddress=dnsserveraddress;
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

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSign() {
		return this.sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSendermac() {
		return this.sendermac;
	}

	public void setSendermac(String sendermac) {
		this.sendermac = sendermac;
	}

	public String getSendername() {
		return this.sendername;
	}

	public void setSendername(String sendername) {
		this.sendername = sendername;
	}

	public String getSniffernames() {
		return this.sniffernames;
	}

	public void setSniffernames(String sniffernames) {
		this.sniffernames = sniffernames;
	}

	public String getRoutermac() {
		return this.routermac;
	}

	public void setRoutermac(String routermac) {
		this.routermac = routermac;
	}

	public String getServeraddress() {
		return this.serveraddress;
	}

	public void setServeraddress(String serveraddress) {
		this.serveraddress = serveraddress;
	}

	public Integer getThreadnum() {
		return this.threadnum;
	}

	public void setThreadnum(Integer threadnum) {
		this.threadnum = threadnum;
	}

	public String getOnlinestate() {
		return this.onlinestate;
	}

	public void setOnlinestate(String onlinestate) {
		this.onlinestate = onlinestate;
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

	public Integer getHostthreadnum() {
		return hostthreadnum;
	}

	public void setHostthreadnum(Integer hostthreadnum) {
		this.hostthreadnum = hostthreadnum;
	}

	public Integer getNohostthreadnum() {
		return nohostthreadnum;
	}

	public void setNohostthreadnum(Integer nohostthreadnum) {
		this.nohostthreadnum = nohostthreadnum;
	}

	public Float getTraffic() {
		return traffic;
	}

	public void setTraffic(Float traffic) {
		this.traffic = traffic;
	}

	public Long getMatchrulenum() {
		return matchrulenum;
	}

	public void setMatchrulenum(Long matchrulenum) {
		this.matchrulenum = matchrulenum;
	}

	public Long getTotalnum() {
		return totalnum;
	}

	public void setTotalnum(Long totalnum) {
		this.totalnum = totalnum;
	}

	public String getFileaddress() {
		return fileaddress;
	}

	public void setFileaddress(String fileaddress) {
		this.fileaddress = fileaddress;
	}

	public String getPushserveraddress() {
		return pushserveraddress;
	}

	public void setPushserveraddress(String pushserveraddress) {
		this.pushserveraddress = pushserveraddress;
	}

	public String getDnsserveraddress() {
		return dnsserveraddress;
	}

	public void setDnsserveraddress(String dnsserveraddress) {
		this.dnsserveraddress = dnsserveraddress;
	}

}