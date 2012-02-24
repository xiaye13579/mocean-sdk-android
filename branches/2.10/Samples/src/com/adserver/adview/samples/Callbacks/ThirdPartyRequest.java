package com.adserver.adview.samples.Callbacks;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.adserver.adview.AdServerViewCore.OnThirdPartyRequest;
import com.adserver.adview.MASTAdServerView;
import com.adserver.adview.samples.ApiDemos;
import com.adserver.adview.samples.R;

public class ThirdPartyRequest extends Activity {
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
        
        MASTAdServerView adserverView = new MASTAdServerView(this,8061,21637);
        adserverView.setOnThirdPartyRequest(new UserOnThirdPartyRequest());
        adserverView.setDefaultImage(R.drawable.robot2);
        adserverView.setMinSizeX(320);
	    adserverView.setMinSizeY(50);
	    adserverView.setMaxSizeX(320);
	    adserverView.setMaxSizeY(50);
        adserverView.setId(1);
        adserverView.setUpdateTime(5);
        adserverView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ApiDemos.BANNER_HEIGHT));
		linearLayout.addView(adserverView);
    }
    
    class UserOnThirdPartyRequest implements OnThirdPartyRequest
    {

		private void updateUi(Runnable mUpdateResults, String string, int i) {
			
	    	uMessage = string;
	    	uTime = i;
	    	handler.post(mUpdateResults);
		}

		@Override
		public void event(MASTAdServerView arg0, HashMap<String, String> arg1) {			
			updateUi(mUpdateResults, arg1.toString(),Toast.LENGTH_LONG  );			
		}
    	
    }
    
    private Runnable mUpdateResults = new Runnable() {
    	public void run() {
    		Toast.makeText(context, uMessage, uTime).show();
    		}
    	};
}