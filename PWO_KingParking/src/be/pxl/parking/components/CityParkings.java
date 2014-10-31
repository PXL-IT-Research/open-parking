package be.pxl.parking.components;

import android.content.Context;
import be.pxl.parking.gui.MapFragment;

public abstract class CityParkings {
	protected final MapFragment mapFrag;
	protected final Context context;

	public CityParkings(MapFragment mapFrag) {
		this.mapFrag = mapFrag;
		context = this.mapFrag.getActivity();
	}

	/**
	 * Load parking info into a mapFragment
	 * 
	 * @param ctx
	 */
	public abstract void loadParkings(Context ctx);

	public Context getContext() {
		return context;
	}
}
