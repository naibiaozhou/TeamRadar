package com.nut.teamradar.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class TeamRadarMonitorService extends Service {
	
	public class TeamRadarMonitorBinder extends Binder{
		
		TeamRadarMonitorService getService()
		{
			return TeamRadarMonitorService.this;
		}

	}
	public void onCreate()
	{
		super.onCreate();
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;  
        return super.onStartCommand(intent, flags, startId);
    }
	@Override
    public boolean onUnbind(Intent intent) {
		stopForeground(true); 
        return super.onUnbind(intent);
        
    }
	@Override
	public void onDestroy() {
		stopForeground(true);
		super.onDestroy();
		Intent localIntent = new Intent();
		localIntent.setClass(this, TeamRadarMonitorService.class); //销毁时重新启动Service
		this.startService(localIntent);
	}
}
