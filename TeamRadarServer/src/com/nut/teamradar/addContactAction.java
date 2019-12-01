package com.nut.teamradar;

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.nut.teamradar.base.JsonMessage;
import com.nut.teamradar.model.Contact;
import com.nut.teamradar.model.Group;
import com.nut.teamradar.model.Result;
import com.nut.teamradar.model.User;
import com.nut.teamradar.util.AppUtils;
import com.nut.teamradar.util.DBConnection;
import com.opensymphony.xwork2.ActionSupport;

public class addContactAction extends ActionSupport implements
		ServletRequestAware, ServletResponseAware {
	private Contact contact = null;
	private String SessionId = null;
	private UserInformation userInfo = null;
	private HttpServletRequest mRequest;
	private HttpServletResponse mResponse;
	
	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		mRequest = arg0;
		SessionId = arg0.getHeader("TeamRadarSessionId");
		if (SessionId != null) {
			userInfo = SessionManager.getInstance().getUser(SessionId);
			//System.out.println(SessionId+":"+this.getClass().getName());
		}
	}
	
	@Override
	public String execute() throws Exception {
		JsonMessage msg = new JsonMessage();
		msg.setCode("1");
		msg.setMessage("addContact");
		Result result = new Result();
		if (isSelf()) {
			result.setData("cannot add self");
		}else if (!contactSubscriptionExisted()) {
			    result.setData("no such user");
		} else if (contactAlreadyAdded()) {
			result.setData("contact " + contact.getContactSubscription()
					+ " already added");
		} else {
			if (getContactName() && addContact()) {
				System.out.println(SessionId + "user added: "
						+ contact.getContactSubscription());
				result.setData("success");
			    } else {
				result.setData("failed to add user: "
						+ contact.getContactSubscription());
			}
		}
		msg.setObject(result);
		/*System.out.println(SessionId + ":" + this.getClass().getName() + " "
				+ result.getData());*/
		AppUtils.writeResponse(SessionId, this.getClass().getName(), msg,
				mResponse);
		return null;
	}
	private boolean isSelf() {
		return contact.getContactSubscription().equalsIgnoreCase(contact.getOwnerSubscription());
	}
	private boolean addContact() {
		try {
			String sql = "insert into contacts "
					+ "(ownerSubscription, ownerName, contactSubscription, contactName) "
					+ "values ('" + contact.getOwnerSubscription() + "','"
					+ contact.getOwnerName() + "', '"
					+ contact.getContactSubscription() + "', '"
					+ contact.getContactName() + "'),"
					+ "('" + contact.getContactSubscription() + "','"
					+ contact.getContactName() + "','"
					+ contact.getOwnerSubscription() + "','"
					+ contact.getOwnerName() + "')";
			return DBConnection.getInstance().doInsert(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean getContactName() {
		try {
			ResultSet rs = null;
			String sql = "select name from user where subscription = '"
					+ contact.getContactSubscription() + "'";
			rs = DBConnection.getInstance().doQuery(sql);
			if (rs.next()) {
				contact.setContactName(rs.getString(User.COL_NAME));
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private boolean contactAlreadyAdded() {
		try {
			ResultSet rs = null;
			String sql = "select count(*) from contacts where ownerSubscription = '"
					+ contact.getOwnerSubscription()
					+ "' and contactSubscription = '"
					+ contact.getContactSubscription() + "'";
			rs = DBConnection.getInstance().doQuery(sql);
			return rs.next() && rs.getLong(1) > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean contactSubscriptionExisted() throws Exception {
		try{
		   ResultSet rs = null;
			String sql = "select count(*) from user where subscription = '"
					+ contact.getContactSubscription() + "'";
		   rs = DBConnection.getInstance().doQuery(sql);
		   return rs.next() && rs.getLong(1) > 0;
		} catch (Exception e){
		   e.printStackTrace();
		}
		return false; 
		
	}

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		mResponse = arg0;
	}

	public Contact getContact() {
		return contact;
	}
			
	public void setContact(Contact c) {
		contact = c;
	}
	
	@SkipValidation
	public String form() throws Exception {
	return INPUT;
	}
}
