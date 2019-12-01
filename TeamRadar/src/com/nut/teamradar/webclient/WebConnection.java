package com.nut.teamradar.webclient;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;

import android.os.Looper;
import android.util.Log;

import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.nut.teamradar.Constant;
import com.nut.teamradar.Service.ApplicationData;
import com.nut.teamradar.base.BaseMessage;
import com.nut.teamradar.model.ActivityHistory;
import com.nut.teamradar.model.Contact;
import com.nut.teamradar.model.Group;
import com.nut.teamradar.model.Location;
import com.nut.teamradar.model.MarkerInfo;
import com.nut.teamradar.model.Member;
import com.nut.teamradar.model.Result;
import com.nut.teamradar.model.ShortMessage;
import com.nut.teamradar.model.User;
import com.nut.teamradar.util.Encrypt;

public class WebConnection {
	//private static final String SERVER = "http://192.168.1.102:8080/TeamRadarServer/";
	//private static final String SERVER = "http://192.168.1.113:8080/TeamRadarServer/";
	//private static final String SERVER = "http://192.168.137.131:8080/TeamRadarServer/";
	private static String TAG =  "WebConnection";
	public static int SUCCESS = 1;
	public static int FAILURE = 0;
	private String ServerAddr = null;
	// TODO: Debug Only
	/*
	private static String GetSessionIdURL = SERVER + "getsessionid.action?";
	private static String LoginActionURL = SERVER + "login.action?";
	private static String PushMessageActionURL = SERVER + "pushmessage.action?";
	private static String PullMessageActionURL = SERVER + "pullmessage.action?";
	private static String RegistrationActionURL = SERVER + "registration.action?";
	private static String UpdateProfileActionURL  = SERVER + "updateprofile.action?";
	private static String GetProfileActionURL = SERVER + "getprofile.action?";
	private static String QueryUserIDURL = SERVER + "queryuserid.action?";
	private static String GetActivitiesURL = SERVER + "getactivities.action?";
	private static String CreateActivityURL = SERVER + "createactivity.action?";
	private static String DeleteMemberURL = SERVER + "deletemember.action?";
	private static String DeleteActivityURL = SERVER + "deleteactivity.action?";
	private static String GetMembersURL = SERVER + "getmembers.action?";
	private static String GetJoinedActivitiesURL = SERVER + "getjoinedactivities.action?";
	private static String AddUserToActivityURL = SERVER + "addusertoactivity.action?";
	private static String DeleteAMemberFromActivityURL = SERVER + "deleteamemberfromactivity.action?";
	private static String GetActivitiesBySubscriptionURL = SERVER + "getactivitiesbysubscription.action?";
	private static String AddLocationURL = SERVER + "addlocation.action?";
	private static String GetAllLocationsURL = SERVER + "getalllocations.action?";
    private static String AddContactURL = SERVER + "addcontact.action?";
    private static String GetAllContactsURL = SERVER + "getcontactsbysubscription.action?";
    */
	public void SetServerAddr(String Addr)
	{
		ServerAddr = Addr;
	}
	private String GetServerAddress() {
	    return ServerAddr;
	}
	private String GetSessionIdURL() {
	    return GetServerAddress() + "getsessionid.action?";
	}
    private String LoginActionURL() {
        return GetServerAddress() + "login.action?";
    }
    private String PushMessageActionURL() {
        return GetServerAddress() + "pushmessage.action?";
    }
    private String PullMessageActionURL() {
        return GetServerAddress() + "pullmessage.action?";
    }
    private String RegistrationActionURL() {
        return GetServerAddress() + "registration.action?";
    }
    private String UpdateProfileActionURL() {
        return GetServerAddress() + "updateprofile.action?";
    }
    private String GetProfileActionURL() {
        return GetServerAddress() + "getprofile.action?";
    }
    private String QueryUserIDURL() {
        return GetServerAddress() + "queryuserid.action?";
    }
    private String GetActivitiesURL() {
        return GetServerAddress() + "getactivities.action?";
    }
    private String CreateActivityURL() {
        return GetServerAddress() + "createactivity.action?";
    }
    private String DeleteMemberURL() {
        return GetServerAddress() + "deletemember.action?";
    }
    private String DeleteActivityURL() {
        return GetServerAddress() + "deleteactivity.action?";
    }
    private String GetMembersURL() {
        return GetServerAddress() + "getmembers.action?";
    }
    private String GetJoinedActivitiesURL() {
        return GetServerAddress() + "getjoinedactivities.action?";
    }
    private String AddUserToActivityURL() {
        return GetServerAddress() + "addusertoactivity.action?";
    }
    private String DeleteAMemberFromActivityURL() {
        return GetServerAddress() + "deleteamemberfromactivity.action?";
    }
    private String GetActivitiesBySubscriptionURL() {
        return GetServerAddress() + "getactivitiesbysubscription.action?";
    }
    private String AddLocationURL() {
        return GetServerAddress() + "addlocation.action?";
    }
    private String GetAllLocationsURL() {
        return GetServerAddress() + "getalllocations.action?";
    }
    private String AddContactURL() {
        return GetServerAddress() + "addcontact.action?";
    }
    private String GetAllContactsURL() {
        return GetServerAddress() + "getcontactsbysubscription.action?";
    }
    private String GetUpdateRendezvousURL() {
        return GetServerAddress() + "updaterendezvous.action?";
    }
    private String GetRendezvousURL() {
        return GetServerAddress() + "getrendezvous.action?";
    }   
    private String UpdateUserNameActionURL() {
        return GetServerAddress() + "updateusername.action?";
    }
    private String UpdatePasswordActionURL() {
        return GetServerAddress() + "updatepassword.action?";
    }
    private String UpdateOccupationActionURL() {
        return GetServerAddress() + "updateoccupation.action?";
    }
    private String UpdateWeightActionURL() {
        return GetServerAddress() + "updateweight.action?";
    }
    private String UpdateHeightActionURL() {
        return GetServerAddress() + "updateheight.action?";
    }
    private String UpdateGenderActionURL() {
        return GetServerAddress() + "updategender.action?";
    }
    private String UpdateEmailActionURL() {
        return GetServerAddress() + "updateemail.action?";
    }
    private String UpdateBirthdayActionURL() {
        return GetServerAddress() + "updatebirthday.action?";
    }
    
    private String StartActivityActionURL() {
        return GetServerAddress() + "startactivity.action?";
    }
    private String GetActivityHistoryActionURL() {
        return GetServerAddress() + "getactivityhistory.action?";
    }
    private String GetHistoryLocationsActionURL() {
        return GetServerAddress() + "gethistorylocations.action?";
    }
    private String DeleteContactActionURL() {
        return GetServerAddress() + "deletecontact.action?";
    }
    private String CreateHXUserActionURL() {
        return GetServerAddress() + "createhxuser.action?";
    }
    
    private String ChangePasswordActionURL() {
        return GetServerAddress() + "changepassword.action?";
    }
    
    private String ChangeHXUserPasswordActionURL() {
        return GetServerAddress() + "changehxuserpassword.action?";
    }
    
	private HttpConnection mConnection = null;
	private WebAckCallBack<Integer,Integer> GetSessionIdCallBack;
	private WebAckCallBack<Integer,String> LoginCallBack;
	private WebAckCallBack<Integer,Integer> PushMessageCallBack;
	private WebAckCallBack<List<ShortMessage>,Integer> PullMessageCallBack;
	private WebAckCallBack<Integer,String> RegistrationCallBack;
	private WebAckCallBack<Integer,Integer> UpdateProfileCallBack;
	private WebAckCallBack<User,Integer> GetProfileCallBack;
	private WebAckCallBack<Integer,Integer> GetUserIdCallBack;
	private WebAckCallBack<List<Group>,Integer> GetActivitiesCallBack;
	private WebAckCallBack<Integer,Integer> CreateActivityCallBack;
	private WebAckCallBack<Integer,Integer> DeleteMemberCallBack;
	private WebAckCallBack<Integer,Integer> DeleteActivityCallBack;
	private WebAckCallBack<List<Member>,Integer> GetMembersCallBack;
	private WebAckCallBack<List<Group>,Integer> GetJoinedActivitiesCallBack;
	private WebAckCallBack<Integer,Integer> AddUserToActivityCallBack;
	private WebAckCallBack<Integer,Integer> DeleteAMemberFromActivityCallBack;
	private WebAckCallBack<List<Group>,Integer> GetActivitiesBySubscriptionCallBack;
	private WebAckCallBack<Integer,Integer> AddLocationCallBack;
	private WebAckCallBack<List<Location>,Integer> GetAllLocationsCallBack;	
    private WebAckCallBack<Integer, Integer> AddContactCallback;
    private WebAckCallBack<List<Contact>, Integer> GetAllContactsCallback;
    private WebAckCallBack<Integer,Integer> UpdateRendezvousCallBack;
    private WebAckCallBack<MarkerInfo,Integer> GetRendezvousCallBack;
    private WebAckCallBack<Integer,Integer> UpdateUserNameCallBack;
    private WebAckCallBack<Integer,Integer> UpdatePasswordCallBack;
    private WebAckCallBack<Integer,Integer> UpdateOccupationCallBack;
    private WebAckCallBack<Integer,Integer> UpdateWeightCallBack;
    private WebAckCallBack<Integer,Integer> UpdateHeightCallBack;
    private WebAckCallBack<Integer,Integer> UpdateGenderCallBack;
    private WebAckCallBack<Integer,Integer> UpdateEmailCallBack;
    private WebAckCallBack<Integer,Integer> UpdateBirthdayCallBack;
    private WebAckCallBack<Integer,Integer> StartActivityCallBack;
    private WebAckCallBack<List<ActivityHistory>,Integer> GetActivityHistoryCallBack;
    private WebAckCallBack<List<Location>,Integer> GetHistoryLocationsCallBack;	
    private WebAckCallBack<Integer,Integer> DeleteContactCallBack;
    private WebAckCallBack<Integer,Integer> CreateHXUserCallBack;
    private WebAckCallBack<Integer,Integer> ChangePasswordCallBack;
    private WebAckCallBack<Integer,Integer> ChangeHXUserPasswordCallBack;
    
    
	public WebConnection(HttpConnection connection)
	{
		//TAG = this.getClass().getName();
		mConnection = connection;
	}
	
