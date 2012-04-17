package com.pellcorp.android.transact.sshtunnel;

import static org.junit.Assert.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Ignore;
import org.junit.Test;

public class TimeoutTest {
	@Ignore
	@Test
	public void testTimeout() throws Exception {
		long start = System.currentTimeMillis();
		HttpGet get = new HttpGet("http://google.com");
		HttpClient client = new DefaultHttpClient();

		client.getParams().setIntParameter(AllClientPNames.CONNECTION_TIMEOUT, 3000);
		client.getParams().setIntParameter(AllClientPNames.SO_TIMEOUT, 5000);
		
		HttpResponse response = client.execute(get);
		
		long end = System.currentTimeMillis();
		
		System.out.println("Total ms: " + (end - start));
	}

}
