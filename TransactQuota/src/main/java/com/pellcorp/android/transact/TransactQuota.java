package com.pellcorp.android.transact;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class TransactQuota {
	private static final String URL = "https://portal.vic.transact.com.au/portal/default/user/login?_next=/portal/default/index";
	
	private HttpClient client;
	private String username;
	private String password;
	
	public TransactQuota(String username, String password) {
		this.username = username;
		this.password = password;
		
		client = new DefaultHttpClient();
	}
	
	public Usage getUsage() {
		try {
			CookieStore cookieStore = new BasicCookieStore();
			HttpContext localContext = new BasicHttpContext();
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			
			String formKey = doGetLogin(localContext);
        	return doSubmit(formKey, localContext);
        } catch(Exception e) {
        	throw new UsageNotAvailableException(e);
        }
	}

	private String doGetLogin(HttpContext localContext) throws IOException,
			ClientProtocolException {
		HttpGet get = new HttpGet(URL);
		HttpResponse response = client.execute(get, localContext);
		String html = EntityUtils.toString(response.getEntity());
		Document doc = Jsoup.parse(html, URL);
    	Elements elements = doc.getElementsByAttributeValue("name", "_formkey");
    	return elements.first().attr("value");
	}
	
	private Usage doSubmit(String formKey, HttpContext localContext) throws Exception {
		MultipartEntity entity = new MultipartEntity();
		entity.addPart("uname", new StringBody(username));
		entity.addPart("passwd", new StringBody(password));
		entity.addPart("_next", new StringBody("/portal/default/index"));
		entity.addPart("_formkey", new StringBody(formKey));
		entity.addPart("_formname", new StringBody("login"));

		HttpPost post = new HttpPost(URL);
		post.setEntity(entity);
		
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(post, localContext);
		String html = EntityUtils.toString(response.getEntity());

		String usageHtml = UsageParser.getUsageBlock(html);
		return UsageParser.parseUsageBlock(usageHtml);
	}
}
