/*© 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.*/
package com.adserver.adview.samples.advanced;

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
import com.adserver.adview.AdServerViewCore.OnAdDownload;
import com.adserver.adview.samples.ApiDemos;
import com.adserver.adview.samples.R;

public class Callback extends Activity {
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
        
        AdServerView adserverView = new AdServerView(this,"8061","20249");
        adserverView.setOnAdDownload(new UserAdDownload());
        adserverView.setDefaultImage(R.drawable.robot2);
        adserverView.setMinSizeX(320);
	    adserverView.setMinSizeY(50);
	    adserverView.setMaxSizeX(320);
	    adserverView.setMaxSizeY(50);
        adserverView.setId(1);
        adserverView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ApiDemos.BANNER_HEIGHT));
		linearLayout.addView(adserverView);
    }
    
       
    class UserAdDownload implements OnAdDownload
    {

		public void begin() {
			Log.d("Callback", "begin");
			updateUi(mUpdateResults, "Begin Downloading", 500);
		}

		public void end() {
			Log.d("Callback", "end");
			updateUi(mUpdateResults, "End Downloading", 500);
		}

		public void error(String arg0) {
			Log.d("Callback","error: "+arg0+"\n");
			updateUi(mUpdateResults, "Error :" + arg0, 5000);

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