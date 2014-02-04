package com.moceanmobile.mast.samples;

import android.os.Bundle;

import com.moceanmobile.mast.MASTAdView;
import com.moceanmobile.mast.MASTAdView.LogLevel;

public class DelegateThirdParty extends DelegateGeneric
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Just swap out the zone and disable logging to prevent output spam.
		MASTAdView adView = (MASTAdView) findViewById(R.id.adView);
		adView.setZone(90038);
		adView.setLogLevel(LogLevel.None);
	}
}
