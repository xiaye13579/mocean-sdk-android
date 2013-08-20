package com.MASTAdView.samples.advanced;

import java.util.Vector;

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
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.MASTAdView.MASTAdRequest;
import com.MASTAdView.MASTAdView;
import com.MASTAdView.samples.R;


public class AdsList extends Activity {
	private ListView lstAds;
	private EditText inpSite;
	private EditText inpZone;
	private Button btnRefresh;
	private int site = 19829;
	private int zone = 102238; // all ads on rotation, images and rich media
	private Vector<View> adserverViews;
	
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
			        
			        for (View view : adserverViews) {
			        	if (view instanceof MASTAdView)
			        	{
			        		MASTAdView adserverView = (MASTAdView)view;
			        		adserverView.getAdRequest().setProperty(MASTAdRequest.parameter_site, site);
			        		adserverView.getAdRequest().setProperty(MASTAdRequest.parameter_zone, zone);
			        		//adserverView.setContentAlignment(true);
			        		adserverView.update();
			        	}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
        
        adserverViews = new Vector<View>();
        int[] textColors = { 0xFFFF0000, 0xFF00FF00, 0xFF0000FF };
        int[] bgColors = { 0xFF202020, 0xFF404040, 0xFF606060 };
        
        int max = 20;
        for (int i = 0; i < max; i++)
        {
        	// 3 elements should be ad views, the rest simple labels with varying colors
        	if ((i % 7) == 1)
        	{
	        	MASTAdView adserverView = new MASTAdView(this, site, zone);
	        	adserverView.setId(i+1);
	        	setAdLayoutParams(adserverView);
		        //adserverView.setContentAlignment(true);
	        	adserverViews.add(adserverView);
        	}
        	else
        	{
        		TextView label = new TextView(this);
        		label.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT)); 
        		label.setId(i + 1);
        		label.setText("List view # " + (i+1) + " of " + max);
        		label.setVisibility(View.VISIBLE);
        		label.setBackgroundColor(bgColors[i % 3]);
        		label.setTextColor(textColors[i % 3]);
        		adserverViews.add(label);
        	}
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
		
        for (View view : adserverViews) {
        	if (view instanceof MASTAdView)
        	{
        		MASTAdView adserverView = (MASTAdView)view;
        		setAdLayoutParams(adserverView);
        		//adserverView.setContentAlignment(true);
				adserverView.update();
        	}
		}
	}
	
	private void setAdLayoutParams(MASTAdView adserverView) {
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
		
		// Min size can be useful, but if you don't have ads large enough for all devices, it
		// can result in no ad being shown, so use it sparingly.
        //adserverView.setMinSizeX(metrics.widthPixels);
        //adserverView.setMinSizeY(height);
		
        adserverView.getAdRequest().setProperty(MASTAdRequest.parameter_size_x, metrics.widthPixels);
        adserverView.getAdRequest().setProperty(MASTAdRequest.parameter_size_y, height);
		adserverView.requestLayout();
	}
	
}