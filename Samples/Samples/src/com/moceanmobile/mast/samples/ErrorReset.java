package com.moceanmobile.mast.samples;

import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.moceanmobile.mast.MASTAdView;
import com.moceanmobile.mast.MASTAdViewDelegate;

public class ErrorReset extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_error_reset);
		
		MASTAdView adView = (MASTAdView) findViewById(R.id.adView);
		
		adView.setRequestListener(new MASTAdViewDelegate.RequestListener()
		{
			@Override
			public void onFailedToReceiveAd(final MASTAdView adView, Exception ex)
			{
				// Nothing to do here, just let the new ad update itself.
			}

			@Override
			public void onReceivedAd(final MASTAdView adView)
			{
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						// Just expose the background color.
						adView.removeContent();
					}
				});
			}

			@Override
			public void onReceivedThirdPartyRequest(MASTAdView adView,
					Map<String, String> properties,
					Map<String, String> parameters)
			{
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.refresh_menu, menu);
		return true;
	}
}
