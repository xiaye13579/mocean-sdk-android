package com.moceanmobile.flickrviewer.AdInterfaces;


import java.util.HashMap;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.google.ads.Ad;
import com.vdopia.client.android.VDO;
import com.vdopia.client.android.VDOAdObject;
import com.vdopia.client.android.VDOBannerView;
import com.vdopia.client.android.VDO.AdEventListener;



//
// NOTE: This is a untested.
//



public class iVdopiaAdInterface extends BaseAdInterface implements AdEventListener
{
	private static boolean initialized = false;
	
	
	public iVdopiaAdInterface(Activity activity)
	{
		super(activity);
	}
	
	
	static public String getName()
	{
		return "ivdopia";
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public VDOBannerView createBannerAd(Object site, Object data, Object width, Object height, Object location)
	{
		// Data parameter is a hashmap of strings, pull out what we need; the specific
		// contents vary for each third party ad type.
		HashMap<String,String> params = (HashMap<String,String>)data;	
		String publisherId = params.get("publisherid");
		String campaignId = params.get("campaignId");
		
		setLastCampaign(campaignId);
		
		// Initialize ad engine
		initialize();
		
		return createBannerInternal((Integer)location, (String)width);
	}
	
	private void initialize()
	{
		if (!initialized)
		{
			// XXX AX123 should be our registered key below, when we receive it
			VDO.initialize("AX123", parentActivity);
			VDO.setListener(this);
			initialized = true;
		}
	}
	
	private VDOBannerView createBannerInternal(int location, String size)
	{
		String bannerSize = new String(size);
		VDOBannerView banObject = new VDOBannerView(parentActivity, bannerSize, location);
		if (banObject == null) 
		{
		    System.out.println("No banner of the requested size found");
		    return null;
		}

		RelativeLayout.LayoutParams p =
			(RelativeLayout.LayoutParams)banObject.getLayoutParams();
		if(location == 1)
			p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		else
			p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                
		banObject.setLayoutParams(p);

		return banObject;
	}
	
	
	//
	// Ad Event Listener interface
	//
	
	
	public void displayedBanner(VDOBannerView object)
	{
		//System.out.println("iVdopia: dislay banner ad");
	}
	
    public void noBanner(VDOBannerView object)
    {
		System.out.println("iVdopia: no banner ad");
		notifyAdLoadFailed();
	}
    
    public void playedInApp(VDOAdObject object)
    {
		//System.out.println("iVdopia: played in app");
	}
    
    public void playedPreApp(VDOAdObject object)
    {
		// System.out.println("iVdopia: played pre-app");
	}
    
    public void noInApp(VDOAdObject object)
    {
		System.out.println("iVdopia: no in-app");
		notifyAdLoadFailed();
	}
    
    public void noPreApp(VDOAdObject object)
    {
		// System.out.println("iVdopia: no pre-app");
	}
    
    public void bannerTapStarted(VDOBannerView object)
    {
		// System.out.println("iVdopia: banner tap start");
	}
    
    public void bannerTapEnded(VDOBannerView object)
    {
		System.out.println("iVdopia: banner tap end");
    	notifyAdClosed(); // XXX right?
	}
    
    public void interstitialWillShow(VDOAdObject object)
    {
		// System.out.println("iVdopia: interstitial will show");
	}
    
    public void interstitialDidDismiss(VDOAdObject object)
    {
		// System.out.println("iVdopia: interstitial dismissed");
	}    
}
