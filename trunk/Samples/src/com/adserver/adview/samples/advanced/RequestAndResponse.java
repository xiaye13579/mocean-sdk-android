package com.adserver.adview.samples.advanced;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.adserver.adview.MASTAdServerView;
import com.adserver.adview.MASTAdServerViewCore.MASTOnAdDownload;
import com.adserver.adview.samples.R;

public class RequestAndResponse extends Activity {
	private Context context;
	private Handler handler = new Handler();
	private MASTAdServerView adserverView;
	private LinearLayout linearLayout;
	private EditText inpSite;
	private EditText inpZone;
	private Button btnRefresh;
	private int site = 19829;
	private int zone = 88269;
	private RadioGroup rgTypes;
	private RadioButton rbtnRequest;
	private RadioButton rbtnResponse;
	private TextView lblStatistics;
	private TextView lblTextRequestResponse;
	private int countRequests = 0;
	private int countResponses = 0;
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
			        adserverView.setSite(site);
			        adserverView.setZone(zone);
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
    	
        adserverView = new MASTAdServerView(this, site, zone);
        adserverView.setId(1);
        adserverView.setContentAlignment(true);
        setAdLayoutParams();
        
        
        adserverView.setOnAdDownload(new MASTOnAdDownload() {
			@Override
			public void error(final MASTAdServerView sender, final String error) {
				handler.post(new Runnable(){
					@Override
					public void run() {
						countErrors++;
						lblStatistics.setText(res.getString(R.string.requests_responses_errors, countRequests, countResponses, countErrors));
						responseText = sender.GetLastResponse();

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
			public void end(MASTAdServerView sender) {
				countResponses++;
				lblStatistics.setText(res.getString(R.string.requests_responses_errors, countRequests, countResponses, countErrors));
				responseText = sender.GetLastResponse();
				requestText = sender.GetLastRequest();

				if (rbtnRequest.isChecked()) {
					lblTextRequestResponse.setText(requestText);
				}
				if (rbtnResponse.isChecked()) {
					lblTextRequestResponse.setText(responseText);
				}
			}
			
			@Override
			public void begin(MASTAdServerView sender) {
				countRequests++;
				lblStatistics.setText(res.getString(R.string.requests_responses_errors, countRequests, countResponses, countErrors));
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
		
        //adserverView.setMinSizeX(metrics.widthPixels);
        //adserverView.setMinSizeY(height);
        adserverView.setMaxSizeX(metrics.widthPixels);
        adserverView.setMaxSizeY(height);
		adserverView.requestLayout();
	}
	
}