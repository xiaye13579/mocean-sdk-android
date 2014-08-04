/*
 * PubMatic Inc. (“PubMatic”) CONFIDENTIAL
 * Unpublished Copyright (c) 2006-2014 PubMatic, All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of PubMatic. The intellectual and technical concepts contained
 * herein are proprietary to PubMatic and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from PubMatic.  Access to the source code contained herein is hereby forbidden to anyone except current PubMatic employees, managers or contractors who have executed 
 * Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure  of  this source code, which includes  
 * information that is confidential and/or proprietary, and is a trade secret, of  PubMatic.   ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC  PERFORMANCE, 
 * OR PUBLIC DISPLAY OF OR THROUGH USE  OF THIS  SOURCE CODE  WITHOUT  THE EXPRESS WRITTEN CONSENT OF PubMatic IS STRICTLY PROHIBITED, AND IN VIOLATION OF APPLICABLE 
 * LAWS AND INTERNATIONAL TREATIES.  THE RECEIPT OR POSSESSION OF  THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS  
 * TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE, USE, OR SELL ANYTHING THAT IT  MAY DESCRIBE, IN WHOLE OR IN PART.                
 */

package com.moceanmobile.mast.mraid;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Formatter;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import com.moceanmobile.mast.Defaults;


public class WebView extends android.webkit.WebView
{
	private static String MRAID_JAVASCRIPT_INTERFACE_NAME = "MASTMRAIDWebView";

	private boolean hasAPI11 = false;
	private Handler handler = null;
	private boolean loaded = false;
	
	// For API10 and lower a string, for API11 and higher an InputStream
	private Object mraidBridgeJavascript = null;
	
	//@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	public WebView(Context context)
	{
		super(context);
		
		try
		{
			WebViewClient.class.getMethod("shouldInterceptRequest", 
					new Class[] {android.webkit.WebView.class, String.class});
			
			hasAPI11 = true;
			setWebViewClient(new ViewClientAPI11());
		}
		catch (NoSuchMethodException exception)
		{
			setWebViewClient(new ViewClientAPI8());	
		}
		
		setWebChromeClient(new ChromeClient());
		getSettings().setJavaScriptEnabled(true);
		getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		//getSettings().setPluginsEnabled(true);  // may be needed for inline video
		
		setOnTouchListener(new TouchListener());
		
		setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
	}
	
	public void setHandler(Handler handler)
	{
		this.handler = handler;
	}
	
	public void loadUrl(String url, Bridge bridge)
	{
		addJavascriptInterface(bridge, MRAID_JAVASCRIPT_INTERFACE_NAME);

		super.loadUrl(url);
	}
	
	public void loadFragment(String fragment, Bridge bridge)
	{
		addJavascriptInterface(bridge, MRAID_JAVASCRIPT_INTERFACE_NAME);
		
		String content = null;
		Formatter formatter = new Formatter(Locale.US);
		if (hasAPI11)
		{	
			formatter.format(Defaults.RICHMEDIA_FORMAT_API11, fragment);
		}
		else
		{
			formatter.format(Defaults.RICHMEDIA_FORMAT, mraidBridgeJavascript, fragment);
		}
		content = formatter.toString();
		formatter.close();
		
		loadDataWithBaseURL("mast://ad/", content, "text/html", "UTF-8", null);
	}
	
	public void injectJavascript(String script)
	{
		final String url = "javascript:" + script;
		
		Context ctx = getContext();
		if (ctx instanceof Activity)
		{
			Activity activity = (Activity) ctx;
			activity.runOnUiThread(new Runnable()
			{
				public void run()
				{
					loadUrl(url);					
				}
			});
		}
	}
	
	public boolean isLoaded()
	{
		return loaded;
	}

	private class TouchListener implements View.OnTouchListener
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			switch (event.getAction())
			{
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_UP:
				if (v.hasFocus() == false)
				{
					v.requestFocus();
				}
				break;
				
			default:
				break;
			}
			
			return false;
		}
	}
	
	private class ViewClientAPI8 extends WebViewClient
	{
		public ViewClientAPI8()
		{
			initJavascriptBridge();
		}
		
		protected void initJavascriptBridge()
		{
			if (mraidBridgeJavascript == null)
			{
				try
				{
					InputStream is = WebView.class.getResourceAsStream("/MASTMRAIDController.js");
					BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 16384);
					StringBuilder sb = new StringBuilder();
					char buffer[] = new char[4096];
					while (true)
					{
						int count = br.read(buffer);
						if (count == -1)
							break;
						sb.append(buffer, 0, count);
					}
					mraidBridgeJavascript = sb.toString();
				}
				catch (Exception ex)
				{
					// TODO: Log this?
				}
			}
		}
		
		@Override
		public void onPageStarted(android.webkit.WebView view, String url, Bitmap favicon)
		{
			super.onPageStarted(view, url, favicon);
			
			loaded = false;
			
			if (handler != null)
				handler.webViewPageStarted((WebView) view);
		}
		
		@Override
		public void onPageFinished(android.webkit.WebView view, String url)
		{
			super.onPageFinished(view, url);
			
			loaded = true;
			
			if (handler != null)
				handler.webViewPageFinished((WebView) view);
			
			view.setFocusableInTouchMode(true);
		}
		
		@Override
		public void onReceivedError(android.webkit.WebView view, int errorCode, String description, String failingUrl) 
		{
			super.onReceivedError(view, errorCode, description, failingUrl);
			
			if (handler != null)
				handler.webViewReceivedError((WebView) view, errorCode, description, failingUrl);
		}
		
		@Override
		public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url)
		{
			boolean override = false;
			
			if (handler != null)
				override = handler.webViewshouldOverrideUrlLoading((WebView) view, url);
			
			return override;
		}
	}
	
	private class ViewClientAPI11 extends ViewClientAPI8
	{
		public ViewClientAPI11()
		{
			super();
		}
		
		@Override
		protected void initJavascriptBridge()
		{
			InputStream is = WebView.class.getResourceAsStream("/MASTMRAIDController.js");
			mraidBridgeJavascript = is;
		}
		
		@Override
		public WebResourceResponse shouldInterceptRequest(android.webkit.WebView webView, String url)
		{
			WebResourceResponse response = null;

			if ((TextUtils.isEmpty(url) == false) && url.endsWith("mraid.js"))
			{
				response = 
						new WebResourceResponse("text/javascript", "UTF-8", (InputStream) mraidBridgeJavascript);
			}
			
			return response;
		}
	}
	
	private class ChromeClient extends WebChromeClient
	{
		
	}

	public interface Handler
	{
		public void webViewPageStarted(WebView webView);
		public void webViewPageFinished(WebView webView);
		public void webViewReceivedError(WebView webView, int errorCode, String description, String failingUrl);
		public boolean webViewshouldOverrideUrlLoading(WebView view, String url);
	}
}
