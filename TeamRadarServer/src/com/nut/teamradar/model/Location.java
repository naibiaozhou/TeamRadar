package com.nut.teamradar.model;

import java.sql.Time;

import com.nut.teamradar.base.BaseModel;

public class Location extends BaseModel {
	
	public final static String COL_ID = "id";
	public final static String COL_ACTIVITYID = "groupid";
	public final static String COL_USERID = "userid";
	public final static String COL_TIME = "time";
	public final static String COL_PROVIDER = "provider";
	public final static String COL_LATITUDE = "latitude";
	public final static String COL_LONGITUDE = "longitude";
	public final static String COL_ALTITUDE = "altitude";
	public final static String COL_SPEED = "speed";
	public final static String COL_HEADING = "heading";
	public final static String COL_ACCURACY = "accuracy";
	public final static String COL_AVGCN0 = "averagecn0";
	public final static String COL_MARK = "mark";
	public final static String COL_FLAG = "flag";
	
	
	private long id;
	private long groupid;
	private long userid;
	private long time;
	private String provider;
	private double latitude;
	private double longitude;
	private int    altitude;
	private float  speed;
	private float  heading;
	private float  accuracy;
	private int    averagecn0;
	private String mark;
	private int    flag;
	public Location()
	{
		id = -1;
		groupid = -1;
		userid = -1;
	    time = 0;
	    provider = "unknown";
		latitude=0;
		longitude=0;
		altitude=0;
		speed=0;
		heading=0;
		accuracy=0;
		averagecn0=0;
		mark = "unknown";
		flag=0;
	}
	
	public long getId () {
		return this.id;
	}
	
	public void setId (long id) {
		this.id = id;
	}
	
	public long getGroupid () {
		return this.groupid;
	}
	
	public void setGroupid (long groupid) {
		this.groupid = groupid;
	}
	
	public long getUserid () {
		return this.userid;
	}
	
	public void setUserid (long userid) {
		this.userid = userid;
	}
	
	public long getTime () {
		return this.time;
	}
	
	public void setTime (long intime) {
		this.time = intime;
	}

	public String getProvider () {
		return this.provider;
	}
	
	public void setProvider (String provider) {
		this.provider = provider;
	}
	
	
	public double getLatitude () {
		return this.latitude;
	}
	
	public void setLatitude (double Latitude) {
		this.latitude = Latitude;
	}
	
	public double getLongitude () {
		return this.longitude;
	}
	
	public void setLongitude (double longitude) {
		this.longitude = longitude;
	}
	
	public int getAltitude () {
		return this.altitude;
	}
	
	public void setAltitude (int altitude) {
		this.altitude = altitude;
	}

	public float getSpeed () {
		return this.speed;
	}
	
	public void setSpeed (float speed) {
		this.speed = speed;
	}
	
	public float getHeading () {
		return this.heading;
	}
	
	public void setHeading (float heading) {
		this.heading = heading;
	}		
	
	public float getAccuracy () {
		return this.accuracy;
	}
	
	public void setAccuracy (float accuracy) {
		this.accuracy = accuracy;
	}	
	public int getAveragecn0 () {
		return this.averagecn0;
	}
	
	public void setAveragecn0 (int avgCN0) {
		this.averagecn0 = avgCN0;
	}
	
	public String getMark () {
		return this.mark;
	}
	
	public void setMark (String m) {
		this.mark = m;
	}
	
	public int getFlag () {
		return this.flag;
	}
	
	public void setFlag (int flg) {
		this.flag = flg;
	}
}