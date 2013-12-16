package com.moceanmobile.mast.interstitialswipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.moceanmobile.mast.MASTAdView;
import com.moceanmobile.mast.MASTAdViewDelegate;

public class AdQueue implements MASTAdViewDelegate.RequestListener
{
	static private AdQueue instance = null;
	static public AdQueue getInstance()
	{
		if (instance == null)
			instance = new AdQueue();
		
		return instance;
	}
	
	private List<MASTAdView> availableAds = new ArrayList<MASTAdView>();
	private List<MASTAdView> renderedAds = new ArrayList<MASTAdView>();
	private List<MASTAdView> loadingAds = new ArrayList<MASTAdView>();
	
	synchronized public MASTAdView checkoutAd()
	{
		if (availableAds.size() > 0)
		{
			MASTAdView adView = availableAds.get(0);
			renderedAds.add(adView);
			availableAds.remove(0);
			
			return adView;
		}
		
		return null;
	}
	
	synchronized public void returnAd(MASTAdView adView)
	{
		adView.removeContent();
		adView.setRequestListener(this);
		
		int index = renderedAds.indexOf(adView);
		if (index > -1)
			renderedAds.remove(adView);
		
		loadingAds.add(adView);
		
		adView.update(true);
	}

	@Override
	synchronized public void onFailedToReceiveAd(MASTAdView adView, Exception ex)
	{
		// Ignoring failures, if failed the ad could be put in queue
		// to retry after x amount of time or just given up on.  This
		// can and will happen to all applications due to network 
		// failures so should not be avoided in production apps.
	}

	@Override
	synchronized public void onReceivedAd(MASTAdView adView)
	{
		int index = loadingAds.indexOf(adView);
		if (index > -1)
			loadingAds.remove(index);
		
		availableAds.add(adView);
	}

	@Override
	public void onReceivedThirdPartyRequest(MASTAdView adView,
			Map<String, String> properties, Map<String, String> parameters)
	{
		// Not handled here
	}
}
