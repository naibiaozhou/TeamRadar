package com.nut.teamradar.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;

import com.nut.teamradar.Constant;
import com.nut.teamradar.model.Location;
import com.nut.teamradar.model.PhoneContact;
import com.nut.teamradar.model.ScreenInfo;
import com.nut.teamradar.model.User;
import com.nut.teamradar.util.Encrypt;
import com.nut.teamradar.util.LocationRecordWriter;

public class ApplicationData {
    private static final String TAG = "ApplicationData";
    private static final String SUB_FILE = "/sdcard/sub.ini";
    private static final String PASSWD_FILE = "/sdcard/passwd.ini";
    private static final String SUB = "/sdcard/Subscription";
    private static final String PASSWD = "/sdcard/Password";
    private static final String NAME_FILE = "/sdcard/name.ini";
    private static final String NAME = "name";

	private static ApplicationData _instanse=null;
	private ScreenInfo mScreenInfo;
	private User CurrentUser;
	
	private int CurrentUserId=0;
	private String CurrentUserName;
	private String Subscription;
	private LocationRecordWriter locRecWriter = new LocationRecordWriter();;
	private int UserMode;
	private long ActiveOwnerId;
	private long ActiveGroupId;
	private String ActiveGroupName;
	private String ActiveGroupOwnerSubscription;
	private String RequestUserName;
	private long    RequestUserId;
	private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
    private String sessionId=null;
    private String Mark = null;
    private Context applicationContext=null;
    private List<PhoneContact> Contacts = new ArrayList<PhoneContact> ();
    private ApplicationData(){
		mScreenInfo = new ScreenInfo();

		UserMode = Constant.WORK_MODE_ACTIVE;
		CurrentUserId=-1;
		ActiveOwnerId=-1;
		ActiveGroupId=-1;
    }
    public static ApplicationData getInstance() {
    	if(_instanse == null)
    		_instanse = new ApplicationData();
		return _instanse;
	}
    public void setContext(Context ctx)
    {
    	applicationContext = ctx;
    }
    public void putUserProfile(User user)
	{
		CurrentUser = new User(user);
	}
	public User getUserProfile()
	{
		return CurrentUser;
	}

	
    public void setScreenInfo(int width, int height, int sBarHeight) {
		mScreenInfo.height = height;
		mScreenInfo.width = width;
		mScreenInfo.statusBarHeight = sBarHeight;
	}

    public ScreenInfo getScreenInfo() {
		return mScreenInfo;
	}
	
    public void putContact(String name, String subscription) {
		Contacts.add(new PhoneContact(name,subscription));
	}

    public void deleteContact(String subscription) {
    	for(int i=0;i<Contacts.size();i++)
    	{
    		if(Contacts.get(i).SubScription.equals(subscription))
    		{
    			Contacts.remove(i);
    		}
    	}
	}

    public List<PhoneContact> getContacts() {
		return Contacts ;
	}
    
    public void setSessionId(String id) {
		sessionId = id;
	}

    public String getSessionId() {
		return sessionId;
	}
    
    public void setMark(String mark) {
		Mark = mark;
	}

    public String getMark() {
		return Mark;
	}
	
    public void setName(String name) {
		CurrentUserName = name;
	}

    public String getName() {
		return CurrentUserName;
	}

    public void setCurrentUserId(int userid) {
		CurrentUserId = userid;
	}

    public int getCurrentUserId() {
		return CurrentUserId;
	}

    public void setSubscription(String sub) {
		Subscription = sub;
	}

    public String getSubscription() {
		return Subscription;
	}
	
    // TODO: Debug Only ---
    public void saveUserName(String name) {
        writeInfo(NAME_FILE, NAME, Encrypt.GetEncryptString(name));
    }

    public String readUserName() {
        return Encrypt.GetDecryptString(readInfo(NAME_FILE, NAME));
    }

    public void saveSubscription(String sub) {
        writeInfo(SUB_FILE, SUB, Encrypt.GetEncryptString(sub));
    }

    public String readSubscription() {
        return Encrypt.GetDecryptString(readInfo(SUB_FILE, SUB));
    }

    public void savePassword(String passwd) {
        writeInfo(PASSWD_FILE, PASSWD, Encrypt.GetEncryptString(passwd));
    }

    public String readPassword() {
        return Encrypt.GetDecryptString(readInfo(PASSWD_FILE, PASSWD));
    }

    private void writeInfo(String fileName, String key, String info) {
        try {
        	String sdStatus = Environment.getExternalStorageState();  
            if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) { 
            	 Log.d(TAG, "SD card is not avaiable/writeable right now.");   
            }
            File file = new File(fileName);
            if( !file.exists()) {  
                file.createNewFile();  
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write((key + "=" + info).getBytes());
            fos.close();
        } catch (Exception e) {
            Log.e(TAG, "writeInfo()");
            Log.e(TAG, e.toString());
        }
    }

    private String readInfo(String fileName, String key) {
        String val = null;
        try {
        	String sdStatus = Environment.getExternalStorageState();  
            if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) { 
            	 Log.d(TAG, "SD card is not avaiable/writeable right now.");   
            }
            File file = new File(fileName);
            if( !file.exists()) {  
                file.createNewFile();  
            }  
            FileInputStream fis = new FileInputStream(file);
            byte[] info = new byte[fis.available()];
            fis.read(info);
            fis.close();
            val = getValue(new String(info), key);
        } catch (Exception e) {
            Log.e(TAG, "readInfo()");
            Log.e(TAG, e.toString());
        }
        return val;
    }

    private String getValue(String info, String key) {
        String[] p = info.split("=");
        if (p[0].compareTo(key) == 0) {
            return p[1];
        } else {
            return null;
        }
    }

    // ---

    public void setUserMode(int Mode) {
		UserMode = Mode;
	}

    public int getUserMode() {
		return UserMode;
	}

    
    public void setActiveOwnerId( long Id) 
    { 
    	ActiveOwnerId = Id; 
    } 
    public long getActiveOwnerId() 
    {
    	return ActiveOwnerId; 
    }
    
    public void setActiveGroupId(long Id) {
		ActiveGroupId = Id;
	}

    public long getActiveGroupId() {
		return ActiveGroupId;
	}
	
    public void beginWriteLocationRecord() {
        //locRecWriter.beginWrite(applicationContext);
    }
    public void writeLocationRecord(String sub, String name, Location loc) {
        locRecWriter.writeStringRecord(name, loc);
    }
    public void commitAllLocationRecords() {
        locRecWriter.endWrite();
    }
    
    public void setActiveGroupName(String name) 
    { 
    	ActiveGroupName = name; 
    }
    public String getActiveGroupName() 
    { 
    	return ActiveGroupName; 
    }
    public void setActiveGroupSubscription(String name) 
    { 
    	ActiveGroupOwnerSubscription = name; 
    }
    public String getActiveGroupSubscription() 
    { 
    	return ActiveGroupOwnerSubscription; 
    } 

}
