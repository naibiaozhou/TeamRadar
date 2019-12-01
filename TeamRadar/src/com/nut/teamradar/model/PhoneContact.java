package com.nut.teamradar.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PhoneContact implements Parcelable{
	public String Name;
	public String SubScription;
	public PhoneContact(String name,String sub)
	{
		Name = name;
		SubScription = sub;	
	}
	public PhoneContact()
	{
		Name = "unknown";
		SubScription = "18811100682";
	}
	
	public PhoneContact(Parcel src)
	{
		readFromParcel(src);
	}
	public static final Parcelable.Creator<PhoneContact> CREATOR = new Parcelable.Creator<PhoneContact>() {  
		  
        @Override  
        public PhoneContact createFromParcel(Parcel source) {  
            return new PhoneContact(source);  
        }  
  
        @Override  
        public PhoneContact[] newArray(int size) {  
         return new PhoneContact[size];  
        }  
	};
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeString(Name);
		dest.writeString(SubScription);
	}	
	
	public void readFromParcel(Parcel src)
	{
		Name = src.readString();
		SubScription = src.readString();
	}	
	
}
