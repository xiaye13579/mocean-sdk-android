package com.moceanmobile.mast.samples;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;

import com.moceanmobile.mast.MASTAdView;
import com.moceanmobile.mast.MASTAdView.LogLevel;
import com.moceanmobile.mast.MASTAdViewDelegate;

public class DelegateLogging extends DelegateGeneric
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		MASTAdView adView = (MASTAdView) findViewById(R.id.adView);
		adView.setZone(88269);
		adView.setLogLevel(LogLevel.Debug);
		
		adView.setLogListener(new AdLogListener());
	}
	
	private class AdLogListener implements MASTAdViewDelegate.LogListener
	{
		@Override
		public boolean onLogEvent(MASTAdView adView, String event, LogLevel logLevel)
		{
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); 
			String date = simpleDateFormat.format(new Date());
			
			appendOutput(date + "\n" + logLevel + "\n" + event);
			
			return true;
		}
	}
}
