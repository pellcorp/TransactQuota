package com.pellcorp.android.transact.prefs;

public interface PreferenceProvider {
	String getString(int resId);
	boolean getBoolean(int resId);
	int getInteger(int resId, int defaultValue);
}
