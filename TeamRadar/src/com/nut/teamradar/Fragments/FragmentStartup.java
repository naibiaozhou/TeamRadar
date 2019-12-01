package com.nut.teamradar.Fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.nut.teamradar.ChangePasswordActivity;
import com.nut.teamradar.Constant;
import com.nut.teamradar.MainActivity;
import com.nut.teamradar.R;
import com.nut.teamradar.RegistrationActivity;
import com.nut.teamradar.TRServiceConnection;
import com.nut.teamradar.TeamRadarApplication;
import com.nut.teamradar.base.BaseUi;
import com.nut.teamradar.db.UserDao;
import com.nut.teamradar.domain.HXUser;
import com.nut.teamradar.util.Code;
import com.nut.teamradar.util.Encrypt;
import com.nut.teamradar.util.RandomString;
import com.nut.teamradar.webclient.ITRConnectionListener;
import com.nut.teamradar.webclient.WebConnection;
import com.nut.teamradarlib.TeamRadarAPI;

public class FragmentStartup extends BaseUi {
    private static final String TAG = "StartupFragment";
    private RadioButton mRBLocalSrv = null;
    private RadioButton mRBRemoteSrv = null;
    //private RadioGroup mRGServers=null;
    private TextView mLoginTxt = null;
    private TextView mFogetPasswordTxt = null;

    private EditText etSub = null;
    private EditText etPassword = null;
    private EditText etVerifyCode = null;
    private ImageView imgVerifyCode = null;
    private static String subscription = "";
    private String EncryptSubscription = "";
    private String password = null;
    private String EncryptPassword = null;
    private Button btnRegister;
    private Button btnLogin;
    private TextView btnUpdate;
    private TextView btnHelp;
    private FragmentStartup mThis;
    private View rootView = null;
    private ProgressDialog mPrgsDlg = null;
    private String mCode;
    private int RetryTimes=0;
    
    
    

    public FragmentStartup() {
    }

    public static String getSubscription() {
        return subscription;
    }
	private void loginHX()
	{
		
		EMChatManager.getInstance().login(EncryptSubscription, EncryptPassword, new EMCallBack() {

			@Override
			public void onSuccess() {
				
				// 登陆成功，保存用户名密码
				TeamRadarApplication.getInstance().setUserName(EncryptSubscription);
				TeamRadarApplication.getInstance().setPassword(EncryptPassword);
				try {
					// ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
					// ** manually load all local groups and
					// conversations in case we are auto login
					EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
					//处理好友和群组
					processContactsAndGroups();
				} catch (Exception e) {
					e.printStackTrace();
					//取好友或者群聊失败，不让进入主页面
					mThis.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            TeamRadarApplication.getInstance().logout(null);
                            Toast.makeText(mThis.getActivity().getApplicationContext(), R.string.login_failure_failed, 1).show();
                        }
                    });
		            if (mPrgsDlg.isShowing()) {
		                mPrgsDlg.dismiss();
		            }
					return;
				}
				//更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
				boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(TeamRadarApplication.currentUserNick.trim());
				if (!updatenick) {
					Log.e("LoginActivity", "update current user nick fail");
				}
				// 进入主页面
                final Intent mIntent = new Intent(mThis
                        .getActivity(), MainActivity.class);
                mThis.getActivity().startActivity(mIntent);
                mThis.getActivity().finish();
                TeamRadarApplication.getInstance().setLoginState(true);
                Log.d(TAG, "Login success" );
                if (mPrgsDlg.isShowing()) {
                    mPrgsDlg.dismiss();
                }

			}

           

			@Override
			public void onProgress(int progress, String status) {
			}

