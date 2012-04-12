package com.pellcorp.android.transact;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public final class UsageParser {
	private UsageParser() {
	}
	
	public static String getUsageBlock(String html) {
		String tableHeaderHtml = "<h3>Data allowance and usage</h3>";
		String startTableHtml = "<table>";
		String endTableHtml = "</table>";
		
		int indexOf = html.indexOf(tableHeaderHtml);
		if (indexOf != -1) {
			String table = html.substring(indexOf);
			indexOf = table.indexOf(startTableHtml);
			
			table = table.substring(indexOf);
			indexOf = table.indexOf(endTableHtml);
			
			table = table.substring(0, indexOf + endTableHtml.length());
		
			return table.trim();
		} else {
			return null;
		}
	}
	
	public static Usage parseUsageBlock(String usageHtml) {
		Document doc = Jsoup.parse(usageHtml);
		Element tableCells = doc.select("tr").last();
		String peakUsage = tableCells.select("td").get(1).text();
		String offPeakUsage = tableCells.select("td").get(2).text();
		
		return new Usage(new Double(parseNumber(peakUsage)), 
				new Double(parseNumber(offPeakUsage)));
	}
	
	private static String parseNumber(String usageText) {
		int indexOf = usageText.indexOf(" GB");
		return usageText.substring(0, indexOf);
	}
}
