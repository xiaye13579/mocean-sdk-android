package com.adserver.adview.ormma.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.adserver.adview.ormma.OrmmaDisplayController;


public class OrmmaConfigurationBroadcastReceiver extends BroadcastReceiver {
	private OrmmaDisplayController mOrmmaDisplayController;
	private int mLastOrientation;

	public OrmmaConfigurationBroadcastReceiver(OrmmaDisplayController ormmaDisplayController) {
		mOrmmaDisplayController = ormmaDisplayController;
		mLastOrientation = mOrmmaDisplayController.getOrientation();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_CONFIGURATION_CHANGED)){
			int orientation = mOrmmaDisplayController.getOrientation();
			if (orientation != mLastOrientation){
				mLastOrientation = orientation;
				mOrmmaDisplayController.onOrientationChanged(mLastOrientation);
			}
		}
	}

}
