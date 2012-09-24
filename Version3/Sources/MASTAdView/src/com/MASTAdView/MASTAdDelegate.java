//
// Copyright (C) 2011, 2012 Mocean Mobile. All Rights Reserved. 
//
package com.MASTAdView;

import java.util.HashMap;

/**
 * The delegate class defines interfaces developers can use to extend and/or interact with
 * feature and functions of the SDK ad handling, including:
 * <UL>
 * <LI> Ad download events and errors
 * <LI> MRAID ad events
 * <LI> Ad click events
 * <LI> Ad window attach/expand/close events
 * <LI> Third party ad events
 * <LI> User notification/approval events (for picture storage or calendar creation permission)
 * </UL>
 */
public class MASTAdDelegate
{
	private AdDownloadEventHandler		adDownloadEventHandler 			= null;
	private MraidEventHandler			mraidEventHandler 				= null;
	private AdClickEventHandler			adClickEventHandler 			= null;
	private AdActivityEventHandler		adActivityEventHandler 			= null;
	private ThirdPartyEventHandler		thirdPartyEventHandler 			= null;
	private UserApprovalEventHandler	userApprovalEventHandler 		= null;

	
	/**
	 * Set handler for ad download events.
	 * @param adDownload Object implementing AdDownloadEventHandler interface
	 * @see AdDownloadEventHandler
	 */
	public void setAdDownloadHandler(AdDownloadEventHandler handler)
	{
		adDownloadEventHandler = handler;
	}
	
	
	/**
	 * Get reference to registered ad download event handler.
	 */
	public AdDownloadEventHandler getAdDownloadHandler()
	{
		return adDownloadEventHandler;
	}
	
	
	/**
	 * Setup a listener to be notified when MRAID events occur, as described in the
	 * MRAID event interface.
	 * @param handler Object implementing the MraidEventHandler interface.
	 * @see MraidEventHandler
	 */
	public void setMraidEventHandler(MraidEventHandler handler)
	{
		mraidEventHandler = handler;
	}
	
	
	/**
	 * Get reference to registered MRAID event handler.
	 */
	public MraidEventHandler getMraidEventHandler()
	{
		return mraidEventHandler;
	}
	
	
	/**
	 * Setup handler for ad click, triggered when a URL is being loaded.
	 * @param handler Object implementing the AdClickEventHandler interface.
	 * @see AdClickEventHandler
	 */
	public void setAdClickEventHandler(AdClickEventHandler handler)
	{
		adClickEventHandler = handler;
	}
	
	
	/**
	 * Get reference to registered ad click event handler.
	 */
	public AdClickEventHandler getAdClickEventHandler()
	{
		return adClickEventHandler;
	}
	
	
	/**
	 * Setup handler for ad window activity, as described in the interface definition below.
	 * @param handler Object implementing the AdActivityEventHandler interface.
	 * @see AdActivityEventHandler
	 */
	public void setAdActivityEventHandler(AdActivityEventHandler handler)
	{
		adActivityEventHandler = handler;
	}
	
	
	/**
	 * Get reference to registered ad window activity event handler.
	 */
	public AdActivityEventHandler getAdActivityEventHandler()
	{
		return adActivityEventHandler;
	}
	

	/**
	 * Set custom on third party ad request handler.
	 * @param ThirdPartyEventHandler Object implementing the ThirdPartyEventHandler interface.
	 * @see ThirdPartyEventHandler 
	 */
	public void setThirdPartyRequestHandler(ThirdPartyEventHandler handler)
	{
		thirdPartyEventHandler = handler;
	}
	
	
	/**
	 * Get reference to third party ad request handler.
	 */
	public ThirdPartyEventHandler getThirdPartyRequestHandler()
	{
		return thirdPartyEventHandler;
	}
	
	
	/**
	 * Setup custom handler to deal with user approval events before storing a picture or
	 * creating a calendar event.
	 * @param UserApprovalEventHandler Object implementing the UserApprovalEventHandler interface.
	 * @see UserApprovalEventHandler 
	 */
	public void setUserApprovalRequestHandler(UserApprovalEventHandler handler)
	{
		userApprovalEventHandler = handler;
	}
	
	
	/**
	 * Get reference to registered user approval event handler.
	 */
	public UserApprovalEventHandler getUserApprovalRequestHandler()
	{
		return userApprovalEventHandler;
	}
	
	
	/**
	 * The interface for ad download events. During normal processing begin() and end()
	 * will be invoked. If an error occurs, the error() method will be invoked.
	 * See the sample application source code for example source for this use case.
	 */
	public interface AdDownloadEventHandler
	{
		/**
		 * This event is fired before ad download begins.
		 * @param sender The ad view from which the event originates.
		 */
		public void onDownloadBegin(MASTAdView sender);
		
