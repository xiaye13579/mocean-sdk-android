package com.MASTAdView.samples;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.MASTAdView.MASTAdRequest;
import com.MASTAdView.MASTAdView;



// XXX take min size and viewport parameters out of UI



public class Dimensions extends Activity {
	private MASTAdView adserverView;
	private LinearLayout linearLayout;
	private EditText inpSite;
	private EditText inpZone;
	private Button btnRefresh;
	private int site = 19829;
	private int zone = 152280;
    private int dimensionsWidth = -1;
    private int dimensionsHeight = -1;
    private int dimensionsX = 0;
    private int dimensionsY = 0;
    private boolean useInternalBrowser = false;
    
    
    // Custom viewport options
    public static final int INJECTION_CODE_VARIATION_DEFAULT 	= 0;
    public static final int INJECTION_CODE_VARIATION_NONE		= 1;
    public static final int INJECTION_CODE_VARIATION_VIEWPORT	= 2;
    
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.dimensions);
        
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
			        setAdLayoutParams();
					adserverView.update();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    
        
        adserverView = new MASTAdView(this, site, zone);
        adserverView.setId(1);

        setAdLayoutParams();
        linearLayout.addView(adserverView);
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
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dimensions, menu);
        return true;
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
        if (item.getItemId() == R.id.miShowDialog) {
        	final Dialog dialog = new Dialog(this);
        	dialog.setContentView(R.layout.dimensions_dialog);
        	dialog.setTitle(R.string.dimensions);
        	
    		dimensionsWidth = adserverView.getWidth();
    		dimensionsHeight = adserverView.getHeight();

    		final EditText sbWidth = (EditText)dialog.findViewById(R.id.sbWidth);
        	sbWidth.setText("" + dimensionsWidth);
        	
        	final EditText sbHeight = (EditText)dialog.findViewById(R.id.sbHeight);
        	sbHeight.setText("" + dimensionsHeight);
        	
        	final EditText sbX = (EditText)dialog.findViewById(R.id.sbX);
        	sbX.setText("" + dimensionsX);
        	
        	final EditText sbY = (EditText)dialog.findViewById(R.id.sbY);
        	sbY.setText("" + dimensionsY);
        	
        	final CheckBox cbUseInternal = (CheckBox)dialog.findViewById(R.id.cbUseInternal);
        	
        	final CheckBox cbFillWidth = (CheckBox) dialog.findViewById(R.id.cbFillParentWidth);
        	cbFillWidth.setOnCheckedChangeListener(new OnCheckedChangeListener()
        	{
        	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        	    {
        	        if ( isChecked )
        	        {
        	        	WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        				DisplayMetrics metrics = new DisplayMetrics();
        				windowManager.getDefaultDisplay().getMetrics(metrics);
        				sbWidth.setText("" + metrics.widthPixels);
        				
        	        	sbWidth.setEnabled(false);
        	        }
        	        else
        	        {
        	        	sbWidth.setEnabled(true);
        	        }

        	    }
        	});
        	
        	final CheckBox cbFillHeight = (CheckBox) dialog.findViewById(R.id.cbFillParentHeight);
        	cbFillHeight.setOnCheckedChangeListener(new OnCheckedChangeListener()
        	{
        	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        	    {
        	        if ( isChecked )
        	        {
        	        	WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        				DisplayMetrics metrics = new DisplayMetrics();
        				windowManager.getDefaultDisplay().getMetrics(metrics);
        				sbHeight.setText("" + metrics.heightPixels);
        				
        				sbHeight.setEnabled(false);
        	        }
        	        else
        	        {
        	        	sbHeight.setEnabled(true);
        	        }

        	    }
        	});


    		final Activity thisActivity = this;
        	Button btnOk = (Button)dialog.findViewById(R.id.btnOk);
        	btnOk.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.cancel();
					
					ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)adserverView.getLayoutParams();
					
					MASTAdView newAdserverView = new MASTAdView(thisActivity, site, zone);
					newAdserverView.setId(1);
			        
					int val = Integer.parseInt(sbWidth.getText().toString());
					dimensionsWidth = val;
					lp.width = dimensionsWidth;
					newAdserverView.getAdRequest().setProperty(MASTAdRequest.parameter_size_x, dimensionsWidth);
					
					val = Integer.parseInt(sbHeight.getText().toString());
					dimensionsHeight = val;
					lp.height = dimensionsHeight;
					newAdserverView.getAdRequest().setProperty(MASTAdRequest.parameter_size_y, dimensionsHeight);
					
					if (cbUseInternal.isChecked())
					{
						useInternalBrowser = true;
					}
					else
					{
						useInternalBrowser = false;
					}
					
					val = Integer.parseInt(sbX.getText().toString());
					dimensionsX = val;
					lp.leftMargin = dimensionsX;
					
					val = Integer.parseInt(sbY.getText().toString());
					dimensionsY = val;
					lp.topMargin = dimensionsY;
					
					newAdserverView.setLayoutParams(lp);
					
					int index = linearLayout.indexOfChild(adserverView);
					linearLayout.removeView(adserverView);
					adserverView = newAdserverView;
					adserverView.update();
					linearLayout.addView(adserverView, index);
					linearLayout.requestLayout();
				}
			});
        	
        	dialog.show();        	
        }
        return false;
    }
    
	private void setAdLayoutParams()
	{
		if ((dimensionsHeight < 0) || (dimensionsWidth < 0))
		{
			WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics metrics = new DisplayMetrics();
			windowManager.getDefaultDisplay().getMetrics(metrics);
			int height = 50;
			
			int maxSize = metrics.heightPixels;
			if (maxSize < metrics.widthPixels) {
				maxSize = metrics.widthPixels;
			}
			
			if ((maxSize > 480) && (maxSize <= 800)) {
				height = 100;
			} else if (maxSize > 800) {
				height = 120;
			}
			dimensionsHeight = height;
			dimensionsWidth = metrics.widthPixels;
		}
		
		ViewGroup.LayoutParams lp = adserverView.getLayoutParams();
		if (lp == null) {
			//lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.FILL_PARENT, height);
			lp = new ViewGroup.MarginLayoutParams(dimensionsWidth, dimensionsHeight);
			adserverView.setLayoutParams(lp);
		}
		

		// Set internal browser properties form checkbox values
		//adserverView.setInternalBrowser(useInternalBrowser);
		
        adserverView.getAdRequest().setProperty(MASTAdRequest.parameter_size_x, dimensionsWidth);
        lp.width = dimensionsWidth;
        adserverView.getAdRequest().setProperty(MASTAdRequest.parameter_size_y, dimensionsHeight);
        lp.height = dimensionsHeight;
        
        adserverView.setLayoutParams(lp);
        adserverView.requestLayout();
	}
	
}