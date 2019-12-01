package com.nut.teamradar;

import java.net.URLDecoder;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.nut.teamradar.base.Constant;
import com.nut.teamradar.base.JsonMessage;
import com.nut.teamradar.model.Result;
import com.nut.teamradar.model.ShortMessage;
import com.nut.teamradar.model.User;
import com.nut.teamradar.util.AppUtils;
import com.nut.teamradar.util.DBConnection;
import com.opensymphony.xwork2.ActionSupport;

public class pushMessageAction extends ActionSupport implements ServletRequestAware,  
	ServletResponseAware {
	private String message;
	private String code;
	private Result result;
	private ShortMessage shortMessage = null;
	
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
		result = new Result();
	    result.setData("failed");
		msg.setCode("1");
		msg.setMessage("login");
		msg.setObject(result);
		if(shortMessage == null)
		{
			return null;
		}
		try{
		   boolean status = false;
		   ResultSet rs = null;
		   String sql;
		   if(userInfo != null)
		   {
			   if(shortMessage.getToId() != -1)
			   {
				   sql= "select * from user where id ='"+String.valueOf(shortMessage.getToId())+"'";
			   }
			   else
			   {
				   sql= "select * from user where subscription ='"+shortMessage.getToSubscription()+"'";
			   }
			   rs = DBConnection.getInstance().doQuery(sql);
			   if(rs.next()){
				   shortMessage.setToId(rs.getLong(User.COL_ID));
				   shortMessage.setToName(rs.getString(User.COL_NAME));
				   shortMessage.setToSubscription(rs.getString(User.COL_SUBSCRIPTION));
			   }
			   if(shortMessage.getToId() != -1)
			   {
				   sql = "insert into messenger (fromid,fromname,fromsubscription,toid,toname,tosubscription,message) values (\'"+
						   shortMessage.getFromId()+"\',\'"+
						   shortMessage.getFromName()+"\',\'"+
						   shortMessage.getFromSubscription()+"\',\'"+
						   shortMessage.getToId()+"\',\'"+
						   shortMessage.getToName()+"\',\'"+
						   shortMessage.getToSubscription()+"\',\'"+
						   shortMessage.getMessage()+"\')";
				   status =DBConnection.getInstance().doInsert(sql);
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
	
	public void setShortMessage(ShortMessage msg)
	{
		 // avoid use create activity for someone else
		//if(msg.getFromId().equals(userInfo.get(Constant.SESSION_USER_ID).toString()))
		{
			shortMessage = msg;
		}
	}
	public ShortMessage getShortMessage()
	{
		return shortMessage;
	}
}