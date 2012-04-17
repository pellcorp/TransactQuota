package com.pellcorp.android.transact.sshtunnel;

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
	
	public HttpHost connect(HttpHost portForward) throws JSchException {
		init();

		int port = session.setPortForwardingL(
				0, // Auto assign a local port 
				portForward.getHostName(), 
				portForward.getPort());
		
		return new HttpHost("localhost", port);
	}

	protected Session init() throws JSchException {
		if (session != null) {
			throw new IllegalStateException("Session already established");
		}
		
		session = jsch.getSession(
				tunnelConfig.getUsername(), 
				tunnelConfig.getTunnelHost().getHostName(), 
				tunnelConfig.getTunnelHost().getPort());
		
		session.setConfig("StrictHostKeyChecking", "no");

		session.connect();
		
		return session;
	}
	
	public void disconnect() {
		try {
			session.disconnect();
		} catch(Throwable t) {
			// no exceptions can be thrown by this method!
			return;
		} finally {
			session = null;
		}
	}
}
