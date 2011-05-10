package com.adserver.adview.bridges;

import android.content.Context;
import android.webkit.WebView;

public class AdBridgeFactory {
	
	public final static AdBridgeAbstract CreateBridge(Context context, WebView view, 
			String campaignId,String type, String externalParams,String trackUrl)
	{
		if(type.equals("iVdopia")) return new AdBridgeiVdopia(context, view, campaignId, externalParams,trackUrl);
		if(type.equals("Millennial")) return new AdBridgeMillennial(context, view, campaignId, externalParams,trackUrl);			
		if(type.equals("smartadserver")) return new AdBridgeSAS(context, view, campaignId, externalParams,trackUrl);
		//if(type.equals("admob")) return new AdBridgeAdMob(context, view, campaignId, externalParams,trackUrl);
		if(type.equals("GreyStripe")) return new AdBridgeGreyStripe(context, view, campaignId, externalParams,trackUrl);
		//if(type.equals("Medialets")) return new AdBridgeMedialets(context, view, campaignId, externalParams,trackUrl);
		
		return null;		
	}
}
