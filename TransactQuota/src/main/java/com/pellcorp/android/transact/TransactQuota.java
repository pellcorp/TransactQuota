package com.pellcorp.android.transact;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.jcraft.jsch.JSchException;
import com.pellcorp.android.transact.sshtunnel.Tunnel;
import com.pellcorp.android.transact.sshtunnel.TunnelConfig;

public class TransactQuota {
	private static final String USER_AGENT = "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:11.0) Gecko/20100101 Firefox/11.0"; 
	
	private static final String URL = "https://${HOST_PORT}/portal/default/user/login?_next=/portal/default/index";
	private static final String TRANSACT_PORTAL_HOST = "portal.vic.transact.com.au";
	
	private final TunnelConfig tunnelConfig;
	private HttpClient client;
	private final String username;
	private final String password;
	
	public TransactQuota(final String username, final String password) {
		this(null, username, password);
	}

	public TransactQuota(final TunnelConfig tunnelConfig, 
			final String username, final String password) {
		this.username = username;
		this.password = password;
		this.tunnelConfig = tunnelConfig;
	}
	
	public Usage getUsage() throws IOException {
		Tunnel tunnel = null;
		
		try {
			String url = URL.replace("${HOST_PORT}", TRANSACT_PORTAL_HOST);
			client = createClient();
			if (tunnelConfig != null) {
				tunnel = new Tunnel(tunnelConfig);
				HttpHost proxy = tunnel.connect(new HttpHost(TRANSACT_PORTAL_HOST, 443));
				url  = URL.replace("${HOST_PORT}", proxy.getHostName() + ":" + proxy.getPort());
			}
			
			CookieStore cookieStore = new BasicCookieStore();
			HttpContext localContext = new BasicHttpContext();
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			
			String formKey = doGetLogin(url, localContext);
        	return doSubmit(url, formKey, localContext);
		} catch(IOException ioe) {
			throw ioe;
		} catch(InvalidCredentialsException e) {
			throw e;
        } catch(Exception e) {
        	throw new UsageNotAvailableException(e);
        } finally {
        	if (tunnel != null) {
        		tunnel.disconnect();
        	}
        	try {
        		client.getConnectionManager().shutdown();
        	} catch(Throwable t) {
        		// who cares ignore
        	}
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
		
		SingleClientConnManager connManager = new SingleClientConnManager(params, sr);
		
		return new DefaultHttpClient(connManager, params);
	}

	private String doGetLogin(String url, HttpContext localContext) 
			throws IOException, URISyntaxException, HttpException {
		HttpGet get = new HttpGet(url);
		HttpResponse response = client.execute(get, localContext);
		String html = EntityUtils.toString(response.getEntity());
		Document doc = Jsoup.parse(html, URL);
    	Elements elements = doc.getElementsByAttributeValue("name", "_formkey");
    	return elements.first().attr("value");
	}
	
	private Usage doSubmit(String url, String formKey, HttpContext localContext) throws Exception {
		MultipartEntity entity = new MultipartEntity();
		entity.addPart("uname", new StringBody(username));
		entity.addPart("passwd", new StringBody(password));
		entity.addPart("_next", new StringBody("/portal/default/index"));
		entity.addPart("_formkey", new StringBody(formKey));
		entity.addPart("_formname", new StringBody("login"));

		HttpPost post = new HttpPost(url);
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
