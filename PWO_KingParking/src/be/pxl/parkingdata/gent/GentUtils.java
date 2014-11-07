package be.pxl.parkingdata.gent;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;
import be.pxl.itresearch.io.FileReaderAsyncTask;
import be.pxl.itresearch.io.FileWriterAsyncTask;
import be.pxl.itresearch.io.HttpGetAsyncTask;
import be.pxl.itresearch.io.IAsyncCallback;
import be.pxl.parking.components.CityParkings;
import be.pxl.parking.gui.MapFragment;
import be.pxl.stilkin.kingparking.R;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * 
 * @author stilkin
 * 
 */
public class GentUtils extends CityParkings {
	public static final String GHENT_PARKINGS_URL = "http://datatank.gent.be/Mobiliteit/ParkinglocatiesInGent.json";
	public static final String CACHE_FILENAME_GHENT = "parking_cache_ghent";

	private ItemizedIconOverlay<OverlayItem> mGentParkingOverlay;

	public GentUtils(MapFragment mapFrag) {
		super(mapFrag);
	}

	@Override
	public void loadParkings(Context ctx) {
		// load from file
		FileReaderAsyncTask fileTask = new FileReaderAsyncTask(new FileReadCallbackHandler(), ctx);
		fileTask.execute(CACHE_FILENAME_GHENT);
	}

	private class FileReadCallbackHandler implements IAsyncCallback<String> {
		@Override
		public void onOperationCompleted(String result) {
			if (result != null && result.length() > 0) {
				processJsonResult(result);
			} else {
				Log.d("FileReadCallbackHandler", "Cache empty...");
			}

			// fetch from web resource
			loadGhentParkingsFromWeb();
		}
	}

	public void loadGhentParkingsFromWeb() {
		// load from site
		Log.d("loadGhentParkingsFromWeb", "Fetching data from web resource");
		HttpGetAsyncTask getTask = new HttpGetAsyncTask(new HttpGetCallBackHandler());
		getTask.execute(GHENT_PARKINGS_URL);
	}

	private class HttpGetCallBackHandler implements IAsyncCallback<String> {
		@Override
		public void onOperationCompleted(String result) {
			if (result != null && result.length() > 0) {
				// save to file?
				FileWriterAsyncTask writeTask = new FileWriterAsyncTask(null, getContext());
				writeTask.execute(CACHE_FILENAME_GHENT, result);

				processJsonResult(result);
			}
		}
	}

	@SuppressLint("DefaultLocale")
	public void processJsonResult(String json) {
		Log.d("processJsonResult", "Displaying Ghent data: ");

		List<GentParking> parkings = parseJson(json);
		if (parkings == null) { // nothing to display
			return;
		}

		final Drawable parking_marker = getContext().getResources().getDrawable(
				R.drawable.ic_parking_marker);
		ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
		double latitude, longitude;
		OverlayItem overlayItem;
		GeoPoint location;
		for (GentParking parking : parkings) {
			location = null;
			try {
				latitude = Double.parseDouble(parking.getLatitude());
				longitude = Double.parseDouble(parking.getLongitude());
				location = new GeoPoint(latitude, longitude);
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
			}
			if (location != null) {
				String description = String.format("%s\n%s", parking.getNaam(),
						parking.getType());
				overlayItem = new OverlayItem(parking.getNaam(), description, location);
				overlayItem.setMarker(parking_marker);
				items.add(overlayItem);
			}
		}

		// remove previous data
		if (mGentParkingOverlay != null) {
			mapFrag.removeOverlay(mGentParkingOverlay);
			mGentParkingOverlay.removeAllItems();
		}

		// display new data
		final ResourceProxyImpl resourceProxy = new ResourceProxyImpl(getContext());
		this.mGentParkingOverlay = new ItemizedIconOverlay<OverlayItem>(items,
				new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
					@Override
					public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
						Toast.makeText(getContext(), item.getSnippet(), Toast.LENGTH_LONG).show();
						return true; // We 'handled' this event.
					}

					@Override
					public boolean onItemLongPress(final int index, final OverlayItem item) {
						// TODO: show persistent popup
						return false;
					}
				}, resourceProxy);

		mapFrag.addOverlay(mGentParkingOverlay);
	}

	public static List<GentParking> parseJson(String parkeerJson) {
		List<GentParking> parkings = null;
		Gson jsonHelper = new Gson();
		GentParkingWrapper parkeerWrapper = null;
		try {
			parkeerWrapper = jsonHelper.fromJson(parkeerJson, GentParkingWrapper.class);
		} catch (JsonSyntaxException jse) {
			jse.printStackTrace();
		}
		if (parkeerWrapper != null) {
			parkings = parkeerWrapper.getParkinglocatiesInGent();
		}
		return parkings;
	}
}
