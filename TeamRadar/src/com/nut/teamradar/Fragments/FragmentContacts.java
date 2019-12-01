package com.nut.teamradar.Fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.nut.teamradar.MainActivity;
import com.nut.teamradar.R;
import com.nut.teamradar.TRServiceConnection;
import com.nut.teamradar.TeamRadarApplication;
import com.nut.teamradar.adapter.ContactsListAdapter;
import com.nut.teamradar.base.BaseUi;
import com.nut.teamradar.model.Contact;
import com.nut.teamradar.model.Group;
import com.nut.teamradar.model.Member;
import com.nut.teamradar.model.PhoneContact;
import com.nut.teamradar.webclient.ITRContactListener;
import com.nut.teamradar.webclient.ITRGroupListener;
import com.nut.teamradar.webclient.WebAckCallBack;
import com.nut.teamradar.webclient.WebConnection;

public class FragmentContacts extends BaseUi {
    private static final String TAG = "FragmentContacts";
    private static final String SEP = ": ";

    private ArrayList<String> mContactNameSubs = null;
    //private ArrayList<String> mPhoneContactNameSubs = null;
    private ListView mListContacts = null;
    private ListView mPhoneListContacts = null;
    private Button mBtnAdd = null;
    private Button mBtnRefresh = null;
    //private Button mBtnImport = null;
    private View view = null;
    private LayoutInflater mInflater = null;
    private FragmentContacts mThis = null;
    private boolean InitialDownload = true;
    //private MailBox mMBX;
    private ArrayList<Contact> mContacts = new ArrayList<Contact>();  
    private ArrayList<Contact> mPhoneContacts = new ArrayList<Contact>();  
    private ContactsListAdapter ContactAdapter = null;
    private ContactsListAdapter PhoneContactAdapter = null;
    private boolean NeedPopUp = false;
    
