package be.pxl.parkingdata.gent;

import com.google.gson.annotations.SerializedName;

public class GentParking {

	@SerializedName("long")
	private String longitude;
	@SerializedName("lat")
	private String latitude;
	private String naam;
	private String type;

	public synchronized String getLongitude() {
		return longitude;
	}

	public synchronized void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public synchronized String getLatitude() {
		return latitude;
	}

	public synchronized void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public synchronized String getNaam() {
		return naam;
	}

	public synchronized void setNaam(String naam) {
		this.naam = naam;
	}

	public synchronized String getType() {
		return type;
	}

	public synchronized void setType(String type) {
		this.type = type;
	}

}
