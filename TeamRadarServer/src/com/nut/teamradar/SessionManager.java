package com.nut.teamradar;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.nut.teamradar.base.Constant;

public class SessionManager {
	private Thread mThread;
	private SessionManager()
	{
		mSession = new HashMap<String, UserInformation>();
		mThread = new Thread(new Runnable(){

			@Override
			public void run() {
				while(true)
				{
					//System.out.println("SessionManager is running");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//System.out.println("SessionManager is running1");
					Iterator<String> it = mSession.keySet().iterator(); 
					while(it.hasNext())
					{
						String Key = it.next();
						UserInformation UserInfo = mSession.get(Key);
						int Counter = (int)(UserInfo.get(Constant.SESSION_COUNTER));
						Counter++;
						//System.out.println("User info Counter= "+String.valueOf(Counter));
						if(Counter < 28800)
						{
							UserInfo.put(Constant.SESSION_COUNTER, Counter);
							mSession.put(Key,UserInfo);
						}
						else
						{
							mSession.remove(Key);
							System.out.println("User info of "+Key+" removed!");
							
						}
					}
					//System.out.println("SessionManager is running2");
				}
			}
		});
		mThread.start();
	}
	private Map<String, UserInformation> mSession = null;
	static SessionManager _instance = null;
	static SessionManager getInstance()
	{
		if(_instance == null)
		{
			if(_instance == null)
			{
				_instance = new SessionManager();
			}
		}
		return _instance;
	}
	
	public void putUser(String session,UserInformation info)
	{
		mSession.put(session, info);
	}
	public UserInformation getUser(String session)
	{
		return (UserInformation)mSession.get(session);
	}
	
	
}
