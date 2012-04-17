package com.pellcorp.android.transact;

import java.io.File;

import org.apache.http.HttpHost;

import android.content.SharedPreferences;

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
		SSH_PUB_KEY("ssh.pubkey"),
		HTTP_PROXY_HOST("proxy.host"),
		HTTP_PROXY_PORT("proxy.port");
		
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
	
	
	public TunnelConfig getTunnelConfig() {
		boolean isTunnelingEnabled = getBooleanValue(Key.ENABLE_TUNNELING);
		if (isTunnelingEnabled) {
			SshHost sshHost = getSshHost();
			HttpHost proxyHost = getProxyHost();
			String sshUsername = getSshUsername();
			File sshPubKey = getSshKey();
			//String sshPassword = getSshPassword();
			
			if (sshHost != null && proxyHost != null && sshUsername != null && sshPubKey != null) {
				return new TunnelConfig(
					sshHost, //tunnel
					proxyHost, //proxy 
					sshUsername,
					sshPubKey);
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
	
	private String getSshUsername() {
		return getStringValue(Key.SSH_USERNAME);
	}
	
	private String getSshPassword() {
		return getStringValue(Key.SSH_PASSWORD);
	}
	
	private File getSshKey() {
		return getFileValue(Key.SSH_PUB_KEY);
	}
	
	private HttpHost getProxyHost() {
		String proxyHost = getStringValue(Key.HTTP_PROXY_HOST);
		int proxyPort = getIntegerValue(Key.HTTP_PROXY_PORT, 3128);
		if (proxyHost != null) {
			return new HttpHost(proxyHost, proxyPort);
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
			return new File(value);
		} else {
			return null;
		}
	}
	
	private int getIntegerValue(Key key, int defaultValue) {
		return sharedPreferences.getInt(key.getKey(), defaultValue);
	}
}
