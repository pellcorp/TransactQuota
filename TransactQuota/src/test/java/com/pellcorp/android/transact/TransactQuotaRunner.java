package com.pellcorp.android.transact;

public class TransactQuotaRunner {
	public static void main(String[] args) {
		if (args.length == 2) {
			TransactQuota quota = new TransactQuota(args[0], args[1]);
			Usage usage = quota.getUsage();
			
			System.out.println("Peak Usage: " + usage.getPeakUsage());
			System.out.println("Off Peak Usage: " + usage.getOffPeakUsage());
		} else {
			throw new IllegalArgumentException("Usage: TransactQuotaRunner <username> <password>");
		}
	}
}
