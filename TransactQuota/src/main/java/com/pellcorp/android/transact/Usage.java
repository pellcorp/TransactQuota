package com.pellcorp.android.transact;

import java.math.BigDecimal;

public class Usage {
	private BigDecimal peakUsage;
	private BigDecimal offPeakUsage;
	
	public Usage(BigDecimal peakUsage, BigDecimal offPeakUsage) {
		this.peakUsage = peakUsage;
		this.offPeakUsage = offPeakUsage;
	}

	public BigDecimal getPeakUsage() {
		return peakUsage;
	}

	public BigDecimal getOffPeakUsage() {
		return offPeakUsage;
	}
}