package be.pxl.parking.components;

import android.app.Application;
import android.content.Context;
import be.pxl.parking.gui.MapFragment;

public abstract class CityParkings {
	protected final MapFragment mapFrag;
	protected final Context context;

	public CityParkings(MapFragment mapFrag, Context ctx) {
		this.mapFrag = mapFrag;
		this.context = ctx;
	}

	/**
	 * Load parking info into a mapFragment
	 * 
	 * @param ctx
	 */
	public abstract void loadParkings(Context ctx);

	public Context getContext() {
		return this.context;
	}
}
