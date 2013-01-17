package com.MASTAdView.samples.error;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.MASTAdView.MASTAdConstants;
import com.MASTAdView.MASTAdRequest;
import com.MASTAdView.MASTAdView;
import com.MASTAdView.MASTAdDelegate;
import com.MASTAdView.samples.R;


public class Image extends Activity implements MASTAdDelegate.AdDownloadEventHandler {
	private MASTAdView adserverView;
	private LinearLayout linearLayout;
	private EditText inpSite;
	private EditText inpZone;
	private Button btnRefresh;
	private int site = 19829;
	private int zone = 88269;
	private Handler uiHandler = new Handler();
	private ImageView defaultImage = null;
	
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.main);

        adserverView = new MASTAdView(this, site, zone);
        
        // Use download delegate implemenation in this class in order to take actoin
        // on successful or failed ad download events.
        adserverView.getAdDelegate().setAdDownloadHandler(this);
        
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
        
        adserverView.setId(1);
        setAdLayoutParams();
        linearLayout.addView(adserverView);
		adserverView.update();
		
        LinearLayout frameMain = (LinearLayout) findViewById(R.id.frameMain);
        BitmapDrawable background = (BitmapDrawable)getResources().getDrawable(R.drawable.repeat_bg);
        background.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        frameMain.setBackgroundDrawable(background);
        
        // Add explanatory message to bottom
        TextView note = new TextView(this);
        note.setText("Use valid zone (ex 88269) or broken zone (ex 158514) to see error handling.");
        note.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(20, 20, 20, 20);
        note.setLayoutParams(lp);
        frameMain.addView(note);
    }
    
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setAdLayoutParams();
		adserverView.update();
	}

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
		
		// Min size can be useful, but if you don't have ads large enough for all devices, it
		// can result in no ad being shown, so use it sparingly.
        //adserverView.setMinSizeX(metrics.widthPixels);
        //adserverView.setMinSizeY(height);
		
        adserverView.getAdRequest().setProperty(MASTAdRequest.parameter_size_x, metrics.widthPixels);
        adserverView.getAdRequest().setProperty(MASTAdRequest.parameter_size_y, height);
		adserverView.requestLayout();
	}


    private void setImageLayoutParams()
    {
		// Copy ad layout params to image layout
		ViewGroup.LayoutParams alp = adserverView.getLayoutParams();

		ViewGroup.LayoutParams ilp = defaultImage.getLayoutParams();
		if (ilp == null) {
			ilp = new ViewGroup.LayoutParams(alp.width, alp.height);
		} else {
			ilp.width = alp.width;
			ilp.height = alp.height;
		}
		defaultImage.setLayoutParams(ilp);
    }
    
    private ImageView getImageToShowOnError()
	{
		if (defaultImage == null)
		{
	        // Create image view to show static image if no ad available
	        defaultImage = new ImageView(this);
	        defaultImage.setImageDrawable(getResources().getDrawable(R.drawable.default_banner));
	        setImageLayoutParams();
	        defaultImage.setScaleType(ImageView.ScaleType.FIT_XY);
		}
		
		return defaultImage;
	}
	
    

	/*
	 * The following section implements the ad download delegate interface for custom error handling 
	 */
	
	
	@Override
	public void onDownloadBegin(MASTAdView sender) {
		return; // Do nothing
	}

	@Override
	public void onDownloadEnd(final MASTAdView sender) {
		// This must run on the UI thread
		uiHandler.post(new Runnable()
		{
			public void run()
			{
				ImageView iv = getImageToShowOnError();
				ViewGroup adParent = (ViewGroup) iv.getParent();
				if (adParent != null)
				{
					// Swap image and ad view
					int index = adParent.indexOfChild(iv);
					adParent.removeView(iv);
					adParent.addView(adserverView, index);
				}
			}
		});
	}

	@Override
	public void onAdViewable(MASTAdView sender) {
		return; // Do nothing
	}

	@Override
	public void onDownloadError(final MASTAdView sender, String error) {
		// This must run on the UI thread
		uiHandler.post(new Runnable()
		{
			public void run()
			{
				ImageView iv = getImageToShowOnError();
				ViewGroup adParent = (ViewGroup) adserverView.getParent();
				if (adParent != null)
				{
					// Swap image and ad view
					int index = adParent.indexOfChild(adserverView);
					adParent.removeView(adserverView);
					adParent.addView(iv, index);
				}	
			}
		});
	}	
}
