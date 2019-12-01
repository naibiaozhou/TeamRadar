package com.nut.teamradar;

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.nut.teamradar.base.Constant;
import com.nut.teamradar.base.JsonMessage;
import com.nut.teamradar.model.Group;
import com.nut.teamradar.util.AppUtils;
import com.nut.teamradar.util.DBConnection;
import com.opensymphony.xwork2.ActionSupport;

public class getJoinedActivitiesAction extends ActionSupport implements ServletRequestAware,
	ServletResponseAware {
	private Group  activity;
	private HttpServletRequest request;  
	
	private HttpServletResponse response;
	private UserInformation userInfo = null;
	private String SessionId=null;
	
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
	    	//System.out.println(SessionId+":"+this.getClass().getName());
	    }
	}
	
	@Override
	public String execute() throws Exception {
		JsonMessage msg = new JsonMessage();
		msg.setCode("1");
		msg.setMessage("fetch");
		int rows=0;
		try{
		   ResultSet rs = null;
		   if(userInfo != null)
		   {
    		   String sql;
    		   if(flag != null && flag.equals("1"))
    		   {
    			   sql  = "select * from activity  where id in (select activityid from member where userid ='"+userid+"')";
    		   }
    		   else
    		   {
    			   sql  = "select * from activity where available=1 and id in (select activityid from member where available=1 and userid ='"+userid+"')";
    		   }
			   rs = DBConnection.getInstance().doQuery(sql);
			   while(rs.next())
			   {
				   activity = new Group();
				   activity.setId(rs.getLong(Group.COL_ID));
				   activity.setOwnerId(rs.getLong(Group.COL_OWNERID));
				   activity.setName(rs.getString(Group.COL_NAME));
				   activity.setSubscription(rs.getString(Group.COL_SUBSCRIPTION));
				   activity.setComment(rs.getString(Group.COL_COMMENT));
				   activity.setAvailable(rs.getInt(Group.COL_AVAILABLE));
				   msg.addObject(activity);
				   rows++;
			   }

		   }
		} catch (Exception e){
		   e.printStackTrace();
		}
	   if(rows == 0)
	   {
		   activity = new Group();
		   msg.addObject(activity);
	   }
		AppUtils.writeResponse(SessionId,this.getClass().getName(),msg, response);
	    return null; 
	}
	
	@SkipValidation
	public String form() throws Exception {
	    return INPUT;
	}
	
	private String userid = null;
	
	public String getUserid() {
	    return userid;
	}
	
	public void setUserid(String userid) {
	    this.userid = userid;
	}
	private String flag = null;
	public String getFlag() {
		return flag;
	}
	
	public void setFlag(String f) {
		this.flag = f;
	}
}
