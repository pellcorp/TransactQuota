package com.pellcorp.android.transact;

import java.io.Serializable;

public class DownloadResult<R> implements Serializable {
	private static final long serialVersionUID = -8445426032593156662L;
	
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
