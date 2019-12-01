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
package com.nut.teamradar.model;

import com.nut.teamradar.util.HXPreferenceUtils;

/**
 * HX SDK app model which will manage the user data and preferences
 * @author easemob
 *
 */
public abstract class HXSDKModel {
    public abstract void setSettingMsgNotification(boolean paramBoolean);
    public abstract boolean getSettingMsgNotification();

    public abstract void setSettingMsgSound(boolean paramBoolean);
    public abstract boolean getSettingMsgSound();

    public abstract void setSettingMsgVibrate(boolean paramBoolean);
    public abstract boolean getSettingMsgVibrate();

    public abstract void setSettingMsgSpeaker(boolean paramBoolean);
    public abstract boolean getSettingMsgSpeaker();
   
    public abstract boolean saveHXId(String hxId);
    public abstract String getHXId();
    
    public abstract boolean savePassword(String pwd);
    public abstract String getPwd();
    
    /**
     * 杩斿洖application鎵�鍦ㄧ殑process name,榛樿鏄寘鍚�
     * @return
     */
    public abstract String getAppProcessName();
    /**
     * 鏄惁鎬绘槸鎺ユ敹濂藉弸閭�璇�
     * @return
     */
    public boolean getAcceptInvitationAlways(){
        return false;
    }
    
    /**
     * 鏄惁闇�瑕佺幆淇″ソ鍙嬪叧绯伙紝榛樿鏄痜alse
     * @return
     */
    public boolean getUseHXRoster(){
        return false;
    }
    
    /**
     * 鏄惁闇�瑕佸凡璇诲洖鎵�
     * @return
     */
    public boolean getRequireReadAck(){
        return true;
    }
    
    /**
     * 鏄惁闇�瑕佸凡閫佽揪鍥炴墽
     * @return
     */
    public boolean getRequireDeliveryAck(){
        return false;
    }
    
    /**
     * 鏄惁杩愯鍦╯andbox娴嬭瘯鐜. 榛樿鏄叧鎺夌殑
     * 璁剧疆sandbox 娴嬭瘯鐜
     * 寤鸿寮�鍙戣�呭紑鍙戞椂璁剧疆姝ゆā寮�
     */
    public boolean isSandboxMode(){
        return false;
    }
    
    /**
     * 鏄惁璁剧疆debug妯″紡
     * @return
     */
    public boolean isDebugMode(){
        return true;
    }
    
    public void setGroupsSynced(boolean synced){
    }
    
    public boolean isGroupsSynced(){
        return false;
    }
    
    public void setContactSynced(boolean synced){
    }
    
    public boolean isContactSynced(){
        return false;
    }
    
    public void setBlacklistSynced(boolean synced){
    }
    
    public boolean isBacklistSynced(){
        return false;
    }
}
