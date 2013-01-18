package com.MASTAdView.samples;

import java.util.Vector;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;

import com.MASTAdView.MASTAdLog;

public class LogViewer extends Activity
{
	private EditText logView = null;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setContentView(R.layout.view_logs);
        
        logView = (EditText)findViewById(R.id.logTextView);
        logView.setKeyListener(null);
    }

    
    @Override
	public void onResume()
    {
    	super.onResume();
    	Vector<String> logMessages = MASTAdLog.getInternalLogs();
    	if (logMessages != null)
    	{
    		StringBuffer sb = new StringBuffer();
    		
    		for (int n = 0; n < logMessages.size(); n++)
    		{
    			sb.append(logMessages.get(n));
    			sb.append("\r\n");
    		}
    		
    		logView.setText(sb.toString());
    	}
    }
}
