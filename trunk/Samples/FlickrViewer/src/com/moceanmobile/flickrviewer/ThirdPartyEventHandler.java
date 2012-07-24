package com.moceanmobile.flickrviewer;


import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import android.app.Activity;
import android.os.Handler;
import android.view.View;

import com.MASTAdView.MASTAdView;
import com.google.ads.AdSize;
import com.millennialmedia.android.MMAdView;
import com.moceanmobile.flickrviewer.AdInterfaces.AdmobAdInterface;
import com.moceanmobile.flickrviewer.AdInterfaces.BaseAdInterface;
import com.moceanmobile.flickrviewer.AdInterfaces.MillenialAdInterface;
import com.moceanmobile.flickrviewer.AdInterfaces.iVdopiaAdInterface;
import com.vdopia.client.android.VDO;


public class ThirdPartyEventHandler implements MASTAdView.MASTOnThirdPartyRequest
{ 
	private Activity parentActivity;
	private Handler parentHandler;
	private BaseAdInterface thirdPartyAd = null;
	private View thirdPartyAdView = null;
	//private long thirdPartyAdStartTime = 0;
	private Vector excludeThirdPartyCampaigns = null;	
	private int adLocation = 0;
	
	
	public ThirdPartyEventHandler(Activity parent, Handler handler, int location)
	{
		parentActivity = parent;
		parentHandler = handler;
		adLocation = location;
	}
	
	
    // mOcean SDK third party ad callback event() method:
    // param map will always include standard "type", "campaign" and "track_url"
    // parameters plus optional custom parameters.
    public void event(MASTAdView sender, final HashMap<String,String> params)
    {
    	System.out.println("on third party ad / event() called");
    	
    	
    	// Debug: spit out all parameters...
    	String key;
    	String value;
    	Iterator paramIterator = params.keySet().iterator();
    	while (paramIterator.hasNext())
    	{
    		key = (String)paramIterator.next();
    		value = (String)params.get(key);
    		System.out.println("parameter: key=" + key + ", value=" + value);
    	}
    	
    	String type = params.get("type");
    	String campaign = params.get("campaign");
    	String track_uurl = params.get("track_url");
    	
    	if ((type != null) && (type.equalsIgnoreCase(AdmobAdInterface.getName())))
    	{
    		System.out.println("Creating third party ad with admob");
    	
    		// Show the ad, make sure to use UI thread;
    		// This assumes the ad component performs long running tasks on a different thread!
        	parentHandler.post(new Runnable()
    		{
    			public void run()
    			{
    				thirdPartyAd = createAdmobInterface();
    	    		thirdPartyAdView = thirdPartyAd.createBannerAd(null, params, AdSize.BANNER, null, null);
    				insertThirdPartyAdView(thirdPartyAdView);
    			}
    		});     		
    	}
    	else if ((type != null) && (type.equalsIgnoreCase(iVdopiaAdInterface.getName())))
    	{
    		System.out.println("Creating third party ad with ivdopia");
    	
    		// Show the ad, make sure to use UI thread;
    		// This assumes the ad component performs long running tasks on a different thread!
        	parentHandler.post(new Runnable()
    		{
    			public void run()
    			{
    				thirdPartyAd = createiVdopiaInterface();
    				
    				
    				
    				// XXX Untested; need to use correct parameters here
    				String adSize = new String(VDO.MINI_VDO_BANNER);
    				if (adLocation == Constants.banner_location_bottom)
    				{
    					thirdPartyAdView = thirdPartyAd.createBannerAd(null, params, adSize, null, 1);
    				}
    				else
    				{
    					thirdPartyAdView = thirdPartyAd.createBannerAd(null, params, adSize, null, 0);
    				}
    				
    				
    				
    				insertThirdPartyAdView(thirdPartyAdView);
    			}
    		});     		
    	}
    	else if ((type != null) && (type.equalsIgnoreCase(MillenialAdInterface.getName())))
    	{
    		System.out.println("Creating third party ad with millenial");
    	
    		// Show the ad, make sure to use UI thread;
    		// This assumes the ad component performs long running tasks on a different thread!
        	parentHandler.post(new Runnable()
    		{
    			public void run()
    			{
    				thirdPartyAd = createMillenialInterface();
    				
    				if (adLocation == Constants.banner_location_bottom)
    				{
    					thirdPartyAdView = thirdPartyAd.createBannerAd(null, params, null, null, MMAdView.BANNER_AD_BOTTOM);
    				}
    				else
    				{
    					thirdPartyAdView = thirdPartyAd.createBannerAd(null, params, null, null, MMAdView.BANNER_AD_TOP);
    				}
    				
    				insertThirdPartyAdView(thirdPartyAdView);
    			}
    		});     		
    	}
    	else
    	{
    		System.out.println("Unknown third party ad type... skipping");
    		thirdPartyAdView = null;
    		thirdPartyAd = null;
    		
    		adLoadFailed(); // XXX ???
    		
    		return;
    	}
    }
    
    
    public View getAdView()
    {
    	return thirdPartyAdView;
    }

    
    public void destroyAdView()
    {
    	if ((thirdPartyAd != null) && (thirdPartyAdView != null))
    	{
    		thirdPartyAd.destroy(thirdPartyAdView);
    		thirdPartyAdView = null;
    		thirdPartyAd = null;
		}
    }
    

