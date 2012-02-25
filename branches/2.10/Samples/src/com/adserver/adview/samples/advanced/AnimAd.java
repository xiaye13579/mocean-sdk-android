package com.adserver.adview.samples.advanced;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.adserver.adview.MASTAdServerView;
import com.adserver.adview.MASTAdServerViewCore.MASTOnAdDownload;
import com.adserver.adview.samples.ApiDemos;
import com.adserver.adview.samples.R;

public class AnimAd extends Activity {
    /** Called when the activity is first created. */
	private Context context;
	private LinearLayout linearLayout;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.animation_ad);
        context = this;
        linearLayout = (LinearLayout) findViewById(R.id.frameAdContent);
         
        final MASTAdServerView adserverView1 = new MASTAdServerView(this,8061,20249);
        
        adserverView1.setId(1);
        adserverView1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ApiDemos.BANNER_HEIGHT));
        
        Animation animation = new TranslateAnimation(
	            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
	            Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, -1.0f
	        );
        animation.setDuration(Integer.MAX_VALUE);
        animation.setFillAfter(true);
		adserverView1.startAnimation(animation);		
        
        adserverView1.setOnAdDownload(new MASTOnAdDownload() {
			@Override
			public void error(MASTAdServerView sender, String arg0) {
				
			}
			
			@Override
			public void end(MASTAdServerView sender) {
				Animation animation = new TranslateAnimation(
			            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
			            Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
			        );
				animation.setDuration(2000);
				adserverView1.startAnimation(animation);	
			}
			
			@Override
			public void begin(MASTAdServerView sender) {
				
			}
		});
        
        linearLayout.addView(adserverView1);         
    }
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
    
}