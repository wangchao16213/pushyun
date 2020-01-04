package com.common.comm;


import java.util.List;

import com.bean.ManageUser;





public class UserSession {

	
	private ManageUser manageUser;
	private String cookiekey;
	

	

	public ManageUser getUsers() {
		return manageUser;
	}

	public void setUsers(ManageUser manageUser) {
		this.manageUser = manageUser;
	}

	public String getCookiekey() {
		return cookiekey;
	}

	public void setCookiekey(String cookiekey) {
		this.cookiekey = cookiekey;
	}



}
