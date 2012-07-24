package com.moceanmobile.flickrviewer.AdInterfaces;

import java.util.HashMap;
import java.util.Hashtable;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMAdViewSDK;
import com.millennialmedia.android.MMAdView.MMAdListener;
import com.moceanmobile.flickrviewer.Constants;


public class MillenialAdInterface extends BaseAdInterface implements MMAdListener
{

	public MillenialAdInterface(Activity activity)
	{
		super(activity);
	}

	
	static public String getName()
	{
		return "Millennial";
	}
	

	@Override
	@SuppressWarnings("unchecked")
	public View createBannerAd(Object site, Object data, Object width, Object height, Object location)
	{
		// Data parameter is a hashmap of strings, pull out what we need; the specific
		// contents vary for each third party ad type.
		HashMap<String,String> params = (HashMap<String,String>)data;	
		String publisherId = params.get("id");
		String campaignId = params.get("campaignId");
		String zip = params.get("zip");
		String adType = params.get("adType");

		setLastCampaign(campaignId);

		Hashtable<String, String> map = new Hashtable<String, String>();
	    map.put("zip", zip);
	   	    
		return createBannerInternal(publisherId, (String)location, map);
	}
	
	private MMAdView createBannerInternal(String appId, String adType, Hashtable<String, String> params) 
	{
	    MMAdView adView = new MMAdView(parentActivity, appId, adType, Constants.millenial_ad_refresh, params);
	    adView.setId(MMAdViewSDK.DEFAULT_VIEWID);
	    
	    // Setup ad listener to be notified of events (here)
	    adView.setListener(this);
	    
	    // Use full width, minimal height
	    LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
	    adView.setLayoutParams(lp);
	    
		return adView;
		
	}
	
	
	//
	// Implementation of the Millenial MMAdListener interface
	//
	
	
	public void MMAdFailed(MMAdView adview)
	{
		System.out.println("Millenial: ad failed.");
		notifyAdLoadFailed();		
	}

	public void MMAdReturned(MMAdView adview)
	{
		System.out.println("Millenial: received ad");
		notifyAdLoadOK();
	}
	
	public void MMAdClickedToNewBrowser(MMAdView adview)        
	{
		System.out.println("Millenial: clicked - new browser");
	}
	
	public void MMAdClickedToOverlay(MMAdView adview)   
	{
		System.out.println("Millenial: clicked - overlay");
	}
	
	public void MMAdOverlayLaunched(MMAdView adview)
	{
		System.out.println("Millenial: overlay launched");	
	}
	
	public void MMAdRequestIsCaching(MMAdView adview)
	{
		System.out.println("Millenial: caching");
	}
	
	public void MMAdCachingCompleted(MMAdView adview, boolean success)
	{
		System.out.println("Millenial: caching complete");	
	}
}
