package com.MASTAdView.ormma;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.MASTAdView.MASTAdViewCore;
import com.MASTAdView.ormma.listeners.AccelListener;

public class OrmmaSensorController extends OrmmaController{
	final int INTERVAL = 1000;
	private AccelListener mAccel;
	private float mLastX = 0;
	private float mLastY = 0;
	private float mLastZ = 0;

	public OrmmaSensorController(MASTAdViewCore adView, Context context) {
		super(adView, context);
		mAccel = new AccelListener(context, this);
	}

	public void startTiltListener(){
		mAccel.startTrackingTilt();
	}

	public void startShakeListener(){
		mAccel.startTrackingShake();
	}

	public void stopTiltListener(){
		mAccel.stopTrackingTilt();
	}

	public void stopShakeListener(){
		mAccel.stopTrackingShake();
	}

	public void startHeadingListener(){
		mAccel.startTrackingHeading();
	}

	public void stopHeadingListener(){
		mAccel.stopTrackingHeading();
	}

	void stop() {
	}

	public void onShake(){
		mOrmmaView.injectJavaScript("Ormma.gotShake()");		
	}
	
	public void onTilt(float x, float y, float z){
		mLastX = x;
		mLastY = y;
		mLastZ = z;	

		mOrmmaView.injectJavaScript("Ormma.gotTiltChange({ x : \"" + mLastX + "\", y : \"" + mLastY + "\", z : \"" + mLastZ + "\"})");
	}
	
	public String getTilt(){
		return ("{ x : \"" + mLastX + "\", y : \"" + mLastY + "\", z : \"" + mLastZ + "\"}");
	}

	public void onHeadingChange(int angle) {
		mOrmmaView.injectJavaScript("Ormma.gotHeadingChange("+ angle +")");
	}

	public void injectJavaScript(String js) {
		mOrmmaView.injectJavaScript(js);
	}
	
	public float getHeading(){
		return mAccel.getHeading();
	}

	public static boolean hasAccelerometer(Context context) {
		SensorManager sm = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);

		if(sm != null) {
			List<Sensor> list = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
			if(list.size() > 0) {
				return true;	
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean hasMagneticField(Context context) {
		SensorManager sm = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);

		if(sm != null) {
			List<Sensor> list = sm.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
			if(list.size() > 0) {
				return true;	
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
}