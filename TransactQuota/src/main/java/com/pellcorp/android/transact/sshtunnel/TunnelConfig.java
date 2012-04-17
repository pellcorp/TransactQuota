package com.pellcorp.android.transact.sshtunnel;

import java.io.File;

import org.apache.http.HttpHost;

public class TunnelConfig {
	private final File privateKey;
	private final String username;
	private final SshHost tunnelHost;
	private final HttpHost proxyHost;
	
	public TunnelConfig(
			final SshHost tunnelHost, 
			final HttpHost proxyHost,  
			final String username, 
			final File privateKey) {
		
		this.tunnelHost = tunnelHost;
		this.proxyHost = proxyHost;
		
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
	
	public HttpHost getProxyHost() {
		return proxyHost;
	}
}