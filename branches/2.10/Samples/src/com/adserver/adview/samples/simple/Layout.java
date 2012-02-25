package com.adserver.adview.samples.simple;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

import com.adserver.adview.samples.R;

public class Layout extends Activity {
    /** Called when the activity is first created. */
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main_layout);        
    }
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
    
}