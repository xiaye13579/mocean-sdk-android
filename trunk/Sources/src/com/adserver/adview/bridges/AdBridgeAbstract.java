package com.adserver.adview.bridges;

import java.io.IOException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.adserver.adview.AdLog;
import com.adserver.adview.AdServerViewCore.OnAdClickListener;
import com.adserver.adview.AdServerViewCore.OnAdDownload;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;

public abstract class AdBridgeAbstract implements Runnable{
		Context context;
		WebView view;
		String campaignId;
		String externalParams;
		String trackUrl;
		private OnAdDownload onAdDownload;
		private OnAdClickListener onAdClickListener;
		private boolean IsDownloadEnd = false;
		private boolean IsDownloadError = false;
	
		public AdBridgeAbstract(Context context, WebView view, String campaignId, String externalParams,String trackUrl)
		{
			this.context=context;
			this.view=view;
			this.campaignId=campaignId;
			this.externalParams=externalParams;		
			this.trackUrl = trackUrl;
		}	
		
		public void OnAdDownload(OnAdDownload adDownload)
		{
			this.onAdDownload = adDownload;
		}
		
		public void OnAdClickListener(OnAdClickListener adClickListener)
		{
			this.onAdClickListener = adClickListener;
		}
		
		void DownloadEnd()
		{
			if(!IsDownloadEnd)
			{	
				IsDownloadEnd =true;
				if(onAdDownload !=null) onAdDownload.end();
			}
		}
		
		void DownloadError(String error)
		{
			if(!IsDownloadError)
			{
				AdLog.log(AdLog.LOG_LEVEL_2, AdLog.LOG_TYPE_ERROR, "3rd Party Download Error", error);
				IsDownloadError =true;
				if(onAdDownload !=null) onAdDownload.error(error);
			}
		}
		
		void Click()
		{
			if((trackUrl != null) && (trackUrl.length() > 0)) {
				try {
					sendGetRequest(trackUrl);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (onAdClickListener != null)
			{
				onAdClickListener.click(trackUrl);
			}
		}
		
		void Failed()
		{
			
		}
		
		private static void sendGetRequest(String url) throws IOException {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			client.execute(get);
		}
		
		void SetOnBeginVisible(View v)
		{
			v.setLayoutParams(view.getLayoutParams());
			//v.setVisibility(View.GONE);
			
		}
		
		void SetOnEndVisible(View v)
		{
			
		}
}
