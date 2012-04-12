package com.pellcorp.android.transact;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public final class UsageParser {
	private UsageParser() {
	}
	
	public static String getUsageBlock(String html) {
		String tableHeaderHtml = "<h3>Data allowance and usage</h3>";
		String startTableHtml = "<table>";
		String endTableHtml = "</table>";
		
		int indexOf = html.indexOf(tableHeaderHtml);

		String table = html.substring(indexOf);
		indexOf = table.indexOf(startTableHtml);
		
		table = table.substring(indexOf);
		indexOf = table.indexOf(endTableHtml);
		
		table = table.substring(0, indexOf + endTableHtml.length());
		
		return table.trim();
	}
	
	public static Usage parseUsageBlock(String usage) {
		Document doc = Jsoup.parse(usage);
		return new Usage(0.0, 0.0);
	}
}
