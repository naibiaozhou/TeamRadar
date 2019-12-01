package com.nut.teamradar.model;

import com.nut.teamradar.base.BaseModel;


public class User extends BaseModel {
	
	// model columns
	public final static String COL_ID = "id";
	public final static String COL_NAME = "name";
	public final static String COL_SUBSCRIPTION = "subscription";
	public final static String COL_BIRTHDAY = "birthday";
	public final static String COL_HEIGHT = "height";
	public final static String COL_WEIGHT = "weight";
	public final static String COL_GENDER = "gender";
	public final static String COL_MAIL = "mail";
	public final static String COL_PASSWORD = "password";
	public final static String COL_PROFESSION = "profession";
	
	private long id;
	private String name;
	private String subscription;
	private String birthday;
	private String height;
	private String weight;
	private String gender;
	private String mail;
	private String password;
	private String profession;
	
	public User () {
		id=-1;
		name="unknown";
		subscription="13888888888";
		birthday="1970-01-01";
		height="70";
		weight="20";
		gender="M";
		mail="123@321.com";
		password="unknown";
		profession="unknown";		
	}
	
	public long getId () {
		return this.id;
	}
	
	public void setId (long id) {
		this.id = id;
	}

	public String getName () {
		return this.name;
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public String getSubscription () {
		return this.subscription;
	}
	
	public void setSubscription (String subscription) {
		this.subscription = subscription;
	}
	
	public String getBirthday () {
		return this.birthday;
	}
	
	public void setBirthday (String birthday) {
		this.birthday = birthday;
	}
	
	public String getHeight () {
		return this.height;
	}
	
	public void setHeight (String height) {
		this.height = height;
	}
	
	public String getWeight () {
		return this.weight;
	}
	
	public void setWeight (String weight) {
		this.weight = weight;
	}
	
	public String getGender () {
		return this.gender;
	}
	
	public void setGender (String gender) {
		this.gender = gender;
	}
	
	public String getMail () {
		return this.mail;
	}
	
	public void setMail (String mail) {
		this.mail = mail;
	}
	
	public String getPassword () {
		return this.password;
	}
	
	public void setPassword (String password) {
		this.password = password;
	}
	public String getProfession () {
		return this.profession;
	}
	
	public void setProfession (String profession) {
		this.profession = profession;
	}	

}