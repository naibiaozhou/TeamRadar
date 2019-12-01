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
package com.nut.teamradar.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatConfig.EMEnvMode;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.nut.teamradar.model.DefaultHXSDKModel;
import com.nut.teamradar.model.HXNotifier;
import com.nut.teamradar.model.HXNotifier.HXNotificationInfoProvider;
import com.nut.teamradar.model.HXSDKModel;


/**
 * The developer can derive from this class to talk with HuanXin SDK
 * All the Huan Xin related initialization and global listener are implemented in this class which will 
 * help developer to speed up the SDK integration銆�
 * this is a global instance class which can be obtained in any codes through getInstance()
 * 
 * 寮�鍙戜汉鍛樺彲浠ラ�夋嫨缁ф壙杩欎釜鐜俊SDK甯姪绫诲幓鍔犲揩鍒濆鍖栭泦鎴愰�熷害銆傛绫讳細鍒濆鍖栫幆淇DK锛屽苟璁剧疆鍒濆鍖栧弬鏁板拰鍒濆鍖栫浉搴旂殑鐩戝惉鍣�
 * 涓嶈繃缁ф壙绫婚渶瑕佹牴鎹姹傛眰鎻愪緵鐩稿簲鐨勫嚱鏁帮紝灏ゅ叾鏄彁渚涗竴涓獅@link HXSDKModel}. 鎵�浠ヨ瀹炵幇abstract protected HXSDKModel createModel();
 * 鍏ㄥ眬浠呮湁涓�涓绫荤殑瀹炰緥瀛樺湪锛屾墍浠ュ彲浠ュ湪浠绘剰鍦版柟閫氳繃getInstance()鍑芥暟鑾峰彇姝ゅ叏灞�瀹炰緥
 * 
 * @author easemob
 *
 */
public abstract class HXSDKHelper {

	/**
	 * 群组更新完成
	 */
	static public interface HXSyncListener {
		public void onSyncSucess(boolean success);
	}
	
    private static final String TAG = "HXSDKHelper";
    /**
     * application context
     */
    protected Context appContext = null;
    
    /**
     * HuanXin mode helper, which will manage the user data and user preferences
     */
    protected HXSDKModel hxModel = null;
    
    /**
     * MyConnectionListener
     */
    protected EMConnectionListener connectionListener = null;
    
    /**
     * HuanXin ID in cache
     */
    protected String hxId = null;
    
    /**
     * password in cache
     */
    protected String password = null;
    
    /**
     * init flag: test if the sdk has been inited before, we don't need to init again
     */
    private boolean sdkInited = false;

    /**
     * the global HXSDKHelper instance
     */
    private static HXSDKHelper me = null;
    
    /**
     * the notifier
     */
    protected HXNotifier notifier = null;

	/**
	 * HuanXin sync groups status listener
	 */
	private List<HXSyncListener> syncGroupsListeners;

	/**
	 * HuanXin sync contacts status listener
	 */
	private List<HXSyncListener> syncContactsListeners;

	/**
	 * HuanXin sync blacklist status listener
	 */
	private List<HXSyncListener> syncBlackListListeners;

	private boolean isSyncingGroupsWithServer = false;

	private boolean isSyncingContactsWithServer = false;

	private boolean isSyncingBlackListWithServer = false;
	
	private boolean isGroupsSyncedWithServer = false;

	private boolean isContactsSyncedWithServer = false;

	private boolean isBlackListSyncedWithServer = false;
	
	private boolean alreadyNotified = false;
	
	public boolean isVoiceCalling;
    public boolean isVideoCalling;

    protected HXSDKHelper(){
        me = this;
    }
    
