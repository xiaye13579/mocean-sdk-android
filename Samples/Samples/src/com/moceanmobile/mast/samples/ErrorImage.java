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

import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.moceanmobile.mast.MASTAdView;
import com.moceanmobile.mast.MASTAdViewDelegate;

public class ErrorImage extends RefreshActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_error_image);
		
		MASTAdView adView = (MASTAdView) findViewById(R.id.adView);
		final ImageView imageView = (ImageView) findViewById(R.id.adPlaceholder);
		
		adView.setRequestListener(new MASTAdViewDelegate.RequestListener()
		{
			@Override
			public void onFailedToReceiveAd(final MASTAdView adView, Exception ex)
			{
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						adView.setVisibility(View.GONE);
						imageView.setVisibility(View.VISIBLE);
					}
				});
			}

			@Override
			public void onReceivedAd(final MASTAdView adView)
			{
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						adView.setVisibility(View.VISIBLE);
						imageView.setVisibility(View.GONE);
					}
				});
			}

			@Override
			public void onReceivedThirdPartyRequest(MASTAdView adView,
					Map<String, String> properties,
					Map<String, String> parameters)
			{
				
			}
		});
	}
}
