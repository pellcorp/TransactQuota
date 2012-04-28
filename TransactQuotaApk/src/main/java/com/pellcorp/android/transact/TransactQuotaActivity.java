package com.pellcorp.android.transact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.pellcorp.android.transact.asynctask.DownloadResult;

public class TransactQuotaActivity extends Activity {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private TransactionQuotaService mBoundService;
	private TextView peakUsage;
	private TextView offPeakUsage;
	
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((TransactionQuotaService.LocalBinder)service).getService();

            Toast.makeText(TransactQuotaActivity.this, R.string.local_service_connected,
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
            Toast.makeText(TransactQuotaActivity.this, R.string.local_service_disconnected,
                    Toast.LENGTH_SHORT).show();
        }
    };
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		logger.info("Starting onCreate");
		
		setContentView(R.layout.main);
		
		peakUsage = (TextView) findViewById(R.id.PeakUsage);
		offPeakUsage = (TextView) findViewById(R.id.OffPeakUsage);
	}
	
	@Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, TransactionQuotaService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBoundService != null) {
            unbindService(mConnection);
        }
    }
	
	private void refreshUsage() {
		if (mBoundService != null) {
			DownloadResult<Usage> result = mBoundService.getUsage();
			if (result.getResult() != null) {
				peakUsage.setText(result.getResult().getPeakUsage().toString());
				offPeakUsage.setText(result.getResult().getOffPeakUsage().toString());
			} else if (result.isInvalidCredentials()) {
				Dialog dialog = createSettingsMissingDialog(getString(R.string.invalid_account_details));
				dialog.show();
			} else {
				AlertDialog dialog = createErrorDialog(result.getErrorMessage());
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
}