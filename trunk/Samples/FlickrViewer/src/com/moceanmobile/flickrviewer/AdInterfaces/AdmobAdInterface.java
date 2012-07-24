package com.moceanmobile.flickrviewer.AdInterfaces;


import java.util.HashMap;

import android.app.Activity;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;


public class AdmobAdInterface extends BaseAdInterface implements AdListener
{
	public AdmobAdInterface(Activity activity)
	{
		super(activity);
	}
	
	
	static public String getName()
	{
		return "admob";
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public AdView createBannerAd(Object site, Object data, Object width, Object height, Object location)
	{
		// Data parameter is a hashmap of strings, pull out what we need; the specific
		// contents vary for each third party ad type.
		HashMap<String,String> params = (HashMap<String,String>)data;	
		String publisherId = params.get("publisherid");
		String campaignId = params.get("campaignId");
		setLastCampaign(campaignId);
		
    	return createBannerInternal(width, publisherId);
	}
	
	
	public AdView createBannerInternal(Object width, String publisherId)
	{	
		// Create the adView
    	AdView admobAdView = new AdView(parentActivity, (AdSize)width, publisherId);
    	admobAdView.setAdListener(this);
    
    	// Lookup your LinearLayout assuming it’s been given
    	// the attribute android:id="@+id/mainLayout"
    	//LinearLayout layout = (LinearLayout)findViewById(R.id.mainLayout);
    	
    	// Add the adView to it
    	//layout.addView(adView);
    	
    	// Create and customize ad request; be sure to enable test mode during development
    	// and testing, so that google will not penalize you for clicking on your own ads.
    	AdRequest request = new AdRequest();
    	request.addTestDevice(AdRequest.TEST_EMULATOR);
    	//request.addTestDevice("E83D20734F72FB3108F104ABC0FFC738");    // My T-Mobile G1 test phone - replace this with value from log message
    	
    	// Initiate a generic request to load it with an ad
    	admobAdView.loadAd(request);
    	
    	return admobAdView;
	}
	
	
	//
	// Implementation of the Admob AdListener interface
	//
	
	
	public void onReceiveAd(Ad ad)
	{
		System.out.println("Admob: received ad");
		notifyAdLoadOK();
	}
	
	public void onFailedToReceiveAd(Ad ad, AdRequest.ErrorCode error)
	{
		System.out.println("Admob: receive ad failed.");
		notifyAdLoadFailed();
	}
	
	public void onPresentScreen(Ad ad)
	{
		System.out.println("Admod: present screen");
		notifyAdOpened();
	}
	
	public void onDismissScreen(Ad ad)
	{
		System.out.println("Admod: dissmis screen");
		notifyAdClosed();
	}
	
	public void onLeaveApplication(Ad ad)
	{
		System.out.println("Admod: leave application");
	}
}
