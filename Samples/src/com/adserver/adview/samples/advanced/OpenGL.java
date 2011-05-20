/*© 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.*/
package com.adserver.adview.samples.advanced;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.adserver.adview.AdServerView;
import com.adserver.adview.samples.ApiDemos;
import com.adserver.adview.samples.R;

public class OpenGL extends Activity {
    /** Called when the activity is first created. */
	private Context context;
	private LinearLayout linearLayout;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
LinearLayout base = new LinearLayout(this);
        
        FrameLayout frame = new FrameLayout(this);
        
        mGLView = new GLSurfaceView(this);
        mGLView.setRenderer(new TriangleRenderer(this));
        frame.addView(mGLView);
        
        AdServerView adserverView = new AdServerView(this,"8061","20249");
        adserverView.setMinSizeX(320);
	    adserverView.setMinSizeY(50);
	    adserverView.setMaxSizeX(320);
	    adserverView.setMaxSizeY(50);
        adserverView.setId(1);
        adserverView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ApiDemos.BANNER_HEIGHT));
        frame.addView(adserverView);
        
        base.addView(frame);
        
        setContentView(base);
        
        /*setContentView(R.layout.main);
        context = this;
        linearLayout = (LinearLayout) findViewById(R.id.frameAdContent);
        
         
        AdServerView adserverView = new AdServerView(this,"8061","20249");
        adserverView.setId(1);
        adserverView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 50));
		linearLayout.addView(adserverView);*/
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