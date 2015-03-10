package be.pxl.parkingdata.antwerpen;

import java.util.List;

/**
 * Changed to comply with API v4
 */
public class ParkeerWrapper {
    private List<ParkeerZone> data;

    public List<ParkeerZone> getData() {
        return this.data;
    }

    public void setData(List<ParkeerZone> data) {
        this.data = data;
    }

}
