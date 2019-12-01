package com.nut.teamradar.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.nut.teamradar.R;
import com.nut.teamradar.TRServiceConnection;
import com.nut.teamradar.TeamRadarApplication;
import com.nut.teamradar.model.Location;
import com.nut.teamradar.model.ScreenInfo;

public class ActivitySession {
	private static final String TAG="ActivitySeesion";
    public static final int TIME_INVALIDE = 8003;
    public static final int TIME_COARSE = 8004;
    public static final int TIME_FINE = 8005;
	private int colorIndex = 0;
	private int mColor[] = {Color.BLACK,Color.BLUE,Color.CYAN,Color.DKGRAY,Color.GREEN,Color.MAGENTA,Color.GRAY,Color.RED,Color.YELLOW,Color.LTGRAY};
	private  List<Locations> mMemberLocations;
	private List<Location> mCurLocations;
	private AMap mMap;
	private Context mContext;
	private Marker markerBg;// background
	private Marker markerDirection;// diretion
	private Marker markerHeading;// heading
	private long GroupId=-1;
    private long SessionTime=-1;
    private int SessionTimeAccuracy = TIME_INVALIDE;
    private MyMaker mMaker;
    private LatLngBounds  mViewBounds=null;
    private boolean started = false;
    
    
	public ActivitySession(AMap Map,Context ctx)
	{
		mContext = ctx;
		mMap = Map;
		mMemberLocations = new ArrayList<Locations>();
		mCurLocations = new ArrayList<Location>();
	}
	public void updateCompassMarker(float angle)
	{
		if (markerDirection != null) {
			markerBg.setVisible(true);
			markerDirection.setVisible(true);
			markerDirection.setRotateAngle(-angle); 
			//mMap.invalidate();
		}	
	}
    public void setGroupId(long Id) {
    	GroupId = Id;
	}

    public long getGroupId() {
		return GroupId;
	}
    public List<Location> getCurLocations()
    {
    	return mCurLocations;
    }
	public void updateDitectionMarker(LatLng Pos)
	{
		markerBg.setPosition(Pos);
		markerDirection.setPosition(Pos);	
	}
	public void setSessionTime(long time,int accuracy)
	{
		if(SessionTimeAccuracy == ActivitySession.TIME_INVALIDE)
		{
			SessionTimeAccuracy = accuracy;
			this.SessionTime = time;
		}
		else if(SessionTimeAccuracy == ActivitySession.TIME_COARSE)
		{
			SessionTimeAccuracy = accuracy;
			this.SessionTime = time;
		}
		else
		{
			//do nothing;
		}
	}
	public long getSessionTime()
	{
		return this.SessionTime;
	}
	