	private BaseJsonHttpResponseHandler<Result> GetSessionIdResponser = new BaseJsonHttpResponseHandler<Result>() {

        @Override
        public void onFailure(int statusCode, Header[] headers,
                Throwable throwable, String rawJsonData, Result errorResponse) {
            if(GetSessionIdCallBack != null)
            	GetSessionIdCallBack.eventCallBack(FAILURE, null, 0);
            Log.e(TAG, "doGetSessionId(): get failed!");

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                String rawJsonResponse, Result Response) {
            if (Response != null) {
                if (Response.getData().equals("success")) {
	                if(GetSessionIdCallBack != null)
	                	GetSessionIdCallBack.eventCallBack(SUCCESS, 0, 0);
                } else {
                	if(GetSessionIdCallBack != null)
                		GetSessionIdCallBack.eventCallBack(FAILURE, null, 0);
                    Log.e(TAG, "doGetSessionId(): get failed!");
                }
            }
            else
            {
            	if(GetSessionIdCallBack != null)
            		GetSessionIdCallBack.eventCallBack(FAILURE, null, 0);
            	Log.e(TAG, "doGetSessionId(): get failed!");
            }

        }

        @Override
        protected Result parseResponse(String rawJsonData, boolean isFailure)
                throws Throwable {
            BaseMessage message = mConnection.getMessage(rawJsonData);
            return (Result) message.getResult("Result");
        }

    };
	
	public void doGetSessionId(WebAckCallBack<Integer,Integer> cb)
	{
		GetSessionIdCallBack = cb;
		 mConnection.doGet(GetSessionIdURL(), GetSessionIdResponser);		
	}
	private BaseJsonHttpResponseHandler<Result> LogInResponser =  new BaseJsonHttpResponseHandler<Result>() {

        @Override
        public void onFailure(int statusCode, Header[] headers,
                Throwable throwable, String rawJsonData,
                Result errorResponse) {
            Log.e(TAG, "doLogin onFailure");
            if(LoginCallBack != null)
            	LoginCallBack.eventCallBack(FAILURE ,0,null);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                String rawJsonResponse, Result Response) {
			String results[] = Response.getData().split("#");
			if (results[0].equals("success")) {
                /*for (int i = 0; i < headers.length; i++) {
                    if (headers[i].getName().equals(
                            "TeamRadarSessionId")) {
                        TeamRadarApplication.getInstance()
                                .setSessionId(
                                        headers[i].getValue());
                    }
                }*/
                if(LoginCallBack != null)
                	LoginCallBack.eventCallBack(SUCCESS ,Integer.valueOf(results[1]),results[2]);
			}
			else
			{
				if(LoginCallBack != null)
					LoginCallBack.eventCallBack(FAILURE ,0,null);
				Log.e(TAG, "doLogin onFailure");
			}                   	
        }

        @Override
        protected Result parseResponse(String rawJsonData,
                boolean isFailure) throws Throwable {
            BaseMessage message = mConnection.getMessage(rawJsonData);
            return (Result) message.getResult("Result");
        }

    };
	
    public void doLogin(String subscription,String password, 
    		WebAckCallBack<Integer,String> cb) {
        HashMap<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("username", Encrypt.Base64Encode(subscription));
        urlParams.put("password", Encrypt.Base64Encode(password));
        LoginCallBack = cb;
        mConnection.doPost(LoginActionURL(), urlParams,LogInResponser);
    }
    private BaseJsonHttpResponseHandler<Result> PushMessageResponser = new BaseJsonHttpResponseHandler<Result>() {

        @Override
        public void onFailure(int statusCode, Header[] headers,
                Throwable throwable, String rawJsonData,
                Result errorResponse) {
        	if(PushMessageCallBack != null)
        		PushMessageCallBack.eventCallBack(FAILURE, FAILURE, 0);
        	Log.e(TAG, "push message onFailure");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                String rawJsonResponse, Result Response) {
        	if(Response.getData().equals("success"))
        	{
        		if(PushMessageCallBack != null) 
        			PushMessageCallBack.eventCallBack(SUCCESS, SUCCESS, 0);
        	}
        	else
        	{
        		if(PushMessageCallBack != null)
        			PushMessageCallBack.eventCallBack(FAILURE, FAILURE, 0);
        		Log.e(TAG, "push message onFailure");
        	}
        }

        @Override
        protected Result parseResponse(String rawJsonData,
                boolean isFailure) throws Throwable {
            BaseMessage message = mConnection.getMessage(rawJsonData);
            return (Result) message.getResult("Result");
        }
    };
    public void doPushMessage(ShortMessage msg,WebAckCallBack<Integer,Integer> cb)
    {
    	PushMessageCallBack = cb;
        HashMap<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("shortMessage."+ShortMessage.COL_FROM, String.valueOf(msg.getFromId()));
        urlParams.put("shortMessage."+ShortMessage.COL_TO, String.valueOf(msg.getToId()));
        urlParams.put("shortMessage."+ShortMessage.COL_TONAME, msg.getToName());
        urlParams.put("shortMessage."+ShortMessage.COL_TOSUBSCRIPTION, String.valueOf(msg.getToSubscription()));
        urlParams.put("shortMessage."+ShortMessage.COL_FROMNAME, msg.getFromName());
        urlParams.put("shortMessage."+ShortMessage.COL_FROMSUBSCRIPTION, String.valueOf(msg.getFromSubscription()));
		urlParams.put("shortMessage."+ShortMessage.COL_MESSAGE, msg.getMessage());

        mConnection.doPost(PushMessageActionURL(), urlParams, PushMessageResponser);    	
    }
    private BaseJsonHttpResponseHandler<List<ShortMessage>> PullMessageResponser = new BaseJsonHttpResponseHandler<List<ShortMessage>>() {

        @Override
        public void onFailure(int statusCode, Header[] headers,
                Throwable throwable, String rawJsonData, List<ShortMessage> errorResponse) {
        	if(PullMessageCallBack != null)
        		PullMessageCallBack.eventCallBack(FAILURE, null, 0);
        	Log.e(TAG, "pull message onFailure");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                String rawJsonResponse, List<ShortMessage> Response) {
        	if(Response.size() > 0)
        	{
        		if(PullMessageCallBack != null)
        			PullMessageCallBack.eventCallBack(SUCCESS, Response, 0);
        	}
        	else
        	{
        		if(PullMessageCallBack != null)
        			PullMessageCallBack.eventCallBack(FAILURE, null, 0);
        		Log.e(TAG, "pull message onFailure");
        	}
       	
        }

        @Override
        protected List<ShortMessage> parseResponse(String rawJsonData, boolean isFailure)
                throws Throwable {
            BaseMessage message = mConnection.getMessage(rawJsonData);
            return (List<ShortMessage>) message.getResultList("ShortMessage");
        }
     };
	 public void doPullMessages(WebAckCallBack<List<ShortMessage>,Integer> cb)
	 {
		PullMessageCallBack = cb;
        HashMap<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("toId", String.valueOf(ApplicationData.getInstance().getCurrentUserId()));
        mConnection.doPost(PullMessageActionURL(), urlParams, PullMessageResponser);		 
	 }
	 
	 private BaseJsonHttpResponseHandler<Result> RegistrationResponser = new BaseJsonHttpResponseHandler<Result>() {

         @Override
         public void onFailure(int statusCode, Header[] headers,
                 Throwable throwable, String rawJsonData,
                 Result errorResponse) {
        	 if(RegistrationCallBack != null)
        		 RegistrationCallBack.eventCallBack(FAILURE, -1, null);
        	 Log.e(TAG, "push message onFailure");
         }

         @Override
         public void onSuccess(int statusCode, Header[] headers,
                 String rawJsonResponse, Result Response) {

					String results[] = Response.getData().split("#");
				if (results[0].equals("success")) {
                    for (int i = 0; i < headers.length; i++) {
                        if (headers[i].getName().equals(
                                "TeamRadarSessionId")) {
                        	ApplicationData
                                    .getInstance()
                                    .setSessionId(
                                            headers[i]
                                                    .getValue());
                        }
                    }
                    if(RegistrationCallBack != null)
                    	RegistrationCallBack.eventCallBack(SUCCESS, Integer.valueOf(results[1]), results[0]);
				}
				else
				{
					if(RegistrationCallBack != null)
						RegistrationCallBack.eventCallBack(FAILURE, -1, null);
					Log.e(TAG, "push message onFailure");
				}

         }

         @Override
         protected Result parseResponse(String rawJsonData,
                 boolean isFailure) throws Throwable {
             BaseMessage message = mConnection.getMessage(rawJsonData);
             return (Result) message.getResult("Result");
         }

     };
	 
