package com.pellcorp.android.transact;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import com.pellcorp.android.transact.android.PreferenceProvider;

public class PreferencesTest extends TestCase {
	public void testNoTunnelPreferences() {
		PreferenceProvider prefs = mock(PreferenceProvider.class);
		when(prefs.getString(R.string.pref_account_password)).thenReturn("password");
		when(prefs.getString(R.string.pref_account_username)).thenReturn("username");

		Preferences preferences = new Preferences(prefs);
		
		assertEquals("username", preferences.getAccountUsername());
		assertEquals("password", preferences.getAccountPassword());
		assertNull(preferences.getTunnelConfig());
	}
	
	public void testTunnelPreferencesNoPublicKey() {
		PreferenceProvider prefs = mock(PreferenceProvider.class);
		when(prefs.getBoolean(R.string.pref_enable_tunnel)).thenReturn(true);
		when(prefs.getString(R.string.pref_account_password)).thenReturn("password");
		when(prefs.getString(R.string.pref_account_username)).thenReturn("username");
		
		when(prefs.getString(R.string.pref_ssh_proxy_host)).thenReturn("localhost");
		when(prefs.getInteger(R.string.pref_ssh_proxy_port, 22)).thenReturn(22);
		when(prefs.getString(R.string.pref_ssh_username)).thenReturn("jason");
		
		Preferences preferences = new Preferences(prefs);
		
		assertEquals("username", preferences.getAccountUsername());
		assertEquals("password", preferences.getAccountPassword());
		assertNull(preferences.getTunnelConfig());
	}
	
	public void testTunnelPreferences() throws Exception {
		PreferenceProvider prefs = mock(PreferenceProvider.class);
		
		Preferences preferences = new Preferences(prefs);
		
		assertEquals("username", preferences.getAccountUsername());
		assertEquals("password", preferences.getAccountPassword());
		assertNotNull(preferences.getTunnelConfig());
	}
	
	public void testTunnelPreferencesTunnelingNotEnabled() throws Exception {
		PreferenceProvider prefs = mock(PreferenceProvider.class);
		
		Preferences preferences = new Preferences(prefs);
		
		assertEquals("username", preferences.getAccountUsername());
		assertEquals("password", preferences.getAccountPassword());
		assertNull(preferences.getTunnelConfig());
	}

	private PreferenceProvider createAllPreferences(boolean isTunnelingEnabled) throws IOException {
		PreferenceProvider prefs = mock(PreferenceProvider.class);
		when(prefs.getBoolean(R.string.pref_enable_tunnel)).thenReturn(true);
		when(prefs.getString(R.string.pref_account_password)).thenReturn("password");
		when(prefs.getString(R.string.pref_account_username)).thenReturn("username");
		
		when(prefs.getString(R.string.pref_ssh_proxy_host)).thenReturn("localhost");
		when(prefs.getInteger(R.string.pref_ssh_proxy_port, 22)).thenReturn(22);
		when(prefs.getString(R.string.pref_ssh_username)).thenReturn("jason");
		
		File privateKey = ResourceUtils.getResourceAsFile("/android.pk");
		//when(prefs.getString(R.string.pref_ssh_password)).thenReturn(null);
		when(prefs.getFile(R.string.pref_ssh_pubkey)).thenReturn(privateKey);
		
		return prefs;
	}
}
