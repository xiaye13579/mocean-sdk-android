package com.moceanmobile.mast.samples;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class AdvancedTopAndBottom extends RefreshActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advanced_top_and_bottom);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.refresh_top_and_bottom_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
	    switch (item.getItemId()) 
	    {
	        case R.id.action_refresh_top:
	        	showRefreshPrompt(R.id.adViewTop);
	            return true;
	            
	        case R.id.action_refresh_bottom:
	        	showRefreshPrompt(R.id.adViewBottom);
	            return true;

	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
