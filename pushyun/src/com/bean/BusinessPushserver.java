package com.bean;

import java.util.Date;

/**
 * BusinessPushserver entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class BusinessPushserver extends com.bean.BaseRecord implements
		java.io.Serializable {

	// Fields

	private String id;
	private String code;
	private String name;
	private String sign;
	private String version;
	private String sendermac;
	private String sendername;
	private String routermac;
	private String serveraddress;
	private String remark;
	private String state;
	private Date createtime;
	private Date updatetime;
	private String createuserid;
	private String updateuserid;

	// Constructors

	/** default constructor */
	public BusinessPushserver() {
	}

	/** full constructor */
	public BusinessPushserver(String code, String name, String sign,
			String version, String sendermac, String sendername,
			String routermac, String serveraddress, String remark,
			String state, Date createtime, Date updatetime,
			String createuserid, String updateuserid) {
		this.code = code;
		this.name = name;
		this.sign = sign;
		this.version = version;
		this.sendermac = sendermac;
		this.sendername = sendername;
		this.routermac = routermac;
		this.serveraddress = serveraddress;
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