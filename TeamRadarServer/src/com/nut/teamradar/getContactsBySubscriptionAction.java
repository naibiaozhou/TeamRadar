package com.nut.teamradar;

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.nut.teamradar.base.JsonMessage;
import com.nut.teamradar.model.Contact;
import com.nut.teamradar.util.AppUtils;
import com.nut.teamradar.util.DBConnection;
import com.opensymphony.xwork2.ActionSupport;

public class getContactsBySubscriptionAction extends ActionSupport implements
		ServletRequestAware, ServletResponseAware {

	private HttpServletResponse mResponse;
	private String SessionId;
	private UserInformation userInfo;
	private HttpServletRequest mRequest;

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		mResponse = arg0;
	}

	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		mRequest = arg0;
		SessionId = arg0.getHeader("TeamRadarSessionId");
		if (SessionId != null) {
			userInfo = SessionManager.getInstance().getUser(SessionId);
		}
	}

	public String execute() throws Exception {
		JsonMessage msg = new JsonMessage();
    	msg.setCode("1");
    	msg.setMessage("fetch");
		try {
			int rows = 0;
			ResultSet rs = null;
			String sql = "select * from contacts where ownerSubscription = '"
					+ subscription + "'";
			rs = DBConnection.getInstance().doQuery(sql);
			while (rs.next()) {
				Contact c = new Contact(
						rs.getLong(Contact.COL_ID),
						rs.getString(Contact.COL_OWNER_NAME), 
						rs.getString(Contact.COL_OWNER_SUBSCRIPTION), 
						rs.getString(Contact.COL_CONTACT_SUBSCRIPTION),
						rs.getString(Contact.COL_CONTACT_NAME));
				msg.addObject(c);
				++rows;
			}
			if (rows == 0) {
				msg.addObject(new Contact());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		AppUtils.writeResponse(SessionId,this.getClass().getName(),msg, mResponse);
		return null;
	}
	private String subscription = null;

	public String getSubscription() {
		return subscription;
	}

	public void setSubscription(String sub) {
		this.subscription = sub;
	}

	@SkipValidation
	public String form() throws Exception {
		return INPUT;
	}
}
