package com.pellcorp.android.transact;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.pellcorp.android.transact.prefs.PreferenceProviderImpl;

public class TransactionQuotaService extends IntentService {
	public static final String USAGE_DATA = "com.pellcorp.android.transact.USAGE_DATA";
	
	private final Logger logger = LoggerFactory.getLogger(getClass().getName());
	
	private Preferences preferences;

	public TransactionQuotaService() {
		super("TransactionQuotaService");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		logger.info("onCreate...");
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferences = new Preferences(new PreferenceProviderImpl(this, sharedPreferences));
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		logger.info("Starting task");
		
		TransactQuota transactQuota = new TransactQuota(
				preferences.getAccountUsername(),
				preferences.getAccountPassword());

		DownloadResult<Usage> usage = null;
		try {
			if (preferences.getAccountUsername() != null
					&& preferences.getAccountPassword() != null) {
				usage = new DownloadResult<Usage>(transactQuota.getUsage());
			} else {
				usage = new DownloadResult<Usage>(true);
			}
		} catch(InvalidCredentialsException e) {
			usage = new DownloadResult<Usage>(true);
		} catch (UsageNotAvailableException e) {
			logger.error("UsageNotAvailableException", e);
			usage = new DownloadResult<Usage>(e.getMessage());
		} catch (IOException e) {
			logger.error("Connectivity Exception", e);
			usage = new DownloadResult<Usage>(e.getMessage());
		} catch (Exception e) {
			logger.error("Unknown Exception", e);
			usage = new DownloadResult<Usage>(e.getMessage());
		} finally {
			transactQuota.disconnect();
		}
			
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(TransactQuotaUsageReceiver.ACTION_USAGE_DOWNLOADED);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(USAGE_DATA, usage);
		sendBroadcast(broadcastIntent);
	}
}
