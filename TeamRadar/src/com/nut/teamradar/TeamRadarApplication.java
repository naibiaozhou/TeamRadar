package com.nut.teamradar;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.nut.teamradar.domain.HXUser;


public class TeamRadarApplication extends Application{
	private static final String TAG = "TeamRadarApplication";
	public static final int HOME_PAGE_ID = 1000+1;
	public static final int ACTIVITY_PAGE_ID = 1000+2;
	public static final int CONTACT_PAGE_ID = 1000+3;
	public static final int INVITE_PAGE_ID = 1000+4;
	public static final int JOININ_PAGE_ID = 1000+5;
	public static final int PROFILE_PAGE_ID = 1000+6;

	private static TeamRadarApplication application;
	
	public static Context applicationContext;
	public final String PREF_USERNAME = "username";
	
	public static String currentUserNick = "";
	public static TeamRadarHXSDKHelper hxSDKHelper = new TeamRadarHXSDKHelper();

	private boolean login_success = false;
	
	public boolean getLoginState()
	{
		return login_success;
	}
	public void setLoginState(boolean state)
	{
		login_success = state;
	}

	private List<Activity> activities = new ArrayList<Activity>();

	@Override
	public void onCreate() {
		super.onCreate();
        applicationContext = this;
		application = this;

        if(hxSDKHelper.onInit(applicationContext))
        {
        	System.out.println("HXinit success");
        }
        TRServiceConnection.getInstance().Connect(applicationContext);
	}
	
    public static TeamRadarApplication getInstance() {
		return application;
	}

	
	public void addActivity(Activity activity) {
		activities.add(activity);
	}
	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.e(TAG, "onTerminate()");
		
		for (Activity activity : activities) {
			activity.finish();
			
			Log.e(TAG, "finish activity "+ activity.toString());
		}
		
		activities.removeAll(activities);
		if(TRServiceConnection.getInstance().isConnected())
		{
			TRServiceConnection.getInstance().stop();
			TRServiceConnection.getInstance().DisConnect();
		}
		EMChatManager.getInstance().logout();
		Intent service = new Intent();
		service.setAction("com.EmChatService");
		service.setPackage(getPackageName());
		stopService(service);
		
		Intent TRservice = new Intent();
		TRservice.setAction("com.nut.teamradar.TeamRadarService");
		TRservice.setPackage(getPackageName());
		stopService(TRservice);
		try {
			String s = "/n"; 
			Process process = Runtime.getRuntime().exec("am force-stop com.nut.teamradar \n");
			process = Runtime.getRuntime().exec("am force-stop com.EmChatService \n");
			BufferedReader in = new BufferedReader(  
                    new InputStreamReader(process.getInputStream()));  
			String line = null;  
			while ((line = in.readLine()) != null) {  
			    s += line + "/n";                 
			} 
			Log.d(TAG, s);
			s = "/n"; 
			process = Runtime.getRuntime().exec("ps | grep com.nut.teamradar \n");
			in = new BufferedReader(  
                    new InputStreamReader(process.getInputStream()));  
			line = null;  
			while ((line = in.readLine()) != null) {  
			    s += line + "/n";                 
			} 
			Log.d(TAG, s);
			Log.d(TAG, "app Process id = "+android.os.Process.myPid());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		Log.d(TAG, "Acrivity number ="+activities.size());
		android.os.Process.killProcess(android.os.Process.myPid());
		System.gc();
		System.exit(0);
	}
    

    /*
     * public void setRequestUserName(String name) { RequestUserName = name; }
     * public String getRequestUserName() { return RequestUserName; }
     */
	
    /*
     * public void setRequestUserId(int id) { RequestUserId = id; } public int
     * getRequestUserId() { return RequestUserId; }
     */
    /**
	 * ��ȡ�ڴ��к���user list
	 *
	 * @return
	 */
	public Map<String, HXUser> getContactList() {
	    return hxSDKHelper.getContactList();
	}

	/**
	 * ���ú���user list���ڴ���
	 *
	 * @param contactList
	 */
	public void setContactList(Map<String, HXUser> contactList) {
	    hxSDKHelper.setContactList(contactList);
	}

	/**
	 * ��ȡ��ǰ��½�û���
	 *
	 * @return
	 */
	public String getUserName() {
	    return hxSDKHelper.getHXId();
	}
	
	public HXUser getUserByName(String Name) {
	    return hxSDKHelper.getUserByName(Name);
	}

	/**
	 * ��ȡ����
	 *
	 * @return
	 */
	public String getPassword() {
		return hxSDKHelper.getPassword();
	}

	/**
	 * �����û���
	 *
	 * @param user
	 */
	public void setUserName(String username) {
	    hxSDKHelper.setHXId(username);
	}

	/**
	 * �������� �����ʵ������ ֻ��demo��ʵ�ʵ�Ӧ������Ҫ��password ���ܺ���� preference ����sdk
	 * �ڲ����Զ���¼��Ҫ�����룬�Ѿ����ܴ洢��
	 *
	 * @param pwd
	 */
	public void setPassword(String pwd) {
	    hxSDKHelper.setPassword(pwd);
	}

	/**
	 * �˳���¼,�������
	 */
	public void logout(final EMCallBack emCallBack) {
		// �ȵ���sdk logout��������app���Լ�������
	    hxSDKHelper.logout(emCallBack);
	}
	
}