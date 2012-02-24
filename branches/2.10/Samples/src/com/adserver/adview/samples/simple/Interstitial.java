package com.adserver.adview.samples.simple;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.adserver.adview.AdServerInterstitialView;
import com.adserver.adview.samples.R;

public class Interstitial extends Activity {
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
				AdServerInterstitialView adS = new AdServerInterstitialView(context, 8061, 16112);
				adS.setMinSizeX(320);
				adS.setMinSizeY(320);
				adS.show();		       
			}
        });

        AdServerInterstitialView adS = new AdServerInterstitialView(context, 8061, 16112);
		adS.setMinSizeX(320);
		adS.setMinSizeY(320);
		adS.show();		
    }
}