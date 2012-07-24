package com.moceanmobile.flickrviewer.AdInterfaces;

import java.util.Timer;
import java.util.TimerTask;

import com.moceanmobile.flickrviewer.Constants;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.webkit.WebView;

import com.MASTAdView.MASTAdLog;
import com.MASTAdView.MASTAdView;


abstract public class BaseAdInterface
{
	protected Activity parentActivity;
	private String lastCampaign = null;
	private Timer idleTimer = null;
	private TimerTask reloadTask = null;
	
	
	public BaseAdInterface(Activity activity)
	{
		parentActivity = activity;
		idleTimer = new Timer();
	}
	
	// Destroy an ad view object created with this interface
	public void destroy(Object adView)
	{
		if (adView instanceof WebView)
		{
			((WebView)adView).destroy();
		}
	}

	
	public void startIdleTimer()
	{
		try
		{
			if (idleTimer == null)
			{
				idleTimer = new Timer();
			}
			
			if (reloadTask != null)
			{
				reloadTask.cancel();
				reloadTask = null;
			}
			
			reloadTask = new ReloadTask();
			idleTimer.schedule(reloadTask, Constants.banner_ad_update_time * 1000);
		}
		catch(Exception ex)
		{
			System.out.println("Error starting idle timer: " + ex.getMessage());
		}
	}
	
	
	public void stopIdleTimer(boolean remove)
	{
		if(idleTimer != null)
		{
			try
			{
				idleTimer.cancel();
				if (remove)
				{
					idleTimer = null;
				}
			}
			catch (Exception e)
			{
				System.out.println("stopTimer: " + e.getMessage());				
			}
		}	
	}

	
	private class ReloadTask extends TimerTask
	{	
		public ReloadTask()
		{
			// NA
		}

		@Override
		public void run()
		{
			notifyAdTimedOut();
		}
	}
	
	
	// Return the "name" for this ad type, as used with the back-end mobile UI
	static protected String getName()
	{
		return ""; // Override this...
	}
	
	// Create a standard banner ad that will be inserted into a UI view occupying part of a screen;
	// this method must be implemented for each derived class, no exceptions.
	abstract public View createBannerAd(Object site, Object data, Object width, Object height, Object location);
	
	// Create an interstitial ad that will popup and occupy the full screen
	public MASTAdView createInterstitialAd(Object site, Object zone, Object width, Object height)
	{
		return null; // Do nothing by default, we don't use this for all ad types at this time
	}
	
	// Derived classes and/or app should use this to take action on a failure
	public void notifyAdLoadFailed()
	{
		System.out.println("Loading ad failed.");
	}
	
	// Derived classes and/or app should use this to take action on a success
	public void notifyAdLoadOK()
	{
		System.out.println("Ad loaded OK.");
		startIdleTimer();
	}

	// Derived classes and/or app should use this to take action after an ad is opened (clicked)
	public void notifyAdOpened()
	{
		System.out.println("Ad opened.");
		stopIdleTimer(false);
	}
	
	// Derived classes and/or app should use this to take action after an ad closes
	public void notifyAdClosed()
	{
		System.out.println("Ad closed.");
		startIdleTimer();
	}
		
	// Derived classes and/or app should use this to take action after timer expires
	// indicating we want to replace the third party ad with a mocean ad.
	public void notifyAdTimedOut()
	{
		System.out.println("Ad display timeout.");
	}
	
	public String getLastCampaign()
	{
		return lastCampaign;
	}
	
	public void setLastCampaign(String value)
	{
		lastCampaign = value; 
	}
}
