package com.nut.teamradar;

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.nut.teamradar.base.Constant;
import com.nut.teamradar.base.JsonMessage;
import com.nut.teamradar.model.ShortMessage;
import com.nut.teamradar.util.AppUtils;
import com.nut.teamradar.util.DBConnection;
import com.opensymphony.xwork2.ActionSupport;

public class pullMessageAction extends ActionSupport implements ServletRequestAware,
	ServletResponseAware {
	private ShortMessage  shortMessage;
	
	private UserInformation userInfo = null;
	private String SessionId=null;
	
	private HttpServletRequest request;  
	
	private HttpServletResponse response;
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
	// TODO Auto-generated method stub
		this.response = response;
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
	// TODO Auto-generated method stub
		this.request = request;  
		SessionId = this.request.getHeader("TeamRadarSessionId");
        if(SessionId != null)
        {
        	
        	userInfo = SessionManager.getInstance().getUser(SessionId);
        	userInfo.put(Constant.SESSION_COUNTER, 0);
        	if(userInfo != null)
        	{
        		//System.out.println(SessionId+":"+this.getClass().getName()+" : Session OK!");
        	}
        }
	}
	
	@Override
	public String execute() throws Exception {
		JsonMessage msg = new JsonMessage();
		msg.setCode("1");
		msg.setMessage("fetch");
		int row = 0;
		try{
		   ResultSet rs = null;
		   String idlist="";
		   String sql;
		   if(userInfo != null)
		   {
			   sql = "select * from messenger where toid ='" + 
					   userInfo.get(Constant.SESSION_USER_ID).toString() + "'";
			   rs = DBConnection.getInstance().doQuery(sql);
			   while(rs.next())
			   {
				   long id = rs.getLong(ShortMessage.COL_ID);
				   shortMessage = new ShortMessage();
				   
				   shortMessage.setId(rs.getLong(ShortMessage.COL_ID));
				   shortMessage.setFromId(rs.getLong(ShortMessage.COL_FROM));
				   shortMessage.setFromName(rs.getString(ShortMessage.COL_FROMNAME));
				   shortMessage.setFromSubscription(rs.getString(ShortMessage.COL_FROMSUBSCRIPTION));
				   shortMessage.setToId(rs.getLong(ShortMessage.COL_TO));
				   shortMessage.setToName(rs.getString(ShortMessage.COL_TONAME));
				   shortMessage.setToSubscription(rs.getString(ShortMessage.COL_TOSUBSCRIPTION));
				   shortMessage.setMessage(rs.getString(ShortMessage.COL_MESSAGE));
				   if(rs.isLast())
				   {
					   idlist += "\'"+String.valueOf(id)+"\'";
				   }
				   else
				   {
					   idlist += "\'"+String.valueOf(id)+"\',";
				   }
				   msg.addObject(shortMessage);
				   row++;
			   }
			   if(row == 0)
			   {
				   shortMessage = new ShortMessage();
				   msg.addObject(shortMessage);
			   }
			   else
			   {
				   sql =  "delete from messenger where id in ("+ idlist +")";
				   DBConnection.getInstance().doDelete(sql);
			   }
		   }
		} catch (Exception e){
		   shortMessage = new ShortMessage();
		   msg.addObject(shortMessage);
		   e.printStackTrace();
		}
		AppUtils.writeResponse(SessionId,this.getClass().getName(),msg, response);
		return null; 
	}
	
	@SkipValidation
	public String form() throws Exception {
		return INPUT;
	}
	
	private String toid = null;
	
	public String getToid() {
		
		return toid;
	}
	
	public void setToid(String id) {
		if(userInfo.get(Constant.SESSION_USER_ID).toString().equals(toid))
		{
			this.toid = id;
		}
	}
}