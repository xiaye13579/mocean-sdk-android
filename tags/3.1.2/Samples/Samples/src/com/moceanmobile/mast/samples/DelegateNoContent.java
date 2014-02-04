package com.moceanmobile.mast.samples;

import android.os.Bundle;

import com.moceanmobile.mast.MASTAdView;

public class DelegateNoContent extends DelegateLogging
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	
		MASTAdView adView = (MASTAdView) findViewById(R.id.adView);
		adView.setZone(158514);
	}
}
