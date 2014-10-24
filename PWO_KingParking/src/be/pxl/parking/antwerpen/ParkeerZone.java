package be.pxl.parking.antwerpen;

public class ParkeerZone {
    private String tariefzone;
    private String tariefkleur;
    private String begindatum;
    private String geometry;
    private transient Geometry parsedGeometry;
    private String shape_length;
    private String shape_area;

    public String getTariefzone() {
	return this.tariefzone;
    }

    public void setTariefzone(String tariefzone) {
	this.tariefzone = tariefzone;
    }

    public String getTariefkleur() {
	return this.tariefkleur;
    }

    public void setTariefkleur(String tariefkleur) {
	this.tariefkleur = tariefkleur;
    }

    public String getBegindatum() {
	return this.begindatum;
    }

    public void setBegindatum(String begindatum) {
	this.begindatum = begindatum;
    }

    /**
     * Gives the geometry of a zone in JSON format
     *
     * @return
     */
    public String getGeometry() {
	return this.geometry;
    }

    public void setGeometry(String geometry) {
	this.geometry = geometry;
    }

    public String getShape_length() {
	return this.shape_length;
    }

    public void setShape_length(String shape_length) {
	this.shape_length = shape_length;
    }

    public String getShape_area() {
	return this.shape_area;
    }

    public void setShape_area(String shape_area) {
	this.shape_area = shape_area;
    }

    public Geometry getParsedGeometry() {
	return this.parsedGeometry;
    }

    public void setParsedGeometry(Geometry parsedGeometry) {
	this.parsedGeometry = parsedGeometry;
    }

    @Override
    public String toString() {
	return "ParkeerZone [tariefzone=" + this.tariefzone + ", tariefkleur=" + this.tariefkleur
		+ "]";
    }

}
