/*© 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.*/
package com.adserver.adview.ormma.listeners;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import com.adserver.adview.ormma.OrmmaLocationController;

public class LocListener implements LocationListener {
	OrmmaLocationController mOrmmaLocationController;
	private LocationManager mLocMan;
	private String mProvider;
	private long mInterval;
	
	public LocListener(Context c, int interval, OrmmaLocationController ormmaLocationController, String provider) {
		mOrmmaLocationController = ormmaLocationController;
		mLocMan = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
		mProvider = provider;
		mInterval = interval;
	}
	
	public void onProviderDisabled(String provider) {
		mOrmmaLocationController.fail("Location provider is disabled");
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		if(status == LocationProvider.OUT_OF_SERVICE) {
			mOrmmaLocationController.fail("Location provider is out of service");
		}
	}

	public void onLocationChanged(Location location) {
		mOrmmaLocationController.success(location);
	}

	public void stop() {
		mLocMan.removeUpdates(this);
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	public void start() {
		mLocMan.requestLocationUpdates(mProvider, 0, 0, this);		
	}
	
}
