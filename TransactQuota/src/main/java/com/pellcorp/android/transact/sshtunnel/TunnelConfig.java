package com.pellcorp.android.transact.sshtunnel;

import java.io.File;

public class TunnelConfig {
	private final File privateKey;
	private final String username;
	private final SshHost tunnelHost;
	
	public TunnelConfig(
			final SshHost tunnelHost, 
			final String username, 
			final File privateKey) {
		
		this.tunnelHost = tunnelHost;
		
		this.username = username;
		this.privateKey = privateKey;
	}

	public File getPrivateKey() {
		return privateKey;
	}
	
	public String getUsername() {
		return username;
	}
	
	public SshHost getTunnelHost() {
		return tunnelHost;
	}
}
