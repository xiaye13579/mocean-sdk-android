/*© 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.*/
package com.adserver.adview.samples.simple.Millennial;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.adserver.adview.AdServerView;
import com.adserver.adview.samples.ApiDemos;
import com.adserver.adview.samples.R;

public class MillennialAd extends Activity {
    /** Called when the activity is first created. */
	private Context context;
	private LinearLayout linearLayout;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        context = this;
        linearLayout = (LinearLayout) findViewById(R.id.frameAdContent);
        
         
        AdServerView adserverView = new AdServerView(this,"8061","16938");
        adserverView.setId(1);
        adserverView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ApiDemos.BANNER_HEIGHT));
		linearLayout.addView(adserverView);
    }
}