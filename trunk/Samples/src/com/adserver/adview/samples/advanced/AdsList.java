package com.adserver.adview.samples.advanced;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.adserver.adview.MASTAdServerView;
import com.adserver.adview.samples.R;

public class AdsList extends Activity {
	private ListView lstAds;
	private EditText inpSite;
	private EditText inpZone;
	private Button btnRefresh;
	private int site = 19829;
	private int zone = 88269;
	private Vector<MASTAdServerView> adserverViews;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.list);
        
        lstAds = (ListView) findViewById(R.id.lstAds);
        inpSite = (EditText) findViewById(R.id.inpSite);
        inpSite.setText(String.valueOf(site));
        inpZone = (EditText) findViewById(R.id.inpZone);
        inpZone.setText(String.valueOf(zone));
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					site = Integer.parseInt(inpSite.getText().toString());
			        zone = Integer.parseInt(inpZone.getText().toString());
			        
			        for (MASTAdServerView adserverView : adserverViews) {
				        adserverView.setSite(site);
				        adserverView.setZone(zone);
				        adserverView.setContentAlignment(true);
						adserverView.update();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
        
        adserverViews = new Vector<MASTAdServerView>();
        
        for (int i = 0; i < 10; i++) {
        	MASTAdServerView adserverView = new MASTAdServerView(this, site, zone);
        	adserverView.setId(i+1);
        	setAdLayoutParams(adserverView);
	        adserverView.setContentAlignment(true);
        	adserverViews.add(adserverView);
		}
        
        AdsListAdapter listAdapter = new AdsListAdapter(this, adserverViews);
		lstAds.setAdapter(listAdapter);
        
        LinearLayout frameMain = (LinearLayout) findViewById(R.id.frameMain);
        BitmapDrawable background = (BitmapDrawable)getResources().getDrawable(R.drawable.repeat_bg);
        background.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        frameMain.setBackgroundDrawable(background);
    }
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
        for (MASTAdServerView adserverView : adserverViews) {
    		setAdLayoutParams(adserverView);
	        adserverView.setContentAlignment(true);
			adserverView.update();
		}
	}
	
	private void setAdLayoutParams(MASTAdServerView adserverView) {
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
		int height = 50;

		int maxSize = metrics.heightPixels;
		if (maxSize < metrics.widthPixels) {
			maxSize = metrics.widthPixels;
		}
		
		if (maxSize <= 480) {
			height = 50;
		} else if ((maxSize > 480) && (maxSize <= 800)) {
			height = 100;
		} else if (maxSize > 800) {
			height = 120;
		}
		
		ViewGroup.LayoutParams lp = adserverView.getLayoutParams();
		if (lp == null) {
			lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, height);
			adserverView.setLayoutParams(lp);
		}
		
        adserverView.setMinSizeX(metrics.widthPixels);
        adserverView.setMinSizeY(height);
        adserverView.setMaxSizeX(metrics.widthPixels);
        adserverView.setMaxSizeY(height);
		adserverView.requestLayout();
	}
	
}