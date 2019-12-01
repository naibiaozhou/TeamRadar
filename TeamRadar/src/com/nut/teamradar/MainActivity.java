package com.nut.teamradar;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.smssdk.SMSSDK;

import com.amap.api.maps.MapsInitializer;
import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.EMValueCallBack;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.EMNotifier;
import com.easemob.chat.GroupChangeListener;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.easemob.util.NetUtils;
import com.nut.teamradar.Fragments.FragmentActivityManagment;
import com.nut.teamradar.Fragments.FragmentChat;
import com.nut.teamradar.Fragments.FragmentContacts;
import com.nut.teamradar.Fragments.FragmentMap;
import com.nut.teamradar.base.BaseUi;
import com.nut.teamradar.controller.HXSDKHelper;
import com.nut.teamradar.db.InviteMessgeDao;
import com.nut.teamradar.db.UserDao;
import com.nut.teamradar.domain.HXUser;
import com.nut.teamradar.domain.InviteMessage;
import com.nut.teamradar.domain.InviteMessage.InviteMesageStatus;
import com.nut.teamradar.model.ActivityHistory;
import com.nut.teamradar.model.ScreenInfo;
import com.nut.teamradar.model.ShortMessage;
import com.nut.teamradar.util.CommonUtils;
import com.nut.teamradar.util.Encrypt;
import com.nut.teamradar.util.OffLineMapUtils;
import com.nut.teamradar.util.PushNotification;
import com.nut.teamradar.util.RandomString;
import com.nut.teamradar.util.StretchAnimation;
import com.nut.teamradar.util.UserUtils;
import com.nut.teamradar.webclient.ITRMessageListener;
import com.nut.teamradar.webclient.WebConnection;
import com.nut.teamradarlib.TeamRadarAPI;


