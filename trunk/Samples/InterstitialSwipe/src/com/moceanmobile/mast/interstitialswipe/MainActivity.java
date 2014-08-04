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

package com.moceanmobile.mast.interstitialswipe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.moceanmobile.mast.MASTAdView;
import com.moceanmobile.mast.MASTAdView.LogLevel;

public class MainActivity extends Activity
{
	// How many ad instances to create.
	static private final int MAX_ADS = 2;
	
	// How many data pages.
	static private final int SWIPE_DATA_PAGES = 20;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		createAds();
		
		Button showDataButton = (Button) findViewById(R.id.show_data_button);
		showDataButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(MainActivity.this, DataActivity.class);
				startActivity(intent);
			}
		});
		
		SwipeData swipeData = SwipeData.getInstance();
		for (int i = 0; i < SWIPE_DATA_PAGES; ++i)
		{
			swipeData.addItem(String.valueOf(i));
		}
		swipeData.next();
	}
	
	@SuppressWarnings("deprecation")
	private void createAds()
	{
		int maxAdWidth = getWindowManager().getDefaultDisplay().getWidth();
		int maxAdHeight = getWindowManager().getDefaultDisplay().getHeight();
		
		for (int i = 0; i < MAX_ADS; ++i)
		{
			MASTAdView adView = new MASTAdView(this);
			adView.getAdRequestParameters().put("size_x", String.valueOf(maxAdWidth));
			adView.getAdRequestParameters().put("size_y", String.valueOf(maxAdHeight));
			adView.setLogLevel(LogLevel.Debug);
			adView.setZone(88269);
			
			AdQueue.getInstance().returnAd(adView);
		}
	}
}
