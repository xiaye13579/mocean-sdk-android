package com.adserver.adview;

import android.app.Dialog;

public class CloseDialogRunnable implements Runnable {
	private Dialog dialog;

	public CloseDialogRunnable(Dialog dialog) {
		this.dialog = dialog;
	}

	public void run() {
		if(dialog != null) {
			dialog.dismiss();
		}
	}
}