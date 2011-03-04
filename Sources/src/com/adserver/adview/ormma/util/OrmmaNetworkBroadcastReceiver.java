/*© 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.*/
package com.adserver.adview.ormma.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.adserver.adview.ormma.OrmmaNetworkController;

public class OrmmaNetworkBroadcastReceiver extends BroadcastReceiver {
	private OrmmaNetworkController mOrmmaNetworkController;

	public OrmmaNetworkBroadcastReceiver(OrmmaNetworkController ormmaNetworkController) {
		mOrmmaNetworkController = ormmaNetworkController;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
			mOrmmaNetworkController.onConnectionChanged();
		}
	}
	
}
