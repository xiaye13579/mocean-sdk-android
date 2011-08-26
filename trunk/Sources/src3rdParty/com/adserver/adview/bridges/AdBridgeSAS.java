package com.adserver.adview.bridges;

import com.adserver.adview.AdLog;
import com.adserver.adview.Utils;
import com.smartadserver.library.SmartAdServerAd;
import com.smartadserver.library.SmartAdServerAdBannerView;
import com.smartadserver.library.SmartAdServerAdView;
import com.smartadserver.library.SmartAdServerAdBannerView.SmartAdServerAdBannerViewDelegate;
import com.smartadserver.library.SmartAdServerAdView.SmartAdServerAdViewDelegate;

import android.app.Activity;
import android.content.Context;
import android.webkit.WebView;

public class AdBridgeSAS extends AdBridgeAbstract implements SmartAdServerAdViewDelegate, SmartAdServerAdBannerViewDelegate {

	static SmartAdServerAd mAdBanner;
	SmartAdServerAdBannerView mAdBannerView;
	public AdBridgeSAS(Context context, WebView view,AdLog AdLog, String campaignId,
			String externalParams, String trackUrl) {
		super(context, view, AdLog, campaignId, externalParams, trackUrl);
	}
	
	public static boolean IsAvailable()
	{
		return  IsClassExist("com.smartadserver.library.SmartAdServerAd");
	}

	public void run() {
//		if (Build.VERSION.SDK_INT<8)
//		{
//			DownloadError("[ERROR]AdBridgeSAS: SDK version < 8");
//			return;
//		}
		try
		{
			if(mAdBanner==null) 
				mAdBanner = new SmartAdServerAd();
			String siteid = /*"19369";//*/Utils.scrapeIgnoreCase(externalParams, "<param name=\"siteID\">", "</param>");
			String pageid = /*"136527";//*/Utils.scrapeIgnoreCase(externalParams, "<param name=\"pageID\">", "</param>");
			String formatid = /*"5919";//*/Utils.scrapeIgnoreCase(externalParams, "<param name=\"formatID\">", "</param>");
					
			mAdBannerView = new SmartAdServerAdBannerView(context);
			
			mAdBannerView.setDelegate(this);		
			mAdBannerView.setBannerDelegate(this);
			
			mAdBanner.init(mAdBannerView, (Activity)context, siteid, pageid, formatid, "M", "null", null);
//			mAdBanner.init(mAdBannerView, (Activity)context, "19369", "136527", "5919", "M", "null", null);
			
			mAdBannerView.setLayoutParams(view.getLayoutParams());
			view.addView(mAdBannerView);
			//view.removeAllViews();
			//view.addView(mAdBannerView);
					
			
		} catch (Exception e) {
			DownloadError(e.getMessage());
		}
	}

	@Override
	public void onAdDownloadComplete(SmartAdServerAdView arg0) {
		DownloadEnd();		
	}

	@Override
	public void onAdDownloadFailed(SmartAdServerAdView arg0) {
		DownloadError("[ERROR] AdBridgeSAS: onAdDownloadFailed");
	}

	@Override
	public void onAdImageDownloadComplete(SmartAdServerAdView arg0, boolean arg1) {		
//		Log.d("SAS","onAdImageDownloadComplete");
	}

	@Override
	public void onAdImageDownloadFailed(SmartAdServerAdView arg0, boolean arg1) {		
		AdLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "AdBridgeSAS", "onAdImageDownloadFailed");		
	}

	@Override
	public void onClick(SmartAdServerAdView arg0) {
		Click();		
	}

	@Override
	public void onExpand(SmartAdServerAdBannerView arg0) {
//		Log.d("SAS","onExpand");
	}

	@Override
	public void onUnexpand(SmartAdServerAdBannerView arg0) {
//		Log.d("SAS","onUnexpand");
	}

}
