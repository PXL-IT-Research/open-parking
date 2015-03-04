package be.pxl.parkingdata.brussel;

import java.util.List;

public class BxlParameters {
	private int rows;
	private String format;
	private List<String> dataset;

	public synchronized int getRows() {
		return rows;
	}

	public synchronized void setRows(int rows) {
		this.rows = rows;
	}

	public synchronized String getFormat() {
		return format;
	}

	public synchronized void setFormat(String format) {
		this.format = format;
	}

	public synchronized List<String> getDataset() {
		return dataset;
	}

	public synchronized void setDataset(List<String> dataset) {
		this.dataset = dataset;
	}

}
