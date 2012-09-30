package com.pellcorp.android.transact;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSchException;

public class TransactQuota {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final String USER_AGENT = "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:11.0) Gecko/20100101 Firefox/11.0"; 
	
	private static final String URL = "https://portal.vic.transact.com.au/portal/default/user/login?_next=/portal/default/index";
	
	private HttpClient client;
	
	private final String username;
	private final String password;
	
	public TransactQuota(final String username, final String password) {
		this.username = username;
		this.password = password;
		
		//logger.info("Username: {}, Password: {}", username, password);
		
		try {
			client = createClient();
			
		} catch (Throwable t) {
			throw new UsageNotAvailableException(t);
		}
	}
	
	public void disconnect() {
    	try {
    		client.getConnectionManager().shutdown();
    	} catch(Throwable t) {
    		// who cares ignore
    	}
	}
	
	public Usage getUsage() throws IOException {
		try {
			CookieStore cookieStore = new BasicCookieStore();
			HttpContext localContext = new BasicHttpContext();
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			
			String formKey = doGetLogin(localContext);
        	return doSubmit(formKey, localContext);
		} catch(IOException ioe) {
			logger.info("Connectivity Exception", ioe);
			throw ioe;
		} catch(InvalidCredentialsException e) {
			logger.info("Invalid Credentials", e);
			throw e;
        } catch(Exception e) {
        	logger.info("Usage not available", e);
        	throw new UsageNotAvailableException(e);
        }
	}
	
	private HttpClient createClient() throws JSchException, KeyManagementException, NoSuchAlgorithmException {
		int timeoutConnection = 5000;
		int timeoutSocket = 10000;
		
		FakeSocketFactory sf = new FakeSocketFactory();
		
		SchemeRegistry sr = new SchemeRegistry();
		sr.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		sr.register(new Scheme("https", sf, 443));
		
		HttpParams params = new BasicHttpParams();
		params.setIntParameter(AllClientPNames.CONNECTION_TIMEOUT, timeoutConnection);
		params.setIntParameter(AllClientPNames.SO_TIMEOUT, timeoutSocket);
		params.setParameter(AllClientPNames.USER_AGENT, USER_AGENT);
		params.setIntParameter(AllClientPNames.SOCKET_BUFFER_SIZE, 8192);
		
		ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager(params, sr);
		
		return new DefaultHttpClient(connManager, params);
	}

	private String doGetLogin(HttpContext localContext) 
			throws IOException, URISyntaxException, HttpException {
		HttpGet get = new HttpGet(URL);
		HttpResponse response = client.execute(get, localContext);
		String html = EntityUtils.toString(response.getEntity());
		Document doc = Jsoup.parse(html, URL);
    	Elements elements = doc.getElementsByAttributeValue("name", "_formkey");
    	return elements.first().attr("value");
	}
	
	private Usage doSubmit(String formKey, HttpContext localContext) throws Exception {
		HttpPost post = new HttpPost(URL);

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("uname", username));
		parameters.add(new BasicNameValuePair("passwd", password));
		parameters.add(new BasicNameValuePair("_next", "/portal/default/index"));
		parameters.add(new BasicNameValuePair("_formkey", formKey));
		parameters.add(new BasicNameValuePair("_formname", "login"));
		
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters);
		post.setEntity(entity);
		
		HttpResponse response = client.execute(post, localContext);
		String html = EntityUtils.toString(response.getEntity());

		String usageHtml = UsageParser.getUsageBlock(html);
		if (usageHtml != null) {
			return UsageParser.parseUsageBlock(usageHtml);
		} else {
			throw new InvalidCredentialsException();
		}
	}
}
