package com.moceanmobile.flickrviewer;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.MASTAdView.MASTAdLog;
import com.MASTAdView.MASTAdView;
import com.moceanmobile.flickrviewer.AdInterfaces.MoceanAdInterface;


public class FlickrViewerActivity extends Activity
{
	// Class-wide reference to ad view object for banner and interstitial
	private MASTAdView adView = null;
	private MASTAdView interstitialView = null;

	private int bannerAdLocation = 0; // unknown, set below
	
	// Class wide reference to third party ad interface and ad view object
	private long thirdPartyAdStartTime = 0;
	private ThirdPartyEventHandler thirdPartyHandler = null;
	
	// Display size information
	private DisplayMetrics metrics;

	// Layout inflater
	LayoutInflater inflater = null;
	
	// Handler so that UI can be notified to update when flickr feed is loaded
	private Handler handler = new Handler();

	// index of current image to display; count of changes for triggering interstitial ad
	private int imageIndex = 0;
	private int imageCycleCount = 0;
	
	// flickr feed reference
	private FlickrDataFeed dataFeed = null;

	// Bookmarks
	private Bookmarks bookmarks = null;
	
	// Metainfo popup creator
	private MetainfoPopup metainfoFactory = null;
	
	// reference to items in layout that we update periodically
	private LinearLayout mainManager = null; 
	private ImageView flickrImage = null;
	private TextView imageTitle = null;
	private TextView imageCounter = null;
	private TextView imageAuthor = null;
	private TextView imageDate = null;
	
	// Menu item helper
	private ApplicationMenu applicationMenu;

	// Key to save help popup usage in saved instance state, around configuration changes
	public static final String helpPopupShownKey = "com.moceanmobile.flickrviewer.helpPopupShown";
	private boolean helpPopupShown = false;
	
	// Center body CSS
	//public static final String injectionString =
		//"<body style=\"margin: 0px; padding: 0px; width: 100%; height: 100%; display:-webkit-box;-webkit-box-orient:horizontal;-webkit-box-pack:center;-webkit-box-align:center;\">";
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	// Get device size information, save
    	WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    	metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Setup  menu worker
        applicationMenu = new ApplicationMenu(this);
        
        // process data feed form flickr & then update UI
        refreshFlickrFeed();
    
        // Setup bookmarks
        bookmarks = new Bookmarks(this.getApplicationContext());
        bookmarks.load();
        
        // Top level manager for view
        mainManager = (LinearLayout)findViewById(R.id.mainManager);
        
        // Get reference to ad view for banner ad; we will randomly use either the top or bottom
        // ad, and mark it visible; the other will be removed because just leaving it gone doesn't
        // seem to do the trick.
        int random = (int)(System.currentTimeMillis() % 2); // 0 or 1
        //int random = 1; // XXX for testing 3rd party ads on bottom zone
        if (random == 0)
        {
        	// Show top banner ad
        	bannerAdLocation = Constants.banner_location_top;
        	adView = (MASTAdView)findViewById(R.id.mainBottomAdView);
        	mainManager.removeView(adView);
        	adView.destroy();
        	adView = (MASTAdView)findViewById(R.id.mainTopAdView);
        }
        else
        {
        	// Show bottom banner ad
        	bannerAdLocation = Constants.banner_location_bottom;
        	adView = (MASTAdView)findViewById(R.id.mainTopAdView);
        	mainManager.removeView(adView);
        	adView.destroy();
        	adView = (MASTAdView)findViewById(R.id.mainBottomAdView); 
        }
        adView.setVisibility(View.VISIBLE);
        adView.setBackgroundColor(Color.BLACK);
        
        // Center ads in the ad view using custom injection body code feature
        //adView.setInjectionBodyCode(injectionString);
        
        adView.setUpdateTime(Constants.banner_ad_update_time);
        //adView.setUpdateTime(30); // XXX for testing 3rd party ads
        
        adView.setLogLevel(Constants.defaultAdLogLevel);
        //adView.setLogLevel(MASTAdLog.LOG_LEVEL_3); // debug logging level
        
        // Setup third party ad handler; currently we are only getting third party ads
        // in the bottom zone, but this will handle either banner ad view.
        thirdPartyHandler = createThirdPartyHandler();
        adView.setOnThirdPartyRequest(thirdPartyHandler);
        
        // Create interstitial view and let it start an update, so it will
        // (hopefully) be ready for display when needed
        interstitialView = createInterstitialView();
        
        // Get reference to layout items we update with flickr "stuff"
        imageTitle = (TextView)findViewById(R.id.contentTitle);
        imageCounter = (TextView)findViewById(R.id.contentCounter);
        imageAuthor = (TextView)findViewById(R.id.contentAuthor);
        imageDate = (TextView)findViewById(R.id.contentDate);
        
