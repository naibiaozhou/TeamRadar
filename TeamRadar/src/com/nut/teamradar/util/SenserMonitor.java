package com.nut.teamradar.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

public class SenserMonitor implements SensorEventListener{
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private final int TIME_SENSOR = 100;
	
	private long lastTime = 0;
	private Context mContext;
	private List<SensorObserver> observers;
	public SenserMonitor(Context context)
	{
        //初始化传感器	
		mContext = context;
  		mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
  		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);	
  		observers = new ArrayList<SensorObserver>();
		mSensorManager.registerListener(this, mSensor,
				SensorManager.SENSOR_DELAY_NORMAL);	
	}
	public void Attatch(SensorObserver obs)
	{
		observers.add(obs);
	}
	public void Detatch(SensorObserver obs)
	{
		observers.remove(obs);
	}
	private int getScreenRotationOnPhone() {
		final Display display = ((WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		switch (display.getRotation()) {
		case Surface.ROTATION_0:
			return 0;

		case Surface.ROTATION_90:
			return 90;

		case Surface.ROTATION_180:
			return 180;

		case Surface.ROTATION_270:
			return -90;
		}
		return 0;
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		int index;
		float Angle = 0.0f;
		if (System.currentTimeMillis() - lastTime < TIME_SENSOR) {
			return;
		}
		switch (event.sensor.getType()) {
			case Sensor.TYPE_ORIENTATION: {
				float x = event.values[0];
				//System.out.println(x);
				x += getScreenRotationOnPhone();
				x %= 360.0F;
				if (x > 180.0F)
					x -= 360.0F;
				else if (x < -180.0F)
					x += 360.0F;
				if (Math.abs(Angle -90+ x) < 3.0f) {
					break;
				}
			    Angle = x;
				for(index = 0;index<observers.size();index++)
				{
					observers.get(index).directionChange(Angle);
				}

				lastTime = System.currentTimeMillis();
			}
		}
	}
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
	}
}
