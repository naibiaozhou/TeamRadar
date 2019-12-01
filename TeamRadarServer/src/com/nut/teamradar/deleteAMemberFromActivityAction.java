package com.nut.teamradar;

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

public class deleteAMemberFromActivityAction extends ActionSupport implements ServletRequestAware,
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
		this.request = request;  
		SessionId = this.request.getHeader("TeamRadarSessionId");
	    if(SessionId != null)
	    {
	    	
	    	userInfo = SessionManager.getInstance().getUser(SessionId);
	    	//System.out.println(SessionId+":"+this.getClass().getName()+" : Session OK!");
	    }
	}
	private boolean DeleteMember()
	{
		boolean status = false;
		try {
			String  sql = "delete from member where userid ='" + userid + "' and activityid = '"+activityid+"'";
				   
		   status =DBConnection.getInstance().doUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}
	private boolean InactiveMember()
	{
		boolean status = false;
		try {
			String  sql = "update member set available=0 "+ 
				   		" where userid ='" + userid + "' and activityid = '"+activityid+"'";
				   
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
			if(userInfo != null)
			{
			   status = InactiveMember();
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
	
	private String userid = null;
	
	public String getUserid() {
		return userid;
	}
	
	public void setUserid(String id) {
		this.userid = id;
	}
	
	private String activityid = null;
	
	public String getActivityid() {
		return activityid;
	}
	
	public void setActivityid(String id) {
		this.activityid = id;
	}
}