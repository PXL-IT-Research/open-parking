package be.pxl.parkingdata.brussel;

import java.util.List;

public class BxlParkingWrapper {
	private int nhits;
	private BxlParameters parameters;
	private List<BxlParkingRecord> records;

	public synchronized int getNhits() {
		return nhits;
	}

	public synchronized void setNhits(int nhits) {
		this.nhits = nhits;
	}

	public synchronized BxlParameters getParameters() {
		return parameters;
	}

	public synchronized void setParameters(BxlParameters parameters) {
		this.parameters = parameters;
	}

	public synchronized List<BxlParkingRecord> getRecords() {
		return records;
	}

	public synchronized void setRecords(List<BxlParkingRecord> records) {
		this.records = records;
	}

}
