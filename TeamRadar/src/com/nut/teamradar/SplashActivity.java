package com.nut.teamradar;

import com.nut.teamradarlib.TeamRadarAPI;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class SplashActivity extends FragmentActivity {
	private static String TAG = "SplashActivity";
	private int START_FULLSCREEN_TIME = 2000;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TeamRadarAPI.getInstance().Init(Constant.magic, Constant.magic_len);
		TRServiceConnection.getInstance().Connect(getApplicationContext());
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		Log.d(TAG, "Login Activity onCreate");
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
 
	    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public class PlaceholderFragment extends Fragment {
		private int status = 0;
		public PlaceholderFragment() {
		}
		private static final int STOPSPLASH = 0; 
		  private Handler splashHandler = new Handler() { 
			    public void handleMessage(Message msg) {   
			         switch (msg.what) { 
			         case STOPSPLASH: 
			              if( status == 1 ){
			            	  Intent intent = new Intent(SplashActivity.this,
										LoginActivity.class);
			            	  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							  startActivity(intent);
							  SplashActivity.this.finish();
			                  break; 
			              } 
			              sendEmptyMessageDelayed(STOPSPLASH, START_FULLSCREEN_TIME); 
			              status = 1;
			         } 
			         super.handleMessage(msg); 
			    	

			        super.handleMessage(msg); 
			    } 
			}; 
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_splash,
					container, false);
			if(isAdded())
			{
				rootView.setBackground(this.getResources().getDrawable(R.drawable.splash));
			}
		    Message msg = new Message(); 
		    msg.what = STOPSPLASH; 
		    splashHandler.sendMessageDelayed(msg, START_FULLSCREEN_TIME);
			return rootView;
		}
	}

}