		/**
		 * This event is fired after ad content has been fully downloaded. It will NOT be invoked
		 * if an error occurs, instead the error() method will be triggered.
		 * @param sender The ad view from which the event originates. 
		 */
		public void onDownloadEnd(MASTAdView sender);
		
		/**
		 * This event is fired in the case of a failure to download content.
		 * @param sender The ad view from which the event originates.
		 * @param error String error message. For example, if the server does not return an ad,
		 * the error method will be invoked and the error string will contain the message defined
		 * in Constants.STR_EMPTY_SERVER_RESPONSE. 
		 */
		public void onDownloadError(MASTAdView sender, String error);
	}


	/**
	 * Interface for MRAID event handler, invoked when MRAID events
	 * such as resize, expand, close, etc. occur.
	 */
	public interface MraidEventHandler 
	{
		/**
		 * The onMraidEvent() method is invoked when MRAID events (such as resize, close, etc.)
		 * as defined in the MRAID standard, occur. The SDK responds to these events as needed,
		 * but the application can perform logging or other application side processing as desired.
		 * @param sender The ad view from which the event originates.
		 * @param name Event name (such as "close").
		 * @param params Any parameters associated with the event.
		 * <P>
		 * A sample which executes a custom method when an MRAID event fires is as follows:
		 * <pre>
		 * class UserOnMraidHandler implements MraidEventHandler {
		 *     public void onMraidEvent(MASTAdView sender, String name, String params) {
		 *         // This class method is responsible for invoking a handler to run
		 *         // code on the UI thread that displays information about the event.
		 *         updateUi("mraid event: " + name + ": " + params);
		 *     }	
		 * }
		 *   
		 * adView.getAdDelegate().setMraidEventHandler(new UserOnMraidHandler());
		 * </pre>
		 * <P>
		 * See the sample application source code for complete source for this use case.
		 */
		public void onMraidEvent(MASTAdView sender, String name, String params);
	}

	
	/**
	 * The interface which will be invoked when loading a URL into the ad view.
	 */
	public interface AdClickEventHandler 
	{
		/**
		 * Invoked when a URL is being loaded in the ad view. See the documentation for the
		 * underlying web view shouldOverrideUrlLoading () method for more information.
		 * @param sender The ad view from which the event originates.
		 * @param url URL to be loaded in ad view.
		 * @return Boolean true if caller has completely handled the click event and wants
		 * to skip the default SDK click processing; return false if the caller has only
		 * implemented a "side-effect" such as logging, and wants the default SDK logic
		 * to continue. 
		 * <P>
		 * Sample code which shows a custom click listener is as follows:
		 * <pre>
		 * class UserOnAdClickListener implements AdClickEventHandler {
		 *     public boolean onClickEvent(MASTAdView arg0, String arg1) {
		 *         // This class method is responsible for invoking a handler to run
		 *         // code on the UI thread that displays information about the event.
		 *         updateUi("Click url = "+ arg1);
		 *         return false; // SDK should continue processing click
		 *     }
		 * }
		 * 
		 * adView.getAdDelegate().setAdClickEventHandler(new UserOnAdClickListener());
		 * </pre>
		 * <P>
		 * See the sample application source code for complete source for this use case.
		 */
		public boolean onClickEvent(MASTAdView sender, String url);
	}
	
	
	/**
	 * Interface which supports custom callback to be invoked when ad view is attached to
	 * or detached from an activity (window), and when a banner ad view "expands" in any
	 * way (MRAID expand, resize, or interstitial display), and closes back again.
	 */
	public interface AdActivityEventHandler 
	{
		/**
		 * This is called when the view is attached to a window. See the underlying web view
		 * onAttachedToWindow documentation for more information.
		 * @param sender The ad view from which the event originated is passed as a parameter.
		 */
		public void onAdAttachedToActivity(MASTAdView sender);
		
