package com.pellcorp.android.transact.android;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkManagement {
	private final ConnectivityManager connectivityManager;
	
	public NetworkManagement(final ConnectivityManager connectivityManager) {
		this.connectivityManager = connectivityManager;
	}
	
	public boolean isWifiConnected() {
	    NetworkInfo networkInfo = null;
	    if (connectivityManager != null) {
	        networkInfo =
	            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    }
	    return networkInfo == null ? false : networkInfo.isConnected();
	}
}
