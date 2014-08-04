/*
 * PubMatic Inc. (“PubMatic”) CONFIDENTIAL
 * Unpublished Copyright (c) 2006-2014 PubMatic, All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of PubMatic. The intellectual and technical concepts contained
 * herein are proprietary to PubMatic and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from PubMatic.  Access to the source code contained herein is hereby forbidden to anyone except current PubMatic employees, managers or contractors who have executed 
 * Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure  of  this source code, which includes  
 * information that is confidential and/or proprietary, and is a trade secret, of  PubMatic.   ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC  PERFORMANCE, 
 * OR PUBLIC DISPLAY OF OR THROUGH USE  OF THIS  SOURCE CODE  WITHOUT  THE EXPRESS WRITTEN CONSENT OF PubMatic IS STRICTLY PROHIBITED, AND IN VIOLATION OF APPLICABLE 
 * LAWS AND INTERNATIONAL TREATIES.  THE RECEIPT OR POSSESSION OF  THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS  
 * TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE, USE, OR SELL ANYTHING THAT IT  MAY DESCRIBE, IN WHOLE OR IN PART.                
 */

package com.moceanmobile.mast;

public class Defaults
{
	public static final String SDK_VERSION = "3.1.2";
	
	// This is used if the WebView's value returned is empty.
	public static final String USER_AGENT = "MASTAdView/" + SDK_VERSION + " (Android)";
	
	public static final int NETWORK_TIMEOUT_SECONDS = 5;
	
	public static final int LOCATION_DETECTION_MINTIME = 10 * 60 * 1000; // 10 Minutes in ms
	public static final int LOCATION_DETECTION_MINDISTANCE = 20; // Meters
	
	// How much content is allowed after parsing out click url and image or text content before
	// falling through and rendering as html vs. native rendering.
	public static final int DESCRIPTOR_THIRD_PARTY_VALIDATOR_LENGTH = 20;
	
	public static final String AD_NETWORK_URL = "http://ads.moceanads.com/ad";
	
	// Default injection HTML rich media ads.
	// IMPORTANT: These strings have specific format specifiers (%s).
	//   Improper modification to these strings can cause ad rendering failures.
	public static final String RICHMEDIA_FORMAT = "<html><head><meta name=\"viewport\" content=\"user-scalable=0\"/><style>body{margin:0;padding:0;}</style><script type=\"text/javascript\">%s</script></head><body>%s</body></html>";
	public static final String RICHMEDIA_FORMAT_API11 = "<html><head><meta name=\"viewport\" content=\"user-scalable=0\"/><style>body{margin:0;padding:0;}</style></head><body>%s</body></html>";
}
