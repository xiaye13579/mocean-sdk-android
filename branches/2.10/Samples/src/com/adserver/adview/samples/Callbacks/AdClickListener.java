package com.adserver.adview.samples.Callbacks;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.adserver.adview.MASTAdServerView;
import com.adserver.adview.MASTAdServerViewCore.MASTOnAdClickListener;
import com.adserver.adview.samples.ApiDemos;
import com.adserver.adview.samples.R;

public class AdClickListener extends Activity {
    /** Called when the activity is first created. */
	private Context context;
	private LinearLayout linearLayout;
	public Handler handler = new Handler();
	String uMessage;
	int uTime;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        context = this;
        linearLayout = (LinearLayout) findViewById(R.id.frameAdContent);
        
        MASTAdServerView adserverView = new MASTAdServerView(this,8061,20249);
        adserverView.setOnAdClickListener(new UserOnAdClickListener());
        adserverView.setDefaultImage(R.drawable.robot2);
        adserverView.setMinSizeX(320);
	    adserverView.setMinSizeY(50);
	    adserverView.setMaxSizeX(320);
	    adserverView.setMaxSizeY(50);
        adserverView.setId(1);
        adserverView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ApiDemos.BANNER_HEIGHT));
		linearLayout.addView(adserverView);
    }
    
    class UserOnAdClickListener implements MASTOnAdClickListener
    {

		@Override
		public void click(MASTAdServerView arg0, String arg1) {
			updateUi(mUpdateResults, "Click url = "+ arg1, 500);
		}
		
		private void updateUi(Runnable mUpdateResults, String string, int i) {
			
	    	uMessage = string;
	    	uTime = i;
	    	handler.post(mUpdateResults);
		}
    	
    }
    
    private Runnable mUpdateResults = new Runnable() {
    	public void run() {
    		Toast.makeText(context, uMessage, uTime).show();
    		}
    	};
}