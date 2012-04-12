package com.pellcorp.android.transact;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class TransactQuotaActivity extends Activity {
	private static final String TAG = "Transact Quota";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TransactQuota quota = new TransactQuota("jpell", "finlay");
        
    }
   
}