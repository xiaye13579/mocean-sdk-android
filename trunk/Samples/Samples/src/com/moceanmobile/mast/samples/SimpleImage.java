package com.moceanmobile.mast.samples;

import android.os.Bundle;

import com.moceanmobile.mast.MASTAdView;

public class SimpleImage extends RefreshActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_image);
		
		MASTAdView adView = (MASTAdView) findViewById(R.id.adView);
		adView.setLocationDetectionEnabled(true);
	}
}
