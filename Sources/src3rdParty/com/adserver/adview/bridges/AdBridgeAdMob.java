package com.adserver.adview.bridges;

import com.admob.android.ads.AdListener;
import com.admob.android.ads.AdManager;
import com.admob.android.ads.AdView;
import com.admob.android.ads.SimpleAdListener;
import com.admob.android.ads.view.AdMobWebView;
import com.adserver.adview.Utils;

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
			String publisherID = Utils.scrape(externalParams, "<param name=\"publisherid\">", "</param>");
			String size = Utils.scrape(externalParams, "<param name=\"size\">", "</param>");
			String backgroundColor = Utils.scrape(externalParams, "<param name=\"backgroundColor\">", "</param>");
			String primaryTextColor = Utils.scrape(externalParams, "<param name=\"primaryTextColor\">", "</param>");
			String secondaryTextColor = Utils.scrape(externalParams, "<param name=\"secondaryTextColor\">", "</param>");
			String zip = Utils.scrape(externalParams, "<param name=\"zip\">", "</param>");			
			
			//AdManager.setTestDevices( new String[] { "085435FDBACAAE775764A9E27F40E0FB" } );
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
			view.loadDataWithBaseURL(null, "", "text/html", "UTF-8", null);			
		} catch (Exception e) {
		}
	}
	
	private class AdBridgeAdListener extends SimpleAdListener
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
    	
    }

}
