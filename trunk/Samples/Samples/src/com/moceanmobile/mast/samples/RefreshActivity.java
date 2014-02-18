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
