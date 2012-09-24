//
// Copyright (C) 2011, 2012 Mocean Mobile. All Rights Reserved. 
//
package com.MASTAdView;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.MASTAdView.core.AdViewContainer;
import com.MASTAdView.core.AdWebView;


/**
 * Ad view object, acting as a container for an ad view control. This is the main object developers
 * use when integrating the SDK into an application. This class (and the related public classes
 * in this documentation set) make up the public interfaces which developers can safely interact with.
 * Classes and methods not defined in this documentation may change.
 * <P>
 * The ad publisher site and zone must be configured to fetch and display an ad. Ads can range from
 * simple text up to rich media ads with MRAID 1 or 2 javascript content. 
 * <P>
 * A variety of optional properties can be set that assist the back-end in delivering content targeted to the app user.
 * These includes the publisher site and zone. These and other named parameters are managed via the MASTAdRequest object.
 * See the documentation for MASTAdRequest for more information about these parameters, which are passed to the back-end
 * when requesting ad content.
 * <P>
 * Related to this are several properties which control on-device ad behavior, such as enabling automatic timed based ad
 * updates (via the setUpdateTime() method), controlling diagnostic logging (via the setLogLevel() method), controlling
 * use of GPS location detection to serve ads in some proximity to the user (via the setLocationDetection() method), and
 * other described in this documentation.
 * <P>
 * In addition, a number of properties control the display and behavior of ad content after it has been delivered to
 * the SDK. These parameters are manipulated through methods in this class itself; examples are the addCloseToBanner()
 * method and others documented here. Finally, the developer can obtain references to the actual text, image or web
 * view object that will display ads with the getAdTextView(), getAdImageView() or getAdWebView() methods. With these
 * objects the full range of standard Android view properties are available for manipulation (for advanced developers only!)
 */
