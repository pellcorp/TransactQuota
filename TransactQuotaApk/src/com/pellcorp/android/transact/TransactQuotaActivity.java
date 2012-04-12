package com.pellcorp.android.transact;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TransactQuotaActivity extends Activity implements  OnClickListener, OnSharedPreferenceChangeListener {
	// FIXME - is there a way to get android developer kit generate these for me if I define them
	// in some xml file.
	private static final String USERNAME_KEY = "username";
	private static final String PASSWORD_KEY = "password";
	
	private String username;
	private String password;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = sharedPreferences.getString(USERNAME_KEY, null);
        password = sharedPreferences.getString(PASSWORD_KEY, null);
        
        Button refreshButton = (Button) findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(this);
        
        refreshUsage();
    }
    
    private void refreshUsage() {
    	TextView peakUsage = (TextView) findViewById(R.id.PeakUsage);
        TextView offPeakUsage = (TextView) findViewById(R.id.OffPeakUsage);

        peakUsage.setText("-");
        offPeakUsage.setText("-");
        
        // TODO - this probably needs to be a separate intent, or else I need a refresh button.
        if (username != null && password != null) {
        	try {
	        	TransactQuota quota = new TransactQuota(username, password);
	        	Usage usage = quota.getUsage();
	        	peakUsage.setText(usage.getPeakUsage().toString());
	            offPeakUsage.setText(usage.getOffPeakUsage().toString());
        	} catch(UsageNotAvailableException e) {
        		// FIXME - display dialog message
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
			startActivity(new Intent(this, Prefs.class));
			return true;
			// More items go here (if any) ...
		}
		return false;
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(USERNAME_KEY)) {
			username = sharedPreferences.getString(USERNAME_KEY, null);
		} else if(key.equals(PASSWORD_KEY)) {
			password = sharedPreferences.getString(PASSWORD_KEY, null);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.refresh_button:
				refreshUsage();
				break;
		}
	}
}