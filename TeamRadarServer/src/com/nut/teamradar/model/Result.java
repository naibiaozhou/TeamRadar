package com.nut.teamradar.model;

import com.nut.teamradar.base.BaseModel;

public class Result extends BaseModel {
	private String data;
	
	public String getData () {
		return this.data;
	}
	
	public void setData (String data) {
		this.data = data;
	}
}
