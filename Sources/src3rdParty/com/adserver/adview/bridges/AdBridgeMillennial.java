package com.adserver.adview.bridges;

import com.adserver.adview.AdLog;
import com.adserver.adview.Utils;
import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMAdView.MMAdListener;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.webkit.WebView;

public class AdBridgeMillennial extends AdBridgeAbstract {

	public AdBridgeMillennial(Context context, WebView view,AdLog AdLog, String campaignId,
			String externalParams,String trackUrl) 
	{
		super(context, view, AdLog, campaignId, externalParams, trackUrl);
	}

	public void run() {
		try {
			String applicationId = Utils.scrape(externalParams, "<param name=\"id\">", "</param>");
			String adType = Utils.scrape(externalParams, "<param name=\"adType\">", "</param>");
			//String zip = Utils.scrape(externalParams, "<param name=\"zip\">", "</param>");
			//String longet = Utils.scrape(externalParams, "<param name=\"long\">", "</param>");
			//String lat = Utils.scrape(externalParams, "<param name=\"lat\">", "</param>");
	
			MMAdView adview = new MMAdView((Activity)context, applicationId, adType, MMAdView.REFRESH_INTERVAL_OFF);
			adview.setLayoutParams(view.getLayoutParams());
			adview.setListener(new MillennialListener());
			view.addView(adview);
			//view.setBackgroundColor(Color.WHITE);
			//view.loadDataWithBaseURL(null, "", "text/html", "UTF-8", null);
			adview.callForAd();
		} catch (Exception e) {
			DownloadError(e.getMessage());
		}
	}
	
	private class MillennialListener implements MMAdListener 	
	{
		public void MMAdFailed(MMAdView adview)
		{
			//Log.i("SampleApp", "Millennial Ad View Failed" );
			//DownloadError(context.getString(R.string.millennial_download_error));
			DownloadError("[ERROR] AdBridgeMillennial: MMAdFailed");
		}

		public void MMAdReturned(MMAdView adview)
		{
			//Log.i("SampleApp", "Millennial Ad View Success" );
			DownloadEnd();
		}
		
		public void MMAdClickedToNewBrowser(MMAdView adview)
		{
			//Log.i("SampleApp", "Millennial Ad clicked, new browser launched" );	
			Click();
		}
		
		public void MMAdClickedToOverlay(MMAdView adview)
		{
			//Log.i("SampleApp", "Millennial Ad Clicked to overlay" );
			Click();
		}
		
		public void MMAdOverlayLaunched(MMAdView adview)
		{
			//Log.i("SampleApp", "Millennial Ad Overlay Launched" );
		}		
	}

}
