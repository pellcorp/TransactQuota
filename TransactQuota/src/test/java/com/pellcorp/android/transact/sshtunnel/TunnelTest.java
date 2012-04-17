package com.pellcorp.android.transact.sshtunnel;

import java.io.File;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.pellcorp.android.transact.ResourceUtils;

/**
 * Note this is not going to work on your host, unless you append the android.pk.pub
 * to your .ssh/authorized_keys2 file
 */
public class TunnelTest {
	private TunnelConfig tunnelConfig;
	
	@Before
	public void setUp() throws Exception {
		File privateKey = ResourceUtils.getResourceAsFile("/android.pk");
		
		tunnelConfig = new TunnelConfig(
				new SshHost("192.168.79.156", 22), //tunnel
				new HttpHost("192.168.0.5", 3128), //proxy 
				"developer",
				privateKey);
	}
	
	@Test
	@Ignore
	public void testShell() throws Exception {
		Shell shell = new Shell(tunnelConfig);
		System.out.println(shell.getShellLoginMessage());
	}
	
	@Test
	@Ignore
	public void testTunnel() throws Exception {
		Tunnel tunnel = new Tunnel(tunnelConfig);
		HttpHost proxyHost = tunnel.connect();
		
		try {
			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHost);
			
			HttpGet get = new HttpGet("http://google.com");
			HttpResponse response = client.execute(get);
			String html = EntityUtils.toString(response.getEntity());
			System.out.println(html);
		} finally {
			tunnel.disconnect();
		}
	}
}
