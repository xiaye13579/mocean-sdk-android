package com.adserver.adview.bridges;

import com.adserver.adview.AdLog;

import android.content.Context;
import android.webkit.WebView;

public class AdBridgeFactory {
	
	public final static AdBridgeAbstract CreateBridge(Context context, WebView view,AdLog AdLog, 
			String campaignId,String type, String externalParams,String trackUrl)
	{
		if(type.equals("iVdopia")) return new AdBridgeiVdopia(context, view, AdLog, campaignId, externalParams,trackUrl);
		if(type.equals("Millennial")) return new AdBridgeMillennial(context, view, AdLog, campaignId, externalParams,trackUrl);			
		if(type.equals("SmartAdServer")) return new AdBridgeSAS(context, view, AdLog, campaignId, externalParams,trackUrl);
		//if(type.equals("admob")) return new AdBridgeAdMob(context, view, AdLog,campaignId, externalParams,trackUrl);
		if(type.equals("GreyStripe")) return new AdBridgeGreyStripe(context, view, AdLog, campaignId, externalParams,trackUrl);
		if(type.equals("Medialets")) return new AdBridgeMedialets(context, view,AdLog, campaignId, externalParams,trackUrl);		
		return null;		
	}
}
