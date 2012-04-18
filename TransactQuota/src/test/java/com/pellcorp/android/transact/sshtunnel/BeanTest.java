package com.pellcorp.android.transact.sshtunnel;

import java.io.File;

import org.junit.Test;
import static org.junit.Assert.*;
import com.pellcorp.android.transact.ResourceUtils;

public class BeanTest {
	@Test
	public void testTunnelConfig() throws Exception {
		File privateKey = ResourceUtils.getResourceAsFile("/android.pk");
		SshHost sshHost = new SshHost("127.0.0.1", 22);
		SshCredentials creds = new SshCredentials("developer", privateKey, null);
		
		TunnelConfig config = new TunnelConfig(sshHost, creds);

		assertEquals(sshHost, config.getTunnelHost());
		assertEquals(creds, config.getSshCredentials());
	}

	@Test
	public void testSshHost() {
		SshHost sshHost = new SshHost("127.0.0.1", 22);
		assertEquals("127.0.0.1", sshHost.getHostName());
		assertEquals(22, sshHost.getPort());
	}
	
	@Test
	public void testSshCredentials() throws Exception {
		File privateKey = ResourceUtils.getResourceAsFile("/android.pk");
		SshCredentials creds = new SshCredentials("developer", privateKey, "password");
		assertEquals("developer", creds.getUsername());
		assertEquals("password", creds.getPassword());
		assertEquals(privateKey, creds.getKey());
	}
}
