package com.moceanmobile.mast.interstitialswipe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.moceanmobile.mast.MASTAdView;
import com.moceanmobile.mast.MASTAdView.LogLevel;

public class MainActivity extends Activity
{
	// How many ad instances to create.
	static private final int MAX_ADS = 2;
	
	// How many data pages.
	static private final int SWIPE_DATA_PAGES = 20;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		createAds();
		
		Button showDataButton = (Button) findViewById(R.id.show_data_button);
		showDataButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(MainActivity.this, DataActivity.class);
				startActivity(intent);
			}
		});
		
		SwipeData swipeData = SwipeData.getInstance();
		for (int i = 0; i < SWIPE_DATA_PAGES; ++i)
		{
			swipeData.addItem(String.valueOf(i));
		}
		swipeData.next();
	}
	
	@SuppressWarnings("deprecation")
	private void createAds()
	{
		int maxAdWidth = getWindowManager().getDefaultDisplay().getWidth();
		int maxAdHeight = getWindowManager().getDefaultDisplay().getHeight();
		
		for (int i = 0; i < MAX_ADS; ++i)
		{
			MASTAdView adView = new MASTAdView(this);
			adView.getAdRequestParameters().put("size_x", String.valueOf(maxAdWidth));
			adView.getAdRequestParameters().put("size_y", String.valueOf(maxAdHeight));
			adView.setLogLevel(LogLevel.Debug);
			adView.setZone(88269);
			
			AdQueue.getInstance().returnAd(adView);
		}
	}
}
