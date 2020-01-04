package com.bean;

import java.util.Date;

/**
 * BusinessTask entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class BusinessTask extends com.bean.BaseRecord implements
		java.io.Serializable {

	// Fields

	private String id;
	private BusinessChannel BusinessChannel;
	private String code;
	private String name;
	private String type;
	private Date tasktime;
	private String uploadfile;
	private String downloadfile;
	private String content;
	private String remark;
	private String state;
	private Date createtime;
	private Date updatetime;
	private String createuserid;
	private String updateuserid;

	// Constructors

	/** default constructor */
	public BusinessTask() {
	}

	/** full constructor */
	public BusinessTask(BusinessChannel BusinessChannel, String code,String name, String type,
			Date tasktime, String uploadfile, String downloadfile,String content,
			String remark, String state, Date createtime, Date updatetime,
			String createuserid, String updateuserid) {
		this.BusinessChannel = BusinessChannel;
		this.code = code;
		this.name = name;
		this.type = type;
		this.tasktime = tasktime;
		this.uploadfile = uploadfile;
		this.downloadfile = downloadfile;
		this.content=content;
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

	

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getTasktime() {
		return this.tasktime;
	}

	public void setTasktime(Date tasktime) {
		this.tasktime = tasktime;
	}

	public String getUploadfile() {
		return this.uploadfile;
	}

	public void setUploadfile(String uploadfile) {
		this.uploadfile = uploadfile;
	}

	public String getDownloadfile() {
		return this.downloadfile;
	}

	public void setDownloadfile(String downloadfile) {
		this.downloadfile = downloadfile;
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
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}