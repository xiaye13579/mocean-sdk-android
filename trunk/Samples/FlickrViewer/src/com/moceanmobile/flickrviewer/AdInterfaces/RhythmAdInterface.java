package com.moceanmobile.flickrviewer.AdInterfaces;

import java.util.HashMap;

import android.app.Activity;
import android.view.View;



//
// NOTE: THis is a stub, Rhythm integration has not been completed at this time.
//



public class RhythmAdInterface extends BaseAdInterface
{

	public RhythmAdInterface(Activity activity)
	{
		super(activity);
		// TODO Auto-generated constructor stub
	}

	
	static public String getName()
	{
		return "rhythm";
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
