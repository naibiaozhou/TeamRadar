package com.nut.teamradar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nut.teamradar.base.JsonMessage;
import com.nut.teamradar.model.Result;
import com.nut.teamradar.util.AppUtils;
import com.nut.teamradar.util.Constants;
import com.nut.teamradar.util.DBConnection;
import com.nut.teamradar.util.EasemobIMUsers;
import com.opensymphony.xwork2.ActionSupport;

public class ChangeHXUserPasswordAction extends ActionSupport implements ServletRequestAware,  
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
		   if(subscription != null && password != null)
		   {
		        ObjectNode json2 = JsonNodeFactory.instance.objectNode();
		        json2.put("newpassword", password);
		        ObjectNode modifyIMUserPasswordWithAdminTokenNode = EasemobIMUsers.modifyIMUserPasswordWithAdminToken(subscription, json2);
		        if (null != modifyIMUserPasswordWithAdminTokenNode) {
				   result.setData("success");
				   msg.setObject(result);	
				   System.out.println("重置IM用户密码 提供管理员token: " + modifyIMUserPasswordWithAdminTokenNode.toString());
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
