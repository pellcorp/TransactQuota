package com.pellcorp.android.transact;

import com.pellcorp.android.transact.prefs.PreferenceProvider;

public class Preferences {
	private final PreferenceProvider preferenceProvider;
	
	public Preferences(final PreferenceProvider preferenceProvider) {
		this.preferenceProvider = preferenceProvider;
	}
	
	public String getAccountUsername() {
		return preferenceProvider.getString(R.string.pref_account_username);
	}
	
	public String getAccountPassword() {
		return preferenceProvider.getString(R.string.pref_account_password);
	}
}