public class MainActivity extends BaseActivity implements
        StretchAnimation.AnimationListener, OnClickListener,EMEventListener {

    private static final String TAG = "MainActivity";
    private static final String[] PHONES_PROJECTION = new String[] {  
        Phone.DISPLAY_NAME, Phone.NUMBER }; 
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;  
    private static final int PHONES_NUMBER_INDEX = 1; 
    
    private ArrayList<BaseUi> fragmentList=null;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Intent mService;
    private MainActivity mThis;   
    private int currentIndex = 0;
    private Stack<Integer> mPageIndexStack = new Stack<Integer>();
    private int backCounter = 0;
    private TextView mHome, mActivity, mContact,
    	mChatMessages;
    private FrameLayout naviFrame;
    private FrameLayout mainBottom;
    private LinearLayout mavContent;
    private StretchAnimation stretchanimation;
    private boolean naviFrameUp = false;
    public FragmentActivityManagment ActivityManagerFrame = null;
    public FragmentMap MapFregment = null;
    public FragmentChat ChatFregment = null;
    private AlertDialog OperationDlg=null;
    private InputMethodManager mImm;
	// 账号在别处登录
	public boolean isConflict = false;
	//账号被移除
	private boolean isCurrentAccountRemoved = false;
	private MyConnectionListener connectionListener = null;
	private MyGroupChangeListener groupChangeListener = null;
	private InviteMessgeDao inviteMessgeDao;
	private UserDao userDao;
	/**
	 * 检查当前用户是否被删除
	 */
	public boolean getCurrentAccountRemoved(){
		return isCurrentAccountRemoved;
	}
	
	 public int getStatusBarHeight() {
	        int statusBarHeight = 0;

	        if (!hasOnScreenSystemBar()) {
	            int resourceId = getResources().getIdentifier("status_bar_height",
	                    "dimen", "android");
	            if (resourceId > 0) {
	                statusBarHeight = getResources().getDimensionPixelSize(
	                        resourceId);
	            }
	        }
	        Class<?> c = null;
	        Object obj = null;
	        Field field = null;
	        int x = 0;
	        try {
	            c = Class.forName("com.android.internal.R$dimen");
	            obj = c.newInstance();
	            field = c.getField("status_bar_height");
	            x = Integer.parseInt(field.get(obj).toString());
	            statusBarHeight = getResources().getDimensionPixelSize(x);
	        } catch (Exception e1) {
	            e1.printStackTrace();
	        }
	        return statusBarHeight;
	    }

	    private boolean hasOnScreenSystemBar() {
	        DisplayMetrics displayMetrics = new DisplayMetrics();
	        Display display = getWindowManager().getDefaultDisplay();

	        display.getRealMetrics(displayMetrics);
	        int rawDisplayHeight = displayMetrics.heightPixels;

	        display.getMetrics(displayMetrics);
	        int UIRequestedHeight = displayMetrics.heightPixels;

	        return rawDisplayHeight - UIRequestedHeight > 0;
	    }	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TeamRadarAPI.getInstance().Init(Constant.magic, Constant.magic_len);
        MapsInitializer.sdcardDir = OffLineMapUtils.getSdCacheDir(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        mThis = this;
        TeamRadarApplication.getInstance().addActivity(this);
        Log.d(TAG, "MainActivity onCreate");
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        int barheight = getStatusBarHeight();
        mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        TRServiceConnection.getInstance().SetScreenInfo(dm.widthPixels, dm.heightPixels, barheight);     
        //if(savedInstanceState == null)
        {
	        PushNotification.getInstance().SetContainerActivity(mThis);
	        getPhoneContacts();
	        getSIMContacts();
	        
			inviteMessgeDao = new InviteMessgeDao(this);
			userDao = new UserDao(this);

			TRServiceConnection.getInstance().RegisterMessageListener(mMessageListener);
			if(!TRServiceConnection.getInstance().isLoginSuccess())
			{
				this.finish();
				return;
			}
	        fragmentManager = getSupportFragmentManager();
	        fragmentTransaction = fragmentManager.beginTransaction();
	        fragmentList = new ArrayList<BaseUi>();
	        MapFregment = (FragmentMap)fragmentManager
	                .findFragmentById(R.id.fragmentMain);
	        fragmentList.add((BaseUi) MapFregment);
	        ActivityManagerFrame = (FragmentActivityManagment) fragmentManager
	                .findFragmentById(R.id.fragmentActivity);
	        fragmentList.add((BaseUi) ActivityManagerFrame);
	        fragmentList.add((BaseUi) fragmentManager
	                .findFragmentById(R.id.fragmentContacts));
	        ChatFregment = (FragmentChat)fragmentManager
	                .findFragmentById(R.id.fragmentProfile);
	        fragmentList.add((BaseUi)ChatFregment);
	        fragmentTransaction.commit();
	
	        switchPages(0);
	
	        naviFrame = (FrameLayout) this.findViewById(R.id.bottom_container);
	        mainBottom = (FrameLayout) this.findViewById(R.id.framefakebottom);
	        mavContent = (LinearLayout) this.findViewById(R.id.verticalcontainer);
	
	        mainBottom.setBackgroundColor(Color.GRAY);
	
	        LayoutParams params = naviFrame.getLayoutParams();
	        ScreenInfo lScreenInfo = TRServiceConnection.getInstance().GetScreenInfo();
	        params.height = (lScreenInfo.height - lScreenInfo.statusBarHeight) / 9;
	        naviFrame.setLayoutParams(params);
	        stretchanimation = new StretchAnimation(params.height * 2 + 1,
	                params.height, StretchAnimation.TYPE.vertical, 500);
	        stretchanimation.setInterpolator(new BounceInterpolator());
	        stretchanimation.setDuration(800);
	        stretchanimation.setOnAnimationListener(this);
	        naviFrame.setOnClickListener(this);
	
	        mHome = (TextView) this.findViewById(R.id.btnCtrHome);
	        mHome.setClickable(true);
	        mHome.setOnClickListener(this);
	        mActivity = (TextView) findViewById(R.id.btnCtrActivity);
	        mActivity.setClickable(true);
	        mActivity.setOnClickListener(this);
	        mContact = (TextView) findViewById(R.id.btnCtrContact);
	        mContact.setClickable(true);
	        mContact.setOnClickListener(this);
	        mChatMessages = (TextView) findViewById(R.id.btnCtrChat);
	        mChatMessages.setClickable(true);
	        mChatMessages.setOnClickListener(this);
	        naviFrame.setOnClickListener(this);
	
	        if (lScreenInfo.width < 600) {
	            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
	                    LinearLayout.LayoutParams.WRAP_CONTENT,
	                    LinearLayout.LayoutParams.WRAP_CONTENT);
	            lp.setMargins(0, 20, 0, 0);
	            lp.weight = 0.1f;
	            mHome.setLayoutParams(lp);
	            mActivity.setLayoutParams(lp);
	            mContact.setLayoutParams(lp);
	            mChatMessages.setLayoutParams(lp);
	            mHome.setCompoundDrawablesRelativeWithIntrinsicBounds(null, this
	                    .getResources().getDrawable(R.drawable.home_32), null, null);
	            mActivity.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
	                    this.getResources().getDrawable(R.drawable.activity_32),
	                    null, null);
	            mContact.setCompoundDrawablesRelativeWithIntrinsicBounds(null, this
	                    .getResources().getDrawable(R.drawable.contact_32), null,
	                    null);
	            mChatMessages.setCompoundDrawablesRelativeWithIntrinsicBounds(null, this
	                    .getResources().getDrawable(R.drawable.chat_32), null,
	                    null);
	        }

			initHX();
			OperationDlg = new AlertDialog.Builder(mThis)
			.setTitle(getString(R.string.operation))
			.setItems(new String[] {getString(R.string.opbakground),getString(R.string.opexit)}, 
					new DialogInterface.OnClickListener() {
	
				public void onClick(DialogInterface dialog,int which) {
					String Id;
					switch (which)
					{
					case 0:
						PackageManager pm = getPackageManager();  
		                ResolveInfo homeInfo = pm.resolveActivity(new Intent(Intent.ACTION_MAIN)  
		                											.addCategory(Intent.CATEGORY_HOME), 0);  
		                ActivityInfo ai = homeInfo.activityInfo;  
		                Intent startIntent = new Intent(Intent.ACTION_MAIN);  
		                startIntent.addCategory(Intent.CATEGORY_LAUNCHER);  
		                startIntent.setComponent(new ComponentName(ai.packageName, ai.name));  
		                startActivitySafely(startIntent);  
						break;
					case  1:
						MyBackPressed();
		                TeamRadarApplication.getInstance().onTerminate();
						break;
					}
					
					dialog.dismiss();
				}
			}).create();
	    }
        TRServiceConnection.getInstance().CheckForUpdate();
    }
    private void MyBackPressed()
    {
    	super.onBackPressed();
    }
	private ITRMessageListener.Stub mMessageListener = new ITRMessageListener.Stub() {
		
		@Override
		public void OnPushMessage(int Flag) throws RemoteException {
			if(Flag == WebConnection.SUCCESS)
			{
				//Log.d(TAG, "PushMessage Success");
			}
			else
			{
				//Log.d(TAG, "PushMessage Fulure");
			}
		}

		@Override
		public void OnPullMessage(int Flag, List<ShortMessage> msgs)
				throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void OnProcessMessage(int Flag, ShortMessage msg)
				throws RemoteException {
			ProcessMessage(msg);
			
		}
		
	};   
    public void sendInviteMessage(String destSubscription, String groupName,
            String groupId, String groupOwnerId) {
        ShortMessage msg = new ShortMessage();
        msg.setFromId(TRServiceConnection.getInstance().GetCurrentUserId());
        msg.setFromName(TRServiceConnection.getInstance().GetName());
        msg.setFromSubscription(TRServiceConnection.getInstance()
                .GetSubscription());
        msg.setToSubscription(destSubscription);
        msg.setMessage(groupName + "#" + groupId + "#" + groupOwnerId + "#"
                + Constant.MSG_INVITATION);
        TRServiceConnection.getInstance().SendMessage(msg);
        Log.d(TAG, ">>>>>>>>>>>>send Invite Message");
    }
    
    public void sendJoininMessage(long toId, String toName, String toSub,
            String groupName, long groupId, long groupOwnerId) {
        ShortMessage msg = new ShortMessage();
        msg.setFromId(TRServiceConnection.getInstance().GetCurrentUserId());
        msg.setFromName(TRServiceConnection.getInstance().GetName());
        msg.setFromSubscription(TRServiceConnection.getInstance().GetSubscription());
        msg.setToId(toId);
        msg.setToName(toName);
        msg.setToSubscription(toSub);
        msg.setMessage( groupName + "#" + 
                        groupId + "#" +
                        groupOwnerId + "#"+Constant.MSG_JOININ);
        TRServiceConnection.getInstance().SendMessage(msg);
        Log.d(TAG, ">>>>>>>>>>>>Send Join");
    }
    private void ProcessMessage(ShortMessage msg)
	 {
		 
		 String MsgInfo[] = msg.getMessage().split("#");
		 if(MsgInfo[3].equals(Constant.MSG_INVITATION))
		 {
			// passive will receive this message.
			 PushNotification.getInstance().pushANotification(Constant.MESSAGE_TYPE_COMMUNICATION,Constant.MSG_INVITATION, msg);
			 Log.d(TAG, "<<<<<<<<<<<<<<<<<Invite!");
		 }
		 else if(MsgInfo[3].equals(Constant.MSG_INVITATION_ACCEPT))
		 {
			// active will receive this message.
			//add persion to group
			ActivityManagerFrame.addUserToActivity(msg);
			ShortMessage newMsg = msg.getAckMessage();
			newMsg.setMessage(MsgInfo[0]+"#"+MsgInfo[1]+"#"+MsgInfo[2]+"#"+Constant.MSG_INVITATION_OK);
			TRServiceConnection.getInstance().SendMessage(newMsg);
			Log.d(TAG, "<<<<<<<<<<<<<<<<<Invite accepted!");
			ActivityManagerFrame.reloadActivities();
		 }
		 else if(MsgInfo[3].equals(Constant.MSG_INVITATION_REJECT))
		 {
			// active will receive this message.
			 Log.d(TAG, "<<<<<<<<<<<<<<<<<Invite accepted!");
			 
		 }
		 else if(MsgInfo[3].equals(Constant.MSG_INVITATION_OK))
		 {
			// passive will receive this message.
			// passive will enter passive mode.
			 //mActivity.ActivityManagerFrame.setMode(TeamRadarApplication.WORK_MODE_PASSIVE); 
			 Log.d(TAG, ">>>>>>>>>>>>>>>>>>Invite OK!");
			 ActivityManagerFrame.reloadActivities();
		 }
		 else if(MsgInfo[3].equals(Constant.MSG_JOININ))
		 {
			// active will receive this message.
			 PushNotification.getInstance().pushANotification(Constant.MESSAGE_TYPE_COMMUNICATION,Constant.MSG_JOININ, msg);
			// add person to active when accept .
			Log.d(TAG, "<<<<<<<<<<<<<Joinin!");
		 }
		 else if(MsgInfo[3].equals(Constant.MSG_JOININ_ACCEPT))
		 {
			// passive will receive this message.
			//mActivity.pushANotification(MESSAGE_TYPE_COMMUNICATION, msg);
			ShortMessage newMsg = msg.getAckMessage();
			newMsg.setMessage(MsgInfo[0]+"#"+MsgInfo[1]+"#"+MsgInfo[2]+"#"+Constant.MSG_JOININ_OK);
			TRServiceConnection.getInstance().SendMessage(newMsg);
			Log.d(TAG, ">>>>>>>>>>>>Joinin accepted!");
			ActivityManagerFrame.reloadActivities();
		 }
		 else if(MsgInfo[3].equals(Constant.MSG_JOININ_REJECT))
		 {
			// passive will receive this message.
			//mActivity.pushANotification(MESSAGE_TYPE_COMMUNICATION, msg);
			 Log.d(TAG, ">>>>>>>>>>>>Joinin rejected!");
		 }
		 else if(MsgInfo[3].equals(Constant.MSG_JOININ_OK))
		 {
			// passive will receive this message.
			//mActivity.pushANotification(MESSAGE_TYPE_COMMUNICATION, msg);
			 Log.d(TAG, "<<<<<<<<<<<<<Joinin OK!");
			 ActivityManagerFrame.reloadActivities();
		 }
	 }
	private void initHX() {     
		// setContactListener监听联系人的变化等
		EMContactManager.getInstance().setContactListener(new MyContactListener());
		// 注册一个监听连接状态的listener
		
		connectionListener = new MyConnectionListener();
		EMChatManager.getInstance().addConnectionListener(connectionListener);
		
		groupChangeListener = new MyGroupChangeListener();
		// 注册群聊相关的listener
        EMGroupManager.getInstance().addGroupChangeListener(groupChangeListener);
		
	}
    private void startActivitySafely(Intent intent) {    
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
        try {    
            startActivity(intent);    
        } catch (ActivityNotFoundException e) {    
            Toast.makeText(this, "start abnormal", Toast.LENGTH_SHORT).show();    
        } catch (SecurityException e) {    
            Toast.makeText(this, "safty potontial",    
                    Toast.LENGTH_SHORT).show();    
                    Log.e(TAG,"Launcher does not have the permission to launch "    
                                + intent    
                                + ". Make sure to create a MAIN intent-filter for the corresponding activity "    
                                + "or use the exported attribute for this activity.",    
                                e);    
        }    
       }    
    public FragmentContacts getFragmentContacts() {
        // TODO: hard coded "2"
        return (FragmentContacts) fragmentList.get(2);
    }
    
    public PendingIntent getDefalutIntent(int flags){
        PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), flags);
        return pendingIntent;
    }   
    private void getPhoneContacts() {  
        ContentResolver resolver = TeamRadarApplication.getInstance().getApplicationContext().getContentResolver();  
        
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);  
     
     
        if (phoneCursor != null) {  
            while (phoneCursor.moveToNext()) {  
      
	            String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);  
	 
	            if (TextUtils.isEmpty(phoneNumber))  
	                continue;  
	 
	            String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);  
	            TRServiceConnection.getInstance().PutContact(contactName, phoneNumber);
            }  
     
            phoneCursor.close();  
        }  
    }  
    private void getSIMContacts() {  
        ContentResolver resolver = TeamRadarApplication.getInstance().getApplicationContext().getContentResolver();   
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
	              
	            TRServiceConnection.getInstance().PutContact(contactName, phoneNumber);
            }  
     
            phoneCursor.close();  
        }  
    }  
    private void switchPages(int index) {
        fragmentTransaction = fragmentManager.beginTransaction();
        currentIndex = index;
        for (int i = 0; i < fragmentList.size(); i++) {
            fragmentTransaction.hide(fragmentList.get(i));
        }

        fragmentTransaction.show(fragmentList.get(index)).commit();

    }
    public void switchToMapPage()
    {
    	mPageIndexStack.push(0);
    	switchPages(0);
    }
    @Override
    public void onClick(View v) {
        int index = -1;
        int id = v.getId();
        switch (id) {
        case R.id.btnCtrHome:
            index = 0;
            break;
        case R.id.btnCtrActivity:
            index = 1;
            break;
        case R.id.btnCtrContact:
            index = 2;
            break;
        case R.id.btnCtrChat:
            index = 3;
            break;
        default:
            break;
        }
        if (index != -1 && index < 4) {
            if (currentIndex != index) {
                mPageIndexStack.push(currentIndex);// must ahead of switchPages;
                switchPages(index);
            }
        }
    }
    @Override
	protected void onDestroy() {
		super.onDestroy();
		EMChatManager.getInstance().unregisterEventListener(this);

		if(connectionListener != null){
		    EMChatManager.getInstance().removeConnectionListener(connectionListener);
		}
		
		if(groupChangeListener != null){
		    EMGroupManager.getInstance().removeGroupChangeListener(groupChangeListener);
		}
		Log.d(TAG, "onDestroy()");
	}

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }
    
    @Override
    public void onBackPressed() {
    	if(naviFrameUp)
    	{
    		stretchanimation.startAnimation(naviFrame);
    		naviFrameUp = ! naviFrameUp;
    		return;
    	}
		
        if (!mPageIndexStack.empty()) {
            int index = mPageIndexStack.pop();
            switchPages(index);
            if(index == 0)
            {
            	mPageIndexStack.clear();
            }
            backCounter = 0;
        } else {
            backCounter++;
            if (backCounter == 1) {
            	backCounter = 0;
            	OperationDlg.show();
            }
        }
    }

    @Override
    public void animationEnd(View v) {

    }
    
    /**
	 * 刷新未读消息数
	 */
	public void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
		ScreenInfo lScreenInfo = TRServiceConnection.getInstance().GetScreenInfo();
		if(count > 0)
		{
			if(lScreenInfo.width<600)
			{
	            mChatMessages.setCompoundDrawablesRelativeWithIntrinsicBounds(null, this
	                    .getResources().getDrawable(R.drawable.chat_32_unread), null,
	                    null);
			}
			else
			{
	            mChatMessages.setCompoundDrawablesRelativeWithIntrinsicBounds(null, this
	                    .getResources().getDrawable(R.drawable.chat_64_unread), null,
	                    null);
			}	
		}
		else
		{
			if(lScreenInfo.width<600)
			{
	            mChatMessages.setCompoundDrawablesRelativeWithIntrinsicBounds(null, this
	                    .getResources().getDrawable(R.drawable.chat_32), null,
	                    null);
			}
			else
			{
	            mChatMessages.setCompoundDrawablesRelativeWithIntrinsicBounds(null, this
	                    .getResources().getDrawable(R.drawable.chat_64), null,
	                    null);
			}	
		}
		mChatMessages.postInvalidate();
		/*if (count > 0) {
			unreadLabel.setText(String.valueOf(count));
			unreadLabel.setVisibility(View.VISIBLE);
		} else {
			unreadLabel.setVisibility(View.INVISIBLE);
		}*/
	}

	/**
	 * 刷新申请与通知消息数
	 */
	public void updateUnreadAddressLable() {
		runOnUiThread(new Runnable() {
			public void run() {
				int count = getUnreadAddressCountTotal();
				/*if (count > 0) {
					unreadAddressLable.setText(String.valueOf(count));
					unreadAddressLable.setVisibility(View.VISIBLE);
				} else {
					unreadAddressLable.setVisibility(View.INVISIBLE);
				}*/
			}
		});

	}

	/**
	 * 获取未读申请与通知消息
	 * 
	 * @return
	 */
	public int getUnreadAddressCountTotal() {
		int unreadAddressCountTotal = 0;
		//if (TeamRadarApplication.getInstance().getContactList().get(Constant.NEW_FRIENDS_USERNAME) != null)
			//unreadAddressCountTotal = TeamRadarApplication.getInstance().getContactList().get(Constant.NEW_FRIENDS_USERNAME).getUnreadMsgCount();
		return unreadAddressCountTotal;
	}

	/**
	 * 获取未读消息数
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		return unreadMsgCountTotal;
	}

	
	/***
	 * 好友变化listener
	 * 
	 */
	private class MyContactListener implements EMContactListener {

		@Override
		public void onContactAdded(List<String> usernameList) {
			// 保存增加的联系人
			Map<String, HXUser> localUsers = TeamRadarApplication.getInstance().getContactList();
			Map<String, HXUser> toAddUsers = new HashMap<String, HXUser>();
			for (String username : usernameList) {
				HXUser user = setUserHead(username);
				// 添加好友时可能会回调added方法两次
				if (!localUsers.containsKey(username)) {
					userDao.saveContact(user);
				}
				toAddUsers.put(username, user);
			}
			localUsers.putAll(toAddUsers);
			// 刷新ui
			ChatFregment.refresh();

		}

		@Override
		public void onContactDeleted(final List<String> usernameList) {
			// 被删除
			Map<String, HXUser> localUsers = TeamRadarApplication.getInstance().getContactList();
			for (String username : usernameList) {
				localUsers.remove(username);
				userDao.deleteContact(username);
				inviteMessgeDao.deleteMessage(username);
			}
			runOnUiThread(new Runnable() {
				public void run() {
					// 如果正在与此用户的聊天页面
					String st10 = getResources().getString(R.string.have_you_removed);
					if (ChatActivity.activityInstance != null && usernameList.contains(ChatActivity.activityInstance.getToChatUsername())) {
						Toast.makeText(MainActivity.this, ChatActivity.activityInstance.getToChatUsername() + st10, 1).show();
						ChatActivity.activityInstance.finish();
					}
					updateUnreadLabel();
					// 刷新ui
					//contactListFragment.refresh();
					ChatFregment.refresh();
				}
			});
		
		}

		@Override
		public void onContactInvited(String username, String reason) {
			// 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
					inviteMessgeDao.deleteMessage(username);
				}
			}
			// 自己封装的javabean
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			msg.setReason(reason);
			Log.d(TAG, username + "请求加你为好友,reason: " + reason);
			// 设置相应status
			msg.setStatus(InviteMesageStatus.BEINVITEED);
			notifyNewIviteMessage(msg);

		}

		@Override
		public void onContactAgreed(String username) {
			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getFrom().equals(username)) {
					return;
				}
			}
			// 自己封装的javabean
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			Log.d(TAG, username + "同意了你的好友请求");
			msg.setStatus(InviteMesageStatus.BEAGREED);
			notifyNewIviteMessage(msg);

		}

		@Override
		public void onContactRefused(String username) {
			// 参考同意，被邀请实现此功能,demo未实现
			Log.d(username, username + "拒绝了你的好友请求");
		}

	}



	/**
	 * 连接监听listener
	 * 
	 */
	private class MyConnectionListener implements EMConnectionListener {

		@Override
		public void onConnected() {
			boolean groupSynced = HXSDKHelper.getInstance().isGroupsSyncedWithServer();
            boolean contactSynced = HXSDKHelper.getInstance().isContactsSyncedWithServer();
            
            // in case group and contact were already synced, we supposed to notify sdk we are ready to receive the events
            if(groupSynced && contactSynced){
                new Thread(){
                    @Override
                    public void run(){
                        HXSDKHelper.getInstance().notifyForRecevingEvents();
                    }
                }.start();
            }else{
                if(!groupSynced){
                    asyncFetchGroupsFromServer();
                }
                
                if(!contactSynced){
                    asyncFetchContactsFromServer();
                }
                
                if(!HXSDKHelper.getInstance().isBlackListSyncedWithServer()){
                    asyncFetchBlackListFromServer();
                }
            }
            
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					ChatFregment.errorItem.setVisibility(View.GONE);
				}

			});
		}

		@Override
		public void onDisconnected(final int error) {
			final String st1 = getResources().getString(R.string.Less_than_chat_server_connection);
			final String st2 = getResources().getString(R.string.the_current_network);
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					if(error == EMError.USER_REMOVED){
						// 显示帐号已经被移除
						//showAccountRemovedDialog();
					}else if (error == EMError.CONNECTION_CONFLICT) {
						// 显示帐号在其他设备登陆dialog
						//showConflictDialog();
					} else {
						ChatFregment.errorItem.setVisibility(View.VISIBLE);
						if (NetUtils.hasNetwork(MainActivity.this))
							ChatFregment.errorText.setText(st1);
						else
							ChatFregment.errorText.setText(st2);

					}
				}

			});
		}
	}

	/**
	 * MyGroupChangeListener
	 */
	private class MyGroupChangeListener implements GroupChangeListener {

		@Override
		public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
			boolean hasGroup = false;
			for (EMGroup group : EMGroupManager.getInstance().getAllGroups()) {
				if (group.getGroupId().equals(groupId)) {
					hasGroup = true;
					break;
				}
			}
			if (!hasGroup)
				return;

			// 被邀请
			String st3 = getResources().getString(R.string.Invite_you_to_join_a_group_chat);
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(inviter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
			msg.addBody(new TextMessageBody(inviter + st3));
			// 保存邀请消息
			//EMChatManager.getInstance().saveMessage(msg);
			// 提醒新消息
			//EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

			/*runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					// 刷新ui
					//if (currentTabIndex == 0)
						ChatFregment.refresh();
					if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
						GroupsActivity.instance.onResume();
					}
				}
			});*/

		}

		@Override
		public void onInvitationAccpted(String groupId, String inviter, String reason) {

		}

		@Override
		public void onInvitationDeclined(String groupId, String invitee, String reason) {

		}

		@Override
		public void onUserRemoved(String groupId, String groupName) {
			// 提示用户被T了，demo省略此步骤
			// 刷新ui
			/*runOnUiThread(new Runnable() {
				public void run() {
					try {
						updateUnreadLabel();
						//if (currentTabIndex == 0)
						ChatFregment.refresh();
						if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
							GroupsActivity.instance.onResume();
						}
					} catch (Exception e) {
						EMLog.e(TAG, "refresh exception " + e.getMessage());
					}
				}
			});*/
		}

		@Override
		public void onGroupDestroy(String groupId, String groupName) {
			// 群被解散
			// 提示用户群被解散,demo省略
			// 刷新ui
			/*runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					//if (currentTabIndex == 0)
					ChatFregment.refresh();
					if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
						GroupsActivity.instance.onResume();
					}
				}
			});*/

		}

		@Override
		public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {
			// 用户申请加入群聊
			InviteMessage msg = new InviteMessage();
			msg.setFrom(applyer);
			msg.setTime(System.currentTimeMillis());
			msg.setGroupId(groupId);
			msg.setGroupName(groupName);
			msg.setReason(reason);
			Log.d(TAG, applyer + " 申请加入群聊：" + groupName);
			msg.setStatus(InviteMesageStatus.BEAPPLYED);
			notifyNewIviteMessage(msg);
		}

		@Override
		public void onApplicationAccept(String groupId, String groupName, String accepter) {
			String st4 = getResources().getString(R.string.Agreed_to_your_group_chat_application);
			// 加群申请被同意
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(accepter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
			msg.addBody(new TextMessageBody(accepter + st4));
			// 保存同意消息
			EMChatManager.getInstance().saveMessage(msg);
			// 提醒新消息
			EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

			/*runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					// 刷新ui
					//if (currentTabIndex == 0)
					ChatFregment.refresh();
					if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
						GroupsActivity.instance.onResume();
					}
				}
			});*/
		}

		@Override
		public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
			// 加群申请被拒绝，demo未实现
		}

	}


	/**
	 * 保存提示新消息
	 * 
	 * @param msg
	 */
	private void notifyNewIviteMessage(InviteMessage msg) {
		saveInviteMsg(msg);
		// 提示有新消息
		EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

		// 刷新bottom bar消息未读数
		updateUnreadAddressLable();
		// 刷新好友页面ui
		//	contactListFragment.refresh();
	}

	/**
	 * 保存邀请等msg
	 * 
	 * @param msg
	 */
	private void saveInviteMsg(InviteMessage msg) {
		// 保存msg
		inviteMessgeDao.saveMessage(msg);
		// 未读数加1
		HXUser user = TeamRadarApplication.getInstance().getContactList().get(Constant.NEW_FRIENDS_USERNAME);
		if (user.getUnreadMsgCount() == 0)
			user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
	}

	/**
	 * set head
	 * 
	 * @param username
	 * @return
	 */
	HXUser setUserHead(String username) {
		HXUser user = new HXUser();
		user.setUsername(username);
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
        	String displayName = UserUtils.GetUserNameBySub(Encrypt.GetDecryptString(user.getUsername()));
        	if(displayName == null)
        	{
        		displayName = Encrypt.GetDecryptString(user.getUsername());
        	}
            headerName = displayName;
		}
		if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
		return user;
	}
	@Override
	protected void onResume() {
		super.onResume();
		if (!isConflict||!isCurrentAccountRemoved) {
			updateUnreadLabel();
			updateUnreadAddressLable();
			EMChatManager.getInstance().activityResumed();
		}
		TeamRadarHXSDKHelper sdkHelper = (TeamRadarHXSDKHelper) TeamRadarHXSDKHelper.getInstance();
		//sdkHelper.pushActivity(this);
		Log.d(TAG, "push MainActivity");
		// register the event listener when enter the foreground
		EMChatManager.getInstance().registerEventListener(this,
				new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage ,EMNotifierEvent.Event.EventOfflineMessage, EMNotifierEvent.Event.EventConversationListChanged});


	}
    @Override
    public void onPause() {
        super.onPause();
    }
	
	@Override
	protected void onStop() {
		
		TeamRadarHXSDKHelper sdkHelper = (TeamRadarHXSDKHelper) TeamRadarHXSDKHelper.getInstance();
		//sdkHelper.popActivity(this);
		Log.d(TAG, "POP MainActivity");
		super.onStop();
	}

	@Override
	public void finish() {
		TRServiceConnection.getInstance().RemoveMessageListener(mMessageListener);
		//super.onDestroy();
		if(fragmentList != null)
		{
			for(int i=0;i<fragmentList.size();i++)
			{
				fragmentList.get(i).onDestroy();
			}
		}
	    super.finish();
	}
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
    }
    
    // Add For HX
    static void asyncFetchGroupsFromServer(){
	    HXSDKHelper.getInstance().asyncFetchGroupsFromServer(new EMCallBack(){

            @Override
            public void onSuccess() {
                HXSDKHelper.getInstance().noitifyGroupSyncListeners(true);
                
                if(HXSDKHelper.getInstance().isContactsSyncedWithServer()){
                    HXSDKHelper.getInstance().notifyForRecevingEvents();
                }
            }

            @Override
            public void onError(int code, String message) {
                HXSDKHelper.getInstance().noitifyGroupSyncListeners(false);                
            }

            @Override
            public void onProgress(int progress, String status) {
                
            }
            
        });
	}
	
	static void asyncFetchContactsFromServer(){
	    HXSDKHelper.getInstance().asyncFetchContactsFromServer(new EMValueCallBack<List<String>>(){

            @Override
            public void onSuccess(List<String> usernames) {
                Context context = HXSDKHelper.getInstance().getAppContext();
                
                System.out.println("----------------"+usernames.toString());
                EMLog.d("roster", "contacts size: " + usernames.size());
                Map<String, HXUser> userlist = new HashMap<String, HXUser>();
                for (String username : usernames) {
                	HXUser user = new HXUser();
                    user.setUsername(username);
                    setUserHearder(username, user);
                    userlist.put(username, user);
                }
                // 添加user"申请与通知"
                HXUser newFriends = new HXUser();
                newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
                String strChat = context.getString(R.string.Application_and_notify);
                newFriends.setNick(strChat);
        
                userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
                // 添加"群聊"
                HXUser groupUser = new HXUser();
                String strGroup = context.getString(R.string.group_chat);
                groupUser.setUsername(Constant.GROUP_USERNAME);
                groupUser.setNick(strGroup);
                groupUser.setHeader("");
                userlist.put(Constant.GROUP_USERNAME, groupUser);
                
                 // 添加"聊天室"
                HXUser chatRoomItem = new HXUser();
                String strChatRoom = context.getString(R.string.chat_room);
                chatRoomItem.setUsername(Constant.CHAT_ROOM);
                chatRoomItem.setNick(strChatRoom);
                chatRoomItem.setHeader("");
                userlist.put(Constant.CHAT_ROOM, chatRoomItem);
                
                // 添加"Robot"
        		HXUser robotUser = new HXUser();
        		String strRobot = context.getString(R.string.robot_chat);
        		robotUser.setUsername(Constant.CHAT_ROBOT);
        		robotUser.setNick(strRobot);
        		robotUser.setHeader("");
        		userlist.put(Constant.CHAT_ROBOT, robotUser);
        		
                 // 存入内存
                TeamRadarApplication.getInstance().setContactList(userlist);
                 // 存入db
                UserDao dao = new UserDao(context);
                List<HXUser> users = new ArrayList<HXUser>(userlist.values());
                dao.saveContactList(users);

                HXSDKHelper.getInstance().notifyContactsSyncListener(true);
                
                if(HXSDKHelper.getInstance().isGroupsSyncedWithServer()){
                    HXSDKHelper.getInstance().notifyForRecevingEvents();
                }
                
            }

            @Override
            public void onError(int error, String errorMsg) {
                HXSDKHelper.getInstance().notifyContactsSyncListener(false);
            }
	        
	    });
	}
	
	static void asyncFetchBlackListFromServer(){
	    HXSDKHelper.getInstance().asyncFetchBlackListFromServer(new EMValueCallBack<List<String>>(){

            @Override
            public void onSuccess(List<String> value) {
                EMContactManager.getInstance().saveBlackList(value);
                HXSDKHelper.getInstance().notifyBlackListSyncListener(true);
            }

            @Override
            public void onError(int error, String errorMsg) {
                HXSDKHelper.getInstance().notifyBlackListSyncListener(false);
            }
	        
	    });
	}
	
	/**
     * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
     * 
     * @param username
     * @param user
     */
    private static void setUserHearder(String username, HXUser user) {
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNick())) {
            headerName = user.getNick();
        } else {
        	String displayName = UserUtils.GetUserNameBySub(Encrypt.GetDecryptString(user.getUsername()));
        	if(displayName == null)
        	{
        		displayName = Encrypt.GetDecryptString(user.getUsername());
        	}
            headerName = displayName;
        }
        if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
            user.setHeader("");
        } else if (Character.isDigit(headerName.charAt(0))) {
            user.setHeader("#");
        } else {
            user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1)
                    .toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
    }



	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage: 
		{
			EMMessage message = (EMMessage) event.getData();
			if(TeamRadarApplication.getInstance().getLoginState())
			{
				HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
			}
			updateUnreadLabel();
			break;
		}

		case EventOfflineMessage: {
			break;
		}

		case EventConversationListChanged: {
		    break;
		}
		
		default:
			break;
		}
	}
}
