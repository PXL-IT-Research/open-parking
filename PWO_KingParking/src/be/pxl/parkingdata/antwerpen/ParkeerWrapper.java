package be.pxl.parkingdata.antwerpen;

import java.util.List;

public class ParkeerWrapper {
    private List<ParkeerZone> paparkeertariefzones;

    public List<ParkeerZone> getPaparkeertariefzones() {
	return this.paparkeertariefzones;
    }

    public void setPaparkeertariefzones(List<ParkeerZone> paparkeertariefzones) {
	this.paparkeertariefzones = paparkeertariefzones;
    }

}
