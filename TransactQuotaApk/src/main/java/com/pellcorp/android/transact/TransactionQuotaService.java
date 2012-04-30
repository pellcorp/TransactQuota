package com.pellcorp.android.transact;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.pellcorp.android.transact.asynctask.DownloadResult;
import com.pellcorp.android.transact.asynctask.DownloadTask;
import com.pellcorp.android.transact.prefs.PreferenceProviderImpl;

public class TransactionQuotaService extends Service {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	// the default so we always return something
	private DownloadResult<Usage> currentUsage = 
			new DownloadResult<Usage>(new Usage(BigDecimal.ZERO, BigDecimal.ZERO));
	
	private Preferences preferences;

	private final IBinder mBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		TransactionQuotaService getService() {
			return TransactionQuotaService.this;
		}
	}

	@Override
	public void onCreate() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		preferences = new Preferences(new PreferenceProviderImpl(this,
				sharedPreferences));
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	public DownloadResult<Usage> getUsage() {
		return currentUsage;
	}

	private void doUsageDownload(final Preferences preferences)
			throws InterruptedException, ExecutionException, TimeoutException {

		DownloadTask<Usage> downloadTask = new DownloadTask<Usage>() {
			@Override
			protected Usage doTask() throws Exception {
				if (preferences.getAccountUsername() != null
						&& preferences.getAccountPassword() != null) {
					TransactQuota transactQuota = new TransactQuota(
							preferences.getAccountUsername(),
							preferences.getAccountPassword());

					try {
						return transactQuota.getUsage();
					} finally {
						transactQuota.disconnect();
					}
				} else {
					throw new InvalidCredentialsException();
				}
			}

			@Override
			protected void onFinish(DownloadResult<Usage> usage) {
				TransactionQuotaService.this.currentUsage = usage;
			}
		};

		downloadTask.execute();
	}
}
