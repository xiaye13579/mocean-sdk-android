package com.adserver.adview.bridges;

import com.adserver.adview.Utils;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.AdRequest.ErrorCode;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

public class AdBridgeAdMob extends AdBridgeAbstract {

	public AdBridgeAdMob(Context context, WebView view, String campaignId,
			String externalParams, String trackUrl) {
		super(context, view, campaignId, externalParams, trackUrl);
	}

	@Override
	public void run() {
		try {
			String adUnitId = Utils.scrape(externalParams, "<param name=\"publisherid\">", "</param>");
			/*String size = Utils.scrape(externalParams, "<param name=\"size\">", "</param>");
			String backgroundColor = Utils.scrape(externalParams, "<param name=\"backgroundColor\">", "</param>");
			String primaryTextColor = Utils.scrape(externalParams, "<param name=\"primaryTextColor\">", "</param>");
			String secondaryTextColor = Utils.scrape(externalParams, "<param name=\"secondaryTextColor\">", "</param>");
			String zip = Utils.scrape(externalParams, "<param name=\"zip\">", "</param>");	*/		
			
			//AdSize adSize = new AdSize(view.getWidth(), view.getHeight());
			AdSize adSize = new AdSize(320,50);
			AdView adView = new AdView((Activity)context, adSize, adUnitId);
			
			
			adView.setLayoutParams(view.getLayoutParams());
			adView.setAdListener(new AdBridgeAdListener());
			adView.setOnClickListener(new OnClickListener() {			
				public void onClick(View v) {
					Click();
				}
			});   
			
			
			AdRequest adReq = new AdRequest();			
			//adReq.setTesting(true);
			adView.loadAd(adReq);
			
			view.addView(adView);
			//view.setBackgroundColor(Color.WHITE);
			//view.loadDataWithBaseURL(null, "", "text/html", "UTF-8", null);
			
			/*//AdManager.setTestDevices( new String[] { "085435FDBACAAE775764A9E27F40E0FB" } );
			AdManager.setPublisherId(publisherID);			
			
			AdView adView = new AdView((Activity)context);
			
			adView.setRequestInterval(0);
			
			if (zip.length()>0) AdManager.setPostalCode(zip);
			if (primaryTextColor.length()>0) adView.setPrimaryTextColor(Integer.parseInt(primaryTextColor));
			if (secondaryTextColor.length()>0) adView.setSecondaryTextColor(Integer.parseInt(secondaryTextColor));
			if (backgroundColor.length()>0) adView.setBackgroundColor(Integer.parseInt(backgroundColor));
			if (backgroundColor.length()>0) adView.setBackgroundColor(Integer.parseInt(backgroundColor));
			//adView.setKeywords("0");
			
			adView.setLayoutParams(view.getLayoutParams());
			adView.setAdListener(new AdBridgeAdListener());
			view.addView(adView);
			adView.setOnClickListener(new OnClickListener() {			
				public void onClick(View v) {
					Click();
				}
			});
			view.setBackgroundColor(Color.WHITE);
			view.loadDataWithBaseURL(null, "", "text/html", "UTF-8", null);	*/		
		} catch (Exception e) {
			DownloadError(e.getMessage());
		}
	}
	
	private class AdBridgeAdListener implements AdListener
	{

		@Override
		public void onDismissScreen(Ad arg0) {
			
		}

		@Override
		public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
			DownloadError("[ERROR] AdBridgeAdMob: onFailedToReceiveAd");
		}

		@Override
		public void onLeaveApplication(Ad arg0) {
			
		}

		@Override
		public void onPresentScreen(Ad arg0) {
			
		}

		@Override
		public void onReceiveAd(Ad arg0) {
			DownloadEnd();			
		}
		
	}
	
	/*private class AdBridgeAdListener extends SimpleAdListener
    {
		
		
		public void onFailedToReceiveAd(AdView adView)
		{
			super.onFailedToReceiveAd(adView);
			Failed();
			//DownloadError(context.getString(R.string.admob_download_error));
		}

		public void onFailedToReceiveRefreshedAd(AdView adView)
		{
			super.onFailedToReceiveRefreshedAd(adView);
		}

		public void onReceiveAd(AdView adView)
		{
			super.onReceiveAd(adView);
			//Click();
		}

		public void onReceiveRefreshedAd(AdView adView)
		{
			super.onReceiveRefreshedAd(adView);
		}
    	
    }*/

}
