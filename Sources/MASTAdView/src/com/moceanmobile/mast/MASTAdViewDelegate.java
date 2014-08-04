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

import java.util.Map;

import android.graphics.Rect;

public interface MASTAdViewDelegate
{
	public interface RequestListener
	{
		/**
		 * Failed to receive ad content (network or other related error).
		 * 
		 * @param adView 
		 * @param ex Exception, if any, encountered while attempting to reqest an ad.
		 */
		public void onFailedToReceiveAd(MASTAdView adView, Exception ex);
		
		/**
		 * Ad received and rendered.
		 * 
		 * @param adView
		 */
		public void onReceivedAd(MASTAdView adView);
		
		/**
		 * Third party ad received.  The application should be expecting this and ready to
		 * render the ad with the supplied configuration.
		 * 
		 * @param adView
		 * @param properties Properties of the ad request (ad network information).
		 * @param parameters Parameters for the third party network (expected to be passed to that network).
		 */
		public void onReceivedThirdPartyRequest(MASTAdView adView, Map<String, String> properties, Map<String, String> parameters);
	}

	public interface ActivityListener
	{
		/**
		 * Invoked when the ad will navigate to a clicked link (or rich media open).
		 * 
		 * Applications can use this method to filter URLs being opened by the SDK.  If 
		 * the application will handle the URL directly it should return false from this
		 * method so the SDK doesn't also act on the URL.
		 * 
		 * Note that for rich media ads the SDK may have already resized or expanded the ad 
		 * and this method may be invoked when the ad invokes MRAID's open method.
		 * 
		 * @param adView The MASTAdView instance invoking the method.
		 * @param url The URL to open.
		 * @return Boolean true if caller has completely handled the click event and wants
		 * to skip the default SDK click processing; return false if the caller has only
		 * implemented a "side-effect" such as logging, and wants the default SDK logic
		 * to continue. 
		 */
		public boolean onOpenUrl(MASTAdView adView, String url);
		
		/**
		 * Invoked when the ad will start a new system activity.
		 * 
		 * @param adView
		 */
		public void onLeavingApplication(MASTAdView adView);
		
		/**
		 * Invoked when the ad receives a close button press that should be handled by 
		 * the application.
		 * 
		 * This only occurs for the close button enabled with showCloseButton() or in 
		 * the case of a interstitial rich media ad that closes itself.  It will not be
		 * sent for rich media close buttons that collapse expanded or resized ads. 
		 * 
		 * @param adView The MASTAdView instance invoking the method.
		 * @return Boolean true if the caller has completely handled the click event and
		 * wants to skip the default SDK click processing; return false if the caller has
		 * only implemented a "side-effect" such as logging and wants the default SDK logic
		 * to continue.  For MASTAdView instances that are interstitial implementations MUST
		 * call closeInterstitial() if returning true from this method.
		 */
		public boolean onCloseButtonClick(MASTAdView adView);
	}

	public interface InternalBrowserListener
	{
		/**
		 * Invoked when the internal browser has been presented to the user.
		 * 
		 * @param adView
		 */
		public void onInternalBrowserPresented(MASTAdView adView);
		
		/**
		 * Invoked when the internal browser has been closed by the user or the SDK.
		 * @param adView
		 */
		public void onInternalBrowserDismissed(MASTAdView adView);
	}
	
	public interface RichMediaListener
	{
		/**
		 * Invoked when a rich media ad expands to the full screen size.
		 * 
		 * @param adView
		 */
		public void onExpanded(MASTAdView adView);
		
		/**
		 * Invoked when a rich media ad is resized larger than it's default/configured size.
		 * 
		 * @param adView
		 * @param area Area of the screen used to render the resized ad.
		 */
		public void onResized(MASTAdView adView, Rect area);
		
		/**
		 * Invoked when a rich media ad collapses from an expanded or resized state.
		 * 
		 * @param adView
		 */
		public void onCollapsed(MASTAdView adView);
		