    // Set ad request exclude campaign value
    public void setExcludeCampaigns(MASTAdView adView)
    {
    	if (excludeThirdPartyCampaigns != null)
    	{
    		StringBuilder sb = new StringBuilder();
    		Iterator i = excludeThirdPartyCampaigns.iterator();
    		while (i.hasNext())
    		{
    			sb.append((String)i.next());
    			sb.append(",");
    		}
    		
    		Hashtable<String,String> customs = new Hashtable<String,String>();
    		customs.put("excampaigns", sb.toString());
    		adView.setCustomParameters(customs);
    	}
    	else
    	{
    		adView.setCustomParameters(null);
    	}
    }
    
    
    public Vector getExcludeThirdPartyCampaigns()
    {
    	return excludeThirdPartyCampaigns;
    }
    
    
    public void setExcludeThirdPartyCampains(Vector v)
    {
    	excludeThirdPartyCampaigns = v;
    }
    
     
    // If loading an ad failed, remember that campaign and tell ad server not to use it again
    public void campaignFailed()
    {
    	if (thirdPartyAd != null)
		{
			String failedCampaign = thirdPartyAd.getLastCampaign();
			if (failedCampaign != null)
			{
				if (excludeThirdPartyCampaigns == null)
				{
					excludeThirdPartyCampaigns = new Vector();
				}
				
				excludeThirdPartyCampaigns.add(failedCampaign);
				System.out.println("Ecluding failed third party campaign: " + failedCampaign);
			}
		}
    }

    
    //
    // Admob
    //
    

    private BaseAdInterface createAdmobInterface()
    {
    	return new AdmobAdInterface(parentActivity)
		{
			public void notifyAdLoadFailed()
			{
				adLoadFailed();
			}
			
			public void notifyAdClosed()
			{
				adClosed();
			}
			
			public void notifyAdTimedOut()
			{
				adTimedOut();
			}
		};
    }
   
   
    //
    // iVdopia
    //
    
    private BaseAdInterface createiVdopiaInterface()
    {
    	return new iVdopiaAdInterface(parentActivity)
		{
			public void notifyAdLoadFailed()
			{
				adLoadFailed();
			}
			
			public void notifyAdClosed()
			{
				adClosed();
			}
			
			public void notifyAdTimedOut()
			{
				adTimedOut();
			}
		};
    }
    
    
    //
    // Millenial
    //
    
    private BaseAdInterface createMillenialInterface()
    {
    	return new MillenialAdInterface(parentActivity)
		{
			public void notifyAdLoadFailed()
			{
				adLoadFailed();
			}
			
			public void notifyAdClosed()
			{
				adClosed();
			}
			
			public void notifyAdTimedOut()
			{
				adTimedOut();
			}
		};
    }
    
    
    //
    // Parent should override these
    //
    
    
    public void adLoadFailed()
    {
    	// Override, but call super(), or invoke this public method as needed
    	campaignFailed();
    }
    
    public void adClosed()
    {
    	// Override
    }
    
    public void insertThirdPartyAdView(View v)
    {
    	// Override
    }
    
    public void adTimedOut()
    {
    	// Override
    }
}
