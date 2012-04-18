package com.pellcorp.android.transact;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "",
mode = ReportingInteractionMode.TOAST,
resToastText = R.string.crash_toast_text,
mailTo = "jason@pellcorp.com") 
public class MyApplication extends Application {
	@Override
    public void onCreate() {
        ACRA.init(this);
        
        super.onCreate();
    }
}
