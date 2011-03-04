/*© 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.*/
package com.adserver.adview.ormma;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import com.adserver.adview.AdServerViewCore;
import com.adserver.adview.ormma.util.OrmmaConfigurationBroadcastReceiver;

public class OrmmaDisplayController extends OrmmaController {
	private WindowManager mWindowManager;
	private OrmmaConfigurationBroadcastReceiver mConfigurationBroadCastReceiver;
	private IntentFilter mConfigurationFilter;
	private int mOrientationListenerCount = 0;
	private float mDensity;
	private Dimensions defaultPosition;

	public OrmmaDisplayController(AdServerViewCore adView, Context c) {
		super(adView, c);
		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mDensity = metrics.density;
		mWindowManager = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
	}

	public void getState() {
		mOrmmaView.getState();
	}
	
	public void resize(int width, int height) {
		DisplayMetrics metrics = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(metrics);

		if ((height > metrics.heightPixels) || (width > metrics.widthPixels)) {
			mOrmmaView.injectJavaScript("Ormma.fireError(\"resize\",\"Maximum size exceeded\")");
		} else {
			mOrmmaView.resize((int)(mDensity*width), (int)(mDensity*height));
		}
	}

	public void open(String url) {
		try {
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
			mContext.startActivity(i);
		} catch (Exception e) {
			mOrmmaView.injectJavaScript("Ormma.fireError(\"open\",\"Cannot open this URL\")");
		}
	}

	public void expand(String dimensions, String URL, String properties) {

		try {
			Dimensions d = (Dimensions) getFromJSON(new JSONObject(dimensions), Dimensions.class);
			d.width *= mDensity;
			d.height *= mDensity;
			d.x *= mDensity;
			d.y *= mDensity;
			if (d.height < 0)
				d.height = mOrmmaView.getHeight();
			if (d.width < 0)
				d.width = mOrmmaView.getWidth();
			int loc[] = new int[2];
			mOrmmaView.getLocationInWindow(loc);
			if (d.x < 0)
				d.x = loc[0];
			if (d.y < 0) {
				int topStuff = 0;// ((Activity)mContext).findViewById(Window.ID_ANDROID_CONTENT).getTop();
				d.y = loc[1] - topStuff;
			}
			
			mOrmmaView.expand(d, URL, (Properties) getFromJSON(new JSONObject(properties), Properties.class));
		} catch (NumberFormatException e) {
			mOrmmaView.injectJavaScript("Ormma.fireError(\"expand\",\"Wrong number\")");
		} catch (JSONException e) {
			mOrmmaView.injectJavaScript("Ormma.fireError(\"expand\",\"Wrong JSON format\")");
		} catch (Exception e) {
			mOrmmaView.injectJavaScript("Ormma.fireError(\"expand\",\"Internal error\")");
		}
	}

	public void close() {
		mOrmmaView.close();
	}

	public void hide() {
		mOrmmaView.hide();
	}

	public void show() {
		mOrmmaView.show();
	}

	public boolean isVisible() {
		return (mOrmmaView.getVisibility() == View.VISIBLE);
	}

	public String dimensions() {
		return "{ \"top\" :" + mOrmmaView.getTop()*mDensity + "," + "\"left\" :" + mOrmmaView.getLeft()*mDensity + "," + "\"bottom\" :"
				+ mOrmmaView.getBottom()*mDensity + "," + "\"right\" :" + mOrmmaView.getRight()*mDensity + "}";
	}

	public int getOrientation() {
		int orientation = mWindowManager.getDefaultDisplay().getOrientation();
		int ret = -1;
		switch (orientation) {
		case Surface.ROTATION_0:
			ret = 0;
			break;

		case Surface.ROTATION_90:
			ret = 90;
			break;

		case Surface.ROTATION_180:
			ret = 180;
			break;

		case Surface.ROTATION_270:
			ret = 270;
			break;
		}
		return ret;
	}

	public String getScreenSize() {
		DisplayMetrics metrics = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(metrics);

		return "{ \"width\": " + metrics.widthPixels + ", " + 
		"\"height\": " + metrics.heightPixels + "}";
	}

	public String getSize() {
		return "{ \"width\": " + mOrmmaView.getWidth()*mDensity + ", " + 
		"\"height\": " + mOrmmaView.getHeight()*mDensity + "}";
	}

	public String getMaxSize() {
		return getScreenSize();
	}

	public String getDefaultPosition() {
		if(defaultPosition != null) {
			return "{ \"x\": " + defaultPosition.x + ", \"y\": " + defaultPosition.y + ", " + 
			"\"width\": " + defaultPosition.width + ", \"height\": " + defaultPosition.height + "}";
		} else {
			return "";
		}
	}

	public void setDefaultPosition() {
		if(defaultPosition == null) {
			defaultPosition = new Dimensions();
			defaultPosition.x = (int)(mOrmmaView.getLeft()*mDensity);
			defaultPosition.y = (int)(mOrmmaView.getTop()*mDensity);
			defaultPosition.width = (int)(mOrmmaView.getWidth()*mDensity);
			defaultPosition.height = (int)(mOrmmaView.getHeight()*mDensity);
		}
	}
	
	public void startOrientationListener() {
		if(mOrientationListenerCount == 0) {
			mConfigurationBroadCastReceiver = new OrmmaConfigurationBroadcastReceiver(this);
			mConfigurationFilter = new IntentFilter();
			mConfigurationFilter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
		}
		mOrientationListenerCount++;
		mContext.registerReceiver(mConfigurationBroadCastReceiver, mConfigurationFilter);
	}

	public void stopOrientationListener() {
		if(mOrientationListenerCount > 0) {			
			mOrientationListenerCount--;

			if(mOrientationListenerCount == 0) {			
				mContext.unregisterReceiver(mConfigurationBroadCastReceiver);
				mConfigurationBroadCastReceiver = null;
				mConfigurationFilter = null;
			}
		}
	}

	public void onOrientationChanged(int orientation) {
		mOrmmaView.injectJavaScript("Ormma.gotOrientationChange(" + orientation + ")");
	}
	
}