	 public int doRegistration(User usr,WebAckCallBack<Integer,String> cb) {
		 RegistrationCallBack = cb;
         HashMap<String, String> urlParams = new HashMap<String, String>();
         urlParams.put("user." + User.COL_NAME, usr.getName());
         urlParams.put("user." + User.COL_SUBSCRIPTION, usr.getSubscription());
         urlParams.put("user." + User.COL_BIRTHDAY, usr.getBirthday());
         urlParams.put("user." + User.COL_HEIGHT, String.valueOf(usr.getHeight()));
         urlParams.put("user." + User.COL_WEIGHT, String.valueOf(usr.getWeight()));
         urlParams.put("user." + User.COL_GENDER, usr.getGender());
         urlParams.put("user." + User.COL_MAIL, usr.getMail());
         urlParams.put("user." + User.COL_PASSWORD, usr.getPassword());
         urlParams.put("user." + User.COL_PROFESSION, usr.getProfession());

         mConnection.doPost(RegistrationActionURL(), urlParams,
        		 RegistrationResponser);
         return 0;
     }
	 
	 private BaseJsonHttpResponseHandler<Result> UpdateProfileResponser = new BaseJsonHttpResponseHandler<Result>() {

         @Override
         public void onFailure(int statusCode, Header[] headers,
                 Throwable throwable, String rawJsonData,
                 Result errorResponse) {
             
             if(UpdateProfileCallBack != null)
             	UpdateProfileCallBack.eventCallBack(FAILURE, 0, 0);
             Log.e(TAG, "update profile onFailure");
         }

         @Override
         public void onSuccess(int statusCode, Header[] headers,
                 String rawJsonResponse, Result Response) {
         	if(UpdateProfileCallBack != null)
             	UpdateProfileCallBack.eventCallBack(SUCCESS, 0, 0);
         }

         @Override
         protected Result parseResponse(String rawJsonData,
                 boolean isFailure) throws Throwable {
             BaseMessage message = mConnection.getMessage(rawJsonData);
             return (Result) message.getResult("Result");
         }

     };
	   public void doUpdateProfile(User usr,WebAckCallBack<Integer,Integer> cb) {
		   UpdateProfileCallBack = cb;
	       HashMap<String, String> urlParams = new HashMap<String, String>();
	        urlParams.put("user." + User.COL_NAME, usr.getName());
	        urlParams.put("user." + User.COL_BIRTHDAY, usr.getBirthday());// Todo
	        urlParams.put("user." + User.COL_HEIGHT, String.valueOf((int) usr.getHeight()));
	        urlParams.put("user." + User.COL_WEIGHT, String.valueOf((int) usr.getWeight()));
	        urlParams.put("user." + User.COL_GENDER, usr.getGender());
	        urlParams.put("user." + User.COL_MAIL, usr.getMail());
	        urlParams.put("user." + User.COL_PASSWORD, usr.getPassword());
	        urlParams.put("user." + User.COL_PROFESSION, usr.getProfession());

	        mConnection.doPost(UpdateProfileActionURL(), urlParams, 
	        		UpdateProfileResponser);
	    }
	   
	   private BaseJsonHttpResponseHandler<User> GetProfileResponser = new BaseJsonHttpResponseHandler<User>() {

           @Override
           public void onFailure(int statusCode, Header[] headers,
                   Throwable throwable, String rawJsonData, User errorResponse) {
               if(GetProfileCallBack != null)
               	GetProfileCallBack.eventCallBack(FAILURE, null, 0);
               Log.e(TAG, "doGetProfile(): get failed!");

           }

           @Override
           public void onSuccess(int statusCode, Header[] headers,
                   String rawJsonResponse, User Response) {
               if (Response != null) {
                   if (Response.getId() != -1) {
   	                if(GetProfileCallBack != null)
   	                	GetProfileCallBack.eventCallBack(SUCCESS, Response, 0);
                   } else {
                   	if(GetProfileCallBack != null)
                   		GetProfileCallBack.eventCallBack(FAILURE, null, 0);
                       Log.e(TAG, "doGetProfile(): get failed!");
                   }
               }
               else
               {
               	if(GetProfileCallBack != null)
               		GetProfileCallBack.eventCallBack(FAILURE, null, 0);
               	Log.e(TAG, "doGetProfile(): get failed!");
               }

           }

           @Override
           protected User parseResponse(String rawJsonData, boolean isFailure)
                   throws Throwable {
               BaseMessage message = mConnection.getMessage(rawJsonData);
               return (User) message.getResult("User");
           }

       };
	   public void doGetProfile(WebAckCallBack<User,Integer> cb) {
		   GetProfileCallBack = cb;
		   mConnection.doGet(GetProfileActionURL(), GetProfileResponser);
	    }
	   private BaseJsonHttpResponseHandler<Result> UpdateRendezvousResponser = new BaseJsonHttpResponseHandler<Result>() {

           @Override
           public void onFailure(int statusCode, Header[] headers,
                   Throwable throwable, String rawJsonData,
                   Result errorResponse) {
               
               if(UpdateRendezvousCallBack != null)
               	UpdateRendezvousCallBack.eventCallBack(FAILURE, 0, 0);
               Log.e(TAG, "update rendezvous onFailure");
           }

           @Override
           public void onSuccess(int statusCode, Header[] headers,
                   String rawJsonResponse, Result Response) {
           	if(UpdateRendezvousCallBack != null)
           		UpdateRendezvousCallBack.eventCallBack(SUCCESS, 0, 0);
           }

           @Override
           protected Result parseResponse(String rawJsonData,
                   boolean isFailure) throws Throwable {
               BaseMessage message = mConnection.getMessage(rawJsonData);
               return (Result) message.getResult("Result");
           }

       };
	   public void doUpdateRendezvous(MarkerInfo Info,WebAckCallBack<Integer,Integer> cb) {
		   UpdateRendezvousCallBack = cb;
	       HashMap<String, String> urlParams = new HashMap<String, String>();
	        urlParams.put("markerInfo." + MarkerInfo.COL_ID, String.valueOf(Info.getId()));
	        urlParams.put("markerInfo." + MarkerInfo.COL_LATITUDE, String.valueOf(Info.getLatitude()));
	        urlParams.put("markerInfo." + MarkerInfo.COL_LONGITUDE, String.valueOf(Info.getLongitude()));
	        urlParams.put("markerInfo." + MarkerInfo.COL_EDITABLE, String.valueOf(Info.getEditable()));

	        mConnection.doPost(GetUpdateRendezvousURL(), urlParams,UpdateRendezvousResponser);
	    }
	   private BaseJsonHttpResponseHandler<MarkerInfo> GetRendezvousResponser = new BaseJsonHttpResponseHandler<MarkerInfo>() {

           @Override
           public void onFailure(int statusCode, Header[] headers,
                   Throwable throwable, String rawJsonData, MarkerInfo errorResponse) {
               if(GetRendezvousCallBack != null)
               	GetRendezvousCallBack.eventCallBack(FAILURE, null, 0);
               Log.e(TAG, "doGetRandezvous(): get failed!");

           }

           @Override
           public void onSuccess(int statusCode, Header[] headers,
                   String rawJsonResponse, MarkerInfo Response) {
               if (Response != null) {
                   if (Response.getId() != -1) {
   	                if(GetRendezvousCallBack != null)
   	                	GetRendezvousCallBack.eventCallBack(SUCCESS, Response, 0);
                   } else {
                   	if(GetRendezvousCallBack != null)
                   		GetRendezvousCallBack.eventCallBack(FAILURE, null, 0);
                       Log.e(TAG, "doGetRandezvous(): get failed!");
                   }
               }
               else
               {
               	if(GetRendezvousCallBack != null)
               		GetRendezvousCallBack.eventCallBack(FAILURE, null, 0);
               	Log.e(TAG, "doGetRandezvous(): get failed!");
               }

           }

           @Override
           protected MarkerInfo parseResponse(String rawJsonData, boolean isFailure)
                   throws Throwable {
               BaseMessage message = mConnection.getMessage(rawJsonData);
               return (MarkerInfo) message.getResult("MarkerInfo");
           }

       };
	   public void doGetRandezvous(long GroupId,WebAckCallBack<MarkerInfo,Integer> cb) {
		   GetRendezvousCallBack = cb;
		   HashMap<String, String> urlParams = new HashMap<String, String>();
	        urlParams.put("groupId", String.valueOf(GroupId));
		   mConnection.doPost(GetRendezvousURL(),urlParams, GetRendezvousResponser );
	    }
	   
	   private BaseJsonHttpResponseHandler<Result> GetUserIdResponser = new BaseJsonHttpResponseHandler<Result>() {

	        @Override
	        public void onFailure(int statusCode, Header[] headers,
	                Throwable throwable, String rawJsonData,
	                Result errorResponse) {
	            Log.e(TAG, "Query userid onFailure");
	            if(GetUserIdCallBack != null)
	            	GetUserIdCallBack.eventCallBack(FAILURE, 0, 0);
	        }

	        @Override
	        public void onSuccess(int statusCode, Header[] headers,
	                String rawJsonResponse, Result Response) {
	            if(GetUserIdCallBack != null)
	            	GetUserIdCallBack.eventCallBack(SUCCESS, Integer.valueOf(Response.getData()), 0);
	        }

	        @Override
	        protected Result parseResponse(String rawJsonData,
	                boolean isFailure) throws Throwable {
	            BaseMessage message = mConnection.getMessage(rawJsonData);
	            return (Result) message.getResult("Result");
	        }

	    };
	   
