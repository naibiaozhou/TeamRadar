package com.nut.teamradar.Fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.exceptions.EaseMobException;
import com.nut.teamradar.ChatActivity;
import com.nut.teamradar.Constant;
import com.nut.teamradar.MainActivity;
import com.nut.teamradar.Parameters;
import com.nut.teamradar.R;
import com.nut.teamradar.R.color;
import com.nut.teamradar.TRServiceConnection;
import com.nut.teamradar.TeamRadarApplication;
import com.nut.teamradar.adapter.ActivityHistoryAdapter;
import com.nut.teamradar.adapter.ActivityViewAdapter;
import com.nut.teamradar.adapter.MemberViewAdapter;
import com.nut.teamradar.base.BaseUi;
import com.nut.teamradar.model.ActivityHistory;
import com.nut.teamradar.model.Contact;
import com.nut.teamradar.model.Group;
import com.nut.teamradar.model.Location;
import com.nut.teamradar.model.Member;
import com.nut.teamradar.model.ShortMessage;
import com.nut.teamradar.model.User;
import com.nut.teamradar.util.Encrypt;
import com.nut.teamradar.util.GroupMembers;
import com.nut.teamradar.util.RandomString;
import com.nut.teamradar.util.UserUtils;
import com.nut.teamradar.webclient.ITRGroupListener;
import com.nut.teamradar.webclient.ITRHistoryListener;
import com.nut.teamradar.webclient.ITRProfileListener;
import com.nut.teamradar.webclient.WebAckCallBack;
import com.nut.teamradar.webclient.WebConnection;

public class FragmentActivityManagment extends BaseUi {
    private static final String SEP = ": ";
    private final static String TAG = "FragmentActivityManagment";

    private static final HandlerThread sWorkerThread = new HandlerThread(
            "activities-fetcher");
    static {
        sWorkerThread.start();
    }
    private static final Handler sWorker = new Handler(
            sWorkerThread.getLooper());

	//protected static final String Messenger = null;

    private Button mCreate = null;
    private Button mRefresh = null;
    private ListView mListActivities = null;
    private ListView mListMembers = null;

    private ArrayList<Group> mActivities = null;
    private ArrayList<ActivityHistory> mActivityHistory = null;
    private ArrayList<Location> mLocations = null;
    
    private ArrayList<Member> mCurrentMembers = null;
    private ActivityHistoryAdapter mActivityHistoryAdapter = null;

    private ProgressDialog mPrgsDlg = null;
    private View mView;
    private FragmentActivityManagment mThis = null;
    private LayoutInflater mInflater ;
    private AlertDialog.Builder CreateActivityDlg;
    private View  CreateActivityDlgLayout;
    private long mActivityId = 0;
    private Group mActiveGroup = null;
    private int SelectedGroup=0;
    private long SelectedGroupId =0;
    private int SelectGroupIndex=0;
    private int SelectMemberIndex=0;
    private int ActiveGroup = -1;
    private ActivityViewAdapter ActivityAdapter = null;
    private MemberViewAdapter MemberAdapter = null;
    private ListView mListViewHistories = null;
    private AlertDialog mHistoriesDlg = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	TRServiceConnection.getInstance().Connect(this.getActivity().getApplicationContext());
    	mInflater = inflater;
    	mThis = this;
    	mView = inflater.inflate(R.layout.fragment_activity, container, false);
        mActivities = new ArrayList<Group>();
        mCurrentMembers = new ArrayList<Member>();
        mActivityHistory = new ArrayList<ActivityHistory>();
        mLocations = new ArrayList<Location>();
        initWidgets();
        
