package com.pellcorp.android.transact;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public final class UsageParser {
	private static final Pattern USAGE_PATTERN = Pattern.compile("([0-9.]+) (['GB'|'MB']+)");
	
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
		
		return new Usage(parseNumber(peakUsage), 
				parseNumber(offPeakUsage));
	}
	
	private static BigDecimal parseNumber(String usageText) {
		Matcher matcher = USAGE_PATTERN.matcher(usageText);
		if (matcher.find()) {
			BigDecimal number = toNumber(matcher.group(1));
			String type = matcher.group(2);
			if ("GB".equals(type)) {
				return number;
			} else { // MB
				return number.divide(new BigDecimal(1000));
			}
		} else {
			return BigDecimal.ZERO;
		}
	}
	
	private static BigDecimal toNumber(String numberValue) {
		try {
			return new BigDecimal(numberValue);
		} catch(NumberFormatException e) {
			return null;
		}
	}
}
