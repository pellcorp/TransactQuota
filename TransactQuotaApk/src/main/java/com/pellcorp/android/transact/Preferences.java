package com.pellcorp.android.transact;

import java.io.File;

import android.content.SharedPreferences;

import com.pellcorp.android.transact.sshtunnel.SshCredentials;
import com.pellcorp.android.transact.sshtunnel.SshHost;
import com.pellcorp.android.transact.sshtunnel.TunnelConfig;

public class Preferences {
	public enum Key {
		ACCOUNT_USERNAME("account.username"), 
		ACCOUNT_PASSWORD("account.password"),
		
		ENABLE_TUNNELING("enable.tunnel"), 
		SSH_PROXY_HOST("ssh.proxy.host"),
		SSH_PROXY_PORT("ssh.proxy.port"),
		SSH_USERNAME("ssh.username"),
		SSH_PASSWORD("ssh.password"),
		SSH_PUB_KEY("ssh.pubkey");
		
		private String key;
		
		private Key(String key) {
			this.key = key;
		}
		
		public String getKey() {
			return key;
		}
	}
	
	private SharedPreferences sharedPreferences;
	
	public Preferences(SharedPreferences SharedPreferences) {
		this.sharedPreferences = SharedPreferences;
	}
	
	public boolean isTunnelingEnabled() {
		return getBooleanValue(Key.ENABLE_TUNNELING);
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
		String sshProxyHost = getStringValue(Key.SSH_PROXY_HOST);
		int sshProxyPort = getIntegerValue(Key.SSH_PROXY_PORT, 22);
		
		if (sshProxyHost != null) {
			return new SshHost(sshProxyHost, sshProxyPort);
		} else {
			return null;
		}
	}
	
	private SshCredentials getSshCredentials() {
		File keyFile = getFileValue(Key.SSH_PUB_KEY);
		String username = getStringValue(Key.SSH_USERNAME);
		String password = getStringValue(Key.SSH_PASSWORD);
		
		if (keyFile != null && username != null) {
			return new SshCredentials(username, keyFile, password);
		} else {
			return null;
		}
	}
	
	public String getAccountUsername() {
		return getStringValue(Key.ACCOUNT_USERNAME);
	}
	
	public String getAccountPassword() {
		return getStringValue(Key.ACCOUNT_PASSWORD);
	}
	
	private boolean getBooleanValue(Key key) {
		return sharedPreferences.getBoolean(key.getKey(), false);
	}
	
	private String getStringValue(Key key) {
		return sharedPreferences.getString(key.getKey(), null);
	}
	
	private File getFileValue(Key key) {
		String value = sharedPreferences.getString(key.getKey(), null);
		if (value != null) {
			File file = new File(value);
			return file;
		} else {
			return null;
		}
	}
	
	private int getIntegerValue(Key key, int defaultValue) {
		String value = getStringValue(key);
		
		try {
			// FIXME - why are the preferences not loaded as numeric?!!
			return Integer.valueOf(value);
			//return sharedPreferences.getInt(key.getKey(), defaultValue);
		} catch (Exception e) {
			//Log.e(TAG, "Failed to load integer! " + key.getKey() 
			//		+ " = [" + value + "]", e);
			return defaultValue;
		}
	}
}
