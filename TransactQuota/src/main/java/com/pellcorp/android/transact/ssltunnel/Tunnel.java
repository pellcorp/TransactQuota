package com.pellcorp.android.transact.ssltunnel;

import org.apache.http.HttpHost;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class Tunnel {
	private final TunnelConfig tunnelConfig;
	private final JSch jsch = new JSch();
	private Session session;
	
	public Tunnel(final TunnelConfig tunnelConfig) throws JSchException {
		this.tunnelConfig = tunnelConfig;
		
		jsch.addIdentity(tunnelConfig.getPrivateKey().getAbsolutePath(), "");
	}
	
	public HttpHost connect() throws JSchException {
		if (session != null) {
			throw new IllegalStateException("Session already established");
		}
		
		session = jsch.getSession(
				tunnelConfig.getUsername(), 
				tunnelConfig.getTunnelHost().getHostName(), 
				tunnelConfig.getTunnelHost().getPort());
		
		session.setConfig("StrictHostKeyChecking", "no");

		session.connect();

		int port = session.setPortForwardingL(
				0, // Auto assign a local port 
				tunnelConfig.getProxyHost().getHostName(), 
				tunnelConfig.getProxyHost().getPort());
		
		return new HttpHost("127.0.0.1", port);
	}
	
	public void disconnect() {
		try {
			session.disconnect();
		} finally {
			session = null;
		}
	}
}
