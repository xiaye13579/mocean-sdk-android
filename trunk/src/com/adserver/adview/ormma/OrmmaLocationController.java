/*© 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.*/
package com.adserver.adview.ormma;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.adserver.adview.AdServerViewCore;
import com.adserver.adview.ormma.listeners.LocListener;

public class OrmmaLocationController extends OrmmaController {
	private LocationManager mLocationManager;
	private boolean hasPermission = false;
	final int INTERVAL = 1000;
	private LocListener mGps;
	private LocListener mNetwork;
	private int mLocListenerCount;

	public OrmmaLocationController(AdServerViewCore adView, Context context) {
		super(adView, context);

		try{
			mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			if (mLocationManager.getProvider(LocationManager.GPS_PROVIDER) != null)
				mGps = new LocListener(context, INTERVAL, this, LocationManager.GPS_PROVIDER);
			if (mLocationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null)
				mNetwork = new LocListener(context, INTERVAL, this, LocationManager.NETWORK_PROVIDER);
			hasPermission = true;
		} catch (SecurityException e){
			mOrmmaView.injectJavaScript("Ormma.fireError(\"location\",\"Security error\")");
		}
	}

	public String getLocation() {
		if(!hasPermission){
			return null;
		}
		List<String> providers = mLocationManager.getProviders(true);
		Iterator<String> provider = providers.iterator();
		Location lastKnown = null;
		while(provider.hasNext()) {
			lastKnown = mLocationManager.getLastKnownLocation(provider.next());
			if(lastKnown != null) {
				break;
			}
		}
		if(lastKnown != null) {
			return "{ \"lat\": " + lastKnown.getLatitude() + ", " + 
				"\"lon\": " + lastKnown.getLongitude() + ", " + 
				"\"acc\": " + lastKnown.getAccuracy() + "}";
		} else {
			return null;
		}
	}
	
	public void startLocationListener() {
		if(mLocListenerCount == 0) {
			if(mNetwork != null)
				mNetwork.start();
			if(mGps != null)
				mGps.start();
		}
		mLocListenerCount++;
	}

	public void stopLocationListener() {
		if(mLocListenerCount > 0) {			
			mLocListenerCount--;

			if(mLocListenerCount == 0) {			
				if(mNetwork != null)
					mNetwork.stop();
				if(mGps != null)
					mGps.stop();
			}
		}
	}

	public void success(Location loc) {
		String ret = "{ \"lat\": " + loc.getLatitude() + ", " + "\"lon\": " + 
			loc.getLongitude() + ", " + "\"acc\": " + loc.getAccuracy() + "}";
		mOrmmaView.injectJavaScript("Ormma.locationChanged(" + ret + ")");
	}
	
	public void fail(String description) {
		mOrmmaView.injectJavaScript("Ormma.fireError(\"location\"," + description + ")");
	}
	
}
