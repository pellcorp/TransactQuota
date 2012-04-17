package com.pellcorp.android.transact;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
	private static final String TAG = TransactQuotaActivity.class.getName();

	private static final String USERNAME_KEY = "username";
	private static final String PASSWORD_KEY = "password";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button refreshButton = (Button) findViewById(R.id.refresh_button);
		refreshButton.setOnClickListener(this);

		refreshUsage();
	}

	private void refreshUsage() {
		if (isWifiConnected()) {
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(TransactQuotaActivity.this);
			final String username = sharedPreferences.getString(USERNAME_KEY, "");
			final String password = sharedPreferences.getString(PASSWORD_KEY, "");
	
			// TODO - this probably needs to be a separate intent, or else I need a
			// refresh button.
			if (username.length() > 0 && password.length() > 0) {
				new DownloadUsageTask().execute(username, password);
			} else {
				Dialog dialog = createSettingsMissingDialog(getString(R.string.settings_missing_label));
				dialog.show();
			}
		} else {
			Dialog dialog = createSettingsMissingDialog(getString(R.string.wifi_not_enabled));
			dialog.show();
		}
	}

	private boolean isWifiConnected() {
	    ConnectivityManager connectivityManager = (ConnectivityManager)
	        getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = null;
	    if (connectivityManager != null) {
	        networkInfo =
	            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    }
	    return networkInfo == null ? false : networkInfo.isConnected();
	}
	
	private class DownloadUsageTask extends AsyncTask<String, Void, Usage> {
		private boolean invalidCredentials;
		private String errorMessage;
		private ProgressDialog dialog;

		public DownloadUsageTask() {
			dialog = new ProgressDialog(TransactQuotaActivity.this);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		}
		
		@Override
		protected void onPreExecute() {
			dialog.show();
			dialog.setMessage(getString(R.string.loading_usage));
		}

		@Override
		protected Usage doInBackground(String... params) {
			try {
				TransactQuota quota = new TransactQuota(params[0], params[1]);
				return quota.getUsage();
			} catch(InvalidCredentialsException e) {
				invalidCredentials = true;
				return null;
			} catch (UsageNotAvailableException e) {
				Log.e(TAG, "", e);
				
				errorMessage = e.getMessage();
				return null;
			}
		}

		protected void onPostExecute(Usage usage) {
			if (dialog.isShowing()) {
	            dialog.dismiss();
	        }
			
			if (usage != null) {
				TextView peakUsage = (TextView) findViewById(R.id.PeakUsage);
				TextView offPeakUsage = (TextView) findViewById(R.id.OffPeakUsage);
				
				peakUsage.setText(usage.getPeakUsage().toString());
				offPeakUsage.setText(usage.getOffPeakUsage().toString());
			} else if (invalidCredentials) {
				Dialog dialog = createSettingsMissingDialog(getString(R.string.invalid_account_details));
				dialog.show();
			} else {
				AlertDialog dialog = createErrorDialog(errorMessage);
				dialog.show();
			}
		}
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
										TransactQuotaActivity.this, PrefsActivity.class));
							}
						});
		return builder.create();
	}
}