package com.pellcorp.android.transact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TransactQuotaStartReceiver extends BroadcastReceiver {
	private final Logger logger = LoggerFactory.getLogger(getClass().getName());
	
	@Override
	public void onReceive(Context context, Intent intent) {
		logger.info("onReceive");
		Intent service = new Intent(context, TransactionQuotaService.class);
		context.startService(service);
	}
}
