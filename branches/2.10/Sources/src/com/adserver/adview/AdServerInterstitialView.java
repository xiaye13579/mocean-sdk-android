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
public class AdServerInterstitialView extends MASTAdServerView {
	private Integer showCloseButtonTime; 
	private Integer autoCloseInterstitialTime;
	private Boolean isShowPhoneStatusBar;
	private Button closeButton;	
	
	public AdServerInterstitialView(Context context, Integer site, Integer zone) {
		super(context, site, zone);
	}
	public AdServerInterstitialView(Context context) {
		super(context);
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
			Boolean isShowPhoneStatusBar, MASTAdServerView adServerView, Button closeButton) {
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
		
		((AdServerInterstitialView)adServerView).dialog = dialog;
		
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
			dialog.setCancelable(false);
			ShowCloseButtonThread showButtonThread = new ShowCloseButtonThread(handler,dialog, closeButton, showCloseButtonTime);
			showButtonThread.start();
		}
		
		if(autoCloseInterstitialTime > 0) {						
			CloseDialogThread closeDialogThread = new CloseDialogThread(handler, dialog, autoCloseInterstitialTime);
			closeDialogThread.start();
		}
		
		dialog.setContentView(mainLayout);
		dialog.show();
		adServerView.update();
	}
	
	@Override
	public boolean isInterstitial() {
		return true;
	}
	
	@Override
	void InterstitialClose() {
		handler.post(new Runnable() {
			public void run() {
				if(dialog != null) {
					dialog.dismiss();
				}
			}
		});		
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