    	// Setup image view and touch listener
        flickrImage = (ImageView)findViewById(R.id.contentImage);
        flickrImage.setOnTouchListener(createTouchListener());
           
        // Show help popup? Only if the app was just launched.
        if ((savedInstanceState == null) || (savedInstanceState.containsKey(helpPopupShownKey) == false))
        {
        	startupHelpPopup();
        	helpPopupShown = true; // Remember, so we don't show again
        }
        
        // Create metainfo popup factory
        metainfoFactory = new MetainfoPopup(this, metrics, bookmarks);
    }
    
    // Save UI state changes to the savedInstanceState.
    @Override 
    public void onSaveInstanceState(Bundle savedInstanceState) { 
      super.onSaveInstanceState(savedInstanceState);  
      savedInstanceState.putBoolean(helpPopupShownKey, helpPopupShown); 
    } 
    
    /** Called when the activity returns to the front of the stack **/
    @Override
    public void onResume()
    {
    	super.onResume();
    	
    	// Undim window (in case recovering from popup)
    	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    	
    	// If to much time has elapsed, may need to replace third party ad view
    	// with mocean ad view.
    	long now = System.currentTimeMillis();
    	if (thirdPartyAdStartTime > 0)
    	{
    		long delta = now - thirdPartyAdStartTime;
    		if (now > (Constants.banner_ad_update_time * 1000))
    		{
    			//System.out.println("Force restoring mocean adview in onResume()");
    			restoreMoceanAdView();
    		}
    	}
    	
    	// Update the banner ad view each time the screen is resumed, just to keep it
    	// refreshed. This will be triggered each time the interstitial view closes.
    	// The ad view also has an auto-refresh if more than X seconds.
    	// goes by without an update.
    	if (adView != null)
    	{
    		if (metrics != null)
			{
    			adView.setMaxSizeX(metrics.widthPixels);
			}
    		adView.update();
    	}
    	
    	// Update ad view content for interstitial ad here each time the screen is resumed;
    	// this is intended to let the update run "behind the scenes" while the user is on
    	// the main screen, so that when the interstitial is shown, it will be ready.
    	if (interstitialView != null)
    	{
    		interstitialView.update();
    	}
    }
    
    
    /** Called when the configuration changes, such as rotation or keyboard open/close **/
    //
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
    	super.onConfigurationChanged(newConfig);
    	
    	
    	// System.out.println("Custom onConfigurationChange method called");
    	
    	
    	
    	// Get device size information, save
    	WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    	metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
	
    	// Refresh ad with new dimensions
		if (adView != null)
    	{
			adView.setMaxSizeX(metrics.widthPixels);
    		adView.update();
    	}
    }
    //
    
    // Display image in view, and potentially update button visibility
    private void showImage(Bitmap bitmap)
    {
    	if (bitmap != null)
    	{
    		flickrImage.setImageBitmap(bitmap);
    	}
    	
    	FlickrFeed.flickrItem item = dataFeed.getItem(imageIndex);
    	if (item != null)
    	{
    		if (item.title != null)
    			imageTitle.setText(item.title);
    		else
    			imageTitle.setText("");
    	
    		if (item.author != null)
    			imageAuthor.setText(item.author);
			else
				imageAuthor.setText("");
    		
    		if (item.published != null)
    			imageDate.setText(item.published);
			else
				imageDate.setText("");
    	}
    	
    	// Update counter
    	int count = dataFeed.itemCount();
    	imageCounter.setText("" + (imageIndex + 1) + "/" + count);
    	
    	// Show interstitial ad after every X image cycles AFTER the first one
    	if ((imageCycleCount > 0) &&
			((imageCycleCount % Constants.interstitial_interval) == 0))
    	{
    		showInterstitialAd();
    	}
    }
    
    // Create interstitial view and get it ready for use
    private MASTAdView createInterstitialView()
    {
    	MoceanAdInterface adInterface = new MoceanAdInterface(this);
    	MASTAdView adserverView =
    		adInterface.createInterstitialAd(Constants.application_site,
    										 Constants.SHOW_OFF_MAIN_INTERSTITIAL_z_112074,
    										 metrics.widthPixels,
    										 metrics.heightPixels);
    	
    	return adserverView;
    }
    
    // Show the interstitial view, it has already been setup and an update started
    private void showInterstitialAd()
    {
    	//System.out.println("XXX Showing interstitial ad view...");
    	
    	Thread show = new Thread()
    	{
    		public void run()
    		{
    			// pause briefly, to prevent the ugly "flash" when the image cycles and then
    			// the interstitial is shown on top of it all at "the same time"
    			try	{ Thread.sleep(400); }	catch(Exception ex) { }
    			
    			// Always run this on the  UI thread
    			handler.post(new Runnable()
    	    	{
    	    		public void run()
    	    		{
    	    			// Show ad; update has already been done, content "should" be ready, so just display it
    	    	    	if (interstitialView != null)
    	    	    	{
    	    	    		interstitialView.show();
    	    	    	}	
    	    		}
    	    	});		
    		}
    	};
    	show.start();
    }
    
    
    // Fetch and display an image from flickr; run on another thread because we
    // can't tie up the UI thread with long/blocking operations like this.
    public void showFlickrImage()
    {
    	Thread worker = new Thread()
    	{
    		public void run()
    		{
		    	// Show the current picture with data from flickr feed
		    	FlickrFeed.flickrItem item = dataFeed.getItem(imageIndex);
		    	if (item != null)
		    	{
		    		final Bitmap image = ImageUtils.fetchImage(item.media);
		    		
		    		// Now show the image; MUST be run on the  UI thread (now that we have the bitmap)
		    		handler.post(new Runnable()
					{
						public void run()
						{
							showImage(image);
						}
					});	
		    	}
    		}
    	};
    	
    	worker.start();
    }
    
    
    // Show the previous image, if any
    private void previousImage()
    {
    	if (imageIndex > 0)
    	{
    		imageIndex -= 1;
    		imageCycleCount += 1;
    		showFlickrImage();
    	}
    }
    
    // show the next image, if any, otherwise refresh feed to get new data
    private void nextImageOrRefresh()
    {
    	int max = dataFeed.itemCount() - 1;
    	
    	if (imageIndex < max)
    	{
    		imageIndex += 1;
    		imageCycleCount += 1;
    		showFlickrImage();
    	}
    	else
    	{
    		// Refresh feed from flickr, then show first new image after
    		imageIndex = 0;
    		imageCycleCount += 1;
    		dataFeed.refresh();
    	}
    }
    
    // Touch listener for image; handles swipe left/right to cycle image
    // or click to display information popup.
    private View.OnTouchListener createTouchListener()
    {
    	return new View.OnTouchListener()
        {
        	private float oldTouchValue = 0;	
        	
        	// On swipe, navigate to previous/next
			public boolean onTouch(View v, MotionEvent event)
			{
        		switch (event.getAction())
        		{
    	            case MotionEvent.ACTION_DOWN:
    	            {
    	            	//System.out.println("ontouch: action down");
    	                oldTouchValue = event.getX();
    	                
    	                // NOTE: must return true for the down event, or we will never
    	                // receive the other action events.
    	                return true;
    	            }
    	            case MotionEvent.ACTION_UP:
    	            {
    	            	//System.out.println("ontouch: action up");
    	                float currentX = event.getX();
    	                float delta = currentX - oldTouchValue;
    	                if (delta < -Constants.picture_swipe_navigate_delta)
    	                {
    	                    // Show next picture, if any
    	                	nextImageOrRefresh();
    	                	return true;
    	                }
    	                else if (delta > Constants.picture_swipe_navigate_delta)
    	                {
    	                    // Show previous picture, if any
    	                	previousImage();
    	                	return true;
    	                }
    	                else // positions are equal (or to close for a swipe), treat as a click
    	                {
    	                	// Show informational popup
    	                	metainfoPopup();
    	                	return true;
            			}    
    	            }
    	        }
        		
    	        return false;
    	    }
        };
    }
    
    // Show help popup describing how to use the app
    private void startupHelpPopup()
    {
    	//Dim window behind the popup
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Welcome");
    	builder.setMessage("Swipe image left to go back, right to go forward.\n\n" +
    					   "Images are grouped in batches of 20. At end of one batch, a new one will be loaded.\n\n" +
    					   "Click image to view poster information and manage bookmarks.\n\n" +
    					   "Use Menu->Bookmarks to view and select bookmarked images. While viewing bookmarked images, swipe to navigate or back for more flickr images."
    					   );
    	builder.setCancelable(true);
    	builder.setPositiveButton("OK", 
			new DialogInterface.OnClickListener() 
			{           
				public void onClick(DialogInterface dialog, int id) 
				{
					dialog.dismiss();
				}       
			}
    	);
    	AlertDialog alert = builder.create();
    	alert.show(); 
    }

    
    private void metainfoPopup()
    {
    	FlickrDataFeed.flickrItem item = dataFeed.getItem(imageIndex);
    	int source = dataFeed.getDataSource();

    	//Dim window behind the popup
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    	
    	// Create popup using factory we created earlier
    	AlertDialog authorAlert = metainfoFactory.createDialog(item, source);
		authorAlert.show();
    }

    
    // Get feed
    public FlickrDataFeed getDataFeed()
    {
    	return dataFeed; 
    }
    
    
    // Used to set custom feed from bookmarks view
	public void setDataFeed(FlickrDataFeed feed)
	{
		dataFeed = feed;
		imageIndex = 0;
	}
	
	
	// Set index for current feed item to view, if it is valid
	public boolean setFeedIndex(int index)
	{
		if ((index >= 0) && (index < dataFeed.itemCount()))
		{
			imageIndex = index;
			return true;
		}
		
		return false;
	}
	
	
	public Bookmarks getBookmarks()
	{
		return bookmarks;
	}
	
	
	/** This is called one when the menu is being created and give you a chance to add items. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        return applicationMenu.onCreateOptionsMenu(menu, getMenuItems());
    }

    
    private int[] getMenuItems()
    {
    	if (dataFeed.getDataSource() == FlickrDataFeed.DATA_SOURCE_FLICKR)
    	{
	    	// NOTE: this does not set the order, just what items will be on the menu
	    	int[] menuItemsToShow = {
	        	ApplicationMenu.BOOKMARKS_ID,
	        };
		
	    	return menuItemsToShow;
    	}
    	else
    	{
    		int[] menuItemsToShow = {
	        };
    		
	    	return menuItemsToShow;
    	}
    }
    
    
    /** This is called when a menu item is selected. */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	if (applicationMenu.onMenuItemSelected(featureId, item))
    	{
    		return true;	
    	}
    	
    	return super.onMenuItemSelected(featureId, item);
    }

    
    private void refreshFlickrFeed()
    {
    	// process data feed form flickr & then update UI
        dataFeed = new FlickrFeed(handler)
        {
        	@Override
        	public void notifyFeedUpdated()
        	{
        		showFlickrImage();
        	}
        };
        dataFeed.refresh();
    }
    
    
    @Override
    public void onBackPressed()
    {
    	// If looking at bookmarks, return to normal flickr feed view, otherwise exit
    	if (dataFeed.getDataSource() == FlickrDataFeed.DATA_SOURCE_BOOKMARKS)
    	{
    		// Reload stream and refresh view
    		imageIndex = 0;
    		refreshFlickrFeed();
    	}
    	else
    	{
    		finish();
    	}
    }
    
    
    @Override
    public void onDestroy()
    {
    	if (adView != null)
    	{
    		adView.destroy();
    		adView = null;
    	}
    	
    	if (thirdPartyHandler != null)
    	{
    		thirdPartyHandler.destroyAdView();
		}
    	
    	super.onDestroy();
	}
    
    
    
    //
    // Integration/support for 3rd party ad requests through mocean SDK
    //

    
    
    private ThirdPartyEventHandler createThirdPartyHandler()
    {
    	// Create third party handler interface and customize a few of the callbacks
    	return new ThirdPartyEventHandler(this, handler, bannerAdLocation)
    	{
    		public void adLoadFailed()
    	    {
    	    	super.adLoadFailed();
    	    	restoreMoceanAdView();
    	    }
    	    
    	    public void adClosed()
    	    {
    	    	restoreMoceanAdView();
    	    }
    	    
    	    public void insertThirdPartyAdView(View v)
    	    {
    	    	insertThirdPartyView(v);
    	    }
    	    
    	    public void adTimedOut()
    	    {
    	    	restoreMoceanAdView();
    	    }
    	};
    }
    
    
    // Replace mocean banner ad with third party ad view; remember time
    private void insertThirdPartyView(View v)
    {
    	//System.out.println("Inserting third party ad view");
    	
    	if (v != null)
    	{
	    	int index = mainManager.indexOfChild(adView);
	    	mainManager.removeView(adView);
	    	mainManager.addView(v, index);
	    	thirdPartyAdStartTime = System.currentTimeMillis();
    	}
    }
    
    
    // Replace third party ad view with mocean ad view (if any)
    private void restoreMoceanAdView()
    {
    	//System.out.println("Restoring Mocean ad view");
    
    	// Always run this on the  UI thread
		handler.post(new Runnable()
    	{
    		public void run()
    		{
		    	View thirdPartyAdView = thirdPartyHandler.getAdView();
		    	if (thirdPartyAdView != null)
		    	{    	
			    	int index = mainManager.indexOfChild(thirdPartyAdView);
			    	mainManager.removeView(thirdPartyAdView);
			    	mainManager.addView(adView, index);
			    	
			    	// Update excluded campaign list for future ad requests, so we will skip
			    	// any campaigns that fail to deliver ads.
			    	thirdPartyHandler.setExcludeCampaigns(adView);
			    	
			    	adView.update();
			    	thirdPartyAdStartTime = 0;
		    	}
    		}
    	});
    }
}
