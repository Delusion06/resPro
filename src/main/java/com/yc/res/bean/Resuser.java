package com.yc.res.bean;

import java.io.Serializable;

public class Resuser implements Serializable {

	private Integer userid;
	private String username;
	private String pwd;
	private String email;

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getUserid() {
		return userid;
	}

	public String getUsername() {
		return username;
	}

	public String getPwd() {
		return pwd;
	}

	public String getEmail() {
		return email;
	}
}
