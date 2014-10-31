package be.pxl.parking.brussel;

import java.util.List;

public class BxlParking {
	private String societe_gestionnaire;
	private List<Double> geo;
	private String description;
	private int nombre_de_places;

	public synchronized String getSociete_gestionnaire() {
		return societe_gestionnaire;
	}

	public synchronized void setSociete_gestionnaire(String societe_gestionnaire) {
		this.societe_gestionnaire = societe_gestionnaire;
	}

	public synchronized List<Double> getGeo() {
		return geo;
	}

	public synchronized void setGeo(List<Double> geo) {
		this.geo = geo;
	}

	public synchronized String getDescription() {
		return description;
	}

	public synchronized void setDescription(String description) {
		this.description = description;
	}

	public synchronized int getNombre_de_places() {
		return nombre_de_places;
	}

	public synchronized void setNombre_de_places(int nombre_de_places) {
		this.nombre_de_places = nombre_de_places;
	}

}
