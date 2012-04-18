package com.pellcorp.android.transact.sshtunnel;

import java.io.File;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.pellcorp.android.transact.FakeSocketFactory;
import com.pellcorp.android.transact.ResourceUtils;

/**
 * Note this is not going to work on your host, unless you append the
 * android.pk.pub to your .ssh/authorized_keys2 file
 */
public class TunnelTest {
	private TunnelConfig tunnelConfig;
	private File privateKey;

	@Before
	public void setUp() throws Exception {
		privateKey = ResourceUtils.getResourceAsFile("/android.pk");

		tunnelConfig = new TunnelConfig(new SshHost("localhost", 22),
				new SshCredentials("developer", privateKey, null));
	}

	@Test
	@Ignore
	public void testTunnelToTransact() throws Exception {
		tunnelConfig = new TunnelConfig(new SshHost("localhost", 22),
				new SshCredentials("developer", privateKey, null));
		
		FakeSocketFactory sf = new FakeSocketFactory();
		
		Scheme http = new Scheme("http", PlainSocketFactory.getSocketFactory(), 80);
		Scheme https = new Scheme("https", sf, 443);

		SchemeRegistry sr = new SchemeRegistry();
		sr.register(http);
		sr.register(https);
		
		HttpParams params = new BasicHttpParams();
		SingleClientConnManager connManager = new SingleClientConnManager(params, sr);
		
		Tunnel tunnel = new Tunnel(tunnelConfig);
		HttpHost proxyHost = tunnel.connect(new HttpHost("portal.vic.transact.com.au", 443));

		HttpClient client = new DefaultHttpClient(connManager, params);

		HttpGet get = new HttpGet("https://localhost:" + proxyHost.getPort()
				+ "/portal/default/user/login?_next=/portal/default/index");
		HttpResponse response = client.execute(get);
		String html = EntityUtils.toString(response.getEntity());
		System.out.println(html);
	}
	
	@Test
	public void testShell() throws Exception {
		Shell shell = new Shell(tunnelConfig);
		System.out.println(shell.getShellLoginMessage());
	}

	@Test
	@Ignore
	public void testTunnel() throws Exception {
		Tunnel tunnel = new Tunnel(tunnelConfig);
		HttpHost proxyHost = tunnel.connect(new HttpHost("google.com", 80));

		try {
			HttpClient client = new DefaultHttpClient();

			HttpGet get = new HttpGet("http://localhost:" + proxyHost.getPort());
			HttpResponse response = client.execute(get);
			String html = EntityUtils.toString(response.getEntity());
			System.out.println(html);
		} finally {
			tunnel.disconnect();
		}
	}
}
