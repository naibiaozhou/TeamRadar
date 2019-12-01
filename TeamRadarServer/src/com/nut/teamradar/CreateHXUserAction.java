package com.nut.teamradar;

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nut.teamradar.base.Constant;
import com.nut.teamradar.base.JsonMessage;
import com.nut.teamradar.model.Result;
import com.nut.teamradar.model.User;
import com.nut.teamradar.util.AppUtils;
import com.nut.teamradar.util.Constants;
import com.nut.teamradar.util.DBConnection;
import com.nut.teamradar.util.EasemobIMUsers;
import com.opensymphony.xwork2.ActionSupport;

public class CreateHXUserAction extends ActionSupport implements ServletRequestAware,  
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
	    	userInfo = new UserInformation();
	    	userInfo.put(Constant.SESSION_COUNTER,0);
	    	SessionManager.getInstance().putUser(SessionId, userInfo);
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
	    	msg.setMessage("reg");
	    	msg.setObject(result);
	    	try{
	            ObjectNode datanode = JsonNodeFactory.instance.objectNode();
	            datanode.put("username",username);
	            datanode.put("password", password);
	            ObjectNode createNewIMUserSingleNode = EasemobIMUsers.createNewIMUserSingle(datanode);
	            if (null != createNewIMUserSingleNode) {
	            	result.setData("success");
	            	msg.setObject(result);
	            	System.out.println("注册IM用户[单个]: " + createNewIMUserSingleNode.toString());
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
	
	    private String username;

	    public String getUsername() {
	        return username;
	    }

	    public void setUsername(String username) {
	        this.username = username;
	    }

	    private String password;

	    public String getPassword() {
	        return password;
	    }

	    public void setPassword(String password) {
	        this.password = password;
	    }
}