package com.pellcorp.android.transact.wifi;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class NetworkManagement {
	private final WifiManager wifiManager;
	
	public NetworkManagement(final WifiManager wifiManager) {
		this.wifiManager = wifiManager;
	}
	
	public boolean isWifiConnected() {
	    WifiInfo networkInfo = wifiManager.getConnectionInfo();
	    return networkInfo != null;
	}
}
