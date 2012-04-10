package com.MASTAdView.samples.advanced;

import android.app.Activity;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.MASTAdView.MASTAdView;
import com.MASTAdView.samples.ApiDemos;

public class OpenGL extends Activity {
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        
        LinearLayout base = new LinearLayout(this);
        FrameLayout frame = new FrameLayout(this);
        
        mGLView = new GLSurfaceView(this);
        mGLView.setRenderer(new TriangleRenderer(this));
        frame.addView(mGLView);
        
        MASTAdView adserverView = new MASTAdView(this, 19829, 88269);
     
        // Min size can be useful, but if you don't have ads large enough for all devices, it
		// can result in no ad being shown, so use it sparingly.
        //adserverView.setMinSizeX(metrics.widthPixels);
        //adserverView.setMinSizeY(height);
		
	    adserverView.setMaxSizeX(320);
	    adserverView.setMaxSizeY(50);
        adserverView.setId(1);
        adserverView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ApiDemos.BANNER_HEIGHT));
        frame.addView(adserverView);
        adserverView.setContentAlignment(true);
        adserverView.update();
        base.addView(frame);
        
        setContentView(base);
    }
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
    
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }

    private GLSurfaceView mGLView;
}