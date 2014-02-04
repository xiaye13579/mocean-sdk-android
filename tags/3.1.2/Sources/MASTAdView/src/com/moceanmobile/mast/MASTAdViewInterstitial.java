//
// Copyright (C) 2013 Mocean Mobile. All Rights Reserved. 
//
package com.moceanmobile.mast;

import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Helper class to allow placing placing an interstitial ad in an XML layout.  As such this class
 * is only intended to be used in XML layouts and the resulting instance should just be casted
 * to the base MASTAdView class.
 * <p>
 * Note this must be added with a width/height of 0 and a visibility state of INVISIBLE.  Do not
 * attempt to present this view directly as a normal view.
 * <p>
 * When the ad is loaded with a set zone the ad will request an update and display the interstitial
 * after it has been downloaded and rendered.
*/
public class MASTAdViewInterstitial extends MASTAdView implements MASTAdViewDelegate.RequestListener
{
	public MASTAdViewInterstitial(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		super.applyAttributeSet(attrs);
		init(true);
	}
	
	public MASTAdViewInterstitial(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		super.applyAttributeSet(attrs);
		init(true);
	}
	
	@Override
	protected void init(boolean interstitial)
	{
		super.init(interstitial);
		
		setRequestListener(this);
	}

	@Override
	public void onFailedToReceiveAd(MASTAdView adView, Exception ex)
	{

	}

	@Override
	public void onReceivedAd(MASTAdView adView)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				showInterstitial();
			}
		});
	}

	@Override
	public void onReceivedThirdPartyRequest(MASTAdView adView,
			Map<String, String> properties, Map<String, String> parameters)
	{
		
	}
}