		/**
		 * Invoked when a rich media ad requests a video to be played.
		 * <p>
		 * If false is returned the URL is handled like any other URL action and ActivityListener.onOpenUrl()
		 * will be invoked for further processing.
		 * 
		 * @param adView
		 * @param url
		 * @return true to indicate the video playing has been handled by the application, false to 
		 * allow the SDK to handle the URL.
		 */
		public boolean onPlayVideo(MASTAdView adView, String url);

		/**
		 * Invoked after a rich media (MRAID) event has occurred.  Since the event has already been handled
		 * applications need not implement any behavior.  However, applications can use this to listen and act
		 * on handled rich media events with other behavior.
		 * 
		 * @param adView
		 * @param request
		 */
		public void onEventProcessed(MASTAdView adView, String request);
	}
	
	/**
	 * Interface allowing application developers to control logging.
	 */
	public interface LogListener
	{
		/**
		 * Invoked when the SDK logs events.  If applications override logging they can return true to
		 * indicate the log event has been consumed and the SDK processing is not needed.
		 * <p>
		 * Will not be invoked if the adView instance's logLevel is set lower than the event.
		 * 
		 * @param adView
		 * @param event String representing the event to be logged.
		 * @param logLevel LogLevel of the event.
		 * @return
		 */
		public boolean onLogEvent(MASTAdView adView, String event, MASTAdView.LogLevel logLevel);
	}
	
	/**
	 * Interface allowing application developers to control which device features are exposed to rich media
	 * ads. By default the SDK considers hardware availability and OS level permissions to determine which
	 * features should be reported as available to rich media ads. Using this interface, the application
	 * can override this process and force features to be reported as available, or not.
	 */
	public interface FeatureSupportHandler
	{
		/**
		 * Should sending SMS messages be reported as a supported feature?
		 * @return Boolean true if this feature should be reported as a supported feature,
		 * Boolean false if it should not, or NULL if the normal SDK hardware/permission check
		 * should be performed. 
		 */
		public Boolean shouldSupportSMS(MASTAdView adView);
		
		/**
		 * Should placing phone calls be reported as a supported feature?
		 * @return Boolean true if this feature should be reported as a supported feature,
		 * Boolean false if it should not, or NULL if the normal SDK hardware/permission check
		 * should be performed.
		 */
		public Boolean shouldSupportPhone(MASTAdView adView);
		
		/**
		 * Should creating calendar entries by reported as a supported feature?
		 * @return Boolean true if this feature should be reported as a supported feature,
		 * Boolean false if it should not, or NULL if the normal SDK hardware/permission check
		 * should be performed.
		 */
		public Boolean shouldSupportCalendar(MASTAdView adView);
		
		/**
		 * Should storing pictures to the camera roll be reported as a supported feature?
		 * @return Boolean true if this feature should be reported as a supported feature,
		 * Boolean false if it should not, or NULL if the normal SDK hardware/permission check
		 * should be performed.
		 */
		public Boolean shouldSupportStorePicture(MASTAdView adView);
		
		/**
		 * Invoked when an ad intends to store a picture to the device camera role. Return boolean
		 * true indicating the user has approved storing the picture, or false otherwise.
		 * NOTE: the application developer is responsible for displaying user dialog, and associated
		 * details such as running UI code on a UI thread if needed.
		 * @param sender The originating ad view where the event was triggered.
		 * @param url String URL of image that will be downloaded and stored, if approved.
		 * @return True to allow picture storage, false otherwise.
		 */
		public boolean shouldStorePicture(MASTAdView sender, String url);
		
		/**
		 * Invoked when an ad intends to create an event in the users' calendar. Return boolean
		 * true indicating the user has approved creating the event, or false otherwise.
		 * NOTE: the application developer is responsible for displaying user dialog, and associated
		 * details such as running UI code on a UI thread if needed.
		 * @param sender The originating ad view where the event was triggered.
		 * @param calendarProperties Complex string describing specifics of the calendar event.
		 * @return True to allow picture storage, false otherwise.
		 */
		public boolean shouldAddCalendarEntry(MASTAdView sender, String calendarProperties);
	}
}
