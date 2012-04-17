package com.pellcorp.android.transact;

import java.io.File;

import com.pellcorp.android.transact.sshtunnel.SshHost;
import com.pellcorp.android.transact.sshtunnel.TunnelConfig;

public class TransactQuotaRunner {
	public static void main(String[] args) throws Exception {
		
		if (args.length == 2) {
			File privateKey = ResourceUtils.getResourceAsFile("/android.pk");
			
			TunnelConfig tunnelConfig = new TunnelConfig(
					new SshHost("127.0.0.1", 22), //tunnel
					"developer",
					privateKey);
			
			TransactQuota quota = new TransactQuota(tunnelConfig, args[0], args[1]);
			Usage usage = quota.getUsage();
			
			System.out.println("Peak Usage: " + usage.getPeakUsage());
			System.out.println("Off Peak Usage: " + usage.getOffPeakUsage());
		} else {
			throw new IllegalArgumentException("Usage: TransactQuotaRunner <username> <password>");
		}
	}
}
