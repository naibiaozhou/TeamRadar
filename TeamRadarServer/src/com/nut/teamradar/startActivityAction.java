package com.nut.teamradar;

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.nut.teamradar.base.JsonMessage;
import com.nut.teamradar.model.ActivityHistory;
import com.nut.teamradar.model.Result;
import com.nut.teamradar.util.AppUtils;
import com.nut.teamradar.util.DBConnection;
import com.opensymphony.xwork2.ActionSupport;

public class startActivityAction extends ActionSupport implements
	ServletRequestAware, ServletResponseAware {
	private ActivityHistory activityHistory = null;
	private String SessionId = null;
	private UserInformation userInfo = null;
	private HttpServletRequest mRequest;
	private HttpServletResponse mResponse;
	private Result result;
	
	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		mRequest = arg0;
		SessionId = arg0.getHeader("TeamRadarSessionId");
		if (SessionId != null) {
			userInfo = SessionManager.getInstance().getUser(SessionId);
			//System.out.println(SessionId+":"+this.getClass().getName());
		}
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
	   		String sql = "insert into activityhistory (userid,activityid,datetime,mark) values (\'"+
	   				activityHistory.getUserId()+"\',\'"+
	   				activityHistory.getActivityId()+"\',\'"+
	   				activityHistory.getDatetime()+"\',\'"+
	   				activityHistory.getMark()+"\')";
	   		status =DBConnection.getInstance().doInsert(sql);
	   		if(status)
	   		{
	   			result.setData("success");
	   			msg.setObject(result);
	   		}
		}
	}catch(Exception e){
	   e.printStackTrace();
	}
	AppUtils.writeResponse(SessionId, this.getClass().getName(), msg,
			mResponse);
	return null;
	}

	
	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		mResponse = arg0;
	}
	
	public ActivityHistory getActivityHistory() {
		return activityHistory;
	}
		
	public void setActivityHistory(ActivityHistory a) {
		activityHistory = a;
	}
	
	@SkipValidation
	public String form() throws Exception {
		return INPUT;
	}
}