    /**
     * this function will initialize the HuanXin SDK
     * 
     * @return boolean true if caller can continue to call HuanXin related APIs after calling onInit, otherwise false.
     * 
     * 鐜俊鍒濆鍖朣DK甯姪鍑芥暟
     * 杩斿洖true濡傛灉姝ｇ‘鍒濆鍖栵紝鍚﹀垯false锛屽鏋滆繑鍥炰负false锛岃鍦ㄥ悗缁殑璋冪敤涓笉瑕佽皟鐢ㄤ换浣曞拰鐜俊鐩稿叧鐨勪唬鐮�
     * 
     * for example:
     * 渚嬪瓙锛�
     * 
     * public class DemoHXSDKHelper extends HXSDKHelper
     * 
     * HXHelper = new DemoHXSDKHelper();
     * if(HXHelper.onInit(context)){
     *     // do HuanXin related work
     * }
     */
    public synchronized boolean onInit(Context context){
        if(sdkInited){
            return true;
        }

        appContext = context;
        
        // create HX SDK model
        hxModel = createModel();
        
        // create a defalut HX SDK model in case subclass did not provide the model
        if(hxModel == null){
            hxModel = new DefaultHXSDKModel(appContext);
        }
        
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        
        Log.d(TAG, "process app name : " + processAppName);
        
        // 濡傛灉app鍚敤浜嗚繙绋嬬殑service锛屾application:onCreate浼氳璋冪敤2娆�
        // 涓轰簡闃叉鐜俊SDK琚垵濮嬪寲2娆★紝鍔犳鍒ゆ柇浼氫繚璇丼DK琚垵濮嬪寲1娆�
        // 榛樿鐨刟pp浼氬湪浠ュ寘鍚嶄负榛樿鐨刾rocess name涓嬭繍琛岋紝濡傛灉鏌ュ埌鐨刾rocess name涓嶆槸app鐨刾rocess name灏辩珛鍗宠繑鍥�
        if (processAppName == null || !processAppName.equalsIgnoreCase(hxModel.getAppProcessName())) {
            Log.e(TAG, "enter the service process!");
            
            // 鍒欐application::onCreate 鏄service 璋冪敤鐨勶紝鐩存帴杩斿洖
            return false;
        }

        // 鍒濆鍖栫幆淇DK,涓�瀹氳鍏堣皟鐢╥nit()
        EMChat.getInstance().init(context);
        
        // 璁剧疆sandbox娴嬭瘯鐜
        // 寤鸿寮�鍙戣�呭紑鍙戞椂璁剧疆姝ゆā寮�
        if(hxModel.isSandboxMode()){
            EMChat.getInstance().setEnv(EMEnvMode.EMSandboxMode);
        }
        
        if(hxModel.isDebugMode()){
            // set debug mode in development process
            EMChat.getInstance().setDebugMode(true);    
        }

        Log.d(TAG, "initialize EMChat SDK");
        
        initHXOptions();
        initListener();
        
        syncGroupsListeners = new ArrayList<HXSyncListener>();
        syncContactsListeners = new ArrayList<HXSyncListener>();
        syncBlackListListeners = new ArrayList<HXSyncListener>();
        
        isGroupsSyncedWithServer = hxModel.isGroupsSynced();
        isContactsSyncedWithServer = hxModel.isContactSynced();
        isBlackListSyncedWithServer = hxModel.isBacklistSynced();
        
        sdkInited = true;
        return true;
    }
    
    /**
     * get global instance
     * @return
     */
    public static HXSDKHelper getInstance(){
        return me;
    }
    
    public Context getAppContext(){
        return appContext;
    }
    
    public HXSDKModel getModel(){
        return hxModel;
    }
    
    public String getHXId(){
        if(hxId == null){
            hxId = hxModel.getHXId();
        }
        return hxId;
    }
    
    public String getPassword(){
        if(password == null){
            password = hxModel.getPwd();
        }
        return password;    
    }
    
    public void setHXId(String hxId){
        if (hxId != null) {
            if(hxModel.saveHXId(hxId)){
                this.hxId = hxId;
            }
        }
    }
    
    public void setPassword(String password){
        if(hxModel.savePassword(password)){
            this.password = password;
        }
    }
    
    /**
     * the subclass must override this class to provide its own model or directly use {@link DefaultHXSDKModel}
     * @return
     */
    abstract protected HXSDKModel createModel();
    
    /**
     * please make sure you have to get EMChatOptions by following method and set related options
     *      EMChatOptions options = EMChatManager.getInstance().getChatOptions();
     */
    protected void initHXOptions(){
        Log.d(TAG, "init HuanXin Options");
        
        // 鑾峰彇鍒癊MChatOptions瀵硅薄
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        // 榛樿娣诲姞濂藉弸鏃讹紝鏄笉闇�瑕侀獙璇佺殑锛屾敼鎴愰渶瑕侀獙璇�
        options.setAcceptInvitationAlways(hxModel.getAcceptInvitationAlways());
        // 榛樿鐜俊鏄笉缁存姢濂藉弸鍏崇郴鍒楄〃鐨勶紝濡傛灉app渚濊禆鐜俊鐨勫ソ鍙嬪叧绯伙紝鎶婅繖涓睘鎬ц缃负true
        options.setUseRoster(hxModel.getUseHXRoster());
        // 设置是否需要已读回执
        options.setRequireAck(hxModel.getRequireReadAck());
        // 璁剧疆鏄惁闇�瑕佸凡閫佽揪鍥炴墽
        options.setRequireDeliveryAck(hxModel.getRequireDeliveryAck());
        // 设置从db初始化加载时, 每个conversation需要加载msg的个数
        options.setNumberOfMessagesLoaded(1);
        
        notifier = createNotifier();
        notifier.init(appContext);
        
        notifier.setNotificationInfoProvider(getNotificationListener());
    }
    
