package com.pellcorp.android.transact;

public class Usage {
	private Double peakUsage;
	private Double offPeakUsage;
	
	public Usage(Double peakUsage, Double offPeakUsage) {
		this.peakUsage = peakUsage;
		this.offPeakUsage = offPeakUsage;
	}

	public Double getPeakUsage() {
		return peakUsage;
	}

	public Double getOffPeakUsage() {
		return offPeakUsage;
	}
}