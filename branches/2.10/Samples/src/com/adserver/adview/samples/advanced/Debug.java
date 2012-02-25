package com.adserver.adview.samples.advanced;

import java.util.Hashtable;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.adserver.adview.MASTAdServerView;
import com.adserver.adview.samples.ApiDemos;
import com.adserver.adview.samples.R;

public class Debug extends Activity {
    /** Called when the activity is first created. */
	private Context context;
	private LinearLayout linearLayout;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        context = this;
        linearLayout = (LinearLayout) findViewById(R.id.frameAdContent);
        
        Hashtable<String, String> customParameters = new Hashtable<String, String>();
        customParameters.put("debug", "1");
                
        MASTAdServerView adserverView = new MASTAdServerView(this,8061,20249);
        //MASTAdServerView sa= new MASTAdServerView()
        adserverView.setCustomParameters(customParameters);
        adserverView.setId(1);
        adserverView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ApiDemos.BANNER_HEIGHT));        
		linearLayout.addView(adserverView);
    }
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
    
}