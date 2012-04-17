package com.pellcorp.android.transact.sshtunnel;

public class SshHost {
	private final String hostName;
	private final int port;
	
	public SshHost(final String hostName, final int port) {
		this.hostName = hostName;
		this.port = port;
	}

	public String getHostName() {
		return hostName;
	}

	public int getPort() {
		return port;
	}
}
