package com.moceanmobile.flickrviewer.AdInterfaces;

import java.util.HashMap;

import com.vdopia.client.android.VDOBannerView;

import android.app.Activity;
import android.view.View;



//
// NOTE: THis is a stub, greystripe integration has not been completed at this time.
//



public class GreystripeAdInterface extends BaseAdInterface
{

	public GreystripeAdInterface(Activity activity)
	{
		super(activity);
	}


	static public String getName()
	{
		return "greystripe";
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public View createBannerAd(Object site, Object data, Object width, Object height, Object location)
	{
		// Data parameter is a hashmap of strings, pull out what we need; the specific
		// contents vary for each third party ad type.
		HashMap<String,String> params = (HashMap<String,String>)data;	
		String publisherId = params.get("publisherid"); // XXX
		String campaignId = params.get("campaignId");
		
		// XXX extract key from custom parameter
		
		setLastCampaign(campaignId);
		
		return createBannerInternal(); // XXX what parameters?
	}
	
	private View createBannerInternal()
	{
	
		return null;
		
	}
}
