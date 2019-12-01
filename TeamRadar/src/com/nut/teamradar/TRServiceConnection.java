package com.nut.teamradar;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.nut.teamradar.Service.ITeamRadarService;
import com.nut.teamradar.Service.TeamRadarService;
import com.nut.teamradar.model.ActivityHistory;
import com.nut.teamradar.model.Contact;
import com.nut.teamradar.model.Group;
import com.nut.teamradar.model.MarkerInfo;
import com.nut.teamradar.model.Member;
import com.nut.teamradar.model.PhoneContact;
import com.nut.teamradar.model.ScreenInfo;
import com.nut.teamradar.model.ShortMessage;
import com.nut.teamradar.model.User;
import com.nut.teamradar.webclient.ITRConnectionListener;
import com.nut.teamradar.webclient.ITRContactListener;
import com.nut.teamradar.webclient.ITRGroupListener;
import com.nut.teamradar.webclient.ITRHistoryListener;
import com.nut.teamradar.webclient.ITRLocationListener;
import com.nut.teamradar.webclient.ITRMessageListener;
import com.nut.teamradar.webclient.ITRProfileListener;

public class TRServiceConnection {
	private static String TAG= "TRServiceConnection";
	private static TRServiceConnection _instance = null;
	private TeamRadarConnection mConnection = null;
	private Context mCtx = null;
	private boolean Connected = false;
	private TRServiceConnection()
	{
		mConnection = new TeamRadarConnection();
	}
	public static TRServiceConnection getInstance()
	{
		if(_instance == null)
		{
			synchronized(TRServiceConnection.class)
			{
				if(_instance == null)
				{
					_instance = new TRServiceConnection();
				}
			}
		}
		
		return _instance;
	}
	
