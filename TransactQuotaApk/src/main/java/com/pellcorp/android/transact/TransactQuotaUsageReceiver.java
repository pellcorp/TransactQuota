package com.pellcorp.android.transact;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class TransactQuotaUsageReceiver extends BroadcastReceiver {
	public static final String ACTION_USAGE_DOWNLOADED =
		      "com.pellcorp.android.transact.action.USAGE_DOWNLOADED";
	
	private final Receiver receiver;
	
	public TransactQuotaUsageReceiver(final Receiver receiver) {
		super();
		
		this.receiver = receiver;
	}
	
	public interface Receiver {
		void onReceive(DownloadResult<Usage> results);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		DownloadResult<Usage> results = (DownloadResult<Usage>) intent.getSerializableExtra(TransactionQuotaService.USAGE_DATA);
		receiver.onReceive(results);
	}
}
