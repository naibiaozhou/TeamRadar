package com.nut.teamradar.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.graphics.Color;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.PolylineOptions;
import com.nut.teamradar.TRServiceConnection;
import com.nut.teamradar.TeamRadarApplication;
import com.nut.teamradar.model.Location;
import com.nut.teamradar.model.ScreenInfo;

public class Locations {
	private static final String TAG = "Locations";
	private Location preLocation=null;
	private Location latestLocation=null;
	private long groupid;
	private long userid;
	private int mColorSerial;
	private int Count=0;
	private long time=0;
	private double distance;
	private double timeelapse;
	private int state=0;
	private Marker markerStart;//
	private Marker markerCurrent;
	private Marker markerBg;// background
	private Marker markerDirection;// diretion
	private Marker markerHeading;// heading
	private MyMaker mMakerMaker;
	
	public Locations()
	{
		Count = 0;
		groupid = -1;
		userid = -1;
		mColorSerial = -1;
		distance =0;
		timeelapse =0;
	}
	public Locations setMarkers(Marker mkrStart,Marker mkrCur,
			Marker mkrBg,Marker mkrDr,Marker mkrHeading)
	{
		markerStart = mkrStart;
		markerCurrent = mkrCur;
		markerBg = mkrBg;
		markerDirection = mkrDr;
		markerHeading = mkrHeading;
		return this;
	}
	public Locations setMyMarker(MyMaker maker)
	{
		mMakerMaker = maker;
		return this;
	}
	
	public long getUserid()
	{
		return userid;
	}
	public long getGroupid()
	{
		return groupid;
	}
	public int getPositionCount()
	{
		return Count;
	}
	public LatLng getPreLatLng()
	{
		if(preLocation != null)
		{
			return new LatLng(preLocation.getLatitude(),preLocation.getLongitude());
		}
		return null;
	}
	public LatLng getLastLatLng()
	{
		if(latestLocation != null)
		{
			return new LatLng(latestLocation.getLatitude(),latestLocation.getLongitude());
		}
		return null;
	}
	public Location getPreLocation()
	{
		return preLocation;
	}
	public Location getLatestLocation()
	{
		return latestLocation;
	}
	private void addPolyLineGps(AMap map,double speed)
	{
		PolylineOptions opt = new PolylineOptions();
		opt.add(getPreLatLng(),getLastLatLng());
		opt.geodesic(true);
		opt.color(MyColor.getInstance().getColor(getColorSerial(), speed*3.6));
		ScreenInfo info = TRServiceConnection.getInstance().GetScreenInfo();
		opt.width((6*info.width/320));
		map.addPolyline(opt);
	}
	private void addPolyLineLbs(AMap map,double speed)
	{
		PolylineOptions opt = new PolylineOptions();
		opt.add(getPreLatLng(),getLastLatLng());
		opt.geodesic(true);
		opt.color(Color.BLUE);
		opt.setDottedLine(true);
		opt.width(4);
		map.addPolyline(opt);
	}
	private void updateMarkers(Location loc,boolean updateMarkers)
	{
		LatLng lastLatLng = null;
		lastLatLng = new LatLng(loc.getLatitude(),loc.getLongitude());
		markerCurrent.setIcon(getBitmapDes(loc.getUserid(),loc.getSpeed(),getDistance(),getTimeElapse(),loc.getTime(),loc.getFlag(),true));
		markerCurrent.setPosition(getLastLatLng());
		markerCurrent.setVisible(true);
		if(updateMarkers && loc.getUserid() == TRServiceConnection.getInstance().GetCurrentUserId())
		{
			markerBg.setPosition(lastLatLng);
			markerBg.setVisible(true);
			markerDirection.setPosition(lastLatLng);
			markerDirection.setVisible(true);
			if(loc.getSpeed() > 0.1)
			{
				markerHeading.setVisible(true);
				markerHeading.setRotateAngle(-loc.getHeading());
				markerHeading.setPosition(lastLatLng);
			}
			else
			{
				markerHeading.setVisible(false);
			}
		}
	}
	public String getUserName(long userid) {
	    return TRServiceConnection.getInstance().GetUserName(TRServiceConnection.getInstance().GetActiveGroupId(), userid);
	}

	private BitmapDescriptor getBitmapDes(long userid,float speed, double distance,double elapse,long time,int em, boolean flag) {

		String name = getUserName(userid);
		mMakerMaker.setSpeed(speed);
		mMakerMaker.setDistance(distance);
		mMakerMaker.setTimeelapse(elapse);
		mMakerMaker.setName(name);
		mMakerMaker.setEmergency(em);
		if(flag)
			return BitmapDescriptorFactory.fromBitmap(
					mMakerMaker.GetCoolMarker(MyColor.getInstance().getColor(getColorSerial(), speed*3.6),time));
		else
			return BitmapDescriptorFactory.fromBitmap(mMakerMaker.GetMarker(time));
	}
	public Locations AddALocation(Location loc,long SessionStartTime,AMap map)
	{
		double dist;
		groupid = loc.getGroupid();
		userid = loc.getUserid();

		if(SessionStartTime != -1)
		{
			if(Count == 0)
			{
				latestLocation = loc; 
				markerStart.setVisible(true);
				markerStart.setPosition(getLastLatLng());
				updateMarkers(latestLocation,true);
				preLocation = latestLocation;
				if(loc.getTime() >= SessionStartTime)
				{
					Count++;
				}
			}
			else
			{
				latestLocation = loc; 
				double timediff = latestLocation.getTime() - preLocation.getTime();
				if(preLocation.getProvider().equals(latestLocation.getProvider()) && latestLocation.getProvider().equals("gps"))
				{
					if(timediff >1)
					{
						dist = AMapUtils.calculateLineDistance(new LatLng(latestLocation.getLatitude(),latestLocation.getLongitude()) ,
								new LatLng(preLocation.getLatitude(),preLocation.getLongitude()));
						this.distance += dist;
						timeelapse += timediff/1000;
						addPolyLineGps(map,latestLocation.getSpeed());
						updateMarkers(latestLocation,true);
						preLocation = latestLocation;
					}
				}
				else
				{
					if(preLocation.getTime() != latestLocation.getTime())
					{
						addPolyLineLbs(map,latestLocation.getSpeed());
						updateMarkers(latestLocation,false);
						preLocation = latestLocation;
					}
				}
				Count++;
			}
		}
		
		return this;
	}

	public double getDistance()
	{
		return this.distance/1000;
	}
	public double getTimeElapse()
	{
		return timeelapse;
	}
	public Locations setColorSerial(int col)
	{
		mColorSerial = col;
		return this;
	}
	public int getColorSerial()
	{
		return mColorSerial;
	}

}
