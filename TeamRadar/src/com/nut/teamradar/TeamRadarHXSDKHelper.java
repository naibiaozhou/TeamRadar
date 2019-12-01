/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nut.teamradar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMChatRoomChangeListener;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.OnMessageNotifyListener;
import com.easemob.chat.OnNotificationClickListener;
import com.easemob.util.EMLog;
import com.easemob.util.EasyUtils;
import com.nut.teamradar.controller.HXSDKHelper;
import com.nut.teamradar.domain.HXUser;
import com.nut.teamradar.domain.RobotUser;
import com.nut.teamradar.model.HXNotifier;
import com.nut.teamradar.model.HXNotifier.HXNotificationInfoProvider;
import com.nut.teamradar.model.HXSDKModel;
import com.nut.teamradar.model.TeamRadarHXSDKModel;
import com.nut.teamradar.receiver.CallReceiver;
import com.nut.teamradar.util.CommonUtils;


/**
 * Demo UI HX SDK helper class which subclass HXSDKHelper
 * @author easemob
 *
 */
public class TeamRadarHXSDKHelper extends HXSDKHelper{

    private static final String TAG = "DemoHXSDKHelper";
    
    /**
     * EMEventListener
     */
    protected EMEventListener eventListener = null;

    /**
     * contact list in cache
     */
    private Map<String, HXUser> contactList;
    private Map<String, RobotUser> robotList;
    private CallReceiver callReceiver;
    
    
    /**
     * ????foreground Activity
     */
    private List<Activity> activityList = new ArrayList<Activity>();
    
    public void pushActivity(Activity activity){
        if(!activityList.contains(activity)){
            activityList.add(0,activity); 
        }
    }
    
    public void popActivity(Activity activity){
        activityList.remove(activity);
    }
    
    @Override
    protected void initHXOptions(){
        super.initHXOptions();

        // you can also get EMChatOptions to set related SDK options
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        options.allowChatroomOwnerLeave(getModel().isChatroomOwnerLeaveAllowed());  
    }

    @Override
    protected void initListener(){
        super.initListener();
        IntentFilter callFilter = new IntentFilter(EMChatManager.getInstance().getIncomingCallBroadcastAction());
        if(callReceiver == null){
            callReceiver = new CallReceiver();
        }

        //?????????
        appContext.registerReceiver(callReceiver, callFilter);    
        //????????
        initEventListener();
    }
    
