package com.pellcorp.android.transact;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.pellcorp.android.transact.asynctask.DownloadResult;
import com.pellcorp.android.transact.asynctask.DownloadTask;
import com.pellcorp.android.transact.prefs.PreferenceProviderImpl;

public class TransactQuotaActivity extends Activity implements OnClickListener {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		logger.info("Starting onCreate");
		
		setContentView(R.layout.main);

		Button refreshButton = (Button) findViewById(R.id.refresh_button);
		refreshButton.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		logger.info("Starting onResume");
		refreshUsage();
	}
	
	private void refreshUsage() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		Preferences preferences = new Preferences(new PreferenceProviderImpl(this,
				sharedPreferences));

		if (preferences.getAccountUsername() != null
				&& preferences.getAccountPassword() != null) {
			try {
				doUsageDownload(preferences);
			} catch (Exception e) {
				logger.error("refreshUsage", e);
				AlertDialog dialog = createErrorDialog(e.getMessage());
				dialog.show();
			}
		} else {
			logger.info("Username and password not provided");
			Dialog dialog = createSettingsMissingDialog(getString(R.string.settings_missing_label));
			dialog.show();
		}
	}
	
	private void doUsageDownload(final Preferences preferences) throws InterruptedException,
			ExecutionException, TimeoutException {

		DownloadTask<Usage> downloadTask = new DownloadTask<Usage>(this, 
				getString(R.string.loading)) {
			@Override
			protected Usage doTask() throws Exception {
				TransactQuota transactQuota = new TransactQuota(
					preferences.getAccountUsername(),
					preferences.getAccountPassword());
				
				try {
					return transactQuota.getUsage();
				} finally {
					transactQuota.disconnect();
				}
			}

			@Override
			protected void onFinish(DownloadResult<Usage> usage) {
				if (usage.getResult() != null) {
					TextView peakUsage = (TextView) findViewById(R.id.PeakUsage);
					TextView offPeakUsage = (TextView) findViewById(R.id.OffPeakUsage);

					peakUsage.setText(usage.getResult().getPeakUsage().toString());
					offPeakUsage.setText(usage.getResult().getOffPeakUsage().toString());
				} else if (usage.isInvalidCredentials()) {
					Dialog dialog = createSettingsMissingDialog(getString(R.string.invalid_account_details));
					dialog.show();
				} else {
					AlertDialog dialog = createErrorDialog(usage.getErrorMessage());
					dialog.show();
				}
			}
		};

		downloadTask.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			startActivity(new Intent(this, PrefsActivity.class));
			return true;
		}
		return false;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.refresh_button:
			refreshUsage();
			break;
		}
	}

	private AlertDialog createErrorDialog(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
				.setCancelable(false)
				.setNeutralButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		return builder.create();
	}

	private AlertDialog createSettingsMissingDialog(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(message)
				.setCancelable(true)
				.setPositiveButton(R.string.settings_label,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								startActivity(new Intent(
										TransactQuotaActivity.this,
										PrefsActivity.class));
							}
						});
		return builder.create();
	}
}