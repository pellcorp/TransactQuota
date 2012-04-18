package com.pellcorp.android.transact.sshtunnel;

import java.io.File;

public class SshCredentials {
	private final String username;
	private final String password;
	private final File key;
	
	public SshCredentials(String username, File key, String password) {
		this.username = username;
		this.key = key;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public File getKey() {
		return key;
	}

	public String getPassword() {
		return password;
	}
}