    /**
     * ??????
     * ??????UI??????????,??????UI??????,??????????
     * activityList.size() <= 0 ???????????????,??????Activity Stack
     */
    protected void initEventListener() {
        eventListener = new EMEventListener() {
            private BroadcastReceiver broadCastReceiver = null;
            
            @Override
            public void onEvent(EMNotifierEvent event) {
                EMMessage message = null;
                if(event.getData() instanceof EMMessage){
                    message = (EMMessage)event.getData();
                    EMLog.d(TAG, "receive the event : " + event.getEvent() + ",id : " + message.getMsgId());
                }
                
                switch (event.getEvent()) {
                case EventNewMessage:
                    //?????,?????UI,????????
                    if(activityList.size() <= 0){
                        HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
                        
                    }
                    break;
                case EventOfflineMessage:
                    if(activityList.size() <= 0){
                        EMLog.d(TAG, "received offline messages");
                        List<EMMessage> messages = (List<EMMessage>) event.getData();
                        HXSDKHelper.getInstance().getNotifier().onNewMesg(messages);
                    }
                    break;
                // below is just giving a example to show a cmd toast, the app should not follow this
                // so be careful of this
                case EventNewCMDMessage:
                {
                    
                    EMLog.d(TAG, "收到透传消息");
                    //????body
                    CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
                    final String action = cmdMsgBody.action;//?????action
                    
                    //?????? ????
                    //message.getStringAttribute("");
                    EMLog.d(TAG, String.format("透传消息：action:%s,message:%s", action,message.toString()));
                    final String str = appContext.getString(R.string.receive_the_passthrough);
                    
                    final String CMD_TOAST_BROADCAST = "easemob.demo.cmd.toast";
                    IntentFilter cmdFilter = new IntentFilter(CMD_TOAST_BROADCAST);
                    
                    if(broadCastReceiver == null){
                        broadCastReceiver = new BroadcastReceiver(){

                            @Override
                            public void onReceive(Context context, Intent intent) {
                                // TODO Auto-generated method stub
                                Toast.makeText(appContext, intent.getStringExtra("cmd_value"), Toast.LENGTH_SHORT).show();
                            }
                        };
                        
                      //???????
                        appContext.registerReceiver(broadCastReceiver,cmdFilter);
                    }

                    Intent broadcastIntent = new Intent(CMD_TOAST_BROADCAST);
                    broadcastIntent.putExtra("cmd_value", str+action);
                    appContext.sendBroadcast(broadcastIntent, null);
                    
                    break;
                }
                case EventDeliveryAck:
                    message.setDelivered(true);
                    break;
                case EventReadAck:
                    message.setAcked(true);
                    break;
                // add other events in case you are interested in
                default:
                    break;
                }
                
            }
        };
        
        EMChatManager.getInstance().registerEventListener(eventListener);
        
        EMChatManager.getInstance().addChatRoomChangeListener(new EMChatRoomChangeListener(){
            private final static String ROOM_CHANGE_BROADCAST = "easemob.demo.chatroom.changeevent.toast";
            private final IntentFilter filter = new IntentFilter(ROOM_CHANGE_BROADCAST);
            private boolean registered = false;
            
            private void showToast(String value){
                if(!registered){
                  //???????
                    appContext.registerReceiver(new BroadcastReceiver(){

                        @Override
                        public void onReceive(Context context, Intent intent) {
                            Toast.makeText(appContext, intent.getStringExtra("value"), Toast.LENGTH_SHORT).show();
                        }
                        
                    }, filter);
                    
                    registered = true;
                }
                
                Intent broadcastIntent = new Intent(ROOM_CHANGE_BROADCAST);
                broadcastIntent.putExtra("value", value);
                appContext.sendBroadcast(broadcastIntent, null);
            }
            
            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                showToast(" room : " + roomId + " with room name : " + roomName + " was destroyed");
                Log.i("info","onChatRoomDestroyed="+roomName);
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {
                showToast("member : " + participant + " join the room : " + roomId);
                Log.i("info", "onmemberjoined="+participant);
                
            }

            @Override
            public void onMemberExited(String roomId, String roomName,
                    String participant) {
                showToast("member : " + participant + " leave the room : " + roomId + " room name : " + roomName);
                Log.i("info", "onMemberExited="+participant);
                
            }

            @Override
            public void onMemberKicked(String roomId, String roomName,
                    String participant) {
                showToast("member : " + participant + " was kicked from the room : " + roomId + " room name : " + roomName);
                Log.i("info", "onMemberKicked="+participant);
                
            }

        });
    }

