package be.pxl.parkingdata.brussel;

public class BxlParkingRecord {
	private String datasetid;
	private String recordid;
	private BxlParking fields;
	private String record_timestamp;

	public String getDatasetid() {
		return datasetid;
	}

	public void setDatasetid(String datasetid) {
		this.datasetid = datasetid;
	}

	public String getRecordid() {
		return recordid;
	}

	public void setRecordid(String recordid) {
		this.recordid = recordid;
	}

	public BxlParking getFields() {
		return fields;
	}

	public void setFields(BxlParking fields) {
		this.fields = fields;
	}

	public String getRecord_timestamp() {
		return record_timestamp;
	}

	public void setRecord_timestamp(String record_timestamp) {
		this.record_timestamp = record_timestamp;
	}

}
