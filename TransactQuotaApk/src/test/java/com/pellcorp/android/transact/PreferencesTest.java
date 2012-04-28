package com.pellcorp.android.transact;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import com.pellcorp.android.transact.prefs.PreferenceProvider;

public class PreferencesTest extends TestCase {
	public void testPreferences() {
		PreferenceProvider prefs = mock(PreferenceProvider.class);
		when(prefs.getString(R.string.pref_account_password)).thenReturn("password");
		when(prefs.getString(R.string.pref_account_username)).thenReturn("username");

		Preferences preferences = new Preferences(prefs);
		
		assertEquals("username", preferences.getAccountUsername());
		assertEquals("password", preferences.getAccountPassword());
	}
}
