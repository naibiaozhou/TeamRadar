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

public class updateProfileAction extends ActionSupport implements ServletRequestAware,  
	ServletResponseAware {
	private String message;
	private String code;
	private Result result;
	private User user;
	private boolean flag =false;
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
	    	   if(userInfo != null)
	    	   {
				   sql = "update user set "+ 
		    	        "name="+"\'"+ user.getName()+"\',"+
				   		"birthday="+"\'"+ user.getBirthday()+"\',"+
				   		"height="+"\'"+ user.getHeight()+"\',"+
				   		"weight="+"\'"+ user.getWeight()+"\',"+
				   		"gender="+"\'"+ user.getGender()+"\',"+
				   		"mail="+"\'"+ user.getMail()+"\',"+
				   		"password="+"\'"+ user.getPassword()+"\',"+
				   		"profesion="+"\'"+ user.getProfession()+"\'"+
				   		" where id='" + userInfo.get(Constant.SESSION_USER_ID).toString() +"'";
				   
				   status =DBConnection.getInstance().doUpdate(sql);
				   if(status)
				   {
					   sql = "update member set "+
							   "username="+"\'"+ user.getName()+"\'"+
							   " where userid='" + userInfo.get(Constant.SESSION_USER_ID).toString() +"'";
					   status =DBConnection.getInstance().doUpdate(sql);
					   
					   if(status)
					   {
						   result.setData("success");
						   msg.setObject(result);	
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
	
	public void setUser(User usr)
	{
		user = usr;
	}
	public User getUser()
	{
		return user;
	}
}