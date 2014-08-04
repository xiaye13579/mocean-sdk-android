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
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.moceanmobile.mast.MASTAdView;

public class DataActivity extends Activity
{
	// How many swipes to display ad.
	static private final int AD_INTERVAL = 3;
	
	static private int dataDisplayCount = 0;
	
	// Ad this activity is currently consuming, if any.
	private View adView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		++dataDisplayCount;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data);
		
		TextView dataText = (TextView) findViewById(R.id.data_text);
		dataText.setText(SwipeData.getInstance().getCurrent().toString());
		
		Button previousButton = (Button) findViewById(R.id.previous_button);
		if (SwipeData.getInstance().hasPrevious() == false)
			previousButton.setEnabled(false);
		
		Button forwardButton = (Button) findViewById(R.id.forward_button);
		if (SwipeData.getInstance().hasNext() == false)
			forwardButton.setEnabled(false);
		
		Button doneButton = (Button) findViewById(R.id.done_button);
		
		previousButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				SwipeData.getInstance().previous();
				
				Intent intent = new Intent(DataActivity.this, DataActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
			}			
		});
		
		forwardButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				SwipeData.getInstance().next();
				
				Intent intent = new Intent(DataActivity.this, DataActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
			}			
		});
		
		doneButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				DataActivity.this.finish();
			}			
		});
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		if ((dataDisplayCount % AD_INTERVAL) > 0)
			return;
		
		adView = AdQueue.getInstance().checkoutAd();
		
		if (adView == null)
			return;

		// The dialog can be custom and control when users can close it.
		Dialog adDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		adDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		adDialog.setContentView(adView);
		
		adDialog.show();
	}
	
	@Override
	protected void onDestroy()
	{
		if (adView != null)
		{
			ViewGroup adParent = (ViewGroup) adView.getParent();
			if (adParent != null)
				adParent.removeView(adView);
			
			AdQueue.getInstance().returnAd((MASTAdView) adView);
			
			adView = null;
		}
		
		super.onDestroy();
	}
}
