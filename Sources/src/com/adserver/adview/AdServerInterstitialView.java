/*© 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.*/
package com.adserver.adview;

import java.util.Hashtable;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Viewer of interstitial advertising.
 */
public class AdServerInterstitialView extends AdServerView {
	private Integer showCloseButtonTime; 
	private Integer autoCloseInterstitialTime;
	private Boolean isShowPhoneStatusBar;
	private Button closeButton;
	
	/**
	 * @deprecated
	 * Creation of viewer of advertising.
	 * @param context - The reference to the context of Activity.
	 * @param minSizeX - The minimum width of advertising.
	 * @param minSizeY - The minimum height of advertising.
	 * @param sizeX - The maximum width of advertising.
	 * @param sizeY - The maximum height of advertising.
	 * @param showCloseButtonTime - The delay after which appears the button "Close". 0 - the button is visible at once.
	 * @param autoCloseInterstitialTime - The delay after which viewer is automatically closed. 0 - viewer is not closed automatically.
	 * @param isShowPhoneStatusBar - Whether to show Phone Status Bar or not.
	 * @param isInternalBrowser - The flag which operates advertising opening. False - Ad opens in an external browser. True - Ad opening at the help of a adClickListener.
	 * @param adClickListener - The interface for advertising opening.
	 * @param defaultImage - The identifier of the resource, which will be shown during advertising loading.
	 * @param adReloadPeriod - The period of an automatic reload of advertising (in milliseconds).
	 * @param visibleMode - Mode of loading and refreshing of Ad (use VISIBLE_MODE_CASE1, VISIBLE_MODE_CASE2, VISIBLE_MODE_CASE3).
	 * @param appId - The id of the application.
	 * @param campaign - campaign.
	 * @param mode - Mode of viewer of advertising (use MODE_COUNTER_ONLY, MODE_ADS_ONLY, MODE_COUNTER_AND_ADS).
	 * @param site - The id of the publisher site.
	 * @param zone - The id of the zone of publisher site.
	 * @param ip - The IP address of the carrier gateway over which the device is connecting.
	 * @param keywords - Keywords to search ad delimited by commas.
	 * @param adstype - Type of advertisement (ADS_TYPE_TEXT_ONLY - text only, ADS_TYPE_IMAGES_ONLY - image only, ADS_TYPE_TEXT_AND_IMAGES - image and text, ADS_TYPE_SMS - SMS ad). SMS will be returned in XML.
	 * @param over18 - Filter by ad over 18 content (OVER_18_TYPE_DENY - deny over 18 content , OVER_18_TYPE_ONLY - only over 18 content, OVER_18_TYPE_ALL - allow all ads including over 18 content).
	 * @param latitude - Latitude. 
	 * @param longitude - Longitude.
	 * @param ua - The browser user agent of the device making the request.
	 * @param premium - Filter by premium (PREMIUM_STATUS_NON_PREMIUM - non-premium, PREMIUM_STATUS_PREMIUM - premium only, PREMIUM_STATUS_BOTH - both). Can be used only by premium publishers.
	 * @param isTestModeEnabled - Setting is test mode where, if the ad code is true, the ad response is "Test MODE". 
	 * @param count - Quantity of ads, returned by a server. Maximum value is 5. 
	 * @param country - Country of visitor (for example: US). 
	 * @param region - Region of visitor (for example: NY). 
	 * @param isTextborderEnabled - Show borders around text ads (false - non-borders, true - show borders). 
	 * @param paramBorder - Borders color (for example: #000000).
	 * @param paramBG - Background color in borders (for example: #ffffff).
	 * @param paramLINK - Text color (for example: #ffffff).
	 * @param carrier - Carrier name.
	 * @param target - Target attribute for HTML link element (TARGET_BLANK - open the linked document in a new window, TARGET_SELF - open the linked document in the same frame, TARGET_PARENT - open the linked document in the parent frameset, TARGET_TOP - open the linked document in the full body of the window). 
	 * @param url - URL of site for which it is necessary to receive advertising. 
	 * @param customParameters - Custom parameters.
	 */
	public static void show(Context context, 
			Integer minSizeX, Integer minSizeY, Integer sizeX, Integer sizeY, 
			Integer showCloseButtonTime, Integer autoCloseInterstitialTime,
			Boolean isShowPhoneStatusBar, 
			boolean isInternalBrowser, OnAdClickListener adClickListener,
			int defaultImage, Long adReloadPeriod, Integer visibleMode,
			String appId, String campaign, Integer mode, String site,
			String zone, String ip, String keywords, Integer adstype,
			Integer over18, String latitude, String longitude, String ua,
			Integer premium, Boolean isTestModeEnabled,
			Integer count, String country, String region,
			Boolean isTextborderEnabled, String paramBorder, String paramBG,
			String paramLINK, String carrier, String target,
			String url, Hashtable<String, String> customParameters) 
	{
		AdServerView adServerView = new AdServerView(context, 
				minSizeX, minSizeY, sizeX, sizeY, 
				isInternalBrowser, adClickListener, defaultImage,
				adReloadPeriod, visibleMode, appId, campaign, mode, site, zone, ip,
				keywords, adstype, over18, latitude, longitude, ua, premium, 
				isTestModeEnabled, count, country, region, isTextborderEnabled,
				paramBorder, paramBG, paramLINK, carrier, target, url,
				customParameters);

		openInterstitialForm(context, showCloseButtonTime,
				autoCloseInterstitialTime, isShowPhoneStatusBar, adServerView, null);
	}

