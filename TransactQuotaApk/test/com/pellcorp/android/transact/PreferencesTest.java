package com.pellcorp.android.transact;

import static org.mockito.Mockito.*;

import java.io.File;

import junit.framework.TestCase;
import android.content.SharedPreferences;

public class PreferencesTest extends TestCase {
	public void testNoTunnelPreferences() {
		SharedPreferences prefs = mock(SharedPreferences.class);
		when(prefs.getString(Preferences.Key.ACCOUNT_PASSWORD.getKey(), null)).thenReturn("password");
		when(prefs.getString(Preferences.Key.ACCOUNT_USERNAME.getKey(), null)).thenReturn("username");

		Preferences preferences = new Preferences(prefs);
		
		assertEquals("username", preferences.getAccountUsername());
		assertEquals("password", preferences.getAccountPassword());
		assertNull(preferences.getTunnelConfig());
	}
	
	public void testTunnelPreferencesNoPublicKey() {
		SharedPreferences prefs = mock(SharedPreferences.class);
		when(prefs.getString(Preferences.Key.ACCOUNT_PASSWORD.getKey(), null)).thenReturn("password");
		when(prefs.getString(Preferences.Key.ACCOUNT_USERNAME.getKey(), null)).thenReturn("username");
		
		when(prefs.getString(Preferences.Key.SSH_PROXY_HOST.getKey(), null)).thenReturn("localhost");
		when(prefs.getInt(Preferences.Key.SSH_PROXY_PORT.getKey(), -1)).thenReturn(22);
		when(prefs.getString(Preferences.Key.SSH_USERNAME.getKey(), null)).thenReturn("jason");
		
		Preferences preferences = new Preferences(prefs);
		
		assertEquals("username", preferences.getAccountUsername());
		assertEquals("password", preferences.getAccountPassword());
		assertNull(preferences.getTunnelConfig());
	}
	
	public void testTunnelPreferencesNoProxy() throws Exception {
		SharedPreferences prefs = mock(SharedPreferences.class);
		when(prefs.getString(Preferences.Key.ACCOUNT_PASSWORD.getKey(), null)).thenReturn("password");
		when(prefs.getString(Preferences.Key.ACCOUNT_USERNAME.getKey(), null)).thenReturn("username");
		
		when(prefs.getString(Preferences.Key.SSH_PROXY_HOST.getKey(), null)).thenReturn("localhost");
		when(prefs.getInt(Preferences.Key.SSH_PROXY_PORT.getKey(), -1)).thenReturn(22);
		when(prefs.getString(Preferences.Key.SSH_USERNAME.getKey(), null)).thenReturn("jason");
		
		File privateKey = ResourceUtils.getResourceAsFile("/android.pk");
//		when(prefs.getString(Preferences.Key.SSH_PASSWORD.getKey(), null)).thenReturn(null);
		when(prefs.getString(Preferences.Key.SSH_PUB_KEY.getKey(), null)).thenReturn(privateKey.getAbsolutePath());
		
		Preferences preferences = new Preferences(prefs);
		
		assertEquals("username", preferences.getAccountUsername());
		assertEquals("password", preferences.getAccountPassword());
		assertNull(preferences.getTunnelConfig());
	}
	
	public void testTunnelPreferences() throws Exception {
		SharedPreferences prefs = mock(SharedPreferences.class);
		when(prefs.getString(Preferences.Key.ACCOUNT_PASSWORD.getKey(), null)).thenReturn("password");
		when(prefs.getString(Preferences.Key.ACCOUNT_USERNAME.getKey(), null)).thenReturn("username");
		
		when(prefs.getString(Preferences.Key.SSH_PROXY_HOST.getKey(), null)).thenReturn("localhost");
		when(prefs.getInt(Preferences.Key.SSH_PROXY_PORT.getKey(), -1)).thenReturn(22);
		when(prefs.getString(Preferences.Key.SSH_USERNAME.getKey(), null)).thenReturn("jason");
		
		File privateKey = ResourceUtils.getResourceAsFile("/android.pk");
//		when(prefs.getString(Preferences.Key.SSH_PASSWORD.getKey(), null)).thenReturn(null);
		when(prefs.getString(Preferences.Key.SSH_PUB_KEY.getKey(), null)).thenReturn(privateKey.getAbsolutePath());
		
		when(prefs.getString(Preferences.Key.HTTP_PROXY_HOST.getKey(), null)).thenReturn("localhost");
		when(prefs.getInt(Preferences.Key.HTTP_PROXY_PORT.getKey(), -1)).thenReturn(3128);
		
		Preferences preferences = new Preferences(prefs);
		
		assertEquals("username", preferences.getAccountUsername());
		assertEquals("password", preferences.getAccountPassword());
		assertNotNull(preferences.getTunnelConfig());
	}
}
