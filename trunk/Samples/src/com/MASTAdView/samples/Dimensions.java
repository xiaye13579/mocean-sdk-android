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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.MASTAdView.MASTAdView;
import com.MASTAdView.samples.R;
import com.MASTAdView.samples.R.drawable;
import com.MASTAdView.samples.R.id;
import com.MASTAdView.samples.R.layout;
import com.MASTAdView.samples.R.menu;
import com.MASTAdView.samples.R.string;


public class Dimensions extends Activity {
	private MASTAdView adserverView;
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
    private int dimensionsMinWidth = -1;
    private int dimensionsMinHeight = -1;
    private boolean isContentAligned = false;
    private boolean useInternalBrowser = false;
    
    
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

        	//TextView lbWidth = (TextView)dialog.findViewById(R.id.lbWidth);
        	//lbWidth.setText(getString(R.string.width, dimensionsWidth));
        	//TextView lbHeight = (TextView)dialog.findViewById(R.id.lbHeight);
        	//lbHeight.setText(getString(R.string.height, dimensionsHeight));
        	//TextView lbX = (TextView)dialog.findViewById(R.id.lbX);
        	//lbX.setText(getString(R.string.x, dimensionsX));
        	//TextView lbY = (TextView)dialog.findViewById(R.id.lbY);
        	//lbY.setText(getString(R.string.y, dimensionsY));
        	
        	final EditText sbWidth = (EditText)dialog.findViewById(R.id.sbWidth);
        	sbWidth.setText("" + dimensionsWidth);
        	/*
        	SeekBar sbWidth = (SeekBar)dialog.findViewById(R.id.sbWidth);
        	sbWidth.setMax(maxWidth);
        	sbWidth.setProgress(dimensionsWidth);
        	sbWidth.setOnSeekBarChangeListener(new CustomOnSeekBarChangeListener(lbWidth, R.string.width));
        	*/
        	
        	final EditText sbHeight = (EditText)dialog.findViewById(R.id.sbHeight);
        	sbHeight.setText("" + dimensionsHeight);
        	/*
        	SeekBar sbHeight = (SeekBar)dialog.findViewById(R.id.sbHeight);
        	sbHeight.setMax(maxHeight);
        	sbHeight.setProgress(dimensionsHeight);
        	sbHeight.setOnSeekBarChangeListener(new CustomOnSeekBarChangeListener(lbHeight, R.string.height));
        	*/
        	
        	final EditText sbMinWidth = (EditText)dialog.findViewById(R.id.sbMinWidth);
        	//sbWidth.setText("" + dimensionsMinWidth);
        	
        	final EditText sbMinHeight = (EditText)dialog.findViewById(R.id.sbMinHeight);
        	//sbHeight.setText("" + dimensionsMinHeight);
        	
        	final EditText sbX = (EditText)dialog.findViewById(R.id.sbX);
        	sbX.setText("" + dimensionsX);
        	/*
        	SeekBar sbX = (SeekBar)dialog.findViewById(R.id.sbX);
        	sbX.setMax(maxWidth - 50);
        	sbX.setProgress(dimensionsX);
        	sbX.setOnSeekBarChangeListener(new CustomOnSeekBarChangeListener(lbX, R.string.x));
        	*/
        	
        	final EditText sbY = (EditText)dialog.findViewById(R.id.sbY);
        	sbY.setText("" + dimensionsY);
        	/*
        	SeekBar sbY = (SeekBar)dialog.findViewById(R.id.sbY);
        	sbY.setMax(maxHeight - 50);
        	sbY.setProgress(dimensionsY);
        	sbY.setOnSeekBarChangeListener(new CustomOnSeekBarChangeListener(lbY, R.string.y));
			*/
        
        	final CheckBox cbIsAligned = (CheckBox)dialog.findViewById(R.id.cbIsAligned);
        	final CheckBox cbUseInternal = (CheckBox)dialog.findViewById(R.id.cbUseInternal);
        	
        	Button btnOk = (Button)dialog.findViewById(R.id.btnOk);
        	btnOk.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.cancel();
					
					ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)adserverView.getLayoutParams();
					
					int val = Integer.parseInt(sbWidth.getText().toString());
					dimensionsWidth = val;
					lp.width = dimensionsWidth;
					
					val = Integer.parseInt(sbHeight.getText().toString());
					dimensionsHeight = val;
					lp.height = dimensionsHeight;
					
					String s = sbMinWidth.getText().toString();
					if ((s != null) && (s.length() > 0))
					{
						val = Integer.parseInt(s);
						dimensionsMinWidth = val;
					}
					else
					{
						dimensionsMinWidth = -1;
					}
					
					s = sbMinHeight.getText().toString();
					if ((s != null) && (s.length() > 0))
					{
						val = Integer.parseInt(s);
						dimensionsMinHeight = val;
					}
					else
					{
						dimensionsMinHeight = -1;
					}
					
					if (cbIsAligned.isChecked())
					{
						isContentAligned = true;
					}
					else
					{
						isContentAligned = false;
					}
					
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
					
					linearLayout.requestLayout();
				}
			});
        	
        	dialog.show();        	
        }
        return false;
    }
    
	private void setAdLayoutParams()
	{
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
		
		// Only set min width/height if value >= 0 provided by user
		if (dimensionsMinWidth >= 0)
		{
			adserverView.setMinSizeX(dimensionsMinWidth);
		}
		else
		{
			adserverView.setMinSizeX(null);
		}
		if (dimensionsMinHeight >= 0)
		{
			adserverView.setMinSizeY(dimensionsMinHeight);
		}
		else
		{
			adserverView.setMinSizeY(null);
		}
		

		// Set aligned and internal browser properties form checkbox values
		adserverView.setInternalBrowser(useInternalBrowser);
		adserverView.setContentAlignment(isContentAligned);
		
        adserverView.setMaxSizeX(metrics.widthPixels);
        adserverView.setMaxSizeY(height);

        adserverView.requestLayout();
	}
	
}