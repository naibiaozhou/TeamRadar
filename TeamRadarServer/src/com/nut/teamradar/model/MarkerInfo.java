package com.nut.teamradar.model;

import com.nut.teamradar.base.BaseModel;

public class MarkerInfo extends BaseModel{
	public final static String COL_ID = "id";
	public final static String COL_LATITUDE = "latitude";
	public final static String COL_LONGITUDE = "longitude";
	public final static String COL_EDITABLE = "editable";
	private long id;
	private double latitude;
	private double longitude;
	private int editable;
	public MarkerInfo()
	{
		id = -1;
		latitude =-1;
		longitude=-1;
		editable=1;		
	}
	public long getId () {
		return this.id;
	}
	public void setId (long id) {
		this.id = id;
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
	public int getEditable () {
		return this.editable;
	}
	public void setEditable  (int able) {
		this.editable = able;
	}
}
