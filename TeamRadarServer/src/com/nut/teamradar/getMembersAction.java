package com.nut.teamradar;

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.nut.teamradar.base.JsonMessage;
import com.nut.teamradar.model.Member;
import com.nut.teamradar.util.AppUtils;
import com.nut.teamradar.util.DBConnection;
import com.opensymphony.xwork2.ActionSupport;

public class getMembersAction extends ActionSupport implements ServletRequestAware,
	ServletResponseAware {
	private Member  member;
	private UserInformation userInfo = null;
	private String SessionId=null;
	private HttpServletRequest request;  
	
	private HttpServletResponse response;
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;  
		SessionId = this.request.getHeader("TeamRadarSessionId");
        if(SessionId != null)
        {
        	
        	userInfo = SessionManager.getInstance().getUser(SessionId);
        	//System.out.println(SessionId+":"+this.getClass().getName()+" : Session OK!");
        }
	}
	
	@Override
	public String execute() throws Exception {
		int row =0;
		JsonMessage msg = new JsonMessage();
		msg.setCode("1");
		msg.setMessage("fetch");
		try{
		   ResultSet rs = null;
		   if(userInfo != null)
		   {
			   String sql;
			   if(flag != null && flag.equals('1'))
			   {
				   sql = "select * from member where activityid ='" + 
						   activityid + "'" ;				   
			   }
			   else
			   {
				   sql = "select * from member where activityid ='" + 
						   activityid + "' and available=1" ;
			   }

			   rs = DBConnection.getInstance().doQuery(sql);
			   while(rs.next())
			   {
				   member = new Member();
				   member.setId(rs.getLong(Member.COL_ID));
				   member.setActivityId(rs.getLong(Member.COL_ACTIVITYID));
				   member.setUserId(rs.getLong(Member.COL_USERID));
				   member.setUsername(rs.getString(Member.COL_USERNAME));
				   member.setSubscription(rs.getString(Member.COL_SUBSCRIPTION));
				   member.setComment(rs.getString(Member.COL_COMMENT));
				   member.setAvailable(rs.getInt(Member.COL_AVAILABLE));
				   msg.addObject(member);
				   row++;
			   }
		   }
		} catch (Exception e){
		   e.printStackTrace();
		}
	   if(row==0)
	   {
		   member = new Member();
		   msg.addObject(member);
	   }
		AppUtils.writeResponse(SessionId,this.getClass().getName(),msg, response);
		return null; 
	}
	
	@SkipValidation
	public String form() throws Exception {
		return INPUT;
	}
	
	private String activityid = null;
	
	public String getActivityid() {
		return activityid;
	}
	
	public void setActivityid(String activityid) {
		this.activityid = activityid;
	}
	private String flag = null;
	public String getFlag() {
		return flag;
	}
	
	public void setFlag(String f) {
		this.flag = f;
	}
}