    private Thread mThread=null;
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	TRServiceConnection.getInstance().Connect(this.getActivity().getApplicationContext());
        mInflater = inflater;
        mThis = this;
        view = inflater.inflate(R.layout.fragment_contacts, container, false);
        initWidgets();
        new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
	        	{
	    	        mContactNameSubs = new ArrayList<String>();
	    	       //mPhoneContactNameSubs = new ArrayList<String>();
	    	        
	    	        TRServiceConnection.getInstance().RegisterContactListener(mContactListener);
	    	        // ContextLoader.getInstance().registerObserver(this);
	    	        
	    	        mThread = new Thread(mRunable);
	    	        mThread.start();
	    	        mHandler.sendMessageDelayed(new Message(), 100);
	            }
				
			}
        },800);
        
        return view;
    }
    private boolean populateActivities(Iterator<Group> it) {
        while(it.hasNext())
        {
            Group g = it.next();
            if (!mActivityNames.contains(g.getName()))
            {
                Log.d(TAG, "Downloaded activity: " + g.getName());
                mActivities.add(g);
                mActivityNames.add(g.getName());
            }
        }
        Log.d(TAG, "Activities found: " + Integer.toString(mActivityNames.size()));
        return mActivities.size() > 0;
    }
    
    private void popupActivitiesSelectionDialog() {
    	mThis.getActivity().runOnUiThread(new Runnable(){

			@Override
			public void run() {
	            mInviteContactDlgLayout = mInflater.inflate(R.layout.invite_contact,
	                    (ViewGroup) view.findViewById(R.id.invite_a_contact));
	            mListViewActivities  = (ListView) mInviteContactDlgLayout.findViewById(R.id.list_activities);
	            mInviteContactDlgBuilder  = new AlertDialog.Builder(mThis.getContext())
	            .setTitle(R.string.invite_to_activity)
	            .setView(mInviteContactDlgLayout);
	            mInviteContactDlg = mInviteContactDlgBuilder.show();
			}
    		
    	});

    }
    private void getAllPhoneContacts()
    {
        Iterator<PhoneContact> it = TRServiceConnection
                .getInstance().GetContacts().iterator();
        mPhoneContacts.removeAll(mPhoneContacts);
        while (it.hasNext()) {
        	PhoneContact entry = it.next();
            addToPhoneContactList(new Contact(null, null, entry.SubScription,entry.Name));
        }
    }
    private ITRContactListener.Stub mContactListener = new ITRContactListener.Stub()
    {

		@Override
		public void OnDownloadContacts(int Flag, List<Contact> contacts)
				throws RemoteException {
			if (mPrgsDlg.isShowing()) {
                mPrgsDlg.dismiss();
            }
            if (Flag == WebConnection.SUCCESS) {
            	mContactNameSubs.removeAll(mContactNameSubs);
            	mContacts.removeAll(mContacts);
                mContactsPopulated = true;
                for(int i=0;i<contacts.size();i++)
                {
                	if(contacts.get(i).getId() != -1)
                	{
                		mContacts.add(contacts.get(i));
                	}
                }
            }
            getAllPhoneContacts();
            populateContacts();
			
		}

		@Override
		public void OnDeleteContact(int Flag) throws RemoteException {
			getAllContacts();
			
		}

		@Override
		public void OnAddContact(int Flag) throws RemoteException {
            if (Flag == WebConnection.SUCCESS) {
                // TODO:
                mContactsPopulated = false;
                getAllContacts();
            	Toast t = Toast.makeText(mThis.getActivity(),
	                    "add Friend success", Toast.LENGTH_LONG);
				t.show();
            }
            else
            {
            	Toast t = Toast.makeText(mThis.getActivity(),
	                    "add Friend failed", Toast.LENGTH_LONG);
				t.show();
            }
		}
    	
    };
    Runnable mRunable = new Runnable() {  
        @Override  
        public void run() {  
        	return;
        }  
    };
    private Handler mHandler = new Handler() {  
        public void handleMessage (Message msg) {
        	getAllContacts();
	        populateContacts();
        }  
    };  

    private ProgressDialog mPrgsDlg;

    public void SendAMessage() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private View mInviteContactDlgLayout = null;
    private Builder mInviteContactDlgBuilder = null;
    private AlertDialog mInviteContactDlg = null;
    private ListView mListViewActivities = null;
    
    private void initWidgets() {
        mListContacts = (ListView) view.findViewById(R.id.listContacts);
        mPhoneListContacts = (ListView) view.findViewById(R.id.phnoeContactsList);
        mBtnAdd = (Button) view.findViewById(R.id.btnAdd);
        mBtnRefresh = (Button) view.findViewById(R.id.btnContectRefresh);
        mBtnAdd.setOnClickListener(mBtnOnClickListener);
        mBtnRefresh.setOnClickListener(mBtnRefreshClickListener);
        mPrgsDlg = new ProgressDialog(mThis.getContext());
        mPrgsDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mPrgsDlg.setTitle(mThis.getResources().getString(R.string.please_wait));
        mPrgsDlg.setMessage(mThis.getResources().getString(R.string.reading_info));
        mPrgsDlg.setCanceledOnTouchOutside(false);
        
    }
        
    private void getAllContacts() {
        // get Buddies from contact list;
    	//if(mContacts != null && mContacts.size() == 0)
    	{
    		downloadAllContacts();
    	}
    }

    public List<Contact> getContacts() {
        return Collections.unmodifiableList(mContacts);
    }

    public List<String> getContactNames() {
        return Collections.unmodifiableList(mContactNameSubs);
    }
    
    private boolean contactExists(Contact c) {
        Iterator<Contact> it = mContacts.iterator();
        while(it.hasNext()) {
            if (c.getContactSubscription().equalsIgnoreCase(it.next().getContactSubscription()))
                return true;
        }
        return false;
    }
    
    private boolean addToContactList(Contact c) {
        if (!contactExists(c)) {
            mContacts.add(c);
            mContactNameSubs.add(c.getContactName() + SEP + c.getContactSubscription());
            return true;
        }
        return false;
    }
    
    private boolean addToPhoneContactList(Contact c) {
        if (!contactExists(c)) {
            mPhoneContacts.add(c);
            return true;
        }
        return false;
    }


    private Group mCurrGroup = null;
    private ArrayList<String> mActivityNames = new ArrayList<String>();
    private ArrayList<Group> mActivities = new ArrayList<Group>();
    
    private AdapterView.OnItemClickListener mActivitiesListViewOnItemClickListener = new AdapterView.OnItemClickListener() {

        private Group getSelectedActivities(String activityName) {
            Iterator<Group> it = mActivities.iterator();
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
            	((MainActivity)(mThis.getActivity())).sendInviteMessage(mCurrInvitee.getContactSubscription(),
                        mCurrGroup.getName(),
                        Long.toString(mCurrGroup.getId()),
                        Long.toString(mCurrGroup.getOwnerId()));
                mInviteContactDlg.dismiss();
                Toast t = Toast.makeText(mThis.getActivity(),
                        R.string.invitation_sent, Toast.LENGTH_SHORT);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
            }
        }
    };
    private Contact mCurrInvitee = null;
    private AdapterView.OnItemClickListener mListViewOnItemClickListener = new AdapterView.OnItemClickListener() {


        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
        	ContactAdapter.setSelectItem(arg2);
        	ContactAdapter.notifyDataSetChanged();
            Log.d(TAG, "Selected Item: #" + Integer.toString(mListContacts.getId()));
            Log.d(TAG, "arg0.getId: #" + Integer.toString(arg0.getId()));
            if (arg0.getId() == mListContacts.getId()) {
                //Log.d(TAG, "Selected Item: #" + Integer.toString(mListContacts.getId()));
            }
        }
    };
    
    private OnItemLongClickListener mListViewOnLongClickListener =
            new AdapterView.OnItemLongClickListener() {
    	AdapterView<?> mAdpView = null;
    	int row=0;

        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                int arg2, long arg3) {
        	mAdpView = arg0;
        	row = arg2;
        	ContactAdapter.setSelectItem(arg2);
        	ContactAdapter.notifyDataSetChanged();
        	AlertDialog OperationDlg = new AlertDialog.Builder(mThis.getActivity())
			.setTitle(getString(R.string.invitetoactivity))
			.setItems(new String[] {getString(R.string.selectactivity),getString(R.string.deletecontact)}, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog,int which) {
					switch(which)
					{
					case 0:
						if (populateActivities(TRServiceConnection.getInstance().getGroups().iterator())) {
		                    popupActivitiesSelectionDialog();
		                    if(mListViewActivities != null)
		                    {
			                    mListViewActivities.setAdapter(null);
			                    ArrayAdapter<String> arrAdapter = new ArrayAdapter<String>(
			                            view.getContext(),
			                            android.R.layout.simple_expandable_list_item_1,
			                            mActivityNames);
			                    mListViewActivities.setAdapter(arrAdapter);
			                    mListViewActivities.setOnItemClickListener(mActivitiesListViewOnItemClickListener);
		                    }
		                }
		                Log.d(TAG, "Conact to be invited: " + ContactAdapter.getItem(row).getContactSubscription());
		                mCurrInvitee = ContactAdapter.getItem(row);
		                if (mCurrInvitee == null) {
		                    Log.e(TAG, "!!!ERROR!!! NULL selected Contact");
			            } 
						break;
					case 1:
						TRServiceConnection.getInstance().DeleteContacts(ContactAdapter.getItem(row).getId());
						break;
					default:
							break;
					}
					dialog.dismiss();
				}
			}).show();
      	
            return false;
        }
    };
    
    private AdapterView.OnItemClickListener mPhoneListViewOnItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
        	PhoneContactAdapter.setSelectItem(arg2);
        	PhoneContactAdapter.notifyDataSetChanged();
        }
    };
    private OnItemLongClickListener mPhoneListViewOnLongClickListener =
            new AdapterView.OnItemLongClickListener() {
    	private int selectitem;
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                int arg2, long arg3) {
        	selectitem = arg2;
        	PhoneContactAdapter.setSelectItem(arg2);
        	PhoneContactAdapter.notifyDataSetChanged();
        	AlertDialog OperationDlg = new AlertDialog.Builder(mThis.getActivity())
			.setTitle(getString(R.string.addfriend))
			.setItems(new String[] {getString(R.string.addfriend)}, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog,int which) {
					switch(which)
					{
					case 0:
						String Subscription;
						Subscription = PhoneContactAdapter.getItem(selectitem).getContactSubscription();
						if(Subscription.startsWith("+86"))
						{
							Subscription = Subscription.substring(3, Subscription.length());
						}
						Contact c = new Contact(TRServiceConnection.getInstance()
			                    .ReadSubscription(), TRServiceConnection.getInstance()
			                    .ReadUserName(), Subscription,
			                    PhoneContactAdapter.getItem(selectitem).getContactName());
						addContact(c);
						break;
					default:
							break;
					}
					dialog.dismiss();
				}
			}).show();
            return false;
        }
    };
    private void downloadAllContacts() {
    	if(InitialDownload!=true)
    	{
    		mPrgsDlg.show();
    	}
    	TRServiceConnection.getInstance().DownloadContacts(TRServiceConnection.getInstance().GetSubscription());
    	getAllPhoneContacts();
        InitialDownload  = false;
        return;
    }

    private void populateContacts() {
        //if (mContacts.size() >0) 
    	{
            Log.d(TAG,
                    "populateContacts(): " + Integer.toString(mContacts.size())
                            + " contacts");
            mListContacts.setAdapter(null);
            ContactAdapter = null;
            ContactAdapter = new ContactsListAdapter(
                    view.getContext(),
                    android.R.layout.simple_expandable_list_item_1,
                    mContacts);
            mListContacts.setAdapter(ContactAdapter);
            mListContacts.setOnItemClickListener(mListViewOnItemClickListener );
            mListContacts.setOnItemLongClickListener(mListViewOnLongClickListener );
            ContactAdapter.notifyDataSetChanged();

        }
        //if(mPhoneContacts.size() > 0)
        {
	        mPhoneListContacts.setAdapter(null);
	        PhoneContactAdapter = null;
	        PhoneContactAdapter = new ContactsListAdapter(
	                view.getContext(),
	                android.R.layout.simple_expandable_list_item_1,
	                mPhoneContacts);
	        mPhoneListContacts.setAdapter(PhoneContactAdapter);
	        mPhoneListContacts.setOnItemClickListener(mPhoneListViewOnItemClickListener );
	        mPhoneListContacts.setOnItemLongClickListener(mPhoneListViewOnLongClickListener );
	        PhoneContactAdapter.notifyDataSetChanged();
        }
    }


    public void addContact(Contact contact) {
    	 TRServiceConnection.getInstance().AddContact(contact);
    }
    private View.OnClickListener mBtnRefreshClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			getAllContacts();
		}
    
    };
    private View.OnClickListener mBtnOnClickListener = new View.OnClickListener() {

        private View mAddContactDlgLayout;
        private Builder mAddContactDlgBuilder;

        private Contact createContact() {
            String contactSubscription, contactName;
            EditText etSubscription = (EditText) mAddContactDlgLayout
                    .findViewById(R.id.contact_subscription);
            EditText etCommnet = (EditText) mAddContactDlgLayout
                    .findViewById(R.id.contact_notes);
            contactSubscription = etSubscription.getText().toString();
            if (contactSubscription == null || contactSubscription.isEmpty())
                return null;
            contactName = etCommnet.getText().toString();
            contactName = contactName == null || contactName.isEmpty() ? "dummy"
                    : contactName;
            Log.e(TAG, "ContactSubscription:" + contactSubscription
                    + " ContactNotes:" + contactName);
            return new Contact(TRServiceConnection.getInstance()
                    .ReadSubscription(), TRServiceConnection.getInstance()
                    .ReadUserName(), contactSubscription, contactName);
        }

        @Override
        public void onClick(View arg0) {
            if (arg0.getId() == mBtnAdd.getId()) {
                Log.d(TAG, "Add button clicked");

                mAddContactDlgLayout = mInflater.inflate(R.layout.add_contact,
                        (ViewGroup) view.findViewById(R.id.add_a_contact));
                mAddContactDlgBuilder = new AlertDialog.Builder(mThis.getContext())
                        .setTitle(R.string.add_contact)
                        .setView(mAddContactDlgLayout)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0,
                                            int arg1) {
                                        Contact c = createContact();
                                        if (c != null) {
                                            addContact(c);
                                            //if (addContact(c))
                                            //    populateContacts();
                                        } else {

                                            Toast t = Toast.makeText(
                                                    mThis.getActivity(),
                                                    "subscription is mandatory!",
                                                    Toast.LENGTH_SHORT);
                                            t.setGravity(Gravity.CENTER, 0, 0);
                                            t.show();
                                        }
                                    }
                                }).setNegativeButton("cancel", null);
                mAddContactDlgBuilder.show();

            }
        }
    };
    
    private boolean mContactsPopulated = false;

    @Override
    public void onResume() {
        super.onResume();
        if (!mContactsPopulated ) {
            populateContacts();
        }
    }
    @Override  
    public void onDestroyView() {  
    	TRServiceConnection.getInstance().RemoveContactListener(mContactListener);
        super.onDestroyView();  
        
    } 
    @Override  
    public void onDestroy() {  
    	TRServiceConnection.getInstance().RemoveContactListener(mContactListener);
        super.onDestroy();  
        System.out.println("onDestroy");  
    } 
}
