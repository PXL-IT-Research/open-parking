package be.pxl.parkingdata.kortrijk;

import org.osmdroid.util.GeoPoint;

public class KrtParkingInfo {
	private String name;
	private GeoPoint geoLocation;
	private String address;

	public synchronized String getName() {
		return name;
	}

	public synchronized void setName(String name) {
		this.name = name;
	}

	public synchronized GeoPoint getGeoLocation() {
		return geoLocation;
	}

	public synchronized void setGeoLocation(GeoPoint geoLocation) {
		this.geoLocation = geoLocation;
	}

	public synchronized String getAddress() {
		return address;
	}

	public synchronized void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "KrtParkingInfo [name=" + name + ", geoLocation=" + geoLocation + ", address="
				+ address + "]";
	}

}
