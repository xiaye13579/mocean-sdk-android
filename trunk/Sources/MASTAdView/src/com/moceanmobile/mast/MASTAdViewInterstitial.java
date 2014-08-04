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

package com.moceanmobile.mast;

import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Helper class to allow placing placing an interstitial ad in an XML layout.  As such this class
 * is only intended to be used in XML layouts and the resulting instance should just be casted
 * to the base MASTAdView class.
 * <p>
 * Note this must be added with a width/height of 0 and a visibility state of INVISIBLE.  Do not
 * attempt to present this view directly as a normal view.
 * <p>
 * When the ad is loaded with a set zone the ad will request an update and display the interstitial
 * after it has been downloaded and rendered.
*/
public class MASTAdViewInterstitial extends MASTAdView implements MASTAdViewDelegate.RequestListener
{
	public MASTAdViewInterstitial(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		super.applyAttributeSet(attrs);
		init(true);
	}
	
	public MASTAdViewInterstitial(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		super.applyAttributeSet(attrs);
		init(true);
	}
	
	@Override
	protected void init(boolean interstitial)
	{
		super.init(interstitial);
		
		setRequestListener(this);
	}

	@Override
	public void onFailedToReceiveAd(MASTAdView adView, Exception ex)
	{

	}

	@Override
	public void onReceivedAd(MASTAdView adView)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				showInterstitial();
			}
		});
	}

	@Override
	public void onReceivedThirdPartyRequest(MASTAdView adView,
			Map<String, String> properties, Map<String, String> parameters)
	{
		
	}
}
