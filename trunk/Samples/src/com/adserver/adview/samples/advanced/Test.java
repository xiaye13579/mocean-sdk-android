package com.adserver.adview.samples.advanced;

import com.adserver.adview.AdServerView;
import com.adserver.adview.samples.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class Test extends Activity {
	private Context context;
	private LinearLayout linearLayout;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        context = this;
        linearLayout = (LinearLayout) findViewById(R.id.frameAdContent);
         
        AdServerView adserverView = new AdServerView(this,"8061","20249");
        adserverView.setTest(true);
        adserverView.setId(1);
        adserverView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 50));
		linearLayout.addView(adserverView);
    }
}
