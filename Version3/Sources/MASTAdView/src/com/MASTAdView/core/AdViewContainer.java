//
// Copyright (C) 2011, 2012 Mocean Mobile. All Rights Reserved. 
//
package com.MASTAdView.core;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.NameValuePair;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.MASTAdView.MASTAdConstants;
import com.MASTAdView.MASTAdDelegate;
import com.MASTAdView.MASTAdLog;
import com.MASTAdView.MASTAdRequest;
import com.MASTAdView.MASTAdView;


public class AdViewContainer extends RelativeLayout implements ContentManager.ContentConsumer
{
	private Context 								context;
	
	private TextView								adTextView;
	private ImageView								adImageView;
	private AdWebView								adWebView;
	
	private Button									bannerCloseButton = null;

	//private Bitmap 									defaultImage = null;
	private int 									defaltBackgroundColor = Color.TRANSPARENT;
	private int										defaultTextColor = Color.BLACK;

	private boolean									isShowCloseOnBanner = false;
	private boolean									isShowPreviousAdOnError = false;
	
	private AdSizeUtilities							adSizeUtilities;
	
	private MASTAdLog 								adLog = new MASTAdLog(this);

	protected MASTAdRequest							adserverRequest;
	private String 									lastRequest;
	private AdData 									lastResponse;
	
	private DisplayMetrics							metrics = null;
	
	protected MASTAdDelegate						adDelegate;
	
	protected AdReloadTimer							adReloadTimer;
	
	private int 									requestCounter = 0;
	
	private Handler 								handler;
	
	private OrientationChangeListener				orientationChangeListener = null;
	private AdLocationListener						locationListener = null;
	
	// Local notion of placement type, partically duplicating the mraid interface value, needed for non-mraid ads
	protected MraidInterface.PLACEMENT_TYPES		adPlacementType = MraidInterface.PLACEMENT_TYPES.INLINE;
	
	// Reference to self
	private AdViewContainer							self;
	
	// Use internal browser or standalone browser?
	//private boolean 								internalBrowser = false;
	
	
	//
	// Constructors
	//
	
	
	public AdViewContainer(Context context, Integer site, Integer zone)
	{
		super(context); // NOTE: needs to be an activity for orientation changes to work
		
		initialize(context);
		
		adserverRequest.setProperty(MASTAdRequest.parameter_site, site);
		adserverRequest.setProperty(MASTAdRequest.parameter_zone, zone);
	}
	
	
	public AdViewContainer(Context context, Integer site, Integer zone, boolean isInterstitial)
	{
		super(context); // NOTE: needs to be an activity for orientation changes to work
		
		initialize(context);
		
		adserverRequest.setProperty(MASTAdRequest.parameter_site, site);
		adserverRequest.setProperty(MASTAdRequest.parameter_zone, zone);
	
		adPlacementType = MraidInterface.PLACEMENT_TYPES.INLINE;
		if (isInterstitial)
		{
			adPlacementType = MraidInterface.PLACEMENT_TYPES.INTERSTITIAL;
		}
		adWebView.getMraidInterface().setPlacementType(adPlacementType);
	}
	
	
	public AdViewContainer(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle); // NOTE: needs to be an activity for orientation changes to work
		
		initialize(context);
		
