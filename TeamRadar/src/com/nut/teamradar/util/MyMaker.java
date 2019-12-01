package com.nut.teamradar.util;

import com.nut.teamradar.R;
import com.nut.teamradar.TRServiceConnection;
import com.nut.teamradar.TeamRadarApplication;
import com.nut.teamradar.model.ScreenInfo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class MyMaker{ 
	private static String TAG="MyMaker";
	private String Name;
	private Context mCtx;
	private float speed;
	private double distance;
	private double timeelapse;
	private int    emergency;
	private Resources res;
	private Bitmap CoolBmp=null;
	private Bitmap CoolEmBmp=null;
	private Bitmap Bmp=null;
	private Bitmap CoolscaledBmp=null;
	
	private Bitmap scaledBmp=null;
	
	public MyMaker(Context ctx)
	{
		mCtx = ctx;
		speed = 0;
		distance = 0;
		emergency =0;
		Name = "";
		Resources res = mCtx.getResources();
		CoolBmp=BitmapFactory.decodeResource(res, R.drawable.cool_pin_big).copy(Bitmap.Config.ARGB_8888, true);
		CoolEmBmp =BitmapFactory.decodeResource(res, R.drawable.cool_pin_em_big).copy(Bitmap.Config.ARGB_8888, true);
		Bmp=BitmapFactory.decodeResource(res, R.drawable.pin_128_big).copy(Bitmap.Config.ARGB_8888, true);
		
	}
    public void setName(String Name)
    {
    	if(Name != null)
    	{
    		this.Name = Name;
    	}
    	else
    	{
    		this.Name = "";
    	}
    	
    }
    public void setSpeed(float sp)
    {
    	this.speed = sp;
    }
    public void setDistance(double dis)
    {
    	this.distance = dis;
    }
    public void setTimeelapse(double elapse)
    {
    	this.timeelapse = elapse;
    }
    public void setEmergency(int em)
    {
    	emergency = em;
    }
    public Bitmap GetMarker(long time)
    {
    	
    	float xscale=1.0f ,yscale=1.0f;
    	float fontsize = 40, starty=40;
    	float timex=110,timey=160;
    	float infofontsize = 30;

    	ScreenInfo info = TRServiceConnection.getInstance().GetScreenInfo();
    	float scale = Float.valueOf(info.width)/1080.0f;
		xscale = 1f;
		yscale = 1f;
		infofontsize = 30*scale;
		fontsize = 40*scale;
		starty = 40*scale;
    	timex=110*scale;
    	timey=160*scale;
    	Log.e(TAG,String.format("GetMarker ~~~~~~scale %f %f %d \r\n", xscale,yscale,info.width));
		Paint paint = new Paint(); 
		paint.setAlpha(100);
		paint.setColor(Color.BLACK);
				
		Matrix matrix = new Matrix(); 
		matrix.postScale(0.5f*xscale,0.5f*yscale); 
		if(scaledBmp != null)
		{
			scaledBmp = null;
		}
		Bitmap scaledBmp = Bitmap.createBitmap(Bmp,0,0,Bmp.getWidth(),Bmp.getHeight(),matrix,true);
		
		Canvas canvas = new Canvas(scaledBmp);
		paint.setTextSize(fontsize);
		paint.setFakeBoldText(true);
		canvas.drawText(Name, 0, starty, paint);
		paint.setTextSize(infofontsize);
		canvas.drawText(AMapUtil.convertToTime(time), timex, timey, paint);
		return scaledBmp;
    }
    public Bitmap GetCoolMarker(int Colour,long time)
    {
    	float xscale=1.0f ,yscale=1.0f;
    	float fontsize = 40, starty=40;
    	float infofontsize = 30;
    	float speedx=110,speedy=84;
    	float distx=110,disty=120;
    	float elapsex=110,elapsey=160;
    	float timex=110,timey=192;
    	ScreenInfo info = TRServiceConnection.getInstance().GetScreenInfo();
    	float scale = Float.valueOf(info.width)/1080.0f;
		xscale = 1f;
		yscale = 1f;
    	fontsize = 40*scale;
    	starty=40*scale;
    	infofontsize = 30*scale;
    	speedx=110*scale;
    	speedy=84*scale;
    	distx=110*scale;
    	disty=120*scale;
    	elapsex=110*scale;
    	elapsey=156*scale;
    	timex=110*scale;
    	timey=192*scale;
    	Log.e(TAG,String.format("GetCoolMarker ~~~~~~scale %f %f %d \r\n", xscale,yscale,info.width));
    	Paint paint = new Paint(); 
		paint.setAlpha(100);
		if(emergency == 1)
		{
			paint.setColor(Color.RED);
		}
		else
		{
			paint.setColor(Color.BLACK);
		}
		
			
		Matrix matrix = new Matrix(); 
		matrix.postScale(0.5f*xscale,0.5f*xscale); 
		if(CoolscaledBmp != null)
		{
			CoolscaledBmp = null;
		}
		if(emergency == 0)
			CoolscaledBmp = Bitmap.createBitmap(CoolBmp,0,0,CoolBmp.getWidth(),CoolBmp.getHeight(),matrix,true);
		else	
			CoolscaledBmp = Bitmap.createBitmap(CoolEmBmp,0,0,CoolBmp.getWidth(),CoolBmp.getHeight(),matrix,true);
		
		Canvas canvas = new Canvas(CoolscaledBmp);
		paint.setTextSize(fontsize);
		paint.setFakeBoldText(true);
		canvas.drawText(Name, speedx, starty, paint);
		paint.setColor(Colour);
		
		paint.setFakeBoldText(true);
		paint.setTextSize(infofontsize);
		canvas.drawText(String.format("速        度:%.2f km/h", this.speed*3.6), speedx, speedy, paint);
		canvas.drawText(String.format("有效距离:%.3f km", this.distance), distx, disty, paint);
		canvas.drawText(String.format("有效时间:%d:%02d:%.2f s",(int)this.timeelapse/3600,(int)(this.timeelapse%3600)/60,this.timeelapse%60.0), elapsex, elapsey, paint);
		canvas.drawText(AMapUtil.convertToTime(time), timex, timey, paint);
		return CoolscaledBmp;
    }
}
