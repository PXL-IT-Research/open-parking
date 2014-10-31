package be.pxl.parking.brussel;

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

public class BrusselsUtils extends CityParkings {
	public static final String BRUSSELS_PARKINGS_URL = "http://opendata.brussel.be/api/records/1.0/search?dataset=bruxelles_parkings_publics";
	public static final String CACHE_FILENAME_BRUSSELS = "parking_cache_brussels";

	private ItemizedIconOverlay<OverlayItem> mMyLocationOverlay;

	public BrusselsUtils(MapFragment mapFrag) {
		super(mapFrag);
	}

	@Override
	public void loadParkings(Context ctx) {
		// load from file
		FileReaderAsyncTask fileTask = new FileReaderAsyncTask(new FileReadCallbackHandler(), ctx);
		fileTask.execute(CACHE_FILENAME_BRUSSELS);
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
			loadBrusselsParkingsFromWeb();
		}
	}

	public void loadBrusselsParkingsFromWeb() {
		// load from site
		Log.d("loadBrusselsParkingsFromWeb", "Fetching data from web resource");
		HttpGetAsyncTask getTask = new HttpGetAsyncTask(new HttpGetCallBackHandler());
		getTask.execute(BRUSSELS_PARKINGS_URL);
	}

	private class HttpGetCallBackHandler implements IAsyncCallback<String> {
		@Override
		public void onOperationCompleted(String result) {
			// save to file?
			FileWriterAsyncTask writeTask = new FileWriterAsyncTask(null, getContext());
			writeTask.execute(CACHE_FILENAME_BRUSSELS, result);
			Log.d("HttpGetCallBackHandler", "Saving to cache");
			processJsonResult(result);
		}
	}

	@SuppressLint("DefaultLocale")
	public void processJsonResult(String json) {
		Log.d("parseJsonResult", "Displaying data");

		if (mMyLocationOverlay != null) {
			mapFrag.removeOverlay(mMyLocationOverlay);
			mMyLocationOverlay.removeAllItems();
		}

		ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
		final Drawable parking_marker = getContext().getResources().getDrawable(R.drawable.ic_parking_marker);

		List<BxlParking> parkings = parseJson(json);
		List<Double> coords;
		OverlayItem overlayItem;
		for (BxlParking parking : parkings) {
			coords = parking.getGeo();
			if (coords != null && coords.size() > 1) {
				GeoPoint location = new GeoPoint(coords.get(0), coords.get(1));
				String description = String.format("%s\n%d places", parking.getDescription(),
						parking.getNombre_de_places());
				overlayItem = new OverlayItem(parking.getSociete_gestionnaire(), description, location);
				overlayItem.setMarker(parking_marker);
				items.add(overlayItem);
			}
		}

		final ResourceProxyImpl resourceProxy = new ResourceProxyImpl(getContext());
		this.mMyLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items,
				new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
					@Override
					public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
						Toast.makeText(getContext(), item.getSnippet(), Toast.LENGTH_LONG)
								.show();
						return true; // We 'handled' this event.
					}

					@Override
					public boolean onItemLongPress(final int index, final OverlayItem item) {
						// TODO: show persistent popup
						return false;
					}
				}, resourceProxy);

		mapFrag.addOverlay(mMyLocationOverlay);
	}

	public static List<BxlParking> parseJson(String parkeerJson) {
		List<BxlParking> parkings = null;
		List<BxlParkingRecord> records = null;
		Gson jsonHelper = new Gson();
		BxlParkingWrapper parkeerWrapper = null;
		try {
			parkeerWrapper = jsonHelper.fromJson(parkeerJson, BxlParkingWrapper.class);
		} catch (JsonSyntaxException jse) {
			jse.printStackTrace();
		}
		if (parkeerWrapper != null) {
			records = parkeerWrapper.getRecords();
			parkings = new ArrayList<BxlParking>();
			if (records != null) {
				BxlParking parking;
				for (BxlParkingRecord record : records) {
					parking = record.getFields();
					if (parking != null) {
						parkings.add(parking);
					}
				}
			}
		}
		return parkings;
	}
}
