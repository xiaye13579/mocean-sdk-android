package com.moceanmobile.mast;

import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;

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
