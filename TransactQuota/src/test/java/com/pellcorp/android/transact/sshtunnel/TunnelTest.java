package com.pellcorp.android.transact.sshtunnel;

import java.io.File;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.pellcorp.android.transact.ResourceUtils;
import com.pellcorp.android.transact.sshtunnel.SshHost;
import com.pellcorp.android.transact.sshtunnel.Tunnel;
import com.pellcorp.android.transact.sshtunnel.TunnelConfig;

public class TunnelTest {
	@Test
	public void test() throws Exception {
		File privateKey = ResourceUtils.getResourceAsFile("/android.pk");
		
		TunnelConfig tunnelConfig = new TunnelConfig(
				new SshHost("127.0.0.1", 22), //tunnel
				new HttpHost("192.168.0.5", 3128), //proxy 
				"developer",
				privateKey);

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