	public int getSessionTimeAccuracy()
	{
		return this.SessionTimeAccuracy;
	}
	public void addBasicMarkers()
	{
    	float xscale=0.4f ,yscale=0.4f;
    	ScreenInfo info = TRServiceConnection.getInstance().GetScreenInfo();
    	if(info.width < 600 )
    	{
    		xscale = 0.4f;
    		yscale = 0.4f;
    	}
    	Log.e(TAG,String.format("addBasicMarkers ~~~~~~scale %f %f %d \r\n", xscale,yscale,info.width));
		Resources res = mContext.getResources();
		
		Bitmap bmp=BitmapFactory.decodeResource(res, R.drawable.compassbg).copy(Bitmap.Config.ARGB_8888, true);
		Matrix matrix = new Matrix(); 
		matrix.postScale(xscale,xscale); 
		Bitmap scaledBmp = Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),matrix,true);
		markerBg = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
				.icon(BitmapDescriptorFactory.fromBitmap(scaledBmp)).period(50));
		
		Bitmap Drbmp=BitmapFactory.decodeResource(res, R.drawable.direction_128).copy(Bitmap.Config.ARGB_8888, true);
		Bitmap DrscaledBmp = Bitmap.createBitmap(Drbmp,0,0,Drbmp.getWidth(),Drbmp.getHeight(),matrix,true);
		markerDirection = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
				.icon(BitmapDescriptorFactory.fromBitmap(DrscaledBmp)).period(50));
		
		Bitmap Headbmp=BitmapFactory.decodeResource(res, R.drawable.go_512).copy(Bitmap.Config.ARGB_8888, true);
		Bitmap HeadScaledBmp = Bitmap.createBitmap(Headbmp,0,0,Headbmp.getWidth(),Headbmp.getHeight(),matrix,true);		
		markerHeading = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
				.icon(BitmapDescriptorFactory.fromBitmap(HeadScaledBmp)).period(50));
		
		markerBg.setVisible(true);
		markerDirection.setVisible(false);
		markerHeading.setVisible(true);
	}
	public LatLngBounds GetBounds()
	{
		return this.mViewBounds;
	}
    public void StartSession()
    {
		GroupId = TRServiceConnection.getInstance().GetActiveGroupId();
		mMemberLocations.removeAll(mMemberLocations);
		addBasicMarkers();
    }
    public void StopSession()
    {
		SessionTime=-1;
		SessionTimeAccuracy = TIME_INVALIDE;
    }
	public void AddLocations(List<Location> locs)
	{
		int i,j;
		com.nut.teamradar.model.Location loc,preloc;
		Locations memloc;
		mCurLocations.removeAll(mCurLocations);
		mCurLocations.addAll(locs);
		//check new activity actived;

		mViewBounds = null;
		
		// add Locations and markers
		for(i=0;i<locs.size();i++)
		{
			loc = locs.get(i);
			if(mViewBounds == null)
			{
				mViewBounds = new LatLngBounds.Builder().include(new LatLng(loc.getLatitude(),loc.getLongitude())).build();
			}
			else
			{
				mViewBounds.including(new LatLng(loc.getLatitude(),loc.getLongitude()));
			}
			for(j=0;j<mMemberLocations.size();j++)
			{
				
				if(loc.getUserid() == mMemberLocations.get(j).getUserid())
				{
					mMemberLocations.get(j).AddALocation(loc,this.SessionTime,mMap);
					Log.e(TAG, "mMemberLocations.AddLocation:"+String.valueOf(j)+" size is "+String.valueOf(mMemberLocations.get(j).getPositionCount())+"\n\n");
					break;
				}
			}
			if(j== mMemberLocations.size())
			{
				mMaker = new MyMaker(mContext);
				Marker mk=  mMap.addMarker(new MarkerOptions().anchor(0.13f, 1.0f).period(50));
				mk.setIcon(getBitmapDes(loc.getUserid(),0,0,0,loc.getTime(),false));
				mk.setVisible(false);
				Marker mk1=  mMap.addMarker(new MarkerOptions().anchor(0.1f, 1.0f).period(50));
				mk1.setIcon(getBitmapDes(loc.getUserid(),0,0,0,loc.getTime(),false));	
				mk1.setVisible(false);
				mMemberLocations.add((new Locations()).setMarkers(mk, mk1, markerBg, markerDirection, markerHeading)
						                              .setMyMarker(mMaker)
						                              .setColorSerial(MyColor.getInstance().GetColorSeries())
						                              .AddALocation(loc,SessionTime,mMap));
			}
		}
	}
	public String getUserName(long userid) {
	    return TRServiceConnection.getInstance().GetUserName(GroupId, userid);
	}

	private BitmapDescriptor getBitmapDes(long userid,float speed, double distance,double elapse, long time, boolean flag) {

		String name = getUserName(userid);
		mMaker.setSpeed(speed);
		mMaker.setDistance(distance);
		mMaker.setTimeelapse(elapse);
		mMaker.setName(name);
		if(flag)
			return BitmapDescriptorFactory.fromBitmap(mMaker.GetCoolMarker(0,time));
		else
			return BitmapDescriptorFactory.fromBitmap(mMaker.GetMarker(time));
	}
}
