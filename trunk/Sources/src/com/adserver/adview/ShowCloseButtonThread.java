/*© 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.*/
package com.adserver.adview;

import android.os.Handler;
import android.view.View;
import android.widget.Button;

public class ShowCloseButtonThread extends Thread {
	private Handler handler;
	private Button btnClose;
	private Integer showCloseButtonTime;

	public ShowCloseButtonThread(Handler handler, Button btnClose, Integer showCloseButtonTime) {
		this.handler = handler;
		this.btnClose = btnClose;
		this.showCloseButtonTime = showCloseButtonTime;
	}

	@Override
	public void run() {
		if((handler != null) && (btnClose != null)) {
			try { Thread.sleep(showCloseButtonTime * 1000); } catch (Exception e) {}
			handler.post(new VisibilityButtonRunnable(btnClose, View.VISIBLE));
		}
	}
}