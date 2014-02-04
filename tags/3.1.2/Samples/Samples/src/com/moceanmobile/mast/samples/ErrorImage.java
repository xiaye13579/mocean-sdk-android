package com.moceanmobile.mast.samples;

import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.moceanmobile.mast.MASTAdView;
import com.moceanmobile.mast.MASTAdViewDelegate;

public class ErrorImage extends RefreshActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_error_image);
		
		MASTAdView adView = (MASTAdView) findViewById(R.id.adView);
		final ImageView imageView = (ImageView) findViewById(R.id.adPlaceholder);
		
		adView.setRequestListener(new MASTAdViewDelegate.RequestListener()
		{
			@Override
			public void onFailedToReceiveAd(final MASTAdView adView, Exception ex)
			{
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						adView.setVisibility(View.GONE);
						imageView.setVisibility(View.VISIBLE);
					}
				});
			}

			@Override
			public void onReceivedAd(final MASTAdView adView)
			{
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						adView.setVisibility(View.VISIBLE);
						imageView.setVisibility(View.GONE);
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
}
