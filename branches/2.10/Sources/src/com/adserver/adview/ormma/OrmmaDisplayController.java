
package com.adserver.adview.ormma;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;


import com.adserver.adview.MASTAdLog;
import com.adserver.adview.MASTAdServerViewCore;
import com.adserver.adview.ormma.util.OrmmaConfigurationBroadcastReceiver;

public class OrmmaDisplayController extends OrmmaController {
	private WindowManager mWindowManager;
	private OrmmaConfigurationBroadcastReceiver mConfigurationBroadCastReceiver;
	private IntentFilter mConfigurationFilter;
	private int mOrientationListenerCount = 0;
	//private float mDensity;
	private Dimensions defaultPosition;
	private static final String LOG_TAG = "OrmmaDisplayController";

	public OrmmaDisplayController(MASTAdServerViewCore adView, Context c) {
		super(adView, c);
		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		//mDensity = metrics.density;
		mWindowManager = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
	}

	public String getPlacementType()
	{
		return mOrmmaView.isInterstitial() ? "interstitial" : "inline";
	}
	
	public void useCustomClose(boolean flag)
	{
		mOrmmaView.useCustomClose(flag);
	}
	
	public void getState() {
		mOrmmaView.getState();
	}
	
	public void resize(int width, int height) {
		int maxSizeWidth = width;
		int maxSizeHeight = height;

		try {
			String maxSize = getMaxSize();
			Dimensions properties = (Dimensions) getFromJSON(new JSONObject(maxSize), Dimensions.class);
			maxSizeWidth = properties.width;
			maxSizeHeight = properties.height;
			
			if (width > maxSizeWidth) {
				width = maxSizeWidth;
			}
			if (height > maxSizeHeight) {
				height = maxSizeHeight;
			}
		} catch (Exception e) {
		}
		
		mOrmmaView.resize((int)(/*mDensity*/width/*mDensity*/), (int)(/*mDensity*/height/*mDensity*/));
	}

	public void open(String url) {
		try {
			mOrmmaView.ormmaEvent("open", "url="+url);
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
			mContext.startActivity(i);
		} catch (Exception e) {
			mOrmmaView.injectJavaScript("Ormma.fireError(\"open\",\"Cannot open this URL\")");
		}
	}

	public void expand(String URL, String properties) {
		try {
			Dimensions d = new Dimensions(); //(Dimensions) getFromJSON(new JSONObject(dimensions), Dimensions.class);
			Properties prop = (Properties) getFromJSON(new JSONObject(properties), Properties.class);
			d.width = prop.width;//*mDensity;
			d.height =prop.height;//*mDensity;
			d.x = 0;
			d.y = 0;
			/*if (d.height < 0)
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
			}*/
			
			mOrmmaView.expand(d, URL, prop);
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
	
	public boolean getViewable()
	{		
		return (mOrmmaView.getVisibility() == View.VISIBLE);
		
	}

	public String dimensions() {
		return "{ \"top\" :" + mOrmmaView.getTop()/*mDensity*/ + "," + "\"left\" :" + mOrmmaView.getLeft()/*mDensity*/ + "," + "\"bottom\" :"
				+ mOrmmaView.getBottom()/*mDensity*/ + "," + "\"right\" :" + mOrmmaView.getRight()/*mDensity*/ + "}";
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
		return "{ \"width\": " + mOrmmaView.getWidth()/*mDensity*/ + ", " + 
		"\"height\": " + mOrmmaView.getHeight()/*mDensity*/ + "}";
	}

	public String getMaxSize() {
		Rect rect= new Rect();
		Window window= ((Activity) mOrmmaView.getContext()).getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rect);
		int statusBarHeight= rect.top;
		int contentViewTop= window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		int height=rect.bottom - contentViewTop;
		int width=rect.right - rect.left;
		
		return "{ \"width\": " + width + ", " + 
		"\"height\": " + height + "}";
		//return getScreenSize();
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
			defaultPosition.x = (int)(mOrmmaView.getLeft()/*mDensity*/);
			defaultPosition.y = (int)(mOrmmaView.getTop()/*mDensity*/);
			defaultPosition.width = (int)(mOrmmaView.getWidth()/*mDensity*/);
			defaultPosition.height = (int)(mOrmmaView.getHeight()/*mDensity*/);
		}
	}
	
