package com.nut.teamradar;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.nut.teamradar.Fragments.FragmentStartup;
import com.nut.teamradar.util.Encrypt;
import com.nut.teamradar.util.PushNotification;
import com.nut.teamradar.util.RandomString;
import com.nut.teamradarlib.TeamRadarAPI;

public class LoginActivity extends FragmentActivity {
	private static String TAG = "LoginActivity";
	private WifiManager mWifiManager; 
	private ConnectivityManager mConnectivityManager; 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	TeamRadarAPI.getInstance().Init(Constant.magic, Constant.magic_len);
    	TRServiceConnection.getInstance().Connect(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "Login Activity onCreate");
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE); 
        mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE); 
        //if (savedInstanceState == null) 
        {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new FragmentStartup()).commit();
        }
        TeamRadarApplication.getInstance().addActivity(this);
        boolean wifiEnabled = mWifiManager.isWifiEnabled();
        boolean mobileEnabled = getMobileDataStatus();
        NetworkInfo netinfo = mConnectivityManager.getActiveNetworkInfo();
        if((!wifiEnabled && !mobileEnabled) || (netinfo == null) || (!netinfo.isConnected()))
        {
        	AlertDialog OperationDlg = new AlertDialog.Builder(this)
			.setTitle(getString(R.string.Network_error))
			.setItems(new String[] {getString(R.string.opennetworksetting),
					getString(R.string.ignoremessage)}, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog,int which) {
					switch(which)
					{
					case 0:
			        	Intent intent = new Intent(Settings.ACTION_SETTINGS);
			        	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
			        	startActivity(intent);
						break;
					default:
							break;
					}

					dialog.dismiss();
				}
			}).setNegativeButton(getString(R.string.cancel),null).show();

        }
        
    }
    @Override
    public void onBackPressed() {
    	TeamRadarApplication.getInstance().onTerminate();
    	super.onBackPressed();
    }
	@Override
	public void finish() {
	    super.finish();
	}
    private boolean getMobileDataStatus()  
    {  
        String methodName = "getMobileDataEnabled";  
        Class cmClass = mConnectivityManager.getClass();  
        Boolean isOpen = null;  
          
        try   
        {  
            Method method = cmClass.getMethod(methodName, null);  
  
            isOpen = (Boolean) method.invoke(mConnectivityManager, null);  
        }   
        catch (Exception e)   
        {  
            e.printStackTrace();  
        }  
        return isOpen;  
    }  
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
   
}
