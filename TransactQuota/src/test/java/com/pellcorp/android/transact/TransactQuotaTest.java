package com.pellcorp.android.transact;

import org.junit.Test;
import static org.junit.Assert.*;

public class TransactQuotaTest {
	@Test
	public void testQuota() {
		TransactQuota quota = new TransactQuota("jpell", "finlay");
		Usage usage = quota.getUsage();
		assertNotNull(usage);
	}
}
