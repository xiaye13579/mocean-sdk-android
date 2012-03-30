package com.MASTAdView;

import java.security.SignedObject;
import java.util.Hashtable;
import java.util.Locale;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.PorterDuff.Mode;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.UserDictionary.Words;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Viewer of advertising.
 * Following parametres are defined automatically, if they are equal NULL:
 * latitude - Latitude. 
 * longitude - Longitude.
 * carrier - Carrier name.
 * country - Country of visitor (for example: US). 
 * ua - The browser user agent of the device making the request.
 */
public class MASTAdView extends MASTAdViewCore {
	
	private Integer showCloseButtonTime; 
	private Integer autoCloseInterstitialTime;
	private Boolean isShowPhoneStatusBar;
	private Button closeButton;	
	
		/**
	 * Creation of viewer of advertising.
	 * @param context - The reference to the context of Activity.
	 * @param site - The id of the publisher site.
	 * @param zone - The id of the zone of publisher site.
	 */
	public MASTAdView(Context context, Integer site, Integer zone) {
		super(context, site, zone);
	}
	
	/**
	 * Creation of advanced viewer of advertising. It is used for element creation in a XML template.
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public MASTAdView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Creation of advanced viewer of advertising. It is used for element creation in a XML template.
	 * @param context
	 * @param attrs
	 */
	public MASTAdView(Context context, AttributeSet attrs) {
		super(context, attrs);
		AutoDetectParameters(context);
	}

	/**
	 * Creation of advanced viewer of advertising. It is used for element creation in a XML template.
	 * @param context
	 */
	public MASTAdView(Context context) {
		super(context);
	}
	

	protected MASTAdView(Context context, boolean expanded, MASTAdViewCore expandParent) {
		super(context, expanded, expandParent);
	}
	
	@Override	
	protected void onAttachedToWindow() {
		adLog.log(MASTAdLog.LOG_LEVEL_2, MASTAdLog.LOG_TYPE_INFO, "AttachedToWindow", "");
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		adLog.log(MASTAdLog.LOG_LEVEL_2, MASTAdLog.LOG_TYPE_INFO, "DetachedFromWindow", "");
		
		if(c != null) c = null;
		if(paint != null) paint = null;
		if(matrix != null) matrix = null;
		if(clear != null) clear = null;		
		
		if (image!=null)
		{
			image.recycle();
			image = null;
		}		
		
		super.onDetachedFromWindow();
	}
	
	/*
	 *  Show interstitial advertising.
	 */
	public void show() {
		this.isInterstitial = true;
		openInterstitialForm(getContext(), showCloseButtonTime, 
				autoCloseInterstitialTime, isShowPhoneStatusBar, this, closeButton);
	}
	
	private static void openInterstitialForm(Context context,
			Integer showCloseButtonTime, Integer autoCloseInterstitialTime,
			Boolean isShowPhoneStatusBar, MASTAdView adServerView, Button closeButton) {
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
		
		adServerView.dialog = dialog;
		
		if (adServerView.getParent() != null) {
			((ViewGroup)adServerView.getParent()).removeAllViews();
		}
		
		final RelativeLayout mainLayout = new RelativeLayout(context);
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
		dialog.setOnDismissListener(new Dialog.OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				mainLayout.removeAllViews();
			}
		});		

		dialog.show();
		//adServerView.update();
	}
	
	public void setUA(String ua)
	{
		adserverRequest.setUa(ua);
	}
	
	public String getUA()
	{
		return adserverRequest.getUa();
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
	
	@Override
	void InterstitialClose() {
		if(isInterstitial)			
			handler.post(new Runnable() {
				public void run() {
					if(dialog != null) {
						dialog.dismiss();
					}
				}
			});		
	}
	
	/*@Override
	public void destroy() {
		if (image!=null)
		{
			image.recycle();
			image = null;
		}
		super.destroy();
	}*/
	
	@Override
	void AutoDetectParameters(Context context) {
		super.AutoDetectParameters(context);
		MASTAdViewCore adserverView = this;
		if(adserverRequest != null) {
			AutoDetectParameters autoDetectParameters = AutoDetectParameters.getInstance();
			
			if(adserverRequest.getVersion() == null) {
				if(autoDetectParameters.getVersion() == null) {
					try {
						String version = Constants.SDK_VERSION;
						
						if((version != null) && (version.length() > 0)) {
							adserverRequest.setVersion(version);
							autoDetectParameters.setVersion(version);
						}
						adLog.log(MASTAdLog.LOG_LEVEL_2, MASTAdLog.LOG_TYPE_INFO, "AutoDetectParameters.SDK_VERSION", version);
					} catch (Exception e) {
						adLog.log(MASTAdLog.LOG_LEVEL_1, MASTAdLog.LOG_TYPE_ERROR, "SDK_VERSION", e.getMessage());
					}
				} else {
					adserverRequest.setVersion(autoDetectParameters.getVersion());
					adLog.log(MASTAdLog.LOG_LEVEL_2, MASTAdLog.LOG_TYPE_INFO, "AutoDetectParameters.SDK_VERSION", autoDetectParameters.getVersion());
				}
			}
			
		
			if(adserverRequest.getPremium() == null) {
				adserverRequest.setPremium(2);					
			}
		
			if(adserverRequest.getUa() == null) {
				if(autoDetectParameters.getUa() == null) {
					String userAgent = adserverView.getSettings().getUserAgentString();

					if((userAgent != null) && (userAgent.length() > 0)) {
						adserverRequest.setUa(userAgent);
						autoDetectParameters.setUa(userAgent);
					}
				} else {
					adserverRequest.setUa(autoDetectParameters.getUa());
				}
			}
		}
	}
	
	Bitmap image;
	Canvas c;
	Paint paint;
	Matrix matrix;
	Paint clear;
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		try
		{
			if((getBackgroundColor()==0) &&(image!=null) && (ev!=null)&&
					(ev.getX()>=0) && (ev.getX()<image.getWidth()) &&
					(ev.getY()>=0) && (ev.getY()<image.getHeight())) 
			{
				int  color = image.getPixel((int)ev.getX(), (int)ev.getY());
				if (Color.alpha(color)>0)
				{
					return super.onTouchEvent(ev);
				} return false;
			}else return super.onTouchEvent(ev);
		}catch (Exception e) {
			adLog.log(MASTAdLog.LOG_LEVEL_1,MASTAdLog.LOG_TYPE_ERROR,"onTouchEvent",e.getMessage());
			return true;
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		try
		{
			if((getBackgroundColor()==0)&&(getWidth()>0)&&(getHeight()>0))
			{
				if((image==null)||(image.getWidth() != getWidth())||(image.getHeight() != getHeight())) 
				{
					if (image!=null) image.recycle();
					image = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
					adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "onDraw", "create bmp "+String.valueOf(getWidth())+"x"+String.valueOf(getHeight()));
					c = new Canvas(image);
					paint = new Paint();						
					matrix = new Matrix();
					
					clear = new Paint();
			        clear.setColor(Color.TRANSPARENT);
			        clear.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
				}
				
		        c.drawPaint(clear);
	
				super.onDraw(c);
				canvas.drawBitmap(image, matrix, paint);
			} else super.onDraw(canvas);
		}catch (Exception e) {
			image=null;
			super.onDraw(canvas);
		}
	}
}