	   public void doGetUserId(String subscription,WebAckCallBack<Integer,Integer> cb)
	    {
		   GetUserIdCallBack = cb;
	        HashMap<String, String> urlParams = new HashMap<String, String>();
	        urlParams.put("subscription", subscription);
	        Log.d(TAG, "Start http connection thread");
	        mConnection.doPost(QueryUserIDURL(), urlParams, GetUserIdResponser);    	
	    }
	   private BaseJsonHttpResponseHandler<List<Group>> GetActivitysResponser = new BaseJsonHttpResponseHandler<List<Group>>() {

	        @Override
	        public void onFailure(int statusCode, Header[] headers,
	                Throwable throwable, String rawJsonData, List<Group> errorResponse) {
	            if(GetActivitiesCallBack != null)
	            	GetActivitiesCallBack.eventCallBack(FAILURE, null, 0);
	            Log.e(TAG, "Fetching activities failed");
	        }

	        @Override
	        public void onSuccess(int statusCode, Header[] headers,
	                String rawJsonResponse, List<Group> Response) {

	            if (Response != null) {
	                if (Response.size()>0) {
			            if(GetActivitiesCallBack != null)
			            	GetActivitiesCallBack.eventCallBack(SUCCESS, Response, 0);
	                } else {
			            if(GetActivitiesCallBack != null)
			            	GetActivitiesCallBack.eventCallBack(FAILURE, null, 0);
	                    Log.e(TAG, "Fetching activities failed");
	                }
	            }
	            else
	            {
		            if(GetActivitiesCallBack != null)
		            	GetActivitiesCallBack.eventCallBack(FAILURE, null, 0);
		            Log.e(TAG, "Fetching activities failed");
	            }
	        }

	        @Override
	        protected List<Group> parseResponse(String rawJsonData, boolean isFailure)
	                throws Throwable {
	            BaseMessage message = mConnection.getMessage(rawJsonData);
	            return (List<Group>) message.getResultList("Group");
	        }
	    };
	   public void doGetActivities(WebAckCallBack<List<Group>,Integer> cb) {

	        GetActivitiesCallBack = cb;
	        HashMap<String, String> urlParams = new HashMap<String, String>();
	        urlParams.put("ownerid", String.valueOf(ApplicationData.getInstance().getCurrentUserId()));
	        urlParams.put("flag", String.valueOf(Constant.USER_RULES));
	        mConnection.doPost(GetActivitiesURL(), urlParams, GetActivitysResponser);
	    }
	    private BaseJsonHttpResponseHandler<Result> CreateActivityResponser = new BaseJsonHttpResponseHandler<Result>() {

	        @Override
	        public void onFailure(int statusCode, Header[] headers,
	                Throwable throwable, String rawJsonData,
	                Result errorResponse) {
	        	if(CreateActivityCallBack != null)
	        		CreateActivityCallBack.eventCallBack(FAILURE, 0, 0);
	        	 Log.e(TAG, "create activity failed");
	        }

	        @Override
	        public void onSuccess(int statusCode, Header[] headers,
	                String rawJsonResponse, Result Response) {
	            if (Response != null) {
	                if (Response.getData().equals("success")) {
			        	if(CreateActivityCallBack != null)
			        		CreateActivityCallBack.eventCallBack(SUCCESS, 0, 0);
	                } else {
	                    Log.e(TAG, "create activity failed");
			        	if(CreateActivityCallBack != null)
			        		CreateActivityCallBack.eventCallBack(FAILURE, 0, 0);
	                }
	            }
	            else
	            {
		        	if(CreateActivityCallBack != null)
		        		CreateActivityCallBack.eventCallBack(FAILURE, 0, 0);
		        	 Log.e(TAG, "create activity failed");
	            }
	        }

	        @Override
	        protected Result parseResponse(String rawJsonData,
	                boolean isFailure) throws Throwable {
	            BaseMessage message = mConnection.getMessage(rawJsonData);
	            return (Result) message.getResult("Result");
	        }

	    };
	    public void doCreateActivity(String name , String subscription, String comment,WebAckCallBack<Integer,Integer> cb)
	    {
	    	CreateActivityCallBack = cb;
	        HashMap<String, String> urlParams = new HashMap<String, String>();
	        urlParams.put("group."+Group.COL_OWNERID, String.valueOf(ApplicationData.getInstance().getCurrentUserId())/*FragmentStartup.getSubscription()*/);
	        urlParams.put("group."+Group.COL_NAME, name);
	        urlParams.put("group."+Group.COL_SUBSCRIPTION, subscription);
	        urlParams.put("group."+Group.COL_COMMENT, comment);
	        Log.d(TAG, "Start http connection thread");
	        mConnection.doPost(CreateActivityURL(), urlParams, CreateActivityResponser);   	
	    }
	    private BaseJsonHttpResponseHandler<Result> DeleteMemberResponser = new BaseJsonHttpResponseHandler<Result>() {

	        @Override
	        public void onFailure(int statusCode, Header[] headers,
	                Throwable throwable, String rawJsonData,
	                Result errorResponse) {
	        	if(DeleteMemberCallBack != null)
	        		DeleteMemberCallBack.eventCallBack(FAILURE, 0, 0);
	        	Log.e(TAG, "delete member failed");
	        }

	        @Override
	        public void onSuccess(int statusCode, Header[] headers,
	                String rawJsonResponse, Result Response) {
	            if (Response != null) {
	                if (Response.getData().equals("success")) {
			        	if(DeleteMemberCallBack != null)
			        		DeleteMemberCallBack.eventCallBack(SUCCESS, 0, 0);
	                } else {
			        	if(DeleteMemberCallBack != null)
			        		DeleteMemberCallBack.eventCallBack(FAILURE, 0, 0);
	                	Log.e(TAG, "delete member failed");
	                }
	            }
	            else
	            {
		        	if(DeleteMemberCallBack != null)
		        		DeleteMemberCallBack.eventCallBack(FAILURE, 0, 0);
		        	Log.e(TAG, "delete member failed");
	            }
	        }
	        @Override
	        protected Result parseResponse(String rawJsonData,
	                boolean isFailure) throws Throwable {
	            BaseMessage message = mConnection.getMessage(rawJsonData);
	            return (Result) message.getResult("Result");
	        }

	    };
	    public void doDeleteMember(long id,WebAckCallBack<Integer,Integer> cb)
	    {
	    	DeleteMemberCallBack = cb;
	        HashMap<String, String> urlParams = new HashMap<String, String>();
	        urlParams.put("id", String.valueOf(id));
	        mConnection.doPost(DeleteMemberURL(), urlParams, DeleteMemberResponser);    	
	    }
	    private BaseJsonHttpResponseHandler<Result> DeleteActivityResponser = new BaseJsonHttpResponseHandler<Result>() {

	        @Override
	        public void onFailure(int statusCode, Header[] headers,
	                Throwable throwable, String rawJsonData,
	                Result errorResponse) {
	        	if(DeleteActivityCallBack != null)
	        		DeleteActivityCallBack.eventCallBack(FAILURE, 0, 0);
	        	Log.e(TAG, "create activity failed");
	        }

	        @Override
	        public void onSuccess(int statusCode, Header[] headers,
	                String rawJsonResponse, Result Response) {
	            if (Response != null) {
	                if (Response.getData().equals("success")) {
			        	if(DeleteActivityCallBack != null)
			        		DeleteActivityCallBack.eventCallBack(SUCCESS, 0, 0);
	                } else {
			        	if(DeleteActivityCallBack != null)
			        		DeleteActivityCallBack.eventCallBack(FAILURE, 0, 0);
	                	Log.e(TAG, "create activity failed");
	                }
	            }
	            else
	            {
		        	if(DeleteActivityCallBack != null)
		        		DeleteActivityCallBack.eventCallBack(FAILURE, 0, 0);
		        	Log.e(TAG, "create activity failed");
	            }
	        }
	        @Override
	        protected Result parseResponse(String rawJsonData,
	                boolean isFailure) throws Throwable {
	            BaseMessage message = mConnection.getMessage(rawJsonData);
	            return (Result) message.getResult("Result");
	        }

	    };
	    public void doDeleteActivity(long id,WebAckCallBack<Integer,Integer> cb)
	    {
	    	DeleteActivityCallBack = cb;
	        HashMap<String, String> urlParams = new HashMap<String, String>();
	        urlParams.put("activityid", String.valueOf(id));
	        mConnection.doPost(DeleteActivityURL(), urlParams, DeleteActivityResponser);    	
	    }   
	    private BaseJsonHttpResponseHandler<List<Member>> GetCurrentActivityMembersResponser = new BaseJsonHttpResponseHandler<List<Member>>() {

	        @Override
	        public void onFailure(int statusCode, Header[] headers,
	                Throwable throwable, String rawJsonData, List<Member> errorResponse) {
	            Log.e(TAG, "Fetching members failure");
	            if(GetMembersCallBack != null)
	            	GetMembersCallBack.eventCallBack(FAILURE, null, 0);
	            Log.e(TAG, "Fetching activities failed");
	        }

	        @Override
	        public void onSuccess(int statusCode, Header[] headers,
	                String rawJsonResponse, List<Member> Response) {

	            if (Response != null) {
	            	 if (Response.size()>0) {
	 		            if(GetMembersCallBack != null)
			            	GetMembersCallBack.eventCallBack(SUCCESS, Response, 0);
	                     
	                 } else {
	 		            if(GetMembersCallBack != null)
			            	GetMembersCallBack.eventCallBack(FAILURE, null, 0);
	                     Log.e(TAG, "Fetching activities failed");
	                 }
	            }
	            else
	            {
		            if(GetMembersCallBack != null)
		            	GetMembersCallBack.eventCallBack(FAILURE, null, 0);
		            Log.e(TAG, "Fetching activities failed");
	            }
	        }

	        @Override
	        protected List<Member> parseResponse(String rawJsonData, boolean isFailure)
	                throws Throwable {
	            BaseMessage message = mConnection.getMessage(rawJsonData);
	            return (List<Member>) message.getResultList("Member");
	        }

	    };
	    public void doGetCurrentActivityMembers(long activityid,WebAckCallBack<List<Member>,Integer> cb)
	    {
	    	GetMembersCallBack = cb;
	        HashMap<String, String> urlParams = new HashMap<String, String>();
	        urlParams.put("activityid", String.valueOf(activityid));
	        mConnection.doPost(GetMembersURL(), urlParams, GetCurrentActivityMembersResponser);
	    }
	    private BaseJsonHttpResponseHandler<List<Group>> GetJoinedActivitiesResponser = new BaseJsonHttpResponseHandler<List<Group>>() {

	        @Override
	        public void onFailure(int statusCode, Header[] headers,
	                Throwable throwable, String rawJsonData, List<Group> errorResponse) {
	            if(GetJoinedActivitiesCallBack != null)
	            	GetJoinedActivitiesCallBack.eventCallBack(FAILURE, null, 0);
	            Log.e(TAG, "Fetching Joined activities failed");
	        }

	        @Override
	        public void onSuccess(int statusCode, Header[] headers,
	                String rawJsonResponse, List<Group> Response) {

	            if (Response != null) {
	                if (Response.size()>0) {
			            if(GetJoinedActivitiesCallBack != null)
			            	GetJoinedActivitiesCallBack.eventCallBack(SUCCESS, Response, 0);
	                } else {
			            if(GetJoinedActivitiesCallBack != null)
			            	GetJoinedActivitiesCallBack.eventCallBack(FAILURE, null, 0);
	                    Log.e(TAG, "Fetching Joined activities failed");
	                }
	            }
	            else
	            {
		            if(GetJoinedActivitiesCallBack != null)
		            	GetJoinedActivitiesCallBack.eventCallBack(FAILURE, null, 0);
		            Log.e(TAG, "Fetching Joined activities failed");
	            }
	        }

	        @Override
	        protected List<Group> parseResponse(String rawJsonData, boolean isFailure)
	                throws Throwable {
	            BaseMessage message = mConnection.getMessage(rawJsonData);
	            return (List<Group>) message.getResultList("Group");
	        }
	    };
	    public void doGetJoinedActivities(int userid , WebAckCallBack<List<Group>,Integer> cb) {

	        GetJoinedActivitiesCallBack = cb;
	        HashMap<String, String> urlParams = new HashMap<String, String>();
	        urlParams.put("userid", String.valueOf(userid));
	        urlParams.put("flag", String.valueOf(Constant.USER_RULES));
	        mConnection.doPost(GetJoinedActivitiesURL(), urlParams, GetJoinedActivitiesResponser);
	    }
	    private BaseJsonHttpResponseHandler<Result> AddContactResponser = new BaseJsonHttpResponseHandler<Result>() {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, String rawJsonData,
                    Result errorResponse) {
                if (AddContactCallback != null)
                    AddContactCallback.eventCallBack(FAILURE, 0, 0);
                Log.e(TAG, "add contact failed");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                    String rawJsonResponse, Result Response) {
                if (Response != null) {
                    if (Response.getData().equals("success")) {
                        if (AddContactCallback != null)
                            AddContactCallback.eventCallBack(SUCCESS,
                                    0, 0);
                    } else {
                        Log.e(TAG, "add contact failed");
                        if (AddContactCallback != null)
                            AddContactCallback.eventCallBack(FAILURE,
                                    0, 0);
                    }
                } else {
                    if (AddContactCallback != null)
                        AddContactCallback.eventCallBack(FAILURE, 0, 0);
                    Log.e(TAG, "add contact failed");
                }
            }

