package com.pellcorp.android.transact;

import java.io.File;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SshAuthenticationTest {
	@Ignore
	@Test
	public void test() throws Exception {
		File privateKey = ResourceUtils.getResourceAsFile("/android.pk");
		JSch jsch = new JSch();
		jsch.addIdentity(privateKey.getAbsolutePath(), "");

		Session session = jsch.getSession("developer", "127.0.0.1", 22);
		session.setConfig("StrictHostKeyChecking", "no");

		session.connect();

		int assinged_port = session.setPortForwardingL(3128, "192.168.0.5", 3128);
		
		System.out.println("localhost:" + assinged_port);

		HttpClient client = new DefaultHttpClient();
		HttpHost proxy = new HttpHost("localhost", 3128);
		client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		
		HttpGet get = new HttpGet("http://google.com");
		HttpResponse response = client.execute(get);
		String html = EntityUtils.toString(response.getEntity());
		System.out.println(html);
	}
}
