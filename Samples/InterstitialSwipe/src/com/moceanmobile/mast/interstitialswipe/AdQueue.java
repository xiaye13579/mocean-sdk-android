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
