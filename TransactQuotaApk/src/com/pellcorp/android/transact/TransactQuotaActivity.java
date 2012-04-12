package com.pellcorp.android.transact;

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

public class TransactQuotaActivity extends Activity implements  OnClickListener {
	private static final String TAG = "MyActivity";
	
	// FIXME - is there a way to get android developer kit generate these for me if I define them
	// in some xml file.
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
    	TextView peakUsage = (TextView) findViewById(R.id.PeakUsage);
        TextView offPeakUsage = (TextView) findViewById(R.id.OffPeakUsage);
        
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = sharedPreferences.getString(USERNAME_KEY, "");
        String password = sharedPreferences.getString(PASSWORD_KEY, "");
        
        peakUsage.setText("-");
        offPeakUsage.setText("-");
        
        // TODO - this probably needs to be a separate intent, or else I need a refresh button.
        if (username.length() > 0 && password.length() > 0) {
        	try {
	        	TransactQuota quota = new TransactQuota(username, password);
	        	Usage usage = quota.getUsage();
	        	peakUsage.setText(usage.getPeakUsage().toString());
	            offPeakUsage.setText(usage.getOffPeakUsage().toString());
        	} catch(InvalidCredentialsException e) {
        		AlertDialog dialog = createErrorDialog(getString(R.string.invalid_account_details));
        		dialog.show();
        		
        	} catch(UsageNotAvailableException e) {
        		Log.e(TAG, "", e);
        		
        		AlertDialog dialog = createErrorDialog(e.getMessage());
        		dialog.show();
        	}
        } else {
        	Dialog dialog = createSettingsMissingDialog();
        	dialog.show();
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
			startActivity(new Intent(this, Prefs.class));
			return true;
			// More items go here (if any) ...
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
		.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   dialog.cancel();
	           }
	       });
		
		return builder.create();
	}
	
	private AlertDialog createSettingsMissingDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(R.string.settings_missing_label)
	       .setCancelable(false)
	       .setPositiveButton(R.string.settings_label, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   startActivity(
	        			   new Intent(TransactQuotaActivity.this, Prefs.class));
	           }
	       })
	       .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   dialog.cancel();
	           }
	       });
		return builder.create();
	}
}