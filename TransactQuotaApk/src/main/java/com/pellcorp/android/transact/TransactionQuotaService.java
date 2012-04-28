package com.pellcorp.android.transact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class TransactionQuotaService extends Service {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	// the default 
	private Usage currentUsage = new Usage(0.0, 0.0);

	private final IBinder mBinder = new Binder() {
		TransactionQuotaService getService() {
			return TransactionQuotaService.this;
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	public Usage getUsage() {
		return currentUsage;
	}
}
