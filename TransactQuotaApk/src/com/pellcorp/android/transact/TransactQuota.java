package com.pellcorp.android.transact;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class TransactQuota {
	private static final String URL = "https://portal.vic.transact.com.au/portal/default/user/login?_next=/portal/default/index";
	
	private String username;
	private String password;
	
	public TransactQuota(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public Usage getUsage() {
		try {
        	Document doc = Jsoup.connect(URL).get();
        	Elements elements = doc.getElementsByAttributeValue("name", "_formkey");
        	String formKey = elements.first().attr("value");

        	doc = doSubmit(formKey);

        	return new Usage(0.0, 0.0);
        } catch(Exception e) {
        	throw new UsageNotAvailable();
        }
	}
	
	private Document doSubmit(String formKey) throws Exception {
		MultipartEntity entity = new MultipartEntity();
		entity.addPart("uname", new StringBody(username));
		entity.addPart("passwd", new StringBody(password));
		entity.addPart("_next", new StringBody("/portal/default/index"));
		entity.addPart("_formkey", new StringBody(formKey));
		entity.addPart("_formname", new StringBody("login"));

		HttpPost post = new HttpPost(URL);
		post.setEntity(entity);

		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(post);
		String html = EntityUtils.toString(response.getEntity());

		return Jsoup.parse(html, URL);
	}
}
