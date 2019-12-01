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
import com.nut.teamradar.util.AppUtils;
import com.nut.teamradar.util.DBConnection;
import com.opensymphony.xwork2.ActionSupport;

public class createActivityAction extends ActionSupport implements ServletRequestAware,  
	ServletResponseAware {
	private String message;
	private String code;
	private Result result;
	private Group group;
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
	
	private String FindActivity()
	{
		String GroupID = null;
		try
		{
			ResultSet rs = null;
			String sql = "select * from activity where name ='" + 
	 			   group.getName()+ "' and ownerid ='" + group.getOwnerId() +"'";
			rs = DBConnection.getInstance().doQuery(sql);
			if(rs.next())
			{
				GroupID = String.valueOf(rs.getLong(Group.COL_ID));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return GroupID;
	}
	
	private boolean createActivity()
	{
		boolean status = false;
		try
		{
			String  sql = "insert into activity (name,ownerid,subscription,comment,available) values (\'"+
					   group.getName()+"\',\'"+
					   group.getOwnerId()+"\',\'"+
					   group.getSubscription()+"\',\'"+
					   group.getComment()+"\',\'1\')";
			status =DBConnection.getInstance().doInsert(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}
	private boolean ActiveActivity( String id)
	{
		boolean status = false;
		try
		{
			String  sql = "update activity set available=1 "+ 
				   		" where id='" + id +"'";
				   
		   status =DBConnection.getInstance().doUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	   return status;
	}
	private boolean addSelfToActivity(String groupid)
	{
		boolean status = false;
		try
		{
			String   sql = "insert into member (userid ,activityid,username,subscription,comment,available) values (\'"+
					   group.getOwnerId()+"\',\'"+
					   groupid+"\',\'"+
					   userInfo.get(Constant.SESSION_USER_NAME)+"\',\'"+
					   group.getSubscription()+"\',\'unknown\',1)";
			status =DBConnection.getInstance().doInsert(sql);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;   
	}
	private String FindMember(String groupid)
	{
		String MemberID = null;
		try{
			ResultSet rs = null;
			String sql = "select * from member where userid ='" + 
					group.getOwnerId()+ "' and activityid ='" + groupid +"'";
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
	
	 @Override
    public String execute() throws Exception {
    	JsonMessage msg = new JsonMessage();
    	result = new Result();
	    result.setData("failed");
    	msg.setCode("1");
    	msg.setMessage("login");
    	msg.setObject(result);
    	if(group.getOwnerId() != Long.valueOf(userInfo.get(Constant.SESSION_USER_ID).toString()))
    	{
    		return null;
    	}
    	try{
    	   boolean status = false;
    	   String GroupId= null;
    	   if(userInfo != null)
    	   {
    		   GroupId = FindActivity();
			   if(GroupId != null)
			   {
				   status = ActiveActivity(GroupId);
				   if(status)
				   {
					  String MemberID = FindMember(GroupId);
					  if(MemberID != null)
					  {
						  status = ActiveMember(MemberID);
					  }
					  else
					  {
						  status = addSelfToActivity(GroupId); 
					  }
					   if(status)
					   {
						   result.setData("success");
						   msg.setObject(result);
					   }
				   }
			   }
			   else
			   {
				   status = createActivity();
				   if(status)
				   {
					   GroupId = FindActivity();
			    	   if(GroupId != null)
			    	   {
						   status =addSelfToActivity(GroupId);
						   if(status)
						   {
							   result.setData("success");
							   msg.setObject(result);
						   }
			    	   }
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

	public void setGroup(Group grp)
	{
		 // avoid use create activity for someone else
		//if(grp.getOwnerId().equals(UserManager.get(Constant.SESSION_USER_ID).toString()))
		{
			this.group = grp;
		}
	}
	public Group getGroup()
	{
		return group;
	}
}