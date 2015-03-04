package be.pxl.parking.osmsearch;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class OsmResult implements Serializable {
	private transient static final long serialVersionUID = -2171634701227233092L;
	private String osm_id;
	private String lat;
	private String lon;
	private String display_name;
	private String type;
	@SerializedName("class")
	private String class_name;
	private double importance;

	public synchronized String getLat() {
		return lat;
	}

	public synchronized void setLat(String lat) {
		this.lat = lat;
	}

	public synchronized String getLon() {
		return lon;
	}

	public synchronized void setLon(String lon) {
		this.lon = lon;
	}

	public synchronized String getDisplay_name() {
		return display_name;
	}

	public synchronized void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}

	public synchronized String getType() {
		return type;
	}

	public synchronized void setType(String type) {
		this.type = type;
	}

	public synchronized String getClass_name() {
		return class_name;
	}

	public synchronized void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public synchronized double getImportance() {
		return importance;
	}

	public synchronized void setImportance(double importance) {
		this.importance = importance;
	}

	public synchronized String getOsm_id() {
		return osm_id;
	}

	public synchronized void setOsm_id(String osm_id) {
		this.osm_id = osm_id;
	}

	@Override
	public String toString() {
		return "OsmResult [lat=" + lat + ", lon=" + lon + ", display_name=" + display_name
				+ ", type=" + type + ", class_name=" + class_name + ", importance=" + importance
				+ "]";
	}

}
