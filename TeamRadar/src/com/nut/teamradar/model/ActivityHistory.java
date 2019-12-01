package com.nut.teamradar.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.nut.teamradar.base.BaseModel;

public class ActivityHistory extends BaseModel implements Parcelable{
	
	// model columns
	public final static String COL_ID = "id";
	public final static String COL_USERID = "userId";
	public final static String COL_ACTIVITYID = "activityId";
	public final static String COL_DATETIME= "datetime";
	public final static String COL_MARK = "mark";

	
	private long id;
	private long userId;
	private long activityId;
	private String datetime;
	private String mark;
	
	public ActivityHistory () {
		id=-1;
		userId=-1;
		activityId=-1;
		datetime="1970-01-01-01-01-01";
		mark="unknown";	
	}
	public ActivityHistory(Parcel src)
	{
		readFromParcel(src);
	}
	
	public static final Parcelable.Creator<ActivityHistory> CREATOR = new Parcelable.Creator<ActivityHistory>() {  
		  
        @Override  
        public ActivityHistory createFromParcel(Parcel source) {  
            return new ActivityHistory(source);  
        }  
  
        @Override  
        public ActivityHistory[] newArray(int size) {  
         return new ActivityHistory[size];  
        }  
	};

	public long getId () {
		return this.id;
	}
	
	public void setId (long id) {
		this.id = id;
	}

	public long getUserId () {
		return this.userId;
	}
	
	public void setUserId (long id) {
		this.userId = id;
	}
	
	public long getActivityId () {
		return this.activityId;
	}
	
	public void setActivityId (long id) {
		this.activityId = id;
	}
	
	public String getDatetime () {
		return this.datetime;
	}
	
	public void setDatetime (String t) {
		this.datetime = t;
	}
	
	public String getMark () {
		return this.mark;
	}
	
	public void setMark (String m) {
		this.mark = m;
	}
	@Override
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeLong(id);
		dest.writeLong(userId);
		dest.writeLong(activityId);
		dest.writeString(datetime);
		dest.writeString(mark);
	}	
	
	public void readFromParcel(Parcel src)
	{
		id = src.readLong();
		userId = src.readLong();
		activityId = src.readLong();
		datetime = src.readString();
		mark = src.readString();
	}
	@Override
	public int describeContents() {
		return 0;
	}
}