package com.yc.res.bean;

import java.io.Serializable;

public class Resorderitem implements Serializable {
	private Integer roiid ;
	private Integer roid ;
	private Integer fid ;
	private Double dealprice ;
	private Integer num ;

	public void setRoiid(Integer roiid) {
		this.roiid = roiid;
	}

	public void setRoid(Integer roid) {
		this.roid = roid;
	}

	public void setFid(Integer fid) {
		this.fid = fid;
	}

	public void setDealprice(Double dealprice) {
		this.dealprice = dealprice;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public Integer getRoiid() {
		return roiid;
	}

	public Integer getRoid() {
		return roid;
	}

	public Integer getFid() {
		return fid;
	}

	public Double getDealprice() {
		return dealprice;
	}

	public Integer getNum() {
		return num;
	}
}