    	new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				{
					
					TRServiceConnection.getInstance().RegisterGroupListener(mGroupListener);
					TRServiceConnection.getInstance().RegisterHistoryListener(mHistoryListener);
					TRServiceConnection.getInstance().RegisterProfileListener(mProfileListener);
			        //ContextLoader.getInstance().registerObserver(this);
			        //ContextLoader.getInstance().setContainer(mThis);
			        //ContextLoader.getInstance().DownloadMyAllGroupsAndMembers();
			        //ContextLoader.getInstance().GetUserProfiles();
			        TRServiceConnection.getInstance().DownloadJoinedGroups();
			        TRServiceConnection.getInstance().DownloadProfile();
			    	/*new Thread(new Runnable(){
			
						@Override
						public void run() {
					        ContextLoader.getInstance().DownloadMyAllGroupsAndMembers();
					        ContextLoader.getInstance().GetUserProfiles();
						}
					}).start();*/
		        }
			}
    	
    	},800);
        
        return mView;
    }
    Handler mHandler = new Handler(){
		@Override  
        public void handleMessage(Message msg) {
            switch (msg.what) {   
            case 0x11:   
            	 ((MainActivity)mThis.getActivity()).MapFregment.ShowHistoryTrejactory(mLocations); 
                 break;   
            }
            super.handleMessage(msg); 
        }
    };
    private ITRHistoryListener.Stub mHistoryListener = new ITRHistoryListener.Stub()
    {

		@Override
		public void OnGetActivity(int Flag, List<Group> Groups)
				throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void OnGetActivityHistory(int Flag,
				List<ActivityHistory> Historys) throws RemoteException {
			mActivityHistory.removeAll(mActivityHistory);
			if(Flag == WebConnection.SUCCESS)
			{
				mActivityHistory.addAll(TRServiceConnection.getInstance().GetActivityHistory());
				View historyDlgLayout = mInflater.inflate(R.layout.list_history, (ViewGroup) mView.findViewById(R.id.list_all_histories));
                mListViewHistories = (ListView) historyDlgLayout.findViewById(R.id.histories);
                Builder historyDlgBuilder = new AlertDialog.Builder(mThis.getContext())
                    .setTitle(R.string.history)
                    .setView(historyDlgLayout);
                mHistoriesDlg = historyDlgBuilder.show();

                mListViewHistories.setAdapter(null);
                mActivityHistoryAdapter = new ActivityHistoryAdapter(
                		mView.getContext(),
                        android.R.layout.simple_expandable_list_item_1,
                        mActivityHistory);
                mListViewHistories.setAdapter(mActivityHistoryAdapter);
                mListViewHistories.setOnItemClickListener(mHistoriesListViewOnItemClickListener);
			}
		}

		@Override
		public void OnGetHistoryLocations(int Flag, List<Location> Locations)
				throws RemoteException {
			mLocations.removeAll(mLocations);
			if(Flag == WebConnection.SUCCESS)
			{
				mLocations.addAll(Locations);
				((MainActivity)mThis.getActivity()).switchToMapPage();
				Log.d(TAG,"Switch to Map Page!");
                Message message = new Message();   
                message.what = 0x11;
                mHandler.sendMessage(message); 
			}
			Log.d(TAG, "OnGetHistoryLocations return!");
		}
    	
    };
    private ITRProfileListener.Stub mProfileListener = new ITRProfileListener.Stub()
    {

		@Override
		public void OnObtainProfile(User usr) throws RemoteException {
			try
			{
				Parameters.instance().setItem(Constant.KEY_USER_NAME,usr.getName());
				Parameters.instance().setItem(Constant.KEY_EMAIL,String.valueOf(usr.getMail()));
				Parameters.instance().setItem(Constant.KEY_WEIGHT,String.valueOf(usr.getWeight()));
				Parameters.instance().setItem(Constant.KEY_HEIGHT,String.valueOf(usr.getHeight()));
				Parameters.instance().setItem(Constant.KEY_BIRTHDAY,usr.getBirthday());
				Parameters.instance().setItem(Constant.KEY_OCCUPATION,usr.getProfession());
				Parameters.instance().setItem(Constant.KEY_PASSWORD,usr.getPassword());
				Parameters.instance().setItem(Constant.KEY_GENDER,usr.getGender());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void OnProfileUpdate(int flag) throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void OnPasswordUpdate(int flag) throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void OnHXUserPasswordUpdate(int flag) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
    	
    };
    private ITRGroupListener.Stub mGroupListener = new ITRGroupListener.Stub()
    {
    	List<Group> curGroups=null;
    	private boolean populateActivities(Iterator<Group> it) {
        	mActivityNames.clear();
        	mFetchedActivities.clear();
            while(it.hasNext())
            {
                Group g = it.next();
                if (!mActivityNames.contains(g.getName()))
                {
                    //Log.d(TAG, "Downloaded activity: " + g.getName());
                    mFetchedActivities.add(g);
                    mActivityNames.add(g.getName());
                }
            }
            Log.d(TAG, "Activities found: " + Integer.toString(mActivityNames.size()));
            return mFetchedActivities.size() > 0;
        }
        
        private void popupActivitiesSelectionDialog() {
        	mThis.getActivity().runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    mInviteContactDlgLayout = mInflater.inflate(R.layout.invite_contact,
                            (ViewGroup) mView.findViewById(R.id.invite_a_contact));
                    mListViewActivities  = (ListView) mInviteContactDlgLayout.findViewById(R.id.list_activities);
                    mJoininContactDlgBuilder  = new AlertDialog.Builder(mThis.getContext())
                    .setTitle(R.string.join)
                    .setView(mInviteContactDlgLayout);
                    mInviteContactDlg = mJoininContactDlgBuilder.show();
                }
            });

        }
		@Override
		public void OnMemberUpdate(int flag, List<Member> Members)
				throws RemoteException {
		}

		@Override
		public void OnDeleteMember(int Flag) throws RemoteException {
			reloadActivities();
			
		}

		@Override
		public void OnDeleteActivity(int Flag) throws RemoteException {
			reloadActivities();
			
		}

		@Override
		public void OnCreateActivity(int Flag) throws RemoteException {
			reloadActivities();
			
		}

		@Override
		public void OnAddUserToActivity(int Flag) throws RemoteException {
			reloadActivities();
			
		}

		@Override
		public void OnObtainActivity(int flag, List<Group> groups)
				throws RemoteException {
			curGroups = TRServiceConnection.getInstance().getGroups();
			if(curGroups != null)
			{
				for(int i = 0 ;i < curGroups.size();i++)
				{
					TRServiceConnection.getInstance().DownloadGroupMembers(curGroups.get(i).getId());
				}
			}
			mThis.getActivity().runOnUiThread(new Runnable(){

				@Override
				public void run() {
					DisplayGroups(curGroups);
				}
				
			});
			
		}

		@Override
		public void OnSessionStart() throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void OnSessionStop() throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void OnObtainActivityBySubscription(int flag, List<Group> groups)
				throws RemoteException {
			  if (flag == WebConnection.SUCCESS) {
                if (populateActivities(groups.iterator())) {
                    popupActivitiesSelectionDialog();

                    mListViewActivities.setAdapter(null);
                    ArrayAdapter<String> arrAdapter = new ArrayAdapter<String>(
                            mView.getContext(),
                            android.R.layout.simple_expandable_list_item_1,
                            mActivityNames);
                    mListViewActivities.setAdapter(arrAdapter);
                    mListViewActivities.setOnItemClickListener(mActivitiesListViewOnItemClickListener);
                }
            } else {
                Log.e(TAG, "error to get activities");
            }
	}

		@Override
		public void OnDeleteMemberFromActivity(int Flag) throws RemoteException {
			reloadActivities();
			
		}

		@Override
		public void OnHXUserCreated(int Flag) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
    	
    };
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    } 


    String MsgInfo[];
    public void addUserToActivity(final ShortMessage msg)
    {
    	MsgInfo = msg.getMessage().split("#");
    	//Message Format : GROUPNAME#GROUPID#OWNERID#OPTION
    	TRServiceConnection.getInstance().AddUserToActivity(msg.getFromId(), Integer.valueOf(MsgInfo[1]), msg.getFromName());
    	Log.e(TAG, String.format("Add user %d to activity %d %s", msg.getFromId(),Integer.valueOf(MsgInfo[1]),msg.getFromName()));
    	new Thread(new Runnable(){

			@Override
			public void run() {
				if(MsgInfo[3].equals(Constant.MSG_JOININ))
				{
					Log.e(TAG, String.format("Add user %s to HX Group %s:%s", msg.getFromSubscription(),msg.getToSubscription(),MsgInfo[0]));
					String Id = UserUtils.GetGroupId(Encrypt.GetEncryptString(UserUtils.MakeGroupName(MsgInfo[0],msg.getToSubscription())));
					if(Id != null)
					{
						Log.e(TAG, String.format("Add user %s to HX Group %s:%s OK", msg.getFromSubscription(),msg.getToSubscription(),MsgInfo[0]));
						String[] Users = new String[1];
						Users[0] = Encrypt.GetEncryptString(msg.getFromSubscription());
						try {
							EMGroupManager.getInstance().addUsersToGroup(Id,Users);
						} catch (EaseMobException e) {
							e.printStackTrace();
						}
					}
				}
				else if(MsgInfo[3].equals(Constant.MSG_INVITATION_ACCEPT))
				{
					String Id = UserUtils.GetGroupId(Encrypt.GetEncryptString(UserUtils.MakeGroupName(MsgInfo[0],msg.getToSubscription())));
					Log.e(TAG, String.format("Add user %s to HX Group %s:%s", msg.getFromSubscription(),msg.getToSubscription(),MsgInfo[0]));
					if(Id != null)
					{
						Log.e(TAG, String.format("Add user %s to HX Group %s:%s OK", msg.getFromSubscription(),msg.getToSubscription(),MsgInfo[0]));
						String[] Users = new String[1];
						Users[0] = Encrypt.GetEncryptString(msg.getFromSubscription());
						try {
							EMGroupManager.getInstance().addUsersToGroup(Id,Users);
						} catch (EaseMobException e) {
							e.printStackTrace();
						}
					}
				}
			}
    		
    	}).start();
    }
    private AdapterView.OnItemClickListener mHistoriesListViewOnItemClickListener = new 
    		AdapterView.OnItemClickListener()
    {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			ActivityHistory aRecord = mActivityHistoryAdapter.getItem(arg2);
			TRServiceConnection.getInstance().DownloadHistoryLocations(aRecord.getUserId(), 
					aRecord.getActivityId(), aRecord.getMark());
			mHistoriesDlg.dismiss();
			Log.e(TAG, "mHistoriesListViewOnItemClickListener");
			
		}
    	
    };
    private void showCreateActivityDialog()
    {
    	CreateActivityDlgLayout = mInflater.inflate(R.layout.createactivity,(ViewGroup) 
				mView.findViewById(R.id.create_an_activity));  
 	    CreateActivityDlg = new AlertDialog.Builder(mThis.getContext()).setTitle(R.string.create_activity).
    			setView(CreateActivityDlgLayout).
    			setPositiveButton(getString(R.string.ok), new  DialogInterface.OnClickListener (){
    				String Name;
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						String name,comment;
						EditText ActivityName = (EditText)CreateActivityDlgLayout.findViewById(R.id.activityname);
						EditText Commnet = (EditText)CreateActivityDlgLayout.findViewById(R.id.activitycomment);
						name = ActivityName.getText().toString();
						comment = Commnet.getText().toString();
						//Log.e(TAG, "ActivityName:"+name+ "ActivityName:"+comment);
						if(!name.isEmpty())
						{
							if(comment.isEmpty())
							{
								comment = "dumy";
							}
							Name = ActivityName.getText().toString();
							TRServiceConnection.getInstance().CreateAcitivity(Name, TRServiceConnection.getInstance().GetSubscription(),
									Commnet.getText().toString());
							new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										String GroupName = Encrypt.GetEncryptString(UserUtils.MakeGroupName(Name,TRServiceConnection.getInstance().GetSubscription()));
										EMGroupManager.getInstance().createPublicGroup(GroupName, 
												Name, null , false);
									} catch (EaseMobException e) {
										e.printStackTrace();
									}
								}
								}).start();
						}
						else
						{
							
				            Toast t = Toast.makeText(mThis.getActivity(),
				                    getString(R.string.groupnotempty), Toast.LENGTH_SHORT);
				            t.setGravity(Gravity.CENTER, 0, 0);
				            t.show();
				        }
					}
    			}).
    			setNegativeButton(getString(R.string.cancel), null);    
 	   CreateActivityDlg.show();
    }
    private View.OnClickListener mBtnOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
        	if(view.getId() == R.id.btnCreateActivity)
        	{
        		showCreateActivityDialog();
        	} else if (view.getId() == mBtnQuery.getId()) {
                Log.d(TAG, "Query button clicked");
                String creator = mETCreator.getText().toString();
                if (creator != null && !creator.isEmpty()) {
                    Log.d(TAG, "now start to query activities created by "
                            + creator);
                    TRServiceConnection.getInstance().DownloadActivitysBySubscription(creator);
        	}
            } else if (view.getId() == mBtnContactsList.getId()) {
                Log.d(TAG, "List contacts button clicked");
                popupContactListDialog();
            } else if(view.getId() == R.id.btnRefreshActivity)
        	{
        		if(TRServiceConnection.getInstance().GetUserMode() == Constant.WORK_MODE_ACTIVE )
        		{
        			TRServiceConnection.getInstance().DownloadJoinedGroups();
        			mCurrentMembers.clear();
        			populateCurrentMembers();
        		}
        		 
        	}
        }
    };

    private AdapterView.OnItemLongClickListener mMemberListViewLongClickListener = 
    		new AdapterView.OnItemLongClickListener(){
    	Member mMember;
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View arg1,
				int arg2, long arg3) {
			SelectMemberIndex = arg2;
			MemberAdapter.setSelectItem(arg2);
			MemberAdapter.notifyDataSetChanged();
			Log.e(TAG, String.format("group =%d member=%d", SelectGroupIndex,SelectMemberIndex));
			mMember = TRServiceConnection.getInstance().GetMember(SelectGroupIndex, SelectMemberIndex);
			if(mMember == null)
				return false;
			
			AlertDialog OperationDlg = new AlertDialog.Builder(mThis.getContext())
			.setTitle(getString(R.string.operation))
			.setItems(new String[] {getString(R.string.p2pchat),
					getString(R.string.historytrack),
					getString(R.string.deletemember)}, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog,int which) {
					switch(which)
					{
					case 0:
						startActivity(new Intent(getActivity(), 
								ChatActivity.class).
								putExtra("userId", Encrypt.GetEncryptString(mMember.getSubscription())).
								putExtra("userName", mMember.getUsername()).
								putExtra("TGroupIndex", SelectGroupIndex).
								putExtra("UserIndex", SelectMemberIndex));
						break;
					case 1:
						//TODO 
						// show history trajectory data.
						if(TRServiceConnection.getInstance().GetActiveGroupId() == -1)
						{
							TRServiceConnection.getInstance().DownloadActivityHistory(mMember.getUserId(), mMember.getActivityId());
						}
						else
						{
							Toast t = Toast.makeText(mThis.getActivity(), getString(R.string.closeactivityfirst), Toast.LENGTH_LONG);
							t.show();
						}
						break;
					case 2:
						Group grp = TRServiceConnection.getInstance().GetGroup(SelectGroupIndex);
						if(mMember.getUserId() == TRServiceConnection.getInstance().GetCurrentUserId() || 
								grp.getOwnerId() == TRServiceConnection.getInstance().GetCurrentUserId())
						{
							if(grp.getOwnerId() == mMember.getUserId())
							{
								String Id = UserUtils.GetGroupId(Encrypt.GetEncryptString(UserUtils.MakeGroupName(SelectGroupIndex)));
								if(Id != null)
								{
									try {
										EMGroupManager.getInstance().exitAndDeleteGroup(Id);
									} catch (EaseMobException e) {
										e.printStackTrace();
									}
								}
								
								TRServiceConnection.getInstance().DeleteActivity(grp.getId());
							}
							else
							{
								String Id = UserUtils.GetGroupId(Encrypt.GetEncryptString(UserUtils.MakeGroupName(SelectGroupIndex)));
								if(Id != null)
								{
									try {
										EMGroupManager.getInstance().removeUserFromGroup(Id, Encrypt.GetEncryptString(mMember.getSubscription()));
									} catch (EaseMobException e) {
										e.printStackTrace();
									}
								}
								TRServiceConnection.getInstance().DeleteMemberFromActivity(grp.getId(), 
										mMember.getUserId());
							}
						}
						else
						{
							Toast t = Toast.makeText(mThis.getActivity(),
				                    getString(R.string.youcanotdelete)+mMember.getUsername()+getString(R.string.youarenottheownerorself), Toast.LENGTH_LONG);
							t.show();
						}
						break;


					default:
							break;
					}

					dialog.dismiss();
				}
			}).setNegativeButton(getString(R.string.cancel),null).show();
			
			return false;
		}
    	
    };
    private String MakeGroupName(String name,String sub)
    {
    	String Out;
    	Out = sub+":"+name;
    	return Out;
    }   
    private String GetGroupId(String GroupName)
    {
    	List<EMGroup> grouplist = EMGroupManager.getInstance().getAllGroups();
		Iterator<EMGroup> it = grouplist.iterator();
		while(it.hasNext())
		{
			EMGroup grp = it.next();
			if(grp.getGroupName().equals(GroupName))
			{
				return grp.getGroupId();
			}
		}
		return null;
    }
    private AdapterView.OnItemLongClickListener mGroupListViewLongClickListener = new AdapterView.OnItemLongClickListener(){
    	Group mGroup;
    	View SelectView=null;
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View Item,
				int arg2, long arg3) {
			SelectGroupIndex = arg2;
			SelectView = Item;
			ActivityAdapter.setSelectItem(arg2);
			ActivityAdapter.notifyDataSetChanged();
	       	mCurrentMembers.clear();
        	List<Member> Members = TRServiceConnection.getInstance().GetMembers(SelectedGroupId);
        	if(SelectedGroupId != -1 && Members != null)
        	{
        		mCurrentMembers.addAll(TRServiceConnection.getInstance().GetMembers(SelectedGroupId));
        	}
         	populateCurrentMembers();
			Log.e(TAG, String.format("group =%d", SelectGroupIndex));
			mGroup =TRServiceConnection.getInstance().GetGroup(SelectGroupIndex);
			AlertDialog OperationDlg = new AlertDialog.Builder(mThis.getContext())
			.setTitle(getString(R.string.operation))
			.setItems(new String[] {getString(R.string.active),
					getString(R.string.unactive),
					getString(R.string.groupchat),
					getString(R.string.delete)}, 
					new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog,int which) {
					String Id;
					switch (which)
					{
					case  0:
						
						if(TRServiceConnection.getInstance().GetActiveGroupId() == -1)
						{
							ActiveGroup = SelectGroupIndex;
							String   Mark = RandomString.getRandomString16();
							ActivityAdapter.SetActiveItem(SelectGroupIndex);
							ActivityAdapter.notifyDataSetChanged();
							TRServiceConnection.getInstance().SetMark(Mark);
							TRServiceConnection.getInstance().SetActiveGroupId(mGroup.getId());
							TRServiceConnection.getInstance().SetActiveGroupName(mGroup.getName());
							TRServiceConnection.getInstance().SetActiveOwnerId(mGroup.getOwnerId());
							TRServiceConnection.getInstance().setActiveGroupSubscription(mGroup.getSubscription());
							
							mActiveGroup = mGroup;
							((MainActivity)mThis.getActivity()).MapFregment.ActivityStart();
							ActivityHistory aHistory = new ActivityHistory();
							SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     
							String date = sDateFormat.format(new java.util.Date()); 
							aHistory.setUserId(TRServiceConnection.getInstance().GetCurrentUserId());
							aHistory.setActivityId(mGroup.getId());
							aHistory.setDatetime(date);
							aHistory.setMark(Mark);
							TRServiceConnection.getInstance().StartActivity(aHistory);
							
							String GroupID = GetGroupId(Encrypt.GetEncryptString(MakeGroupName(TRServiceConnection.getInstance().getActiveGroupName(),
									TRServiceConnection.getInstance().getActiveGroupSubscription())));
							if(GroupID != null)
							{
								EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
								EMConversation conversation = EMChatManager.getInstance().getConversation(GroupID);
								message.setChatType(ChatType.GroupChat);
								
								TextMessageBody txtBody = new TextMessageBody(getString(R.string.startactivitymessage).replaceFirst("%1",TRServiceConnection.getInstance().GetName()));
								message.addBody(txtBody);
								message.setReceipt(GroupID);
								conversation.addMessage(message);
								
								EMChatManager.getInstance().sendMessage(message, new EMCallBack(){

									@Override
									public void onError(int arg0, String arg1) {
										
									}

									@Override
									public void onProgress(int arg0, String arg1) {
										
									}

									@Override
									public void onSuccess() {
										
									}});
							}
							
							
						}
						else if(mActiveGroup.getId() != mGroup.getId())
						{
							Toast t = Toast.makeText(mThis.getActivity(),
				                    getString(R.string.activefailmessage)+":"+mActiveGroup.getName(), Toast.LENGTH_LONG);
				            t.setGravity(Gravity.CENTER, 0, 0);
				            t.show();
						}
						
						break;
					case 1:
						if(TRServiceConnection.getInstance().GetActiveGroupId() != -1)
						{
							//TRServiceConnection.getInstance().stop();
							TRServiceConnection.getInstance().SetActiveGroupId(-1);
							TRServiceConnection.getInstance().SetMark(null);
							ActivityAdapter.SetActiveItem(-1);
							ActivityAdapter.notifyDataSetChanged();
							((MainActivity)mThis.getActivity()).MapFregment.ActivityStop();
							String GroupID = GetGroupId(Encrypt.GetEncryptString(MakeGroupName(TRServiceConnection.getInstance().getActiveGroupName(),
									TRServiceConnection.getInstance().getActiveGroupSubscription())));
							if(GroupID != null)
							{
								EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
								EMConversation conversation = EMChatManager.getInstance().getConversation(GroupID);
								message.setChatType(ChatType.GroupChat);
								
								TextMessageBody txtBody = new TextMessageBody(getString(R.string.stopactivitymessage).replaceFirst("%1",TRServiceConnection.getInstance().GetName()));
								message.addBody(txtBody);
								message.setReceipt(GroupID);
								conversation.addMessage(message);
								
								EMChatManager.getInstance().sendMessage(message, new EMCallBack(){
	
									@Override
									public void onError(int arg0, String arg1) {
										
									}
	
									@Override
									public void onProgress(int arg0, String arg1) {
										
									}
	
									@Override
									public void onSuccess() {
										
									}});
							}
						}
						break;
					case  2:
						Id = UserUtils.GetGroupId(Encrypt.GetEncryptString(UserUtils.MakeGroupName(SelectGroupIndex)));
						if(Id != null)
						{
							Intent intent = new Intent(mThis.getContext(), ChatActivity.class);
							// it is group chat
							intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
							intent.putExtra("groupId", Id);
							intent.putExtra("TGroupIndex", SelectGroupIndex);
							startActivityForResult(intent, 0);
						}
						break;
					case  3:
						if(mGroup.getOwnerId() == TRServiceConnection.getInstance().GetCurrentUserId())
						{
							Id = UserUtils.GetGroupId(Encrypt.GetEncryptString(UserUtils.MakeGroupName(SelectGroupIndex)));
							if(Id != null)
							{
								try {
									EMGroupManager.getInstance().exitAndDeleteGroup(Id);
								} catch (EaseMobException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
							TRServiceConnection.getInstance().DeleteActivity(mGroup.getId());

						}
						else
						{
							Toast t = Toast.makeText(mThis.getActivity(),
				                    getString(R.string.youcanotdelete)+mGroup.getName()+getString(R.string.youarenottheowner), Toast.LENGTH_LONG);
							t.show();
						}
						break;
					}
					
					dialog.dismiss();
				}
			}).setNegativeButton(getString(R.string.cancel),null).show();
			return false;
		}
    	
    };

    private AdapterView.OnItemClickListener mListViewOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View Item, int row,
                long col) {
        	SelectedGroup = row;
        	SelectGroupIndex = row;
        	SelectedGroupId = TRServiceConnection.getInstance().GetGroup(SelectedGroup).getId();
        	ActivityAdapter.setSelectItem(SelectedGroup);
        	ActivityAdapter.notifyDataSetChanged();
        	mCurrentMembers.clear();
        	List<Member> Members = TRServiceConnection.getInstance().GetMembers(SelectedGroupId);
        	if(SelectedGroupId != -1 && Members != null)
        	{
        		mCurrentMembers.addAll(TRServiceConnection.getInstance().GetMembers(SelectedGroupId));
        	}
         	populateCurrentMembers();
        }
    };
    private AdapterView.OnItemClickListener mMemberListViewOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View Item, int row,
                long col) {
			SelectMemberIndex = row;
			MemberAdapter.setSelectItem(row);
			MemberAdapter.notifyDataSetChanged();
        }
    };
    
    private void DisplayGroups(List<Group> groups)
    {
    	mActivities.clear();
    	ActiveGroup = -1;
    	SelectGroupIndex = -1;
    	for (int i =0;i< groups.size();i++)
    	{
    		if(groups.get(i).getId()!=-1)
    		{
    	    	if(TRServiceConnection.getInstance().GetActiveGroupId() != -1)
    	    	{
    	    		if(groups.get(i).getId() == TRServiceConnection.getInstance().GetActiveGroupId() )
    	    		{
    	    			mActiveGroup = groups.get(i);
    	    			ActiveGroup = i;
    	    		}
    	    	}
        		mActivities.add(groups.get(i));
    		}

    	}
    	ActivityAdapter = new ActivityViewAdapter(mView.getContext(),
                android.R.layout.simple_expandable_list_item_1,
                mActivities);
        mListActivities.setAdapter(ActivityAdapter);
        mListActivities.setOnItemClickListener(mListViewOnItemClickListener);
        mListActivities.setOnItemLongClickListener(mGroupListViewLongClickListener);	
        if(ActiveGroup != -1)
        {
			ActivityAdapter.SetActiveItem(ActiveGroup);
			ActivityAdapter.notifyDataSetChanged();  
        }
    }

    public void reloadActivities() {
    	TRServiceConnection.getInstance().DownloadJoinedGroups();
    }
    
    private void populateCurrentMembers() {
        Log.d(TAG,
                "populateCurrentListMembers(): "
                        + Integer.toString(mCurrentMembers.size())
                        + " members");
        MemberAdapter = new MemberViewAdapter(
        		mView.getContext(),
                android.R.layout.simple_expandable_list_item_1,
                mCurrentMembers);
        mListMembers.setAdapter(MemberAdapter);
        mListMembers.setOnItemClickListener(mMemberListViewOnItemClickListener);
        mListMembers.setOnItemLongClickListener(mMemberListViewLongClickListener);
    }


    private void initWidgets() {
        mCreate = (Button) mView.findViewById(R.id.btnCreateActivity);
        mRefresh = (Button) mView.findViewById(R.id.btnRefreshActivity);
        mListActivities = (ListView) mView.findViewById(R.id.listActivities);
        mListMembers = (ListView) mView.findViewById(R.id.listMemebers);
        mCreate.setOnClickListener(mBtnOnClickListener);
        mRefresh.setOnClickListener(mBtnOnClickListener);

        mPrgsDlg = new ProgressDialog(this.getContext());
        mPrgsDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mPrgsDlg.setTitle(this.getResources().getString(R.string.please_wait));
        mPrgsDlg.setMessage(this.getResources()
                .getString(R.string.reading_info));
        mPrgsDlg.setCanceledOnTouchOutside(false);
        mBtnContactsList = (Button) mView.findViewById(R.id.btnContacts);
        mBtnContactsList.setOnClickListener(mBtnOnClickListener);
        mETCreator = (EditText) mView.findViewById(R.id.etActivityCreator);
        mBtnQuery = (Button) mView.findViewById(R.id.btnQuery);
        mBtnQuery.setOnClickListener(mBtnOnClickListener);
    }

	private void popupContactListDialog() {
        // TODO Auto-generated method stub
        mListContactDlgLayout = mInflater.inflate(R.layout.invite_contact,
                (ViewGroup) mView.findViewById(R.id.invite_a_contact));
        mListViewContacts  = (ListView) mListContactDlgLayout.findViewById(R.id.list_activities);
        mListContactDlgBuilder  = new AlertDialog.Builder(mThis.getContext())
        .setTitle(R.string.choose_contact)
        .setView(mListContactDlgLayout);
        populateContacts();
        mListContactDlg = mListContactDlgBuilder.show();
    }

	private View mListContactDlgLayout = null;
    private Builder mListContactDlgBuilder = null;
    private AlertDialog mListContactDlg = null;
    private ListView mListViewContacts = null;
    private ArrayList<String> mListContactsSub = new ArrayList<String>();
    private EditText mETCreator = null;
    
    private Button mBtnContactsList = null;
    private Button mBtnQuery = null;
    
    private void populateContacts() {
        mListViewContacts.setAdapter(null);
        catContactsNameSub();
        ArrayAdapter<String> arrAdapter = new ArrayAdapter<String>(
                mView.getContext(),
                android.R.layout.simple_expandable_list_item_1,
                mListContactsSub);
        mListViewContacts.setAdapter(arrAdapter);
        mListViewContacts.setOnItemClickListener(mListViewContactsOnItemClickListener);
    }
    private void catContactsNameSub() {
        mListContactsSub.clear();
        for (Contact c : ((MainActivity)getContext()).getFragmentContacts().getContacts()) {
            mListContactsSub.add(c.getContactName() + SEP + c.getContactSubscription());
        }
    }
    
    private AdapterView.OnItemClickListener mListViewContactsOnItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            String s = mListViewContacts.getItemAtPosition(arg2).toString();
            if (s == null) {
                Log.e(TAG, "!!!ERROR!!! NULL selected contact");
            } else {
                mETCreator.setText(getSubFromNameSub(s));
                mListContactDlg.dismiss();
            }
}
    };
    
    private String getSubFromNameSub(String s) {
        return s.split(SEP)[1].trim();
    }
    private ArrayList<String> mActivityNames = new ArrayList<String>();
    private ArrayList<Group> mFetchedActivities = new ArrayList<Group>();
    private View mInviteContactDlgLayout = null;
    private Builder mJoininContactDlgBuilder = null;
    private AlertDialog mInviteContactDlg = null;
    private ListView mListViewActivities = null;
    
    private AdapterView.OnItemClickListener mActivitiesListViewOnItemClickListener = new AdapterView.OnItemClickListener() {

        private Group getSelectedActivities(String activityName) {
            Iterator<Group> it = mFetchedActivities.iterator();
            Group g = null;
            while (it.hasNext()) {
                 g = it.next();
                 if (activityName.equalsIgnoreCase(g.getName()))
                     return g;
            }
            return null;
        }
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            mCurrGroup = getSelectedActivities(mListViewActivities.getItemAtPosition(arg2).toString());
            if (mCurrGroup == null) {
                Log.e(TAG, "!!!ERROR!!! NULL selected activity");
            } else {
            	((MainActivity)mThis.getActivity()).sendJoininMessage(mCurrGroup.getOwnerId(),
                        "unknown", "unknown",
                        mCurrGroup.getName(),
                        mCurrGroup.getId(),
                        mCurrGroup.getOwnerId());
                mInviteContactDlg.dismiss();
                Toast t = Toast.makeText(mThis.getActivity(),
                        R.string.joinin_sent, Toast.LENGTH_SHORT);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
            }
        }
    };
    private Group mCurrGroup = null;
    
    @Override  
    public void onDestroyView() {  
         
		//TRServiceConnection.getInstance().stop();
		TRServiceConnection.getInstance().SetActiveGroupId(-1);
		TRServiceConnection.getInstance().SetMark(null);
		TRServiceConnection.getInstance().RemoveGroupListener(mGroupListener);
		TRServiceConnection.getInstance().RemoveHistoryListener(mHistoryListener);
		TRServiceConnection.getInstance().RemoveProfileListener(mProfileListener);
		super.onDestroyView(); 

    }  
    @Override  
    public void onDestroy() {  
    	if(TRServiceConnection.getInstance().GetActiveGroupId() != -1)
    	{
	    	String GroupID = GetGroupId(MakeGroupName(TRServiceConnection.getInstance().getActiveGroupName(),
					TRServiceConnection.getInstance().getActiveGroupSubscription()));
			if(GroupID != null)
			{
				EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
				EMConversation conversation = EMChatManager.getInstance().getConversation(GroupID);
				message.setChatType(ChatType.GroupChat);
				
				TextMessageBody txtBody = new TextMessageBody(getString(R.string.stopactivitymessage).replaceFirst("%1",TRServiceConnection.getInstance().GetName()));
				message.addBody(txtBody);
				message.setReceipt(GroupID);
				conversation.addMessage(message);
				
				EMChatManager.getInstance().sendMessage(message, new EMCallBack(){
	
					@Override
					public void onError(int arg0, String arg1) {
						
					}
	
					@Override
					public void onProgress(int arg0, String arg1) {
						
					}
	
					@Override
					public void onSuccess() {
						
					}});
			}     
    	}
		//TRServiceConnection.getInstance().stop();
		TRServiceConnection.getInstance().SetActiveGroupId(-1);
		TRServiceConnection.getInstance().SetMark(null);
		TRServiceConnection.getInstance().RemoveGroupListener(mGroupListener);
		TRServiceConnection.getInstance().RemoveHistoryListener(mHistoryListener);
		TRServiceConnection.getInstance().RemoveProfileListener(mProfileListener);
		super.onDestroy(); 
		Log.d(TAG, "onDestroy()");

    } 
}