	public void startOrientationListener() {
		if((mOrientationListenerCount == 0) || (mConfigurationBroadCastReceiver==null) || 
				(mConfigurationFilter==null)) {
			mConfigurationBroadCastReceiver = new OrmmaConfigurationBroadcastReceiver(this);
			mConfigurationFilter = new IntentFilter();
			mConfigurationFilter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
		}
		mOrientationListenerCount++;
		try
		{
			mContext.registerReceiver(mConfigurationBroadCastReceiver, mConfigurationFilter);
		}catch (Exception e) {
			mOrmmaView.getLog().log(MASTAdLog.LOG_LEVEL_1, MASTAdLog.LOG_TYPE_ERROR, "startOrientationListener", e.getMessage());
		}
	}

	public void stopOrientationListener() {
		if(mOrientationListenerCount > 0) {			
			mOrientationListenerCount--;

			if(mOrientationListenerCount == 0) {
				stopAllOrientationListeners();
			}
		}
	}

	public void stopAllOrientationListeners() {
		try
		{
			mContext.unregisterReceiver(mConfigurationBroadCastReceiver);
		}catch(Exception e)
		{
		}
		mConfigurationBroadCastReceiver = null;
		mConfigurationFilter = null;
	}
	
	public void onOrientationChanged(int orientation) {
		mOrmmaView.injectJavaScript("Ormma.gotOrientationChange(" + orientation + ")");
	}
	
	/**
	 * Play video
	 * @param url - video url to be played
	 * @param audioMuted - should audio be muted
	 * @param autoPlay - should video play immediately
	 * @param controls  - should native player controls be visible
	 * @param loop - should video start over again after finishing
	 * @param position - top and left coordinates of video in pixels if video should play inline
	 * @param startStyle - normal/fullscreen (if video should play in native full screen mode)
	 * @param stopStyle - normal/exit (exit if player should exit after video stops)
	 */
	public void playVideo(String url, boolean audioMuted, boolean autoPlay, boolean controls, boolean loop, int[] position, String startStyle, String stopStyle) {
		Log.d("OrmmaDisplayController", "playVideo: url: " + url + " audioMuted: " + audioMuted + " autoPlay: " + autoPlay + " controls: " + controls + " loop: " + loop + " x: " + position[0] + 
				" y: " + position[1] + " width: " + position[2] + " height: " + position[3] + " startStyle: " + startStyle + " stopStyle: " + stopStyle);
		Dimensions d = null;
		if(position[0] != -1) {
			d = new Dimensions();
			d.x = position[0];
			d.y = position[1];
			d.width = position[2];
			d.height = position[3];
//			d = getDeviceDimensions(d);
		}		
		if(!URLUtil.isValidUrl(url)){
			Log.d(LOG_TAG, "invalid url: " + url);
//			mOrmmaView.raiseError("Invalid url", "playVideo");
		}else{
			mOrmmaView.playVideo(url, audioMuted, autoPlay, controls, loop, d, startStyle, stopStyle);
		}
	}
	
	/**
	 * Play audio
	 * @param url - audio url to be played
	 * @param autoPlay - if audio should play immediately
	 * @param controls - should native player controls be visible
	 * @param loop - should video start over again after finishing
	 * @param position - should audio be included with ad content
	 * @param startStyle - normal/full screen (if audio should play in native full screen mode)
	 * @param stopStyle - normal/exit (exit if player should exit after audio stops)
	 */
	public void playAudio(String url, boolean autoPlay, boolean controls, boolean loop, boolean position, String startStyle, String stopStyle) {
		Log.d(LOG_TAG, "playAudio: url: " + url + " autoPlay: " + autoPlay + " controls: " + controls + " loop: " + loop + " position: " + position + " startStyle: " + startStyle + " stopStyle: "+stopStyle);
		if(!URLUtil.isValidUrl(url)){
//			mOrmmaView.raiseError("Invalid url", "playAudio");
		}else{
			mOrmmaView.playAudio(url, autoPlay, controls, loop, position, startStyle, stopStyle);
		}
		
	}
	/**Open map
	 * @param url - map url
	 * @param fullscreen - boolean indicating whether map to be launched in full screen
	 */
	public void openMap(String url, boolean fullscreen) {
		mOrmmaView.ormmaEvent("openmap", "url="+url+";fullscreen="+String.valueOf(fullscreen));
		Log.d(LOG_TAG, "openMap: url: " + url);
		mOrmmaView.openMap(url, fullscreen);
	}

	
	/**
	 * Get Device dimensions
	 * @param d - dimensions received from java script
	 * @return
	 */
	/*private Dimensions getDeviceDimensions(Dimensions d){
		d.width /= mDensity;
		d.height /= mDensity;
		d.x /= mDensity;
		d.y /= mDensity;
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
		return d;
	}*/
}
