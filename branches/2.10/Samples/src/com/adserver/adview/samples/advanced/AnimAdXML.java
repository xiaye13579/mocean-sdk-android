package com.adserver.adview.samples.advanced;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.adserver.adview.AdServerViewCore.OnAdDownload;
import com.adserver.adview.MASTAdServerView;
import com.adserver.adview.samples.R;

public class AnimAdXML extends Activity {
    /** Called when the activity is first created. */
	private Context context;
	private LinearLayout linearLayout;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.animation_ad_xml);
        context = this;
        
        final MASTAdServerView adserverView1 = (MASTAdServerView)findViewById(R.id.adServerView);
        final Animation animation = 
        	AnimationUtils.loadAnimation(getApplicationContext(),
        	R.animator.ad_in_out);
        
        Animation animation2 = new TranslateAnimation(
	            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
	            Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, -1.0f
	        );
        animation2.setDuration(Integer.MAX_VALUE);
        animation2.setFillAfter(true);
		adserverView1.startAnimation(animation2);
        
        adserverView1.setOnAdDownload(new OnAdDownload() {
			@Override
			public void error(MASTAdServerView sender,String arg0) {
				
			}
			
			@Override
			public void end(MASTAdServerView sender) {
				adserverView1.startAnimation(animation);	
			}
			
			@Override
			public void begin(MASTAdServerView sender) {
				
			}
		});
        /*linearLayout = (LinearLayout) findViewById(R.id.frameAdContent);
         
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
        
        adserverView1.setOnAdDownload(new OnAdDownload() {
			@Override
			public void error(String arg0) {
				
			}
			
			@Override
			public void end() {
				Animation animation = new TranslateAnimation(
			            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
			            Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
			        );
				animation.setDuration(2000);
				adserverView1.startAnimation(animation);	
			}
			
			@Override
			public void begin() {
				
			}
		});
        
        linearLayout.addView(adserverView1);
        
       /* final MASTAdServerView adserverView2 = new MASTAdServerView(this,8061,20249);
        
        adserverView2.setId(1);
        adserverView2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ApiDemos.BANNER_HEIGHT));
        
        Animation animation2 = new AlphaAnimation(0f, 0f);
        animation2.setDuration(Integer.MAX_VALUE);
        animation2.setFillAfter(true);
		adserverView2.startAnimation(animation2);		
        
		adserverView2.setOnAdDownload(new OnAdDownload() {
			@Override
			public void error(String arg0) {
				
			}
			
			@Override
			public void end() {
				Animation animation = new AlphaAnimation(0.0f, 0.5f);
				animation.setDuration(2000);
				adserverView2.startAnimation(animation);	
			}
			
			@Override
			public void begin() {
				
			}
		});
        
        linearLayout.addView(adserverView2);*/       
    }
}