	public AdServerInterstitialView(Context context, String site, String zone) {
		super(context, site, zone);
	}

	/*
	 *  Show interstitial advertising.
	 */
	public void show() {
		openInterstitialForm(getContext(), showCloseButtonTime, 
				autoCloseInterstitialTime, isShowPhoneStatusBar, this, closeButton);
	}
	
	private static void openInterstitialForm(Context context,
			Integer showCloseButtonTime, Integer autoCloseInterstitialTime,
			Boolean isShowPhoneStatusBar, AdServerView adServerView, Button closeButton) {
		if((showCloseButtonTime == null) || (showCloseButtonTime < 0)) {
			showCloseButtonTime = 0;
		}
		if((autoCloseInterstitialTime == null) || (autoCloseInterstitialTime < 0)) {
			autoCloseInterstitialTime = 0;
		}
		if(isShowPhoneStatusBar == null) {
			isShowPhoneStatusBar = true;
		} 

		//show dialog
		final Dialog dialog;
		
		if(isShowPhoneStatusBar) {
			dialog = new Dialog(context, android.R.style.Theme_NoTitleBar);
		} else {
			dialog = new Dialog(context, android.R.style.Theme_NoTitleBar_Fullscreen);
		}
		
		dialog.setCancelable(false);
		
		if (adServerView.getParent() != null) {
			((ViewGroup)adServerView.getParent()).removeAllViews();
		}
		
		RelativeLayout mainLayout = new RelativeLayout(context);
		mainLayout.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
		adServerView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
		mainLayout.addView(adServerView);
		
		if(closeButton == null) {
			closeButton = new Button(context);
			closeButton.setText("Close");
			RelativeLayout.LayoutParams closeLayoutParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			closeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			closeLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			closeButton.setLayoutParams(closeLayoutParams);
		}
		closeButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
		mainLayout.addView(closeButton);

		Handler handler = new Handler();
		
		if(showCloseButtonTime <= 0) {
			closeButton.setVisibility(View.VISIBLE);
		} else {
			closeButton.setVisibility(View.INVISIBLE);
			ShowCloseButtonThread showButtonThread = new ShowCloseButtonThread(handler, closeButton, showCloseButtonTime);
			showButtonThread.start();
		}
		
		if(autoCloseInterstitialTime > 0) {
			CloseDialogThread closeDialogThread = new CloseDialogThread(handler, dialog, autoCloseInterstitialTime);
			closeDialogThread.start();
		}
		
		dialog.setContentView(mainLayout);
		dialog.show();
	}

	/**
	 * Get show close button after delay.
	 * @return
	 */
	public Integer getShowCloseButtonTime() {
		return showCloseButtonTime;
	}

	/**
	 * Set show close button after delay.
	 * @param showCloseButtonTime
	 */
	public void setShowCloseButtonTime(Integer showCloseButtonTime) {
		this.showCloseButtonTime = showCloseButtonTime;
	}

	/**
	 * get auto-close interstitial time.
	 * @return
	 */
	public Integer getAutoCloseInterstitialTime() {
		return autoCloseInterstitialTime;
	}

	/**
	 * Set auto-close interstitial time.
	 * @param autoCloseInterstitialTime
	 */
	public void setAutoCloseInterstitialTime(Integer autoCloseInterstitialTime) {
		this.autoCloseInterstitialTime = autoCloseInterstitialTime;
	}

	/**
	 * Get whether to show Phone Status Bar or not.
	 * @return
	 */
	public Boolean getIsShowPhoneStatusBar() {
		return isShowPhoneStatusBar;
	}

	/**
	 * Set whether to show Phone Status Bar or not.
	 * @param isShowPhoneStatusBar
	 */
	public void setIsShowPhoneStatusBar(Boolean isShowPhoneStatusBar) {
		this.isShowPhoneStatusBar = isShowPhoneStatusBar;
	}

	/**
	 * Get Object for customization close button view.
	 * @return
	 */
	public Button getCloseButton() {
		return closeButton;
	}

	/**
	 * Set Object for customization close button view.
	 * @param closeButton
	 */
	public void setCloseButton(Button closeButton) {
		this.closeButton = closeButton;
	}
	
}
