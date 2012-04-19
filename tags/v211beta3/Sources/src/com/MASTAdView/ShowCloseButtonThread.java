package com.MASTAdView;

import android.app.Dialog;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

public class ShowCloseButtonThread extends Thread {
	private Handler handler;
	private Button btnClose;
	private Integer showCloseButtonTime;
	Dialog dialog;

	public ShowCloseButtonThread(Handler handler, Dialog dialog, Button btnClose, Integer showCloseButtonTime) {
		this.handler = handler;
		this.btnClose = btnClose;
		this.showCloseButtonTime = showCloseButtonTime;
		this.dialog = dialog;
	}

	@Override
	public void run() {
		//dialog.setCancelable(true);
		try { Thread.sleep(showCloseButtonTime * 1000); } catch (Exception e) {}		
		if((handler != null) && (btnClose != null)) {
			dialog.setCancelable(true);
			handler.post(new VisibilityButtonRunnable(btnClose, View.VISIBLE));			
		} else dialog.setCancelable(true);
	}
}