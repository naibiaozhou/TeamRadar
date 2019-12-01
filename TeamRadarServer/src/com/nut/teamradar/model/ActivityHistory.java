package com.nut.teamradar.model;

import com.nut.teamradar.base.BaseModel;

public class ActivityHistory extends BaseModel {
	
	// model columns
	public final static String COL_ID = "id";
	public final static String COL_USERID = "userid";
	public final static String COL_ACTIVITYID = "activityid";
	public final static String COL_DATETIME= "datetime";
	public final static String COL_MARK = "mark";

	
	private long id;
	private long userId;
	private long activityId;
	private String datetime;
	private String mark;
	
	public ActivityHistory () {
		id=-1;
		userId=-1;
		activityId=-1;
		datetime="1970-01-01-01-01-01";
		mark="unknown";	
	}
	
	public long getId () {
		return this.id;
	}
	
	public void setId (long id) {
		this.id = id;
	}

	public long getUserId () {
		return this.userId;
	}
	
	public void setUserId (long id) {
		this.userId = id;
	}
	
	public long getActivityId () {
		return this.activityId;
	}
	
	public void setActivityId (long id) {
		this.activityId = id;
	}
	
	public String getDatetime () {
		return this.datetime;
	}
	
	public void setDatetime (String t) {
		this.datetime = t;
	}
	
	public String getMark () {
		return this.mark;
	}
	
	public void setMark (String m) {
		this.mark = m;
	}
}