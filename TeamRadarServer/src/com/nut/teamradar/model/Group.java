package com.nut.teamradar.model;

import com.nut.teamradar.base.BaseModel;

public class Group extends BaseModel {
	public final static String COL_ID = "id";
	public final static String COL_NAME = "name";
	public final static String COL_OWNERID = "ownerid";
	public final static String COL_SUBSCRIPTION = "subscription";
	public final static String COL_COMMENT = "comment";
	public final static String COL_AVAILABLE = "available";
	private long id;
	private long ownerId;
	private String name;
	private String subscription;
	private String comment;
	private int available;

	public Group()
	{
		id = -1;
		ownerId = -1;
		name="dumy";
		subscription = "dumy";
		comment="dumy";	
		available = 1;
	}
	public long getId () {
		return this.id;
	}
	public void setId (long id) {
		this.id = id;
	}
	
	public long getOwnerId () {
		return this.ownerId;
	}
	
	public void setOwnerId (long ownerid) {
		this.ownerId = ownerid;
	}
	
	public String getName () {
		return this.name;
	}
	
	public void setName (String name) {
		this.name = name;
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
