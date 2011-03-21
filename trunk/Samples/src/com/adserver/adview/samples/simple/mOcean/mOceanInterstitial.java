/*© 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.*/
package com.adserver.adview.samples.simple.mOcean;

import java.nio.Buffer;
import java.util.Hashtable;

import com.adserver.adview.AdServerInterstitialView;
import com.adserver.adview.AdServerView;
import com.adserver.adview.samples.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class mOceanInterstitial extends Activity {
    /** Called when the activity is first created. */
	private Context context;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        setContentView(R.layout.main_advanced);
        context = this;
        
        Button interstitialAd = (Button) findViewById(R.id.interstitialAd);
        interstitialAd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AdServerInterstitialView adS = new AdServerInterstitialView(context, "8061", "16112");
				adS.setMinSizeX(320);
				adS.setMinSizeY(320);
				adS.show();		       
			}
        });

    }
}