package com.moceanmobile.mast;

import java.util.Map;

import android.graphics.Rect;

public interface MASTAdViewDelegate
{
	public interface RequestListener
	{
		public void onFailedToReceiveAd(MASTAdView adView, Exception ex);
		
		public void onReceivedAd(MASTAdView adView);
		
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
		 * @param adView
		 */
		public void onCloseButtonClick(MASTAdView adView);
	}

	public interface InternalBrowserListener
	{
		public void onInternalBrowserPresented(MASTAdView adView);
		public void onInternalBrowserDismissed(MASTAdView adView);
	}
	
	public interface RichMediaListener
	{
		public void onExpanded(MASTAdView adView);
		public void onResized(MASTAdView adView, Rect area);
		public void onCollapsed(MASTAdView adView);
		
		public boolean onPlayVideo(MASTAdView adView, String url);

		public void onEventProcessed(MASTAdView adView, String request);
	}
	
	/**
	 * Interface allowing application developers to control logging.
	 */
	public interface LogListener
	{
		/**
		 * 
		 * @param adView
		 * @param event
		 * @param logLevel
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
