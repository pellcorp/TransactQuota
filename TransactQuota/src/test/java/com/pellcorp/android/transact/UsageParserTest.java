package com.pellcorp.android.transact;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;

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
	public void testFailedLoginParseUsageBlock() throws Exception {
		String html = loadResource("/login.html");
		assertNull(UsageParser.getUsageBlock(html));
	}
	
	@Test
	public void testParseUsageBlock() throws Exception {
		String usageHtml = loadResource("/usage.html");
		Usage usage = UsageParser.parseUsageBlock(usageHtml);
		
		assertEquals(new BigDecimal("35.069"), usage.getPeakUsage());
		assertEquals(new BigDecimal("10.851"), usage.getOffPeakUsage());
	}
	
	@Test
	public void testParseUsageBlockInMb() throws Exception {
		String usageHtml = loadResource("/usage-mb.html");
		Usage usage = UsageParser.parseUsageBlock(usageHtml);
		
		assertEquals(new BigDecimal("0.053661"), usage.getPeakUsage());
		assertEquals(new BigDecimal("0.093106"), usage.getOffPeakUsage());
	}
	
	private String loadResource(String path) throws Exception {
		return IOUtils.toString(UsageParserTest.class.getResourceAsStream(path));
	}
}
