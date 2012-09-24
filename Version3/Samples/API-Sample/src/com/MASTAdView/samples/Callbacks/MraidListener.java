package com.MASTAdView.samples.Callbacks;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.MASTAdView.MASTAdDelegate;
import com.MASTAdView.MASTAdRequest;
import com.MASTAdView.MASTAdView;
import com.MASTAdView.samples.R;


public class MraidListener extends Activity {
	private Context context;
	private Handler handler = new Handler();
	private MASTAdView adserverView;
	private LinearLayout linearLayout;
	private EditText inpSite;
	private EditText inpZone;
	private Button btnRefresh;
	private int site = 19829;
	private int zone = 98463;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.main);
        context = this;
        
        linearLayout = (LinearLayout) findViewById(R.id.frameAdContent);
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
			        adserverView.getAdRequest().setProperty(MASTAdRequest.parameter_site, site);
			        adserverView.getAdRequest().setProperty(MASTAdRequest.parameter_zone, zone);
					adserverView.update();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
        
        adserverView = new MASTAdView(this, site, zone);
        adserverView.setId(1);
        setAdLayoutParams();
        adserverView.getAdDelegate().setMraidEventHandler(new UserOnMraidListener());
        linearLayout.addView(adserverView);
        //adserverView.setContentAlignment(true);
		adserverView.update();
        
        LinearLayout frameMain = (LinearLayout) findViewById(R.id.frameMain);
        BitmapDrawable background = (BitmapDrawable)getResources().getDrawable(R.drawable.repeat_bg);
        background.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        frameMain.setBackgroundDrawable(background);
    }
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setAdLayoutParams();
		adserverView.update();
	}
	
	String uMessage;
    class UserOnMraidListener implements MASTAdDelegate.MraidEventHandler {

		private void updateUi(Runnable mUpdateResults, String string) {
	    	uMessage = string;
	    	handler.post(mUpdateResults);
		}

		@Override
		public void onMraidEvent(MASTAdView arg0, String arg1, String arg2) {
			updateUi(mUpdateResults, "event arg1 = " + arg1+ "\n arg2 = "+arg2);			
		}
    }
    
    private Runnable mUpdateResults = new Runnable() {
    	public void run() {
	    	AlertDialog.Builder builder = new AlertDialog.Builder(context)
			.setTitle("OnMraidListener")
			.setMessage(uMessage);

	    	DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
				}
			};
				
			builder.setPositiveButton(context.getResources().getString(R.string.ok), okListener);
			builder.create().show();
		}
	};
	
	private void setAdLayoutParams() {
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
		
        //adserverView.setMinSizeX(metrics.widthPixels);
        //adserverView.setMinSizeY(height);
        
        adserverView.getAdRequest().setProperty(MASTAdRequest.parameter_size_x, metrics.widthPixels);
        adserverView.getAdRequest().setProperty(MASTAdRequest.parameter_size_y, height);
		adserverView.requestLayout();
	}
	
}