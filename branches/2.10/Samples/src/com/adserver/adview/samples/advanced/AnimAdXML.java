package com.adserver.adview.samples.advanced;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;

import com.adserver.adview.MASTAdServerView;
import com.adserver.adview.MASTAdServerViewCore.MASTOnAdDownload;
import com.adserver.adview.samples.R;

public class AnimAdXML extends Activity {
	private EditText inpSite;
	private EditText inpZone;
	private Button btnRefresh;
	private MASTAdServerView adserverView;
	private int site = 0;
	private int zone = 0;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.animation_ad_xml);

        adserverView = (MASTAdServerView) findViewById(R.id.adServerView);
        site = adserverView.getSite();
        zone = adserverView.getZone();
        inpSite = (EditText) findViewById(R.id.inpSite);
        inpSite.setText(String.valueOf(site));
        inpZone = (EditText) findViewById(R.id.inpZone);
        inpZone.setText(String.valueOf(zone));
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					site = Integer.parseInt(inpSite.getText().toString());
			        zone = Integer.parseInt(inpZone.getText().toString());
			        adserverView.setSite(site);
			        adserverView.setZone(zone);
					adserverView.update();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
        
        final Animation animation = 
        	AnimationUtils.loadAnimation(getApplicationContext(),
        	R.animator.ad_in_out);
        
        Animation animation2 = new TranslateAnimation(
	            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
	            Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, -1.0f
	        );
        animation2.setDuration(Integer.MAX_VALUE);
        animation2.setFillAfter(true);
        adserverView.startAnimation(animation2);
        
        adserverView.setOnAdDownload(new MASTOnAdDownload() {
			@Override
			public void error(MASTAdServerView sender,String arg0) {
				
			}
			
			@Override
			public void end(MASTAdServerView sender) {
				adserverView.startAnimation(animation);	
			}
			
			@Override
			public void begin(MASTAdServerView sender) {
				
			}
		});
    }
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
    
}