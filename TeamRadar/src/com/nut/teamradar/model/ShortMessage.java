package com.nut.teamradar.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.nut.teamradar.base.BaseModel;

public class ShortMessage extends BaseModel implements Parcelable {
	
	public final static String COL_ID = "id";
	public final static String COL_FROM = "fromId";
	public final static String COL_TO = "toId";
	public final static String COL_TOSUBSCRIPTION = "toSubscription";
	public final static String COL_TONAME = "toName";
	public final static String COL_FROMSUBSCRIPTION = "fromSubscription";
	public final static String COL_FROMNAME= "fromName";
	public final static String COL_MESSAGE = "message";	
	
	private long id;
	private long fromId;
	private long toId;
	private String fromSubscription;
	private String fromName;
	private String toSubscription;
	private String toName;
	private String message;

	public ShortMessage()
	{
		id = -1;
		fromId = -1;
		toId = -1;
		fromName = "dumy";
		fromSubscription = "unknown";
		toName = "dumy";
		toSubscription = "unknown";
		message = "dumy";
	}
	
	public ShortMessage(Parcel src)
	{
		readFromParcel(src);
	}
	public static final Parcelable.Creator<ShortMessage> CREATOR = new Parcelable.Creator<ShortMessage>() {  
		  
        @Override  
        public ShortMessage createFromParcel(Parcel source) {  
            return new ShortMessage(source);  
        }  
  
        @Override  
        public ShortMessage[] newArray(int size) {  
         return new ShortMessage[size];  
        }  
	};
	
	public long getId () {
		return this.id;
	}
	public void setId (long id) {
		this.id = id;
	}
	
	public long getFromId () {
		return this.fromId;
	}
	public void setFromId (long from) {
		this.fromId = from;
	}

	public long getToId () {
		return this.toId;
	}
	public void setToId (long to) {
		this.toId = to;
	}

	public String getToSubscription () {
		return this.toSubscription;
	}
	public void setToSubscription (String tosub) {
		this.toSubscription = tosub;
	}

	public String getToName () {
		return this.toName;
	}
	public void setToName (String toname) {
		this.toName = toname;
	}
	
	public String getFromSubscription () {
		return this.fromSubscription;
	}
	public void setFromSubscription (String fromsub) {
		this.fromSubscription = fromsub;
	}
	public String getFromName () {
		return this.fromName;
	}
	public void setFromName (String fromname) {
		this.fromName = fromname;
	}
	public String getMessage () {
		return this.message;
	}
	
	public void setMessage  (String message) {
		this.message = message;
	}
	
	public ShortMessage getAckMessage()
	{
		ShortMessage newMsg = new ShortMessage();
		newMsg.fromId = toId;
		newMsg.fromName = toName;
		newMsg.fromSubscription = toSubscription;
		newMsg.toId = fromId;
		newMsg.toName = fromName;
		newMsg.toSubscription = fromSubscription;
		newMsg.message = message;
		return newMsg;
		
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeLong(id);
		dest.writeLong(fromId);
		dest.writeLong(toId);
		dest.writeString(fromSubscription);
		dest.writeString(fromName);
		dest.writeString(toSubscription);
		dest.writeString(toName);
		dest.writeString(message);
	}	
	
	public void readFromParcel(Parcel src)
	{
		id = src.readLong();
		fromId = src.readLong();
		toId = src.readLong();
		fromSubscription = src.readString();
		fromName = src.readString();
		toSubscription = src.readString();
		toName = src.readString();
		message = src.readString();
	}
}
