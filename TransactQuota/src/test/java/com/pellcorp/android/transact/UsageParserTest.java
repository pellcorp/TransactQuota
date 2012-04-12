package com.pellcorp.android.transact;

import static org.junit.Assert.*;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class UsageParserTest {

	@Test
	public void testGetUsageBlock() throws Exception {
		String html = loadResource("/test.html");
		String usageHtml = loadResource("/usage.html");
		String usage = UsageParser.getUsageBlock(html);
		
		assertEquals(usageHtml.trim(), usage);
		
	}
	
	@Test
	public void testParseUsageBlock() throws Exception {
		String usageHtml = loadResource("/usage.html");
		
		
		//assertEquals(new Double("35.069"), usage.getPeakUsage());
		//assertEquals(new Double("10.851"), usage.getOffPeakUsage());
	}
	
	private String loadResource(String path) throws Exception {
		return IOUtils.toString(UsageParserTest.class.getResourceAsStream(path));
	}
}
