package com.bean;

import java.util.Date;

/**
 * ManageUser entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class ManageUser extends com.bean.BaseRecord implements
		java.io.Serializable {

	// Fields

	private String id;
	private String username;
	private String passwd;
	private String email;
	private String nowloginip;
	private String lastloginip;
	private Integer logintimes;
	private Date lastlogintime;
	private String remark;
	private String state;
	private Date createtime;
	private Date updatetime;
	private String createuserid;
	private String updateuserid;

	// Constructors

	/** default constructor */
	public ManageUser() {
	}

	/** full constructor */
	public ManageUser(String username, String passwd, String email,
			String nowloginip, String lastloginip, Integer logintimes,
			Date lastlogintime, String remark, String state, Date createtime,
			Date updatetime, String createuserid, String updateuserid) {
		this.username = username;
		this.passwd = passwd;
		this.email = email;
		this.nowloginip = nowloginip;
		this.lastloginip = lastloginip;
		this.logintimes = logintimes;
		this.lastlogintime = lastlogintime;
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

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswd() {
		return this.passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNowloginip() {
		return this.nowloginip;
	}

	public void setNowloginip(String nowloginip) {
		this.nowloginip = nowloginip;
	}

	public String getLastloginip() {
		return this.lastloginip;
	}

	public void setLastloginip(String lastloginip) {
		this.lastloginip = lastloginip;
	}

	public Integer getLogintimes() {
		return this.logintimes;
	}

	public void setLogintimes(Integer logintimes) {
		this.logintimes = logintimes;
	}

	public Date getLastlogintime() {
		return this.lastlogintime;
	}

	public void setLastlogintime(Date lastlogintime) {
		this.lastlogintime = lastlogintime;
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