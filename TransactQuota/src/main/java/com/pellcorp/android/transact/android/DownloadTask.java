package com.pellcorp.android.transact.android;

import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.pellcorp.android.transact.InvalidCredentialsException;
import com.pellcorp.android.transact.UsageNotAvailableException;

public abstract class DownloadTask<Result> extends AsyncTask<String, Void, DownloadResult<Result>> {
	private static final String TAG = DownloadTask.class.getName();
	
	private final ProgressDialog dialog;
	private String loadingMessage;
	
	public DownloadTask(Context context) {
		dialog = new ProgressDialog(context);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	}
	
	public void setLoadingMessage(String message) {
		this.loadingMessage = message;
	}
	
	@Override
	protected void onPreExecute() {
		dialog.show();
		dialog.setMessage(loadingMessage);
	}

	@Override
	protected DownloadResult<Result> doInBackground(String... params) {
		try {
			return new DownloadResult<Result>(doTask(params[0], params[1]));
		} catch(InvalidCredentialsException e) {
			return new DownloadResult<Result>(true);
		} catch (UsageNotAvailableException e) {
			return new DownloadResult<Result>(e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "Connectivity Exception", e);
			return new DownloadResult<Result>(e.getMessage());
		}
	}
	
	protected abstract Result doTask(String username, String password) throws IOException;

	protected void onPostExecute(DownloadResult<Result> result) {
		if (dialog.isShowing()) {
            dialog.dismiss();
        }
	}
}
