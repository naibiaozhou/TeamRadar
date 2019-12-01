package com.nut.teamradar.model;

import com.nut.teamradar.base.BaseModel;

public class Member extends BaseModel {
	public final static String COL_ID = "id";
	public final static String COL_ACTIVITYID = "activityid";
	public final static String COL_USERID = "userid";
	public final static String COL_USERNAME = "username";
	public final static String COL_SUBSCRIPTION = "subscription";
	public final static String COL_COMMENT = "comment";
	public final static String COL_AVAILABLE = "available";
	private long id;
	private long activityId;
	private long userId;
	private String username;
	private String subscription;
	private String comment;
	private int available;
	
	public Member()
	{
		id = -1;
		activityId = -1;
		userId=-1;
		username = "dumy";
		subscription = "dumy";
		comment = "dumy";
		available = 1;
	}
	public long getId () {
		return this.id;
	}
	
	public void setId (long id) {
		this.id = id;
	}
	
	public long getActivityId () {
		return this.activityId;
	}
	
	public void setActivityId (long actid) {
		this.activityId = actid;
	}
	
	public long getUserId () {
		return this.userId;
	}
	
	public void setUserId (long userid) {
		this.userId = userid;
	}
	
	public String getUsername () {
		return this.username;
	}
	
	public void setUsername (String name) {
		this.username = name;
	}
	public String getSubscription () {
		return this.subscription;
	}
	
	public void setSubscription (String sub) {
		this.subscription = sub;
	}	
	public String getComment () {
		return this.comment;
	}
	
	public void setComment (String comment) {
		this.comment = comment;
	}
	public int getAvailable () {
		return this.available;
	}
	public void setAvailable (int valid) {
		this.available = valid;
	}
}