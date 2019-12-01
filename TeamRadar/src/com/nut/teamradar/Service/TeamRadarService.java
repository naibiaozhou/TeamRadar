package com.nut.teamradar.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.nut.teamradar.Service.ApplicationData;
import com.nut.teamradar.Constant;
import com.nut.teamradar.MainActivity;
import com.nut.teamradar.R;
import com.nut.teamradar.TRServiceConnection;
import com.nut.teamradar.model.ActivityHistory;
import com.nut.teamradar.model.Contact;
import com.nut.teamradar.model.Group;
import com.nut.teamradar.model.Location;
import com.nut.teamradar.model.MarkerInfo;
import com.nut.teamradar.model.Member;
import com.nut.teamradar.model.PhoneContact;
import com.nut.teamradar.model.ScreenInfo;
import com.nut.teamradar.model.ShortMessage;
import com.nut.teamradar.model.User;
import com.nut.teamradar.util.DialogHelper;
import com.nut.teamradar.util.GroupMembers;
import com.nut.teamradar.util.UpdateManager;
import com.nut.teamradar.util.UserLocations;
import com.nut.teamradar.webclient.HttpConnection;
import com.nut.teamradar.webclient.ITRConnectionListener;
import com.nut.teamradar.webclient.ITRContactListener;
import com.nut.teamradar.webclient.ITRGroupListener;
import com.nut.teamradar.webclient.ITRHistoryListener;
import com.nut.teamradar.webclient.ITRLocationListener;
import com.nut.teamradar.webclient.ITRMessageListener;
import com.nut.teamradar.webclient.ITRProfileListener;
import com.nut.teamradar.webclient.WebAckCallBack;
import com.nut.teamradar.webclient.WebConnection;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.WindowManager;

