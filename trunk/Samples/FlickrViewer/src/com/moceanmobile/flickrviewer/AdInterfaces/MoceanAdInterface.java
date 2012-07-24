package com.moceanmobile.flickrviewer.AdInterfaces;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;

import com.MASTAdView.MASTAdView;
import com.moceanmobile.flickrviewer.Constants;
import com.moceanmobile.flickrviewer.FlickrViewerActivity;


// Note: for consistency, the Mocean SDK is wrapped in the "standard" ad interface created
// for this app, but in truth it is not used the same way the third party ad interfaces are
// used (so far.)
public class MoceanAdInterface extends BaseAdInterface
{
	public MoceanAdInterface(Activity activity)
	{
		super(activity);
	}

	
	static public String getName()
	{
		return "mocean";
	}
	
	
	@Override
	public MASTAdView createBannerAd(Object site, Object data, Object width, Object height, Object location)
	{
		MASTAdView adserverView = new MASTAdView(parentActivity, (Integer)site, (Integer)data);
   	 	adserverView.setBackgroundColor(Color.BLACK);
   	 
   	 	// set ad view size; perform layout
   	 	adserverView.setMaxSizeX((Integer)width);
   	 	adserverView.setMaxSizeY((Integer)height);
   	 	adserverView.setLayoutParams(new ViewGroup.LayoutParams((Integer)width, (Integer)height)); 
   	 	adserverView.requestLayout();
   	 	
   	 	adserverView.setLogLevel(Constants.defaultAdLogLevel);
   	 
        // NOT calling update here;
        //adserverView.update();
        
        return adserverView;
	}
	
	@Override
	public MASTAdView createInterstitialAd(Object site, Object zone, Object width, Object height)
	{
    	 MASTAdView adserverView = new MASTAdView(parentActivity, (Integer)site, (Integer)zone);
    	 adserverView.setBackgroundColor(Color.BLACK);
         adserverView.setAutoCloseInterstitialTime(Constants.interstitial_auto_close_time);
         
 		 // set ad view size; perform layout
         adserverView.setLayoutParams(
        	       new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                           					  ViewGroup.LayoutParams.FILL_PARENT));
         adserverView.requestLayout();
         
         /* Create custom close button with appearance matching our app buttons
         Button closeButton = new Button(this);
         closeButton.setText("Close");
         closeButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_button_background));
         RelativeLayout.LayoutParams closeLayoutParams =
        	 new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
         closeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
         closeLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
         closeButton.setLayoutParams(closeLayoutParams);
         adserverView.setCloseButton(closeButton);
         */
         adserverView.setShowCloseButtonTime(Constants.interstital_show_close_delay);
         adserverView.setLogLevel(Constants.defaultAdLogLevel);
         //adserverView.setInjectionBodyCode(FlickrViewerActivity.injectionString);
         
         // NOT calling update here;
         //adserverView.update();
         
         return adserverView;
	}
}
