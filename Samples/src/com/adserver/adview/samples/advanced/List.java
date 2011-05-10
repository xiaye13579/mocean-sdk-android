/*© 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.*/
package com.adserver.adview.samples.advanced;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.adserver.adview.AdServerView;
import com.adserver.adview.samples.R;

public class List extends Activity {
    /** Called when the activity is first created. */
	private Context context;
	private LinearLayout linearLayout;
	
	String baners[]=
	{
			"20249",//Always working mOcean ad
//			"16109",// Sample site-zone with video ad 
			"16685",// Sample site-zone with Medialets ad
			"16110",// Sample site-zone with iAd ad 
			"16111",// Sample site-zone with Greystripe ad 
			"16938",// Sample site-zone with Millenial ad 
			"18165",// Sample site-zone with iVdopia ad 
			"21636",// Sample site-zone with AdMob ad  
			"21675",// Sample site-zone with SmartAdServer ad 
			"17490",// Sample site-zone with HTML ad 
			"17487",// Sample site-zone with ORMMA level1 ad 
			"17488",// Sample site-zone with ORMMA level2 ad 
			"17489",// Sample site-zone with ORMMA level3 ad 
	};
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main_all);
        context = this;
        linearLayout = (LinearLayout) findViewById(R.id.frameAdContent);
        
        
        for(int x=0;x<baners.length;x++)
        {
        	AdServerView adserverView = new AdServerView(this,"8061",baners[x]);
        	adserverView.setUpdateTime(3000);
            adserverView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 56));
            linearLayout.addView(adserverView);
        }
        
        /*AdServerView adserverView = new AdServerView(this,"8061","20249");
        
        adserverView.setId(1);
        adserverView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 56));
        linearLayout.addView(adserverView);*/
    }
}