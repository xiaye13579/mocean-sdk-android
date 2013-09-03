//
// Copyright (C) 2013 Mocean Mobile. All Rights Reserved. 
//
package com.moceanmobile.mast;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class BrowserDialog extends Dialog 
{
	static private final int ActionBarHeightDp = 50;
	
	private final Handler handler;
	private String url = null;
	private ImageButton backButton = null;
	private ImageButton forwardButton = null;
	private android.webkit.WebView webView = null;
	
	@SuppressWarnings("deprecation")
	public BrowserDialog(Context context, String url, Handler handler)
	{
		super(context, android.R.style.Theme_Black_NoTitleBar);
		
		this.url = url;
		this.handler = handler;
		
		Resources resources = getContext().getResources();
		
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		RelativeLayout contentView = new RelativeLayout(getContext());
		contentView.setBackgroundColor(0xffffffff);
		setContentView(contentView, layoutParams);
		
		RelativeLayout.LayoutParams actionBarLayoutParams =
				new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, MASTAdView.dpToPx(ActionBarHeightDp));
		actionBarLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		LinearLayout actionBar = new LinearLayout(getContext());
		actionBar.setId(100);
		actionBar.setBackgroundDrawable(new BitmapDrawable(resources, BrowserDialog.class.getResourceAsStream("/ib_bg_down.png")));
		actionBar.setOrientation(LinearLayout.HORIZONTAL);
		actionBar.setVerticalGravity(Gravity.CENTER_VERTICAL); 
		contentView.addView(actionBar, actionBarLayoutParams);
		
		LinearLayout.LayoutParams imageButtonLayout = new LinearLayout.LayoutParams(0, 
				LayoutParams.WRAP_CONTENT, 1);
		
		ImageButton imageButton = new ImageButton(getContext());
		imageButton.setImageDrawable(new BitmapDrawable(resources, BrowserDialog.class.getResourceAsStream("/ib_close_regular.png")));
		imageButton.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				BrowserDialog.this.dismiss();
			}
		});
		actionBar.addView(imageButton, imageButtonLayout);
		
		backButton = new ImageButton(getContext());
		backButton.setImageDrawable(new BitmapDrawable(resources, BrowserDialog.class.getResourceAsStream("/ib_arrow_left_regular.png")));
		backButton.setEnabled(false);
		backButton.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				webView.goBack();
			}
		});
		actionBar.addView(backButton, imageButtonLayout);
		
		forwardButton = new ImageButton(getContext());
		forwardButton.setImageDrawable(new BitmapDrawable(resources, BrowserDialog.class.getResourceAsStream("/ib_arrow_right_regular.png")));
		forwardButton.setEnabled(false);
		forwardButton.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				webView.goForward();
			}
		});
		actionBar.addView(forwardButton, imageButtonLayout);
		
		imageButton = new ImageButton(getContext());
		imageButton.setImageDrawable(new BitmapDrawable(resources, BrowserDialog.class.getResourceAsStream("/ib_apdate_regular.png")));
		imageButton.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				webView.reload();
			}
		});
		actionBar.addView(imageButton, imageButtonLayout);
		
		imageButton = new ImageButton(getContext());
		imageButton.setImageDrawable(new BitmapDrawable(resources, BrowserDialog.class.getResourceAsStream("/ib_window_regular.png")));
		imageButton.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				BrowserDialog.this.handler.browserDialogOpenUrl(BrowserDialog.this, webView.getUrl());
			}
		});
		actionBar.addView(imageButton, imageButtonLayout);
		
		RelativeLayout.LayoutParams webViewLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
		webViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		webViewLayoutParams.addRule(RelativeLayout.ABOVE, actionBar.getId());
		webView = new android.webkit.WebView(getContext());
		webView.setWebViewClient(new Client());
		contentView.addView(webView, webViewLayoutParams);
		
		setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface dialog)
			{
				BrowserDialog.this.handler.browserDialogDismissed(BrowserDialog.this);
			}
		});
	}
	
	public void loadUrl(String url2)
	{
		webView.stopLoading();
		webView.clearHistory();
		webView.loadUrl(url);
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		webView.loadUrl(url);
	}
	
	private class Client extends WebViewClient
	{
		@Override
		public void onPageFinished(android.webkit.WebView view, String url)
		{
			backButton.setEnabled(view.canGoBack());
			forwardButton.setEnabled(view.canGoForward());
		}
	}
	
	public interface Handler
	{
		public void browserDialogDismissed(BrowserDialog browserDialog);
		public void browserDialogOpenUrl(BrowserDialog browserDialog, String url);
	}
}
