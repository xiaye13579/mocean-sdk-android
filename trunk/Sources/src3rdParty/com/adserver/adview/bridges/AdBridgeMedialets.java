package com.adserver.adview.bridges;

import com.adserver.adview.Utils;
import com.medialets.advertising.AdManager;
import com.medialets.advertising.AdView;
import com.medialets.advertising.BannerAdView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.webkit.WebView;

public class AdBridgeMedialets extends AdBridgeAbstract implements AdManager.ServiceListener, AdView.AdListener {

	public AdBridgeMedialets(Context context, WebView view, String campaignId,
			String externalParams,String trackUrl) {
		super(context, view, campaignId, externalParams, trackUrl);
		
//		AdManager.getInstance().
	}

	@Override
	public void run() {
		try {
			String applicationId = Utils.scrape(externalParams, "<param name=\"id\">", "</param>");
			String appid = Utils.scrape(externalParams, "<param name=\"appid\">", "</param>");

			BannerAdView mBanner = new BannerAdView(context);
			mBanner.prepare(applicationId);
			mBanner.setLayoutParams(view.getLayoutParams());
			//mBanner.setWidth(320);
		    //mBanner.setHeight(50);
		    //mBanner.setSlotName("ViewOneBottom");
			mBanner.setSlotName("ViewOneBottom");
			view.addView(mBanner);
			view.setBackgroundColor(Color.WHITE);
			view.loadDataWithBaseURL(null, "", "text/html", "UTF-8", null);
		} catch (Exception e) {
		}
	}

	public void onServiceConnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAdVisible(AdView arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onFinishedLoadingView(AdView arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInterstitialDismissed(AdView arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNoAdsAvailable(AdView arg0) {
		// TODO Auto-generated method stub
		
	}

}