    /**
     * ??????????
     * @return
     */
    @Override
    protected HXNotificationInfoProvider getNotificationListener() {
        //?????????
        return new HXNotificationInfoProvider() {
            
            @Override
            public String getTitle(EMMessage message) {
              //????,??????
                return null;
            }
            
            @Override
            public int getSmallIcon(EMMessage message) {
              //?????,?????
                return 0;
            }
            
            @Override
            public String getDisplayedText(EMMessage message) {
                // ??????????,????message????????
                String ticker = CommonUtils.getMessageDigest(message, appContext);
                if(message.getType() == Type.TXT){
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }
                Map<String,RobotUser> robotMap=((TeamRadarHXSDKHelper)HXSDKHelper.getInstance()).getRobotList();
    			if(robotMap!=null&&robotMap.containsKey(message.getFrom())){
    				String nick = robotMap.get(message.getFrom()).getNick();
    				if(!TextUtils.isEmpty(nick)){
    					return nick + ": " + ticker;
    				}else{
    					return message.getFrom() + ": " + ticker;
    				}
    			}else{
    				return message.getFrom() + ": " + ticker;
    			}
            }
            
            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                return null;
                // return fromUsersNum + "???,???" + messageNum + "???";
            }
            
            @Override
            public Intent getLaunchIntent(EMMessage message) {
                //???????????
                Intent intent = new Intent(appContext, ChatActivity.class);
                //?????????????
                if(isVideoCalling){
                    intent = new Intent(appContext, VideoCallActivity.class);
                }else if(isVoiceCalling){
                    intent = new Intent(appContext, VoiceCallActivity.class);
                }else{
                    ChatType chatType = message.getChatType();
                    if (chatType == ChatType.Chat) { // ????
                        intent.putExtra("userId", message.getFrom());
                        intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
                    } else { // ????
                        // message.getTo()???id
                        intent.putExtra("groupId", message.getTo());
                        if(chatType == ChatType.GroupChat){
                            intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                        }else{
                            intent.putExtra("chatType", ChatActivity.CHATTYPE_CHATROOM);
                        }
                        
                    }
                }
                return intent;
            }
        };
    }
    
    @Override
    protected void onConnectionConflict(){
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("conflict", true);
        appContext.startActivity(intent);
    }
    
    @Override
    protected void onCurrentAccountRemoved(){
    	Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.ACCOUNT_REMOVED, true);
        appContext.startActivity(intent);
    }
    
    
    @Override
    protected HXSDKModel createModel() {
        return new TeamRadarHXSDKModel(appContext);
    }

    @Override
    public HXNotifier createNotifier(){
        return new HXNotifier(){
            public synchronized void onNewMsg(final EMMessage message) {
                if(EMChatManager.getInstance().isSlientMessage(message)){
                    return;
                }
                
                String chatUsename = null;
                List<String> notNotifyIds = null;
                // ??????????????????ids
                if (message.getChatType() == ChatType.Chat) {
                    chatUsename = message.getFrom();
                    notNotifyIds = ((TeamRadarHXSDKModel) hxModel).getDisabledGroups();
                } else {
                    chatUsename = message.getTo();
                    notNotifyIds = ((TeamRadarHXSDKModel) hxModel).getDisabledIds();
                }

                if (notNotifyIds == null || !notNotifyIds.contains(chatUsename)) {
                    // ??app?????
                    if (!EasyUtils.isAppRunningForeground(appContext)) {
                        EMLog.d(TAG, "app is running in backgroud");
                        sendNotification(message, false);
                    } else {
                        sendNotification(message, true);

                    }
                    
                    viberateAndPlayTone(message);
                }
            }
        };
    }
    
    /**
     * get demo HX SDK Model
     */
    public TeamRadarHXSDKModel getModel(){
        return (TeamRadarHXSDKModel) hxModel;
    }
    
    /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<String, HXUser> getContactList() {
        if (getHXId() != null && contactList == null) {
            contactList = ((TeamRadarHXSDKModel) getModel()).getContactList();
        }
        
        return contactList;
    }

        
	public Map<String, RobotUser> getRobotList() {
		if (getHXId() != null && robotList == null) {
			robotList = ((TeamRadarHXSDKModel) getModel()).getRobotList();
		}
		return robotList;
	}
	
	
	public boolean isRobotMenuMessage(EMMessage message) {

		try {
			JSONObject jsonObj = message.getJSONObjectAttribute(Constant.MESSAGE_ATTR_ROBOT_MSGTYPE);
			if (jsonObj.has("choice")) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}
	
	public String getRobotMenuMessageDigest(EMMessage message) {
		String title = "";
		try {
			JSONObject jsonObj = message.getJSONObjectAttribute(Constant.MESSAGE_ATTR_ROBOT_MSGTYPE);
			if (jsonObj.has("choice")) {
				JSONObject jsonChoice = jsonObj.getJSONObject("choice");
				title = jsonChoice.getString("title");
			}
		} catch (Exception e) {
		}
		return title;
	}
	
	
	

    public void setRobotList(Map<String, RobotUser> robotList){
    	this.robotList = robotList;
    }
    
    public void setContactList(Map<String, HXUser> contactList) {
        this.contactList = contactList;
    }
    
    public HXUser getUserByName(String Name){
    	HXUser usr = null;
		Iterator<Entry<String, HXUser>> iterator = contactList.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, HXUser> entry = iterator.next();
			if (entry.getValue().getUsername().equals(Name))
			{
				usr = entry.getValue();
			}
		}
        return usr;
    }
    
    
    @Override
    public void logout(final EMCallBack callback){
        endCall();
        super.logout(new EMCallBack(){

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                setContactList(null);
                setRobotList(null);
                getModel().closeDB();
                if(callback != null){
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub
                if(callback != null){
                    callback.onProgress(progress, status);
                }
            }
            
        });
    }
    
    void endCall(){
        try {
            EMChatManager.getInstance().endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
