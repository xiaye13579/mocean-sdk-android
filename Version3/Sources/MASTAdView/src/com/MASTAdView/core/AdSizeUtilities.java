//
// Copyright (C) 2011, 2012 Mocean Mobile. All Rights Reserved. 
//
package com.MASTAdView.core;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.MASTAdView.MASTAdConstants;
import com.MASTAdView.MASTAdDelegate;
import com.MASTAdView.MASTAdLog;
import com.MASTAdView.MASTAdView;
import com.MASTAdView.core.AdDialogFactory.DialogOptions;


final public class AdSizeUtilities
{
	final private AdViewContainer 					parentContainer;
	final private MASTAdLog 						adLog;
	volatile private DisplayMetrics					metrics;
	
	volatile private AdWebView 						expandedAdView = null;
	
	final private AdDialogFactory					adDialogFactory;
	final private Context							context;
	
	private AdClickHandler 							adClickHandler = null;
	
	
	public AdSizeUtilities(AdViewContainer adContainer, DisplayMetrics metrics)
	{
		parentContainer = adContainer;
		this.metrics = metrics;
	
		adLog = parentContainer.getLog();
		context = adContainer.getContext();
		
		// Setup ad dialog factory for interstitial/exapnded/open use
		adDialogFactory = new AdDialogFactory(context, parentContainer);
		
		adClickHandler = new AdClickHandler(parentContainer);
	}

	
	public void setMetrics(DisplayMetrics metrics)
	{
		this.metrics = metrics;
	}
	
	
	// Get content from provided URL; NOTE: This runs in the current thread context;
	// never call this from the UI thread!!!
	private StringBuffer fetchUrl(String url)
	{
		StringBuffer responseValue = new StringBuffer();
		
		try
		{
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			get.addHeader("Connection","close");

			
			
			// XXX set user agent???
			
			
			
			HttpResponse response = client.execute(get);
			if(response.getStatusLine().getStatusCode() == 200)
			{
				HttpEntity entity = response.getEntity();
				responseValue.append(EntityUtils.toString(entity, "UTF-8"));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			adLog.log(MASTAdLog.LOG_LEVEL_ERROR, "fetchUrl", e.getMessage());
			responseValue.setLength(0);
		}
	
		return responseValue;
	}
	
	
	private void expandedCallback(int height, int width)
	{
		MASTAdDelegate delegate = parentContainer.getAdDelegate();
		if (delegate != null)
		{
			MASTAdDelegate.AdActivityEventHandler activityHandler = delegate.getAdActivityEventHandler();
			if (activityHandler != null)
			{
				activityHandler.onAdExpanded((MASTAdView)parentContainer, height, width);
			}
		}
	}
	
	
	// Display URL in a (non-MRAID) web view after fetching content using background thread
	synchronized public String openInBackgroundThread(final AdDialogFactory.DialogOptions options, final String url)
	{
		final StringBuffer responseValue = new StringBuffer();
		
		Thread fetchUrl = new Thread()
		{
			public void run()
			{
				responseValue.append(fetchUrl(url));
				if (responseValue.length() > 0)
				{
					// Now that we have the data, get back on UI thread to display it...
					adClickHandler.openUrlForBrowsing(parentContainer.getContext(), url);
					
					/*
					parentContainer.getHandler().post(new Runnable()
					{
						public void run()
						{
							AdWebView v = new AdWebView(parentContainer, adLog, metrics, false); // custom ad web view w/ no MRAID
							v.setVisibility(View.VISIBLE);
							v.loadDataWithBaseURL(null, responseValue.toString(), "text/html", "UTF-8", null);
							
							//expandedCallback(options.height, options.width);
							Dialog dialog = adDialogFactory.createDialog(v, options);
							dialog.show();	
						}
					});
					*/
				}
			}
		};
		fetchUrl.setName("[AdSizeUtilities] openInBackgroundThread");
		fetchUrl.start();
		
		return null;
	}

	
	synchronized public String startExpand(Bundle data, AdReloadTimer timer)
	{
		// Get all expand parameters from data bundle
		Integer toWidth 		   = null;
		Integer toHeight 		   = null;
		Boolean customClose		   = null;
		//Boolean isModal			   = null; // this is read only, always true per spec
		Boolean allowReorientation = null;
		String  forceOrientation   = null;
		String  url				   = null;
		try
		{
			String value = data.getString(MraidInterface.get_EXPAND_PROPERTIES_name(MraidInterface.EXPAND_PROPERTIES.WIDTH));
			if (value != null)
			{
				toWidth = Integer.parseInt(value);
			}
			else
			{
				toWidth = metrics.widthPixels;
			}
			
			value = data.getString(MraidInterface.get_EXPAND_PROPERTIES_name(MraidInterface.EXPAND_PROPERTIES.HEIGHT));
			if (value != null)
			{
				toHeight = Integer.parseInt(value);
			}
			else
			{
				toHeight = metrics.heightPixels;
			}	
			
			value = data.getString(MraidInterface.get_EXPAND_PROPERTIES_name(MraidInterface.EXPAND_PROPERTIES.USE_CUSTOM_CLOSE));
			if ((value != null) && (value.equalsIgnoreCase("true")))
			{
				customClose = true;
			}
			else
			{
				customClose = false; // default
			}
			
			value = data.getString(MraidInterface.get_ORIENTATION_PROPERTIES_name(MraidInterface.ORIENTATION_PROPERTIES.ALLOW_ORIENTATION_CHANGE));
			if ((value != null) && (value.equalsIgnoreCase("false")))
			{
				allowReorientation = false;
			}
			else
			{
				allowReorientation = true; // default
			}
			
			forceOrientation = data.getString(MraidInterface.get_ORIENTATION_PROPERTIES_name(MraidInterface.ORIENTATION_PROPERTIES.FORCE_ORIENTATION));
			if (forceOrientation == null)
			{
				forceOrientation = MraidInterface.get_FORCE_ORIENTATION_PROPERTIES_name(MraidInterface.FORCE_ORIENTATION_PROPERTIES.NONE);
			}
			
			url = data.getString(AdMessageHandler.EXPAND_URL);
		}
		catch(Exception ex)
		{
			return MASTAdConstants.STR_ORMMA_ERROR_EXPAND; // XXX new, more specific error
		}
	
		// Limit expand size to device width/height at most
		if ((toWidth < 0) || (toWidth > metrics.widthPixels))
		{
			toWidth = metrics.widthPixels; 
		}
		if ((toHeight < 0) || (toHeight > metrics.heightPixels))
		{
			toHeight = metrics.heightPixels;
		}
		System.out.println("startExpand: to h/w = " + toHeight + "/" + toWidth);

		// Pass options for dialog through to creator
		AdDialogFactory.DialogOptions options = new AdDialogFactory.DialogOptions();
		options.backgroundColor = Color.BLACK; // XXX setting?
		options.customClose = customClose;
		options.height = toHeight;
		options.width = toWidth;
		
		timer.stopTimer(false); // stop ad refresh timer		
		
		parentContainer.getAdWebView().getMraidInterface().setState(MraidInterface.STATES.EXPANDED);
		
		if ((url == null) || (url.length() < 1) || (url.equalsIgnoreCase("undefined")))
		{
			// We are using existing ad view / content, safe to do this on UI thread
			return expandInUIThread(options, allowReorientation, forceOrientation);
		}
		else
		{
			// Two part creative, need to fetch new data, must use non-UI thread for that;
			// after data available will call back to finish on ui thread via handler.
			return expandInBackgroundThread(options, allowReorientation, forceOrientation, url);
		}	
	}
	
	
	// MRAID expand for one-part creative where we use existing ad web view / content (so all done in UI thread)
	private String expandInUIThread(AdDialogFactory.DialogOptions options, boolean allowReorientation, String forceOrientation)
	{	
		expandedCallback(options.height, options.width);
		
		expandedAdView = null;
		Dialog dialog = adDialogFactory.createDialog(parentContainer.getAdWebView(), options);
		dialog.show();
	
		// Apply orientation options
		handleOrientation(allowReorientation, forceOrientation, context);
		
		return null;
	}

	
	public static void handleOrientation(boolean allowReorientation, String forceOrientation, Context context)
	{
		// Force orientation, if value is "portrait" or "landscape"
		forceOrientationTo(forceOrientation, context);
		
		// Now that orientation is (potentially) set, lock or unlock based on other setting
		if (allowReorientation)
		{
			// This will unlock orientation changes so that device will follow user
			setScreenOrientation(MraidInterface.FORCE_ORIENTATION_PROPERTIES.NONE, context);
		}
		else
		{
			// Lock orientation in current setting; might be redundant after the above,
			// but if no force orientation was set, is still required (and should not be harmful)
			setScreenOrientation(getScreenOrientation(context), context);
		}
	}
	
	
	// MRAID expand for two-part creative; fetch content from URL and display in new (MRAID) ad web view
	private String expandInBackgroundThread(final AdDialogFactory.DialogOptions options, final boolean allowReorientation, final String forceOrientation, final String url)
	{
		/*boolean dontLoad = false;
		if (URL == null || URL.equals("undefined")) {
			URL = getUrl();
			dontLoad = true;
		}*/

		
		System.out.println("Expand: two part creative, next url=" + url);

		final StringBuffer responseValue = new StringBuffer();
		
		Thread fetchUrl = new Thread()
		{
			public void run()
			{
				responseValue.append(fetchUrl(url));
				if (responseValue.length() > 0)
				{
					// Now that we have the data, get back on UI thread to display it...
					parentContainer.getHandler().post(new Runnable()
					{
						public void run()
						{
							expandedAdView = parentContainer.createWebView(context);
							expandedAdView.setVisibility(View.VISIBLE);
							expandedAdView.loadDataWithBaseURL(null, responseValue.toString(), "text/html", "UTF-8", null);
							
							// Base ad view state already set to expanded, but per spec this new ad view should also
							// be set to expanded after it loads.
							expandedAdView.getMraidInterface().setState(MraidInterface.STATES.EXPANDED);
							
							expandedCallback(options.height, options.width);
							
							Dialog dialog = adDialogFactory.createDialog(expandedAdView, options);
							dialog.show();
							
							// Apply orientation options
							handleOrientation(allowReorientation, forceOrientation, context);
						}
					});
				}
			}
		};
		fetchUrl.setName("[AdSizeUtilities] expandInBackgroundThread");
		fetchUrl.start();
		
		return null;
	}

	
	private String resizePropertiesValid(int toWidth, int toHeight, String closePosition, int offsetX, int offsetY, boolean offscreen)
	{
		if ((toWidth > metrics.widthPixels) || (toHeight > metrics.heightPixels))
		{
			return "Resize to larger the screen size not allowed";
		}
		
		if ((toWidth == metrics.widthPixels) && (toHeight == metrics.heightPixels))
		{
			return "Resize may not completely fill the screen";
		}
		
		return null;
	}
	
	
	private RelativeLayout.LayoutParams createCloseLayoutParameters(String closePosition)
	{
		// Mraid spec requires min. 50 pixel height and width for close area
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(50, 50);
		
		switch(parentContainer.getAdWebView().getMraidInterface().get_RESIZE_CUSTOM_CLOSE_POSITION_by_name(closePosition))
		{
		case TOP_LEFT:
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			break;
		case TOP_CENTER:
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			break;
		case BOTTOM_LEFT:
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			break;
		case BOTTOM_RIGHT:
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			break;
		case BOTTOM_CENTER:
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			break;
		default: // top right is the default
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		}
			
		return layoutParams;
	}
	
	
	private RelativeLayout.LayoutParams createResizeAdLayoutParameters(/* View offsetView */ int width, int height)
	{
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		
		return layoutParams;
	}
	
	
	private Button createResizeCloseButton(String where, final AdWebView adWebView)
	{
		// setup close "button" (transparent clickable region positioned based on resize properties)
		Button closeButton = new Button(context);
		closeButton.setText("");
		closeButton.setBackgroundColor(Color.TRANSPARENT);
		closeButton.setLayoutParams(createCloseLayoutParameters(where));
		
		closeButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				System.out.println("resize close click: dismissing...");
				adWebView.injectJavaScript("mraid.close();");
			}
		});
		
		return closeButton;
	}
	
	
	synchronized public String startResize(Bundle data)
	{
		// Get all resize parameters from data bundle
		Integer toWidth 		= null;
		Integer toHeight 		= null;
		String  closePosition 	= null;
		Integer offsetX 		= null;
		Integer offsetY 		= null;
		Boolean offScreen 		= null;
		try
		{
			String value = data.getString(MraidInterface.get_RESIZE_PROPERTIES_name(MraidInterface.RESIZE_PROPERTIES.WIDTH));
			if (value != null)
			{
				toWidth = Integer.parseInt(value);
			}
			else
			{
				return MASTAdConstants.STR_ORMMA_ERROR_RESIZE; // XXX new, more specific error for missing width
			}
			
			value = data.getString(MraidInterface.get_RESIZE_PROPERTIES_name(MraidInterface.RESIZE_PROPERTIES.HEIGHT));
			if (value != null)
			{
				toHeight = Integer.parseInt(value);
			}
			else
			{
				return MASTAdConstants.STR_ORMMA_ERROR_RESIZE; // XXX new, more specific error for missing height
			}	
				
			closePosition = data.getString(MraidInterface.get_RESIZE_PROPERTIES_name(MraidInterface.RESIZE_PROPERTIES.CUSTOM_CLOSE_POSITION));
			if (closePosition == null)
			{
				closePosition = MraidInterface.get_RESIZE_CUSTOM_CLOSE_POSITION_name(MraidInterface.RESIZE_CUSTOM_CLOSE_POSITION.TOP_RIGHT);
			}
			
			value = data.getString(MraidInterface.get_RESIZE_PROPERTIES_name(MraidInterface.RESIZE_PROPERTIES.OFFSET_X));
			if (value != null)
			{
				offsetX = Integer.parseInt(value);
			}
			else
			{
				offsetX = 0;
			}
			
			value = data.getString(MraidInterface.get_RESIZE_PROPERTIES_name(MraidInterface.RESIZE_PROPERTIES.OFFSET_Y));
			if (value != null)
			{
				offsetY = Integer.parseInt(value);
			}
			else
			{
				offsetY = 0;
			}
			
			value = data.getString(MraidInterface.get_RESIZE_PROPERTIES_name(MraidInterface.RESIZE_PROPERTIES.ALLOW_OFF_SCREEN));
			if ((value != null) && (value.equalsIgnoreCase("false")))
			{
				// reposition view if part will go off screen
				offScreen = false;
			}
			else
			{
				// do nothing even if part of view will be off screen (default)
				offScreen = true;
			}
		}
		catch(Exception ex)
		{
			return MASTAdConstants.STR_ORMMA_ERROR_RESIZE; // XXX new, more specific error
		}
	
		String invalidMessage = resizePropertiesValid(toWidth, toHeight, closePosition, offsetX, offsetY, offScreen);
		if (invalidMessage != null)
		{
			System.out.println(invalidMessage);
			return invalidMessage;
		}
		else
		{
			System.out.println("resize validated: h/w = " + toHeight + "/" + toWidth);
		}
		
		return resizeWorker(toWidth, toHeight, closePosition, offsetX, offsetY, offScreen);
	}
	
	
	// Move ad web view to new / larger contain in front of app content, and display
	private String resizeWorker(int toWidth, int toHeight, String closePosition, int offsetX, int offsetY, boolean allowOffScreen)
	{
		AdWebView adWebView = parentContainer.getAdWebView();
		adWebView.setLayoutParams(createResizeAdLayoutParameters(toWidth, toHeight));
		
		// ZZZ this should probably be wrap content and/or use the size from the properties
		// create layout parameters for frame to hold ad (and close button area)
		ViewGroup.MarginLayoutParams frameLp = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.FILL_PARENT, ViewGroup.MarginLayoutParams.FILL_PARENT);
		offsetX += parentContainer.getLeft();
		offsetY += parentContainer.getTop();
		
		if (!allowOffScreen)
		{
			// If needed, reposition to keep within screen width
			// for example: offset = 120, toWdith = 600, screen width = 640
			// in which case offset needs to be reduced by 80
			int delta = (offsetX + toWidth) - metrics.widthPixels; 
			if (delta > 0)
			{
				offsetX = offsetX - delta;
			}
			
			// offset itself might negative, or the math above could have caused it
			// but in either case, make 0 our minimum
			if (offsetX < 0)
			{
				offsetX = 0;
			}
				
			// and height, using same approach
			delta = (offsetY + toHeight) - metrics.heightPixels; 
			if (delta > 0)
			{
				offsetY = offsetY - delta;
			}
			
			if (offsetY < 0)
			{
				offsetY = 0;
			}
		}
		
		frameLp.setMargins(offsetX, offsetY, 0, 0);
		
		// Ad view is already displayed in a resized "overlay", just adjust the size
		ViewGroup parent = (ViewGroup)adWebView.getParent();
		
		if (adWebView.getMraidInterface().getState() == MraidInterface.STATES.RESIZED)
		{
			// ad view already displayed in resized "overlay", simply adjust the size/position
			parent.setLayoutParams(frameLp);
		}
		else
		{
			// Remove ad view from parent (inline with app content)
			parent.removeView(adWebView);;
			
			// Create new "overlay" and move ad view to it
			RelativeLayout adFrame = new RelativeLayout(context);
			adFrame.addView(adWebView);
			
			// Create close button (transparent, as visual indicator is provided by ad)
			// and position based on ad rule
			adFrame.addView(createResizeCloseButton(closePosition, adWebView));
			
			// Finish, display it
			adFrame.setLayoutParams(frameLp);
			((ViewGroup)((Activity) context).getWindow().getDecorView()).addView(adFrame);
			adWebView.requestFocus();
		}
		
		// notify ad using mraid methods per spec.
		adWebView.getMraidInterface().fireSizeChangeEvent(toWidth, toHeight);
		adWebView.getMraidInterface().setState(MraidInterface.STATES.RESIZED);
	
		MASTAdDelegate delegate = parentContainer.getAdDelegate();
		if (delegate != null)
		{
			MASTAdDelegate.AdActivityEventHandler activityHandler = delegate.getAdActivityEventHandler();
			if (activityHandler != null)
			{
				activityHandler.onAdResized((MASTAdView)parentContainer, toHeight, toWidth);
			}
		}
		
		return null;
	}

	
	// Show interstitial ad view
	synchronized public void showInterstitialDialog(int showCloseDelay, int autoCloseDelay)
	{
		expandedCallback(metrics.heightPixels, metrics.widthPixels);
		
		// create dialog object for showing interstitial ad
		DialogOptions options = new DialogOptions();
		options.showCloseDelay = showCloseDelay;
		options.autoCloseDelay = autoCloseDelay;
		
		Dialog dialog = adDialogFactory.createDialog(parentContainer, options);
		dialog.show();
	}

	
	// Dismiss dialog created via open/expand/show
	synchronized public void dismissDialog()
	{
		Dialog d = adDialogFactory.getDialog();
		if (d != null)
		{
			d.dismiss();
			
			MASTAdDelegate delegate = parentContainer.getAdDelegate();
			if (delegate != null)
			{
				MASTAdDelegate.AdActivityEventHandler eventHandler = delegate.getAdActivityEventHandler();
				if (eventHandler != null)
				{
					eventHandler.onAdCollapsed((MASTAdView)parentContainer);
				}
			}
		}	
	}
	
	
	private static void forceOrientationTo(String orientationName, Context context)
	{
		if (orientationName != null)
		{
			if (orientationName.equalsIgnoreCase("portrait"))
			{
				setScreenOrientation(MraidInterface.FORCE_ORIENTATION_PROPERTIES.PORTRAIT, context);
			}
			else if (orientationName.equalsIgnoreCase("landscape"))
			{
				setScreenOrientation(MraidInterface.FORCE_ORIENTATION_PROPERTIES.LANDSCAPE, context);
			}
		}
	}
	
	
	private static MraidInterface.FORCE_ORIENTATION_PROPERTIES getScreenOrientation(Context context)
	{
		switch (context.getResources().getConfiguration().orientation)
		{
		case Configuration.ORIENTATION_PORTRAIT:
		  return MraidInterface.FORCE_ORIENTATION_PROPERTIES.PORTRAIT;
		case Configuration.ORIENTATION_LANDSCAPE:
			return MraidInterface.FORCE_ORIENTATION_PROPERTIES.LANDSCAPE;
		case Configuration.ORIENTATION_SQUARE:
			return MraidInterface.FORCE_ORIENTATION_PROPERTIES.PORTRAIT;
		default:
			return MraidInterface.FORCE_ORIENTATION_PROPERTIES.PORTRAIT;
		}	
	}
	
	
	MraidInterface.FORCE_ORIENTATION_PROPERTIES getScreenOrientationBySize(DisplayMetrics metrics)
	{
		// Simple test by looking at geometry of screen
		if (metrics.widthPixels > metrics.heightPixels)
		{
			return MraidInterface.FORCE_ORIENTATION_PROPERTIES.LANDSCAPE;
		}
		else
		{
			return MraidInterface.FORCE_ORIENTATION_PROPERTIES.PORTRAIT;
		}
	}
	
	
	// set & lock screen orienetation (if portrait or landscape), otherwise unlock (if none)
	private static boolean setScreenOrientation(MraidInterface.FORCE_ORIENTATION_PROPERTIES toOrientation, Context context)
	{
		if (context instanceof Activity)
		{
			if (toOrientation == MraidInterface.FORCE_ORIENTATION_PROPERTIES.LANDSCAPE)
			{
				((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); 
			}
			else if (toOrientation == MraidInterface.FORCE_ORIENTATION_PROPERTIES.PORTRAIT)
			{
				((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
			}
			else
			{
				((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED); 
			}
			
			return true;
		}
		else
		{
			System.out.println("Context is not an activity, cannot manipulate screen orientation");
			return false;
		}
	}
	
	
	synchronized public AdWebView getExpandedAdView()
	{
		return expandedAdView;
	}
	
	
	synchronized public void clearExpandedAdView()
	{
		expandedAdView = null;
	}
	
	
	public static int devicePixelToMraidPoint(int pixelSize, Context context)
	{
		int points = Math.round(pixelSize / context.getResources().getDisplayMetrics().density);
		return points;
	}
	
	
	public static int mraidPointToDevicePixel(int pointSize, Context context)
	{
		int pixels = Math.round(pointSize * context.getResources().getDisplayMetrics().density);
		return pixels;
	}
}