	private ITeamRadarService mService = null;
	class TeamRadarConnection implements ServiceConnection {
		public void onServiceConnected(ComponentName name, IBinder boundService) {
			mService = ITeamRadarService.Stub.asInterface((IBinder)boundService);
			Log.d(TAG, "onServiceConnected success!");
		}
		
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
			Log.d(TAG, "onServiceDisconnected!");
		}
	}
	public boolean isConnected()
	{
		return mService != null;
	}
	public void Connect(Context ctx)
	{
		if(Connected == false)
		{
			synchronized(TRServiceConnection.class)
			{
				if(Connected == false)
				{
					mCtx = ctx;
					Intent intent = new Intent(mCtx, TeamRadarService.class);  
					intent.putExtra("AppName", "TeamRadarApplication");
					mCtx.startService(intent); 
					mCtx.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
					Log.d(TAG, "Connect TeamRadarService!");
					Connected = true;
				}
			}
		}
	}

	public void DisConnect()
	{
		mCtx.unbindService(mConnection);
		Connected = false;
		Log.d(TAG, "DisConnect TeamRadarService");
	}
	
	public void start()
	{
		try {
			mService.Start();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	public void stop()
	{
		try {
			mService.Stop();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void RegisterConnectionListener(ITRConnectionListener.Stub Listener)
	{
		Log.d(TAG, "RegisterConnectionListener!");
		try {
			mService.RegisterConnectionListener(Listener);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void RemoveConnectionListener(ITRConnectionListener.Stub Listener)
	{
		Log.d(TAG, "RemoveConnectionListener!");
		try {
			mService.RemoveConnectionListener(Listener);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void RegisterContactListener(ITRContactListener.Stub Listener)
	{
		Log.d(TAG, "RegisterContactListener!");
		try {
			mService.RegisterContactListener(Listener);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void RemoveContactListener(ITRContactListener.Stub Listener)
	{
		Log.d(TAG, "RemoveContactListener!");
		try {
			mService.RemoveContactListener(Listener);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void RegisterGroupListener(ITRGroupListener.Stub Listener)
	{
		Log.d(TAG, "RegisterGroupListener!");
		try {
			mService.RegisterGroupListener(Listener);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void RemoveGroupListener(ITRGroupListener.Stub Listener)
	{
		Log.d(TAG, "RemoveGroupListener!");
		try {
			mService.RemoveGroupListener(Listener);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void RegisterHistoryListener(ITRHistoryListener.Stub Listener)
	{
		Log.d(TAG, "RegisterHistoryListener!");
		try {
			mService.RegisterHistoryListener(Listener);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void RemoveHistoryListener(ITRHistoryListener.Stub Listener)
	{
		Log.d(TAG, "RemoveHistoryListener!");
		try {
			mService.RemoveHistoryListener(Listener);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void RegisterLocationListener(ITRLocationListener.Stub Listener)
	{
		Log.d(TAG, "RegisterLocationListener!");
		try {
			mService.RegisterLocationListener(Listener);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void RemoveLocationListener(ITRLocationListener.Stub Listener)
	{
		Log.d(TAG, "RemoveLocationListener!");
		try {
			mService.RemoveLocationListener(Listener);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void RegisterMessageListener(ITRMessageListener.Stub Listener)
	{
		Log.d(TAG, "RegisterMessageListener!");
		try {
			mService.RegisterMessageListener(Listener);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void RemoveMessageListener(ITRMessageListener.Stub Listener)
	{
		Log.d(TAG, "RemoveMessageListener!");
		try {
			mService.RemoveMessageListener(Listener);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void RegisterProfileListener(ITRProfileListener.Stub Listener)
	{
		Log.d(TAG, "RegisterProfileListener!");
		try {
			mService.RegisterProfileListener(Listener);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void RemoveProfileListener(ITRProfileListener.Stub Listener)
	{
		Log.d(TAG, "RemoveProfileListener!");
		try {
			mService.RemoveProfileListener(Listener);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int Login(String UsrName , String PassWord)
	{
		Log.d(TAG, "Login!");
		try {
			return mService.Login(UsrName, PassWord);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	public int Logout()
	{
		Log.d(TAG, "Logout!");
		try {
			return mService.Logout();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	public String GetHXUserName()
	{
		try {
			return mService.GetHXUserName();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public String GetHXUserPassword()
	{
		try {
			return mService.GetHXUserPassword();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public User DownloadProfile()
	{
		try {
			return mService.DownloadProfile();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public void DownloadGroupMembers(long GroupId)
	{
		try {
			mService.DownloadGroupMembers(GroupId);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void DeleteMember(long UserId)
	{
		try {
			mService.DeleteMember(UserId);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void DeleteActivity(long GroupId)
	{
		try {
			mService.DeleteActivity(GroupId);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void CreateAcitivity(String name , String subscription , String Comment)
	{
		try {
			mService.CreateAcitivity(name, subscription, Comment);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void AddUserToActivity(long UserId , long GroupId , String username)
	{
		try {
			mService.AddUserToActivity(UserId, GroupId, username);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void DownloadJoinedGroups()
	{
		try {
			mService.DownloadJoinedGroups();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void UpdateProfile(String field , String value)
	{
		try {
			mService.UpdateProfile(field, value);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void StartActivity(ActivityHistory record)
	{
		try {
			mService.StartActivity(record);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void DownloadActivityHistory(long userid , long activityid)
	{
		try {
			mService.DownloadActivityHistory(userid, activityid);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void DownloadHistoryLocations(long userid , long activityid , String Mark)
	{
		try {
			mService.DownloadHistoryLocations(userid, activityid, Mark);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void PushMessage(ShortMessage Msg)
	{
		try {
			mService.PushMessage(Msg);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void PullMessage()
	{
		try {
			mService.PullMessage();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void DownloadAllLocations(int GroupId)
	{
		try {
			mService.DownloadAllLocations(GroupId);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void DownloadRandezvous(int GoupId)
	{
		try {
			mService.DownloadRandezvous(GoupId);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void Registration(User usr)
	{
		try {
			mService.Registration(usr);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void DownloadContacts(String subscription)
	{
		try {
			mService.DownloadContacts(subscription);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void DeleteContacts(long Id)
	{
		try {
			mService.DeleteContacts(Id);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void AddContact(Contact contact)
	{
		try {
			mService.UploadContact(contact);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void ConnectToServer(String addr, int Port,int cetResId)
	{
		try {
			mService.ConnectToServer(addr, Port, cetResId);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void UpdateRandezvous(MarkerInfo Info)
	{
		try {
			mService.UpdateRandezvous(Info);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void UploadLocation(com.nut.teamradar.model.Location Loc)
	{
		try {
			mService.UploadLocation(Loc);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public List<Group> getGroups()
	{
		try {
			return mService.GetGroups();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public List<Member> GetMembers(long GroupId)
	{
		try {
			return mService.GetMembers(GroupId);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public List<ActivityHistory> GetActivityHistory()
	{
		try {
			return mService.GetActivityHistory();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public void DownloadActivitysBySubscription(String subscription)
	{
		try {
			mService.DownloadActivitysBySubscription(subscription);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Member GetMember(int GroupIndex,int MemberIndex)
	{
		try {
			return mService.GetMember(GroupIndex, MemberIndex);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public Group GetGroup(int index)
	{
		try {
			return mService.GetGroup(index);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public void DeleteMemberFromActivity(long GroupId, long UserId)
	{
		try {
			mService.DeleteMemberFromActivity(GroupId, UserId);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Group getGroupByNameAndSub(String Name, String Sub)
	{
		try {
			return mService.GetGroupByNameAndSub(Name, Sub);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public Member GetMemberBySub(String Sub)
	{
		try {
			return mService.GetMemberBySub(Sub);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public void SendMessage(ShortMessage msg)
	{
		try {
			mService.SendMessage(msg);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String GetUserName(long GroupId, long UserId)
	{
		try {
			return mService.GetUserName(GroupId, UserId);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public void PutUserProfile(User user)
	{
		try {
			mService.PutUserProfile(user);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public User GetUserProfile()
	{
		try {
			return mService.GetUserProfile();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public void SetScreenInfo(int width, int height, int sBarHeight)
	{
		try {
			mService.SetScreenInfo(width, height, sBarHeight);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public ScreenInfo GetScreenInfo()
	{
		try {
			return mService.GetScreenInfo();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public void PutContact(String name, String subscription)
	{
		try {
			mService.PutContact(name, subscription);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void DeleteContact(String subscription)
	{
		try {
			mService.DeleteContact(subscription);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public List<PhoneContact> GetContacts()
	{
		try {
			return mService.GetContacts();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public void SetSessionId(String id)
	{
		try {
			mService.SetSessionId(id);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String GetSessionId()
	{
		try {
			return mService.GetSessionId();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public void SetMark(String mark)
	{
		try {
			mService.SetMark(mark);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String GetMark()
	{
		try {
			return mService.GetMark();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public void SetName(String name)
	{
		try {
			mService.SetName(name);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String GetName()
	{
		try {
			return mService.GetName();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public void SetCurrentUserId(int userid)
	{
		try {
			mService.SetCurrentUserId(userid);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int GetCurrentUserId()
	{
		try {
			return mService.GetCurrentUserId();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	public void SetSubscription(String sub)
	{
		try {
			mService.SetSubscription(sub);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String GetSubscription()
	{
		try {
			return mService.GetSubscription();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public void SaveUserName(String name)
	{
		try {
			mService.SaveUserName(name);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String ReadUserName()
	{
		try {
			return mService.ReadUserName();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public void SaveSubscription(String sub)
	{
		try {
			mService.SaveSubscription(sub);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String ReadSubscription()
	{
		try {
			return mService.ReadSubscription();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public void SavePassword(String passwd)
	{
		try {
			mService.SavePassword(passwd);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String ReadPassword()
	{
		try {
			return mService.ReadPassword();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public void SetUserMode(int Mode)
	{
		try {
			mService.SetUserMode(Mode);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int GetUserMode()
	{
		try {
			return mService.GetUserMode();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	public void SetActiveOwnerId( long Id)
	{
		try {
			mService.SetActiveOwnerId(Id);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public long GetActiveOwnerId()
	{
		try {
			return mService.GetActiveOwnerId();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	public void SetActiveGroupId(long Id)
	{
		try {
			mService.SetActiveGroupId(Id);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public long GetActiveGroupId()
	{
		try {
			return mService.GetActiveGroupId();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	public void BeginWriteLocationRecord()
	{
		try {
			mService.BeginWriteLocationRecord();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void WriteLocationRecord(String sub, String name, com.nut.teamradar.model.Location loc)
	{
		try {
			mService.WriteLocationRecord(sub, name, loc);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void CommitAllLocationRecords()
	{
		try {
			mService.CommitAllLocationRecords();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void SetActiveGroupName(String name)
	{
		try {
			mService.SetActiveGroupName(name);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getActiveGroupName()
	{
		try {
			return mService.getActiveGroupName();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public void setActiveGroupSubscription(String name)
	{
		try {
			mService.setActiveGroupSubscription(name);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getActiveGroupSubscription() 
	{
		try {
			return mService.getActiveGroupSubscription();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public void CreateHXUser(String UserName ,String Password)
	{
		try {
			mService.RegisterHXUser(UserName, Password);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void ChangePassword(String sub, String password)
	{
		try {
			mService.ChangePassword(sub, password);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void ChangeHXUserPassword(String sub, String password)
	{
		try {
			mService.ChangeHXUserPassword(sub, password);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void CheckForUpdate()
	{
		try {
			mService.CheckForUpdate();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean isLoginSuccess()
	{
		try {
			return mService.isloginSuccess();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
