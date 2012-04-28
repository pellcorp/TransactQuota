package com.pellcorp.android.transact.asynctask;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.AsyncTask;

import com.pellcorp.android.transact.InvalidCredentialsException;
import com.pellcorp.android.transact.UsageNotAvailableException;

public abstract class DownloadTask<Result> extends AsyncTask<Void, Void, DownloadResult<Result>> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public DownloadTask() {
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
		onFinish(result);
	}
}
