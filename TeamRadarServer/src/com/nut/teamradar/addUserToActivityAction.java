package com.nut.teamradar;

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.nut.teamradar.base.Constant;
import com.nut.teamradar.base.JsonMessage;
import com.nut.teamradar.model.Group;
import com.nut.teamradar.model.Member;
import com.nut.teamradar.model.Result;
import com.nut.teamradar.model.User;
import com.nut.teamradar.util.AppUtils;
import com.nut.teamradar.util.DBConnection;
import com.opensymphony.xwork2.ActionSupport;

public class addUserToActivityAction extends ActionSupport implements ServletRequestAware,  
	ServletResponseAware {
	private String message;
	private String code;
	private Result result;
	private UserInformation userInfo = null;
	private String SessionId=null;
	private HttpServletRequest request;  
	
	private HttpServletResponse response;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {  
		this.request = request;  
		SessionId = this.request.getHeader("TeamRadarSessionId");
	    if(SessionId != null)
	    {
	    	
	    	userInfo = SessionManager.getInstance().getUser(SessionId);
	    	//System.out.println(SessionId+":"+this.getClass().getName());
	    }
	}  
	@Override
	public void setServletResponse(HttpServletResponse response) {  
	    this.response = response;  
	} 
	private String FindMember()
	{
		String MemberID = null;
		try{
			ResultSet rs = null;
			String sql = "select * from member where userid ='" + 
					userid+ "' and activityid ='" + activityid +"'";
			rs = DBConnection.getInstance().doQuery(sql);
			if(rs.next())
			{
				MemberID = String.valueOf(rs.getLong(Member.COL_ID));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return MemberID;
	}
	private boolean ActiveMember(String Id)
	{
		boolean status = false;
		try {
			String  sql = "update member set available=1 "+ 
				   		" where id='" + Id +"'";
				   
		   status =DBConnection.getInstance().doUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	   return status;	
	}
	private User GetUserProfile()
	{
		User usr = null;
		try{
			ResultSet rs = null;
			String sql = "select * from user where id ='" + userid+ "'";
			rs = DBConnection.getInstance().doQuery(sql);
			if(rs.next())
			{
				usr = new User();
				usr.setName(rs.getString(User.COL_NAME));
				usr.setSubscription(rs.getString(User.COL_SUBSCRIPTION));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return usr;
	}
	 @Override
	public String execute() throws Exception {
		JsonMessage msg = new JsonMessage();
		result = new Result();
	    result.setData("failed");
		msg.setCode("1");
		msg.setMessage("login");
		msg.setObject(result);
		try{
		   boolean status = false;
    	   if(userInfo != null)
    	   {
	    	   String MemberId = FindMember();
	    	   if( MemberId == null)
	    	   {
	    		   System.out.println("New User");
	    		   User usr = GetUserProfile();
				   if(usr != null){
					   System.out.println("Add New User");
					   String sql = "insert into member (activityid,userid,username,subscription,comment,available) values (\'"+
							   activityid+"\',\'"+
							   userid+"\',\'"+
							   usr.getName()+"\',\'"+
							   usr.getSubscription()+"\',\'"+
							   "unknown"+"\',1)";
					   status =DBConnection.getInstance().doInsert(sql);
					   if(status)
					   {
						   result.setData("success");
						   msg.setObject(result);					   
					   }
				   }
	    	   }
	    	   else
	    	   {
	    		   System.out.println("User already in Members");
	    		   status = ActiveMember(MemberId);
				   if(status)
				   {
					   result.setData("success");
					   msg.setObject(result);					   
				   }
	    	   }
    	   }
		}catch(Exception e){
		   e.printStackTrace();
		}
		AppUtils.writeResponse(SessionId,this.getClass().getName(),msg, response);
	    return null; 
	}
	
	@SkipValidation
	public String form() throws Exception {
	    return INPUT;
	}	
	String userid;
	String activityid;
	String username;
	
	public void setUsername(String usrname)
	{
		this.username = usrname;
	}
	public String getUsername()
	{
		return username;
	}
	
	public void setActivityid(String actid)
	{
		this.activityid = actid;
	}
	public String getActivityid()
	{
		return activityid;
	}
	
	public void setUserid(String usrid)
	{
		this.userid = usrid;
	}
	public String getUserid()
	{
		return userid;
	}
}