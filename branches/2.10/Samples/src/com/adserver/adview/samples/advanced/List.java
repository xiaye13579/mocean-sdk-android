package com.adserver.adview.samples.advanced;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.adserver.adview.AdServerView;
import com.adserver.adview.samples.ApiDemos;
import com.adserver.adview.samples.R;

public class List extends Activity {
    /** Called when the activity is first created. */
	private Context context;
	private LinearLayout linearLayout;
	
	int baners[]=
	{
			20249,//Always working mOcean ad
			17490,// Sample site-zone with HTML ad 
			17487,// Sample site-zone with ORMMA level1 ad 
			17488,// Sample site-zone with ORMMA level2 ad 
			17489,// Sample site-zone with ORMMA level3 ad 
	};
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main_all);
        context = this;
        linearLayout = (LinearLayout) findViewById(R.id.frameAdContent);
        
        
        for(int x=0;x<baners.length;x++)
        {
        	AdServerView adserverView = new AdServerView(this,8061,baners[x]);
        	adserverView.setUpdateTime(3000);
            adserverView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ApiDemos.BANNER_HEIGHT));
            linearLayout.addView(adserverView);
        }
        
        /*AdServerView adserverView = new AdServerView(this,"8061","20249");
        
        adserverView.setId(1);
        adserverView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 56));
        linearLayout.addView(adserverView);*/
    }
}