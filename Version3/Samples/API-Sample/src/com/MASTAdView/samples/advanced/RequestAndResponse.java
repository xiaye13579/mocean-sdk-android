package com.MASTAdView.samples.advanced;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.MASTAdView.MASTAdDelegate;
import com.MASTAdView.MASTAdRequest;
import com.MASTAdView.MASTAdView;
import com.MASTAdView.samples.R;


public class RequestAndResponse extends Activity {
	private Context context;
	private Handler handler = new Handler();
	private MASTAdView adserverView;
	private LinearLayout linearLayout;
	private EditText inpSite;
	private EditText inpZone;
	private Button btnRefresh;
	private int site = 19829;
	private int zone = 102238; // all ads on rotation, images and rich media
	private RadioGroup rgTypes;
	private RadioButton rbtnRequest;
	private RadioButton rbtnResponse;
	private TextView lblStatistics;
	private TextView lblTextRequestResponse;
	private int countRequests = 0;
	private int countResponses = -1;
	private int countErrors = 0;
	private Resources res;
	private String requestText;
	private String responseText;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.request_and_response);
        context = this;
        res = getResources();
        
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
        
        rgTypes = (RadioGroup) findViewById(R.id.rgTypes);
        rbtnRequest = (RadioButton) findViewById(R.id.rbtnRequest);
        rbtnRequest.setChecked(true);
    	rbtnResponse = (RadioButton) findViewById(R.id.rbtnResponse);
    	lblStatistics = (TextView) findViewById(R.id.lblStatistics);
		lblStatistics.setText(res.getString(R.string.requests_responses_errors, countRequests, countResponses, countErrors));
    	lblTextRequestResponse = (TextView) findViewById(R.id.lblTextRequestResponse);
    	
        adserverView = new MASTAdView(this, site, zone);
        adserverView.setId(1);
        //adserverView.setContentAlignment(true);
        setAdLayoutParams();
        
        
        adserverView.getAdDelegate().setAdDownloadHandler(new MASTAdDelegate.AdDownloadEventHandler() {
			
			@Override
			public void onDownloadError(final MASTAdView sender, final String error) {
				// Anything that updates UI needs to be run via handler so it is done
				// on the UI thread.
				handler.post(new Runnable(){
					@Override
					public void run() {
						countErrors++;
						lblStatistics.setText(res.getString(R.string.requests_responses_errors, countRequests, countResponses, countErrors));
						responseText = sender.getLastResponse();

						if (rbtnResponse.isChecked()) {
							lblTextRequestResponse.setText(responseText);
						}
						
				    	AlertDialog.Builder builder = new AlertDialog.Builder(context)
						.setTitle(R.string.error)
						.setMessage(error);

				    	DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.dismiss();
							}
						};
							
						builder.setPositiveButton(context.getResources().getString(R.string.ok), okListener);
						builder.create().show();
					}
				});
			}
			
			@Override
			public void onDownloadEnd(final MASTAdView sender) {
				// Anything that updates UI needs to be run via handler so it is done
				// on the UI thread.
				handler.post(new Runnable(){
					@Override
					public void run() {
						countResponses++;
						lblStatistics.setText(res.getString(R.string.requests_responses_errors, countRequests, countResponses, countErrors));
						responseText = sender.getLastResponse();
						requestText = sender.getLastRequest();
		
						if (rbtnRequest.isChecked()) {
							lblTextRequestResponse.setText(requestText);
						}
						if (rbtnResponse.isChecked()) {
							lblTextRequestResponse.setText(responseText);
						}
					}
				});
			}
			
			@Override
			public void onDownloadBegin(MASTAdView arg0) {
				// Anything that updates UI needs to be run via handler so it is done
				// on the UI thread.
				handler.post(new Runnable(){
					@Override
					public void run() {
						countRequests++;
						lblStatistics.setText(res.getString(R.string.requests_responses_errors, countRequests, countResponses, countErrors));
					}
				});
			}

			@Override
			public void onAdViewable(MASTAdView arg0) {
				// TODO Auto-generated method stub
				
			}
		});        
        
        linearLayout.addView(adserverView);
		adserverView.update();
        
		rgTypes.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.rbtnRequest) {
					lblTextRequestResponse.setText(requestText);
				} else {
					lblTextRequestResponse.setText(responseText);
				}
			}
		});
		
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
	
}