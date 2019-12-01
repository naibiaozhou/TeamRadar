package com.nut.teamradar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.nut.teamradar.base.Constant;
import com.nut.teamradar.base.JsonMessage;
import com.nut.teamradar.model.Result;
import com.nut.teamradar.util.AppUtils;
import com.nut.teamradar.util.DBConnection;
import com.opensymphony.xwork2.ActionSupport;

public class ChangePasswordAction extends ActionSupport implements ServletRequestAware,  
	ServletResponseAware {
	private String message;
	private String code;
	private Result result;
	private String password = null;
	private String subscription=null;
	private boolean flag =false;
	private String SessionId=null;
	private HttpServletRequest request;  
	
	private HttpServletResponse response;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {  
		this.request = request;  
		SessionId = this.request.getHeader("TeamRadarSessionId");
	}  
	@Override
	public void setServletResponse(HttpServletResponse response) {  
	    this.response = response;  
	} 
	
	 @Override
	 public String execute() throws Exception {
    	boolean success = false;
    	JsonMessage msg = new JsonMessage();
    	result = new Result();
	    result.setData("failed");
    	msg.setCode("1");
    	msg.setMessage("login");
    	msg.setObject(result);
    	try{
    	   boolean status = false;
    	   
    	   String sql;
    	   if(subscription != null && password != null)
    	   {
			   sql = "update user set "+ 
	    	        "password="+"\'"+ password +"\'"+
			   		" where subscription='" + subscription +"'";
			   
			   status =DBConnection.getInstance().doUpdate(sql);
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
	public void setSubscription(String s)
	{
		subscription = s;
	}
	public String getSubscription()
	{
		return subscription;
	}
	public void setPassword(String p)
	{
		password = p;
	}
	public String getPassword()
	{
		return password;
	}
}