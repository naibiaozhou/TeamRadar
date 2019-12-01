package com.nut.teamradar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import android.content.Context;
import android.util.Log;

public class Parameters {
	public static final String TAG = "Parameters";
	public static final String SETTINGS_FILE = "settings.xml";
	static private Parameters _instance = null;
	private Properties mProperties = null;
	private Context mContext =null;
	private String FilePath = null;
	private File ParameterFile=null;
	private Parameters()
	{
		mProperties = new Properties();
	}
	public static Parameters instance()
	{
		if(_instance == null)
		{
			_instance = new Parameters();
		}	
		return _instance;
	}
	public void setContext(Context ctx)
	{
		mContext = ctx;
		FilePath = mContext.getFilesDir().toString();
		ParameterFile = new File(FilePath,SETTINGS_FILE);
	}
	public void setItem(String Key,String Value)
	{
		mProperties.setProperty(Key, Value);
	}
	private String getDefaultValue(String Key)
	{
		if(Key.equals(Constant.KEY_USER_NAME))
		{
			return "temp";
		}
		if(Key.equals(Constant.KEY_EMAIL))
		{
			return "znb211@163.com";
		}
		if(Key.equals(Constant.KEY_WEIGHT))
		{
			return "65";
		}
		if(Key.equals(Constant.KEY_WEIGHT))
		{
			return "167";
		}
		if(Key.equals(Constant.KEY_BIRTHDAY))
		{
			return "1982-01-01";
		}
		if(Key.equals(Constant.KEY_OCCUPATION))
		{
			return "Engineer";
		}
		if(Key.equals(Constant.KEY_PASSWORD))
		{
			return "123456";
		}
		if(Key.equals(Constant.KEY_GENDER))
		{
			return "Male";
		}
		if(Key.equals(Constant.KEY_MESSAGE_SETTINGS))
		{
			return "Sound";
		}	
		return null;
	}

	public String getItem(String Key)
	{
		String ret =  mProperties.getProperty(Key,getDefaultValue(Key));
		if(ret == null)
		{
			ret = getDefaultValue(Key);
		}
		return ret;
	}
	public Properties loadParameters() {
        try {
            FileInputStream fIS = new FileInputStream(ParameterFile);
            mProperties.loadFromXML(fIS);
            fIS.close();
            return mProperties;
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString());
            return null;
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }
	public void saveParameters() {
        try {
            FileOutputStream fOS = new FileOutputStream(ParameterFile);
            mProperties.storeToXML(fOS, "Parameters");
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }
}
