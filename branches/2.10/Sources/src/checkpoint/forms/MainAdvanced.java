package checkpoint.forms;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;
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

import com.adserver.adview.AdLog;
import com.adserver.adview.AdServerInterstitialView;
import com.adserver.adview.AdServerView;
import com.adserver.adview.InternelBrowser;
import com.adserver.adview.AdServerViewCore.OnAdClickListener;
import com.adserver.adview.AdServerViewCore.OnAdDownload;
import com.adserver.adview.AdServerViewCore.OnOrmmaListener;
import com.adserver.adview.AdServerViewCore.OnThirdPartyRequest;
import com.adserver.adview.ormma.OrmmaDisplayController;

public class MainAdvanced extends Activity {
    /** Called when the activity is first created. */
	private Context context;
	private LinearLayout linearLayout;
	 AdServerView adserverView;
	 
	//byte[] b = new byte[12 * 1000 * 1000];
	 
	void CreateTest()
	{
		AdLog.setDefaultLogLevel(AdLog.LOG_LEVEL_3);   
	     setContentView(R.layout.main_test);
	     linearLayout = (LinearLayout) findViewById(R.id.frameAdContent);
		adserverView = new AdServerView(this,8061,20249);
   	 	adserverView.setId(1);        
        adserverView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 50));
        //adserverView.setUpdateTime(5);
        /*adserverView.setOnAdDownload(new OnAdDownload() {
			
			@Override
			public void error(AdServerView sender, String error) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void end(AdServerView sender) {
				while(true);				
			}
			
			@Override
			public void begin(AdServerView sender) {
				// TODO Auto-generated method stub
				
			}
		});*/
   	 	linearLayout.addView(adserverView);
   	 
		 /*AdLog.setDefaultLogLevel(AdLog.LOG_LEVEL_3);   
	     setContentView(R.layout.main_test);
	     linearLayout = (LinearLayout) findViewById(R.id.frameAdContent);
	     for(int x=0;x<20;x++)
	     {
	    	 adserverView = new AdServerView(this,8061,20249);
	    	 adserverView.setId(x+1);        
	         adserverView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 50));
	         adserverView.setUpdateTime(300);
	    	 linearLayout.addView(adserverView);
	     }
	    */
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        
        if(true)
        {
        	CreateTest();
        	
        	/*WebView view = new WebView(this);
	    	view.loadUrl("http://developer.android.com/sdk/index.html");
	    	setContentView(view);*/
        	
        	return;        	
        }
        AdLog.setDefaultLogLevel(AdLog.LOG_LEVEL_3);   
        setContentView(R.layout.main_advanced);
        linearLayout = (LinearLayout) findViewById(R.id.frameAdContent);
        
        ((Button) findViewById(R.id.btnAdd)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createBaner();
			}
		});
        
        
        
        Button interstitialAd = (Button) findViewById(R.id.interstitialAd);
        interstitialAd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				adserverView.update();
				/*OrmmaDisplayController dc = new OrmmaDisplayController(adserverView, context);
				String s = dc.getMaxSize();
				s+= " ";
				/*Rect rect= new Rect();
				Window window= ((Activity) context).getWindow();
				window.getDecorView().getWindowVisibleDisplayFrame(rect);
				int statusBarHeight= rect.top;
				int contentViewTop= window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
				int titleBarHeight= contentViewTop - statusBarHeight;*/
				//Old API
//		        AdServerInterstitialView.show(context,
//        		50, 10, 320, 100, 3, 20, true,
//        		true, null, R.drawable.test_banner, 10000L, AdServerView.VISIBLE_MODE_CASE2, 
//        		"abCdefG", "52352", AdServerView.MODE_ADS_ONLY, 
//        		"6235", "9312", "1.1.1.1", null, AdServerView.ADS_TYPE_TEXT_AND_IMAGES, null, 
//        		null, null, null, null, false, null, null, null, 
//        		null, null, null, null, null, null, null, null);
				
				//linearLayout.addView(adserverView);
				
				//adserverView.setVisibility(adserverView.getVisibility() == View.VISIBLE ? View.INVISIBLE :View.VISIBLE);
				//adserverView.setUpdateTime(0);
				//adserverView.update();
				
				/*AdServerInterstitialView interstitialView = 
					new AdServerInterstitialView(context);//, 8061, 8888);//16112);
					//new AdServerInterstitialView(context, "10359", "21505");
				interstitialView.setAdserverURL("http://192.168.1.162/new_mcn/request.php");
				interstitialView.setSite(8061);
				interstitialView.setZone(8888);
				interstitialView.setLogLevel(AdLog.LOG_LEVEL_3);
				//interstitialView.setShowCloseButtonTime(3);
				//interstitialView.setAutoCloseInterstitialTime(20);
				interstitialView.setIsShowPhoneStatusBar(true);
				interstitialView.useCustomClose(false);
				interstitialView.show();
				//adserverView.setUpdateTime(adserverView.getUpdateTime() == 0 ? 1 : 0);
				//((FrameLayout)findViewById(R.id.main)).refreshDrawableState();
				
				//new InternelBrowser(context,"www.google.com").show();
				//adserverView.update();
				
				/*AlertDialog.Builder alertbox = new AlertDialog.Builder(context);				
				
				/*LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				
				LinearLayout mailLayout = new LinearLayout(context);
				mailLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
				
				WebView webView = new WebView(context);
				webView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT,1f));
				mailLayout.addView(webView);
				webView.loadUrl("http://www.goole.com");
				
				
				alertbox.setView(mailLayout);*/
				
				/*LayoutInflater inflater = (LayoutInflater)context.getSystemService      (Context.LAYOUT_INFLATER_SERVICE);
				alertbox.setView(inflater.inflate(R.layout.externel_browser, null));
				alertbox.create().show();
				
				
				/*alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface arg0, int arg1) {
		            	OnAllertDialogOk();*/
				
			}
        });
        
        Hashtable<String, String> customParameters = new Hashtable<String, String>();
        customParameters.put("debug", "1");
        
    
  
