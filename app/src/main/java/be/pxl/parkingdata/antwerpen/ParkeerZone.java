package be.pxl.parkingdata.antwerpen;

/**
 * Changed to comply with API v4
 */
public class ParkeerZone {
    private String geometry;
    private transient Geometry parsedGeometry;
    private String tariefzone;
    private String tariefkleur;
    private String begindatum;
    private String shape_area;
    private String shape_len;


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

    public String getShape_len() {
        return this.shape_len;
    }

    public void setShape_len(String shape_len) {
        this.shape_len = shape_len;
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
