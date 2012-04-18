package com.pellcorp.android.transact;

import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.pellcorp.android.transact.InvalidCredentialsException;
import com.pellcorp.android.transact.UsageNotAvailableException;

public abstract class DownloadTask<Result> extends AsyncTask<Void, Void, DownloadResult<Result>> {
	private static final String TAG = DownloadTask.class.getName();
	
	private final Context context;
	private ProgressDialog dialog;
	
	public DownloadTask(Context context) {
		this.context = context;
	}
	
	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(context);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage(context.getString(R.string.loading));
		dialog.show();
	}

	@Override
	protected DownloadResult<Result> doInBackground(Void ... whocares) {
		try {
			return new DownloadResult<Result>(doTask());
		} catch(InvalidCredentialsException e) {
			return new DownloadResult<Result>(true);
		} catch (UsageNotAvailableException e) {
			Log.e(TAG, "UsageNotAvailableException", e);
			return new DownloadResult<Result>(e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "Connectivity Exception", e);
			return new DownloadResult<Result>(e.getMessage());
		}
	}
	
	protected abstract Result doTask() throws IOException;
	protected abstract void onFinish(DownloadResult<Result> result);

	protected void onPostExecute(DownloadResult<Result> result) {
		if (dialog.isShowing()) {
			try {
		        dialog.dismiss();
		        dialog = null;
		    } catch (Exception e) {
		        // nothing
		    }
        }
		onFinish(result);
	}
}
