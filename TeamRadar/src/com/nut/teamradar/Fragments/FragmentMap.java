package com.nut.teamradar.Fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapLongClickListener;
import com.amap.api.maps.AMap.OnMarkerDragListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.MyTrafficStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.EMMessage.ChatType;
import com.nut.teamradar.ChatActivity;
import com.nut.teamradar.MainActivity;
import com.nut.teamradar.OfflineMapActivity;
import com.nut.teamradar.R;
import com.nut.teamradar.TRServiceConnection;
import com.nut.teamradar.TeamRadarApplication;
import com.nut.teamradar.base.BaseUi;
import com.nut.teamradar.model.MarkerInfo;
import com.nut.teamradar.model.ScreenInfo;
import com.nut.teamradar.util.ActivitySession;
import com.nut.teamradar.util.Encrypt;
import com.nut.teamradar.util.MyColor;
import com.nut.teamradar.util.MyMaker;
import com.nut.teamradar.util.SenserMonitor;
import com.nut.teamradar.util.SensorObserver;
import com.nut.teamradar.webclient.ITRLocationListener;
import com.nut.teamradar.webclient.WebConnection;
import com.nut.teamradar.TRSettingsActivity;

public class FragmentMap extends BaseUi implements LocationSource,
	AMapLocationListener  {
	private static final String TAG="FragmentMap";
    private View view;
    private CheckBox traffic;
    private CheckBox satelliteview;
    private ImageView ImgSetting;
    private ImageView ImgHelp;
    private ImageView ImgCenter;
    private AMap mMap;
    private MapView mapView;
    private UiSettings mUiSettings;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	
	private com.nut.teamradar.model.Location mLocation=null;
	private LayoutInflater MyInflater = null;
	private SenserMonitor mMonitor=null;
	
	private ActivitySession mActivitySession=null;
	private Marker mRendezvousMarker = null;
	private LatLng mRendezvousMarkerPos=null;
	private FragmentMap mThis;
	private MarkerInfo mMarkerInfo;
	private AlertDialog.Builder  RendezvousDlg;
	private View  RendezvousDlgLayout;
	private long mPreTime=-1;
	private boolean FirstPosition = true;
	private int needHelp=0;
	private Marker StartMark;
	private Marker EndMark;
	private MyMaker mMaker;
	private double distance;
	private double timeelapse;
	private LatLng preLatLng;
	private LatLng latestLatLng;
	private com.nut.teamradar.model.Location preLocation=null;
	private com.nut.teamradar.model.Location latestLocation=null;
	private List<com.nut.teamradar.model.Location> mLocations=null;
	private FragmentManager fragmentManager;
	private int Index=0;
    @SuppressWarnings("unused")
    private static final String LTAG = FragmentMap.class.getSimpleName();
    
    private ITRLocationListener.Stub mLocationListener = new ITRLocationListener.Stub() {
		
		@Override
		public void OnRandezvousUpdate(int Flag) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void OnObtainRandezvous(int Flag, MarkerInfo Info)
				throws RemoteException {
			if(Flag == WebConnection.SUCCESS)
			{
				updateRendezvousMarker(Info);
			}
			
		}
		
		@Override
		public void OnLocationUploaded(int Flag) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void OnLocationUpdate(int Flag,
				List<com.nut.teamradar.model.Location> locs) throws RemoteException {
			if(Flag == WebConnection.SUCCESS)
			{
				updateLocations(locs);
			}
			
		}
	};

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	TRServiceConnection.getInstance().Connect(this.getActivity().getApplicationContext());
    	MyInflater = inflater;
    	mThis = this;
    	mLocations = new ArrayList<com.nut.teamradar.model.Location>();
        view = inflater.inflate(R.layout.fragment_map, container, false);
        traffic = (CheckBox) view.findViewById(R.id.traffic);
        satelliteview = (CheckBox) view.findViewById(R.id.satelliteview);
        ImgSetting = (ImageView)view.findViewById(R.id.imgSettings);
        ImgHelp = (ImageView)view.findViewById(R.id.imgHelp);
        ImgCenter = (ImageView)view.findViewById(R.id.imgCenter);
        fragmentManager = (((MainActivity) mThis.getActivity()).getSupportFragmentManager());
        SupportMapFragment mapfragment = (SupportMapFragment)fragmentManager.findFragmentById(R.id.bmapView);
        mMap = mapfragment.getMap();
        mMonitor = new SenserMonitor(mThis.getActivity().getApplicationContext());
        mMarkerInfo = new MarkerInfo();
        mActivitySession = new ActivitySession(mMap,mThis.getActivity());
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(mThis.getActivity());
		}
		
        setUpMap();
	    //TeamRadarApplication.getInstance().beginWriteLocationRecord();
        traffic.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				mMap.setTrafficEnabled(((CheckBox) v).isChecked());
			}
        	
        });
        satelliteview.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(satelliteview.isChecked())
				{
					mMap.setMapType(AMap.MAP_TYPE_SATELLITE);
					satelliteview.setText(mThis.getResources().getString(R.string.satelliteview));
				}
				else
				{
					mMap.setMapType(AMap.MAP_TYPE_NORMAL);
					satelliteview.setText(mThis.getResources().getString(R.string.amapmapview));
				}
			}
        });
        
        ImgSetting.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mThis.getContext(), TRSettingsActivity.class);  
		        startActivity(intent); 
			}
        	
        });
        ImgCenter.setOnClickListener(new OnClickListener(){
        	private int mCounter=0;
        	private List<com.nut.teamradar.model.Location> mFriendLocations = null;
			@Override
			public void onClick(View arg0) {
				int len;
				mFriendLocations = mActivitySession.getCurLocations();
				len = mFriendLocations.size();
				
				/*if(mCounter==0)
				{
					if(mActivitySession != null && mActivitySession.GetBounds() != null)
					{
						mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mActivitySession.GetBounds(), 5));
						Log.e(TAG, "~~~~~~~~~~~~Show all");
					}
				}
				else*/
					
				{
					com.nut.teamradar.model.Location loc;
					if(mFriendLocations != null)
					{
						loc = mFriendLocations.get((mCounter)%len);
						Log.e(TAG, String.format("~~~~~~~~~~~~Show item %d",mCounter));

					}
					else
					{
						loc = mLocation;
					}
					if(loc != null)
					{
						mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
		                        new LatLng(loc.getLatitude(),loc.getLongitude()), 18, 0, 0)));
					}
				}
				mCounter++;
				if(mCounter == len)
				{
					mCounter = 0;
				}
			}
			
        	
        });
        ImgHelp.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if(TRServiceConnection.getInstance().GetActiveGroupId() == -1)
				{
					return ;
				}
				if(needHelp == 0)
				{
					ImgHelp.setImageDrawable(getResources().getDrawable(R.drawable.icon_helpr_64));
					needHelp = 1;
					String Id = GetGroupId(MakeGroupName(TRServiceConnection.getInstance().getActiveGroupName(),
							TRServiceConnection.getInstance().getActiveGroupSubscription()));
					if(Id != null)
					{
						EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
						EMConversation conversation = EMChatManager.getInstance().getConversation(Id);
						message.setChatType(ChatType.GroupChat);
						
						TextMessageBody txtBody = new TextMessageBody(getString(R.string.requesthelp));
						message.addBody(txtBody);
						message.setReceipt(Id);
						conversation.addMessage(message);
						
						EMChatManager.getInstance().sendMessage(message, new EMCallBack(){

							@Override
							public void onError(int arg0, String arg1) {
								
							}

							@Override
							public void onProgress(int arg0, String arg1) {
								
							}

							@Override
							public void onSuccess() {
								
							}});
					}
				}
				else
				{
					ImgHelp.setImageDrawable(getResources().getDrawable(R.drawable.icon_helpb_64));
					needHelp =0;
					String Id = GetGroupId(MakeGroupName(TRServiceConnection.getInstance().getActiveGroupName(),
							TRServiceConnection.getInstance().getActiveGroupSubscription()));
					if(Id != null)
					{
						EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
						EMConversation conversation = EMChatManager.getInstance().getConversation(Id);
						message.setChatType(ChatType.GroupChat);
						
						TextMessageBody txtBody = new TextMessageBody(getString(R.string.drawbackhelp));
						// 璁剧疆娑堟伅body
						message.addBody(txtBody);
						// 璁剧疆瑕佸彂缁欒�?鐢ㄦ埛username鎴栬�呯兢鑱奼roupid
						message.setReceipt(Id);
						// 鎶妋essgage鍔犲埌conversation涓�
						conversation.addMessage(message);
						
						EMChatManager.getInstance().sendMessage(message, new EMCallBack(){

							@Override
							public void onError(int arg0, String arg1) {
								
							}

							@Override
							public void onProgress(int arg0, String arg1) {
								
							}

							@Override
							public void onSuccess() {
								
							}});
					}
				}
			}
        });
        boolean gpsenabled = mAMapLocationManager.isProviderEnabled(LocationManagerProxy.GPS_PROVIDER);
        if(!gpsenabled )
        {
        	AlertDialog OperationDlg = new AlertDialog.Builder(mThis.getActivity())
			.setTitle(getString(R.string.gpsclosed))
			.setItems(new String[] {getString(R.string.gpssetting),getString(R.string.gpssettinglater)}, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog,int which) {
					switch(which)
					{
					case 0:
			        	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			        	startActivity(intent);
						break;
					default:
							break;
					}

					dialog.dismiss();
				}
			}).show();

        }
        new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				{
			        
					TRServiceConnection.getInstance().RegisterLocationListener(mLocationListener);
			        mMonitor.Attatch(new SensorObserver(){
						@Override
						public void directionChange(float angle) {
							mActivitySession.updateCompassMarker(angle);
						}
			        });
		        }
				
			}
        	
        }, 800);
        
        return view;
    }
	/**
	 * 设置一些amap的属
	 */
	private void setUpMap() {
		mActivitySession.addBasicMarkers();
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
			        fromResource(R.drawable.man));
		myLocationStyle.strokeColor(Color.BLACK);
		myLocationStyle.radiusFillColor(Color.TRANSPARENT);
		myLocationStyle.strokeWidth(0);
		mMap.setMyLocationStyle(myLocationStyle);
		mMap.setLocationSource(this);// 设置定位监听
		mMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		mMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		
		MyTrafficStyle myTrafficStyle = new MyTrafficStyle();
		myTrafficStyle.setSeriousCongestedColor(0xff92000a);
		myTrafficStyle.setCongestedColor(0xffea0312);
		myTrafficStyle.setSlowColor(0xffff7508);
		myTrafficStyle.setSmoothColor(0xff00a209);
		mMap.setMyTrafficStyle(myTrafficStyle);
		mMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
		mUiSettings = mMap.getUiSettings();
		mUiSettings.setZoomControlsEnabled(true);
		mUiSettings.setMyLocationButtonEnabled(false);
		mUiSettings.setRotateGesturesEnabled(false);
		mUiSettings.setCompassEnabled(false);
		mUiSettings.setTiltGesturesEnabled(false);
		
		PolylineOptions opt = new PolylineOptions();
		
		
		Log.d(TAG, String.format("Line width=%f", opt.getWidth()));
		
		
		mMap.setOnMapLongClickListener(new OnMapLongClickListener(){
			LatLng markerPos;
			@Override
			public void onMapLongClick(LatLng Pos) {
				markerPos = Pos;

				if(mRendezvousMarker ==null && TRServiceConnection.getInstance().GetActiveGroupId() != -1)
				{
					RendezvousDlgLayout = MyInflater.inflate(R.layout.meatmarkerlayout,
							(ViewGroup) view.findViewById(R.id.meatmarkerinfo));  
					RendezvousDlg = new AlertDialog.Builder(mThis.getContext()).setTitle(R.string.create_rendezous).
	            			setView(RendezvousDlgLayout).
	            			setPositiveButton(getString(R.string.ok), new  DialogInterface.OnClickListener (){
	        					@Override
	        					public void onClick(DialogInterface arg0, int arg1) {
	        						String name;
	        						int editflag=0;
	        						EditText PointName = (EditText)RendezvousDlgLayout.findViewById(R.id.edtMarkerName);
	        						CheckBox Editable = (CheckBox)RendezvousDlgLayout.findViewById(R.id.chbEditable);
	        						name = PointName.getText().toString();
	        						editflag = Editable.isChecked()?1:0;
	        						
	        						mRendezvousMarkerPos = markerPos;
	        						mRendezvousMarker = mMap.addMarker(new MarkerOptions().anchor(0.5f,  0.5f)
	        										.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(mThis.getResources(), R.drawable.focus).copy(Bitmap.Config.ARGB_8888, true))).draggable(true).period(50));
	        						mRendezvousMarker.setPosition(markerPos);
	        						mMarkerInfo.setId(TRServiceConnection.getInstance().GetActiveGroupId());
	        						mMarkerInfo.setLatitude(markerPos.latitude);
	        						mMarkerInfo.setLongitude(markerPos.longitude);
	        						mMarkerInfo.setEditable(editflag);
	        						TRServiceConnection.getInstance().UpdateRandezvous(mMarkerInfo);
	        					}
	            			}).setNegativeButton(getString(R.string.cancel), null);    
					RendezvousDlg.show();
				}
				else 
				{
					if(mRendezvousMarker != null && AMapUtils.calculateLineDistance(Pos,mRendezvousMarkerPos)<0.1)
					{
						mRendezvousMarker.notifyAll();
					}
				}
			}
		});
		mMap.setOnMarkerDragListener(new OnMarkerDragListener(){

			@Override
			public void onMarkerDrag(Marker marker) {
				if(mRendezvousMarker.equals(marker) )
				{
					if(!(mMarkerInfo.getEditable() == 1 || 
							TRServiceConnection.getInstance().GetActiveOwnerId() == TRServiceConnection.getInstance().GetCurrentUserId()))
					{
						mRendezvousMarker.setPosition(mRendezvousMarkerPos);
					}
				}
			}

			@Override
			public void onMarkerDragEnd(Marker marker) {
				if(mRendezvousMarker.equals(marker))
				{
					if(mMarkerInfo.getEditable() == 1 || 
							TRServiceConnection.getInstance().GetActiveOwnerId() == TRServiceConnection.getInstance().GetCurrentUserId())
					{
						mRendezvousMarkerPos = marker.getPosition();
						mMarkerInfo.setLatitude(mRendezvousMarkerPos.latitude);
						mMarkerInfo.setLongitude(mRendezvousMarkerPos.longitude);
						TRServiceConnection.getInstance().UpdateRandezvous(mMarkerInfo);
					}
					else
					{
						mRendezvousMarker.setPosition(mRendezvousMarkerPos);
					}
				}
			}

			@Override
			public void onMarkerDragStart(Marker marker) {
				if(mRendezvousMarker.equals(marker) )
				{
					if(!(mMarkerInfo.getEditable() == 1 || 
							TRServiceConnection.getInstance().GetActiveOwnerId() == TRServiceConnection.getInstance().GetCurrentUserId()))
					{
						mRendezvousMarker.setPosition(mRendezvousMarkerPos);
					}
				}
			}
			
		});
	}

	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
		}
	}
	@Override
	public void onProviderDisabled(String arg0) {
	}
	@Override
	public void onProviderEnabled(String arg0) {
	}
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}
	private boolean m_SpeedChanged = false;
	
	private void AdjustUpdateFrequency(float speed) {
        if (speed > 20)
        {
            mAMapLocationManager.requestLocationData(
                    LocationProviderProxy.AMapNetwork, 1000, 2, this);
            m_SpeedChanged = true;
        }
        else if (speed > 10)
        {
            mAMapLocationManager.requestLocationData(
                    LocationProviderProxy.AMapNetwork, 2000, 2, this);
            m_SpeedChanged = true;
        }
        else {
            if (m_SpeedChanged)
            {
                mAMapLocationManager.requestLocationData(
                        LocationProviderProxy.AMapNetwork, 5000, 2, this);
                m_SpeedChanged = false;
            }
        }
	}
	
	@Override
	public void onLocationChanged(AMapLocation aLocation) {
	
		if (mListener != null && aLocation != null) {
			/*String str = ("定位成功:(" + aLocation.getLatitude() + "," + 
					aLocation.getLongitude() + ")"
					+ "\n速度  :" + aLocation.getSpeed() + "m/s"
					+ "\n精度   :" + aLocation.getAccuracy() + "m"
					+ "\n定位方式:" + aLocation.getProvider() + "\n");*/
			//Log.d(TAG, str);
			//Log.d(TAG, String.format("时间差为:seseon=%d,loc=%d,pre=%d,sys=%d", mActivitySession.getSessionTime(),aLocation.getTime(),mPreTime,System.currentTimeMillis()));
			if (aLocation.getAMapException().getErrorCode() == 0) {
				mListener.onLocationChanged(aLocation);
			}
			AdjustUpdateFrequency(aLocation.getSpeed());

				
			if(aLocation.getProvider().equals("gps"))
			{
				mActivitySession.setSessionTime(aLocation.getTime()-10000,ActivitySession.TIME_FINE);
			}
			else
			{
				mActivitySession.setSessionTime(aLocation.getTime()-30000,ActivitySession.TIME_COARSE);
				Date aDate = new Date();
				aLocation.setTime(aDate.getTime());
			}
			

			mLocation = new com.nut.teamradar.model.Location();
			if(TRServiceConnection.getInstance().GetActiveGroupId() != -1 && 
					aLocation.getAccuracy() < 1000 && aLocation.getAccuracy()>0)
			{
				mLocation = new com.nut.teamradar.model.Location();
				mLocation.setUserid(TRServiceConnection.getInstance().GetCurrentUserId());
				mLocation.setGroupid(TRServiceConnection.getInstance().GetActiveGroupId());//TODO
				mLocation.setTime(aLocation.getTime());
				mLocation.setLatitude(aLocation.getLatitude());
				mLocation.setLongitude(aLocation.getLongitude());
				mLocation.setAltitude((int)aLocation.getAltitude());
				mLocation.setSpeed(aLocation.getSpeed());
				if(aLocation.hasBearing())
					mLocation.setHeading(aLocation.getBearing());
				else
					mLocation.setHeading(0);
				if(aLocation.hasAccuracy())
					mLocation.setAccuracy(aLocation.getAccuracy());
				else
					mLocation.setAccuracy(80);
				mLocation.setAveragecn0(31);//TODO
				mLocation.setFlag(needHelp);
				mLocation.setProvider(aLocation.getProvider());
				mLocation.setMark(TRServiceConnection.getInstance().GetMark());
				TRServiceConnection.getInstance().UploadLocation(mLocation);
				Log.d(TAG, "Location Update ");
				if(FirstPosition)
				{
					mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
	                        new LatLng(mLocation.getLatitude(),mLocation.getLongitude()), 18, 0, 0)));
					FirstPosition = false;
				}
			}
			else
			{
				LatLng CurPos = new LatLng(aLocation.getLatitude(), aLocation
						.getLongitude());
				if(TRServiceConnection.getInstance().GetActiveGroupId() == -1)
					mActivitySession.updateDitectionMarker(CurPos);
				mLocation.setLatitude(aLocation.getLatitude());
				mLocation.setLongitude(aLocation.getLongitude());
			}
			/*TeamRadarApplication.getInstance().writeLocationRecord(TeamRadarApplication.getInstance().getSubscription(),
			        TeamRadarApplication.getInstance().getName(), mLocation);*/
			mPreTime = aLocation.getTime();
		}
	}
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this.getActivity());
		}
		mAMapLocationManager.requestLocationData(
                LocationProviderProxy.AMapNetwork, 5000, 2, this);
 
		mAMapLocationManager.setGpsEnable(true);
		
	}
	@Override
	public void deactivate() {
		mAMapLocationManager.removeUpdates(this);
	}
    private String MakeGroupName(String name,String sub)
    {
    	String Out;
    	Out = sub+":"+name;
    	return Out;
    }
    private String GetGroupId(String GroupName)
    {
    	List<EMGroup> grouplist = EMGroupManager.getInstance().getAllGroups();
		Iterator<EMGroup> it = grouplist.iterator();
		while(it.hasNext())
		{
			EMGroup grp = it.next();
			if(grp.getGroupName().equals(GroupName))
			{
				return grp.getGroupId();
			}
		}
		return null;
    }
	public void updateLocations(List<com.nut.teamradar.model.Location> locs)
	{
		mActivitySession.AddLocations(locs);
	}
	
	public void updateRendezvousMarker(MarkerInfo Info)
	{
		if(Info.getLatitude() != -1 && Info.getLongitude() != -1)
		{
			if(TRServiceConnection.getInstance().GetActiveOwnerId() != TRServiceConnection.getInstance().GetCurrentUserId() || 
					mRendezvousMarker == null || (mMarkerInfo != null && mMarkerInfo.getEditable() == 1))
			{
	
				mRendezvousMarkerPos = new LatLng(Info.getLatitude(),Info.getLongitude());
				
				if(mRendezvousMarker == null)
				{
					mRendezvousMarker = mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
							.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(mThis.getResources(), R.drawable.focus).copy(Bitmap.Config.ARGB_8888, true))).draggable(true).period(50));
				}
				mRendezvousMarker.setPosition(mRendezvousMarkerPos);
				mMarkerInfo = Info;
			}
		}
	}
	public void ActivityStart()
	{
		mMap.clear();
		mActivitySession.StartSession();
		if(mRendezvousMarker != null)
			mRendezvousMarker = null;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this.getActivity());
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.requestLocationData(
	                LocationProviderProxy.AMapNetwork, 5000, 2, this);
	 
			mAMapLocationManager.setGpsEnable(true);
		}
		
	}
	public void ActivityStop()
	{
		mPreTime = -1;
		mActivitySession.StopSession();
	}
	public String getUserName(long userid) {
	    return mActivitySession.getUserName(userid);
	}
	private BitmapDescriptor getBitmapDes(long userid,float speed, double distance,double elapse,long time,int em, boolean flag) {

		String name = getUserName(userid);
		mMaker.setSpeed(speed);
		mMaker.setDistance(distance);
		mMaker.setTimeelapse(elapse);
		mMaker.setName(name);
		mMaker.setEmergency(em);
		if(flag)
			return BitmapDescriptorFactory.fromBitmap(
					mMaker.GetCoolMarker(MyColor.getInstance().getColor(9, speed*3.6),time));
		else
			return BitmapDescriptorFactory.fromBitmap(mMaker.GetMarker(time));
	}
	private void addPolyLineGps(AMap map,double speed)
	{
		PolylineOptions opt = new PolylineOptions();
		opt.add(preLatLng,latestLatLng);
		opt.geodesic(true);
		opt.color(MyColor.getInstance().getColor(9, speed*3.6));
		ScreenInfo info = TRServiceConnection.getInstance().GetScreenInfo();
		opt.width((6*info.width/320));
		map.addPolyline(opt);
	}
	private void addPolyLineLbs(AMap map,double speed)
	{
		PolylineOptions opt = new PolylineOptions();
		opt.add(preLatLng,latestLatLng);
		opt.geodesic(true);
		opt.color(Color.BLUE);
		opt.setDottedLine(true);
		opt.width(4);
		map.addPolyline(opt);
	}
	private void updateMarkers(com.nut.teamradar.model.Location loc)
	{
		EndMark.setIcon(getBitmapDes(loc.getUserid(),loc.getSpeed(),distance,timeelapse,loc.getTime(),loc.getFlag(),true));
		EndMark.setPosition(latestLatLng);
		EndMark.setVisible(true);
	}
	Handler mHandle = new Handler(){
		@Override  
        public void handleMessage(Message msg) {
            switch (msg.what) {   
            case 0x12:   
            	updateLocationTrejactorr();
                break;   
            }
            super.handleMessage(msg); 
            
        }
	};
	
	void updateLocationTrejactorr()
	{
		if(Index >=  mThis.mLocations.size())
		{
			return ;
		}
		if(preLocation == null)
		{
			preLocation = mLocations.get(Index);
			preLatLng = new LatLng(preLocation.getLatitude(),preLocation.getLongitude());
			mMaker = new MyMaker(mThis.getActivity());
			StartMark=  mMap.addMarker(new MarkerOptions().anchor(0.13f, 1.0f).period(50));
			StartMark.setIcon(getBitmapDes(preLocation.getUserid(),0.0f,0.0d,0.0d,preLocation.getTime(),0,false));
			StartMark.setPosition(preLatLng);
			StartMark.setVisible(true);
			EndMark=  mMap.addMarker(new MarkerOptions().anchor(0.1f, 1.0f).period(50));
			EndMark.setIcon(getBitmapDes(preLocation.getUserid(),0.0f,0.0d,0.0d,preLocation.getTime(),0,false));
			EndMark.setPosition(preLatLng);
			EndMark.setVisible(false);
			mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                    new LatLng(preLocation.getLatitude(),preLocation.getLongitude()), 12, 0, 0)));
		}
		else
		{
			if(mLocations.get(Index).getProvider().equals("gps") && preLocation.getProvider().equals("gps"))
			{
				latestLocation = mLocations.get(Index);
				latestLatLng = new LatLng(latestLocation.getLatitude(),latestLocation.getLongitude());
				double timediff = latestLocation.getTime() - preLocation.getTime();
				double dist = AMapUtils.calculateLineDistance(latestLatLng ,preLatLng);
				distance += dist/1000;
				timeelapse += timediff/1000;	
				if(dist > 0)
				{
					addPolyLineGps(mMap,latestLocation.getSpeed());
				}
				preLocation = latestLocation;
				preLatLng = new LatLng(preLocation.getLatitude(),preLocation.getLongitude());
				if(Index == mThis.mLocations.size()-1)
				{
					updateMarkers(latestLocation);
				}
			}
			else
			{
				latestLocation = mLocations.get(Index);
				latestLatLng = new LatLng(latestLocation.getLatitude(),latestLocation.getLongitude());
				double dist = AMapUtils.calculateLineDistance(latestLatLng ,preLatLng);
				if(dist > 0)
				{
					addPolyLineLbs(mMap,latestLocation.getSpeed());
				}
				preLocation = latestLocation;
				preLatLng = new LatLng(preLocation.getLatitude(),preLocation.getLongitude());
				if(Index == mThis.mLocations.size()-1)
				{
					updateMarkers(latestLocation);
				}

			}
		}
	}
	private Thread mThread= null; 
	public void ShowHistoryTrejactory(ArrayList<com.nut.teamradar.model.Location> locs)
	{
		mMap.clear();
		mLocations.clear();
		mLocations.addAll(locs);
		mThread = new Thread(new Runnable(){

			@Override
			public void run() {
				timeelapse = 0;
				distance = 0;
				Log.e(TAG, "ShowHistoryTrejactory : "+String.valueOf(mThis.mLocations.size()));
				for(Index=0;Index<mThis.mLocations.size() && mThis.mLocations.size() > 1;Index++)
				{
		            Message message = new Message();   
		            message.what = 0x12;
		            mHandle.sendMessage(message);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		mThread.start();
	}
    @Override  
    public void onDestroyView() {  
    	mAMapLocationManager.setGpsEnable(false);
        mAMapLocationManager.removeUpdates(this);
        TRServiceConnection.getInstance().RemoveLocationListener(mLocationListener);
        super.onDestroyView();  

    }  
    @Override  
    public void onDestroy() {  
    	mAMapLocationManager.setGpsEnable(false);
        mAMapLocationManager.removeUpdates(this);
        TRServiceConnection.getInstance().RemoveLocationListener(mLocationListener);
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

    } 
}
