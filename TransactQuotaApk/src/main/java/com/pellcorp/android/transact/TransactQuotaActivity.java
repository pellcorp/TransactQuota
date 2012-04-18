package com.pellcorp.android.transact;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TransactQuotaActivity extends Activity implements OnClickListener {
	private static final String TAG = "TransactQuotaActivity";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.i(TAG, "Starting onCreate");
		
		setContentView(R.layout.main);

		Button refreshButton = (Button) findViewById(R.id.refresh_button);
		refreshButton.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		Log.i(TAG, "Starting onResume");
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
				Log.e(TAG, "refreshUsage", e);
				AlertDialog dialog = createErrorDialog(e.getMessage());
				dialog.show();
			}
		} else {
			Log.i(TAG, "Username and password not provided");
			Dialog dialog = createSettingsMissingDialog(getString(R.string.settings_missing_label));
			dialog.show();
		}
	}
	
	private void doUsageDownload(final Preferences preferences) throws InterruptedException,
			ExecutionException, TimeoutException {

		DownloadTask<Usage> downloadTask = new DownloadTask<Usage>(this) {
			@Override
			protected Usage doTask() throws Exception {
				TransactQuota transactQuota = new TransactQuota(
					preferences.getTunnelConfig(),
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