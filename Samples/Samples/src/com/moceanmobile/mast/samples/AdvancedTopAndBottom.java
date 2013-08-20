package com.moceanmobile.mast.samples;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class AdvancedTopAndBottom extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advanced_top_and_bottom);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.refresh_menu, menu);
		return true;
	}

}