public class TeamRadarService extends Service{
	private static final String TAG = "TeamRadarService";
    private static final String[] PHONES_PROJECTION = new String[] {  
        Phone.DISPLAY_NAME, Phone.NUMBER }; 
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;  
    private static final int PHONES_NUMBER_INDEX = 1; 
	private static final int PULL_MESSAGE = 1;
	private static final int PUSH_MESSAGE = 2;
	private static final int GET_LOCATIONS  = 1;
	private static final int GET_RENDEZVOUS  = 2;
	private static final String BACK_MESSAGE  = "TRBackToApp";
	private final int REQUEST_CODE = 0xb01;
	private HttpConnection mConnection = null;
	private WebConnection mTRConnection = null;
	private TeanRadarBinder mBinder = new TeanRadarBinder();
	private List<ITRConnectionListener> mConnectionListener = null;
	private List<ITRContactListener> mContactListener = null;
	private List<ITRGroupListener> mGroupListener = null;
	private List<ITRHistoryListener> mHistoryListener = null;
	private List<ITRLocationListener> mLocationListener = null;
	private List<ITRMessageListener> mMessageListener = null;
	private List<ITRProfileListener> mProfileListener = null;
	private List<GroupMembers> mAllMembers = null;
	private int CurrentMemberIndex = 0;
	private List<Group> mGroups=null;
	private List<UserLocations> mMemberLocations;
	private ArrayList<Contact> mContacts = null;  
	private ArrayList<Contact> mPhoneContacts = null;
	private ArrayList<ActivityHistory> mActivityHistory = null;
	private Thread mMessageReadThread;
	private List<ShortMessage> mCommandList;
	private List<ShortMessage> mCommandToSend;
	private List<ShortMessage> mAckList;
	private Thread mLocationReadThread;
	private NotificationCompat.Builder mBuilder;
	private Notification mNotification;
	private NotificationManager mNotifyManager;
	private boolean LoginSuccess = false;
	UpdateManager manager = null;
	TeamRadarService mThis;
	private ProgressDialog updateProgressDialog;
	private Object Locker = new Object();
	
	
	private Handler mWebConnectionHandler = new Handler(){
		
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			switch(msg.what)
			{
			case 0:
				break;
			}
		}
	};
	private Messenger mMessenger = new Messenger(mWebConnectionHandler);
	class WebConnectionThread extends Thread{
		public void run(){
			Looper.prepare();
			Handler handler = new WebConnectionHandler(Looper.myLooper());
			
			
		}
	}
	
	class WebConnectionHandler extends Handler{
		public WebConnectionHandler(Looper looper)
		{
			super(looper);
		}
		public void handleMessage(Message msg)
		{
			
		}
	}
	public void onCreate()
	{
		super.onCreate();
		ApplicationData.getInstance().setContext(getApplicationContext());
		mConnection = new HttpConnection(this.getApplicationContext());
		mTRConnection = new WebConnection(mConnection);
		mConnectionListener = new ArrayList<ITRConnectionListener>();
		mContactListener = new ArrayList<ITRContactListener>();
		mGroupListener = new ArrayList<ITRGroupListener>();
		mHistoryListener = new ArrayList<ITRHistoryListener>();
		mLocationListener = new ArrayList<ITRLocationListener>();
		mMessageListener = new ArrayList<ITRMessageListener>();
		mProfileListener = new ArrayList<ITRProfileListener>();
		mAllMembers = new ArrayList<GroupMembers>();
		mGroups = new ArrayList<Group>();
		mMemberLocations = new ArrayList<UserLocations>();
		mContacts = new ArrayList<Contact>();
		mPhoneContacts = new ArrayList<Contact>();
		mActivityHistory = new ArrayList<ActivityHistory>();
		getPhoneContacts();
		getSIMContacts();
		mCommandList = new ArrayList<ShortMessage>();
		mCommandToSend = new ArrayList<ShortMessage>();
		mAckList = new ArrayList<ShortMessage>();
		mMessageReadThread = new Thread(MessageRunnable);
		mMessageReadThread.start();
		mLocationReadThread = new Thread(LocationRunnable);
		mLocationReadThread.start();
		mThis = this;
		manager = new UpdateManager(this,appUpdateCb);
		
	}
	
	
	// 自动更新回调函数
		UpdateManager.UpdateCallback appUpdateCb = new UpdateManager.UpdateCallback() 
		{
			public void downloadProgressChanged(int progress) {
				if (updateProgressDialog != null
						&& updateProgressDialog.isShowing()) {
					updateProgressDialog.setProgress(progress);
				}

			}

			public void downloadCompleted(Boolean sucess, CharSequence errorMsg) {
				if (updateProgressDialog != null
						&& updateProgressDialog.isShowing()) {
					updateProgressDialog.dismiss();
				}
				if (sucess) {
					manager.update();
				} else {
					DialogHelper.Confirm(mThis,
							mThis.getString(R.string.dialog_error_title),
							mThis.getString(R.string.dialog_downfailed_msg),
							mThis.getString(R.string.dialog_downfailed_btndown),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									manager.downloadPackage();

								}
							}, mThis.getString(R.string.dialog_update_btnnext),
							new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										manager.cancelDownload();

									}
								});
				}
			}

			public void downloadCanceled() 
			{


			}

			public void checkUpdateCompleted(Boolean hasUpdate,
					CharSequence updateInfo) {
				if (hasUpdate) {
					
					DialogHelper.Confirm(mThis,
							mThis.getString(R.string.dialog_update_title),
							mThis.getString(R.string.dialog_update_msg)
							+updateInfo+
							mThis.getString(R.string.dialog_update_msg2),
							mThis.getString(R.string.dialog_update_btnupdate),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									updateProgressDialog = new ProgressDialog(mThis);
									updateProgressDialog
											.setMessage(mThis.getString(R.string.dialog_downloading_msg));
									updateProgressDialog.setIndeterminate(false);
									updateProgressDialog
											.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
									updateProgressDialog.setMax(100);
									updateProgressDialog.setProgress(0);
									updateProgressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
									updateProgressDialog.show();

									manager.downloadPackage();
								}
							},mThis.getString( R.string.dialog_update_btnnext), 
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									//manager.cancelDownload();

								}
							});
				}

			}
		};
	
	
	public boolean ActivityOnTop()
	{
		 ActivityManager activityManager = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
		 List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1); 
		    if (tasksInfo.size() > 0) { 
		        Log.d(TAG,"top Activity = " 
		                + tasksInfo.get(0).topActivity.getPackageName()); 
		        // 应用程序位于堆栈的顶层 
		        if (tasksInfo.get(0).topActivity 
		                .getPackageName().equals("com.nut.teamradar")) { 
		            return true; 
		        } 
		    } 
		    return false; 
	}
	
	private BroadcastReceiver onClickReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			
			if (intent.getAction().equals(BACK_MESSAGE)) 
			{
				ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE); 
				List<RunningTaskInfo> list = manager.getRunningTasks(100);                
				String MY_PKG_NAME = "com.nut.teamradar";
				int i=0;
				for (RunningTaskInfo info : list) {
					if (info.topActivity.getPackageName().equals(MY_PKG_NAME) || info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
					    //Log.e(TAG,info.topActivity.getPackageName() + " info.baseActivity.getPackageName()="+info.baseActivity.getPackageName() + ":" + info.topActivity.getClassName());
						break;
					}
					i++;
				}
				RunningTaskInfo info=list.get(i);                 
				String className = info.topActivity.getClassName();
				
				Intent notifyIntent;
				try {
					notifyIntent = new Intent(mThis, Class.forName(className));
				} catch (ClassNotFoundException e) {
					notifyIntent = new Intent(mThis, MainActivity.class);
					e.printStackTrace();
				}
				notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				mThis.startActivity(notifyIntent);
		    }
		}
	};
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		
		/*Notification notification = new Notification();  
		notification.flags = Notification.FLAG_ONGOING_EVENT;  
		notification.flags |= Notification.FLAG_NO_CLEAR;  
		notification.flags |= Notification.FLAG_FOREGROUND_SERVICE; */ 
		if(intent == null)
		{
			flags = START_STICKY; 
			super.onStartCommand(intent, flags, startId);
			super.stopSelf();
			return START_STICKY;
		}
		String AppName = intent.getStringExtra("AppName");
		if(!ActivityOnTop() || (AppName==null) || (!AppName.equals("TeamRadarApplication")))
		{
			Log.d(TAG, "~~~~~~~~~~~~~onStartCommand error!");
			flags = START_STICKY; 
			super.onStartCommand(intent, flags, startId);
			super.stopSelf();
			return START_STICKY;
		}
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(BACK_MESSAGE);
		
		this.registerReceiver(onClickReceiver, filter);
		
		Intent notifyIntent = new Intent(BACK_MESSAGE);
		
        PendingIntent pendIntent = PendingIntent.getBroadcast(this, 0,
        		notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		
		mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setSmallIcon(R.drawable.logo);
		mBuilder.setContentTitle(getResources().getString(R.string.app_name));
		mBuilder.setTicker(getResources().getString(R.string.app_name));
		mBuilder.setContentText("");
		mBuilder.setContentIntent(pendIntent);
		
		mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotification = mBuilder.build();
		mNotification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT | Notification.FLAG_FOREGROUND_SERVICE;;


		
        //startForeground(0, mNotification); // ID=0 mean no notification bar show on top of the screan.
		startForeground(0x12345, mNotification);
		flags = START_STICKY;  
        return super.onStartCommand(intent, flags, startId);
    }
	private Handler mMessageHander = new Handler()
	 {
		 public void handleMessage(Message msg) {   
            switch (msg.what) {   
                 case PULL_MESSAGE: 
                	 if(LoginSuccess)
                	 {
						try {
							mAidlBinder.PullMessage();
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	 }
                     break;   
                 case PUSH_MESSAGE:
                	 if(LoginSuccess)
                	 {
						try {
							mAidlBinder.PushMessage(mCommandToSend.get(0));
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	               	  	Log.d(TAG, ">>>>>>>>>>>>Send A Message :"+mCommandToSend.get(0).getMessage());
	 					  mCommandToSend.remove(0);
                	 }
 					  
               	  break;
            }   
            super.handleMessage(msg);   
       }  	 
	 };

	 private Runnable MessageRunnable = new Runnable() {
		@Override
       public void run() {
			while(true)
			{
				if(LoginSuccess)
				{
					if(mCommandToSend.size() > 0)
					{
	                   Message message = new Message();   
	                   message.what = PUSH_MESSAGE;
	                   mMessageHander.obtainMessage(PUSH_MESSAGE).sendToTarget();					
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					else
					{
	                   Message message = new Message();   
	                   message.what = PULL_MESSAGE;
	                   mMessageHander.obtainMessage(PULL_MESSAGE).sendToTarget();
						
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
       }	 
	 };
	 private Runnable LocationRunnable = new Runnable() {
			private int Counter=0;
			@Override
	       public void run() {
				long start,end;
				while(true)
				{
					start = System.currentTimeMillis();
					synchronized(Locker)
					{
						
						try {
							
							Locker.wait(5500);
							if(ApplicationData.getInstance().getActiveGroupId() == -1 )
							{
								Counter=0;
								continue;
							}
							
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if(Counter % 6 == 0)
					{
						mLocationHander.obtainMessage(GET_RENDEZVOUS).sendToTarget();
					}
					mLocationHander.obtainMessage(GET_LOCATIONS).sendToTarget();
					Counter++;
					end = System.currentTimeMillis();
					System.out.println(String.format("~~~~~wait time out :%d", (end-start)/1000));
				}
	       }	 
		 };

		 private Handler mLocationHander = new Handler()
		 {
			 public void handleMessage(Message msg) {   
	             switch (msg.what) { 
	             case GET_LOCATIONS:
	            	 if(ApplicationData.getInstance().getActiveGroupId() != -1)
	            	 {
	            		 try {
							mAidlBinder.DownloadAllLocations(ApplicationData.getInstance().getActiveGroupId());
						} catch (RemoteException e) {
							e.printStackTrace();
						}
	            	 }
	                 break;
	             case GET_RENDEZVOUS:
	            	 if(ApplicationData.getInstance().getActiveGroupId() != -1)
	            	 {
	            		 try {
							mAidlBinder.DownloadRandezvous(ApplicationData.getInstance().getActiveGroupId());
						} catch (RemoteException e) {
							e.printStackTrace();
						}
	            	 }
	            	 break;
	             }
	             super.handleMessage(msg);   
	        }  	 
		 };
	private void getPhoneContacts() {  
        ContentResolver resolver = this.getContentResolver();  
        
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);  
     
     
        if (phoneCursor != null) {  
            while (phoneCursor.moveToNext()) {  
      
	            String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);  
	 
	            if (TextUtils.isEmpty(phoneNumber))  
	                continue;  
	 
	            String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);  
	            ApplicationData.getInstance().putContact(contactName, phoneNumber);
            }  
     
            phoneCursor.close();  
        }  
    }  
    private void getSIMContacts() {  
        ContentResolver resolver = this.getContentResolver();  
        Uri uri = Uri.parse("content://icc/adn");  
        Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,  
            null);  
     
        if (phoneCursor != null) {  
            while (phoneCursor.moveToNext()) {  
      
	            String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);   
	            if (TextUtils.isEmpty(phoneNumber))  
	                continue;   
	            String contactName = phoneCursor 
	                .getString(PHONES_DISPLAY_NAME_INDEX);   
	              
	            ApplicationData.getInstance().putContact(contactName, phoneNumber);
            }  
     
            phoneCursor.close();  
        }  
    } 

	public void reloadMembers(List<Member> members)
	{
		boolean flag=false;
		long GroupId =0;
		if(members.size() > 0)
		{
			GroupId = members.get(0).getActivityId();
			Iterator<GroupMembers> it = mAllMembers.iterator();
			GroupMembers mb=null;
			while(it.hasNext())
			{
				mb = it.next();
				if(mb.GetGroupId() == GroupId)
				{
					flag = true;
					CurrentMemberIndex = mAllMembers.indexOf(mb);
					break;
				}
			}
			if(flag == false)
			{
				mb = new GroupMembers(GroupId);
				mAllMembers.add(mb);
				CurrentMemberIndex = mAllMembers.size()-1;
			}
			mb.ClearAllMembers();
			mb.AddAllMembers(members);
		}
	}
	public void reloadGroups(List<Group> groups)
	{
		mGroups.removeAll(mGroups);
		mGroups.addAll(groups);
	}

	public Group getGroup(int index)
	{
		return mGroups.get(index);
	}
	public int getGroupMemberNumber(long GroupId)
	{
		Iterator<GroupMembers> it = mAllMembers.iterator();
		GroupMembers mb=null;
		while(it.hasNext())
		{
			mb = it.next();
			if(mb.GetGroupId() == GroupId)
			{
				return mb.getSize();
			}
		}
		return 0;
	}
	public Group getGroupByNameAndSub(String Name,String Sub)
	{
		for(int i=0;i<mGroups.size();i++)
		{
			if(mGroups.get(i).getName().equals(Name) && mGroups.get(i).getSubscription().equals(Sub))
			{
				return mGroups.get(i);
			}
		}
		return null;
	}
	public Member getMemberBySub(String Sub)
	{
		for(int i=0;i<mAllMembers.size();i++)
		{
			GroupMembers mbs = mAllMembers.get(i);
			for(int j=0;j<mbs.getSize();j++)
			{
				Member mb = mbs.GetMember(j);
				if(mb.getSubscription().equals(Sub))
				{
					return mb;
				}
			}
		}
		return null;
	}

	public GroupMembers getGroupMembers(long GroupId)
	{
		Iterator<GroupMembers> it = mAllMembers.iterator();
		GroupMembers mb=null;
		while(it.hasNext())
		{
			mb = it.next();
			if(mb.GetGroupId() == GroupId)
			{
				return mb;
			}
		}
		return null;
	}
	public Member getMember(int GroupIndex,int MemberIndex)
	{
		for(int i=0;i<mAllMembers.size();i++)
		{
			if(mAllMembers.get(i).GetGroupId() == mGroups.get(GroupIndex).getId())
			{
				return mAllMembers.get(i).GetMember(MemberIndex);
			}
		}
		return null;
	}
	public String getUserName(long GroupId,long UserId)
	{
		boolean flag=false;
		Iterator<GroupMembers> it = mAllMembers.iterator();
		GroupMembers mb=null;
		while(it.hasNext())
		{
			mb = it.next();
			if(mb.GetGroupId() == GroupId)
			{
				flag = true;
				break;
			}
		}
		if(flag == true)
		{
			if (mb.GetAMember(UserId) != null)
				return mb.GetAMember(UserId).getUsername();	
		}
		return null;
	}
    private WebAckCallBack<Integer,String> login_cb = new WebAckCallBack<Integer,String>(){

		@Override
		public void eventCallBack(int flag, Integer ID, String Name) {
			if(flag == WebConnection.SUCCESS)
			{
				LoginSuccess = true;
				ApplicationData.getInstance().setCurrentUserId(ID);
				ApplicationData.getInstance().setName(Name);
			}
			Iterator<ITRConnectionListener> it = mConnectionListener.iterator();
			while(it.hasNext())
			{
				ITRConnectionListener Listener = it.next();
				try {
					Listener.OnLogIn(flag, ID, Name);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	};
	WebAckCallBack<User,Integer> getProfile_cb = new WebAckCallBack<User,Integer> (){

		@Override
		public void eventCallBack(int flag, User usr, Integer arg1) {
			if(flag == WebConnection.SUCCESS)
			{
				ApplicationData.getInstance().putUserProfile(usr);
			}
			Iterator<ITRProfileListener> it = mProfileListener.iterator();
			while(it.hasNext())
			{
				ITRProfileListener Listener = it.next();
				try {
					Listener.OnObtainProfile(usr);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}			
		}
    };
	WebAckCallBack<Integer,Integer> updateProfile_cb = new WebAckCallBack<Integer,Integer> (){

		@Override
		public void eventCallBack(int flag, Integer arg0, Integer arg1) {
			Iterator<ITRProfileListener> it = mProfileListener.iterator();
			while(it.hasNext())
			{
				ITRProfileListener Listener = it.next();
				try {
					Listener.OnProfileUpdate(flag);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
    };
    WebAckCallBack<List<Member>,Integer> getMembers_cb = new WebAckCallBack<List<Member>,Integer> (){

		@Override
		public void eventCallBack(int flag, List<Member> members, Integer arg1) {
			if(flag == WebConnection.SUCCESS)
			{
				reloadMembers(members);
			}
			Iterator<ITRGroupListener> it = mGroupListener.iterator();
			while(it.hasNext())
			{
				ITRGroupListener Listener = it.next();
				try {
					Listener.OnMemberUpdate(flag,members);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
    };
    WebAckCallBack<Integer,Integer> deleteMember_cb = new WebAckCallBack<Integer,Integer> (){

		@Override
		public void eventCallBack(int flag, Integer arg0, Integer arg1) {
			Iterator<ITRGroupListener> it = mGroupListener.iterator();
			while(it.hasNext())
			{
				ITRGroupListener Listener = it.next();
				try {
					Listener.OnDeleteMember(flag);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
    };
    WebAckCallBack<Integer,Integer> deleteActivity_cb = new WebAckCallBack<Integer,Integer> (){

		@Override
		public void eventCallBack(int flag, Integer arg0, Integer arg1) {
			Iterator<ITRGroupListener> it = mGroupListener.iterator();
			while(it.hasNext())
			{
				ITRGroupListener Listener = it.next();
				try {
					Listener.OnDeleteActivity(flag);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
    };
    WebAckCallBack<Integer,Integer> createAcitivity_cb = new WebAckCallBack<Integer,Integer>(){

		@Override
		public void eventCallBack(int flag, Integer arg0, Integer arg1) {
			Iterator<ITRGroupListener> it = mGroupListener.iterator();
			while(it.hasNext())
			{
				ITRGroupListener Listener = it.next();
				try {
					Listener.OnCreateActivity(flag);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
    };
    WebAckCallBack<Integer, Integer> AddMemberCallBack = new WebAckCallBack<Integer, Integer>(){

		@Override
		public void eventCallBack(int flag, Integer arg0, Integer arg1) {
			Iterator<ITRGroupListener> it = mGroupListener.iterator();
			while(it.hasNext())
			{
				ITRGroupListener Listener = it.next();
				try {
					Listener.OnAddUserToActivity(flag);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
    	
    };
    private WebAckCallBack<List<Group>,Integer> getActivities_cb = new WebAckCallBack<List<Group>,Integer> (){

		@Override
		public void eventCallBack(int flag, List<Group> groups, Integer arg1) {
			if(flag == WebConnection.SUCCESS)
			{
				reloadGroups(groups);
				for(int i=0;i<groups.size();i++)
				{
					mTRConnection.doGetCurrentActivityMembers(groups.get(i).getId(),getMembers_cb);
				}
			}
			Iterator<ITRGroupListener> it = mGroupListener.iterator();
			while(it.hasNext())
			{
				ITRGroupListener Listener = it.next();
				try {
					Listener.OnObtainActivity(flag, groups);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
    };

	WebAckCallBack<Integer,Integer> StartActivity_cb = new WebAckCallBack<Integer,Integer> (){

		@Override
		public void eventCallBack(int flag, Integer arg0, Integer arg1) {
			if(flag == WebConnection.SUCCESS)
			{
				
			}
		}
    };
    WebAckCallBack<List<ActivityHistory>,Integer> getActivityHistory_cb = new WebAckCallBack<List<ActivityHistory>,Integer>()
	{
		@Override
		public void eventCallBack(int flag, List<ActivityHistory> historys,
				Integer arg1) {
			if(flag == WebConnection.SUCCESS)
			{
				mActivityHistory.removeAll(mActivityHistory);
				mActivityHistory.addAll(historys);
			}
			Iterator<ITRHistoryListener> it = mHistoryListener.iterator();
			while(it.hasNext())
			{
				ITRHistoryListener Listener = it.next();
				try {
					Listener.OnGetActivityHistory(flag, historys);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	};
	WebAckCallBack<List<Location>,Integer> getHistoryLocations_cb = new WebAckCallBack<List<Location>,Integer>()
	{
		@Override
		public void eventCallBack(int flag, List<Location> locations, Integer arg1) {
			Iterator<ITRHistoryListener> it = mHistoryListener.iterator();
			while(it.hasNext())
			{
				ITRHistoryListener Listener = it.next();
				try {
					Listener.OnGetHistoryLocations(flag, locations);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		
	};
	 private WebAckCallBack<Integer,Integer> pushMessage_cb = new WebAckCallBack<Integer,Integer> ()
	 {

		@Override
		public void eventCallBack(int flag, Integer arg0, Integer arg1 ){
			Iterator<ITRMessageListener> it = mMessageListener.iterator();
			while(it.hasNext())
			{
				ITRMessageListener Listener = it.next();
				try {
					Listener.OnPushMessage(flag);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	 };
	 private WebAckCallBack<List<ShortMessage>,Integer> pullMessage_cb = new WebAckCallBack<List<ShortMessage>,Integer> ()
	 {

		@Override
		public void eventCallBack(int flag, List<ShortMessage> msgs, Integer arg1) {
			if(flag == WebConnection.SUCCESS)
			{
				mAckList.clear();
		 		for(int i=0;i<msgs.size();i++)
		 		{
			 		if(msgs.get(i).getId()!= -1)
			 		{
			 			// Message Format : GROUPNAME#GROUPID#OWNERID#OPTION
			 			//                  TEMP1#12#OWNERID#REQUEST
			 			//                  TEMP1#12#OWNERID#ACCEPT
			 			//                  TEMP1#12#OWNERID#REJECT
			 			mAckList.add(msgs.get(i));
						Iterator<ITRMessageListener> it = mMessageListener.iterator();
						while(it.hasNext())
						{
							ITRMessageListener Listener = it.next();
							try {
								Listener.OnProcessMessage(flag, msgs.get(i));
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
			 			Log.d(TAG, "Get a command from "+ msgs.get(i).getFromId() + " to " + msgs.get(i).getToId()+" with message: "+msgs.get(i).getMessage() );
			 		}
		 		}
			}
			Iterator<ITRMessageListener> it = mMessageListener.iterator();
			while(it.hasNext())
			{
				ITRMessageListener Listener = it.next();
				try {
					Listener.OnPullMessage(flag, msgs);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	 };	 
	 private WebAckCallBack<List<Location>,Integer> getLocation_cb = new WebAckCallBack<List<Location>,Integer>()
	 {

		@Override
		public void eventCallBack(int flag, List<Location> locations, Integer arg1) {
			if(flag == WebConnection.SUCCESS)
			{
				if(ApplicationData.getInstance().getActiveGroupId() != -1)
				{
					int i,j;
					com.nut.teamradar.model.Location loc;
					for(i=0;i<locations.size();i++)
					{
						loc = locations.get(i);
						for(j=0;j<mMemberLocations.size();j++)
						{
							
							if(loc.getUserid() == mMemberLocations.get(j).getUserid())
							{
								mMemberLocations.get(j).AddALocation(loc);
								break;
							}
						}
						if(j== mMemberLocations.size())
						{
							mMemberLocations.add((new UserLocations()).AddALocation(loc));
						}
					}
				}
			}
			//if(ActivityOnTop())
			{
				Iterator<ITRLocationListener> it = mLocationListener.iterator();
				while(it.hasNext())
				{
					ITRLocationListener Listener = it.next();
					try {
						Listener.OnLocationUpdate(flag, locations);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}	
			}
		}
	 };
	 private WebAckCallBack<Integer,Integer> updateRendezvous_cb  = new WebAckCallBack<Integer,Integer>()
	 {

		@Override
		public void eventCallBack(int flag, Integer arg0, Integer arg1) {
			Iterator<ITRLocationListener> it = mLocationListener.iterator();
			while(it.hasNext())
			{
				ITRLocationListener Listener = it.next();
				try {
					Listener.OnRandezvousUpdate(flag);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
 
	 }; 
	 private WebAckCallBack<MarkerInfo,Integer> getRendezvous_cb = new WebAckCallBack<MarkerInfo,Integer>()
	 {

		@Override
		public void eventCallBack(int flag, MarkerInfo Info, Integer arg1) {
			Iterator<ITRLocationListener> it = mLocationListener.iterator();
			while(it.hasNext())
			{
				ITRLocationListener Listener = it.next();
				try {
					Listener.OnObtainRandezvous(flag, Info);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
 
	 }; 
     private WebAckCallBack<Integer,String> registration_cb = new  WebAckCallBack<Integer,String>(){

			@Override
			public void eventCallBack(int flag, Integer ID, String ackStr) {
				Iterator<ITRConnectionListener> it = mConnectionListener.iterator();
				while(it.hasNext())
				{
					ITRConnectionListener Listener = it.next();
					try {
						Listener.OnRregtistration(flag,ID);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
     	
     };
     private WebAckCallBack<List<Contact>, Integer> getContacts_cb =
             new WebAckCallBack<List<Contact>, Integer>() {
         @Override
         public void eventCallBack(int flag, List<Contact> contacts, Integer arg1) {
        	 if(flag == WebConnection.SUCCESS)
        	 {
        		 mContacts.removeAll(mContacts);
        		 mContacts.addAll(contacts);
        	 }
			Iterator<ITRContactListener> it = mContactListener.iterator();
			while(it.hasNext())
			{
				ITRContactListener Listener = it.next();
				try {
					Listener.OnDownloadContacts(flag, contacts);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} 
         }
     };
     private WebAckCallBack<Integer,Integer> deleteContact_cb = new WebAckCallBack<Integer,Integer>(){

 		@Override
 		public void eventCallBack(int flag, Integer arg0, Integer arg1) {
			Iterator<ITRContactListener> it = mContactListener.iterator();
			while(it.hasNext())
			{
				ITRContactListener Listener = it.next();
				try {
					Listener.OnDeleteContact(flag);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
 		}
     	
     };
     private WebAckCallBack<Integer, Integer> addContact_cb = new WebAckCallBack<Integer, Integer>() {

         @Override
         public void eventCallBack(int flag, Integer arg0, Integer arg1) {
 			Iterator<ITRContactListener> it = mContactListener.iterator();
 			while(it.hasNext())
 			{
 				ITRContactListener Listener = it.next();
 				try {
 					Listener.OnAddContact(flag);
 				} catch (RemoteException e) {
 					e.printStackTrace();
 				}
 			}
         }

     };
     private WebAckCallBack<Integer,Integer> addLocation_cb = new WebAckCallBack<Integer,Integer>(){

 		@Override
 		public void eventCallBack(int flag, Integer arg0, Integer arg1) {
			Iterator<ITRLocationListener> it = mLocationListener.iterator();
			while(it.hasNext())
			{
				ITRLocationListener Listener = it.next();
				try {
					Listener.OnLocationUploaded(flag);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
 		}
     	
     };
     private WebAckCallBack<List<Group>, Integer> getActivitysBySub_cb = new WebAckCallBack<List<Group>, Integer>() {
  		@Override
  		public void eventCallBack(int flag, List<Group> groups, Integer arg1) {
 			Iterator<ITRGroupListener> it = mGroupListener.iterator();
 			while(it.hasNext())
 			{
 				ITRGroupListener Listener = it.next();
 				try {
 					Listener.OnObtainActivityBySubscription(flag, groups);
 				} catch (RemoteException e) {
 					e.printStackTrace();
 				}
 			}
  		}
     };
     private WebAckCallBack<Integer,Integer> deleteMemberFromActivity_cb = new WebAckCallBack<Integer,Integer>(){
    	 @Override
   		public void eventCallBack(int flag, Integer arg0, Integer arg1) {
  			Iterator<ITRGroupListener> it = mGroupListener.iterator();
  			while(it.hasNext())
  			{
  				ITRGroupListener Listener = it.next();
  				try {
  					Listener.OnDeleteMemberFromActivity(flag);
  				} catch (RemoteException e) {
  					e.printStackTrace();
  				}
  			}
   		} 
     };
     private WebAckCallBack<Integer,Integer> createHXUser_cb = new WebAckCallBack<Integer,Integer>(){
    	 @Override
   		public void eventCallBack(int flag, Integer arg0, Integer arg1) {
  			Iterator<ITRGroupListener> it = mGroupListener.iterator();
  			while(it.hasNext())
  			{
  				ITRGroupListener Listener = it.next();
  				try {
  					Listener.OnHXUserCreated(flag);
  				} catch (RemoteException e) {
  					e.printStackTrace();
  				}
  			}
   		} 
     };
     private WebAckCallBack<Integer,Integer> ChangePassword_cb = new WebAckCallBack<Integer,Integer>(){
    	 @Override
   		public void eventCallBack(int flag, Integer arg0, Integer arg1) {
  			Iterator<ITRProfileListener> it = mProfileListener.iterator();
  			while(it.hasNext())
  			{
  				ITRProfileListener Listener = it.next();
  				try {
  					Listener.OnPasswordUpdate(flag);
  				} catch (RemoteException e) {
  					e.printStackTrace();
  				}
  			}
   		} 
     };
     private WebAckCallBack<Integer,Integer> ChangeHXUserPassword_cb = new WebAckCallBack<Integer,Integer>(){
    	 @Override
   		public void eventCallBack(int flag, Integer arg0, Integer arg1) {
  			Iterator<ITRProfileListener> it = mProfileListener.iterator();
  			while(it.hasNext())
  			{
  				ITRProfileListener Listener = it.next();
  				try {
  					Listener.OnHXUserPasswordUpdate(flag);
  				} catch (RemoteException e) {
  					e.printStackTrace();
  				}
  			}
   		} 
     };
	private ITeamRadarService.Stub mAidlBinder  = new ITeamRadarService.Stub() {
		
		@Override
		public void Stop() throws RemoteException {
			LoginSuccess = false;
			mThis.stopSelf();
			Log.d(TAG, "Service Process id = "+android.os.Process.myPid());
		}
		
		@Override
		public void Start() throws RemoteException {
			
		}

		@Override
		public int Login(String UsrName, String PassWord)
				throws RemoteException {
			mConnection.ReconnectHttpClient();
			mTRConnection.doLogin(UsrName, PassWord, login_cb );
			
			return 0;
		}

		@Override
		public int Logout() throws RemoteException {
			return 0;
		}

		@Override
		public String GetHXUserName() throws RemoteException {
			return null;
		}

		@Override
		public String GetHXUserPassword() throws RemoteException {
			return null;
		}
		@Override
		public void RegisterConnectionListener(ITRConnectionListener Listener)
				throws RemoteException {
			mConnectionListener.add(Listener);	
		}

		@Override
		public void RemoveConnectionListener(ITRConnectionListener Listener)
				throws RemoteException {
			mConnectionListener.remove(Listener);
		}

		@Override
		public void RegisterContactListener(ITRContactListener Listener)
				throws RemoteException {
			mContactListener.add(Listener);
			
		}

		@Override
		public void RemoveContactListener(ITRContactListener Listener)
				throws RemoteException {
			mContactListener.remove(Listener);
		}

		@Override
		public void RegisterGroupListener(ITRGroupListener Listener)
				throws RemoteException {
			mGroupListener.add(Listener);
			
		}

		@Override
		public void RemoveGroupListener(ITRGroupListener Listener)
				throws RemoteException {
			mGroupListener.remove(Listener);
		}

		@Override
		public void RegisterHistoryListener(ITRHistoryListener Listener)
				throws RemoteException {
			mHistoryListener.add(Listener);
			
		}

		@Override
		public void RemoveHistoryListener(ITRHistoryListener Listener)
				throws RemoteException {
			mHistoryListener.remove(Listener);
		}

		@Override
		public void RegisterLocationListener(ITRLocationListener Listener)
				throws RemoteException {
			mLocationListener.add(Listener);
		}

		@Override
		public void RemoveLocationListener(ITRLocationListener Listener)
				throws RemoteException {
			mLocationListener.remove(Listener);
		}

		@Override
		public void RegisterMessageListener(ITRMessageListener Listener)
				throws RemoteException {
			mMessageListener.add(Listener);
		}

		@Override
		public void RemoveMessageListener(ITRMessageListener Listener)
				throws RemoteException {
			mMessageListener.remove(Listener);
		}

		@Override
		public void RegisterProfileListener(ITRProfileListener Listener)
				throws RemoteException {
			mProfileListener.add(Listener);
		}

		@Override
		public void RemoveProfileListener(ITRProfileListener Listener)
				throws RemoteException {
			mProfileListener.remove(Listener);
		}
		@Override
		public User DownloadProfile() throws RemoteException {
			mTRConnection.doGetProfile(getProfile_cb);
			return null;
		}

		@Override
		public void DownloadGroupMembers(long GroupId) throws RemoteException {
			mTRConnection.doGetCurrentActivityMembers(GroupId,getMembers_cb);
		}

		@Override
		public void DeleteMember(long UserId) throws RemoteException {
			mTRConnection.doDeleteMember(UserId,deleteMember_cb);
		}

		@Override
		public void DeleteActivity(long GroupId) throws RemoteException {
			mTRConnection.doDeleteActivity(GroupId,deleteActivity_cb);
		}

		@Override
		public void CreateAcitivity(String name, String subscription,
				String Comment) throws RemoteException {
			mTRConnection.doCreateActivity(name,subscription,Comment,createAcitivity_cb);
			
		}

		@Override
		public void AddUserToActivity(long UserId, long GroupId, String username)
				throws RemoteException {
			mTRConnection.doAddUserToActivity(UserId, GroupId,username,AddMemberCallBack);
			
		}

		@Override
		public void DownloadJoinedGroups() throws RemoteException {
			mTRConnection.doGetJoinedActivities(ApplicationData.getInstance().getCurrentUserId(),getActivities_cb);
		}

		@Override
		public void UpdateProfile(String field, String value)
				throws RemoteException {
			if(field.equals(Constant.KEY_USER_NAME))
			{
				mTRConnection.doUpdateUserName(value, updateProfile_cb);
			}
			else if(field.equals(Constant.KEY_EMAIL))
			{
				mTRConnection.doUpdateEmail(value, updateProfile_cb);
			}
			else if(field.equals(Constant.KEY_WEIGHT))
			{
				mTRConnection.doUpdateWeight(value, updateProfile_cb);
			}
			else if(field.equals(Constant.KEY_HEIGHT))
			{
				mTRConnection.doUpdateHeight(value, updateProfile_cb);
			}
			else if(field.equals(Constant.KEY_BIRTHDAY))
			{
				mTRConnection.doUpdateBirthday(value, updateProfile_cb);
			}
			else if(field.equals(Constant.KEY_OCCUPATION))
			{
				mTRConnection.doUpdateOccupation(value, updateProfile_cb);
			}
			else if(field.equals(Constant.KEY_PASSWORD))
			{
				mTRConnection.doUpdatePassword(value, updateProfile_cb);
			}
			else if(field.equals(Constant.KEY_GENDER))
			{
				mTRConnection.doUpdateGender(value, updateProfile_cb);
			}
		}

		@Override
		public void StartActivity(ActivityHistory record)
				throws RemoteException {
			mTRConnection.doStartActivity(record, StartActivity_cb);
			
		}

		@Override
		public void DownloadActivityHistory(long userid, long activityid)
				throws RemoteException {
			mTRConnection.doGetAcivityHistory(userid, activityid, getActivityHistory_cb);
		}

		@Override
		public void DownloadHistoryLocations(long userid, long activityid, String Mark)
				throws RemoteException {
			mTRConnection.doGetHistoryLocations(userid, activityid, Mark, getHistoryLocations_cb);
		}

		@Override
		public void PushMessage(ShortMessage Msg) throws RemoteException {
			mTRConnection.doPushMessage(Msg, pushMessage_cb);
		}

		@Override
		public void PullMessage() throws RemoteException {
			mTRConnection.doPullMessages(pullMessage_cb);
		}

		@Override
		public void DownloadAllLocations(long GroupId) throws RemoteException {
			mTRConnection.doGetAllLocations(GroupId, getLocation_cb);
		}

		@Override
		public void DownloadRandezvous(long GoupId) throws RemoteException {
			mTRConnection.doGetRandezvous(GoupId, getRendezvous_cb);
		}

		@Override
		public void Registration(User usr) throws RemoteException {
			mTRConnection.doRegistration(usr, registration_cb);
		}

		@Override
		public void DownloadContacts(String subscription) throws RemoteException {
			mTRConnection.doGetContacts(subscription, getContacts_cb);
			
		}

		@Override
		public void DeleteContacts(long Id) throws RemoteException {
			mTRConnection.doDeleteContact(Id, deleteContact_cb);
		}

		@Override
		public void UploadContact(Contact contact) throws RemoteException {
			mTRConnection.doAddContact(contact, addContact_cb);
		}

		@Override
		public void ConnectToServer(String addr, int Port,int cetResId)
				throws RemoteException {
			mTRConnection.SetServerAddr(addr);
			mConnection.sethttpsport(Port);
			mConnection.setcertfileid(cetResId);
			mConnection.ReconnectHttpClient();
			
		}

		@Override
		public void UpdateRandezvous(MarkerInfo Info) throws RemoteException {
			mTRConnection.doUpdateRendezvous(Info, updateRendezvous_cb);
			
		}

		@Override
		public void UploadLocation(Location Loc) throws RemoteException {
			mTRConnection.doAddLocation(Loc, addLocation_cb);
			
		}

		@Override
		public List<Group> GetGroups() throws RemoteException {
			return mGroups;
		}

		@Override
		public List<Member> GetMembers(long GroupId) throws RemoteException {
			GroupMembers GropMember = getGroupMembers(GroupId);
			if(GropMember != null)
			{
				return GropMember.getAllMembers();
			}
			return null;
		}

		@Override
		public List<ActivityHistory> GetActivityHistory()
				throws RemoteException {
			return mActivityHistory;
		}

		@Override
		public void DownloadActivitysBySubscription(String subscription)
				throws RemoteException {
			mTRConnection.doGetActivitiesBySubscription(subscription, getActivitysBySub_cb);
			
		}

		@Override
		public Member GetMember(int GroupIndex, int MemberIndex)
				throws RemoteException {
			return getMember(GroupIndex,MemberIndex);
		}

		@Override
		public Group GetGroup(int index) throws RemoteException {
			return getGroup(index);
		}

		@Override
		public void DeleteMemberFromActivity(long GroupId, long UserId)
				throws RemoteException {
			mTRConnection.doDeleteAMemberFromActivity(UserId, GroupId, deleteMemberFromActivity_cb);
			
		}

		@Override
		public Group GetGroupByNameAndSub(String Name, String Sub)
				throws RemoteException {
			return getGroupByNameAndSub(Name,Sub);
		}

		@Override
		public Member GetMemberBySub(String Sub) throws RemoteException {
			return getMemberBySub(Sub);
		}

		@Override
		public List<com.nut.teamradar.model.Location> GetLocations(long GroupId,
				long UserId) throws RemoteException {
			for(int i=0;i<mMemberLocations.size();i++)
			{
				if(mMemberLocations.get(i).getGroupid() == GroupId && 
						mMemberLocations.get(i).getUserid() == UserId)
				{
					return mMemberLocations.get(i).getLocations();
				}
			}
			return null;
		}

		@Override
		public void SendMessage(ShortMessage msg) throws RemoteException {
			 mCommandList.add(msg);
			 mCommandToSend.add(msg);
		}

		@Override
		public String GetUserName(long GroupId, long UserId)
				throws RemoteException {
			return getUserName(GroupId, UserId);
		}

		@Override
		public void PutUserProfile(User user) throws RemoteException {
			ApplicationData.getInstance().putUserProfile(user);
			
		}

		@Override
		public User GetUserProfile() throws RemoteException {
			return ApplicationData.getInstance().getUserProfile();
		}

		@Override
		public void SetScreenInfo(int width, int height, int sBarHeight)
				throws RemoteException {
			ApplicationData.getInstance().setScreenInfo(width, height, sBarHeight);
			
		}

		@Override
		public ScreenInfo GetScreenInfo() throws RemoteException {
			return ApplicationData.getInstance().getScreenInfo();
		}

		@Override
		public void PutContact(String name, String subscription)
				throws RemoteException {
			ApplicationData.getInstance().putContact(name, subscription);
			
		}

		@Override
		public void DeleteContact(String subscription) throws RemoteException {
			ApplicationData.getInstance().deleteContact(subscription);
			
		}

		@Override
		public List<PhoneContact> GetContacts() throws RemoteException {
			return ApplicationData.getInstance().getContacts();
		}

		@Override
		public void SetSessionId(String id) throws RemoteException {
			ApplicationData.getInstance().setSessionId(id);
			
		}

		@Override
		public String GetSessionId() throws RemoteException {
			return ApplicationData.getInstance().getSessionId();
		}

		@Override
		public void SetMark(String mark) throws RemoteException {
			ApplicationData.getInstance().setMark(mark);
			
		}

		@Override
		public String GetMark() throws RemoteException {
			return ApplicationData.getInstance().getMark();
		}

		@Override
		public void SetName(String name) throws RemoteException {
			ApplicationData.getInstance().setName(name);
			
		}

		@Override
		public String GetName() throws RemoteException {
			return ApplicationData.getInstance().getName();
		}

		@Override
		public void SetCurrentUserId(int userid) throws RemoteException {
			ApplicationData.getInstance().setCurrentUserId(userid);
			
		}

		@Override
		public int GetCurrentUserId() throws RemoteException {
			return ApplicationData.getInstance().getCurrentUserId();
		}

		@Override
		public void SetSubscription(String sub) throws RemoteException {
			ApplicationData.getInstance().setSubscription(sub);
			
		}

		@Override
		public String GetSubscription() throws RemoteException {
			return ApplicationData.getInstance().getSubscription();
		}

		@Override
		public void SaveUserName(String name) throws RemoteException {
			ApplicationData.getInstance().saveUserName(name);
			
		}

		@Override
		public String ReadUserName() throws RemoteException {
			return ApplicationData.getInstance().readUserName();
		}

		@Override
		public void SaveSubscription(String sub) throws RemoteException {
			ApplicationData.getInstance().saveSubscription(sub);
			
		}

		@Override
		public String ReadSubscription() throws RemoteException {
			return ApplicationData.getInstance().readSubscription();
		}

		@Override
		public void SavePassword(String passwd) throws RemoteException {
			ApplicationData.getInstance().savePassword(passwd);
			
		}

		@Override
		public void SetUserMode(int Mode) throws RemoteException {
			ApplicationData.getInstance().setUserMode(Mode);
			
		}

		@Override
		public int GetUserMode() throws RemoteException {
			return ApplicationData.getInstance().getUserMode();
		}

		@Override
		public void SetActiveOwnerId(long Id) throws RemoteException {
			ApplicationData.getInstance().setActiveOwnerId(Id);
			
		}

		@Override
		public long GetActiveOwnerId() throws RemoteException {
			return ApplicationData.getInstance().getActiveOwnerId();
		}

		@Override
		public void SetActiveGroupId(long Id) throws RemoteException {
			ApplicationData.getInstance().setActiveGroupId(Id);
			
		}

		@Override
		public long GetActiveGroupId() throws RemoteException {
			return ApplicationData.getInstance().getActiveGroupId();
		}

		@Override
		public void BeginWriteLocationRecord() throws RemoteException {
			ApplicationData.getInstance().beginWriteLocationRecord();
			
		}

		@Override
		public void WriteLocationRecord(String sub, String name,
				com.nut.teamradar.model.Location loc) throws RemoteException {
			ApplicationData.getInstance().writeLocationRecord(sub, name, loc);
			
		}

		@Override
		public void CommitAllLocationRecords() throws RemoteException {
			ApplicationData.getInstance().commitAllLocationRecords();
			
		}

		@Override
		public void SetActiveGroupName(String name) throws RemoteException {
			ApplicationData.getInstance().setActiveGroupName(name);
			
		}

		@Override
		public String getActiveGroupName() throws RemoteException {
			return ApplicationData.getInstance().getActiveGroupName();
		}

		@Override
		public void setActiveGroupSubscription(String sub)
				throws RemoteException {
			ApplicationData.getInstance().setActiveGroupSubscription(sub);
			
		}

		@Override
		public String getActiveGroupSubscription() throws RemoteException {
			return ApplicationData.getInstance().getActiveGroupSubscription();
		}

		@Override
		public String ReadPassword() throws RemoteException {
			return ApplicationData.getInstance().readPassword();
		}

		@Override
		public void RegisterHXUser(String username, String Password)
				throws RemoteException {
			mTRConnection.doCreateHXUser(username, Password, createHXUser_cb);
			
		}

		@Override
		public void ChangePassword(String sub, String password)
				throws RemoteException {
			mTRConnection.doChangePassword(sub, password, ChangePassword_cb);
			
		}

		@Override
		public void ChangeHXUserPassword(String sub, String password)
				throws RemoteException {
			mTRConnection.doChangeHXUserPassword(sub, password, ChangeHXUserPassword_cb);
			
		}

		@Override
		public void CheckForUpdate() throws RemoteException {
			manager.checkUpdate();
		}

		@Override
		public boolean isloginSuccess() throws RemoteException {
			return LoginSuccess;
		}
		
	};
	@Override
	public IBinder onBind(Intent intent) {
		String AppName = intent.getStringExtra("AppName");
		if(!ActivityOnTop()  || (AppName==null) || (!AppName.equals("TeamRadarApplication")))
		{
			Log.d(TAG, "~~~~~~~~~~~~~onBind error!");
			super.stopSelf();
		}
		
		return mAidlBinder;
	}
	public class TeanRadarBinder extends Binder{
		
		TeamRadarService getService()
		{
			return TeamRadarService.this;
		}

	}
	@Override
    public boolean onUnbind(Intent intent) {
		LoginSuccess = false;
		stopForeground(true); 
		Log.d(TAG, "Service Process id = "+android.os.Process.myPid());
		android.os.Process.killProcess(android.os.Process.myPid());
		super.stopSelf();
        return super.onUnbind(intent);
    }
	@Override
	public void onDestroy() {
		Log.d(TAG, "Service Process id = "+android.os.Process.myPid());
		android.os.Process.killProcess(android.os.Process.myPid());
		super.stopSelf();
		super.onDestroy();
	}
}
