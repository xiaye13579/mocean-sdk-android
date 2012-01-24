package com.adserver.adview.samples.Callbacks;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adserver.adview.AdServerView;
import com.adserver.adview.AdServerViewCore.OnAdClickListener;
import com.adserver.adview.AdServerViewCore.OnAdDownload;
import com.adserver.adview.AdServerViewCore.OnOrmmaListener;
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
        
        AdServerView adserverView = new AdServerView(this,8061,17488);
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
    
    class UserOnOrmmaListener implements OnOrmmaListener
    {

		private void updateUi(Runnable mUpdateResults, String string, int i) {
			
	    	uMessage = string;
	    	uTime = i;
	    	handler.post(mUpdateResults);
		}

		@Override
		public void event(AdServerView arg0, String arg1, String arg2) {
			updateUi(mUpdateResults, "event arg1 = " + arg1+ "\n arg2 = "+arg2 , 500);			
		}
    	
    }
    
    private Runnable mUpdateResults = new Runnable() {
    	public void run() {
    		Toast.makeText(context, uMessage, uTime).show();
    		}
    	};
}