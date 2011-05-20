package com.adserver.adview.bridges;

//import checkpoint.forms.R;

import com.adserver.adview.AdLog;
import com.adserver.adview.Utils;
import com.greystripe.android.sdk.BannerListener;
import com.greystripe.android.sdk.BannerView;
import com.greystripe.android.sdk.GSSDK;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.webkit.WebView;

public class AdBridgeGreyStripe extends AdBridgeAbstract {

	public AdBridgeGreyStripe(Context context, WebView view,AdLog AdLog, String campaignId,
			String externalParams,String trackUrl) {
		super(context, view, AdLog, campaignId, externalParams, trackUrl);
	}

	public void run() {
		try {
			String applicationId = Utils.scrape(externalParams, "<param name=\"id\">", "</param>");			
		    GSSDK.initialize(context, applicationId);
		    
		    BannerView myBanner = new BannerView(context);		    
		    myBanner.setLayoutParams(view.getLayoutParams());
		    myBanner.addListener(new GreyStripeBannerListener());		    
	        view.addView(myBanner);
			myBanner.refresh();
	        myBanner.setOnClickListener(new  View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Click();
				}
			});
		} catch (Exception e) {
			DownloadError(e.getMessage());			
		}
	}
	
	private class GreyStripeBannerListener implements BannerListener {
		public void onReceivedAd(BannerView bannerView) {
			DownloadEnd();
		}

		public void onFailedToReceiveAd(BannerView bannerView) {
			DownloadError("[ERROR] AdBridgeGreyStripe: onFailedToReceiveAd");
		}
	}

}
