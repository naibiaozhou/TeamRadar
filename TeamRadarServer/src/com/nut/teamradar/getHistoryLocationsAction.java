package com.nut.teamradar;

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.nut.teamradar.base.Constant;
import com.nut.teamradar.base.JsonMessage;
import com.nut.teamradar.model.Location;
import com.nut.teamradar.util.AppUtils;
import com.nut.teamradar.util.DBConnection;
import com.opensymphony.xwork2.ActionSupport;

public class getHistoryLocationsAction extends ActionSupport implements ServletRequestAware,
	ServletResponseAware {
	private Location  location;
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
			userInfo.put(Constant.SESSION_COUNTER, 0);
			System.out.println(SessionId+":"+this.getClass().getName());
		}
	}
	
	@Override
	public String execute() throws Exception {
	
		int rows=0;
		long lasttime = Long.valueOf(userInfo.get(Constant.SESSION_LATESTUPDATE).toString());
		long timeThreashold = lasttime -30000;
		JsonMessage msg = new JsonMessage();
		msg.setCode("1");
		msg.setMessage("fetch");
		
		if(timeThreashold < 0)
			timeThreashold = 0;
		System.out.println(String.format("timeThreashold is %d", timeThreashold));
		try{
			String provider;
		   ResultSet rs = null;
		   if(userInfo != null)
		   {
			   String sql = "select * from location where groupid = '"+ activityId +"' and userid = '"+ userId +"' and mark = '"+ mark +"'";
			   rs = DBConnection.getInstance().doQuery(sql);
			   while(rs.next())
			   {
				   long time;
				   location = new Location();
				   location.setUserid(rs.getLong(Location.COL_USERID));
				   location.setId(rs.getLong(Location.COL_ID));
				   time = rs.getLong(Location.COL_TIME);
				   provider = rs.getString(Location.COL_PROVIDER);
				   if(time > lasttime && provider.equals("gps"))
				   {
					   lasttime = time;
				   }
				   location.setTime(time);
				   location.setProvider(provider);
				   location.setGroupid(rs.getLong(Location.COL_ACTIVITYID));
				   location.setLatitude(rs.getDouble(Location.COL_LATITUDE));
				   location.setLongitude(rs.getDouble(Location.COL_LONGITUDE));
				   location.setAccuracy(rs.getFloat(Location.COL_ACCURACY));
				   location.setAltitude(rs.getInt(Location.COL_ALTITUDE));
				   location.setSpeed(rs.getFloat(Location.COL_SPEED));
				   location.setHeading(rs.getFloat(Location.COL_HEADING));
				   location.setAveragecn0(rs.getInt(Location.COL_AVGCN0));
				   location.setMark(rs.getString(Location.COL_MARK));
				   location.setFlag(rs.getInt(Location.COL_FLAG));
				   msg.addObject(location);
				   rows++;
				   userInfo.put(Constant.SESSION_LATESTUPDATE, lasttime);
			   }
			   if(rows == 0)
			   {
				   location = new Location();
				   msg.addObject(location);
			   }
		   }
		} catch (Exception e){
		   e.printStackTrace();
		}
		AppUtils.writeResponse(SessionId,this.getClass().getName(),msg, response);
		return null; 
	}
	
	@SkipValidation
	public String form() throws Exception {
		return INPUT;
	}
	
	private String activityId = null;
	private String userId = null;
	private String mark = null;
	
	public String getActivityId() {
		return activityId;
	}
	
	public void setActivityId(String actid) {
		this.activityId = actid;
	}
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String id) {
		this.userId = id;
	}
	public String getMark() {
		return mark;
	}
	
	public void setMark(String m) {
		this.mark = m;
	}
}