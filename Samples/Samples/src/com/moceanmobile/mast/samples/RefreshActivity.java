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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.moceanmobile.mast.MASTAdView;

public abstract class RefreshActivity extends Activity 
{
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.refresh_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
	    switch (item.getItemId()) 
	    {
	        case R.id.action_refresh:
	        	refreshAdView(R.id.adView);
	            return true;
	            
	        case R.id.action_zone:
	        	showRefreshPrompt(R.id.adView);
	        	return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	protected void refreshAdViewWithZone(int adViewId, int zone)
	{
		MASTAdView adView = (MASTAdView) findViewById(adViewId);
		adView.setZone(zone);
		adView.update();
	}
	
	protected int getAdZone(int adViewId)
	{
		MASTAdView adView = (MASTAdView) findViewById(adViewId);
		return adView.getZone();
	}
	
	protected void refreshAdView(final int adViewId)
	{
		int zone = getAdZone(adViewId);
		refreshAdViewWithZone(adViewId, zone);
	}
	
	protected void showRefreshPrompt(final int adViewId)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.action_zone);

		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.setHint(R.string.action_zone);
		input.setText(String.valueOf(getAdZone(adViewId)));
		builder.setView(input);
		
		// Set up the buttons
		builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener()
		{
		    @Override
		    public void onClick(DialogInterface dialog, int which)
		    {
		    	try
		    	{
		    		refreshAdViewWithZone(adViewId, Integer.parseInt(input.getText().toString()));
		    	}
		    	catch (Exception ex)
		    	{
		    		// Don't type non-numbers.
		    	}
		    }
		});
		
		builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener()
		{
		    @Override
		    public void onClick(DialogInterface dialog, int which)
		    {
		        dialog.cancel();
		    }
		});

		builder.show();
	}
}
