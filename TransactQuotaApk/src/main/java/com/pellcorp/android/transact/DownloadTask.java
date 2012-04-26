package com.pellcorp.android.transact;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.pellcorp.android.transact.InvalidCredentialsException;
import com.pellcorp.android.transact.UsageNotAvailableException;

public abstract class DownloadTask<Result> extends AsyncTask<Void, Void, DownloadResult<Result>> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
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
			logger.error("UsageNotAvailableException", e);
			return new DownloadResult<Result>(e.getMessage());
		} catch (IOException e) {
			logger.error("Connectivity Exception", e);
			return new DownloadResult<Result>(e.getMessage());
		} catch (Exception e) {
			logger.error("Unknown Exception", e);
			return new DownloadResult<Result>(e.getMessage());
		}
	}
	
	protected abstract Result doTask() throws Exception;
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
