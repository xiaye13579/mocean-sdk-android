package com.adserver.adview.bridges;

import java.util.Hashtable;

import com.adserver.adview.AdLog;
import com.adserver.adview.Utils;
import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMAdViewSDK;
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

	public static boolean IsAvailable()
	{
		return  IsClassExist("com.millennialmedia.android.MMAdView");
	}
	
	public void run() {
		try {
			String applicationId =Utils.scrapeIgnoreCase(externalParams, "<param name=\"id\">", "</param>");
			String adType = Utils.scrapeIgnoreCase(externalParams, "<param name=\"adType\">", "</param>");
			
			//String zip = Utils.scrape(externalParams, "<param name=\"zip\">", "</param>");
			//String longet = Utils.scrape(externalParams, "<param name=\"long\">", "</param>");
			//String lat = Utils.scrape(externalParams, "<param name=\"lat\">", "</param>");
	
			MMAdView adview = new MMAdView((Activity)context, applicationId, adType, MMAdView.REFRESH_INTERVAL_OFF);			
			adview.setId(MMAdViewSDK.DEFAULT_VIEWID);
			adview.setLayoutParams(view.getLayoutParams());
			adview.setListener(new MillennialListener());
			
			// (Optional/Recommended) Set meta data (will be applied to subsequent ad requests)
			Hashtable<String, String> metaData = new Hashtable<String, String>();
			metaData.put("height", "53");
			metaData.put("width", "320");
			adview.setMetaValues(metaData);
			
			view.addView(adview);
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

		public void MMAdRequestIsCaching(MMAdView arg0) {
			
		}		
	}

}
