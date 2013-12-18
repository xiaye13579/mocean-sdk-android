//
// Copyright (C) 2013 Mocean Mobile. All Rights Reserved. 
//
package com.moceanmobile.mast;

public class Defaults
{
	public static final String SDK_VERSION = "3.1.2";
	
	// This is used if the WebView's value returned is empty.
	public static final String USER_AGENT = "MASTAdView/" + SDK_VERSION + " (Android)";
	
	public static final int NETWORK_TIMEOUT_SECONDS = 5;
	
	public static final int LOCATION_DETECTION_MINTIME = 10 * 60 * 1000; // 10 Minutes in ms
	public static final int LOCATION_DETECTION_MINDISTANCE = 20; // Meters
	
	public static final String AD_NETWORK_URL = "http://ads.moceanads.com/ad";
	
	// Default injection HTML rich media ads.
	// IMPORTANT: These strings have specific format specifiers (%s).
	//   Improper modification to these strings can cause ad rendering failures.
	public static final String RICHMEDIA_FORMAT = "<html><head><meta name=\"viewport\" content=\"user-scalable=0\"/><style>body{margin:0;padding:0;}</style><script type=\"text/javascript\">%s</script></head><body>%s</body></html>";
	public static final String RICHMEDIA_FORMAT_API11 = "<html><head><meta name=\"viewport\" content=\"user-scalable=0\"/><style>body{margin:0;padding:0;}</style></head><body>%s</body></html>";
}
