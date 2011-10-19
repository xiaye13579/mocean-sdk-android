
package com.adserver.adview.ormma;

import java.util.Enumeration;
import java.util.Hashtable;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.adserver.adview.AdServerViewCore;
import com.adserver.adview.Base64;
import com.adserver.adview.ormma.util.OrmmaNetworkBroadcastReceiver;

public class OrmmaNetworkController extends OrmmaController {
	private ConnectivityManager mConnectivityManager;
	private int mNetworkListenerCount;
	private OrmmaNetworkBroadcastReceiver mBroadCastReceiver;
	private IntentFilter mFilter;
	private Hashtable<String, String> requests;

	public OrmmaNetworkController(AdServerViewCore adView, Context context) {
		super(adView, context);
		mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		requests = new Hashtable<String, String>();
	}
	
	public String getNetwork() {
		NetworkInfo ni = mConnectivityManager.getActiveNetworkInfo();

		if(ni == null)
			return "offline";

		switch(ni.getState()) {
		case UNKNOWN:
			return "unknown";
		case DISCONNECTED:
			return "offline";
		default:
			int type = ni.getType();
			if(type == ConnectivityManager.TYPE_MOBILE)
				return "cell";
			if(type == ConnectivityManager.TYPE_WIFI)
				return "wifi";
		}

		return "unknown";
	}

	private boolean isOnline() {
		NetworkInfo ni = mConnectivityManager.getActiveNetworkInfo();

		if (ni == null) {
			return false;
		} else {
			return ni.isConnected();
		}
	}
	
	public void startNetworkListener() {		
		if(mNetworkListenerCount == 0) {
			mBroadCastReceiver = new OrmmaNetworkBroadcastReceiver(this);
			mFilter = new IntentFilter();
			mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		}
		mNetworkListenerCount++;
		//mContext.registerReceiver(mBroadCastReceiver, mFilter);
		try
		{
			mContext.registerReceiver(mBroadCastReceiver, mFilter);
		}catch(Exception e)
		{
			//mContext.unregisterReceiver(mBroadCastReceiver);
			//mContext.registerReceiver(mBroadCastReceiver, mFilter);
			//mNetworkListenerCount--;
		}
	}

	public void stopNetworkListener() {
		if(mNetworkListenerCount > 0) {			
			mNetworkListenerCount--;
			
			if(mNetworkListenerCount == 0) {	
				stopAllNetworkListeners();
			}
		}
	}

	public void stopAllNetworkListeners() {
		try
		{
			mContext.unregisterReceiver(mBroadCastReceiver);
		}catch(Exception e)
		{
		}
		mBroadCastReceiver = null;
		mFilter = null;
	}
	
	public void onConnectionChanged() {
		String ret = "{\"online\": " + isOnline() + ", " + "\"connection\": \"" + getNetwork() + "\"}";
		mOrmmaView.injectJavaScript("Ormma.gotNetworkChange(" + ret + ")");

		if(isOnline()) {
			Enumeration<String> keys = requests.keys();
			while (keys.hasMoreElements()) {
				String uri = keys.nextElement();
				String display = requests.get(uri);
				response(uri, display);
			}
			requests.clear();
		}
	}

	public void request(String uri, String display) {
		mOrmmaView.ormmaEvent("request", "uri="+uri+";display="+display);
		if(isOnline()) {
			response(uri, display);
		} else {
			requests.put(uri, display);
		}
	}

	private void response(String uri, String display) {
		try {
			String response = OrmmaAssetController.getHttpContent(uri);
			response = Base64.encodeString(response);
			
			if(display.equalsIgnoreCase("proxy")) {
				mOrmmaView.injectJavaScript("Ormma.gotResponse(\"" + uri + "\", \"" + response + "\")");
			}
		} catch (Exception e) {
			mOrmmaView.injectJavaScript("Ormma.fireError(\"response\",\"Could not read uri content\")");
		}
	}
	
}
