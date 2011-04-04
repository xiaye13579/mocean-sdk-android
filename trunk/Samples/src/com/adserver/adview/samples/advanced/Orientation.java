package com.adserver.adview.samples.advanced;

import com.adserver.adview.AdServerView;
import com.adserver.adview.samples.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class Orientation extends Activity {
	private Context context;
	private LinearLayout linearLayout;
	private AdServerView adserverView;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        context = this;
        
        linearLayout = (LinearLayout) findViewById(R.id.frameAdContent);
        
        adserverView = new AdServerView(this,"8061","20249");
        adserverView.setId(1);
        adserverView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 50));
		linearLayout.addView(adserverView);
		LoadAdServerView();
	}
	
	void LoadAdServerView()
	{
		adserverView.update();
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			adserverView.setSite("8061");
		    adserverView.setZone("20249");
		    adserverView.setMinSizeX(320);
		    adserverView.setMinSizeY(50);
		    adserverView.setMaxSizeX(320);
		    adserverView.setMaxSizeY(50);		     
		} else {
			adserverView.setSite("8061");
		    adserverView.setZone("16741");
		    adserverView.setMinSizeX(468);
		    adserverView.setMinSizeY(60);
		    adserverView.setMaxSizeX(468);
		    adserverView.setMaxSizeY(60);

		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		LoadAdServerView();
		super.onConfigurationChanged(newConfig);
	}
}