public class MASTAdView extends AdViewContainer
{
	/**
	 * Create ad view/container for displaying ads obtained through this SDK. This is the normal signature
	 * a developer uses when setting up ad views using a code-only approach.
	 * @param context - The reference to the context of Activity; NOTE a context object will work for most purposes, but some device specific tasks (such as orientation or location handling) only work if this is an actual activity reference.
	 * @param site - The id of the publisher site (presets the standard ad request site property).
	 * @param zone - The id of the zone of publisher site  (presets the standard ad request zone property).
	 */
	public MASTAdView(Context context, Integer site, Integer zone)
	{
		super(context, site, zone);
	}
	
	
	/**
	 * Create ad view/container for displaying ads obtained through this SDK.
	 * This signature is used when creating an ad view from an XML template.
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public MASTAdView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	
	/**
	 * Create ad view/container for displaying ads obtained through this SDK.
	 * This signature is used when creating an ad view from an XML template.
	 * @param context
	 * @param attrs
	 */
	public MASTAdView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}


	/**
	 * Create ad view/container for displaying ads obtained through this SDK.
	 * This signature is used when creating an ad view from an XML template.
	 * @param context
	 */
	public MASTAdView(Context context)
	{
		super(context);
	}

	
	/**
	 * Request new ad content from the back-end. The network transactions will be performed in a background (non-UI)
	 * thread to prevent slowing down or locking up the application UI, and once new content is available the display
	 * of the ad will automatically run on the UI thread again.
	 */
	public void update()
	{
		super.update();
	}


	/**
	 * Set banner refresh interval (in seconds). Once an ad has finished loading, the timer starts
	 * and a new ad will be loaded after this amount of time has elapsed. Default 120 seconds.
	 * If 0, ads are not updated automatically (use the update() method for a manual update.)
	 * If less than 0, the default value will be used instead.
	 */
	public void setUpdateTime(int updateTime)
	{
		super.setUpdateTime(updateTime);
	}
	
	
	/**
	 *  Show interstitial ad, which appears as a full screen and will popup over top of the current application activity.
	 *  A standard close button will be included as specified by the MRAID standard.
	 *  NOTE: you do not need to add the view to a layout when used for interstitial ads.
	 */
	public void show()
	{
		super.show();
	}
	

	/**
	 * Set log level to one of the log level values defined in he MASTAdLog class
     * (corresponding to errors, errors + warnings, or everything including server traffic.)
     * The SDK is instrumented with diagnostics logging that can assist with troubleshooting
     * integration problems. Log messages are sent to the system logging interface (viewable
     * with logcat) and an in-memory log of recent messages is stored for easy access.
     * @see MASTAdLog See the MASTAdLog class for more information about logging.
	 * @param logLevel Int log level to control which messages will be sent to the logs.
	 */
	public void setLogLevel(int logLevel)
	{
		super.setLogLevel(logLevel);
	}

	
	/**
	 * Get reference to the web view object used when displaying rich media ads. Advanced developers can use this to
	 * customize the display and behavior of that control with the full range of standard view attributes supported
	 * by the Android system. Do not alter this object unless you know what you are doing!
	 */
	public AdWebView getAdWebView()
	{
		return super.getAdWebView();
	}
	
	
	/**
	 * Get reference to the image view object used when displaying image only ads. Advanced developers can use this to
	 * customize the display and behavior of that control with the full range of standard view attributes supported
	 * by the Android system. Do not alter this object unless you know what you are doing!
	 */
	public ImageView getAdImageView()
	{
		return super.getAdImageView();
	}
	
	
	/**
	 * Get reference to the text view object used when displaying text only ads. Advanced developers can use this to
	 * customize the display and behavior of that control with the full range of standard view attributes supported
	 * by the Android system. Do not alter this object unless you know what you are doing!
	 */
	public TextView getAdTextView()
	{
		return super.getAdTextView();
	}
	
	
	/**
	 * Get the user-agent string that will be sent to back-end ad server and to tracking URL servers to report when an ad
	 * has been displayed. This is normally the same string used by the underlying web browser on the device.
	 */
	public String getUserAgent()
	{
		return super.getUserAgent();
	}

	
	/**
	 * Get reference to diagnostics logging object used internal to the SDK.
	 * @see MASTAdLog See the MASTAdLog class for more information about logging. 
	 */
	public MASTAdLog getAdLog()
	{
		return super.getLog();
	}
	
	
	/**
	 * Get ad server request object which allows customizing parameters (such as the site or zone)
	 * that will be sent to the back-end ad server when fetching ads; for example, to set the
	 * zone, use: "getAdRequest().setProperty(MASTAdRequest.parameter_zone, myNewZone);"
	 * @see MASTAdRequest See the MASTAdRequest class for more information about working
	 * with this object, and the available parameters which you can customize. 
	 * @return Object encapsulating parameter sent to back-end when requesting an ad.
	 */
	public MASTAdRequest getAdRequest()
	{
		return adserverRequest;
	}

	
	/**
	 * Get ad delegate object which developers can use to extend and/or interact with
	 * feature and functions of the SDK ad handling, such as ad download callbacks,
	 * MRAID event notifications, etc.
	 * @see MASTAdDelegate See the MASTAdDelegate class for more information.
	 * @return Object encapsulating various delegate interfaces and get/set methods.
	 */
	public MASTAdDelegate getAdDelegate()
	{
		return adDelegate;
	}

	
	/**
	 * Get URL string used to request the last ad from back-end.
	 */
	public String getLastRequest()
	{
		return super.getLastRequest();
	}
	
	
	/**
	 * Return response data (body) returned for the last ad received from the back-end.
	 */
	public String getLastResponse()
	{
		return super.getLastResponse();
	}

	
	/**
	 * Set ad placement value which rich media ads can use to determine if they are operating in a banner (inline)
	 * or interstitial (full screen transitional) context. Use true for interstitial, false for banner. Default is false
	 * for banner ads.
	 */
	public void setAdPlacementInterstitial(boolean isInterstitial)
	{
		super.setAdPlacementInterstitial(isInterstitial);
	}
	
		
	/**
	 * Turn location detection on or off. If enabled, the device GPS location capabilities will be used to obtain
	 * a position fix at least one. Ongoing location updates may continue depending on the minimum wait and minimum
	 * distance settings. If user-specified latitude and/or longitude values have been set, and location detection
	 * is enabled, the detected location will override preset values.
	 * @param detect If true, location detection is enabled.
	 * @param minWaitMillis Set minimum time between location updates from the GPS system, in milliseconds; default 0 (no recurring updates.)
	 * @param minMoveMeters Set minimum distance (in meters) location must change for app to be notified of new location from the GPS system. Use 0 (the default) if no distance based updates are desired.
	 */
	public void setLocationDetection(boolean detect, Integer minWaitMillis, Float minMoveMeters)
	{
		super.setLocationDetection(detect, minWaitMillis, minMoveMeters);
	}
	

	/**
	 * Control if a close button will be displayed on a banner ad, or not. Default is false.
	 */
	public void addCloseToBanner(boolean flag)
	{
		super.addCloseToBanner(flag);
	}
}
