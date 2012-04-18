package com.pellcorp.android.transact;

import java.io.File;

import com.pellcorp.android.transact.android.PreferenceProvider;
import com.pellcorp.android.transact.sshtunnel.SshCredentials;
import com.pellcorp.android.transact.sshtunnel.SshHost;
import com.pellcorp.android.transact.sshtunnel.TunnelConfig;

public class Preferences {
	private final PreferenceProvider preferenceProvider;
	
	public Preferences(final PreferenceProvider preferenceProvider) {
		this.preferenceProvider = preferenceProvider;
	}
	
	public boolean isTunnelingEnabled() {
		return preferenceProvider.getBoolean(R.string.pref_enable_tunnel);
	}
	
	public TunnelConfig getTunnelConfig() {
		if (isTunnelingEnabled()) {
			SshHost sshHost = getSshHost();
			SshCredentials sshCredentials = getSshCredentials();
			
			if (sshHost != null && sshCredentials != null) {
				return new TunnelConfig(sshHost, sshCredentials);
			}
		}
		return null;
	}
	
	private SshHost getSshHost() {
		String sshProxyHost = preferenceProvider.getString(R.string.pref_ssh_proxy_host);
		int sshProxyPort = preferenceProvider.getInteger(R.string.pref_ssh_proxy_port, 22);
		
		if (sshProxyHost != null) {
			return new SshHost(sshProxyHost, sshProxyPort);
		} else {
			return null;
		}
	}
	
	private SshCredentials getSshCredentials() {
		File keyFile = preferenceProvider.getFile(R.string.pref_ssh_pubkey);
		String username = preferenceProvider.getString(R.string.pref_ssh_username);
		String password = preferenceProvider.getString(R.string.pref_ssh_password);
		
		if (keyFile != null && username != null) {
			return new SshCredentials(username, keyFile, password);
		} else {
			return null;
		}
	}
	
	public String getAccountUsername() {
		return preferenceProvider.getString(R.string.pref_account_username);
	}
	
	public String getAccountPassword() {
		return preferenceProvider.getString(R.string.pref_account_password);
	}
}