    /**
     * subclass can override this api to return the customer notifier
     * 
     * @return
     */
    protected HXNotifier createNotifier(){
        return new HXNotifier();
    }
    
    public HXNotifier getNotifier(){
        return notifier;
    }
    
    /**
     * logout HuanXin SDK
     */
    public void logout(final EMCallBack callback){
        setPassword(null);
        reset();
        EMChatManager.getInstance().logout(new EMCallBack(){

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
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
    
    /**
     * 妫�鏌ユ槸鍚﹀凡缁忕櫥褰曡繃
     * @return
     */
    public boolean isLogined(){
       return EMChat.getInstance().isLoggedIn();
    }
    
    protected HXNotificationInfoProvider getNotificationListener(){
        return null;
    }

    /**
     * init HuanXin listeners
     */
    protected void initListener(){
        Log.d(TAG, "init listener");
        
        // create the global connection listener
        connectionListener = new EMConnectionListener() {
            @Override
            public void onDisconnected(int error) {
            	if (error == EMError.USER_REMOVED) {
            		onCurrentAccountRemoved();
            	}else if (error == EMError.CONNECTION_CONFLICT) {
                    onConnectionConflict();
                }else{
                    onConnectionDisconnected(error);
                }
            }

            @Override
            public void onConnected() {
                onConnectionConnected();
            }
        };
        EMChatManager.getInstance().addConnectionListener(connectionListener);
    }
    
    /**
     * the developer can override this function to handle connection conflict error
     */
    protected void onConnectionConflict(){}

    
    /**
     * the developer can override this function to handle user is removed error
     */
    protected void onCurrentAccountRemoved(){}
    
    
    /**
     * handle the connection connected
     */
    protected void onConnectionConnected(){}
    
    /**
     * handle the connection disconnect
     * @param error see {@link EMError}
     */
    protected void onConnectionDisconnected(int error){}

    /**
     * check the application process name if process name is not qualified, then we think it is a service process and we will not init SDK
     * @param pID
     * @return
     */
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = appContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }
    
        
    public void addSyncGroupListener(HXSyncListener listener) {
	    if (listener == null) {
		    return;
	    }
	    if (!syncGroupsListeners.contains(listener)) {
		    syncGroupsListeners.add(listener);
	    }
    }

    public void removeSyncGroupListener(HXSyncListener listener) {
	    if (listener == null) {
		    return;
	    }
	    if (syncGroupsListeners.contains(listener)) {
		    syncGroupsListeners.remove(listener);
	    }
    }

    public void addSyncContactListener(HXSyncListener listener) {
	    if (listener == null) {
		    return;
	    }
	    if (!syncContactsListeners.contains(listener)) {
		    syncContactsListeners.add(listener);
	    }
    }

    public void removeSyncContactListener(HXSyncListener listener) {
	    if (listener == null) {
		    return;
	    }
	    if (syncContactsListeners.contains(listener)) {
		    syncContactsListeners.remove(listener);
	    }
    }

    public void addSyncBlackListListener(HXSyncListener listener) {
	    if (listener == null) {
		    return;
	    }
	    if (!syncBlackListListeners.contains(listener)) {
		    syncBlackListListeners.add(listener);
	    }
    }

    public void removeSyncBlackListListener(HXSyncListener listener) {
	    if (listener == null) {
		    return;
	    }
	    if (syncBlackListListeners.contains(listener)) {
		    syncBlackListListeners.remove(listener);
	    }
    }
   
    /**
     * 同步操作，从服务器获取群组列表
     * 该方法会记录更新状态，可以通过isSyncingGroupsFromServer获取是否正在更新
     * 和HXPreferenceUtils.getInstance().getSettingSyncGroupsFinished()获取是否更新已经完成
     * @throws EaseMobException
     */
    public synchronized void asyncFetchGroupsFromServer(final EMCallBack callback){
        if(isSyncingGroupsWithServer){
            return;
        }
        
        isSyncingGroupsWithServer = true;
        
        new Thread(){
            @Override
            public void run(){
                try {
                    EMGroupManager.getInstance().getGroupsFromServer();
                    
                    // in case that logout already before server returns, we should return immediately
                    if(!EMChat.getInstance().isLoggedIn()){
                        return;
                    }
                    
                    hxModel.setGroupsSynced(true);
                    
                    isGroupsSyncedWithServer = true;
                    isSyncingGroupsWithServer = false;
                    if(callback != null){
                        callback.onSuccess();
                    }
                } catch (EaseMobException e) {
                    hxModel.setGroupsSynced(false);
                    isGroupsSyncedWithServer = false;
                    isSyncingGroupsWithServer = false;
                    if(callback != null){
                        callback.onError(e.getErrorCode(), e.toString());
                    }
                }
            
            }
        }.start();
    }

    public void noitifyGroupSyncListeners(boolean success){
        for (HXSyncListener listener : syncGroupsListeners) {
            listener.onSyncSucess(success);
        }
    }
    
    public void asyncFetchContactsFromServer(final EMValueCallBack<List<String>> callback){
        if(isSyncingContactsWithServer){
            return;
        }
        
        isSyncingContactsWithServer = true;
        
        new Thread(){
            @Override
            public void run(){
                List<String> usernames = null;
                try {
                    usernames = EMContactManager.getInstance().getContactUserNames();
                    
                    // in case that logout already before server returns, we should return immediately
                    if(!EMChat.getInstance().isLoggedIn()){
                        return;
                    }
                    
                    hxModel.setContactSynced(true);
                    
                    isContactsSyncedWithServer = true;
                    isSyncingContactsWithServer = false;
                    if(callback != null){
                        callback.onSuccess(usernames);
                    }
                } catch (EaseMobException e) {
                    hxModel.setContactSynced(false);
                    isContactsSyncedWithServer = false;
                    isSyncingContactsWithServer = false;
                    e.printStackTrace();
                    if(callback != null){
                        callback.onError(e.getErrorCode(), e.toString());
                    }
                }
                
            }
        }.start();
    }

    public void notifyContactsSyncListener(boolean success){
        for (HXSyncListener listener : syncContactsListeners) {
            listener.onSyncSucess(success);
        }
    }
    
    public void asyncFetchBlackListFromServer(final EMValueCallBack<List<String>> callback){
        
        if(isSyncingBlackListWithServer){
            return;
        }
        
        isSyncingBlackListWithServer = true;
        
        new Thread(){
            @Override
            public void run(){
                try {
                    List<String> usernames = null;
                    usernames = EMContactManager.getInstance().getBlackListUsernamesFromServer();
                    
                    // in case that logout already before server returns, we should return immediately
                    if(!EMChat.getInstance().isLoggedIn()){
                        return;
                    }
                    
                    hxModel.setBlacklistSynced(true);
                    
                    isBlackListSyncedWithServer = true;
                    isSyncingBlackListWithServer = false;
                    if(callback != null){
                        callback.onSuccess(usernames);
                    }
                } catch (EaseMobException e) {
                    hxModel.setBlacklistSynced(false);
                    
                    isBlackListSyncedWithServer = false;
                    isSyncingBlackListWithServer = true;
                    e.printStackTrace();
                    
                    if(callback != null){
                        callback.onError(e.getErrorCode(), e.toString());
                    }
                }
                
            }
        }.start();
    }

    public void notifyBlackListSyncListener(boolean success){
        for (HXSyncListener listener : syncBlackListListeners) {
            listener.onSyncSucess(success);
        }
    }
    
    public boolean isSyncingGroupsWithServer() {
	    return isSyncingGroupsWithServer;
    }

    public boolean isSyncingContactsWithServer() {
	    return isSyncingContactsWithServer;
    }

    public boolean isSyncingBlackListWithServer() {
	    return isSyncingBlackListWithServer;
    }
    
    public boolean isGroupsSyncedWithServer() {
	    return isGroupsSyncedWithServer;
    }

    public boolean isContactsSyncedWithServer() {
	    return isContactsSyncedWithServer;
    }

    public boolean isBlackListSyncedWithServer() {
	    return isBlackListSyncedWithServer;
    }
    
    public synchronized void notifyForRecevingEvents(){
        if(alreadyNotified){
            return;
        }
        
        // 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
        EMChat.getInstance().setAppInited();
        alreadyNotified = true;
    }
    
    synchronized void reset(){
        isSyncingGroupsWithServer = false;
        isSyncingContactsWithServer = false;
        isSyncingBlackListWithServer = false;
        
        hxModel.setGroupsSynced(false);
        hxModel.setContactSynced(false);
        hxModel.setBlacklistSynced(false);
        
        isGroupsSyncedWithServer = false;
        isContactsSyncedWithServer = false;
        isBlackListSyncedWithServer = false;
        
        alreadyNotified = false;
    }
}
