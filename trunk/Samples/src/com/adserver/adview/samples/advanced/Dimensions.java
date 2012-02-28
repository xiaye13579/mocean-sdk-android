package com.adserver.adview.samples.advanced;

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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.adserver.adview.MASTAdServerView;
import com.adserver.adview.samples.R;

public class Dimensions extends Activity {
	private MASTAdServerView adserverView;
	private LinearLayout linearLayout;
	private EditText inpSite;
	private EditText inpZone;
	private Button btnRefresh;
	private int site = 19829;
	private int zone = 88269;
    private int dimensionsWidth = 0;
    private int dimensionsHeight = 0;
    private int dimensionsX = 0;
    private int dimensionsY = 0;
	
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
			        adserverView.setSite(site);
			        adserverView.setZone(zone);
					adserverView.update();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
        
        adserverView = new MASTAdServerView(this, site, zone);
        adserverView.setId(1);
        setAdLayoutParams();
        linearLayout.addView(adserverView);
        adserverView.setContentAlignment(true);
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
        	
        	int maxWidth = linearLayout.getWidth();
        	int maxHeight = linearLayout.getHeight();

        	TextView lbWidth = (TextView)dialog.findViewById(R.id.lbWidth);
        	lbWidth.setText(getString(R.string.width, dimensionsWidth));
        	TextView lbHeight = (TextView)dialog.findViewById(R.id.lbHeight);
        	lbHeight.setText(getString(R.string.height, dimensionsHeight));
        	TextView lbX = (TextView)dialog.findViewById(R.id.lbX);
        	lbX.setText(getString(R.string.x, dimensionsX));
        	TextView lbY = (TextView)dialog.findViewById(R.id.lbY);
        	lbY.setText(getString(R.string.y, dimensionsY));
        	
        	SeekBar sbWidth = (SeekBar)dialog.findViewById(R.id.sbWidth);
        	sbWidth.setMax(maxWidth);
        	sbWidth.setProgress(dimensionsWidth);
        	sbWidth.setOnSeekBarChangeListener(new CustomOnSeekBarChangeListener(lbWidth, R.string.width));
        	
        	SeekBar sbHeight = (SeekBar)dialog.findViewById(R.id.sbHeight);
        	sbHeight.setMax(maxHeight);
        	sbHeight.setProgress(dimensionsHeight);
        	sbHeight.setOnSeekBarChangeListener(new CustomOnSeekBarChangeListener(lbHeight, R.string.height));
        	
        	SeekBar sbX = (SeekBar)dialog.findViewById(R.id.sbX);
        	sbX.setMax(maxWidth - 50);
        	sbX.setProgress(dimensionsX);
        	sbX.setOnSeekBarChangeListener(new CustomOnSeekBarChangeListener(lbX, R.string.x));
        	
        	SeekBar sbY = (SeekBar)dialog.findViewById(R.id.sbY);
        	sbY.setMax(maxHeight - 50);
        	sbY.setProgress(dimensionsY);
        	sbY.setOnSeekBarChangeListener(new CustomOnSeekBarChangeListener(lbY, R.string.y));

        	Button btnOk = (Button)dialog.findViewById(R.id.btnOk);
        	btnOk.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.cancel();
					
					ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)adserverView.getLayoutParams();
					lp.width = dimensionsWidth;
					lp.height = dimensionsHeight;
					lp.leftMargin = dimensionsX;
					lp.topMargin = dimensionsY;
					linearLayout.requestLayout();
				}
			});
        	
        	dialog.show();        	
        }
        return false;
    }
	
    private class CustomOnSeekBarChangeListener implements OnSeekBarChangeListener {
    	TextView label;
    	int textRes;
    	
		public CustomOnSeekBarChangeListener(TextView label, int textRes) {
			this.label = label;
			this.textRes = textRes;
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (textRes == R.string.width) {
				dimensionsWidth = progress;
			} else if (textRes == R.string.height) {
				dimensionsHeight = progress;
			} else if (textRes == R.string.x) {
				dimensionsX = progress;
			} else if (textRes == R.string.y) {
				dimensionsY = progress;
			}
			label.setText(getString(textRes, progress));
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			
		}
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
			lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.FILL_PARENT, height);
			adserverView.setLayoutParams(lp);
		}
		
        adserverView.setMinSizeX(metrics.widthPixels);
        adserverView.setMinSizeY(height);
        adserverView.setMaxSizeX(metrics.widthPixels);
        adserverView.setMaxSizeY(height);
		adserverView.requestLayout();
	}
	
}