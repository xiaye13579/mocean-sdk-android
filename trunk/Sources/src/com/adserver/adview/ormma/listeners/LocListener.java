
package com.adserver.adview.ormma.listeners;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Looper;

import com.adserver.adview.MASTAdLog;
import com.adserver.adview.ormma.OrmmaLocationController;

public class LocListener implements LocationListener {
	//OrmmaLocationController mOrmmaLocationController;
	private LocationManager mLocMan;
	private String mProvider;
	private long mInterval;
	private float mDistance;
	private Looper listenerLooper;
	MASTAdLog adLog;
	
	
	public void fail(String message)
	{
		// NA, override in derived class
	}
	
	public void success(Location location)
	{
		// NA, override in derived class
	}
	
	public LocListener(Context c, int interval, float distance, String provider, MASTAdLog logger)
	{
		//mOrmmaLocationController = ormmaLocationController;
		mLocMan = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
		mProvider = provider;
		mInterval = interval;
		mDistance = distance;
		listenerLooper = null;
		adLog = logger;
	}
	
	public LocListener(Context c, int interval, float distance, String provider, Looper looper, MASTAdLog logger)
	{
		//mOrmmaLocationController = ormmaLocationController;
		mLocMan = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
		mProvider = provider;
		mInterval = interval;
		mDistance = distance;
		listenerLooper = looper;
		adLog = logger;
	}
	
	public void onProviderDisabled(String provider) {
		//mOrmmaLocationController.fail("Location provider is disabled");
		fail("Location provider is disabled");
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		if(status == LocationProvider.OUT_OF_SERVICE) {
			//mOrmmaLocationController.fail("Location provider is out of service");
			fail("Location provider is out of service");
		}
	}

	public void onLocationChanged(Location location) {
		if ((mInterval <= 0) && (mDistance <= 0.0f))
		{
			// Legacy behavior: grab initial location fix and continue using it, no updates
			mLocMan.removeUpdates(this);
			if (adLog != null)
			{
				adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "GPSLocation", "Listener stopped after update.");
			}
		}
		
		adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "GPSLocation", "Location change called.");
		
		//mOrmmaLocationController.success(location);
		success(location);
	}

	public void stop() {
		mLocMan.removeUpdates(this);
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	public void start() {
		//mLocMan.requestLocationUpdates(mProvider, 0, 0, this);
		adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "GPSLocation", "Listener started.");
		
		if (listenerLooper != null)
		{
			mLocMan.requestLocationUpdates(mProvider, mInterval, mDistance, this, listenerLooper);
		}
		else
		{
			mLocMan.requestLocationUpdates(mProvider, mInterval, mDistance, this);
		}
	}
}
