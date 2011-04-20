/*© 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.*/
package checkpoint.forms;

import java.util.Hashtable;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.adserver.adview.AdServerInterstitialView;
import com.adserver.adview.AdServerView;
import com.adserver.adview.AdServerViewCore.OnAdClickListener;
import com.adserver.adview.AdServerViewCore.OnAdDownload;

public class MainAdvanced extends Activity {
    /** Called when the activity is first created. */
	private Context context;
	private LinearLayout linearLayout;
	 AdServerView adserverView;
	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_advanced);
        context = this;
        linearLayout = (LinearLayout) findViewById(R.id.frameAdContent);
        
        Button interstitialAd = (Button) findViewById(R.id.interstitialAd);
        interstitialAd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//Old API
//		        AdServerInterstitialView.show(context,
//        		50, 10, 320, 100, 3, 20, true,
//        		true, null, R.drawable.test_banner, 10000L, AdServerView.VISIBLE_MODE_CASE2, 
//        		"abCdefG", "52352", AdServerView.MODE_ADS_ONLY, 
//        		"6235", "9312", "1.1.1.1", null, AdServerView.ADS_TYPE_TEXT_AND_IMAGES, null, 
//        		null, null, null, null, false, null, null, null, 
//        		null, null, null, null, null, null, null, null);
				
				AdServerInterstitialView interstitialView = 
					new AdServerInterstitialView(context, "6235", "9312");
				interstitialView.setShowCloseButtonTime(3);
				interstitialView.setAutoCloseInterstitialTime(20);
				interstitialView.setIsShowPhoneStatusBar(true);
				interstitialView.show();				
			}
        });
        
        Hashtable<String, String> customParameters = new Hashtable<String, String>();
        customParameters.put("debug", "1");
        //customParameters.put("key2", "value2");
        //customParameters.put("key3", "value3");
        
        //Old API
//        final AdServerView adserverView = new AdServerView(this,
//        		50, 10, 320, 100, 
//        		false, null, R.drawable.test_banner, 10000L, AdServerView.VISIBLE_MODE_CASE2, 
//        		"abCdefG", "52352", AdServerView.MODE_ADS_ONLY, 
//        		"5441", "9312", "1.1.1.1", null, AdServerView.ADS_TYPE_TEXT_AND_IMAGES, null, 
//        		null, null, null, null, false, null, null, null, 
//        		null, null, null, null, null, null, null, customParameters);
//        adserverView.setId(1);
//        adserverView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 250));
//		linearLayout.addView(adserverView);
//
        
        //AdServerView adserverView = new AdServerView(this, "5441", "9312");
        adserverView = new AdServerView(this, "8061", "20249");//Default mOcean ad
//        adserverView = new AdServerView(this, "8061", "18165");//iVdopia
//        adserverView = new AdServerView(this, "8061", "16111");//Greystripe
//        adserverView = new AdServerView(this, "8061", "16685");//Medialets
//        adserverView = new AdServerView(this, "8061", "21676");//SAS/YOC
//        adserverView = new AdServerView(this, "8061", "21637");//AdMod
//        adserverView = new AdServerView(this, "8061", "16938");//Millennial
//      adserverView = new AdServerView(this, "8061", "16685");//Medialets
//        adserverView = new AdServerView(this, "10113", "20991");
//        adserverView = new AdServerView(this, "8061", "31");
//        adserverView.setAdserverURL("http://192.168.1.153/mocean/ad/");
        
//        adserverView.setCustomParameters(customParameters);

//        adserverView.setTest(true);
//        adserverView.setSite("5441");
//        adserverView.getSite();
//
//        adserverView.setZone("9312");
//        adserverView.getZone();
        
        adserverView.setId(1);
        adserverView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 250));
		
/*		adserverView.setTest(false);
		adserverView.getTest();
		
		adserverView.setPremium(AdServerView.PREMIUM_STATUS_PREMIUM);
		adserverView.getPremium();
		
		adserverView.setKeywords("test");
		adserverView.getKeywords();
		
		adserverView.setMinSizeX(200);
		adserverView.getMinSizeX();
		
		adserverView.setMinSizeY(40);
		adserverView.getMinSizeY();
		
		adserverView.setMaxSizeX(320);
		adserverView.getMaxSizeX();
		
		adserverView.setMaxSizeY(50);
		adserverView.getMaxSizeY();
		
		//adserverView.setDefaultImage(R.drawable.test_banner);
		
		
		//adserverView.setBackgroundColor("33CCFF");
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
		
		OnAdDownload adDownload = new OnAdDownload() {
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
		};
		adserverView.setOnAdDownload(adDownload);
		adserverView.getOnAdDownload();
		*/
        
       
        
		linearLayout.addView(adserverView);
		
    }

}