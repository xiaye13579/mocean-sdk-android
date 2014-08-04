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

package com.moceanmobile.mast.samples;

import java.util.Date;
import java.util.Map;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.moceanmobile.mast.MASTAdView;
import com.moceanmobile.mast.MASTAdViewDelegate;

public class DelegateGeneric extends RefreshActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delegate_generic);
		
		TextView textView = (TextView) findViewById(R.id.textView);
		textView.setMovementMethod(new ScrollingMovementMethod());
		
		MASTAdView adView = (MASTAdView) findViewById(R.id.adView);
		adView.setRequestListener(new AdRequestListener());
		adView.setActivityListener(new AdActivityListener());
		adView.setInternalBrowserListener(new AdInternalBrowserListener());
		adView.setRichMediaListener(new AdRichMediaListener());
		adView.setFeatureSupportHandler(new AdFeatureSupportHandler());
		
		// Use the DelegateLogging to see the logging listener.  Not included in the base to avoid possible debug spam.
	}

	protected void appendOutput(final String content)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				TextView textView = (TextView) findViewById(R.id.textView);
				textView.setText(new Date().toString() + "\n" + 
						content + "\n\n" + textView.getText());
			}
		});
	}

	private class AdRequestListener implements MASTAdViewDelegate.RequestListener
	{
		@Override
		public void onFailedToReceiveAd(MASTAdView adView, Exception ex)
		{
			appendOutput("onFailedToReceiveAd Exception:" + ex);
		}

		@Override
		public void onReceivedAd(MASTAdView adView)
		{
			appendOutput("onReceivedAd");
		}

		@Override
		public void onReceivedThirdPartyRequest(MASTAdView adView,
				Map<String, String> properties, Map<String, String> parameters)
		{
			appendOutput("onReceivedThirdPartyRequest properties:" + properties + " paramters:" + parameters);
		}
	}
	
	private class AdActivityListener implements MASTAdViewDelegate.ActivityListener
	{

		@Override
		public boolean onOpenUrl(MASTAdView adView, String url)
		{
			appendOutput("onOpenUrl url:" + url);
			return false;
		}

		@Override
		public void onLeavingApplication(MASTAdView adView)
		{
			appendOutput("onLeavingApplication");
		}

		@Override
		public boolean onCloseButtonClick(MASTAdView adView)
		{
			appendOutput("onCloseButtonClick");
			return false;
		}		
	}
	
	private class AdInternalBrowserListener implements MASTAdViewDelegate.InternalBrowserListener
	{
		@Override
		public void onInternalBrowserPresented(MASTAdView adView)
		{
			appendOutput("onInternalBrowserPresented");
		}

		@Override
		public void onInternalBrowserDismissed(MASTAdView adView)
		{
			appendOutput("onInternalBrowserDismissed");
		}
	}
	
	private class AdRichMediaListener implements MASTAdViewDelegate.RichMediaListener
	{
		@Override
		public void onExpanded(MASTAdView adView)
		{
			appendOutput("onExpanded");
		}

		@Override
		public void onResized(MASTAdView adView, Rect area)
		{
			appendOutput("onResized rect:" + area);
		}

		@Override
		public void onCollapsed(MASTAdView adView)
		{
			appendOutput("onCollapsed");
		}

		@Override
		public boolean onPlayVideo(MASTAdView adView, String url)
		{
			appendOutput("onPlayVideo url:" + url);
			return true;
		}

		@Override
		public void onEventProcessed(MASTAdView adView, String request)
		{
			appendOutput("onEventProcessed request:" + request);
		}
	}
	
	private class AdFeatureSupportHandler implements MASTAdViewDelegate.FeatureSupportHandler
	{
		@Override
		public Boolean shouldSupportSMS(MASTAdView adView)
		{
			appendOutput("shouldSupportSMS");
			return null;
		}

		@Override
		public Boolean shouldSupportPhone(MASTAdView adView)
		{
			appendOutput("shouldSupportPhone");
			return null;
		}

		@Override
		public Boolean shouldSupportCalendar(MASTAdView adView)
		{
			appendOutput("shouldSupportCalendar");
			return null;
		}

		@Override
		public Boolean shouldSupportStorePicture(MASTAdView adView)
		{
			appendOutput("shouldSupportStorePicture");
			return null;
		}

		@Override
		public boolean shouldStorePicture(MASTAdView sender, String url)
		{
			appendOutput("shouldStorePicture url:" + url);
			return true;
		}

		@Override
		public boolean shouldAddCalendarEntry(MASTAdView sender, String calendarProperties)
		{
			appendOutput("shouldAddCalendarEntry calendarProperties:" + calendarProperties);
			return true;
		}
	}
}