		/**
		 * This is called when the view is detached to a window. See the underlying web view
		 * onDetachedToWindow documentation for more information.
		 * @param sender The ad view from which the event originated is passed as a parameter.
		 */
		public void onAdDetachedFromActivity(MASTAdView sender);
		
		
		/**
		 * This is called when a banner ad "epxands" to larger size; the expand can be due to
		 * an MRAID expand or resize, or displaying an interstitial ad display.
		 * @param sender The ad view from which the event originated is passed as a parameter.
		 * @param height Height of "expanded" view.
		 * @param width Width of "expanded" view.
		 */
		public void onAdExpanded(MASTAdView sender, int height, int width);
		
		
		/**
		 * This is called when an expanded view is closed, returning to banner state, or
		 * "gone" for interstitial ads.
		 * @param sender The ad view from which the event originated is passed as a parameter.
		 */
		public void onAdClosed(MASTAdView sender);
	}
	
	
	/**
	 * Interface for third party ad requests, with an onThirdPartyEvent() method invoked when
	 * third party ad requests are being handled.
	 */
	public interface ThirdPartyEventHandler 
	{
		/**
		 * Invoked when third party ad requests are being handled.
		 * @param sender The originating ad view where the event was triggered.
		 * @param params Parameters for the event. The parameters will include
		 * name/value pairs for the "type", "campaign" and "track_url" standard 
		 * items, plus any additional data received in the ad response.
		 * <P>
		 * A sample which executes a custom method when a third party request occurs is as follows:
		 * <pre>
		 * class UserOnThirdPartyRequest implements ThirdPartyEventHandler {
		 *     public void onThirdPartyEvent(MASTAdView arg0, HashMap<String, String> arg1) {
		 *         // This class method is responsible for invoking a handler to run
		 *         // code on the UI thread that displays information about the request.
		 *         updateUi(arg1.toString());
		 *     }	
		 * }
		 *   
		 * adserverView.getAdDelegate().setThirdPartyRequestHandler(new UserOnThirdPartyRequest());
		 * </pre>
		 * <P>
		 * See the sample application source code for complete source for this use case.
		 */
		public void onThirdPartyEvent(MASTAdView sender, HashMap<String,String> params);
	}
	
	
	/**
	 * Interface for handler to deal with user approval events before storing a picture or
	 * creating a calendar event. According to the MRAID specification the application should
	 * prompt the user for approval before storing each picture or creating each calendar event. 
	 */
	public interface UserApprovalEventHandler 
	{
		/**
		 * Invoked when an ad intends to store a picture to the device camera role. Return boolean
		 * true indicating the user has approved storing the picture, or false otherwise.
		 * NOTE: the application developer is responsible for displaying user dialog, and associated
		 * details such as running UI code on a UI thread if needed.
		 * @param sender The originating ad view where the event was triggered.
		 * @param url String URL of image that will be downloaded and stored, if approved.
		 * @return True to allow picture storage, false otherwise.
		 */
		public boolean onStorePictureEvent(MASTAdView sender, String url);
		
		/**
		 * Invoked when an ad intends to create an event in the users' calendar. Return boolean
		 * true indicating the user has approved creating the event, or false otherwise.
		 * NOTE: the application developer is responsible for displaying user dialog, and associated
		 * details such as running UI code on a UI thread if needed.
		 * @param sender The originating ad view where the event was triggered.
		 * @return True to allow picture storage, false otherwise.
		 */
		public boolean onAddCalendarEntryEvent(MASTAdView sender);
	}
}
