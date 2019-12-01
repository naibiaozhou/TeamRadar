package com.nut.teamradar.util;

import java.util.ArrayList;
import java.util.List;

import com.nut.teamradar.model.Location;

public class UserLocations {
	
	private List<Location> mLocations;
	private long groupid;
	private long userid;
	public UserLocations()
	{
		groupid = -1;
		userid = -1;
		mLocations = new ArrayList<Location>();
	}
	public long getUserid()
	{
		return userid;
	}
	public long getGroupid()
	{
		return groupid;
	}
	public List<Location> getLocations()
	{
		return mLocations;
	}
	public UserLocations AddALocation(Location loc)
	{
		groupid = loc.getGroupid();
		userid = loc.getUserid();
		mLocations.add(loc);
		return this;
	}
}
