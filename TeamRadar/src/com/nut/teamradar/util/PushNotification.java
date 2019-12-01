package com.nut.teamradar.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigPictureStyle;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.widget.RemoteViews;

import com.nut.teamradar.Constant;
import com.nut.teamradar.MainActivity;
import com.nut.teamradar.R;
import com.nut.teamradar.TRServiceConnection;
import com.nut.teamradar.model.ShortMessage;

public class PushNotification {
	
	private static final String TAG = "pushNotification";
	private static String STATUS_BAR_COVER_CLICK_ACTION = "TRNotificationBarActon";
	private String INTENT_BUTTONID_TAG = "TEAMRADAR_NOTIFICATION_ACTION";
	private String INTENT_NOTIFICATION_INDEX_TAG = "TEAMRADAR_NOTIFICATION_INDEX_ACTION";
	public static final int BUTTON_ACCEPT = 10001;
	public static final int BUTTON_REJECT = 10002;
	public static int NotificationIndex=80004;
	
	private Map<Integer,ShortMessage> MessageList = null;
    private NotificationManager mNotificationManager=null;
    
    private MainActivity ContainerActivity;
    
    private static PushNotification _instance = null;
    
	private PushNotification()
	{
		MessageList = new HashMap<Integer,ShortMessage>();
	}
	public static PushNotification getInstance()
	{
		if(_instance == null)
			_instance = new PushNotification();
		return _instance;
	}
	public void SetContainerActivity(MainActivity act)
	{
		ContainerActivity = act;
	}
	private BroadcastReceiver onClickReceiver = new BroadcastReceiver() {
    	ShortMessage msg;
		@Override
		public void onReceive(Context context, Intent intent) {
			
			String str = intent.getAction();
			if (str.contains(STATUS_BAR_COVER_CLICK_ACTION))
			{
				int index = intent.getIntExtra(INTENT_NOTIFICATION_INDEX_TAG, -1);
				if(index != -1)
				{
					int id = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
					switch(id)
					{
						case BUTTON_ACCEPT:
						{
							msg = MessageList.get(index).getAckMessage();
							String MsgInfo[] = MessageList.get(index).getMessage().split("#");
							if(MsgInfo[3].equals(Constant.MSG_JOININ))
							{
								//active make the decision
								//Message Format : GROUPNAME#GROUPID#OWNERID#OPTION
								msg.setMessage(MsgInfo[0]+"#"+MsgInfo[1]+"#"+MsgInfo[2]+"#"+Constant.MSG_JOININ_ACCEPT);
								ContainerActivity.ActivityManagerFrame.addUserToActivity(MessageList.get(index));
								
							}
							else if(MsgInfo[3].equals(Constant.MSG_INVITATION))
							{
								// passive make the decision
								//Message Format : GROUPNAME#GROUPID#OWNERID#OPTION
								msg.setMessage(MsgInfo[0]+"#"+MsgInfo[1]+"#"+MsgInfo[2]+"#"+Constant.MSG_INVITATION_ACCEPT);
							}
							TRServiceConnection.getInstance().SendMessage(msg);
							MessageList.remove(msg);

							Log.e(TAG,">>>>>>>>>>>>BUTTON_ACCEPT!"+String.valueOf(index));
							mNotificationManager.cancel(index);
							break;
						}
						case BUTTON_REJECT:
						{
							msg = MessageList.get(index).getAckMessage();
							String results[] = MessageList.get(index).getMessage().split("#");
							if(results[3].equals(Constant.MSG_JOININ))
							{
								//active make the decision
								msg.setMessage(results[0]+"#"+results[1]+"#"+results[2]+"#"+Constant.MSG_JOININ_REJECT);
							}
							else if(results[3].equals(Constant.MSG_INVITATION))
							{
								//passive make the decision
								msg.setMessage(results[0]+"#"+results[1]+"#"+results[2]+"#"+Constant.MSG_INVITATION_REJECT);							
							}
							TRServiceConnection.getInstance().SendMessage(msg);
							MessageList.remove(msg);
							Log.e(TAG,">>>>>>>>>>>>BUTTON_REJECT!"+String.valueOf(index));
							mNotificationManager.cancel(index);
							break;
						}
					}
				}
		    }
		}
	};
	public void pushANotification(int type,String func,ShortMessage msg)
    {
    	String msgInfo[] = msg.getMessage().split("#");
    	String DisplayMsg;
    	if(func.equals(Constant.MSG_INVITATION))
    	{
    		DisplayMsg = "邀请你加入活动:"+msgInfo[0];
    	}
    	else
    	{
    		DisplayMsg = "申请加入活动:"+msgInfo[0];
    	}
    	if(type == Constant.MESSAGE_TYPE_COMMUNICATION)
    	{
    		MessageList.put(NotificationIndex,msg);
    	}
    	else
    	{
    		
    	}
    	pushCommnicationNotify(NotificationIndex,msg.getFromName(),msg.getFromSubscription(),DisplayMsg);
    	NotificationIndex++;
    }
    private void pushCommnicationNotify(int index,String name ,String sub,String actName){
    	
    	mNotificationManager = (NotificationManager) ContainerActivity.getSystemService(ContainerActivity.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new Builder(ContainerActivity);
        RemoteViews mRemoteViews = new RemoteViews(ContainerActivity.getPackageName(), R.layout.comm_notification_layout);
        mRemoteViews.setTextViewText(R.id.txtJinviterName, name);
        mRemoteViews.setTextViewText(R.id.txtSubscription, sub);
        mRemoteViews.setTextViewText(R.id.txtActivityName, actName);
        String ClickAction =  String.format("%s%daction", STATUS_BAR_COVER_CLICK_ACTION,index);
 		IntentFilter filter = new IntentFilter();
		filter.addAction(ClickAction);
		ContainerActivity.registerReceiver(onClickReceiver, filter);
		
		Intent buttonIntent = new Intent(ClickAction);
		
		PendingIntent pendButtonIntent = PendingIntent.getBroadcast(ContainerActivity, 0, buttonIntent, 0);
		//mRemoteViews.setOnClickPendingIntent(R.id.btAccept, pendButtonIntent);
		
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_REJECT);
		buttonIntent.putExtra(INTENT_NOTIFICATION_INDEX_TAG, index);
		PendingIntent intent_reject = PendingIntent.getBroadcast(ContainerActivity, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.btReject, intent_reject);
		
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_ACCEPT);
		buttonIntent.putExtra(INTENT_NOTIFICATION_INDEX_TAG, index);
        PendingIntent intent_accept = PendingIntent.getBroadcast(ContainerActivity, 2, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.btAccept, intent_accept);
        
		
		
		
		/*PendingIntent intent_accept = PendingIntent.getBroadcast(ContainerActivity, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_ACCEPT);
		buttonIntent.putExtra(INTENT_NOTIFICATION_INDEX_TAG, index);
		mRemoteViews.setOnClickPendingIntent(R.id.btReject, intent_accept);
		
		buttonIntent = new Intent(STATUS_BAR_COVER_CLICK_ACTION);		
		PendingIntent intent_reject = PendingIntent.getBroadcast(ContainerActivity, 2, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_REJECT);
		buttonIntent.putExtra(INTENT_NOTIFICATION_INDEX_TAG, index);  
		mRemoteViews.setOnClickPendingIntent(R.id.btAccept, intent_reject);*/
        
        //NotificationCompat.BigPictureStyle textStyle = new BigPictureStyle();
        mBuilder.setContent(mRemoteViews)
                .setContentIntent(ContainerActivity.getDefalutIntent(Notification.FLAG_NO_CLEAR))
                .setWhen(System.currentTimeMillis())
                .setTicker("Message")
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOngoing(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.invite_32).build();
        Notification notify = mBuilder.build();
        notify.bigContentView = mRemoteViews;
        notify.flags = Notification.FLAG_NO_CLEAR;
        mNotificationManager.notify(index, notify);
    }	
}
