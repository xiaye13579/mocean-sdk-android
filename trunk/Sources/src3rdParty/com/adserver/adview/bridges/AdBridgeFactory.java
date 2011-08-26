package com.adserver.adview.bridges;

import java.lang.reflect.InvocationTargetException;

import com.adserver.adview.AdLog;

import android.content.Context;
import android.webkit.WebView;
import com.adserver.adview.bridges.AdBridgeAbstract;
import com.google.ads.AdView;

public class AdBridgeFactory {
	
	public final static AdBridgeAbstract CreateBridge(Context context, WebView view,AdLog AdLog, 
			String campaignId,String type, String externalParams,String trackUrl)
	{
		try
		{
			/*if((type.equals("iVdopia"))&&
					IsClassExist("com.vdopia.client.android.VDOView")) return new AdBridgeiVdopia(context, view, AdLog, campaignId, externalParams,trackUrl);
			if((type.equals("Millennial"))&&
					IsClassExist("com.millennialmedia.android.MMAdView")) return new AdBridgeMillennial(context, view, AdLog, campaignId, externalParams,trackUrl);			
			if((type.equals("SmartAdServer"))&&
					IsClassExist("com.smartadserver.library.SmartAdServerAd")) return new AdBridgeSAS(context, view, AdLog, campaignId, externalParams,trackUrl);
			//if(type.equals("admob"))&&(AdBridgeSAS.IsAvailable())) return new AdBridgeAdMob(context, view, AdLog,campaignId, externalParams,trackUrl);
			if((type.equals("GreyStripe"))&&
					IsClassExist("com.greystripe.android.sdk.BannerView")) return new AdBridgeGreyStripe(context, view, AdLog, campaignId, externalParams,trackUrl);
			if((type.equals("Medialets"))&&
					IsClassExist("com.medialets.advertising.AdView")) return new AdBridgeMedialets(context, view,AdLog, campaignId, externalParams,trackUrl);		
			*/
			
			Class obj=null;
			if((type.equalsIgnoreCase("iVdopia"))&&
					IsClassExist("com.vdopia.client.android.VDOView")) obj = Class.forName("com.adserver.adview.bridges.AdBridgeiVdopia");
			if((type.equalsIgnoreCase("Millennial"))&&
					IsClassExist("com.millennialmedia.android.MMAdView")) obj = Class.forName("com.adserver.adview.bridges.AdBridgeMillennial");			
			if((type.equalsIgnoreCase("SmartAdServer"))&&
					IsClassExist("com.smartadserver.library.SmartAdServerAd"))obj = Class.forName("com.adserver.adview.bridges.AdBridgeSAS");
			if((type.equalsIgnoreCase("GreyStripe"))&&
					IsClassExist("com.greystripe.android.sdk.BannerView")) obj = Class.forName("com.adserver.adview.bridges.AdBridgeGreyStripe");
			if((type.equalsIgnoreCase("admob"))&&
					IsClassExist("com.google.ads.AdView")) obj = Class.forName("com.adserver.adview.bridges.AdBridgeAdMob");
		
			if(obj!=null) return (AdBridgeAbstract)obj.getConstructor(new Class[]{Context.class, WebView.class,AdLog.class, String.class,
					String.class, String.class})
				.newInstance(context, view,AdLog, campaignId, externalParams,trackUrl);		
			
			//if(obj!=null)
			//new obj.asSubclass(AdBridgeAbstract.class)();
			
		} catch (Exception e) {
			//AdLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_WARNING, "", msg)
		}
		return null;	
		
	}
	
	static boolean IsClassExist(String className)
	{
		Class obj;
		try {
			obj = Class.forName(className);
		} catch (ClassNotFoundException e) {
			return false;
		}
		return obj!=null;
	}
	
	public static void DeinitObject(Object obj)
	{
		if(obj.getClass().getName().equals("com.google.ads.AdView"))
		{
			try {
				obj.getClass().getDeclaredMethod("destroy").invoke(obj);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			//((AdView)obj).destroy();
		}
	}
}