			@Override
			public void onError(final int code, final String message) {
				TeamRadarApplication.getInstance().setLoginState(false);
				mThis.getActivity().runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(mThis.getActivity().getApplicationContext(), getString(R.string.Login_failed) + message, Toast.LENGTH_SHORT).show();
					}
				});
	            if (mPrgsDlg.isShowing()) {
	                mPrgsDlg.dismiss();
	            }
			}
		});
	}
	private void processContactsAndGroups() throws EaseMobException {
        // demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
        List<String> usernames = EMContactManager.getInstance().getContactUserNames();
        EMLog.d("roster", "contacts size: " + usernames.size());
        Map<String, HXUser> userlist = new HashMap<String, HXUser>();
        for (String username : usernames) {
        	HXUser user = new HXUser();
            user.setUsername(username);
            setUserHearder(username, user);
            userlist.put(username, user);
        }
        // 添加user"申请与通知"
        HXUser newFriends = new HXUser();
        newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
        String strChat = getResources().getString(R.string.Application_and_notify);
        newFriends.setNick(strChat);
        
        userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
        // 添加"群聊"
        HXUser groupUser = new HXUser();
        String strGroup = getResources().getString(R.string.group_chat);
        groupUser.setUsername(Constant.GROUP_USERNAME);
        groupUser.setNick(strGroup);
        groupUser.setHeader("");
        userlist.put(Constant.GROUP_USERNAME, groupUser);

        // 存入内存
        TeamRadarApplication.getInstance().setContactList(userlist);
        System.out.println("----------------"+userlist.values().toString());
        // 存入db
        UserDao dao = new UserDao(mThis.getActivity());
        List<HXUser> users = new ArrayList<HXUser>(userlist.values());
        dao.saveContactList(users);
        
        //获取黑名单列表
        List<String> blackList = EMContactManager.getInstance().getBlackListUsernamesFromServer();
        //保存黑名单
        EMContactManager.getInstance().saveBlackList(blackList);

        // 获取群聊列表(群聊里只有groupid和groupname等简单信息，不包含members),sdk会把群组存入到内存和db中
        EMGroupManager.getInstance().getGroupsFromServer();
    }
	protected void setUserHearder(String username, HXUser user) {
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
	}
    private View.OnClickListener mBtnOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO: Debug Only
        	//mRGServers.setEnabled(false);
            if (arg0.getId() == btnRegister.getId()) {
                Log.d(TAG, "Register button clicked");
                final Intent mIntent = new Intent(mThis.getActivity(),
                        RegistrationActivity.class);
                mThis.getActivity().startActivity(mIntent);
            } else if (arg0.getId() == btnLogin.getId()) {
                Log.d(TAG, "Login button clicked");
                
                if (checkLoginInfo()) {
                	mPrgsDlg.show();
                	RetryTimes = 0;
                	TRServiceConnection.getInstance().SetSubscription(subscription);
                	TRServiceConnection.getInstance().SaveSubscription(subscription);
                	TRServiceConnection.getInstance().SavePassword(password);
                	TRServiceConnection.getInstance().Login(subscription, password);
                }
            }
        }
    };
    
    private ITRConnectionListener.Stub LogInListener = new ITRConnectionListener.Stub(){

		@Override
		public void OnLogIn(int flag, int Id, String Name)
				throws RemoteException {
			if(flag == WebConnection.FAILURE)
			{               
                RetryTimes++;
                if(RetryTimes <= 20)
                {
                	TRServiceConnection.getInstance().Login(subscription, password );
                    /*Toast t = Toast.makeText(mThis.getActivity(),
                            "Login Failed Retry Again.", Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();*/
                }
                else
                {
	                
                	//mRGServers.setEnabled(true);
	                if (mPrgsDlg.isShowing()) {
	                    mPrgsDlg.dismiss();
	                }
	                TeamRadarApplication.getInstance().setLoginState(false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(mThis.getContext());
                    builder.setTitle(getString(R.string.loginfailtitle));
                    builder.setMessage(getString(R.string.loginfailmsg));
                    builder.show();
                    RetryTimes=0;

                }
			}
			else
			{
				TRServiceConnection.getInstance().SetCurrentUserId(Id);
				TRServiceConnection.getInstance().SetName(Name);
				TRServiceConnection.getInstance().SaveUserName(Name);
            	//EMChatManager.getInstance().updateCurrentUserNick(Name);
            	loginHX();
			}
			
		}

		@Override
		public void OnLogOut(int flag) throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void OnRregtistration(int Flag, int id) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
    	
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	TRServiceConnection.getInstance().Connect(this.getActivity().getApplicationContext());
        View rv = inflater.inflate(R.layout.fragment_login, container, false);
        rootView = rv;
        mThis = this;
        TRServiceConnection.getInstance().ConnectToServer(TeamRadarAPI.getInstance().getAddr(), Integer.valueOf(TeamRadarAPI.getInstance().getPort()), R.raw.client);
        initWidgets();
        RetryTimes = 0;
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				TRServiceConnection.getInstance().RegisterConnectionListener(LogInListener);
				TRServiceConnection.getInstance().SetSessionId(RandomString.getRandomString());
			}
		}, 800);
        
        return rootView;
    }
    private int VerCodeCount = 0;
    private void initWidgets() {
    	mLoginTxt = (TextView) rootView.findViewById(R.id.txtLogin);
        btnRegister = (Button) rootView.findViewById(R.id.btnRegister);
        btnLogin = (Button) rootView.findViewById(R.id.btnLogin);
        btnUpdate = (TextView) rootView.findViewById(R.id.btnUpdate);
        btnHelp = (TextView) rootView.findViewById(R.id.btnHelp);
        etSub = (EditText) rootView.findViewById(R.id.etLoginSubscription);
        mFogetPasswordTxt = (TextView) rootView.findViewById(R.id.txtForgetPassword);
        // TODO: Debug Only
        //mRGServers = (RadioGroup) rootView.findViewById(R.id.radioGroupSrv1);
        //mRBLocalSrv = (RadioButton) rootView.findViewById(R.id.rbSrvLocal);
        //mRBRemoteSrv = (RadioButton) rootView.findViewById(R.id.rbSrvRemote);
        subscription = TRServiceConnection.getInstance().ReadSubscription();

        etSub.setText(subscription != null ? subscription : "13800000000");
        etPassword = (EditText) rootView.findViewById(R.id.etLoginPassword);
        // TODO: Debug Only
        password = TRServiceConnection.getInstance().ReadPassword();

        etPassword.setText(password != null ? password : "1234567");
        etVerifyCode = (EditText) rootView.findViewById(R.id.etVerificationcode);
        etVerifyCode.setHint(etVerifyCode.getResources().getString(R.string.authneeded));
        imgVerifyCode = (ImageView) rootView.findViewById(R.id.imageAuth);
        btnRegister.setOnClickListener(mBtnOnClickListener);
        btnLogin.setOnClickListener(mBtnOnClickListener);
        btnUpdate.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(Constant.URL_APP));
				startActivity(intent);
			}
        	
        });
        btnHelp.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(Constant.URL_WEB));
				startActivity(intent);

				
			}
        	
        });        
        imgVerifyCode.setImageBitmap(Code.getInstance().createBitmap());
        //etVerifyCode.setText(Code.getInstance().getCode());
        imgVerifyCode.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				VerCodeCount++;
				imgVerifyCode.setImageBitmap(Code.getInstance().createBitmap());
				if(VerCodeCount > 3)
				{
					etVerifyCode.setText(Code.getInstance().getCode());
				}
			}
        });

        mPrgsDlg = new ProgressDialog(mThis.getContext());
        mPrgsDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mPrgsDlg.setTitle(mThis.getResources().getString(R.string.please_wait));
        mPrgsDlg.setMessage(mThis.getResources().getString(R.string.logining));
        mPrgsDlg.setCanceledOnTouchOutside(false);
        /*mRGServers.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				switch(arg1)
				{
				case R.id.rbSrvLocal:
					SERVER_PORT_SAFE = 8443;
					SERVER_ADDR = getString(R.string.local_server);
					TRServiceConnection.getInstance().ConnectToServer(SERVER_ADDR, SERVER_PORT_SAFE, R.raw.clientloc);
					break;
				case R.id.rbSrvLocalSafe:
					SERVER_PORT_SAFE = 8443;
					SERVER_ADDR = getString(R.string.local_server_safe);
					TRServiceConnection.getInstance().ConnectToServer(SERVER_ADDR, SERVER_PORT_SAFE, R.raw.clientloc);
					break;
				case R.id.rbSrvRemote:
					SERVER_PORT_SAFE = 38270;
					SERVER_ADDR = getString(R.string.remote_server);
					TRServiceConnection.getInstance().ConnectToServer(SERVER_ADDR, SERVER_PORT_SAFE, R.raw.client);
					break;
				case R.id.rbSrvRemoteSafe:
					SERVER_PORT_SAFE = 42060;
					SERVER_ADDR = getString(R.string.remote_server_safe);
					TRServiceConnection.getInstance().ConnectToServer(SERVER_ADDR, SERVER_PORT_SAFE, R.raw.client);
					break;
				}				
			}
        });*/
        
        mLoginTxt.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View arg0) {
				if(checkLoginInfo())
				{
					TeamRadarAPI.getInstance().Init(Constant.magic, Constant.magic_len);
					Log.d(TAG, TeamRadarAPI.getInstance().getAddr());
					Log.d(TAG, TeamRadarAPI.getInstance().getPort());
					Log.d(TAG, TeamRadarAPI.getInstance().getAppKey());
					Log.d(TAG, TeamRadarAPI.getInstance().getAppSecret());
					Log.d(TAG, TeamRadarAPI.getInstance().getAlias());
					Log.d(TAG, TeamRadarAPI.getInstance().getStorepass());
					Log.d(TAG, TeamRadarAPI.getInstance().getSecurityCode());
					
				}
				return false;
			}
        	
        });
        mFogetPasswordTxt.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
                final Intent mIntent = new Intent(mThis.getActivity(),
                		ChangePasswordActivity.class);
                mThis.getActivity().startActivity(mIntent);
			}
        	
        });
    }
    private boolean checkLoginInfo() {
        boolean ret = true;
        subscription = etSub.getText().toString();
        if (subscription == null || subscription.isEmpty()
                || !subscription.matches("[0-9]+")) {
            etSub.setText("");
            etSub.setHint(rootView.getResources().getString(R.string.subneeded));
            etSub.setHintTextColor(Color.RED);
            ret = false;
        }
        EncryptSubscription = Encrypt.GetEncryptString(subscription);
        
        Log.e(TAG, String.format("phone %s enc %s", subscription ,EncryptSubscription));
        
        password = etPassword.getText().toString();
        if (password == null || password.isEmpty()) {
            etPassword.setHint(etPassword.getResources().getString(
                    R.string.passwordneeded));
            etPassword.setHintTextColor(Color.RED);
            ret = false;
        }
        EncryptPassword = Encrypt.GetEncryptString(password);
        mCode = etVerifyCode.getText().toString();
        if(mCode == null || mCode.isEmpty())
        {
        	etVerifyCode.setText(null);
        	etVerifyCode.setHint(etVerifyCode.getResources().getString(R.string.authneeded));
        	etVerifyCode.setHighlightColor(Color.RED);
        	ret = false;
        }else if(!mCode.toLowerCase().equals(Code.getInstance().getCode().toLowerCase()))
        {
        	etVerifyCode.setText(null);
        	etVerifyCode.setHint(etVerifyCode.getResources().getString(R.string.autherror));
        	etVerifyCode.setHighlightColor(Color.RED); 
            ret = false;
        }
        return ret;
    }
    @Override  
    public void onDestroyView() {  
    	TRServiceConnection.getInstance().RemoveConnectionListener(LogInListener);
        super.onDestroyView();  
        
    }  
    @Override  
    public void onDestroy() { 
    	TRServiceConnection.getInstance().RemoveConnectionListener(LogInListener);
        super.onDestroy();  
        Log.d(TAG, "onDestroy()");
    } 
}