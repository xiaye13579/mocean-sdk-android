package com.adserver.adview.bridges;

import com.adserver.adview.AdLog;
import com.adserver.adview.Utils;
import com.medialets.advertising.AdActivity;
import com.medialets.advertising.AdManager;
import com.medialets.advertising.AdView;
import com.medialets.advertising.BannerAdView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class AdBridgeMedialets extends AdBridgeAbstract implements AdManager.ServiceListener, AdView.AdListener {
	static BannerAdView mBanner;
	static MyLinearLayout ll;
	String applicationId;
	
	public AdBridgeMedialets(Context context, WebView view,AdLog AdLog, String campaignId,
			String externalParams,String trackUrl) {
		super(context, view, AdLog, campaignId, externalParams, trackUrl);
	}

	 class MyLinearLayout extends LinearLayout
	 {

		public MyLinearLayout(Context context) {
			super(context);			
		}
		
		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			if(ev.getAction()==MotionEvent.ACTION_DOWN)	Click();
			return super.onInterceptTouchEvent(ev);
		}
	 }
	
	@Override
	public void run() {
		try
		{
			if(!AdActivity.class.isInstance(context))
			{
				DownloadError("[ERROR] It is required to use AdActivity or it's successor to display Medialets");
				return;
			}
			
			if (mBanner==null)
			{
				mBanner = new BannerAdView(context);
			}else
				ll.removeAllViews();
			
			applicationId = Utils.scrape(externalParams, "<param name=\"id\">", "</param>");
			
			mBanner.setLayoutParams(view.getLayoutParams());
			mBanner.setWidth(320);
		    mBanner.setHeight(50);
		    mBanner.setSlotName("ViewOneBottom");
			mBanner.setAdListener(this);
			
			ll =new MyLinearLayout(context);
			ll.setLayoutParams(view.getLayoutParams());
			view.addView(ll);
			ll.addView(mBanner);
			
			try
			{
				mBanner.prepare(); // !!!!
			}catch(Exception e)
			{
				AdManager.getInstance().setServiceListener(this);				
			}
		} catch (Exception e) {
			DownloadError(e.getMessage());
		}
	}

	public void onServiceConnected() {
		mBanner.prepare(applicationId);
		
	}

	@Override
	public void onAdVisible(AdView arg0) {
		DownloadEnd();
	}

	@Override
	public void onFinishedLoadingView(AdView arg0) {
		
	}

	@Override
	public void onInterstitialDismissed(AdView arg0) {
		DownloadError("[ERROR] AdBridgeMedialets: onInterstitialDismissed");
		
	}

	@Override
	public void onNoAdsAvailable(AdView arg0) {		
		DownloadError("[ERROR] AdBridgeMedialets: onNoAdsAvailable");
	}

}
