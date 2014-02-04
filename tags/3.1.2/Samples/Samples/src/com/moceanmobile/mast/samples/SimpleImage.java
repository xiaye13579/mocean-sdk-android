package com.moceanmobile.mast.samples;

import android.os.Bundle;

import com.moceanmobile.mast.MASTAdView;

public class SimpleImage extends RefreshActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_image);
		
		MASTAdView adView = (MASTAdView) findViewById(R.id.adView);
		adView.setLocationDetectionEnabled(true);
	}
	
	@Override
	protected void onDestroy()
	{
		// Need to disable location detection or reset the ad view to properly
		// cleanup resources associated with location detection support.
		MASTAdView adView = (MASTAdView) findViewById(R.id.adView);
		adView.reset();
		
		super.onDestroy();
	}
}
