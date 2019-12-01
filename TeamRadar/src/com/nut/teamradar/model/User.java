package com.nut.teamradar.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.nut.teamradar.base.BaseModel;


public class User extends BaseModel implements Parcelable {
	
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
	private int height;
	private int weight;
	private String gender;
	private String mail;
	private String password;
	private String profession;
	
	public User () {
		id = -1;
		name = "unknown";
		subscription = "unknown";
		birthday = "unknown";
		height = 60;
		weight = 160;
		gender = "unknown";
		mail = "unknown";
		password = "unknown";
		profession = "unknown";
	}
	public User (User user) {
		id=user.id;
		name=user.name;
		subscription=user.subscription;
		birthday=user.birthday;
		height=user.height;
		weight=user.weight;
		gender=user.gender;
		mail=user.mail;
		password=user.password;
		profession=user.profession;
	}
	
	
	public User(Parcel src)
	{
		readFromParcel(src);
	}
	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {  
		  
        @Override  
        public User createFromParcel(Parcel source) {  
            return new User(source);  
        }  
  
        @Override  
        public User[] newArray(int size) {  
         return new User[size];  
        }  
	};

	
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
	
	public int getHeight () {
		return this.height;
	}
	
	public void setHeight (int height) {
		this.height = height;
	}
	
	public int getWeight () {
		return this.weight;
	}
	
	public void setWeight (int weight) {
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
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeLong(id);
		dest.writeInt(height);
		dest.writeInt(weight);
		dest.writeString(name);
		dest.writeString(subscription);
		dest.writeString(birthday);
		dest.writeString(gender);
		dest.writeString(mail);
		dest.writeString(password);
		dest.writeString(profession);
	}	
	
	public void readFromParcel(Parcel src)
	{
		id = src.readLong();
		height = src.readInt();
		weight = src.readInt();
		name = src.readString();
		subscription = src.readString();
		birthday = src.readString();
		gender = src.readString();
		mail = src.readString();
		password = src.readString();
		profession = src.readString();
	}
}