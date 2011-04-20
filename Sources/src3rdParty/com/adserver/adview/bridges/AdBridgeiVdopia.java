package com.adserver.adview.bridges;

import com.adserver.adview.Utils;
import com.vdopia.client.android.VDO;
import com.vdopia.client.android.VDOView;

import android.content.Context;
import android.graphics.Color;
import android.webkit.WebView;

public class AdBridgeiVdopia extends AdBridgeAbstract {

	public AdBridgeiVdopia(Context context, WebView view, String campaignId,
			String externalParams,String trackUrl) {
		super(context, view, campaignId, externalParams, trackUrl);
	}

	public void run() {
		try {
			String applicationKey = Utils.scrape(externalParams, "<param name=\"applicationKey\">", "</param>");
			VDOView iVdopiaView = new VDOView(context);
			iVdopiaView.setLayoutParams(view.getLayoutParams());
	        VDO.initialize(applicationKey, context);
	        VDO.setListener(new VdopiaEventListener());
	        view.addView(iVdopiaView);
			view.setBackgroundColor(Color.WHITE);
			view.loadDataWithBaseURL(null, "", "text/html", "UTF-8", null);
		} catch (Exception e) {
		}
	}
	
	private class VdopiaEventListener implements VDO.AdEventListener {
			
		public void adShown(int type) {
			Click();
		}
		
		public void noAdsAvailable(int type, int willCheckAgainAfterSeconds) {
			//excampaigns.add(campaignId);
			//DownloadError(context.getString(R.string.ivdopia_download_error));
		}
	}

}
