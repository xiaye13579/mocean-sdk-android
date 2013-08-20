package com.moceanmobile.mast.samples;

import com.moceanmobile.mast.MASTAdView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class SimpleImage extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_image);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
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
	        	showRefreshPrompt();
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	protected void refreshAdViewWithZone(int zone)
	{
		MASTAdView adView = (MASTAdView) findViewById(R.id.adView);
		adView.setZone(zone);
		adView.update();
	}
	
	protected int getAdZone()
	{
		MASTAdView adView = (MASTAdView) findViewById(R.id.adView);
		return adView.getZone();
	}
	
	protected void showRefreshPrompt()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Refresh");

		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.setHint("Zone");
		input.setText(String.valueOf(getAdZone()));
		builder.setView(input);
		
		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{ 
		    @Override
		    public void onClick(DialogInterface dialog, int which)
		    {
		    	try
		    	{
		    		refreshAdViewWithZone(Integer.parseInt(input.getText().toString()));
		    	}
		    	catch (Exception ex)
		    	{
		    		// Don't type non-numbers.
		    	}
		    }
		});
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
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
