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
package com.nut.teamradar.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.easemob.util.HanziToPinyin;
import com.nut.teamradar.Constant;
import com.nut.teamradar.domain.HXUser;
import com.nut.teamradar.domain.RobotUser;

public class UserDao {
	public static final String TABLE_NAME = "uers";
	public static final String COLUMN_NAME_ID = "username";
	public static final String COLUMN_NAME_NICK = "nick";
	public static final String COLUMN_NAME_AVATAR = "avatar";
	
	public static final String PREF_TABLE_NAME = "pref";
	public static final String COLUMN_NAME_DISABLED_GROUPS = "disabled_groups";
	public static final String COLUMN_NAME_DISABLED_IDS = "disabled_ids";

	public static final String ROBOT_TABLE_NAME = "robots";
	public static final String ROBOT_COLUMN_NAME_ID = "username";
	public static final String ROBOT_COLUMN_NAME_NICK = "nick";
	public static final String ROBOT_COLUMN_NAME_AVATAR = "avatar";
	
	
	public UserDao(Context context) {
	    TeamRadarDBManager.getInstance().onInit(context);
	}

	/**
	 * 保存好友list
	 * 
	 * @param contactList
	 */
	public void saveContactList(List<HXUser> contactList) {
	    TeamRadarDBManager.getInstance().saveContactList(contactList);
	}

	/**
	 * 鑾峰彇濂藉弸list
	 * 
	 * @return
	 */
	public Map<String, HXUser> getContactList() {
		
	    return TeamRadarDBManager.getInstance().getContactList();
	}
	
	/**
	 * 删除一个联系人
	 * @param username
	 */
	public void deleteContact(String username){
	    TeamRadarDBManager.getInstance().deleteContact(username);
	}
	
	/**
	 * 保存一个联系人
	 * @param user
	 */
	public void saveContact(HXUser user){
	    TeamRadarDBManager.getInstance().saveContact(user);
	}
	
	public void setDisabledGroups(List<String> groups){
	    TeamRadarDBManager.getInstance().setDisabledGroups(groups);
    }
    
    public List<String>  getDisabledGroups(){       
        return TeamRadarDBManager.getInstance().getDisabledGroups();
    }
    
    public void setDisabledIds(List<String> ids){
        TeamRadarDBManager.getInstance().setDisabledIds(ids);
    }
    
    public List<String> getDisabledIds(){
        return TeamRadarDBManager.getInstance().getDisabledIds();
    }
    
    public Map<String, RobotUser> getRobotUser(){
    	return TeamRadarDBManager.getInstance().getRobotList();
    }
    
    public void saveRobotUser(List<RobotUser> robotList){
    	TeamRadarDBManager.getInstance().saveRobotList(robotList);
    }
}
