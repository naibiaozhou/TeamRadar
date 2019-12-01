package com.nut.teamradar;

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.nut.teamradar.base.JsonMessage;
import com.nut.teamradar.model.Result;
import com.nut.teamradar.util.AppUtils;
import com.nut.teamradar.util.DBConnection;
import com.opensymphony.xwork2.ActionSupport;

public class deleteActivityAction extends ActionSupport implements ServletRequestAware,
	ServletResponseAware {
	private Result result;
	private UserInformation userInfo = null;
	private String SessionId=null;
	private HttpServletRequest request;  
	
	private HttpServletResponse response;
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;  
		SessionId = this.request.getHeader("TeamRadarSessionId");
        if(SessionId != null)
        {
        	
        	userInfo = SessionManager.getInstance().getUser(SessionId);
        	
        	//System.out.println(SessionId+":"+this.getClass().getName()+" : Session OK!");
        }
	}
	private boolean InactiveMembers()
	{
		boolean status = false;
		try {
			String  sql = "update member set available=0 "+ 
				   		" where activityid='" + activityid +"'";
				   
		   status =DBConnection.getInstance().doUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	   return status;	
	}
	private boolean InactiveActivity()
	{
		boolean status = false;
		try {
			String  sql = "update activity set available=0 "+ 
			   		" where id='" + activityid +"'";
				   
		   status =DBConnection.getInstance().doUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	   return status;	
	}
	private boolean DeleteActivity()
	{
		boolean status = false;
		try {
			String  sql = "delete from activity where id ='" + 
					   activityid + "'";
				   
		   status =DBConnection.getInstance().doUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}
	private boolean DeleteMembers()
	{
		boolean status = false;
		try {
			String  sql = "delete from member where activityid ='" + 
					   activityid + "'";	
				   
		   status =DBConnection.getInstance().doUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}
	@Override
	public String execute() throws Exception {
		JsonMessage msg = new JsonMessage();
		boolean status = false;
		result = new Result();
	    result.setData("failed");
    	msg.setCode("1");
    	msg.setMessage("login");
    	msg.setObject(result);
		try{
		   //ResultSet rs = null;
			if(userInfo != null)
			{
				status = InactiveMembers();
				status = InactiveActivity();
				if(status)
				{
					   result.setData("success");
					   msg.setObject(result);
				}
			}
		} catch (Exception e){
		   e.printStackTrace();
		}
		AppUtils.writeResponse(SessionId,this.getClass().getName(),msg, response);
		return null; 
	}
	
	@SkipValidation
	public String form() throws Exception {
		return INPUT;
	}
	
	private String activityid = null;
	
	public String getActivityid() {
		return activityid;
	}
	
	public void setActivityid(String activityid) {
		this.activityid = activityid;
	}
}
