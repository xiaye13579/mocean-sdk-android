package com.moceanmobile.flickrviewer;

import com.MASTAdView.MASTAdLog;

public class Constants
{
	public static final int application_site					= 23814; // was 19829 for testing (from sample)
	
	public static final int SHOW_OFF_BOOKMARKS_z_112076			= 112076;
	public static final int SHOW_OFF_MAIN_BOTTOM_z_112067		= 112067; // embedded in XML for now...
	public static final int SHOW_OFF_MAIN_INTERSTITIAL_z_112074	= 112074;
	public static final int SHOW_OFF_MAIN_TOP_z_112073			= 112073; // embedded in XML for now...
	public static final int SHOW_OFF_POPUP_z_112075				= 112075;
	
	public static final int banner_ad_update_time				= 45; // was 60, changed per aron
	public static final int interstitial_interval 				= 10; // show interstitial after this many images
	public static final int interstitial_auto_close_time 		= 20;
	public static final int interstital_show_close_delay 		= 2;
	
	public static final int millenial_ad_refresh				= 0; // 0 - never (manual)
	
	public static final int defaultAdLogLevel 					= MASTAdLog.LOG_LEVEL_2;
	
	public static final double metainfoPopupWidthFactor			= 0.7;
	
	public static final int picture_swipe_navigate_delta		= 10; // min. 10 pixel swipe to navigate next/back
	
	// Banner ad view location on main screen
	final public static int banner_location_top 				= -1;
	final public static int banner_location_bottom 				= 1;
}
