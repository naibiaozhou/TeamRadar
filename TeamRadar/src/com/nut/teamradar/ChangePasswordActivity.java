package com.nut.teamradar;

import com.easemob.chat.EMGroupManager;
import com.nut.teamradar.base.BaseUi;
import com.nut.teamradar.model.User;
import com.nut.teamradar.util.Encrypt;
import com.nut.teamradar.util.SmsContent;
import com.nut.teamradar.webclient.ITRProfileListener;
import com.nut.teamradar.webclient.WebConnection;
import com.nut.teamradarlib.TeamRadarAPI;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;
import android.os.Handler.Callback;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class ChangePasswordActivity extends FragmentActivity {
	private static String TAG = "ChangePasswordActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_passwrod);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new ChangePasswordFragment()).commit();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.change_passwrod, menu);
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
	public static class ChangePasswordFragment extends BaseUi implements Callback{
    	private static final int UPDATA_VERIFYBUTTON  = 202;
    	private ProgressDialog mPrgsDlg=null;
		private EditText mSubscription;
		private EditText mPassword;
		private EditText mConfirmPassword;
		private EditText mVerifyCode;
		private Button mGetVerifyCode;
		private Button mSubmit;
		
		private String subscription;
		private String password;
		private String confirmpassword;
		private String verifycode;
		private View mRootView;
		private int RetryCounter=0;
		private boolean ExitFlag = false;
		private boolean ThreadRun = false;
		private ChangePasswordFragment mThis;
		private SmsContent mContent;
		public ChangePasswordFragment() {
		}
		private void initSDK() {
    		// 初始化短信SDK
    		SMSSDK.initSDK(this.getContext() , TeamRadarAPI.getInstance().getAppKey(), 
    				TeamRadarAPI.getInstance().getAppSecret(),true);
    		Log.e(TAG, String.format("key =%s secret=%s",TeamRadarAPI.getInstance().getAppKey(),TeamRadarAPI.getInstance().getAppSecret()));
    		final Handler handler = new Handler(this);
    		EventHandler eventHandler = new EventHandler() {
    			public void afterEvent(int event, int result, Object data) {
    				Message msg = new Message();
    				msg.arg1 = event;
    				msg.arg2 = result;
    				msg.obj = data;
    				handler.sendMessage(msg);
    			}
    		};
    		// 注册回调监听接口
    		SMSSDK.registerEventHandler(eventHandler);
    		//SMSSDK.getSupportedCountries();
    	}
		private ITRProfileListener.Stub mProfileListener = new ITRProfileListener.Stub() {
			
			@Override
			public void OnProfileUpdate(int flag) throws RemoteException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void OnPasswordUpdate(int flag) throws RemoteException {
				if(flag == WebConnection.SUCCESS)
				{
					String EnctyptSubscription = Encrypt.GetEncryptString(subscription);
					String EnctyptPassword = Encrypt.GetEncryptString(password);
					TRServiceConnection.getInstance().ChangeHXUserPassword(EnctyptSubscription,EnctyptPassword);
					Log.d(TAG, "start Change HX Password");

				}
				else
				{
					RetryCounter ++;
					if(RetryCounter < 20)
					{
						TRServiceConnection.getInstance().ChangePassword(subscription, password);
					}
					else
					{
						Toast t = Toast.makeText(mThis.getActivity(),
								getString(R.string.changepasswordfailed), Toast.LENGTH_LONG);
	                    t.setGravity(Gravity.CENTER, 0, 0);
	                    t.show();
		    			if (mPrgsDlg.isShowing()) {
		                    mPrgsDlg.dismiss();
		                }
		    			RetryCounter = 0;
	    			}
				}
				Log.d(TAG, "Change Password end");
				
			}
			
			@Override
			public void OnObtainProfile(User usr) throws RemoteException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void OnHXUserPasswordUpdate(int flag) throws RemoteException {
				if(flag == WebConnection.SUCCESS)
				{
					/*Toast t = Toast.makeText(mThis.getActivity(),
                            "Change password success", Toast.LENGTH_LONG);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();*/
                    ExitFlag = true;
                    mSubmit.setText(getString(R.string.changepasswordsuccessgotologin));
				}
				else
				{
					Toast t = Toast.makeText(mThis.getActivity(),
                            getString(R.string.changepasswordfailed), Toast.LENGTH_LONG);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();
				}
    			if (mPrgsDlg.isShowing()) {
                    mPrgsDlg.dismiss();
                }
    			Log.d(TAG, "Change HX Password end");
			}
		};
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_change_passwrod,
					container, false);
			mThis = this;
			RetryCounter = 0;
			initSDK();
			TRServiceConnection.getInstance().RegisterProfileListener(mProfileListener);
			mRootView = rootView;
			mSubscription =(EditText) rootView.findViewById(R.id.editCSubscription);
			mSubscription.setHint(rootView.getResources().getString(R.string.subneeded));
			mPassword =(EditText) rootView.findViewById(R.id.editCPassword);
			mPassword.setHint(rootView.getResources().getString(R.string.password1needed));
			mConfirmPassword =(EditText) rootView.findViewById(R.id.editCConfirmPwd);
			mConfirmPassword.setHint(rootView.getResources().getString(R.string.passwordneeded));
			mVerifyCode =(EditText) rootView.findViewById(R.id.edCVerCode1);
			mVerifyCode.setHint(rootView.getResources().getString(R.string.authneeded));
			mGetVerifyCode = (Button) rootView.findViewById(R.id.btnCVerifyCode);
			mSubmit = (Button) rootView.findViewById(R.id.btnCSubmit);
            mPrgsDlg = new ProgressDialog(mThis.getActivity());
            mPrgsDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mPrgsDlg.setTitle(mThis.getResources().getString(R.string.please_wait));
            mPrgsDlg.setMessage(mThis.getResources().getString(R.string.changingpasword));
            mPrgsDlg.setCanceledOnTouchOutside(false);
            VerfyCodeThread.start();
			mGetVerifyCode.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					if(checkRegistrationInfo())
					{
						GetVerificationCode(mSubscription.getText().toString());
						mGetVerifyCode.setEnabled(false);
						index = 0;
						ThreadRun = true;
						
					}
				}
            });
			
			mSubmit.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					if(ExitFlag)
					{
						mThis.getActivity().finish();
					}
					else
					{
						if(collectRegistrationInfo())
						{
							mPrgsDlg.show();
							//TRServiceConnection.getInstance().ChangePassword(subscription, password);
							VerifyCode(mSubscription.getText().toString(),verifycode);
						}
					}
				}
				
			});
	        mContent = new SmsContent(mThis.getActivity(), new Handler(), mVerifyCode);
	        mThis.getActivity().getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, mContent);
			return rootView;
		}
		private int index;
        private Thread VerfyCodeThread = new Thread(new Runnable(){
        	
			@Override
			public void run() {
				while(true)
				{
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if(ThreadRun)
					{
						index++;
						VerifyHandler.obtainMessage(UPDATA_VERIFYBUTTON).sendToTarget();
						if(index == 120)
						{
							ThreadRun = false;
						}
					}
				}
				
			}
        	
        });
        private Handler VerifyHandler = new Handler(){
        	public void handleMessage(Message msg){
    			super.handleMessage(msg);
    			switch(msg.what)
    			{
    			case UPDATA_VERIFYBUTTON:
    				if(isAdded())
    				{
	    				mGetVerifyCode.setText(String.format(getString(R.string.refetchverifycode), 120-index));
	    				if(index == 120)
	    				{
	    					mGetVerifyCode.setText(getString(R.string.fetchverifycode));
	    					mGetVerifyCode.setEnabled(true);
	    					index = 0;
	    				}
    				}
    				break;
    			}
    		}
        };
        
        private boolean checkRegistrationInfo() {
			boolean ret = true;
			subscription = mSubscription.getText().toString();
            if (subscription == null || subscription.isEmpty()
                    || !subscription.matches("[0-9]+")) {
            	mSubscription.setText("");
            	mSubscription.setHint(mRootView.getResources().getString(
                        R.string.subneeded));
            	mSubscription.setHintTextColor(Color.RED);
                ret = false;
            }
			password =  mPassword.getText().toString();
            if (password == null || password.isEmpty()) {
            	mPassword.setHint(mRootView.getResources().getString(
                        R.string.passwordneeded));
            	mPassword.setHintTextColor(Color.RED);
                ret = false;
            }
			confirmpassword = mConfirmPassword.getText().toString();
            if (confirmpassword == null || confirmpassword.isEmpty()) {
            	mConfirmPassword.setHint(mRootView.getResources().getString(
                        R.string.passwordneeded));
            	mConfirmPassword.setHintTextColor(Color.RED);
                ret = false;
            }else if (confirmpassword.compareTo(password) != 0) {
            	mConfirmPassword.setText("");
            	mConfirmPassword.setHint(mRootView.getResources().getString(
                        R.string.password1wrong));
            	mConfirmPassword.setHintTextColor(Color.RED);
                ret = false;
            }
            return ret;
        }
        
		private boolean collectRegistrationInfo() {
			boolean ret = true;
			subscription = mSubscription.getText().toString();
            if (subscription == null || subscription.isEmpty()
                    || !subscription.matches("[0-9]+")) {
            	mSubscription.setText("");
            	mSubscription.setHint(mRootView.getResources().getString(
                        R.string.subneeded));
            	mSubscription.setHintTextColor(Color.RED);
                ret = false;
            }
			password =  mPassword.getText().toString();
            if (password == null || password.isEmpty()) {
            	mPassword.setHint(mRootView.getResources().getString(
                        R.string.passwordneeded));
            	mPassword.setHintTextColor(Color.RED);
                ret = false;
            }
			confirmpassword = mConfirmPassword.getText().toString();
            if (confirmpassword == null || confirmpassword.isEmpty()) {
            	mConfirmPassword.setHint(mRootView.getResources().getString(
                        R.string.passwordneeded));
            	mConfirmPassword.setHintTextColor(Color.RED);
                ret = false;
            }else if (confirmpassword.compareTo(password) != 0) {
            	mConfirmPassword.setText("");
            	mConfirmPassword.setHint(mRootView.getResources().getString(
                        R.string.password1wrong));
            	mConfirmPassword.setHintTextColor(Color.RED);
                ret = false;
            }
			verifycode = mVerifyCode.getText().toString();
            if (verifycode == null || verifycode.isEmpty()) {
            	mVerifyCode.setText(null);
            	mVerifyCode.setHint(mRootView.getResources().getString(
                        R.string.authneeded));
            	mVerifyCode.setHighlightColor(Color.RED);
            	ret = false;
            }
            return ret;
        }
		@Override
    	public boolean handleMessage(Message msg) {

    		int event = msg.arg1;
    		int result = msg.arg2;
    		Object data = msg.obj;
    		Log.e("event", "event="+event);
    		if (result == SMSSDK.RESULT_COMPLETE) {
    			if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
    				Toast.makeText(this.getActivity().getApplicationContext(), getString(R.string.submitverifysuccess), Toast.LENGTH_SHORT).show();
    				new Thread(new Runnable() {
    					public void run() {
    						TRServiceConnection.getInstance().ChangePassword(subscription, password);
    						Log.d(TAG, "start Change Password");
    					}
    					}).start();
    			} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
    				Toast.makeText(this.getActivity().getApplicationContext(), getString(R.string.sendverifycodesuccess), Toast.LENGTH_SHORT).show();
    			}else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){//返回支持发送验证码的国家列表
    				Toast.makeText(this.getActivity().getApplicationContext(),getString(R.string.fetchcountrylistok), Toast.LENGTH_SHORT).show();
    			}
    		} else {
    			((Throwable) data).printStackTrace();
    		}
    		return false;
    	}
        private void GetVerificationCode(String subscription)
        {
        	SMSSDK.getVerificationCode("86",subscription);
        }
        private void VerifyCode(String subscription,String Code)
        {
        	SMSSDK.submitVerificationCode("86", subscription, Code);
        }
        @Override  
        public void onDestroyView() {  
        	SMSSDK.unregisterAllEventHandler();
        	TRServiceConnection.getInstance().RemoveProfileListener(mProfileListener);
        	mThis.getActivity().getContentResolver().unregisterContentObserver(mContent);
            super.onDestroyView();  
            
        }  
        @Override
        public void onDestroy() {
        	SMSSDK.unregisterAllEventHandler();
        	TRServiceConnection.getInstance().RemoveProfileListener(mProfileListener);
        	mThis.getActivity().getContentResolver().unregisterContentObserver(mContent);
            super.onDestroy();
            Log.d(TAG, "onDestroy()");
        }
	}
}
