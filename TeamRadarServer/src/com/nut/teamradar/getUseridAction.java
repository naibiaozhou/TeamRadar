package com.nut.teamradar;

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.nut.teamradar.base.Constant;
import com.nut.teamradar.base.JsonMessage;
import com.nut.teamradar.model.Result;
import com.nut.teamradar.model.User;
import com.nut.teamradar.util.AppUtils;
import com.nut.teamradar.util.DBConnection;
import com.opensymphony.xwork2.ActionSupport;

public class getUseridAction extends ActionSupport implements ServletRequestAware,  
	ServletResponseAware {  
	private String message;
	private String code;
	private Result result;
	private boolean flag;
	private UserInformation userInfo=null;
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
        	if(userInfo != null)
        	{
        		//System.out.println(SessionId+":"+this.getClass().getName()+" : Session OK!");
        	}
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
		   ResultSet rs = null;
		   if(userInfo != null)
		   {
			   String sql = "select * from user where subscription ='" + subscription +"'";
			   rs =DBConnection.getInstance().doQuery(sql);
			   if(rs.next()){
				   result.setData("success#"+rs.getLong(User.COL_ID));
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

	private String subscription;
	
	public String getSubscription() {
	    return subscription;
	}
	
	public void setSubscription(String subscription) {
	    this.subscription = subscription;
	}
}