package com.nut.teamradar;

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.nut.teamradar.base.Constant;
import com.nut.teamradar.base.JsonMessage;
import com.nut.teamradar.model.Location;
import com.nut.teamradar.model.Result;
import com.nut.teamradar.util.AppUtils;
import com.nut.teamradar.util.DBConnection;
import com.opensymphony.xwork2.ActionSupport;

public class addLocationAction extends ActionSupport implements ServletRequestAware,  
	ServletResponseAware {
	private String message;
	private String code;
	private Result result;
	private Location mLocation;
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
		   		String sql = "insert into location (groupid,userid,time,provider,latitude,longitude,altitude,speed,heading,accuracy,averagecn0,mark,flag) values (\'"+
					   mLocation.getGroupid()+"\',\'"+
					   mLocation.getUserid()+"\',\'"+
					   mLocation.getTime()+"\',\'"+
					   mLocation.getProvider()+"\',\'"+
					   mLocation.getLatitude()+"\',\'"+
					   mLocation.getLongitude()+"\',\'"+
					   mLocation.getAltitude()+"\',\'"+
					   mLocation.getSpeed()+"\',\'"+
					   mLocation.getHeading()+"\',\'"+
					   mLocation.getAccuracy()+"\',\'"+
					   mLocation.getAveragecn0()+"\',\'"+
					   mLocation.getMark()+"\',\'"+
					   mLocation.getFlag()+"\')";
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
		AppUtils.writeResponse(SessionId,this.getClass().getName(),msg, response);
	    return null; 
	}
	
	@SkipValidation
	public String form() throws Exception {
	    return INPUT;
	}	
	
	public void setLocation(Location loc)
	{
		 // avoid use create activity for someone else
		//if(grp.getOwnerId().equals(UserManager.get(Constant.SESSION_USER_ID).toString()))
		{
			this.mLocation = loc;
		}
	}
	public Location getLocation()
	{
		return mLocation;
	}
}