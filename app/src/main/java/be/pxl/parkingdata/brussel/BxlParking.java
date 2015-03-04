package be.pxl.parkingdata.brussel;

import java.util.List;

public class BxlParking {
	private String societe_gestionnaire;
	private List<Double> geo;
	private String description;
	private int nombre_de_places;

	public String getSociete_gestionnaire() {
		return societe_gestionnaire;
	}

	public void setSociete_gestionnaire(String societe_gestionnaire) {
		this.societe_gestionnaire = societe_gestionnaire;
	}

	public List<Double> getGeo() {
		return geo;
	}

	public void setGeo(List<Double> geo) {
		this.geo = geo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getNombre_de_places() {
		return nombre_de_places;
	}

	public void setNombre_de_places(int nombre_de_places) {
		this.nombre_de_places = nombre_de_places;
	}

}
