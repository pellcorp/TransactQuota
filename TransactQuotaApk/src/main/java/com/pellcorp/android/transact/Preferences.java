package com.pellcorp.android.transact;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.pellcorp.android.transact.prefs.PreferenceProvider;
import com.pellcorp.android.transact.prefs.PreferenceProviderImpl;

public class Preferences {
	private final PreferenceProvider preferenceProvider;
	
	public Preferences(final PreferenceProvider preferenceProvider) {
		this.preferenceProvider = preferenceProvider;
	}
	
	public static Preferences getPreferences(Context ctx) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		return new Preferences(new PreferenceProviderImpl(ctx, sharedPreferences));
	}
	
	public boolean isConfigured() {
		return getAccountUsername() != null && getAccountPassword() != null;
	}
		
	public String getAccountUsername() {
		String accountName = preferenceProvider.getString(R.string.pref_account_username);
		return accountName;
	}
	
	public String getAccountPassword() {
		String accountPassword = preferenceProvider.getString(R.string.pref_account_password);
		return accountPassword;
	}
}
