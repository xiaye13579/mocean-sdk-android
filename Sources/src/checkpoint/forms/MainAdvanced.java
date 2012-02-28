package checkpoint.forms;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.adserver.adview.MASTAdLog;
import com.adserver.adview.MASTAdServerView;
import com.adserver.adview.InternelBrowser;
import com.adserver.adview.MASTAdServerViewCore.MASTOnAdClickListener;
import com.adserver.adview.MASTAdServerViewCore.MASTOnAdDownload;
import com.adserver.adview.MASTAdServerViewCore.MASTOnOrmmaListener;
import com.adserver.adview.MASTAdServerViewCore.MASTOnThirdPartyRequest;
import com.adserver.adview.ormma.OrmmaDisplayController;

public class MainAdvanced extends Activity {
    /** Called when the activity is first created. */
	private Context context;
	private LinearLayout linearLayout;
	MASTAdServerView adserverView;
	byte[] array = new byte[2*1024*1024]; 
	Handler handler = new Handler();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
       
        MASTAdLog.setDefaultLogLevel(MASTAdLog.LOG_LEVEL_3);
        
        if(false)
        {
        	setContentView(R.layout.main_list);
        	ListView lw = ((ListView)findViewById(R.id.listView));
        	lw.setAdapter(new TestListAdapter(this));
        	return;
        }
        
        setContentView(R.layout.main_advanced);
        linearLayout = (LinearLayout) findViewById(R.id.frameAdContent);
         
        MASTAdLog.setDefaultLogLevel(MASTAdLog.LOG_LEVEL_3);
        
        ((Button)findViewById(R.id.interstitialAd)).setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MASTAdServerView interstitialView = 
						new MASTAdServerView(context, 8061, 20249);//16112);
						//new AdServerInterstitialView(context, "10359", "21505");
					//interstitialView.setAdserverURL("http://192.168.1.162/new_mcn/request.php");
					//interstitialView.setSite(8061);
					//interstitialView.setZone(8888);
					interstitialView.setLogLevel(MASTAdLog.LOG_LEVEL_3);
					interstitialView.setIsShowPhoneStatusBar(true);
					//interstitialView.useCustomClose(false);
					interstitialView.setLayoutParams(new ViewGroup.LayoutParams(320, 50));
					interstitialView.show();
	
			}
		});
       // adserverView = new MASTAdServerView(this);
        //adserverView.setUA("test ua");
      //  adserverView.setSite(17340);
      //  adserverView.setZone(53923);
      //  adserverView.setType(1);
//       adserverView.setSite(8061);
//        adserverView.setZone(22034);
//        adserverView.setAdserverURL("http://192.168.1.153/test_mocean/request.php");
//        adserverView.setAdserverURL("http://192.168.1.153/mocean/ad.php");
        
        //adserverView.setAdserverURL("http://192.168.1.162/new_mcn/request.php");
        //adserverView.setAdserverURL("http://192.168.1.162/orm/exp/bool.html");
        //adserverView.setSite(8061);
        //adserverView.setZone(98006);
        //adserverView.setZone(50002);
        //adserverView.setTrack(true);


        adserverView = new MASTAdServerView(this, 8061, 20249);//Default mOcean ad
        //adserverView = new MASTAdServerView(this, 8061, 54731);
        
//adserverView.setBackgroundResource(R.drawable.icon);
//      adserverView = new AdServerView(this, 8061, 2);
//      adserverView.setBackgroundColor(0);
//        adserverView = new AdServerView(this, "8061", "18165");//iVdopia
//        adserverView = new AdServerView(this,8061,17489);//ORMMA3
//        adserverView = new AdServerView(this,8061,17487);//ORMMA1
//        adserverView = new AdServerView(this, "8061", "16111");//Greystripe
//        adserverView = new AdServerView(this, "8061", "16139");//Greystripe excomp
//        adserverView = new AdServerView(this, "8061", "16685");//Medialets
//        adserverView = new AdServerView(this, 8061, 21676);//SAS/YOC
//        adserverView = new AdServerView(this, 8061, 21637);//AdMob
//        adserverView = new AdServerView(this, "8061", "16938");//Millennial
        
//        adserverView = new AdServerView(this, "8061", "16685");//Medialets
        
//        adserverView = new AdServerView(this, "8061", "16686");//Medialets
//        adserverView = new AdServerView(this, "10113", "20991");
//        adserverView = new AdServerView(this, "8061", "14");
        adserverView.setOnThirdPartyRequest(new MASTOnThirdPartyRequest() {
			public void event(MASTAdServerView sender, HashMap<String, String> params) {
				
			}
		});
        adserverView.setInternalBrowser(true);
        
        adserverView.setId(1);
        adserverView.setLayoutParams(new ViewGroup.LayoutParams(320, 300));
        //adserverView.setAutoCollapse(false);
		adserverView.update();
		adserverView.setContentAlignment(true);
		adserverView.setScaleOnDPI(true);
		//adserverView.setBackgroundColor(0);
		
		adserverView.setOnAdDownload(new MASTOnAdDownload() {
			
			@Override
			public void error(MASTAdServerView sender, String error) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void end(MASTAdServerView sender) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void begin(MASTAdServerView sender) {
				// TODO Auto-generated method stub
				
			}
		});
		
        linearLayout.addView(adserverView);        
        //adserverView.setVisibility(View.VISIBLE);
        //*/
		/*WebView wv= new WebView(this);
        wv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 300));
        linearLayout.addView(wv);*/
       /* (new Timer()).schedule(new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						linearLayout.removeView(adserverView);
						linearLayout.addView(adserverView); 
						adserverView.update();
						adserverView.invalidate();
					}
				});				
			}
		}, 6000, 6000);*/
    }
	
	

}