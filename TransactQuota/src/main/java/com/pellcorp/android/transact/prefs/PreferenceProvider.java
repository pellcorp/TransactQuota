package com.pellcorp.android.transact.prefs;

import java.io.File;

public interface PreferenceProvider {
	String getString(int resId);
	boolean getBoolean(int resId);
	int getInteger(int resId, int defaultValue);
	File getFile(int resId);
}
