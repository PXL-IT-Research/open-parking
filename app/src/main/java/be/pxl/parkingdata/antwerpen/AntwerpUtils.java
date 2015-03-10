package be.pxl.parkingdata.antwerpen;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.List;

import be.pxl.itresearch.io.FileReaderAsyncTask;
import be.pxl.itresearch.io.FileWriterAsyncTask;
import be.pxl.itresearch.io.HttpGetAsyncTask;
import be.pxl.itresearch.io.IAsyncCallback;
import be.pxl.parking.components.CityParkings;
import be.pxl.parking.gui.MapFragment;
import be.pxl.parking.gui.MyPathOverlay;

/**
 * 
 * @author stilkin
 * 
 */
public class AntwerpUtils extends CityParkings {
	// public static final String ANTWERP_ZONES_URL = "http://datasets.antwerpen.be/v1/geografie/paparkeertariefzones.json"; // deprecated
    public static final String ANTWERP_ZONES_URL = "http://datasets.antwerpen.be/v4/gis/paparkeertariefzones.json";
    public static final String CACHE_FILENAME_ANTWERP = "parking_cache_antwerp"; // NOPMD by stilkin on 4-3-15 13:47

	public static final String RED = "Rood";
	public static final String BLUE = "Blauw";
	public static final String LIGHT_GREEN = "Lichtgroen";
	public static final String ORANGE = "Oranje";
	public static final String YELLOW = "Geel";
	public static final String DARK_GREEN = "Donkergroen";

	private List<Overlay> zoneOverlays;

	public AntwerpUtils(MapFragment mapFrag, Context ctx) {
		super(mapFrag, ctx);
	}

	/**
	 * Will load zones from disk if cache is present, otherwise will load directly from site.
	 * 
	 * @param ctx
	 */
	@Override
	public void loadParkings(Context ctx) {
		// load from file
		FileReaderAsyncTask fileTask = new FileReaderAsyncTask(new FileReadCallbackHandler(), ctx);
		fileTask.execute(CACHE_FILENAME_ANTWERP);
	}

	private class FileReadCallbackHandler implements IAsyncCallback<String> {
		@Override
		public void onOperationCompleted(String result) {
			if (result != null && result.length() > 0) {
				processJsonParkeerzones(result);
			} else {
				Log.d("FileReadCallbackHandler", "Cache empty...");
			}

			// fetch from web resource
			loadAntwerpParkingZonesFromWeb();
		}
	}

	public void loadAntwerpParkingZonesFromWeb() {
		// load from site
		// Log.d("loadAntwerpZonesFromWeb", "Fetching data from web resource");
		final HttpGetAsyncTask getTask = new HttpGetAsyncTask(new HttpGetCallBackHandler());
		getTask.execute(ANTWERP_ZONES_URL);
	}

	private class HttpGetCallBackHandler implements IAsyncCallback<String> {
		@Override
		public void onOperationCompleted(String result) {
			if (result != null && result.length() > 0) {
				// save to file?
				final FileWriterAsyncTask writeTask = new FileWriterAsyncTask(null, getContext());
				writeTask.execute(AntwerpUtils.CACHE_FILENAME_ANTWERP, result);

				processJsonParkeerzones(result);
			}
		}
	}

	public void processJsonParkeerzones(String json) {
		Log.d("processJsonParkeerzones", "Displaying Antwerp data: ");

		final List<ParkeerZone> zones = AntwerpUtils.parseJson(json);
		if (zones == null) {
			return;
		}

		// remove previous data
		if (zoneOverlays != null) {
			for (Overlay zone : zoneOverlays) {
				mapFrag.removeOverlay(zone);
			}
			zoneOverlays.clear();
		}

		// display new data
		zoneOverlays = AntwerpUtils.generateParkingzoneOverlays(zones, getContext());
		for (Overlay zone : zoneOverlays) {
			mapFrag.addOverlay(zone);
		}
	}

	/* ** Static methods ** */

	public static int getColorFromString(String colorName) {
		int color = Color.CYAN;
		if (RED.equals(colorName)) {
			color = Color.RED;
		} else if (BLUE.equals(colorName)) {
			color = Color.BLUE;
		} else if (LIGHT_GREEN.equals(colorName)) {
			color = Color.GREEN;
		} else if (ORANGE.equals(colorName)) {
			color = 0xffff8000;
		} else if (YELLOW.equals(colorName)) {
			color = Color.YELLOW;
		} else if (DARK_GREEN.equals(colorName)) {
			color = 0xff008020;
		}
		return color;
	}

	public static List<ParkeerZone> parseJson(String parkeerJson) {
		List<ParkeerZone> zones = null;
		final Gson jsonHelper = new Gson();
		final ParkeerWrapper parkeerWrapper = jsonHelper.fromJson(parkeerJson, ParkeerWrapper.class);
		if (parkeerWrapper != null) {
			zones = parkeerWrapper.getData();
			if (zones != null) {
				String geomJson;
				Geometry geom;
				for (ParkeerZone zone : zones) {
					geomJson = zone.getGeometry();
					geomJson = trimWrappingArraysFromPolygons(geomJson);
					try {
						geom = jsonHelper.fromJson(geomJson, Geometry.class);
						zone.setParsedGeometry(geom);
					} catch (JsonSyntaxException jse) {
						Log.e("ParkeerZone", zone.toString());
						jse.printStackTrace();
					}
				}
			}
		}
		return zones;
	}

	/**
	 * This is a patch for dirty JSON coming back from the server. Some zones have more array nesting than others
	 * 
	 * @param json
	 * @return
	 */
	private static String trimWrappingArraysFromPolygons(String json) {
		String trim = json.trim();
		trim = trim.replace("[[[[", "[[[");
		trim = trim.replace("]]]]", "]]]");
		return trim;
	}

	public static List<Overlay> generateParkingzoneOverlays(List<ParkeerZone> zones, Context ctx) {
		final List<Overlay> overlays = new ArrayList<Overlay>();
		double latit, longit; // NOPMD by stilkin on 4-3-15 13:46
		int color;
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);

		Geometry geom;
		MyPathOverlay zoneOverlay;
		for (ParkeerZone zone : zones) {
			geom = zone.getParsedGeometry();
			if (geom != null) {
				color = AntwerpUtils.getColorFromString(zone.getTariefkleur());
				zoneOverlay = new MyPathOverlay(color, ctx);
				paint = new Paint(paint);
				paint.setColor(color);
				paint.setAntiAlias(true);
				paint.setAlpha(75);
				zoneOverlay.setPaint(paint);
				for (List<List<Double>> pointList : geom.getCoordinates()) {
					for (List<Double> point : pointList) {
						if (point.size() > 1) {
							latit = point.get(1);
							longit = point.get(0);
							zoneOverlay.addPoint(new GeoPoint(latit, longit));
						}
					}
				}
				overlays.add(zoneOverlay);
			} else {
				Log.e("ParkeerZone", zone.toString());
			}
		}
		return overlays;
	}
}
