package com.nut.teamradar.model;

import com.nut.teamradar.base.BaseModel;

public class Contact extends BaseModel {
	// model columns
	public final static String COL_ID = "id";
	public final static String COL_OWNER_SUBSCRIPTION = "ownerSubscription";
	public final static String COL_OWNER_NAME = "ownerName";
	public final static String COL_CONTACT_SUBSCRIPTION = "contactSubscription";
	public final static String COL_CONTACT_NAME = "contactName";
	
	private long id;
	private String ownerSubscription = null;
	private String ownerName = null;
	private String contactSubscription = null;
	private String contactName = null;

	public Contact() {
		id=-1;
		ownerSubscription = "13800000000";
		ownerName = "Test";
		contactSubscription =  "13800000000";
		contactName = "Test";
	}

	public Contact(String ownerName, String ownerSubscription) {
		this.ownerName = ownerName;
		this.ownerSubscription = ownerSubscription;
	}

	public Contact(long id,String ownerSubscription, String ownerName,
			String contactSubscription, String contactName) {
		this.id = id;
		this.ownerName = ownerName;
		this.ownerSubscription = ownerSubscription;
		this.contactName = contactName;
		this.contactSubscription = contactSubscription;
	}
	
	public long getId () {
		return this.id;
	}
	public void setId (long id) {
		this.id = id;
	}
	
	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String name) {
		this.ownerName = name;
	}

	public String getOwnerSubscription() {
		return ownerSubscription;
	}

	public void setOwnerSubscription(String subscription) {
		this.ownerSubscription = subscription;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String name) {
		this.contactName = name;
	}

	public String getContactSubscription() {
		return contactSubscription;
	}

	public void setContactSubscription(String subscription) {
		this.contactSubscription = subscription;
	}

	@Override
	public String toString() {
		return "Person [owner_subscription=" + ownerSubscription
				+ ", owner_name=" + ownerName + ", contact_subscription="
						+ contactSubscription + ", contact_name=" + contactName + "]";
	}
}
