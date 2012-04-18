package com.pellcorp.android.transact.sshtunnel;

public class TunnelConfig {
	private final SshCredentials sshCredentials;
	private final SshHost tunnelHost;
	
	public TunnelConfig(
			final SshHost tunnelHost, 
			final SshCredentials sshCredentials) {
		
		this.tunnelHost = tunnelHost;
		this.sshCredentials = sshCredentials;
	}

	public SshCredentials getSshCredentials() {
		return sshCredentials;
	}
	
	public SshHost getTunnelHost() {
		return tunnelHost;
	}
}
