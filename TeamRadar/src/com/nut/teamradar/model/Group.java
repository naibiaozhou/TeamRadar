package com.nut.teamradar.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.nut.teamradar.base.BaseModel;

public class Group extends BaseModel implements Parcelable {
	public final static String COL_ID = "id";
	public final static String COL_NAME = "name";
	public final static String COL_OWNERID = "ownerId";
	public final static String COL_SUBSCRIPTION = "subscription";
	public final static String COL_COMMENT = "comment";
	private long id;
	private long ownerId;
	private String name;
	private String subscription;
	private String comment;
	private int available;
	public Group()
	{
		id = -1;
		ownerId = -1;
		name="dumy";
		subscription = "dumy";
		comment="dumy";	
		available = 1;
	}
	
	public Group(Parcel src)
	{
		readFromParcel(src);
	}
	public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {  
		  
        @Override  
        public Group createFromParcel(Parcel source) {  
            return new Group(source);  
        }  
  
        @Override  
        public Group[] newArray(int size) {  
         return new Group[size];  
        }  
	};

	
	public long getId () {
		return this.id;
	}
	
	public void setId (long id) {
		this.id = id;
	}
	
	public long getOwnerId () {
		return this.ownerId;
	}
	
	public void setOwnerId (long ownerid) {
		this.ownerId = ownerid;
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
	
	public void setSubscription (String sub) {
		this.subscription = sub;
	}
		
	public String getComment () {
		return this.comment;
	}
	
	public void setComment (String comment) {
		this.comment = comment;
	}
	public int getAvailable () {
		return this.available;
	}
	public void setAvailable (int valid) {
		this.available = valid;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeLong(id);
		dest.writeLong(ownerId);
		dest.writeString(name);
		dest.writeString(subscription);
		dest.writeString(comment);
		dest.writeInt(available);
	}	
	
	public void readFromParcel(Parcel src)
	{
		id = src.readLong();
		ownerId = src.readLong();
		name = src.readString();
		subscription = src.readString();
		comment = src.readString();
		available = src.readInt();
	}
}
