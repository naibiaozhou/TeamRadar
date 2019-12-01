package com.nut.teamradar;

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.nut.teamradar.base.JsonMessage;
import com.nut.teamradar.model.ActivityHistory;
import com.nut.teamradar.model.Member;
import com.nut.teamradar.util.AppUtils;
import com.nut.teamradar.util.DBConnection;
import com.opensymphony.xwork2.ActionSupport;

public class getActivityHistoryAction extends ActionSupport implements ServletRequestAware,
	ServletResponseAware {
	private String userId=null;
	private UserInformation userInfo = null;
	private String SessionId=null;
	private HttpServletRequest request;  
	
	private HttpServletResponse response;
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
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
	
	@Override
	public String execute() throws Exception {
		int row =0;
		JsonMessage msg = new JsonMessage();
		msg.setCode("1");
		msg.setMessage("fetch");
		try{
		   ResultSet rs = null;
		   if(userInfo != null)
		   {
			   String sql= "select * from activityhistory where activityid ='" + 
					   activityId + "' and userid ='"+ userId +"'";				   
			   rs = DBConnection.getInstance().doQuery(sql);
			   while(rs.next())
			   {
				   ActivityHistory history = new ActivityHistory();
				   history.setId(rs.getLong(ActivityHistory.COL_ID));
				   history.setActivityId(rs.getLong(ActivityHistory.COL_ACTIVITYID));
				   history.setUserId(rs.getLong(ActivityHistory.COL_USERID));
				   history.setDatetime(rs.getString(ActivityHistory.COL_DATETIME));
				   history.setMark(rs.getString(ActivityHistory.COL_MARK));
				   msg.addObject(history);
				   row++;
			   }
		   }
		} catch (Exception e){
		   e.printStackTrace();
		}
	   if(row==0)
	   {
		   ActivityHistory history = new ActivityHistory();
		   msg.addObject(history);
	   }
		AppUtils.writeResponse(SessionId,this.getClass().getName(),msg, response);
		return null; 
	}
	
	@SkipValidation
	public String form() throws Exception {
		return INPUT;
	}
	
	private String activityId = null;
	
	
	public String getActivityId() {
		return activityId;
	}
	
	public void setActivityId(String activityid) {
		this.activityId = activityid;
	}
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String id) {
		this.userId = id;
	}
}