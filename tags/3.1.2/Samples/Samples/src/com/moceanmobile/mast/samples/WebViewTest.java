package com.moceanmobile.mast.samples;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

public class WebViewTest extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view_test);
		
		final TextView urlText = (TextView) findViewById(R.id.url_text);
		final Button goButton = (Button) findViewById(R.id.go_button);
		final WebView webView = (WebView) findViewById(R.id.webview);
		
		goButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				loadUrl();
			}
		});		
		
		urlText.setOnKeyListener(new OnKeyListener()
		{
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if (keyCode == KeyEvent.KEYCODE_ENTER)
				{
					loadUrl();
					return true;
				}
				
				return false;
			}
		});
		
		webView.setWebViewClient(new WebViewClient()
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			{
				return false;
			}
		});
	}
	
	private void loadUrl()
	{
		final TextView urlText = (TextView) findViewById(R.id.url_text);
		final WebView webView = (WebView) findViewById(R.id.webview);
		
		try
		{
			String txt = urlText.getText().toString();
			if (txt.startsWith("http") == false)
			{
				txt = "http://" + txt;
				urlText.setText(txt);
			}
			
			webView.clearView();
			webView.clearHistory();
			webView.loadUrl(txt);
		}
		catch (Exception ex)
		{
			ex.printStackTrace(System.err);
		}
		
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(urlText.getWindowToken(), 0);
	}
}
