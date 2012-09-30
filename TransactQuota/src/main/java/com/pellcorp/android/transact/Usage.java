package com.pellcorp.android.transact;

import java.io.Serializable;
import java.math.BigDecimal;

public class Usage implements Serializable {
	private static final long serialVersionUID = -4571249672346342803L;
	
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