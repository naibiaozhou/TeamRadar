package com.nut.teamradar;


import java.util.List;

import com.nut.teamradar.base.BaseUi;
import com.nut.teamradar.model.Group;
import com.nut.teamradar.model.Member;
import com.nut.teamradar.model.User;
import com.nut.teamradar.util.Encrypt;
import com.nut.teamradar.util.SmsContent;
import com.nut.teamradar.webclient.ITRConnectionListener;
import com.nut.teamradar.webclient.ITRGroupListener;
import com.nut.teamradar.webclient.WebConnection;
import com.nut.teamradarlib.TeamRadarAPI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegistrationActivity extends FragmentActivity {
	private static String TAG = "RegistrationActivity";
    public ProgressDialog mPrgsDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TeamRadarAPI.getInstance().Init(Constant.magic, Constant.magic_len);
        setContentView(R.layout.activity_registration);
        Log.d(TAG, "RegistrationActivity onCreate");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new RegistrationFragment()).commit();
        }
        TeamRadarApplication.getInstance().addActivity(this);
    }
    
    
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.registration, menu);
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
    @Override
    public void onBackPressed() {
    	TeamRadarApplication.getInstance().onTerminate();
    	super.onBackPressed();
    }
    public class RegistrationFragment extends BaseUi implements Callback,OnClickListener{
        private final static String TAG = "RegistrationFragment";
        private final static String FEMALE = "F";
        private final static String MALE = "M";
    	private static final int UPDATA_VERIFYBUTTON  = 1;
    	
        private View mRootView = null;
        private String mSubscription = null;
        private String mNickName = null;
        private String mGender = null;
        private float mWeight = -1.0f;
        private float mHeight = -1.0f;
        private String mEmail = null;
        private String mPassword = null;
        private String mCode = null;
        private String mEnctyptSubscription=null;
        private String mEnctyptPassword=null;
        // private String mVerifcationCode = null;

        private EditText mETSubscription = null;
        private EditText mETNickName = null;
        private RadioButton mRBMale = null;
        private RadioButton mRBFemale = null;
        private EditText mETWeight = null;
        private EditText mETHeight = null;
        private EditText mETEmail = null;
        private EditText mETPassword = null;
        private EditText mETPassword1 = null;
        private EditText mEtVerifyCode = null;
        private Button mBtGetVerifyCode = null;
        //private ImageView mImgVerify = null;
        private User usr;
        private SmsContent mContent;
        private boolean ThreadRun = false;
        private boolean ExitFlag=false;
        private Button mBtnSubmit = null;
        private RegistrationFragment mThis;
        public RegistrationFragment() {
        	
        }

        @Override
        public void onPause() {
            super.onPause();
            Log.d(TAG, "onPause()");
        }

        @Override
        public void onResume() {
            super.onResume();
            Log.d(TAG, "onResume()");
        }
        @Override  
        public void onDestroyView() {  
        	SMSSDK.unregisterAllEventHandler();
            TRServiceConnection.getInstance().RemoveConnectionListener(RegListener);
            TRServiceConnection.getInstance().RemoveGroupListener(mGroupListener);
            mThis.getActivity().getContentResolver().unregisterContentObserver(mContent);
            super.onDestroyView();  
            
        }   
        @Override
        public void onDestroy() {
        	SMSSDK.unregisterAllEventHandler();
            TRServiceConnection.getInstance().RemoveConnectionListener(RegListener);
            TRServiceConnection.getInstance().RemoveGroupListener(mGroupListener);
            mThis.getActivity().getContentResolver().unregisterContentObserver(mContent);
            super.onDestroy();
            Log.d(TAG, "onDestroy()");
        }
        private ITRConnectionListener.Stub RegListener = new ITRConnectionListener.Stub() {
    		
    		@Override
    		public void OnRregtistration(int Flag,int Id) throws RemoteException {
    			if(Flag == WebConnection.FAILURE)
    			{
                    Log.d(TAG, "Registration onFailure");
                    Toast t = Toast.makeText(mThis.getActivity(),
                            getString(R.string.registrationfailed), Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();
    			}
    			else
    			{
                    Log.d(TAG, "Registration success");
    			}
    			if (mPrgsDlg.isShowing()) {
                    mPrgsDlg.dismiss();
                }
    			ExitFlag = true;
    			mBtnSubmit.setText(getString(R.string.registrationsuccessgotologin));
    		}
    		
    		@Override
    		public void OnLogOut(int flag) throws RemoteException {
    			
    		}
    		
    		@Override
    		public void OnLogIn(int flag, int Id, String Name) throws RemoteException {
    		}
    	};
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_registration,
                    container, false);
            mThis = this;
            usr = new User();
            usr.setBirthday("1980-01-01");
            usr.setProfession("unKnown");
            mRootView = rootView;
            TRServiceConnection.getInstance().RegisterConnectionListener(RegListener);
            TRServiceConnection.getInstance().RegisterGroupListener(mGroupListener);
            initSDK();
            initWidgets();
            mContent = new SmsContent(RegistrationActivity.this, new Handler(), mEtVerifyCode);
            mThis.getActivity().getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, mContent);
            return rootView;
        }

        private View.OnClickListener mETOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.d(TAG, "EditText.onClick(): " + arg0.toString());
            }
        };

    	private void initSDK() {
    		// 初始化短信SDK
    		SMSSDK.initSDK(this.getContext(), TeamRadarAPI.getInstance().getAppKey(), TeamRadarAPI.getInstance().getAppSecret());
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
        private void initWidgets() {
            mETSubscription = (EditText) mRootView
                    .findViewById(R.id.etSubscription);
            mETSubscription.setHint(mRootView.getResources().getString(R.string.subneeded));
            mETNickName = (EditText) mRootView.findViewById(R.id.etNickName);
            mETNickName.setHint(mRootView.getResources().getString(R.string.nikeneeded));
            mRBMale = (RadioButton) mRootView.findViewById(R.id.rbMale);
            mRBMale.setChecked(true);
            mRBFemale = (RadioButton) mRootView.findViewById(R.id.rbFemale);
            mRBFemale.setChecked(false);
            mETWeight = (EditText) mRootView.findViewById(R.id.etWeight);
            mETWeight.setText("60");
            mETWeight.setHint(mRootView.getResources().getString(R.string.weightneeded));
            mETHeight = (EditText) mRootView.findViewById(R.id.etHeight);
            mETHeight.setText("180");
            mETHeight.setHint(mRootView.getResources().getString(R.string.heightneeded));
            mETEmail = (EditText) mRootView.findViewById(R.id.etMail);
            mETEmail.setText("abc@163.com");
            mETEmail.setHint(mRootView.getResources().getString(R.string.mailneeded));
            mETPassword = (EditText) mRootView.findViewById(R.id.etPassword);
            mETPassword.setHint(mRootView.getResources().getString(R.string.passwordneeded));
            mETPassword1 = (EditText) mRootView.findViewById(R.id.etPassword1);
            mETPassword1.setHint(mRootView.getResources().getString(R.string.password1needed));
            mEtVerifyCode = (EditText) mRootView.findViewById(R.id.edVerCode1);
            mEtVerifyCode.setHint(mRootView.getResources().getString(R.string.authneeded));
            mBtGetVerifyCode = (Button) mRootView.findViewById(R.id.btnVerifyCode);
            mBtnSubmit = (Button) mRootView.findViewById(R.id.btnSubmit);
            mBtnSubmit.setOnClickListener(this);
            mPrgsDlg = new ProgressDialog(mThis.getContext());
            mPrgsDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mPrgsDlg.setTitle(mThis.getResources().getString(R.string.please_wait));
            mPrgsDlg.setMessage(mThis.getResources().getString(R.string.registering));
            mPrgsDlg.setCanceledOnTouchOutside(false);
            VerfyCodeThread.start();
            mBtGetVerifyCode.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					if (checkRegistrationInfo()) {
						GetVerificationCode(mETSubscription.getText().toString());
						mBtGetVerifyCode.setEnabled(false);
						index = 0;
						ThreadRun = true;
					}
				}
            });
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
	    				mBtGetVerifyCode.setText(String.format(getString(R.string.refetchverifycode), 120-index));
	    				if(index == 120)
	    				{
	    					mBtGetVerifyCode.setText(getString(R.string.fetchverifycode));
	    					mBtGetVerifyCode.setEnabled(true);
	    					index = 0;
	    				}
    				}
    				break;
    			}
    		}
        };
        private void collectRegistrationInfo() {
            Log.d(TAG, "collectRegistrationInfo()");
            mNickName = mETNickName.getText().toString();
            mNickName = mNickName == null || mNickName.isEmpty() ? "dummy" : mNickName;
            usr.setName(mNickName);
            TRServiceConnection.getInstance().SaveUserName(mNickName);
            mGender = mRBMale.isChecked() ? MALE : FEMALE;
            usr.setGender(mGender);
            try {
                mWeight = Float.parseFloat(mETWeight.getText().toString());
                usr.setWeight((int)mWeight);
                mHeight = Float.parseFloat(mETHeight.getText().toString());
                usr.setHeight((int)mHeight);
            } catch (NumberFormatException e) {
                Log.e(TAG,
                        "collectRegistrationInfo(): invalid weight or height");
            }
            mEmail = mETEmail.getText().toString();
            usr.setMail(mEmail);
        }
        
        private ITRGroupListener.Stub mGroupListener = new ITRGroupListener.Stub()
        {

			@Override
			public void OnMemberUpdate(int flag, List<Member> Members)
					throws RemoteException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void OnDeleteMember(int Flag) throws RemoteException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void OnDeleteMemberFromActivity(int Flag)
					throws RemoteException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void OnDeleteActivity(int Flag) throws RemoteException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void OnCreateActivity(int Flag) throws RemoteException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void OnAddUserToActivity(int Flag) throws RemoteException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void OnHXUserCreated(int Flag) throws RemoteException {
				if(Flag == WebConnection.SUCCESS)
				{
					HxUserRetryCount = 0;
					TeamRadarApplication.getInstance().setUserName(mEnctyptSubscription);
					TRServiceConnection.getInstance().Registration(usr);
					Toast.makeText(getApplicationContext(), getString(R.string.registrationsucess), Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(getApplicationContext(), getString(R.string.registrationfailed), Toast.LENGTH_SHORT).show();
					HxUserRetryCount++;
					if(HxUserRetryCount < 20)
					{
	    				new Thread(new Runnable() {
	    					public void run() {
	    						registerHXUser();
	    					}
	    					}).start();
					}
					else
					{
		    			if (mPrgsDlg.isShowing()) {
		                    mPrgsDlg.dismiss();
		                }
					}
				}
				
			}

			@Override
			public void OnObtainActivity(int flag, List<Group> groups)
					throws RemoteException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void OnObtainActivityBySubscription(int flag,
					List<Group> groups) throws RemoteException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void OnSessionStart() throws RemoteException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void OnSessionStop() throws RemoteException {
				// TODO Auto-generated method stub
				
			}
        	
        };
        private int HxUserRetryCount = 0;
        private void registerHXUser()
        {
        	TRServiceConnection.getInstance().CreateHXUser(mEnctyptSubscription, mEnctyptPassword);
        }

        private boolean checkRegistrationInfo() {
            boolean ret = true;
            mSubscription = mETSubscription.getText().toString();
            mEnctyptSubscription = Encrypt.GetEncryptString(mSubscription);
            TRServiceConnection.getInstance().SetSubscription(mSubscription);
            // TODO: Debug Only
            TRServiceConnection.getInstance().SaveSubscription(mSubscription);
            usr.setSubscription(mSubscription);
            if (mSubscription == null || mSubscription.isEmpty()
                    || !mSubscription.matches("[0-9]+")) {
                mETSubscription.setText("");
                mETSubscription.setHint(mRootView.getResources().getString(
                        R.string.subneeded));
                mETSubscription.setHintTextColor(Color.RED);
                ret = false;
            }
            mPassword = mETPassword.getText().toString();
            mEnctyptPassword = Encrypt.GetEncryptString(mPassword);
            if (mPassword == null || mPassword.isEmpty()) {
                mETPassword.setHint(mRootView.getResources().getString(
                        R.string.passwordneeded));
                mETPassword.setHintTextColor(Color.RED);
                ret = false;
            }
            String passwd1 = mETPassword1.getText().toString();
            usr.setPassword(mPassword);
            if (passwd1 == null || passwd1.isEmpty()) {
                mETPassword1.setHint(mRootView.getResources().getString(
                        R.string.password1needed));
                mETPassword1.setHintTextColor(Color.RED);
                ret = false;
            } else if (mPassword.compareTo(passwd1) != 0) {
                mETPassword1.setText("");
                mETPassword1.setHint(mRootView.getResources().getString(
                        R.string.password1wrong));
                mETPassword1.setHintTextColor(Color.RED);
                ret = false;
            }
            // TODO: Debug Only
            TRServiceConnection.getInstance().SavePassword(mPassword);
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
    				Toast.makeText(getApplicationContext(), getString(R.string.submitverifysuccess), Toast.LENGTH_SHORT).show();
    				HxUserRetryCount = 0;
    				new Thread(new Runnable() {
    					public void run() {
    						registerHXUser();
    					}
    					}).start();
    			} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
    				Toast.makeText(getApplicationContext(),  getString(R.string.sendverifycodesuccess), Toast.LENGTH_SHORT).show();
    			}else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){//返回支持发送验证码的国家列表
    				Toast.makeText(getApplicationContext(), getString(R.string.fetchcountrylistok), Toast.LENGTH_SHORT).show();
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
    	public void onClick(View v) {
    		if (v.getId() == mBtnSubmit.getId()) {
            	if(ExitFlag)
            	{
                    Intent mIntent = new Intent(mThis.getActivity(),
                    		LoginActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
                    mThis.getActivity().startActivity(mIntent);
                    mThis.getActivity().finish();
            	}
            	else
            	{
                    /*final Intent mIntent = new Intent(mThis.getActivity(),
                    		LoginActivity.class);
                    mThis.getActivity().startActivity(mIntent);
                    mThis.getActivity().finish();*/
                    //if(false)
                    {
	                    Log.d(TAG, "Submit button clicked");
	                    if (checkRegistrationInfo()) {
	                        collectRegistrationInfo();
	                        mPrgsDlg.show();
	        				/*new Thread(new Runnable() {
	        					public void run() {
	        						registerHXUser();
	        					}
	        					}).start();*/
	                        VerifyCode(mETSubscription.getText().toString(),mEtVerifyCode.getText().toString());
	                        
	                    }
                    }
            	}
                // } else if (arg0.getId() == mBtnCancel.getId()) {
                // Log.d(TAG, "Cancel button clicked");
            } else {
                Log.e(TAG, "Unknown button clicked");
            }
    		
    	}
    }
	

}
