package com.adserver.adview.samples.advanced;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adserver.adview.MASTAdServerView;
import com.adserver.adview.MASTAdServerViewCore.MASTOnAdDownload;
import com.adserver.adview.samples.R;

public class CustomParameters extends Activity {
	private Context context;
	private LinearLayout linearLayout;
	MASTAdServerView adServerView;
	public Handler activityHandler = new Handler();
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.custom_parameters);
        
        context = this;
        linearLayout = (LinearLayout) findViewById(R.id.frameAdContent);
        adServerView = (MASTAdServerView) findViewById(R.id.adServerView);
        
        final Button btnUpdate =  ((Button)findViewById(R.id.btnUpdate));
        
        adServerView.setOnAdDownload(new MASTOnAdDownload() {
			
			@Override
			public void error(MASTAdServerView sender,String arg0) {
				activityHandler.post(updateUI);
				
			}
			
			@Override
			public void end(MASTAdServerView sender) {
				activityHandler.post(updateUI);
				
			}
			
			@Override
			public void begin(MASTAdServerView sender) {
				//activityHandler.post(updateUI);				
			}
		});
        
        btnUpdate.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			try
			{
				btnUpdate.setText("Start updating");
				btnUpdate.setEnabled(false);
			int site = Integer.parseInt(((EditText) findViewById(R.id.etSite)).getEditableText().toString());
			int zone = Integer.parseInt(((EditText) findViewById(R.id.etZone)).getEditableText().toString());
			
			adServerView.setSite(site);
			adServerView.setZone(zone);
			adServerView.update();
			}catch (Exception e) {
				ShowAllertDialog("Error", e.getMessage());
			}
		}
       });       
    }
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
    
    Runnable updateUI = new Runnable() {
		@Override
		public void run() {
			((TextView)findViewById(R.id.txtToServer)).setText("Request:\n"+adServerView.GetLastRequest());
			((TextView)findViewById(R.id.txtFromServer)).setText("Response:\n"+adServerView.GetLastResponse());
			 Button btnUpdate =  ((Button)findViewById(R.id.btnUpdate));
			btnUpdate.setText("Done!Update Me!");
			btnUpdate.setEnabled(true);
		}		
	};
	
	public void ShowAllertDialog(String title, String message)
	{
		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		alertbox.setTitle(title);
        alertbox.setMessage(message);
        alertbox.setNeutralButton("Ok", null);

        alertbox.show();	
	}
}
