package com.nut.teamradar.model;

import com.nut.teamradar.base.BaseModel;

public class ShortMessage extends BaseModel  {
	
	public final static String COL_ID = "id";
	public final static String COL_FROM = "fromid";
	public final static String COL_TO = "toid";
	public final static String COL_TOSUBSCRIPTION = "tosubscription";
	public final static String COL_TONAME = "toname";
	public final static String COL_FROMSUBSCRIPTION = "fromsubscription";
	public final static String COL_FROMNAME = "fromname";
	public final static String COL_MESSAGE = "message";	
	
	private long id;
	private long fromId;
	private long toId;
	private String fromSubscription;
	private String fromName;
	private String toSubscription;
	private String toName;
	private String message;

	public ShortMessage()
	{
		id = -1;
		fromId = -1;
		toId = -1;
		toSubscription = "13800000000";
		message = "dumy";
	}
	
	public long getId () {
		return this.id;
	}
	public void setId (long id) {
		this.id = id;
	}
	
	public long getFromId () {
		return this.fromId;
	}
	public void setFromId (long from) {
		this.fromId = from;
	}

	public long getToId () {
		return this.toId;
	}
	public void setToId (long to) {
		this.toId = to;
	}

	public String getToSubscription () {
		return this.toSubscription;
	}
	public void setToSubscription (String tosub) {
		this.toSubscription = tosub;
	}

	public String getToName () {
		return this.toName;
	}
	public void setToName (String toname) {
		this.toName = toname;
	}
	
	public String getFromSubscription () {
		return this.fromSubscription;
	}
	public void setFromSubscription (String fromsub) {
		this.fromSubscription = fromsub;
	}
	public String getFromName () {
		return this.fromName;
	}
	public void setFromName (String fromname) {
		this.fromName = fromname;
	}
	public String getMessage () {
		return this.message;
	}
	
	public void setMessage  (String message) {
		this.message = message;
	}
}
