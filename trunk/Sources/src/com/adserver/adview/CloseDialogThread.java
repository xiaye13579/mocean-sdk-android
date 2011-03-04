/*© 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.*/
package com.adserver.adview;

import android.app.Dialog;
import android.os.Handler;

public class CloseDialogThread extends Thread {
	private Handler handler;
	private Dialog dialog;
	private Integer autoCloseInterstitialTime;

	public CloseDialogThread(Handler handler, Dialog dialog, Integer autoCloseInterstitialTime) {
		this.handler = handler;
		this.dialog = dialog;
		this.autoCloseInterstitialTime = autoCloseInterstitialTime;
	}

	@Override
	public void run() {
		if((handler != null) && (dialog != null)) {
			try { Thread.sleep(autoCloseInterstitialTime * 1000); } catch (Exception e) {}
			handler.post(new CloseDialogRunnable(dialog));
		}
	}
}