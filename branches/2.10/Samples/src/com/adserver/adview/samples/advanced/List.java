package com.adserver.adview.samples.advanced;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.adserver.adview.MASTAdServerView;
import com.adserver.adview.samples.ApiDemos;
import com.adserver.adview.samples.R;

public class List extends Activity {
	private LinearLayout linearLayout;

	int baners[] = { 
			20249,// Always working mOcean ad
			17490,// Sample site-zone with HTML ad
			17487,// Sample site-zone with ORMMA level1 ad
			17488,// Sample site-zone with ORMMA level2 ad
			17489,// Sample site-zone with ORMMA level3 ad
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		setContentView(R.layout.main_all);

		linearLayout = (LinearLayout) findViewById(R.id.frameAdContent);

		for (int x = 0; x < baners.length; x++) {
			MASTAdServerView adserverView = new MASTAdServerView(this, 8061,
					baners[x]);
	        adserverView.setId(x+1);
			adserverView.setUpdateTime(3);
			adserverView
					.setLayoutParams(new ViewGroup.LayoutParams(
							ViewGroup.LayoutParams.FILL_PARENT,
							ApiDemos.BANNER_HEIGHT));
			linearLayout.addView(adserverView);
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

}