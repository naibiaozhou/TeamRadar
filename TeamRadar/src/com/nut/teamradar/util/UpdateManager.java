package com.nut.teamradar.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.nut.teamradar.Constant;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.DLTaskListener;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class UpdateManager {

	private String curVersion;
	private String newVersion;
	private int curVersionCode;
	private int newVersionCode;
	private String updateInfo;
	private UpdateCallback callback;
	private Context ctx;
    
	private int mProgress;  
	private Boolean hasNewVersion;
	private Boolean canceled;

	public static final String UPDATE_APKNAME = "teamradar.apk";
	public static final String UPDATE_SAVENAME = "teamradar.apk";
	public static final String UPDATE_VERSIONTXTNAME = "version.txt";
	public static final String UPDATE_VERSIONTXSAVENAME = "version.txt";
	
	private static final int UPDATE_CHECKCOMPLETED = 1;
	private static final int UPDATE_DOWNLOADING = 2; 
	private static final int UPDATE_DOWNLOAD_ERROR = 3; 
	private static final int UPDATE_DOWNLOAD_COMPLETED = 4; 
	private static final int UPDATE_DOWNLOAD_CANCELED = 5;

	 private String savefolder = "/mnt/innerDisk/";
	public UpdateManager(Context context, UpdateCallback updateCallback) {
		ctx = context;
		callback = updateCallback;
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			savefolder = TeamradarUtil.getSdCacheDir(this.ctx);
		}
		
		canceled = false;
		getCurVersion();
	}
	
	public String getNewVersionName()
	{
		return newVersion;
	}
	
	public String getUpdateInfo()
	{
		return updateInfo;
	}

	private void getCurVersion() {
		try {
			PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(
					ctx.getPackageName(), 0);
			curVersion = pInfo.versionName;
			curVersionCode = pInfo.versionCode;
		} catch (NameNotFoundException e) {
			Log.e("update", e.getMessage());
			curVersion = "1.1.1000";
			curVersionCode = 111000;
		}

	}

	public void checkUpdate() {		
		hasNewVersion = false;
		new Thread(){
			@Override
			public void run() {
				
				File ApkFile = new File(savefolder,UPDATE_VERSIONTXSAVENAME);
                if(ApkFile.exists())
                {
                	ApkFile.delete();
                }

                DLManager.getInstance(ctx).dlStart(Constant.URL_VERSION, savefolder, new DLTaskListener() {
                    @Override
                    public void onProgress(int progress) {
                    }
                    @Override
                    public void onError(String error) {
                    }
                    @Override
                    public void onFinish(File file) {
                    	try {
                    		FileInputStream fis = new FileInputStream(file);
                    		int length = fis.available();
                    		byte [] buffer = new byte[length];   
                            fis.read(buffer);       
                            String verjson = EncodingUtils.getString(buffer, "UTF-8"); 
        					if(verjson != null)
        					{
        						JSONArray array = new JSONArray(verjson);
        	
        						if (array.length() > 0) {
        							JSONObject obj = array.getJSONObject(0);
        							try {
        								newVersionCode = Integer.parseInt(obj.getString("verCode"));
        								newVersion = obj.getString("verName");
        								updateInfo = "";
        								if (newVersionCode > curVersionCode) {
        									hasNewVersion = true;
        								}
        							} catch (Exception e) {
        								newVersionCode = -1;
        								newVersion = "";
        								updateInfo = "";
        							}
        						}
        						updateHandler.sendEmptyMessage(UPDATE_CHECKCOMPLETED);
        					}
        				} catch (Exception e) {
        					e.printStackTrace();
        				}
                    }
                });
			};
		}.start();

	}

	public void update() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		
		intent.setDataAndType(
				Uri.fromFile(new File(savefolder, UPDATE_SAVENAME)),
				"application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(intent);
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	public void downloadPackage() 
	{
		
		
		new Thread() {			
			 @Override  
		        public void run() {  
	                File ApkFile = new File(savefolder,UPDATE_SAVENAME);
	                if(ApkFile.exists())
	                {
	                	ApkFile.delete();
	                }

	                DLManager.getInstance(ctx).dlStart(Constant.URL_APP, savefolder, new DLTaskListener() {
                        @Override
                        public void onProgress(int progress) {
                        	mProgress = progress;
                        	updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOADING)); 
                        	Log.d("UpdateManager", String.format("Download Process %d", mProgress));
                        	if(canceled)
                        	{
                        		DLManager.getInstance(ctx).dlCancel(Constant.URL_APP);
                        	}
                        }
                        @Override
                        public void onError(String error) {
                        	updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOAD_ERROR,error));
                        }
                        @Override
                        public void onFinish(File file) {
                        	updateHandler.sendEmptyMessage(UPDATE_DOWNLOAD_COMPLETED);
                        	Log.d("UpdateManager", "DownLoad Complate");
                        }
                    });
		        } 
		}.start();
	}

	public void cancelDownload()
	{
		canceled = true;
	}
	
	Handler updateHandler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case UPDATE_CHECKCOMPLETED:
				
				callback.checkUpdateCompleted(hasNewVersion, newVersion);
				break;
			case UPDATE_DOWNLOADING:
				
				callback.downloadProgressChanged(mProgress);
				break;
			case UPDATE_DOWNLOAD_ERROR:
				
				callback.downloadCompleted(false, msg.obj.toString());
				break;
			case UPDATE_DOWNLOAD_COMPLETED:
				
				callback.downloadCompleted(true, "");
				break;
			case UPDATE_DOWNLOAD_CANCELED:
				
				callback.downloadCanceled();
			default:
				break;
			}
		}
	};

	public interface UpdateCallback {
		public void checkUpdateCompleted(Boolean hasUpdate,
				CharSequence updateInfo);

		public void downloadProgressChanged(int progress);
		public void downloadCanceled();
		public void downloadCompleted(Boolean sucess, CharSequence errorMsg);
	}

}
