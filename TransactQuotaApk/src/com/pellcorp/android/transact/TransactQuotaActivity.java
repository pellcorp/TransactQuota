package com.pellcorp.android.transact;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TransactQuotaActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TextView peakUsage = (TextView) findViewById(R.id.PeakUsage);
        TextView offPeakUsage = (TextView) findViewById(R.id.OffPeakUsage);
        
        peakUsage.setText("25.123");
        offPeakUsage.setText("25.123");
        
        //TransactQuota quota = new TransactQuota("jpell", "finlay");
        
    }
   
}