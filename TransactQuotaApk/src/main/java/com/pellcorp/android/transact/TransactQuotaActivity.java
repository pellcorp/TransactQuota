package com.pellcorp.android.transact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.pellcorp.android.transact.TransactQuotaUsageReceiver.Receiver;

public class TransactQuotaActivity extends Activity implements Receiver {
	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	private TransactQuotaUsageReceiver receiver;
	private TextView peakUsage;
	private TextView offPeakUsage;

	private boolean usageLoaded;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		logger.info("Starting onCreate");

		setContentView(R.layout.main);

		peakUsage = (TextView) findViewById(R.id.PeakUsage);
		offPeakUsage = (TextView) findViewById(R.id.OffPeakUsage);
		
		IntentFilter filter = new IntentFilter(TransactQuotaUsageReceiver.ACTION_USAGE_DOWNLOADED);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new TransactQuotaUsageReceiver(this);
        registerReceiver(receiver, filter);
        
	}

	@Override
    protected void onStart() {
        super.onStart();

        logger.info("Starting onStart");
        
        if (!usageLoaded) {
            refreshUsage();
        }
    }
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	private void refreshUsage() {
		peakUsage.setText("-");
		offPeakUsage.setText("-");
		
		Intent intent = new Intent(this, TransactionQuotaService.class);
        startService(intent);
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
		case R.id.refresh:
			refreshUsage();
			return true;
		}
		return false;
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

	@Override
	public void onReceive(DownloadResult<Usage> result) {
		if (result.getResult() != null) {
			peakUsage.setText(result.getResult().getPeakUsage().toString());
			offPeakUsage.setText(result.getResult().getOffPeakUsage().toString());
			usageLoaded = true;
		} else if (result.isInvalidCredentials()) {
			Dialog dialog = createSettingsMissingDialog(getString(R.string.invalid_account_details));
			dialog.show();
		} else {
			AlertDialog dialog = createErrorDialog(result.getErrorMessage());
			dialog.show();
		}
	}
}