//        AdLogManager.SetFileLog("sss", context);
        
        AdLog.setDefaultLogLevel(AdLog.LOG_LEVEL_3);
        //AdLog.SetFileLog(context.getFilesDir().getAbsolutePath()+"/logfile.log");
        //AdLog.setFileLog("sdcard/log.txt");
       
//		Basic ad

//        adserverView = new AdServerView(this, "8061", "202490");

        /*if(true)
        {
        	 AdServerView ad = (AdServerView)findViewById(R.id.AdServerView);
        	 ad.setOnAdDownload(new OnAdDownload() {
				
        		 @Override
     			public void begin() {
     				Log.w("mojiva","mojiva: OnAdDownload begin");
     			}

     			@Override
     			public void end() {
     				Log.w("mojiva","mojiva: OnAdDownload end");
     			}

     			@Override
     			public void error(String error) {
     				Log.w("mojiva","mojiva: OnAdDownload error=" + error);
     			}
			});
        	       
        	  

        	return;
        }*/
        
        //if(true) return;
        
        adserverView = new AdServerView(this);
      //  adserverView.setSite(17340);
      //  adserverView.setZone(53923);
      //  adserverView.setType(1);
//       adserverView.setSite(8061);
//        adserverView.setZone(22034);
//        adserverView.setAdserverURL("http://192.168.1.153/test_mocean/request.php");
//        adserverView.setAdserverURL("http://192.168.1.153/mocean/ad.php");
        
        adserverView.setAdserverURL("http://192.168.1.162/new_mcn/request.php");
        adserverView.setSite(8061);
        adserverView.setZone(96001);
        //adserverView.setZone(50001);
        adserverView.setTrack(true);


//        adserverView.setZone(1250);
        //adserverView.setLocationDetection(true);
        //adserverView.setZone(14);
        //adserverView.setInternalBrowser(true);
        
//adserverView = new AdServerView(this, 8061, 20249);//Default mOcean ad
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
        //adserverView.setAdserverURL("http://192.168.1.153/mocean/ad.php");
        //adserverView.SetLogLevel(AdLog.LOG_LEVEL_3);
        adserverView.setOnThirdPartyRequest(new OnThirdPartyRequest() {
			public void event(AdServerView sender, HashMap<String, String> params) {
				
			}
		});
        adserverView.setCustomParameters(customParameters);

//        adserverView.setTest(true);
//        adserverView.setSite("5441");
//        adserverView.getSite();
//
//        adserverView.setZone("9312");
//        adserverView.getZone();
        
        adserverView.setOnOrmmaListener(new OnOrmmaListener() {
			
			@Override
			public void event(AdServerView sender, String name, String params) {
				Log.w("<<<<<<<<<<<<<<"+name, params);				
			}
		});
        
        adserverView.setId(1);
        //LinearLayout.LayoutParams lpNew = new LinearLayout.LayoutParams(320, 50);
		//lpNew.gravity = Gravity.RIGHT;
		//adserverView.setLayoutParams(lpNew);
        //adserverView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 150));
        adserverView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 300));
		//adserverView.setUpdateTime(5000);
		//adserverView.setVisibility(View.GONE);
		
		//adserverView.setOnAdClickListener(new OnAdClickListener() {
		//   @Override
		//	public void click(String url) {
		//	   finish();
		//	}
		//});
		
		/*adserverView.setDefaultImage(R.drawable.test_banner);
		
		adserverView.setMinSizeX(200);
		adserverView.getMinSizeX();
		
		adserverView.setMinSizeY(40);
		adserverView.getMinSizeY();
		
		adserverView.setMaxSizeX(320);
		adserverView.getMaxSizeX();
		
		adserverView.setMaxSizeY(50);
		adserverView.getMaxSizeY();*/
		
		//adserverView.setLongitude("dsdsdsdsd");
		
		//adserverView.setBackgroundColor(0xFF0000f0);
		//adserverView.setTextColor(0xFF11CC22);
		