		LayoutAttributeHandler layoutHandler = new LayoutAttributeHandler(context, this);
		layoutHandler.processAttributes(attrs);
	}

	
	public AdViewContainer(Context context, AttributeSet attrs)
	{
		super(context, attrs); // NOTE: needs to be an activity for orientation changes to work
		
		initialize(context);
		
		LayoutAttributeHandler layoutHandler = new LayoutAttributeHandler(context, this);
		layoutHandler.processAttributes(attrs);
	}

	
	public AdViewContainer(Context context)
	{
		super(context); // NOTE: needs to be an activity for orientation changes to work
		
		initialize(context);
	}
	
	
	//
	// Initialization/view creation
	//
	
	
	private void initialize(Context c)
	{
		context = c;
		if (adserverRequest == null) adserverRequest = new MASTAdRequest(adLog, context);
		
		self = this; // save reference to the container
		
		// Setup handler for inter-thread communication/method invoation
		handler = new AdMessageHandler(this);
		
		WindowManager windowManager = (WindowManager) ((Activity)context).getSystemService(Context.WINDOW_SERVICE);
			
		// listen for orientation changes, and handle them for eveyr add view
		orientationChangeListener = OrientationChangeListener.getInstance(context, windowManager.getDefaultDisplay());
		orientationChangeListener.addView(this);
		
		// Save original screen dimensions for later use
		metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
				
		// Setup ad dialog factory for interstitial/exapnded use
		//adDialogFactory = new AdDialogFactory(context);
		adSizeUtilities = new AdSizeUtilities(this, metrics);
		
		// Create views for each ad type
		adTextView = createTextView(context);
		adImageView = createImageView(context);
		adWebView = createWebView(context);
		
		// Setup auto parameters (such as user agent, etc.)
		//autoDetectParameters = AutoDetectParameters.getInstance();
		setAutomaticParameters(context);
				
		//setLayoutParams(new ViewGroup.LayoutParams(1, 1));
		setVisibility(View.GONE);
		//setOrientation(LinearLayout.HORIZONTAL);
		setBackgroundColor(MASTAdConstants.DEFAULT_COLOR);
		
		ContentManager.getInstance(this); // force create
		
		adDelegate = new MASTAdDelegate();
		adReloadTimer = new AdReloadTimer(context, this, adLog);
	}
	
	
	private TextView createTextView(Context context)
	{
		TextView v = new TextView(context);
		
		// Child will fill the parent container; we manage the size at the parent level
		v.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		// apply standard properties
		v.setBackgroundColor(defaltBackgroundColor);
		v.setTextColor(defaultTextColor);
		
		// Initially start off with view being invisible
		//v.setVisibility(View.GONE);
		
		return v;
	}

	
	private ImageView createImageView(Context context)
	{
		ImageView v = new ImageView(context);
		
		// Child will fill the parent container; we manage the size at the parent level
		v.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		// apply standard properties
		v.setBackgroundColor(defaltBackgroundColor);
				
		// Initially start off with view being invisible
		//v.setVisibility(View.GONE);
		
		return v;
	}
	
	
	public AdWebView createWebView(Context context)
	{
		AdWebView v = new AdWebView(this, adLog, metrics, true);
	
		//ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		/*
		// XXX handle margin differently for relative layout container???
		ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT); 
		lp.leftMargin = 0;
		lp.topMargin = 0;
		*/
		v.setLayoutParams(createAdLayoutParameters());
		
		v.setBackgroundColor(defaltBackgroundColor);
				
		// Initially start off with view being invisible
		//v.setVisibility(View.GONE);
		
		// Pre-load header (empty body) with ormma / mraid javascrpt code
		//String dataOut = setupViewport(true, null);
		//v.loadDataWithBaseURL(null, dataOut, "text/html", "UTF-8", null);
				
		return v;
	}

	
	private RelativeLayout.LayoutParams createAdLayoutParameters()
	{
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		return layoutParams;
	}
	
	
	public AdWebView getAdWebView()
	{
		return adWebView; 
	}
	
	
	public ImageView getAdImageView()
	{
		return adImageView;
	}
	
	
	public TextView getAdTextView()
	{
		return adTextView;
	}
	

	public String getUserAgent()
	{
		String userAgent = (String)adserverRequest.getProperty(MASTAdRequest.parameter_userAgent);
		if (userAgent == null)
		{
			userAgent = adWebView.getSettings().getUserAgentString();

			if ((userAgent != null) && (userAgent.length() > 0))
			{
				adserverRequest.setProperty(MASTAdRequest.parameter_userAgent, userAgent);
			}
		}
		
		return userAgent;
	}

	
	protected void setAutomaticParameters(Context context)
	{
		// Set SDK version
		String version = MASTAdConstants.SDK_VERSION;
		if ((version != null) && (version.length() > 0))
		{
			adserverRequest.setProperty(MASTAdRequest.parameter_version, version);
		}

		// Set user agent to match what comes from a web view
		getUserAgent();
	}
	
	
	//
	// Layout management for container view
	//
	
	
	/**
	 * Override method to set layout parameters so that local copy of width/height can be saved.
	 */
	@Override
	public void setLayoutParams(ViewGroup.LayoutParams params)
	{
		System.out.println("SetLayoutParams: " + params.toString());
		
		setVisibility(View.VISIBLE);
		
		//layoutWidth  = params.width;
		//layoutHeight = params.height;
		
		super.setLayoutParams(params);
	}

	
	/*
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		// If ad view has a specified width, use it, otherwise use the value passed in
		int width = widthMeasureSpec;
		//if ((layoutWidth >= 0) && (layoutWidth <= metrics.widthPixels))
		if (adserverRequest != null)
		{
			int maxWidth = adserverRequest.getProperty(MASTAdRequest.parameter_size_x, -1);
			if (maxWidth >= 0)
			{
				width = maxWidth;
			}
		}
		else if (layoutWidth >= 0) 
		{
			width = layoutWidth;
		}

		
		// If ad view has a specified height, use it, otherwise use the value passed in
		int height = heightMeasureSpec;
		//if ((layoutHeight >= 0) && (layoutHeight <= metrics.heightPixels))
		if (adserverRequest != null)
		{
			int maxHeight = adserverRequest.getProperty(MASTAdRequest.parameter_size_y, -1);
			if (maxHeight >= 0)
			{
				height = maxHeight;
			}
		}
		else if (layoutHeight >= 0)
		{
			height = layoutHeight;
		}
		
		// Report measured size
		setMeasuredDimension(width, height);
	}
	*/
	
	
	//
	// Content loaders; NOTE - must call these from a UI thread!!
	//
	
	
	private void setTextContent(String textData)
	{
		addView(adTextView);
		adTextView.setText(textData);
		adTextView.setVisibility(View.VISIBLE);

		//mViewState = Mraid.STATES.DEFAULT;
		
		//setBackgroundColor(Color.BLACK);
	}
	
	
	/*
	private void setImageContent(String imageData)
	{
		// XXX decode data ???
		//setImageContent(...);
	}
	*/
	
	
	/*
	private void setImageContent(Uri imageuri)
	{
		// XXX fetch image data
		// setImageContent(bitmap);
	}
	 */
	
	
	private void setImageContent(Bitmap imageData)
	{
		addView(adImageView);
		adImageView.setImageBitmap(imageData);
		adImageView.setVisibility(View.VISIBLE);
		
		//mViewState = Mraid.STATES.DEFAULT;
		
		//setBackgroundColor(Color.BLACK);
	}
	
	
	/*
	private void setImageContent(int color)
	{
		imageView.setBackgroundColor(color);
		imageView.setVisibility(View.VISIBLE);
		
		// If image content, hide other views
		textView.setVisibility(View.GONE);
		webView.setVisibility(View.GONE);
	}
	*/
	
	
	private void setWebContent(String webData)
	{	
		// If state is not loading, set that before continuing
		if (adWebView.getMraidInterface().getState() != MraidInterface.STATES.LOADING)
		{
			/*
			// Recreate web view to start fresh for new ad XXX DONT WANT TO DO THIS!!!
			if (adWebView.getParent() != null)
			{
				((ViewGroup)adWebView.getParent()).removeView(adWebView);
			}
			adWebView.destroy();
			adWebView = createWebView(context);
			*/
			adWebView.resetForNewAd();
		}
				
		// Put web view in place
		addView(adWebView);
		adWebView.setVisibility(View.VISIBLE);
		
		//webData = "<HTML><HEAD><TITLE>Testing...</TITLE></HEAD><BODY><H1>Testing document...</H1></BODY></HTML>";
		String dataOut = setupViewport(false, webData);
		System.out.println("setWebContent: injecting: " + dataOut);
		
		
		adWebView.loadDataWithBaseURL(null, webData, "text/html", "UTF-8", null);
	}
	
	
	synchronized public void setAdContent(AdData ad)
	{
		if (ad == null)
		{
			return;
		}
			
		removeAllViews();
		if (isShowCloseOnBanner && (bannerCloseButton != null))
		{
			// put close button back if requested
			addView(bannerCloseButton);	
		}
		
		if (ad.adType == MASTAdConstants.AD_TYPE_TEXT)
		{
			setTextContent(ad.text);
			sendTrackingImpression(ad);
		}
		else if (ad.adType == MASTAdConstants.AD_TYPE_IMAGE)
		{
			setImageContent(ad.imageBitmap);
			sendTrackingImpression(ad);
		}
		else // RICHMEDIA or THIRDPARTY (which uses rich media)
		{
			setWebContent(ad.richContent);
		}
		
		//adViewState = MraidInterface.STATES.DEFAULT; already done in ad web view???
	}
	
	
	private void sendTrackingImpression(AdData ad)
	{
		if ((ad != null) && (ad.trackUrl != null) && (ad.trackUrl.length() > 0))
		{
			// Looks like we have a tracking url, fire off worker to send impression back to server
			AdData.sendImpressionInBackground(ad.trackUrl);
		}
	}
	
	
	//
	// Generic ad content loader which can be called from a non-ui thread
	//
	
	
	public void setAdContentOnUi(final AdData ad)
	{
		handler.post(new Runnable()
		{
			public void run()
			{
				setAdContent(ad);
				
				/*
				getLayoutParams().width  = layoutWidth;
				getLayoutParams().height = layoutHeight;
				requestLayout();
				*/
			}
		});
	}
	

	/**
	 * Immediately update ad view contents.
	 */
	public void update()
	{
		//update(true);
		
		adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "update", "");
		StartLoadContent();
	}
	
	
	/*
	protected void update(boolean isManual)
	{
		if (isShown() || isManual) 
		{
			adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "update", "");
			if (isManual) IsManualUpdate = true;
			//hideVirtualKeyboard();
			StartLoadContent();
		}
	}
	*/
	

	/**
	 * Set banner refresh interval (in seconds). Once an ad has finished loading, the timer starts
	 * and a new ad will be loaded after this amount of time has elapsed. Default 120 seconds.
	 * If 0, ads are not updated automatically (use the update() method for a manual update.)
	 * If less than 0, the default value is re-instated.
	 */
	public void setUpdateTime(int updateTime)
	{
		adLog.log(MASTAdLog.LOG_LEVEL_2, MASTAdLog.LOG_TYPE_INFO, "setUpdateTime", String.valueOf(updateTime));
		adReloadTimer.setAdReloadPeriod(updateTime);
	}

	
	// 
	// Integration with ad-fetching/parsing 
	//

	
	//private void StartLoadContent(Context context, AdViewContainer view)
	public void StartLoadContent()
	{
		adReloadTimer.cancelTask();
		
		adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "StartLoadContent", "");
		
		// have to have a valid site & zone to request content 
		if ((adserverRequest == null) ||
		    (adserverRequest.getProperty(MASTAdRequest.parameter_site, 0) == 0) ||
		    (adserverRequest.getProperty(MASTAdRequest.parameter_zone, 0) == 0))
		{
			adReloadTimer.startTimer();
			adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_WARNING, "StartLoadContent", "site=0 or zone=0");
			return;
		}
		
		
		/*
		if ((defaultImageResource!=null) && (getBackground()==null))
		{
			try {
				handler.post(new SetBackgroundResourceAction(view, defaultImageResource));
			} catch (Exception e) {
				adLog.log(MASTAdLog.LOG_LEVEL_1, MASTAdLog.LOG_TYPE_ERROR, "StartLoadContent", e.getMessage());
			}
		}
		*/
	
		// If expanded form of ad is being displayed, we don't load new content
		if (adWebView.getMraidInterface().getState() != MraidInterface.STATES.EXPANDED)
		{						
			try
			{
				if (adWebView.getMraidInterface().getState() == MraidInterface.STATES.RESIZED)
				{
					// Ad view is going to reload & resize to default state; we need our state to match that
					adWebView.getMraidInterface().setState(MraidInterface.STATES.DEFAULT);
				}

				// if delegate defined, invoke 
				if (adDelegate.getAdDownloadHandler() != null)
				{
					adDelegate.getAdDownloadHandler().onDownloadBegin((MASTAdView)this);
				}
				
				String url = adserverRequest.toString(MASTAdConstants.AD_REQUEST_TYPE_XML);
				lastRequest = url;
				requestCounter++;
				adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "requestGet["+String.valueOf(requestCounter)+"]" , url);
				ContentManager.getInstance(this).startLoadContent(this, url);
			}
			catch (Exception e)
			{
				adLog.log(MASTAdLog.LOG_LEVEL_1, MASTAdLog.LOG_TYPE_ERROR, "StartLoadContent.requestGet", e.getMessage());
				//interceptOnAdDownload.error(this, e.getMessage());
				
				// XXX start timer?
			}
		}
	}


	private void setErrorResult(AdData ad)
	{
		adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_ERROR, "requestGet result["+String.valueOf(requestCounter)+"][ERROR]", ad.error);

		if (adDelegate.getAdDownloadHandler() != null)
		{
			adDelegate.getAdDownloadHandler().onDownloadError((MASTAdView)this, ad.error);
		}
		
		adReloadTimer.startTimer();
		
		// If supposed to show previous ad on error, but no previous content, skip out
		if ((lastResponse !=null) && (lastResponse.hasContent()) && !isShowPreviousAdOnError)
		{
			return;
		}
		
		// Show previous ad
		setResult(lastResponse);
		
		return;
	}

	
	synchronized public boolean setResult(final AdData ad)
	{
		if (ad == null)
		{
			AdData error = new AdData();
			error.error = "Unknown error getting ad (null object)...";
			setErrorResult(error);
			return false;
		}
		else if (ad.error != null)
		{
			setErrorResult(ad);
			return false;
		}
		else if (this.getParent() == null)
		{
			// View not currently included in any layout, don't try to show content for now, just save it
			lastResponse = ad;
			adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "requestGet result["+String.valueOf(requestCounter)+"]", ad.toString());
			adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "setResult", "no parent for ad view, skipping display for now...");
			return false;
		}
		else
		{	
			adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "requestGet result["+String.valueOf(requestCounter)+"]", ad.toString());
		}
		
		//isFirstTime = false;
		//if(isAutoCollapse) this.setAdVisibility(View.VISIBLE);
		//final Context context = getContext();
		
		try
		{
			if (ad.adType == MASTAdConstants.AD_TYPE_EXTERNAL_THIRDPARTY)
			{
				notifyExternalThirdPartyAd(ad);
			}
			else
			{	
				setAdContentOnUi(ad);
				adReloadTimer.startTimer();
			}
		}
		catch (Exception e)
		{
			adLog.log(MASTAdLog.LOG_LEVEL_1, MASTAdLog.LOG_TYPE_ERROR, "StartLoadContent", e.getMessage());
			adReloadTimer.startTimer();
			return false;
		}
		
		// Not an error, so remember this as the new "last" ad
		lastResponse = ad;
					
		return true;
	}

	
	private void notifyExternalThirdPartyAd(AdData ad)
	{
		if ((ad != null) && (adDelegate.getThirdPartyRequestHandler() != null))
		{
			try
			{
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("type", ad.getAdTypeName());
				
				if ((ad.externalCampaignProperties != null) && (!ad.externalCampaignProperties.isEmpty()))
				{
					Iterator<NameValuePair> i = ad.externalCampaignProperties.iterator();
					NameValuePair nvp;
					while (i.hasNext())
					{
						nvp = i.next();
						if (nvp != null)
						{
							params.put(nvp.getName(), nvp.getValue());
						}
					}
				}
	
				adDelegate.getThirdPartyRequestHandler().onThirdPartyEvent((MASTAdView) this, params);
			}
			catch (Exception e)
			{
				adLog.log(MASTAdLog.LOG_LEVEL_1, MASTAdLog.LOG_TYPE_ERROR, "onThirdPartyRequest", e.getMessage());										
			}
		}
	}

	
	// Create viewport for showing ad; version 2.9 and earlier had a "bug" which caused
	// ad creative to be scaled on device to the device dpi; version 2.10 introduced a fix
	// for this, but the change in behavior caused some issues. A deprecated flag allowed
	// reverting to the old behavior. Per a client suggestion, another fix is being introduced
	// which allows the app developer to customer the header and/or body code to be injected.
	private String setupViewport(boolean headerOnly, String body)
	{
		StringBuffer data = new StringBuffer("<html><head>");
		
		// Insert our javascript bridge library; this is always required
		data.append("<style>*{margin:0;padding:0}</style>");				
		data.append(getInjectionHeaderCode());
		data.append("</head><body>");
		
		if (!headerOnly && (body != null))
		{
			data.append(body);
		}
		
		data.append("</body></html>");
		
		System.out.println("SetupViewport: final string: " + data.toString());
		return data.toString();
	}
	
	
	//
	// Javascript interaction
	//
	

	/**
	 * Provide access to the diagnostic log object created internal to this view
	 * 
	 * @return MASTAdLog usable for diagnostics debug logging
	 */
	public MASTAdLog getLog()
	{
		return adLog;
	}
		

	// Called when close() method is invoked from ad view
	public String close(Bundle data)
	{	
		//buttonClose.setVisibility(View.INVISIBLE);
		
		//ViewGroup.LayoutParams lp = getLayoutParams();
		
		MraidInterface.STATES adState = adWebView.getMraidInterface().getState();
		if ((adState == MraidInterface.STATES.DEFAULT) && (adPlacementType == MraidInterface.PLACEMENT_TYPES.INTERSTITIAL))
		{
			System.out.println("Closing interstitial ad view...");
			
			// dismiss dialog containing interstitial view
			adSizeUtilities.dismissDialog();
			
			// Make view invisible
			//adWebView.setVisibility(View.GONE);
			
			// set state to hidden
			adWebView.getMraidInterface().setState(MraidInterface.STATES.HIDDEN);
		}
		else if (adState == MraidInterface.STATES.EXPANDED)
		{
			System.out.println("Closing expanded ad view...");
			
			// dismiss dialog containing expanded view
			adSizeUtilities.dismissDialog();
			
			// In the case of a 2-part creative, the original adview was left in place,
			// which means none of this is needed.
			ViewGroup parent = (ViewGroup)adWebView.getParent();
			if (parent != this)
			{
				// Remove adview from temporary container
				if (parent != null)
				{
					parent.removeView(adWebView);
				}
							
				// Reset layout parameters
				//ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				/*
				// XXX handle margin differently for relative layout container???
				ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)adWebView.getLayoutParams();
				lp.width = ViewGroup.LayoutParams.FILL_PARENT;
				lp.height = ViewGroup.LayoutParams.FILL_PARENT;
				lp.setMargins(0,  0, 0, 0);
				*/
				adWebView.setLayoutParams(createAdLayoutParameters());
				
				// Move ad view back to normal container
				this.addView(adWebView);
				
				adSizeUtilities.clearExpandedAdView();
			}
				
			// Return to default state
			adWebView.getMraidInterface().setState(MraidInterface.STATES.DEFAULT);			
		}
		else if (adState == MraidInterface.STATES.RESIZED)
		{
			System.out.println("Closing resized ad view...");
			
			// Remove adview from temporary container
			ViewGroup parent = (ViewGroup)adWebView.getParent();
			if (parent != null)
			{
				parent.removeView(adWebView);
				
				// Now remove parent, which was a temporary container created for the expand/resize
				ViewGroup pparent = (ViewGroup)parent.getParent();
				if (pparent != null)
				{
					pparent.removeView(parent);
				}
			}
	
			// Reset layout parameters
			//ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			/*
			// XXX handle margin differently for relative layout container???
			ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)adWebView.getLayoutParams();
			lp.width = ViewGroup.LayoutParams.FILL_PARENT;
			lp.height = ViewGroup.LayoutParams.FILL_PARENT;
			lp.setMargins(0,  0, 0, 0);
			*/
			adWebView.setLayoutParams(createAdLayoutParameters());
			
			// Move ad view back to normal container
			this.addView(adWebView);
			
			// Return to default state
			adWebView.getMraidInterface().setState(MraidInterface.STATES.DEFAULT);
		}
		else
		{
			// Ignored, NOT an error, per spec.
		}
		
		
		return null;
	}

	
	// Hide an interstitial ad view
	public String hide(Bundle data)
	{ 
		
		//if(isInterstitial() && !isExpanded) InterstitialClose();
		if (adPlacementType == MraidInterface.PLACEMENT_TYPES.INTERSTITIAL)
		{
			adWebView.setVisibility(View.GONE);
			return null;
		}
			
		return "Hide called for ad that is not interstitial"; // XXX
	}
	
	
	// Show interstitial ad view
	public void show()
	{
		if (adPlacementType != MraidInterface.PLACEMENT_TYPES.INTERSTITIAL)
		{
			System.out.println("WARNING: Interstitial show() for ad where interstitial placement type not set!");
			adWebView.getMraidInterface().setPlacementType(MraidInterface.PLACEMENT_TYPES.INTERSTITIAL);
		}

		// Set ad state to default if it is hidden
		MraidInterface mraid = adWebView.getMraidInterface();
		if (mraid.getState() == MraidInterface.STATES.HIDDEN)
		{
			mraid.setState(MraidInterface.STATES.DEFAULT);
		}
		
		// create dialog object for showing interstitial ad
		adSizeUtilities.showInterstitialDialog();
	}
	
	
	public String playVideo(Bundle data)
	{
		String mediaUri = null;
		try
		{
			mediaUri = data.getString(AdMessageHandler.PLAYBACK_URL);
		}
		catch(Exception ex)
		{
			String error = "Error getting playback uri for video: " + ex.getMessage();
			System.out.println(error);
			return error;
		}
		
		if (mediaUri != null)
		{
			return adWebView.getMraidInterface().getDeviceFeatures().playVideo(mediaUri);
		}
		else
		{
			String error = "No playback uri for video found, skipping...";
			System.out.println(error);
			return error;
		}
	}

	
	public String createCalendarEvent(Bundle data)
	{
		try
		{
			String description = data.getString(MraidInterface.get_CALENDAR_EVENT_PARAMETERS_name(MraidInterface.CALENDAR_EVENT_PARAMETERS.DESCRIPTION));
			if (description == null)
			{
				description = "";
			}
			
			String summary = data.getString(MraidInterface.get_CALENDAR_EVENT_PARAMETERS_name(MraidInterface.CALENDAR_EVENT_PARAMETERS.SUMMARY));
			if (summary == null)
			{
				summary = "";
			}
			
			String location = data.getString(MraidInterface.get_CALENDAR_EVENT_PARAMETERS_name(MraidInterface.CALENDAR_EVENT_PARAMETERS.LOCATION));
			if (location == null)
			{
				location = "";
			}
			
			String start = data.getString(MraidInterface.get_CALENDAR_EVENT_PARAMETERS_name(MraidInterface.CALENDAR_EVENT_PARAMETERS.START));
			if (start == null)
			{
				String error = "Missing calendar event start date/time, cannot continue";
				System.out.println(error);
				return error;
			}
			
			String end = data.getString(MraidInterface.get_CALENDAR_EVENT_PARAMETERS_name(MraidInterface.CALENDAR_EVENT_PARAMETERS.END));
			if (end == null)
			{
				String error = "Missing calendar event end date/time, cannot continue";
				System.out.println(error);
				return error;
			}
			
			return adWebView.getMraidInterface().getDeviceFeatures().createCalendarInteractive(description, location, summary, start, end);
		}
		catch(Exception ex)
		{
			String error = "Error getting parameters for calendar event: " + ex.getMessage();
			System.out.println(error);
			return error;
		}
	}
	
	
	/**
	 * Resize the ad view container; this method is invoked by the javascript interface and runs on
	 * the UI thread via a handler invocation, with data passed as part of the message bundle.
	 */
	public String resize(Bundle data)
	{
		// You can only invoke resize from the default ad state, or from the resized state (to further change the size)
		if ((adWebView.getMraidInterface().getState() == MraidInterface.STATES.DEFAULT) ||
			(adWebView.getMraidInterface().getState() == MraidInterface.STATES.RESIZED))
		{
			adReloadTimer.stopTimer(false); // stop ad refresh timer
			
			// Get all resize parameters from data bundle
			Integer toWidth 		= null;
			Integer toHeight 		= null;
			String  closePosition 	= null;
			Integer offsetX 		= null;
			Integer offsetY 		= null;
			Boolean offScreen 		= null;
			try
			{
				String value = data.getString(MraidInterface.get_RESIZE_PROPERTIES_name(MraidInterface.RESIZE_PROPERTIES.WIDTH));
				if (value != null)
				{
					toWidth = Integer.parseInt(value);
				}
				else
				{
					return MASTAdConstants.STR_ORMMA_ERROR_RESIZE; // XXX new, more specific error for missing width
				}
				
				value = data.getString(MraidInterface.get_RESIZE_PROPERTIES_name(MraidInterface.RESIZE_PROPERTIES.HEIGHT));
				if (value != null)
				{
					toHeight = Integer.parseInt(value);
				}
				else
				{
					return MASTAdConstants.STR_ORMMA_ERROR_RESIZE; // XXX new, more specific error for missing height
				}	
					
				closePosition = data.getString(MraidInterface.get_RESIZE_PROPERTIES_name(MraidInterface.RESIZE_PROPERTIES.CUSTOM_CLOSE_POSITION));
				if (closePosition == null)
				{
					closePosition = MraidInterface.get_RESIZE_CUSTOM_CLOSE_POSITION_name(MraidInterface.RESIZE_CUSTOM_CLOSE_POSITION.TOP_RIGHT);
				}
				
				value = data.getString(MraidInterface.get_RESIZE_PROPERTIES_name(MraidInterface.RESIZE_PROPERTIES.OFFSET_X));
				if (value != null)
				{
					offsetX = Integer.parseInt(value);
				}
				else
				{
					offsetX = 0;
				}
				
				value = data.getString(MraidInterface.get_RESIZE_PROPERTIES_name(MraidInterface.RESIZE_PROPERTIES.OFFSET_Y));
				if (value != null)
				{
					offsetY = Integer.parseInt(value);
				}
				else
				{
					offsetY = 0;
				}
				
				value = data.getString(MraidInterface.get_RESIZE_PROPERTIES_name(MraidInterface.RESIZE_PROPERTIES.ALLOW_OFF_SCREEN));
				if ((value != null) && (value.equalsIgnoreCase("false")))
				{
					// reposition view if part will go off screen
					offScreen = false;
				}
				else
				{
					// do nothing even if part of view will be off screen (default)
					offScreen = true;
				}
			}
			catch(Exception ex)
			{
				return MASTAdConstants.STR_ORMMA_ERROR_RESIZE; // XXX new, more specific error
			}
			
			
			adSizeUtilities.resizeWorker(toWidth, toHeight, closePosition, offsetX, offsetY, offScreen);		
			
			return null;
		} 
		
		return MASTAdConstants.STR_ORMMA_ERROR_RESIZE;
	}
	
	
	public String open(Bundle data)
	{
		String  url = data.getString(AdMessageHandler.OPEN_URL);

		// Pass options for dialog through to creator
		AdDialogFactory.DialogOptions options = new AdDialogFactory.DialogOptions();
		options.backgroundColor = Color.BLACK; // XXX setting?
		options.noClose = true; // no add-on close function, just browser default
		
		return adSizeUtilities.openInBackgroundThread(options, url);
	}
	
	
	public String expand(Bundle data)
	{
		// You can only invoke expand from the default or expanded ad states
		if ((adWebView.getMraidInterface().getState() == MraidInterface.STATES.DEFAULT) ||
			(adWebView.getMraidInterface().getState() == MraidInterface.STATES.EXPANDED))
		{
			// Get all expand parameters from data bundle
			Integer toWidth 		   = null;
			Integer toHeight 		   = null;
			Boolean customClose		   = null;
			//Boolean isModal			   = null; // this is read only, always true per spec
			Boolean allowReorientation = null;
			String  forceOrientation   = null;
			String  url				   = null;
			try
			{
				String value = data.getString(MraidInterface.get_EXPAND_PROPERTIES_name(MraidInterface.EXPAND_PROPERTIES.WIDTH));
				if (value != null)
				{
					toWidth = Integer.parseInt(value);
				}
				else
				{
					toWidth = metrics.widthPixels;
				}
				
				value = data.getString(MraidInterface.get_EXPAND_PROPERTIES_name(MraidInterface.EXPAND_PROPERTIES.HEIGHT));
				if (value != null)
				{
					toHeight = Integer.parseInt(value);
				}
				else
				{
					toHeight = metrics.heightPixels;
				}	
				
				value = data.getString(MraidInterface.get_EXPAND_PROPERTIES_name(MraidInterface.EXPAND_PROPERTIES.USE_CUSTOM_CLOSE));
				if ((value != null) && (value.equalsIgnoreCase("true")))
				{
					customClose = true;
				}
				else
				{
					customClose = false; // default
				}
				
				/*
				// Always modal, no need for this
				value = data.getString(MraidInterface.get_EXPAND_PROPERTIES_name(MraidInterface.EXPAND_PROPERTIES.IS_MODAL));
				if ((value != null) && (value.equalsIgnoreCase("false")))
				{
					isModal = false;
				}
				else
				{
					isModal = true;
				}
				*/
				
				value = data.getString(MraidInterface.get_ORIENTATION_PROPERTIES_name(MraidInterface.ORIENTATION_PROPERTIES.ALLOW_ORIENTATION_CHANGE));
				if ((value != null) && (value.equalsIgnoreCase("false")))
				{
					allowReorientation = false;
				}
				else
				{
					allowReorientation = true; // default
				}
				
				forceOrientation = data.getString(MraidInterface.get_ORIENTATION_PROPERTIES_name(MraidInterface.ORIENTATION_PROPERTIES.FORCE_ORIENTATION));
				if (forceOrientation == null)
				{
					forceOrientation = MraidInterface.get_FORCE_ORIENTATION_PROPERTIES_name(MraidInterface.FORCE_ORIENTATION_PROPERTIES.NONE);
				}
				
				url = data.getString(AdMessageHandler.EXPAND_URL);
			}
			catch(Exception ex)
			{
				return MASTAdConstants.STR_ORMMA_ERROR_EXPAND; // XXX new, more specific error
			}
		
			// Limit expand size to device width/height at most
			if ((toWidth < 0) || (toWidth > metrics.widthPixels))
			{
				toWidth = metrics.widthPixels; 
			}
			if ((toHeight < 0) || (toHeight > metrics.heightPixels))
			{
				toHeight = metrics.heightPixels;
			}
			System.out.println("exapndInUiThread: to h/w = " + toHeight + "/" + toWidth);

			// Pass options for dialog through to creator
			AdDialogFactory.DialogOptions options = new AdDialogFactory.DialogOptions();
			options.backgroundColor = Color.BLACK; // XXX setting?
			options.customClose = customClose;
			options.height = toHeight;
			options.width = toWidth;
			
			adReloadTimer.stopTimer(false); // stop ad refresh timer		
			
			adWebView.getMraidInterface().setState(MraidInterface.STATES.EXPANDED);
			
			if ((url == null) || (url.length() < 1) || (url.equalsIgnoreCase("undefined")))
			{
				// We are using existing ad view / content, safe to do this on UI thread
				return adSizeUtilities.expandInUIThread(options, allowReorientation, forceOrientation);
			}
			else
			{
				// Two part creative, need to fetch new data, must use non-UI thread for that;
				// after data available will call back to finish on ui thread via handler.
				return adSizeUtilities.expandInBackgroundThread(options, allowReorientation, forceOrientation, url);
			}
		}
		
		return MASTAdConstants.STR_ORMMA_ERROR_EXPAND; // XXX new, more specific error
	}
	
		
	//
	// Injection header
	//

	
	/** Default viewport string injected into ad view **/
	public static final String defaultViewportDefinition =
		"<meta name=\"viewport\" content=\"user-scalable=no,target-densitydpi=device-dpi\"/>";
	
	/** Default body style css injected into ad view **/
	public static final String defaultBodyStyle =
		"<style>body{margin: 0px; padding: 0px;}</style>";
	
	// Custom injection string; if any has been set by user
	private String injectionHeaderCode = null;
		
	
	/**
	 * Customize the "HTML" (or javascript/css) code to be inserted into the HTML HEAD when creating
	 * webview for ad content. This is used to setup the viewport for the web view and to define the
	 * style to be applied to the body (for centering, etc.) By default this will contain the string:
	 * 
	 * <meta name=\"viewport\" content=\"target-densitydpi=device-dpi\"/> \
	 * <style>body{margin: 0px; padding: 0px; display:-webkit-box;-webkit-box-orient:horizontal;-webkit-box-pack:center;-webkit-box-align:center;}</style>
	 * 
	 * @param value String content to be inserted, or null to use built-in default.
	 */
	/*
	private void setInjectionHeaderCode(String value)
	{
		injectionHeaderCode = value;
	}
	*/
	
	
	/**
	 * Get current injection header code string.
	 * @return Current injection header value.
	 */
	public String getInjectionHeaderCode()
	{
		if (injectionHeaderCode != null)
		{
			return injectionHeaderCode;
		}
		else
		{
			// Default fragment, revised as of 2.12 SDK
			return defaultViewportDefinition + defaultBodyStyle;
		}
	}

	
	
	
	//
	// Misc
	//

	
	/**
	 * Set log level to one of the log level values defined in he MASTAdLog class
     * (corresponding to errors, errors + warnings, or everything including server traffic.)
     * The SDK is instrumented with diagnostics logging that can assist with troubleshooting
     * integration problems. Log messages are sent to the system logging interface (viewable
     * with logcat) and an in-memory log of recent messages is stored for easy access.
     * @see MASTAdLog See the MASTAdLog class for more information about logging.
	 * @param logLevel Int log level to control which messages will be sent to the logs.
	 */
	public void setLogLevel(int logLevel)
	{
		adLog.setLogLevel(logLevel);
	}
	
	
	public Handler getHandler()
	{
		return handler;
	}

	
	public String getLastRequest()
	{
		return lastRequest;
	}
	
	
	public String getLastResponse()
	{
		return lastResponse.responseData;
	}
	
	
	public void setAdPlacementInterstitial(boolean isInterstitial)
	{
		if (isInterstitial)
		{
			adPlacementType = MraidInterface.PLACEMENT_TYPES.INTERSTITIAL;
		}
		else
		{
			adPlacementType = MraidInterface.PLACEMENT_TYPES.INLINE;		
		}
		adWebView.getMraidInterface().setPlacementType(adPlacementType);
	}
	
	
	// XXX remove? or use for XML object creation???
	public boolean setAdPlacementType(String placement)
	{
		if (placement.equalsIgnoreCase(MraidInterface.PLACEMENT_TYPES.INLINE.toString()))
		{
			adPlacementType = MraidInterface.PLACEMENT_TYPES.INLINE;
			adWebView.getMraidInterface().setPlacementType(adPlacementType);
			return true;
		}
		else if (placement.equalsIgnoreCase(MraidInterface.PLACEMENT_TYPES.INTERSTITIAL.toString()))
		{
			adPlacementType = MraidInterface.PLACEMENT_TYPES.INTERSTITIAL;
			adWebView.getMraidInterface().setPlacementType(adPlacementType);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	protected void onAttachedToWindow()
	{
		adLog.log(MASTAdLog.LOG_LEVEL_2, MASTAdLog.LOG_TYPE_INFO, "Attached to Window", "");

		adReloadTimer.startTimer();
		
		//StartLoadContent(getContext(), this);
		
		super.onAttachedToWindow();

		if (adDelegate.getAdActivityEventHandler() != null)
		{
			adDelegate.getAdActivityEventHandler().onAdAttachedToActivity((MASTAdView)this);
		}

		// ??? If the ad content was downloaded while the view was not attached to a window
		// they we attempt to "install" it now; however, need to make sure we don't get into
		// a race condition between set content completing and this method at the same time.
		if ((lastResponse != null) && (lastResponse.hasContent()))
		{
			if (lastResponse.adType == MASTAdConstants.AD_TYPE_EXTERNAL_THIRDPARTY)
			{
				notifyExternalThirdPartyAd(lastResponse);
			}
			else
			{
				setAdContent(lastResponse); // If we have ad content from "before", load it
			}
		}
		
		// Notify ad that viewable state has changed
		adWebView.getMraidInterface().setViewable(true);
	}
	
	
	protected void onDetachedFromWindow()
	{
		adLog.log(MASTAdLog.LOG_LEVEL_2, MASTAdLog.LOG_TYPE_INFO, "Detached from Window", "");
		
		/*
		if (image!=null)
		{
			image.recycle();
			image = null;
		}		
		*/
		
		if (locationListener != null)
		{
			locationListener.stop();
			locationListener = null;
		}
		
		adReloadTimer.stopTimer(true); // stop timer, clean up object
		
		// stop loading any content
		ContentManager.getInstance(this).stopLoadContent(this);

		super.onDetachedFromWindow();
		
		if (adDelegate.getAdActivityEventHandler() != null)
		{
			adDelegate.getAdActivityEventHandler().onAdDetachedFromActivity((MASTAdView)this);
		}
		
		// Notify ad that viewable state has changed
		adWebView.getMraidInterface().setViewable(false);
	}
	
	
	public void setLocationDetection(boolean detect, Integer minWaitMillis, Float minMoveMeters)
	{
		if (detect)
		{		 	
			int isAccessFineLocation = context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
	    	if (isAccessFineLocation == PackageManager.PERMISSION_GRANTED)
	    	{
				locationListener = new AdLocationListener(context, minWaitMillis, minMoveMeters, LocationManager.GPS_PROVIDER, Looper.getMainLooper(), adLog)
				{
					public void fail(String m)
					{
						// Nothing (legacy)
					}
					
					public void success(Location location)
					{
						try
						{
							double latitude = location.getLatitude();
							adserverRequest.setProperty(MASTAdRequest.parameter_latitude, Double.toString(latitude));
							
							double longitude = location.getLongitude();
							adserverRequest.setProperty(MASTAdRequest.parameter_longitude, Double.toString(longitude)); 
							
							adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "LocationDetection changed", "("+latitude+";"+longitude+")");								
			    		}
						catch (Exception e)
						{
			    			adLog.log(MASTAdLog.LOG_LEVEL_2,MASTAdLog.LOG_TYPE_ERROR,"LocationDetection",e.getMessage());
			    		}									
					}
				};

		    	if (locationListener.isAvailable())
		    	{
		    		adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "LocationDetection", "Start listening for location updates");
		    		locationListener.start();
		    	}
		    	else
		    	{
		    		adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "LocationDetection", "Location updates not available");
		    		locationListener.stop();
		    		locationListener = null;
		    	}
			}
	    	else
	    	{
	    		adLog.log(MASTAdLog.LOG_LEVEL_2, MASTAdLog.LOG_TYPE_ERROR, "LocationDetection", "No permission for GPS");
	    	}
    	}
		else
		{
			// If listening, stop
			if (locationListener != null)
			{
				locationListener.stop();
				locationListener = null;
			}
			
			adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "LocationDetection", "Stop listening for location updates");
		}
	}
	
	
	public void addCloseToBanner(boolean flag)
	{
		boolean change = false;
		if (flag != isShowCloseOnBanner)
		{
			change = true;
		}
		
		isShowCloseOnBanner = flag;

		if (change)
		{
			final int visible;
			if (isShowCloseOnBanner)
			{
				visible = View.VISIBLE;
			}
			else
			{
				visible = View.GONE;
			}
	
			handler.post(new Runnable()
			{			
				@Override
				public void run()
				{

					// Create close button for banner
					if (bannerCloseButton == null)
					{
						bannerCloseButton = createCloseButton(context);
						self.addView(bannerCloseButton);
					}
					
					bannerCloseButton.setVisibility(visible);
				}
			});
		}
	}
	
	
	protected Button createCloseButton(Context c)
	{
		Button b = new Button(c);
		b.setMinHeight(50);
		b.setMinWidth(50);
		b.setText("Close"); // XXX string, allow customizing
		b.setVisibility(View.GONE); // default is not present
		
		// Position button in upper right
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		b.setLayoutParams(layoutParams);
		
		b.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				handler.post(new Runnable()
				{
					@Override
					public void run()
					{
						// For interstitial ad, dismiss dialog;
						// for banner, remove the view (and if
						// richmedia, set state to hidden?)
						if (adPlacementType == MraidInterface.PLACEMENT_TYPES.INTERSTITIAL)
						{
							System.out.println("Closing interstitial ad view...");
							
							// dismiss dialog containing interstitial view
							adSizeUtilities.dismissDialog();
						}
						else
						{
							System.out.println("Closing banner ad view...");
							
							// Remove ad container from parent, hiding it
							ViewGroup parent = (ViewGroup)self.getParent();
							if (parent != null)
							{
								// Remove container from parent
								parent.removeView(self);
							}
						}
					}
				});
			}
		});
		
		return b;
	}
	
	
	public void onOrientationChange(int orientationAngle, int screenOrientation)
	{
		// Update size/position (triggering a size change event if appropriate)
		WindowManager windowManager = (WindowManager) ((Activity)context).getSystemService(Context.WINDOW_SERVICE);
		if (metrics == null)
		{
			metrics = new DisplayMetrics();
		}
		windowManager.getDefaultDisplay().getMetrics(metrics);		
		adSizeUtilities.setMetrics(metrics);
		
		adWebView.getMraidInterface().setCurrentPosition(adWebView.getLeft(), adWebView.getTop(), adWebView.getWidth(), adWebView.getHeight());
		//adWebView.getMraidInterface().setOrientation(orientationAngle);
		
		// If ad is expanded and a 2 part creative caused a new view to be created,
		// inject events into that one also.
		if ((adWebView.getMraidInterface().getState() == MraidInterface.STATES.EXPANDED) &&
			(adSizeUtilities.getExpandedAdView() != null))
		{
			AdWebView expandedWebView = adSizeUtilities.getExpandedAdView(); 
			expandedWebView.getMraidInterface().setCurrentPosition(expandedWebView.getLeft(), expandedWebView.getTop(), expandedWebView.getWidth(), expandedWebView.getHeight());
			//expandedWebView.getMraidInterface().setOrientation(orientationAngle);
		}
	}
	
	
	public void mraidEvent(String method, String parameter)
	{
		if (adDelegate.getMraidEventHandler() != null)
		{
			adDelegate.getMraidEventHandler().onMraidEvent((MASTAdView)this, method, parameter);
		}
	}
}
