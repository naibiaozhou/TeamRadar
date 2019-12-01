package com.nut.teamradar;

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.nut.teamradar.base.Constant;
import com.nut.teamradar.base.JsonMessage;
import com.nut.teamradar.model.MarkerInfo;
import com.nut.teamradar.util.AppUtils;
import com.nut.teamradar.util.DBConnection;
import com.opensymphony.xwork2.ActionSupport;

public class getRendezvousAction extends ActionSupport implements ServletRequestAware,  
	ServletResponseAware {  
	private String message;
	private String code;
	private MarkerInfo markerInfo;
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
		markerInfo = new MarkerInfo();
		msg.setCode("2");
		msg.setMessage("get_profile");
		msg.setObject(markerInfo);
	
		try{
		   ResultSet rs = null;
		   if(userInfo != null)
		   {
			   String sql = "select id,latitude,longitude,editable from activity where id ='" + String.valueOf(groupId) +"'";
			   rs =DBConnection.getInstance().doQuery(sql);
			   if(rs.next()){
				   markerInfo.setId(rs.getLong(MarkerInfo.COL_ID));
				   markerInfo.setLatitude(rs.getDouble(MarkerInfo.COL_LATITUDE));
				   markerInfo.setLongitude(rs.getDouble(MarkerInfo.COL_LONGITUDE));
				   markerInfo.setEditable(rs.getInt(MarkerInfo.COL_EDITABLE));
				   msg.setObject(markerInfo);
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
	
	private int groupId;
	
	public int getGroupId() {
	    return groupId;
	}
	
	public void setGroupId(int id) {
	    this.groupId = id;
	}
	
}
