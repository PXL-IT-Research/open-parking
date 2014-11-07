package be.pxl.parkingdata.brussel;

public class BxlParkingRecord {
	private String datasetid;
	private String recordid;
	private BxlParking fields;
	private String record_timestamp;

	public synchronized String getDatasetid() {
		return datasetid;
	}

	public synchronized void setDatasetid(String datasetid) {
		this.datasetid = datasetid;
	}

	public synchronized String getRecordid() {
		return recordid;
	}

	public synchronized void setRecordid(String recordid) {
		this.recordid = recordid;
	}

	public synchronized BxlParking getFields() {
		return fields;
	}

	public synchronized void setFields(BxlParking fields) {
		this.fields = fields;
	}

	public synchronized String getRecord_timestamp() {
		return record_timestamp;
	}

	public synchronized void setRecord_timestamp(String record_timestamp) {
		this.record_timestamp = record_timestamp;
	}

}
