package com.pellcorp.android.transact;

import java.io.File;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.pellcorp.android.transact.sshtunnel.Tunnel;
import com.pellcorp.android.transact.sshtunnel.TunnelConfig;

public class SshAuthenticationTest {
	@Test
	public void test() throws Exception {
		File privateKey = ResourceUtils.getResourceAsFile("/android.pk");
		
		TunnelConfig tunnelConfig = new TunnelConfig(
				new HttpHost("127.0.0.1", 22), //tunnel
				new HttpHost("192.168.0.5", 3128), //proxy 
				"developer",
				privateKey);

		Tunnel tunnel = new Tunnel(tunnelConfig);
		HttpHost proxyHost = tunnel.connect();
		
		try {
			/**
			 * JSch jsch = new JSch();
			jsch.addIdentity(privateKey.getAbsolutePath(), "");
	
			Session session = jsch.getSession("developer", "127.0.0.1", 22);
			session.setConfig("StrictHostKeyChecking", "no");
	
			session.connect();
	
			int localPort = session.setPortForwardingL(0, "192.168.0.5", 3128);
			 */
			
			System.out.println("localhost:" + proxyHost.getPort());
	
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