            @Override
            protected Result parseResponse(String rawJsonData,
                    boolean isFailure) throws Throwable {
                BaseMessage message = mConnection
                        .getMessage(rawJsonData);
                return (Result) message.getResult("Result");
            }

        };
	    public void doAddContact(Contact contact,
	            WebAckCallBack<Integer, Integer> cb) {
	        AddContactCallback = cb;
	        HashMap<String, String> urlParams = new HashMap<String, String>();
	        urlParams.put("contact." + Contact.COL_OWNER_SUBSCRIPTION,
	                contact.getOwnerSubscription());
	        urlParams.put("contact." + Contact.COL_OWNER_NAME,
	                contact.getOwnerName());
	        urlParams.put("contact." + Contact.COL_CONTACT_SUBSCRIPTION,
	                contact.getContactSubscription());
	        urlParams.put("contact." + Contact.COL_CONTACT_NAME,
	                contact.getContactName());
	        Log.d(TAG, "Start http connection thread");
	        mConnection.doPost(AddContactURL(), urlParams,AddContactResponser);
	        return;
	    }
	    private BaseJsonHttpResponseHandler<Result> AddUserToActivityResponser = new BaseJsonHttpResponseHandler<Result>() {

	        @Override
	        public void onFailure(int statusCode, Header[] headers,
	                Throwable throwable, String rawJsonData, Result errorResponse) {
	            if(AddUserToActivityCallBack != null)
	            	AddUserToActivityCallBack.eventCallBack(FAILURE, 0, 0);
	            Log.e(TAG, "add user to activity failed");
	        }

	        @Override
	        public void onSuccess(int statusCode, Header[] headers,
	                String rawJsonResponse, Result Response) {

	            if (Response != null) {
	                if (Response.getData().equals("success")) {
			            if(AddUserToActivityCallBack != null)
			            	AddUserToActivityCallBack.eventCallBack(SUCCESS, 0, 0);
	                } else {
			            if(AddUserToActivityCallBack != null)
			            	AddUserToActivityCallBack.eventCallBack(FAILURE, 0, 0);
			            Log.e(TAG, "add user to activity failed");
	                }
	            }
	            else
	            {
		            if(AddUserToActivityCallBack != null)
		            	AddUserToActivityCallBack.eventCallBack(FAILURE, 0, 0);
		            Log.e(TAG, "add user to activity failed");
	            }
	        }

	        @Override
	        protected Result parseResponse(String rawJsonData, boolean isFailure)
	                throws Throwable {
	            BaseMessage message = mConnection.getMessage(rawJsonData);
	            return (Result) message.getResult("Result");
	        }
	    };
	    public void doAddUserToActivity(long userid ,long activityid,String username, WebAckCallBack<Integer,Integer> cb) {

	        AddUserToActivityCallBack = cb;
	        HashMap<String, String> urlParams = new HashMap<String, String>();
	        urlParams.put("userid", String.valueOf(userid));
	        urlParams.put("activityid", String.valueOf(activityid));
	        urlParams.put("username", username);
	        mConnection.doPost(AddUserToActivityURL(), urlParams, AddUserToActivityResponser);
	    }
	    private BaseJsonHttpResponseHandler<Result> DeleteAMemberFromActivityResponser = new BaseJsonHttpResponseHandler<Result>() {

	        @Override
	        public void onFailure(int statusCode, Header[] headers,
	                Throwable throwable, String rawJsonData,
	                Result errorResponse) {
	        	if(DeleteAMemberFromActivityCallBack != null)
	        		DeleteAMemberFromActivityCallBack.eventCallBack(FAILURE, 0, 0);
	        	Log.e(TAG, "delete member failed");
	        }

	        @Override
	        public void onSuccess(int statusCode, Header[] headers,
	                String rawJsonResponse, Result Response) {
	            if (Response != null) {
	                if (Response.getData().equals("success")) {
			        	if(DeleteAMemberFromActivityCallBack != null)
			        		DeleteAMemberFromActivityCallBack.eventCallBack(SUCCESS, 0, 0);
			        	Log.e(TAG, "delete member success");
	                } else {
			        	if(DeleteAMemberFromActivityCallBack != null)
			        		DeleteAMemberFromActivityCallBack.eventCallBack(FAILURE, 0, 0);
	                	Log.e(TAG, "delete member failed");
	                }
	            }
	            else
	            {
		        	if(DeleteAMemberFromActivityCallBack != null)
		        		DeleteAMemberFromActivityCallBack.eventCallBack(FAILURE, 0, 0);
		        	Log.e(TAG, "delete member failed");
	            }
	        }
	        @Override
	        protected Result parseResponse(String rawJsonData,
	                boolean isFailure) throws Throwable {
	            BaseMessage message = mConnection.getMessage(rawJsonData);
	            return (Result) message.getResult("Result");
	        }

	    };
	    public void doDeleteAMemberFromActivity(long userid, long groupid,WebAckCallBack<Integer,Integer> cb)
	    {
	    	DeleteAMemberFromActivityCallBack = cb;
	        HashMap<String, String> urlParams = new HashMap<String, String>();
	        urlParams.put("userid", String.valueOf(userid));
	        urlParams.put("activityid", String.valueOf(groupid));
	        mConnection.doPost(DeleteAMemberFromActivityURL(), urlParams,DeleteAMemberFromActivityResponser );    	
	    }
	    private BaseJsonHttpResponseHandler<List<Group>> GetActivityBySubScriptionResponser = new BaseJsonHttpResponseHandler<List<Group>>() {

	        @Override
	        public void onFailure(int statusCode, Header[] headers,
	                Throwable throwable, String rawJsonData, List<Group> errorResponse) {
	            if(GetActivitiesBySubscriptionCallBack != null)
	            	GetActivitiesBySubscriptionCallBack.eventCallBack(FAILURE, null, 0);
	            Log.e(TAG, "Fetching activities failed");
	        }

	        @Override
	        public void onSuccess(int statusCode, Header[] headers,
	                String rawJsonResponse, List<Group> Response) {

	            if (Response != null) {
	                if (Response.size()>0) {
			            if(GetActivitiesBySubscriptionCallBack != null)
			            	GetActivitiesBySubscriptionCallBack.eventCallBack(SUCCESS, Response, 0);
	                } else {
			            if(GetActivitiesBySubscriptionCallBack != null)
			            	GetActivitiesBySubscriptionCallBack.eventCallBack(FAILURE, null, 0);
	                    Log.e(TAG, "Fetching activities failed");
	                }
	            }
	            else
	            {
		            if(GetActivitiesBySubscriptionCallBack != null)
		            	GetActivitiesBySubscriptionCallBack.eventCallBack(FAILURE, null, 0);
		            Log.e(TAG, "Fetching activities failed");
	            }
	        }

	        @Override
	        protected List<Group> parseResponse(String rawJsonData, boolean isFailure)
	                throws Throwable {
	            BaseMessage message = mConnection.getMessage(rawJsonData);
	            return (List<Group>) message.getResultList("Group");
	        }
	    };
	   public void doGetActivitiesBySubscription(String sub,WebAckCallBack<List<Group>,Integer> cb) {

		   GetActivitiesBySubscriptionCallBack = cb;
	        HashMap<String, String> urlParams = new HashMap<String, String>();
	        urlParams.put("subscription",sub);
	        urlParams.put("flag", String.valueOf(Constant.USER_RULES));
	        mConnection.doPost(GetActivitiesBySubscriptionURL(), urlParams, GetActivityBySubScriptionResponser);
	    }
	   private BaseJsonHttpResponseHandler<Result> AddLocationResponser = new BaseJsonHttpResponseHandler<Result>() {

	        @Override
	        public void onFailure(int statusCode, Header[] headers,
	                Throwable throwable, String rawJsonData, Result errorResponse) {
	            if(AddLocationCallBack != null)
	            	AddLocationCallBack.eventCallBack(FAILURE, 0, 0);
	            Log.e(TAG, "Add Location failed");
	        }

	        @Override
	        public void onSuccess(int statusCode, Header[] headers,
	                String rawJsonResponse, Result Response) {

	            if (Response != null) {
	                if (Response.getData().equals("success")) {
			            if(AddLocationCallBack != null)
			            	AddLocationCallBack.eventCallBack(SUCCESS, 0, 0);
	                } else {
			            if(AddLocationCallBack != null)
			            	AddLocationCallBack.eventCallBack(FAILURE, 0, 0);
	                    Log.e(TAG, "add Location failed");
	                }
	            }
	            else
	            {
		            if(AddLocationCallBack != null)
		            	AddLocationCallBack.eventCallBack(FAILURE, 0, 0);
		            Log.e(TAG, "Add Location failed");
	            }
	        }

	        @Override
	        protected Result parseResponse(String rawJsonData, boolean isFailure)
	                throws Throwable {
	            BaseMessage message = mConnection.getMessage(rawJsonData);
	            return (Result) message.getResult("Result");
	        }
	    };
	   public void doAddLocation(Location loc,WebAckCallBack<Integer,Integer> cb) {

		   AddLocationCallBack = cb;
	        HashMap<String, String> urlParams = new HashMap<String, String>();
	        urlParams.put("location."+Location.COL_USERID,String.valueOf(loc.getUserid()));
	        urlParams.put("location."+Location.COL_ACTIVITYID,String.valueOf(loc.getGroupid()));
	        urlParams.put("location."+Location.COL_TIME,String.valueOf(loc.getTime()));
	        urlParams.put("location."+Location.COL_LATITUDE,String.valueOf(loc.getLatitude()));
	        urlParams.put("location."+Location.COL_LONGITUDE,String.valueOf(loc.getLongitude()));
	        urlParams.put("location."+Location.COL_ALTITUDE,String.valueOf(loc.getAltitude()));
	        urlParams.put("location."+Location.COL_SPEED,String.valueOf(loc.getSpeed()));
	        urlParams.put("location."+Location.COL_HEADING,String.valueOf(loc.getHeading()));
	        urlParams.put("location."+Location.COL_ACCURACY,String.valueOf(loc.getAccuracy()));
	        urlParams.put("location."+Location.COL_PROVIDER,loc.getProvider());
	        urlParams.put("location."+Location.COL_AVGCN0,String.valueOf(loc.getAveragecn0()));
	        urlParams.put("location."+Location.COL_MARK,String.valueOf(loc.getMark()));
	        urlParams.put("location."+Location.COL_FLAG,String.valueOf(loc.getFlag()));
	        mConnection.doPost(AddLocationURL(), urlParams, AddLocationResponser);
	    }
	   private BaseJsonHttpResponseHandler<List<Location>> GetAllLocationsResponser = new BaseJsonHttpResponseHandler<List<Location>>() {
			
		    @Override
		    public void onFailure(int statusCode, Header[] headers,
		            Throwable throwable, String rawJsonData, List<Location> errorResponse) {
		        if(GetAllLocationsCallBack != null)
		        	GetAllLocationsCallBack.eventCallBack(FAILURE, null, 0);
		        Log.e(TAG, "Fetching activities failed");
		}
		
		@Override
		public void onSuccess(int statusCode, Header[] headers,
		        String rawJsonResponse, List<Location> Response) {
		
		    if (Response != null) {
		        if (Response.size()>0) {
		            if(GetAllLocationsCallBack != null)
		            	GetAllLocationsCallBack.eventCallBack(SUCCESS, Response, 0);
		        } else {
		            if(GetAllLocationsCallBack != null)
		            	GetAllLocationsCallBack.eventCallBack(FAILURE, null, 0);
		            Log.e(TAG, "Fetching activities failed");
		        }
		    }
		    else
		    {
		        if(GetAllLocationsCallBack != null)
		        	GetAllLocationsCallBack.eventCallBack(FAILURE, null, 0);
		        Log.e(TAG, "Fetching activities failed");
		    }
		}
		
		@Override
		protected List<Location> parseResponse(String rawJsonData, boolean isFailure)
		        throws Throwable {
		    BaseMessage message = mConnection.getMessage(rawJsonData);
		    return (List<Location>) message.getResultList("Location");
		    }
		};
	   public void doGetAllLocations(long ActivityId,WebAckCallBack<List<Location>,Integer> cb) {

		    GetAllLocationsCallBack = cb;
			HashMap<String, String> urlParams = new HashMap<String, String>();
			urlParams.put("activityid",String.valueOf(ActivityId));
			mConnection.doPost(GetAllLocationsURL(), urlParams, GetAllLocationsResponser);
	    }
	   private BaseJsonHttpResponseHandler<List<Contact>> GetContactsResponser = new BaseJsonHttpResponseHandler<List<Contact>>() {
	        
           @Override
           public void onFailure(int statusCode, Header[] headers,
                   Throwable throwable, String rawJsonData, List<Contact> errorResponse) {
               if(GetAllContactsCallback != null)
                   GetAllContactsCallback.eventCallBack(FAILURE, null, 0);
               Log.e(TAG, "Fetching contacts failed");
       }
       
       @Override
       public void onSuccess(int statusCode, Header[] headers,
               String rawJsonResponse, List<Contact> Response) {
           if (Response != null) {
               if (Response.size()>0) {
                   if(GetAllContactsCallback != null)
                       GetAllContactsCallback.eventCallBack(SUCCESS, Response, 0);
               } else {
                   if(GetAllLocationsCallBack != null)
                       GetAllContactsCallback.eventCallBack(FAILURE, null, 0);
                   Log.e(TAG, "Fetching contacts failed");
               }
           }
           else
           {
               if(GetAllContactsCallback != null)
                   GetAllContactsCallback.eventCallBack(FAILURE, null, 0);
               Log.e(TAG, "Fetching contacts failed");
           }
       }
       
       @Override
       protected List<Contact> parseResponse(String rawJsonData, boolean isFailure)
               throws Throwable {
           BaseMessage message = mConnection.getMessage(rawJsonData);
           return (List<Contact>) message.getResultList("Contact");
           }
       };
    public void doGetContacts(String subscription,
            WebAckCallBack<List<Contact>, Integer> getContacts_cb) {
        GetAllContactsCallback = getContacts_cb;
        HashMap<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("subscription", subscription);
        mConnection.doPost(GetAllContactsURL(), urlParams, GetContactsResponser);
    }
    private BaseJsonHttpResponseHandler<Result> UpdateUserNameResponser = new BaseJsonHttpResponseHandler<Result>() {

        @Override
        public void onFailure(int statusCode, Header[] headers,
                Throwable throwable, String rawJsonData,
                Result errorResponse) {
            
            if(UpdateUserNameCallBack != null)
            	UpdateUserNameCallBack.eventCallBack(FAILURE, 0, 0);
            Log.e(TAG, "update username onFailure");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                String rawJsonResponse, Result Response) {
        	if(UpdateUserNameCallBack != null)
        		UpdateUserNameCallBack.eventCallBack(SUCCESS, 0, 0);
        }

        @Override
        protected Result parseResponse(String rawJsonData,
                boolean isFailure) throws Throwable {
            BaseMessage message = mConnection.getMessage(rawJsonData);
            return (Result) message.getResult("Result");
        }

    };
    public void doUpdateUserName(String username,WebAckCallBack<Integer,Integer> cb) {
	    UpdateUserNameCallBack = cb;
        HashMap<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("userName", username);

        mConnection.doPost(UpdateUserNameActionURL(), urlParams,UpdateUserNameResponser);
	    }
    private BaseJsonHttpResponseHandler<Result> UpdatePasswordResponser = new BaseJsonHttpResponseHandler<Result>() {

        @Override
        public void onFailure(int statusCode, Header[] headers,
                Throwable throwable, String rawJsonData,
                Result errorResponse) {
            
            if(UpdatePasswordCallBack != null)
            	UpdatePasswordCallBack.eventCallBack(FAILURE, 0, 0);
            Log.e(TAG, "update password onFailure");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                String rawJsonResponse, Result Response) {
        	if(UpdatePasswordCallBack != null)
        		UpdatePasswordCallBack.eventCallBack(SUCCESS, 0, 0);
        }

        @Override
        protected Result parseResponse(String rawJsonData,
                boolean isFailure) throws Throwable {
            BaseMessage message = mConnection.getMessage(rawJsonData);
            return (Result) message.getResult("Result");
        }

    };
    public void doUpdatePassword(String password,WebAckCallBack<Integer,Integer> cb) {
    	UpdatePasswordCallBack = cb;
        HashMap<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("password", password);

        mConnection.doPost(UpdatePasswordActionURL(), urlParams,UpdatePasswordResponser);
	    }
    private BaseJsonHttpResponseHandler<Result> UpdateOccupationResponser = new BaseJsonHttpResponseHandler<Result>() {

        @Override
        public void onFailure(int statusCode, Header[] headers,
                Throwable throwable, String rawJsonData,
                Result errorResponse) {
            
            if(UpdateOccupationCallBack != null)
            	UpdateOccupationCallBack.eventCallBack(FAILURE, 0, 0);
            Log.e(TAG, "update occupation onFailure");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                String rawJsonResponse, Result Response) {
        	if(UpdateOccupationCallBack != null)
        		UpdateOccupationCallBack.eventCallBack(SUCCESS, 0, 0);
        }

        @Override
        protected Result parseResponse(String rawJsonData,
                boolean isFailure) throws Throwable {
            BaseMessage message = mConnection.getMessage(rawJsonData);
            return (Result) message.getResult("Result");
        }

    };
    public void doUpdateOccupation(String occupation,WebAckCallBack<Integer,Integer> cb) {
    	UpdateOccupationCallBack = cb;
        HashMap<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("occupation", occupation);

        mConnection.doPost(UpdateOccupationActionURL(), urlParams,UpdateOccupationResponser );
	    }
    private BaseJsonHttpResponseHandler<Result> UpdateWeightResponser = new BaseJsonHttpResponseHandler<Result>() {

        @Override
        public void onFailure(int statusCode, Header[] headers,
                Throwable throwable, String rawJsonData,
                Result errorResponse) {
            
            if(UpdateWeightCallBack != null)
            	UpdateWeightCallBack.eventCallBack(FAILURE, 0, 0);
            Log.e(TAG, "update weight onFailure");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                String rawJsonResponse, Result Response) {
        	if(UpdateWeightCallBack != null)
        		UpdateWeightCallBack.eventCallBack(SUCCESS, 0, 0);
        }

        @Override
        protected Result parseResponse(String rawJsonData,
                boolean isFailure) throws Throwable {
            BaseMessage message = mConnection.getMessage(rawJsonData);
            return (Result) message.getResult("Result");
        }

    };
    public void doUpdateWeight(String weight,WebAckCallBack<Integer,Integer> cb) {
    	UpdateWeightCallBack = cb;
        HashMap<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("weight", weight);

        mConnection.doPost(UpdateWeightActionURL(), urlParams,UpdateWeightResponser);
	    }
    private BaseJsonHttpResponseHandler<Result> UpdateHeightResponser = new BaseJsonHttpResponseHandler<Result>() {

        @Override
        public void onFailure(int statusCode, Header[] headers,
                Throwable throwable, String rawJsonData,
                Result errorResponse) {
            
            if(UpdateHeightCallBack != null)
            	UpdateHeightCallBack.eventCallBack(FAILURE, 0, 0);
            Log.e(TAG, "update height onFailure");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                String rawJsonResponse, Result Response) {
        	if(UpdateHeightCallBack != null)
        		UpdateHeightCallBack.eventCallBack(SUCCESS, 0, 0);
        }

        @Override
        protected Result parseResponse(String rawJsonData,
                boolean isFailure) throws Throwable {
            BaseMessage message = mConnection.getMessage(rawJsonData);
            return (Result) message.getResult("Result");
        }

    };
    public void doUpdateHeight(String height,WebAckCallBack<Integer,Integer> cb) {
    	UpdateHeightCallBack = cb;
        HashMap<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("height", height);

        mConnection.doPost(UpdateHeightActionURL(), urlParams,UpdateHeightResponser);
	    }
    private BaseJsonHttpResponseHandler<Result> UpdateGenderResponser = new BaseJsonHttpResponseHandler<Result>() {

        @Override
        public void onFailure(int statusCode, Header[] headers,
                Throwable throwable, String rawJsonData,
                Result errorResponse) {
            
            if(UpdateGenderCallBack != null)
            	UpdateGenderCallBack.eventCallBack(FAILURE, 0, 0);
            Log.e(TAG, "update gender onFailure");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                String rawJsonResponse, Result Response) {
        	if(UpdateGenderCallBack != null)
        		UpdateGenderCallBack.eventCallBack(SUCCESS, 0, 0);
        }

        @Override
        protected Result parseResponse(String rawJsonData,
                boolean isFailure) throws Throwable {
            BaseMessage message = mConnection.getMessage(rawJsonData);
            return (Result) message.getResult("Result");
        }

    };
    public void doUpdateGender(String gender,WebAckCallBack<Integer,Integer> cb) {
    	UpdateGenderCallBack = cb;
        HashMap<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("gender", gender);

        mConnection.doPost(UpdateGenderActionURL(), urlParams,UpdateGenderResponser);
	    }
    private BaseJsonHttpResponseHandler<Result> UpdateEmailResponser = new BaseJsonHttpResponseHandler<Result>() {

        @Override
        public void onFailure(int statusCode, Header[] headers,
                Throwable throwable, String rawJsonData,
                Result errorResponse) {
            
            if(UpdateEmailCallBack != null)
            	UpdateEmailCallBack.eventCallBack(FAILURE, 0, 0);
            Log.e(TAG, "update email onFailure");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                String rawJsonResponse, Result Response) {
        	if(UpdateEmailCallBack != null)
        		UpdateEmailCallBack.eventCallBack(SUCCESS, 0, 0);
        }

        @Override
        protected Result parseResponse(String rawJsonData,
                boolean isFailure) throws Throwable {
            BaseMessage message = mConnection.getMessage(rawJsonData);
            return (Result) message.getResult("Result");
        }

    };
    public void doUpdateEmail(String email,WebAckCallBack<Integer,Integer> cb) {
    	UpdateEmailCallBack = cb;
        HashMap<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("email", email);

        mConnection.doPost(UpdateEmailActionURL(), urlParams,UpdateEmailResponser);
	    }
    private BaseJsonHttpResponseHandler<Result> UpdateBirthdayResponser = new BaseJsonHttpResponseHandler<Result>() {

        @Override
        public void onFailure(int statusCode, Header[] headers,
                Throwable throwable, String rawJsonData,
                Result errorResponse) {
            
            if(UpdateBirthdayCallBack != null)
            	UpdateBirthdayCallBack.eventCallBack(FAILURE, 0, 0);
            Log.e(TAG, "update birthday onFailure");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                String rawJsonResponse, Result Response) {
        	if(UpdateBirthdayCallBack != null)
        		UpdateBirthdayCallBack.eventCallBack(SUCCESS, 0, 0);
        }

        @Override
        protected Result parseResponse(String rawJsonData,
                boolean isFailure) throws Throwable {
            BaseMessage message = mConnection.getMessage(rawJsonData);
            return (Result) message.getResult("Result");
        }

    };
    public void doUpdateBirthday(String birthday,WebAckCallBack<Integer,Integer> cb) {
    	UpdateBirthdayCallBack = cb;
        HashMap<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("birthday", birthday);

        mConnection.doPost(UpdateBirthdayActionURL(), urlParams,UpdateBirthdayResponser);
	    }
    private BaseJsonHttpResponseHandler<Result> StartActivityResponser = new BaseJsonHttpResponseHandler<Result>() {

        @Override
        public void onFailure(int statusCode, Header[] headers,
                Throwable throwable, String rawJsonData,
                Result errorResponse) {
            
            if(StartActivityCallBack != null)
            	StartActivityCallBack.eventCallBack(FAILURE, 0, 0);
            Log.e(TAG, "start Activity onFailure");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                String rawJsonResponse, Result Response) {
        	if(StartActivityCallBack != null)
        		StartActivityCallBack.eventCallBack(SUCCESS, 0, 0);
        }

        @Override
        protected Result parseResponse(String rawJsonData,
                boolean isFailure) throws Throwable {
            BaseMessage message = mConnection.getMessage(rawJsonData);
            return (Result) message.getResult("Result");
        }

    };
    public void doStartActivity(ActivityHistory arecord,WebAckCallBack<Integer,Integer> cb)
    {
    	StartActivityCallBack = cb;
        HashMap<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("activityHistory."+ActivityHistory.COL_USERID, String.valueOf(arecord.getUserId()));
        urlParams.put("activityHistory."+ActivityHistory.COL_ACTIVITYID, String.valueOf(arecord.getActivityId()));
        urlParams.put("activityHistory."+ActivityHistory.COL_DATETIME, String.valueOf(arecord.getDatetime()));
        urlParams.put("activityHistory."+ActivityHistory.COL_MARK, String.valueOf(arecord.getMark()));
        mConnection.doPost(StartActivityActionURL(), urlParams,StartActivityResponser);	
    }
    private BaseJsonHttpResponseHandler<List<ActivityHistory>> GetActivityHistoryResponser = new BaseJsonHttpResponseHandler<List<ActivityHistory>>() {

        @Override
        public void onFailure(int statusCode, Header[] headers,
                Throwable throwable, String rawJsonData, List<ActivityHistory> errorResponse) {
            if(GetActivityHistoryCallBack != null)
            	GetActivityHistoryCallBack.eventCallBack(FAILURE, null, 0);
            Log.e(TAG, "Fetching activitie history failed");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                String rawJsonResponse, List<ActivityHistory> Response) {

            if (Response != null) {
                if (Response.size()>0) {
		            if(GetActivityHistoryCallBack != null)
		            	GetActivityHistoryCallBack.eventCallBack(SUCCESS, Response, 0);
                } else {
		            if(GetActivityHistoryCallBack != null)
		            	GetActivityHistoryCallBack.eventCallBack(FAILURE, null, 0);
                    Log.e(TAG, "Fetching activitie history failed");
                }
            }
            else
            {
	            if(GetActivityHistoryCallBack != null)
	            	GetActivityHistoryCallBack.eventCallBack(FAILURE, null, 0);
	            Log.e(TAG, "Fetching activitie history failed");
            }
        }

        @Override
        protected List<ActivityHistory> parseResponse(String rawJsonData, boolean isFailure)
                throws Throwable {
            BaseMessage message = mConnection.getMessage(rawJsonData);
            return (List<ActivityHistory>) message.getResultList("ActivityHistory");
        }
    };
    public void doGetAcivityHistory(long userid , long activityid, WebAckCallBack<List<ActivityHistory>,Integer> cb) {

        GetActivityHistoryCallBack = cb;
        HashMap<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("userId", String.valueOf(userid));
        urlParams.put("activityId", String.valueOf(activityid));
        mConnection.doPost(GetActivityHistoryActionURL(), urlParams,GetActivityHistoryResponser );
    }
    public BaseJsonHttpResponseHandler<List<Location>> GetHistoryLocationsResponser = new BaseJsonHttpResponseHandler<List<Location>>() {
		
	    @Override
	    public void onFailure(int statusCode, Header[] headers,
	            Throwable throwable, String rawJsonData, List<Location> errorResponse) {
	        if(GetHistoryLocationsCallBack != null)
	        	GetHistoryLocationsCallBack.eventCallBack(FAILURE, null, 0);
	        Log.e(TAG, "Fetching activities failed");
	}
	
	@Override
	public void onSuccess(int statusCode, Header[] headers,
	        String rawJsonResponse, List<Location> Response) {
	
	    if (Response != null) {
	        if (Response.size()>0) {
	            if(GetHistoryLocationsCallBack != null)
	            	GetHistoryLocationsCallBack.eventCallBack(SUCCESS, Response, 0);
	        } else {
	            if(GetHistoryLocationsCallBack != null)
	            	GetHistoryLocationsCallBack.eventCallBack(FAILURE, null, 0);
	            Log.e(TAG, "Fetching activities failed");
	        }
	    }
	    else
	    {
	        if(GetHistoryLocationsCallBack != null)
	        	GetHistoryLocationsCallBack.eventCallBack(FAILURE, null, 0);
	        Log.e(TAG, "Fetching activities failed");
	    }
	}
	
	@Override
	protected List<Location> parseResponse(String rawJsonData, boolean isFailure)
	        throws Throwable {
	    BaseMessage message = mConnection.getMessage(rawJsonData);
	    return (List<Location>) message.getResultList("Location");
	    }
	};
    public void doGetHistoryLocations(long userid,long ActivityId,String Mark,WebAckCallBack<List<Location>,Integer> cb) {

    	GetHistoryLocationsCallBack = cb;
		HashMap<String, String> urlParams = new HashMap<String, String>();
		urlParams.put("userId",String.valueOf(userid));
		urlParams.put("activityId",String.valueOf(ActivityId));
		urlParams.put("mark",String.valueOf(Mark));
		
		mConnection.doPost(GetHistoryLocationsActionURL(), urlParams, GetHistoryLocationsResponser);
    }
    private BaseJsonHttpResponseHandler<Result> DeletContactResponser = new BaseJsonHttpResponseHandler<Result>() {

        @Override
        public void onFailure(int statusCode, Header[] headers,
                Throwable throwable, String rawJsonData,
                Result errorResponse) {
        	if(DeleteContactCallBack != null)
        		DeleteContactCallBack.eventCallBack(FAILURE, 0, 0);
        	Log.e(TAG, "delete contact failed");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                String rawJsonResponse, Result Response) {
            if (Response != null) {
                if (Response.getData().equals("success")) {
		        	if(DeleteContactCallBack != null)
		        		DeleteContactCallBack.eventCallBack(SUCCESS, 0, 0);
                } else {
		        	if(DeleteContactCallBack != null)
		        		DeleteContactCallBack.eventCallBack(FAILURE, 0, 0);
                	Log.e(TAG, "delete contact failed");
                }
            }
            else
            {
	        	if(DeleteContactCallBack != null)
	        		DeleteContactCallBack.eventCallBack(FAILURE, 0, 0);
	        	Log.e(TAG, "delete contact failed");
            }
        }
        @Override
        protected Result parseResponse(String rawJsonData,
                boolean isFailure) throws Throwable {
            BaseMessage message = mConnection.getMessage(rawJsonData);
            return (Result) message.getResult("Result");
        }

    };
    public void doDeleteContact(long id,WebAckCallBack<Integer,Integer> cb)
    {
    	DeleteContactCallBack = cb;
        HashMap<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("id", String.valueOf(id));
        mConnection.doPost(DeleteContactActionURL(), urlParams, DeletContactResponser);    	
    } 
    private BaseJsonHttpResponseHandler<Result> CreateHXUserResponser =  new BaseJsonHttpResponseHandler<Result>() {

        @Override
        public void onFailure(int statusCode, Header[] headers,
                Throwable throwable, String rawJsonData,
                Result errorResponse) {
            Log.e(TAG, "CreateHXUser onFailure");
            if(CreateHXUserCallBack != null)
            	CreateHXUserCallBack.eventCallBack(FAILURE ,0,null);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                String rawJsonResponse, Result Response) {
			String results[] = Response.getData().split("#");
			if (results[0].equals("success")) {
                if(CreateHXUserCallBack != null)
                	CreateHXUserCallBack.eventCallBack(SUCCESS ,0,0);
			}
			else
			{
				if(CreateHXUserCallBack != null)
					CreateHXUserCallBack.eventCallBack(FAILURE ,0,0);
				Log.e(TAG, "CreateHXUser onFailure1");
			}                   	
        }

        @Override
        protected Result parseResponse(String rawJsonData,
                boolean isFailure) throws Throwable {
            BaseMessage message = mConnection.getMessage(rawJsonData);
            return (Result) message.getResult("Result");
        }

    };
    public void doCreateHXUser(String Username,String Passwrod ,WebAckCallBack<Integer,Integer> cb)
    {
        HashMap<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("username", Username);
        urlParams.put("password", Passwrod);
        CreateHXUserCallBack = cb;
        mConnection.doPost(CreateHXUserActionURL(), urlParams,CreateHXUserResponser); 	
    } 
    private BaseJsonHttpResponseHandler<Result> ChangePasswordResponser =  new BaseJsonHttpResponseHandler<Result>() {

        @Override
        public void onFailure(int statusCode, Header[] headers,
                Throwable throwable, String rawJsonData,
                Result errorResponse) {
            Log.e(TAG, "ChangePassword onFailure");
            if(ChangePasswordCallBack != null)
            	ChangePasswordCallBack.eventCallBack(FAILURE ,0,null);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                String rawJsonResponse, Result Response) {
			String results[] = Response.getData().split("#");
			if (results[0].equals("success")) {
                if(ChangePasswordCallBack != null)
                	ChangePasswordCallBack.eventCallBack(SUCCESS ,0,0);
			}
			else
			{
				if(ChangePasswordCallBack != null)
					ChangePasswordCallBack.eventCallBack(FAILURE ,0,0);
				Log.e(TAG, "ChangePassword onFailure");
			}                   	
        }

        @Override
        protected Result parseResponse(String rawJsonData,
                boolean isFailure) throws Throwable {
            BaseMessage message = mConnection.getMessage(rawJsonData);
            return (Result) message.getResult("Result");
        }

    };
    public void doChangePassword(String sub,String Passwrod ,WebAckCallBack<Integer,Integer> cb)
    {
        HashMap<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("subscription", sub);
        urlParams.put("password", Passwrod);
        ChangePasswordCallBack = cb;
        mConnection.doPost(ChangePasswordActionURL(), urlParams,ChangePasswordResponser); 	
    } 

    private BaseJsonHttpResponseHandler<Result> ChangeHXUserPasswordResponser =  new BaseJsonHttpResponseHandler<Result>() {

        @Override
        public void onFailure(int statusCode, Header[] headers,
                Throwable throwable, String rawJsonData,
                Result errorResponse) {
            Log.e(TAG, "ChangeHXUserPassword onFailure");
            if(ChangeHXUserPasswordCallBack != null)
            	ChangeHXUserPasswordCallBack.eventCallBack(FAILURE ,0,null);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                String rawJsonResponse, Result Response) {
			String results[] = Response.getData().split("#");
			if (results[0].equals("success")) {
                if(ChangeHXUserPasswordCallBack != null)
                	ChangeHXUserPasswordCallBack.eventCallBack(SUCCESS ,0,0);
			}
			else
			{
				if(ChangeHXUserPasswordCallBack != null)
					ChangeHXUserPasswordCallBack.eventCallBack(FAILURE ,0,0);
				Log.e(TAG, "ChangeHXUserPassword onFailure");
			}                   	
        }

        @Override
        protected Result parseResponse(String rawJsonData,
                boolean isFailure) throws Throwable {
            BaseMessage message = mConnection.getMessage(rawJsonData);
            return (Result) message.getResult("Result");
        }

    };
    public void doChangeHXUserPassword(String sub,String Passwrod ,WebAckCallBack<Integer,Integer> cb)
    {
        HashMap<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("subscription", sub);
        urlParams.put("password", Passwrod);
        ChangeHXUserPasswordCallBack = cb;
        mConnection.doPost(ChangeHXUserPasswordActionURL(), urlParams,ChangeHXUserPasswordResponser); 	
    } 
    
}
