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
import com.nut.teamradar.util.Encrypt;
import com.opensymphony.xwork2.ActionSupport;

public class loginAction extends ActionSupport implements ServletRequestAware,  
    ServletResponseAware {  
	private String message;
	private String code;
	private Result result;
	private boolean flag;
	private UserInformation userInfo=null;
	private String SessionId=null;
	//private String Lock = new String("");
  
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
        	System.out.println(SessionId+":"+this.getClass().getName());
        }
    }  
	@Override
    public void setServletResponse(HttpServletResponse response) {  
        this.response = response;  
    } 
  
    @Override
    public String execute() throws Exception {
    	//synchronized (Lock)
    	{
	    	JsonMessage msg = new JsonMessage();
	    	result = new Result();
		    result.setData("failed");
	    	msg.setCode("1");
	    	msg.setMessage("login");
	    	msg.setObject(result);
	    	try{
	    	   ResultSet rs = null;
	    	   System.out.println("User "+Encrypt.Base64Decode(username)+"login with pass:"+ Encrypt.Base64Decode(password));
	    	   String sql = "select * from user where subscription ='" + Encrypt.Base64Decode(username) +
	    			   "' and password = '"+ Encrypt.Base64Decode(password)+"'";
	    	   rs =DBConnection.getInstance().doQuery(sql);
			   if(rs.next()){
				   UserInformation sessionUser = new UserInformation();
				   
				   sessionUser.put(Constant.SESSION_USER_ID, rs.getLong(User.COL_ID));
				   sessionUser.put(Constant.SESSION_USER_NAME, rs.getString(User.COL_NAME));
				   sessionUser.put(Constant.SESSION_USER_SUBSCRIPTION, rs.getString(User.COL_SUBSCRIPTION));
				   sessionUser.put(Constant.SESSION_COUNTER,0);
				   sessionUser.put(Constant.SESSION_LATESTUPDATE, 0);
				   SessionManager.getInstance().putUser(SessionId, sessionUser);
				   result.setData("success#"+rs.getLong(User.COL_ID)+"#"+rs.getString(User.COL_NAME));
				   msg.setObject(result);
				   System.out.println("Login success!");
			   }
			   else
			   {
				   System.out.println("Login failed!");
			   }
			}catch(Exception e){
			   e.printStackTrace();
			}
	    	AppUtils.writeResponse(SessionId,this.getClass().getName(),msg, response);
    	};
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