/*		//adserverView.setDefaultImage(R.drawable.test_banner);
		
		
		//adserverView.getBackgroundColor();
		
		adserverView.setTextColor("11CC22");
		adserverView.getTextColor();
		
		adserverView.setCustomParameters(customParameters);
		adserverView.getCustomParameters();

		adserverView.setAdserverURL("http://ads.mocean.mobi/ad");
		adserverView.getAdserverURL();
		
		adserverView.setDefaultImage(R.drawable.test_banner);
		adserverView.getDefaultImage();
		
		adserverView.setInternalBrowser(false);
		adserverView.getInternalBrowser();
		
		adserverView.setAdvertiserId("23DR67");
		adserverView.getAdvertiserId();
		
		adserverView.setGroupCode("TR57");
		adserverView.getGroupCode();
		
		adserverView.setUpdateTime(2);
		adserverView.getUpdateTime();
		
		adserverView.setLatitude("40.756054");
		adserverView.getLatitude();
		
		adserverView.setLongitude("-73.986951");
		adserverView.getLongitude();
		
		adserverView.setCountry("US");
		adserverView.getCountry();
		
		adserverView.setRegion("NY");
		adserverView.getRegion();
		
		adserverView.setCity("New York");
		adserverView.getCity();
		
		adserverView.setArea("K12");
		adserverView.getArea();
		
		adserverView.setMetro("M01");
		adserverView.getMetro();
		
		adserverView.setZip("10024");
		adserverView.getZip();
		
		adserverView.setCarrier("Verizon");
		adserverView.getCarrier();
		
		OnAdClickListener adClickListener = new OnAdClickListener() {
			@Override
			public void click(String url) {
				Log.w("mojiva","mojiva: OnAdClickListener click url=" + url);
			}
		};
		adserverView.setOnAdClickListener(adClickListener);
		adserverView.getOnAdClickListener();
		
		*/OnAdDownload adDownload = new OnAdDownload() {
			@Override
			public void begin(AdServerView sender) {
				Log.w("mojiva","mojiva: OnAdDownload begin");
			}

			@Override
			public void end(AdServerView sender) {
				Log.w("mojiva","mojiva: OnAdDownload end");
			}

			@Override
			public void error(AdServerView sender,String error) {
				Log.w("mojiva","mojiva: OnAdDownload error=" + error);
			}
		};
		adserverView.setOnAdDownload(adDownload);
		//adserverView.getOnAdDownload();
        
		//adserverView.setVisibility(View.INVISIBLE);
		linearLayout.addView(adserverView);
			
       /* adserverView = new AdServerView(this, "8061", "16685");//Medialets
        adserverView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 150));
		adserverView.setUpdateTime(5);
		linearLayout.addView(adserverView);*/
		//linearLayout.removeAllViews();
		
    }
	
	void createBaner()
	{
		//if(adserverView!=null) linearLayout.removeView(adserverView);
		 	adserverView = new AdServerView(this);
	        //adserverView.setSite(17340);
	        //adserverView.setZone(53923);
		 	adserverView.setSite(8061);
	        //adserverView.setZone(20249);
		 	adserverView.setZone(22034);
	        adserverView.setTrack(true);
	        adserverView.setOnThirdPartyRequest(new OnThirdPartyRequest() {
				public void event(AdServerView sender, HashMap<String, String> params) {
					
				}
			});
	        adserverView.getZone();
	        
	        adserverView.setOnOrmmaListener(new OnOrmmaListener() {
				
				@Override
				public void event(AdServerView sender, String name, String params) {
					Log.w("<<<<<<<<<<<<<<"+name, params);				
				}
			});
	        
	        adserverView.setId(1);
	        adserverView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 50));
			adserverView.setUpdateTime(10);
			OnAdDownload adDownload = new OnAdDownload() {
				@Override
				public void begin(AdServerView sender) {
					Log.w("mojiva","mojiva: OnAdDownload begin");
				}

				@Override
				public void end(AdServerView sender) {
					Log.w("mojiva","mojiva: OnAdDownload end");
				}

				@Override
				public void error(AdServerView sender,String error) {
					Log.w("mojiva","mojiva: OnAdDownload error=" + error);
				}
			};
			adserverView.setOnAdDownload(adDownload);
	        
			linearLayout.addView(adserverView);
				
	}

}