package com.nut.teamradar;


import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.nut.teamradar.controller.HXSDKHelper;
import com.nut.teamradarlib.TeamRadarAPI;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

public class TRSettingsActivity extends Activity {
	private FragmentManager mFragmentManater;
	private FragmentTransaction  mFragmentTrans;
	private PlaceholderFragment mFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TeamRadarAPI.getInstance().Init(Constant.magic, Constant.magic_len);
		setContentView(R.layout.activity_teamradarsettings);
		mFragment = new PlaceholderFragment();
		mFragmentManater = getFragmentManager();
		mFragmentTrans = mFragmentManater.beginTransaction();
		mFragmentTrans.replace(R.id.container, mFragment).addToBackStack(null).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.trsettings, menu);
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
    	this.finish();
    }

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends PreferenceFragment {
		private View mView;
		private EditTextPreference mUserName = null;
		private EditTextPreference mEmail = null;
		private EditTextPreference mWeight = null;
		private EditTextPreference mHeight = null;
		private EditTextPreference mBirthday = null;
		private EditTextPreference mOccupation = null;
		private EditTextPreference mOldPassWord = null;
		private EditTextPreference mNewPassword = null;
		private EditTextPreference mOfflineMap = null;
		private ListPreference mGender = null;
		private ListPreference mMessageSettings= null;
		PlaceholderFragment mThis = null;
		
		public PlaceholderFragment() {
		}

		@Override
	     public void onCreate(Bundle savedInstanceState) {
	          super.onCreate(savedInstanceState);

	          addPreferencesFromResource(R.xml.settings);
	          mUserName = (EditTextPreference)findPreference("EditPreferenceUserName");
	          mEmail = (EditTextPreference)findPreference("EditPreferenceEmail");
	          mWeight = (EditTextPreference)findPreference("EditPreferenceWeight");
	          mHeight = (EditTextPreference)findPreference("EditPreferenceHeight");
	          mBirthday = (EditTextPreference)findPreference("EditPreferenceBirthday");
	          mOccupation = (EditTextPreference)findPreference("EditPreferenceOccupation");
	          mOldPassWord = (EditTextPreference)findPreference("EditPreferenceOldPassword");
	          mNewPassword = (EditTextPreference)findPreference("EditPreferenceNewPassword");
	          mOfflineMap = (EditTextPreference)findPreference("EditPreferenceOfflineMap");
	          mGender = (ListPreference)findPreference("ListPreferenceGender");
	          mMessageSettings = (ListPreference)findPreference("ListPreferenceMessageSetting");
		}
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			mView= super.onCreateView(inflater, container, savedInstanceState);
			mThis = this;
			mUserName.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

				@Override
				public boolean onPreferenceChange(Preference preference,
						Object newValue) {
					Parameters.instance().setItem(Constant.KEY_USER_NAME,(String)newValue);
					mUserName.setSummary((String)newValue);
					mUserName.setText((String)newValue);
					TRServiceConnection.getInstance().UpdateProfile(Constant.KEY_USER_NAME,(String)newValue);
					return true;
				}
			});
			mEmail.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

				@Override
				public boolean onPreferenceChange(Preference preference,
						Object newValue) {
					Parameters.instance().setItem(Constant.KEY_EMAIL,(String)newValue);
					mEmail.setSummary((String)newValue);
					mEmail.setText((String)newValue);
					TRServiceConnection.getInstance().UpdateProfile(Constant.KEY_EMAIL,(String)newValue);
					return true;
				}
			});
			mWeight.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

				@Override
				public boolean onPreferenceChange(Preference preference,
						Object newValue) {
					Parameters.instance().setItem(Constant.KEY_WEIGHT,(String)newValue);
					mWeight.setSummary((String)newValue);
					mWeight.setText((String)newValue);
					TRServiceConnection.getInstance().UpdateProfile(Constant.KEY_WEIGHT,(String)newValue);
					return true;
				}
			});
			mHeight.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

				@Override
				public boolean onPreferenceChange(Preference preference,
						Object newValue) {
					Parameters.instance().setItem(Constant.KEY_HEIGHT,(String)newValue);
					mHeight.setSummary((String)newValue);
					mHeight.setText((String)newValue);
					TRServiceConnection.getInstance().UpdateProfile(Constant.KEY_HEIGHT,(String)newValue);
					return true;
				}
			});
			mBirthday.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

				@Override
				public boolean onPreferenceChange(Preference preference,
						Object newValue) {
					Parameters.instance().setItem(Constant.KEY_BIRTHDAY,(String)newValue);
					mBirthday.setSummary((String)newValue);
					mBirthday.setText((String)newValue);
					TRServiceConnection.getInstance().UpdateProfile(Constant.KEY_BIRTHDAY,(String)newValue);
					return true;
				}
			});
			mOccupation.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

				@Override
				public boolean onPreferenceChange(Preference preference,
						Object newValue) {
					Parameters.instance().setItem(Constant.KEY_OCCUPATION,(String)newValue);
					mOccupation.setSummary((String)newValue);
					mOccupation.setText((String)newValue);
					TRServiceConnection.getInstance().UpdateProfile(Constant.KEY_OCCUPATION,(String)newValue);
					return true;
				}
			});
			mOldPassWord.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

				@Override
				public boolean onPreferenceChange(Preference preference,
						Object newValue) {
					mOldPassWord.setSummary((String)newValue);
					mOldPassWord.setText((String)newValue);
					return true;
				}
			});
			mNewPassword.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

				@Override
				public boolean onPreferenceChange(Preference preference,
						Object newValue) {
					mNewPassword.setSummary((String)newValue);
					mNewPassword.setText((String)newValue);
					TRServiceConnection.getInstance().UpdateProfile(Constant.KEY_PASSWORD,(String)newValue);
					return true;
				}
			});
			mGender.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

				@Override
				public boolean onPreferenceChange(Preference preference,
						Object newValue) {
					Parameters.instance().setItem(Constant.KEY_GENDER,(String)newValue);
					mGender.setSummary((String)newValue);
					mGender.setValueIndex(mGender.findIndexOfValue((String)newValue));
					TRServiceConnection.getInstance().UpdateProfile(Constant.KEY_GENDER,(String)newValue);
					return true;
				}
			});
			mMessageSettings.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

				@Override
				public boolean onPreferenceChange(Preference preference,
						Object newValue) {
					int index=0;
					EMChatOptions chatOptions=EMChatManager.getInstance().getChatOptions();
					Parameters.instance().setItem(Constant.KEY_MESSAGE_SETTINGS,(String)newValue);
					mMessageSettings.setSummary((String)newValue);
					index = mMessageSettings.findIndexOfValue((String)newValue);
					mMessageSettings.setValueIndex(index);
					
					switch(index)
					{
					case 0:
						chatOptions.setNoticedByVibrate(false);
						chatOptions.setNoticeBySound(true);
						EMChatManager.getInstance().setChatOptions(chatOptions);
						HXSDKHelper.getInstance().getModel().setSettingMsgVibrate(false);
						HXSDKHelper.getInstance().getModel().setSettingMsgSound(true);
					case 1:
						chatOptions.setNoticedByVibrate(true);
						chatOptions.setNoticeBySound(false);
						EMChatManager.getInstance().setChatOptions(chatOptions);
						HXSDKHelper.getInstance().getModel().setSettingMsgVibrate(true);
						HXSDKHelper.getInstance().getModel().setSettingMsgSound(false);
					case 2:
						chatOptions.setNoticedByVibrate(false);
						chatOptions.setNoticeBySound(false);
						EMChatManager.getInstance().setChatOptions(chatOptions);
						HXSDKHelper.getInstance().getModel().setSettingMsgVibrate(false);
						HXSDKHelper.getInstance().getModel().setSettingMsgSound(false);
						
					}
					return true;
				}
			});
			mOfflineMap.setOnPreferenceClickListener(new OnPreferenceClickListener(){

				@Override
				public boolean onPreferenceClick(Preference preference) {
					Intent intent = new Intent(mThis.getActivity(), OfflineMapActivity.class);  
			        startActivity(intent); 
					return true;
				}
				
			});
			LoadParamers();
			return mView;
		}
		private void LoadParamers()
		{
	          mUserName.setSummary(Parameters.instance().getItem(Constant.KEY_USER_NAME));
	          mUserName.setText(Parameters.instance().getItem(Constant.KEY_USER_NAME));
	          mEmail.setSummary(Parameters.instance().getItem(Constant.KEY_EMAIL));
	          mEmail.setText(Parameters.instance().getItem(Constant.KEY_EMAIL));
	          mWeight.setSummary(Parameters.instance().getItem(Constant.KEY_WEIGHT));
	          mWeight.setText(Parameters.instance().getItem(Constant.KEY_WEIGHT));
	          mHeight.setSummary(Parameters.instance().getItem(Constant.KEY_HEIGHT));
	          mHeight.setText(Parameters.instance().getItem(Constant.KEY_HEIGHT));
	          mBirthday.setSummary(Parameters.instance().getItem(Constant.KEY_BIRTHDAY));
	          mBirthday.setText(Parameters.instance().getItem(Constant.KEY_BIRTHDAY));
	          mOccupation.setSummary(Parameters.instance().getItem(Constant.KEY_OCCUPATION));
	          mOccupation.setText(Parameters.instance().getItem(Constant.KEY_OCCUPATION));
	          mGender.setSummary(Parameters.instance().getItem(Constant.KEY_GENDER));
	          mGender.setValueIndex(mGender.findIndexOfValue(Parameters.instance().getItem(Constant.KEY_GENDER)));
	          mMessageSettings.setSummary(Parameters.instance().getItem(Constant.KEY_MESSAGE_SETTINGS));
	          mMessageSettings.setValueIndex(mMessageSettings.findIndexOfValue(Parameters.instance().getItem(Constant.KEY_MESSAGE_SETTINGS)));
		}
	}

}
