package com.adserver.adview.samples.Callbacks;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.adserver.adview.MASTAdServerView;
import com.adserver.adview.MASTAdServerViewCore.MASTOnOrmmaListener;
import com.adserver.adview.samples.ApiDemos;
import com.adserver.adview.samples.R;

public class OrmmaListener extends Activity {
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
        
        MASTAdServerView adserverView = new MASTAdServerView(this,8061,17488);
        adserverView.setOnOrmmaListener(new UserOnOrmmaListener());
        adserverView.setDefaultImage(R.drawable.robot2);
        adserverView.setMinSizeX(320);
	    adserverView.setMinSizeY(50);
	    adserverView.setMaxSizeX(320);
	    adserverView.setMaxSizeY(50);
        adserverView.setId(1);
        adserverView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ApiDemos.BANNER_HEIGHT));
		linearLayout.addView(adserverView);
    }
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
    
    class UserOnOrmmaListener implements MASTOnOrmmaListener
    {

		private void updateUi(Runnable mUpdateResults, String string, int i) {
			
	    	uMessage = string;
	    	uTime = i;
	    	handler.post(mUpdateResults);
		}

		@Override
		public void event(MASTAdServerView arg0, String arg1, String arg2) {
			updateUi(mUpdateResults, "event arg1 = " + arg1+ "\n arg2 = "+arg2 , 500);			
		}
    	
    }
    
    private Runnable mUpdateResults = new Runnable() {
    	public void run() {
    		Toast.makeText(context, uMessage, uTime).show();
    		}
    	};
}