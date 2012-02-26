package com.adserver.adview.samples.simple;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.adserver.adview.MASTAdServerView;
import com.adserver.adview.samples.R;

public class TopAndBottom extends Activity {
	private MASTAdServerView adserverViewTop;
	private LinearLayout linearLayoutTop;
	private EditText inpSiteTop;
	private EditText inpZoneTop;
	private Button btnRefreshTop;
	private int siteTop = 19829;
	private int zoneTop = 88269;

	private MASTAdServerView adserverViewBottom;
	private LinearLayout linearLayoutBottom;
	private EditText inpSiteBottom;
	private EditText inpZoneBottom;
	private Button btnRefreshBottom;
	private int siteBottom = 19829;
	private int zoneBottom = 88269;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.top_and_bottom);
        
        linearLayoutTop = (LinearLayout) findViewById(R.id.frameAdContentTop);
        inpSiteTop = (EditText) findViewById(R.id.inpSiteTop);
        inpSiteTop.setText(String.valueOf(siteTop));
        inpZoneTop = (EditText) findViewById(R.id.inpZoneTop);
        inpZoneTop.setText(String.valueOf(zoneTop));
        btnRefreshTop = (Button) findViewById(R.id.btnRefreshTop);
        btnRefreshTop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					siteTop = Integer.parseInt(inpSiteTop.getText().toString());
			        zoneTop = Integer.parseInt(inpZoneTop.getText().toString());
			        adserverViewTop.setSite(siteTop);
			        adserverViewTop.setZone(zoneTop);
					adserverViewTop.update();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
        
        adserverViewTop = new MASTAdServerView(this, siteTop, zoneTop);
        adserverViewTop.setId(1);
        setAdLayoutParamsTop();
        linearLayoutTop.addView(adserverViewTop);
		adserverViewTop.update();

		
        linearLayoutBottom = (LinearLayout) findViewById(R.id.frameAdContentBottom);
        inpSiteBottom = (EditText) findViewById(R.id.inpSiteBottom);
        inpSiteBottom.setText(String.valueOf(siteBottom));
        inpZoneBottom = (EditText) findViewById(R.id.inpZoneBottom);
        inpZoneBottom.setText(String.valueOf(zoneBottom));
        btnRefreshBottom = (Button) findViewById(R.id.btnRefreshBottom);
        btnRefreshBottom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					siteBottom = Integer.parseInt(inpSiteBottom.getText().toString());
			        zoneBottom = Integer.parseInt(inpZoneBottom.getText().toString());
			        adserverViewBottom.setSite(siteBottom);
			        adserverViewBottom.setZone(zoneBottom);
					adserverViewBottom.update();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
        
        adserverViewBottom = new MASTAdServerView(this, siteBottom, zoneBottom);
        adserverViewBottom.setId(1);
        setAdLayoutParamsBottom();
        linearLayoutBottom.addView(adserverViewBottom);
		adserverViewBottom.update();
		
        LinearLayout frameMain = (LinearLayout) findViewById(R.id.frameMain);
        BitmapDrawable background = (BitmapDrawable)getResources().getDrawable(R.drawable.repeat_bg);
        background.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        frameMain.setBackgroundDrawable(background);
    }
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setAdLayoutParamsTop();
		adserverViewTop.update();
		
		setAdLayoutParamsBottom();
		adserverViewBottom.update();
	}
	
	private void setAdLayoutParamsTop() {
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
		
		ViewGroup.LayoutParams lp = adserverViewTop.getLayoutParams();
		if (lp == null) {
			lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, height);
			adserverViewTop.setLayoutParams(lp);
		}
		
        adserverViewTop.setMinSizeX(metrics.widthPixels);
        adserverViewTop.setMinSizeY(height);
        adserverViewTop.setMaxSizeX(metrics.widthPixels);
        adserverViewTop.setMaxSizeY(height);
		adserverViewTop.requestLayout();
	}

	private void setAdLayoutParamsBottom() {
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
		
		ViewGroup.LayoutParams lp = adserverViewBottom.getLayoutParams();
		if (lp == null) {
			lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, height);
			adserverViewBottom.setLayoutParams(lp);
		} else {
			lp.height = height;
			adserverViewBottom.setLayoutParams(lp);
		}
		
        adserverViewBottom.setMinSizeX(metrics.widthPixels);
        adserverViewBottom.setMinSizeY(height);
        adserverViewBottom.setMaxSizeX(metrics.widthPixels);
        adserverViewBottom.setMaxSizeY(height);
		adserverViewBottom.requestLayout();
	}
	
}