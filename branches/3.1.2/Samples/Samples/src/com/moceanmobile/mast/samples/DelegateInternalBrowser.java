package com.moceanmobile.mast.samples;

import android.os.Bundle;

import com.moceanmobile.mast.MASTAdView;
import com.moceanmobile.mast.MASTAdView.LogLevel;

public class DelegateInternalBrowser extends DelegateGeneric
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		MASTAdView adView = (MASTAdView) findViewById(R.id.adView);
		adView.setZone(88269);
		adView.setLogLevel(LogLevel.Debug);
		adView.setUseInternalBrowser(true);
	}
}
