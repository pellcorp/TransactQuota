package com.pellcorp.android.transact.android;

public class DownloadResult<R> {
	private R result;
	private boolean invalidCredentials;
	private String errorMessage;
	
	public DownloadResult(R result) {
		this.result = result;
	}
	
	public DownloadResult(boolean invalidCredentials) {
		this.invalidCredentials = invalidCredentials;
	}
	
	public DownloadResult(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public R getResult() {
		return result;
	}

	public boolean isInvalidCredentials() {
		return invalidCredentials;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
