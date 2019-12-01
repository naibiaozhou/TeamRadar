package com.nut.teamradar;

import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.nut.teamradar.base.Constant;
import com.nut.teamradar.base.JsonMessage;
import com.nut.teamradar.model.Result;
import com.nut.teamradar.model.User;
import com.nut.teamradar.util.AppUtils;
import com.nut.teamradar.util.DBConnection;
import com.opensymphony.xwork2.ActionSupport;

public class getProfileAction extends ActionSupport implements ServletRequestAware,  
	ServletResponseAware {  
	private String message;
	private String code;
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
		JsonMessage msg = new JsonMessage();
		user = new User();
		msg.setCode("2");
		msg.setMessage("get_profile");
		msg.setObject(user);

		try{
		   ResultSet rs = null;
		   if(userInfo != null)
		   {
			   String sql = "select * from user where id ='" + userInfo.get(Constant.SESSION_USER_ID).toString() +"'";
			   rs =DBConnection.getInstance().doQuery(sql);
			   if(rs.next()){
				   user.setId(rs.getLong(User.COL_ID));
				   user.setName(rs.getString(User.COL_NAME));
				   user.setSubscription(rs.getString(User.COL_SUBSCRIPTION));
				   user.setBirthday(rs.getDate(User.COL_BIRTHDAY).toString());
				   user.setGender(rs.getString(User.COL_GENDER));
				   user.setHeight(String.valueOf(rs.getInt(User.COL_HEIGHT)));
				   user.setWeight(String.valueOf(rs.getInt(User.COL_WEIGHT)));
				   user.setMail(rs.getString(User.COL_MAIL));
				   user.setPassword(rs.getString(User.COL_PASSWORD));
				   user.setSubscription(rs.getString(User.COL_SUBSCRIPTION));
				   msg.setObject(user);
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
