package com.pellcorp.android.transact;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class DownloadTask<Result> extends AsyncTask<String, Void, DownloadResult<Result>> {
	private final ProgressDialog dialog;
	private final Context context;
	
	public DownloadTask(Context context) {
		this.context = context;
		
		dialog = new ProgressDialog(context);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	}
	
	@Override
	protected void onPreExecute() {
		dialog.show();
		dialog.setMessage(context.getString(R.string.loading));
	}

	@Override
	protected DownloadResult<Result> doInBackground(String... params) {
		try {
			return new DownloadResult<Result>(doTask(params[0], params[1]));
		} catch(InvalidCredentialsException e) {
			return new DownloadResult<Result>(true);
		} catch (UsageNotAvailableException e) {
			return new DownloadResult<Result>(e.getMessage());
		}
	}
	
	protected abstract Result doTask(String username, String password);

	protected void onPostExecute(DownloadResult<Result> result) {
		if (dialog.isShowing()) {
            dialog.dismiss();
        }
	}
}