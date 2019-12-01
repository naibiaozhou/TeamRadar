package com.nut.teamradar.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.nut.teamradar.base.BaseModel;

public class MarkerInfo extends BaseModel implements Parcelable {
	public final static String COL_ID = "id";
	public final static String COL_LATITUDE = "latitude";
	public final static String COL_LONGITUDE = "longitude";
	public final static String COL_EDITABLE = "editable";
	private long id;
	private double latitude;
	private double longitude;
	private int editable;
	public MarkerInfo()
	{
		id = -1;
		latitude =-1;
		longitude=-1;
		editable=1;		
	}
	
	public MarkerInfo(Parcel src)
	{
		readFromParcel(src);
	}
	public static final Parcelable.Creator<MarkerInfo> CREATOR = new Parcelable.Creator<MarkerInfo>() {  
		  
        @Override  
        public MarkerInfo createFromParcel(Parcel source) {  
            return new MarkerInfo(source);  
        }  
  
        @Override  
        public MarkerInfo[] newArray(int size) {  
         return new MarkerInfo[size];  
        }  
	};
	
	public long getId () {
		return this.id;
	}
	public void setId (long id) {
		this.id = id;
	}
	public double getLatitude () {
		return this.latitude;
	}
	
	public void setLatitude (double Latitude) {
		this.latitude = Latitude;
	}
	
	public double getLongitude () {
		return this.longitude;
	}
	
	public void setLongitude (double longitude) {
		this.longitude = longitude;
	}	
	public int getEditable () {
		return this.editable;
	}
	public void setEditable  (int able) {
		this.editable = able;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeLong(id);
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeInt(editable);
	}	
	
	public void readFromParcel(Parcel src)
	{
		id = src.readLong();
		latitude = src.readDouble();
		longitude = src.readDouble();
		editable = src.readInt();
	}
}
