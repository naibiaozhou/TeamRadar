package com.nut.teamradar.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ScreenInfo implements Parcelable {
	public int width;
	public int height;
	public int statusBarHeight;
	public ScreenInfo()
	{
		width = 0;
		height = 0;
		statusBarHeight = 0;
	}
	public ScreenInfo(Parcel src)
	{
		readFromParcel(src);
	}
	public static final Parcelable.Creator<ScreenInfo> CREATOR = new Parcelable.Creator<ScreenInfo>() {  
		  
        @Override  
        public ScreenInfo createFromParcel(Parcel source) {  
            return new ScreenInfo(source);  
        }  
  
        @Override  
        public ScreenInfo[] newArray(int size) {  
         return new ScreenInfo[size];  
        }  
	};
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeInt(width);
		dest.writeInt(height);
		dest.writeInt(statusBarHeight);
	}	
	
	public void readFromParcel(Parcel src)
	{
		width = src.readInt();
		height = src.readInt();
		statusBarHeight = src.readInt();
	}
}