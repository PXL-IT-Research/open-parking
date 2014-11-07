package be.pxl.parkingdata.kortrijk;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
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

/**
 * 
 * @author stilkin
 * 
 */
public class KortrijkUtils extends CityParkings {
	public static final String KORTRIJK_PARKINGS_URL = "http://www.parkodata.be/OpenData/ParkoInfo.xml";
	public static final String CACHE_FILENAME_KORTRIJK = "parking_cache_kortrijk";
	private static final String XML_ROOT_NODE = "ITSPS";
	private static final String XML_AUTHORITY_TAG = "Authority";
	private static final String XML_OPERATOR_TAG = "Operator";
	private static final String XML_OFFSTREETPARKING_TAG = "OffstreetParking";
	private static final String XML_GENERALINFO_TAG = "GeneralInfo";
	private static final String XML_IDINFO_TAG = "IDInfo";
	private static final String XML_NAME_TAG = "Name";
	private static final String XML_GEOLOCATION_TAG = "GeoLocation";
	private static final String XML_LATITUDE_TAG = "Latitude";
	private static final String XML_LONGITUDE_TAG = "Longitude";
	private static final String XML_ADDRESS_TAG = "Address";
	private static final String XML_STREETNAME_TAG = "StreetName";
	private ItemizedIconOverlay<OverlayItem> mKrtParkingOverlay;

	public KortrijkUtils(MapFragment mapFrag, Context ctx) {
		super(mapFrag, ctx);
	}

	@Override
	public void loadParkings(Context ctx) {
		// load from file
		FileReaderAsyncTask fileTask = new FileReaderAsyncTask(new FileReadCallbackHandler(), ctx);
		fileTask.execute(CACHE_FILENAME_KORTRIJK);
	}

	private class FileReadCallbackHandler implements IAsyncCallback<String> {
		@Override
		public void onOperationCompleted(String result) {
			if (result != null && result.length() > 0) {
				processXmlResult(result);
			} else {
				Log.d("FileReadCallbackHandler", "Cache empty...");
			}

			// fetch from web resource
			loadKortrijkParkingsFromWeb();
		}
	}

	public void loadKortrijkParkingsFromWeb() {
//		Log.d("loadKortrijkParkingsFromWeb", "Fetching data from web resource");
		HttpGetAsyncTask getTask = new HttpGetAsyncTask(new HttpGetCallBackHandler());
		getTask.execute(KORTRIJK_PARKINGS_URL);
	}

	private class HttpGetCallBackHandler implements IAsyncCallback<String> {
		@Override
		public void onOperationCompleted(String result) {
			if (result != null && result.length() > 0) {
				// save to cache
				FileWriterAsyncTask writeTask = new FileWriterAsyncTask(null, getContext());
				writeTask.execute(CACHE_FILENAME_KORTRIJK, result);

				processXmlResult(result);
			}
		}
	}

	@SuppressLint("DefaultLocale")
	public void processXmlResult(String xmlResult) {
		Log.d("processXmlResult", "Displaying Kortrijk data: " );
		List<KrtParkingInfo> parkings = parseXml(xmlResult);

		if (parkings == null) { // nothing to display
			return;
		}

		final Drawable parking_marker = getContext().getResources().getDrawable(
				R.drawable.ic_parking_marker);
		ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
		OverlayItem overlayItem;
		String description;
		for (KrtParkingInfo parking : parkings) {
			if (parking.getGeoLocation() != null) {
				description = String.format("%s\n%s", parking.getName(),
						parking.getAddress());
				overlayItem = new OverlayItem(parking.getName(), description,
						parking.getGeoLocation());
				overlayItem.setMarker(parking_marker);
				items.add(overlayItem);
			}
		}

		// remove previous data
		if (mKrtParkingOverlay != null) {
			mapFrag.removeOverlay(mKrtParkingOverlay);
			mKrtParkingOverlay.removeAllItems();
		}

		// display new data
		final ResourceProxyImpl resourceProxy = new ResourceProxyImpl(getContext());
		this.mKrtParkingOverlay = new ItemizedIconOverlay<OverlayItem>(items,
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

		mapFrag.addOverlay(mKrtParkingOverlay);
	}

	private List<KrtParkingInfo> parseXml(String xmlResult) {
		List<KrtParkingInfo> parkings = new ArrayList<KrtParkingInfo>();
		try {
			// convert XML to JSON?
			JSONObject xmlJsonObj = XML.toJSONObject(xmlResult);
			xmlJsonObj = xmlJsonObj.getJSONObject(XML_ROOT_NODE);
			xmlJsonObj = xmlJsonObj.getJSONObject(XML_AUTHORITY_TAG);
			xmlJsonObj = xmlJsonObj.getJSONObject(XML_OPERATOR_TAG);
			JSONArray parkingArray = xmlJsonObj.getJSONArray(XML_OFFSTREETPARKING_TAG);
			JSONObject parking;
			KrtParkingInfo info;
			for (int p = 0; p < parkingArray.length(); p++) {
				parking = parkingArray.getJSONObject(p);
				info = processJsonToParkingInfo(parking);
				parkings.add(info);
			}

		} catch (JSONException jse) {
			jse.printStackTrace();
		}
		return parkings;
	}

	private KrtParkingInfo processJsonToParkingInfo(JSONObject parking) {
		KrtParkingInfo parkingInfo = new KrtParkingInfo();
		JSONObject tmp;
		JSONObject generalInfo = parking.getJSONObject(XML_GENERALINFO_TAG);

		// get name
		tmp = generalInfo.getJSONObject(XML_IDINFO_TAG);
		String name = tmp.getString(XML_NAME_TAG);
		parkingInfo.setName(name);

		// get position
		tmp = generalInfo.getJSONObject(XML_GEOLOCATION_TAG);
		double latitude = tmp.getDouble(XML_LATITUDE_TAG);
		double longitude = tmp.getDouble(XML_LONGITUDE_TAG);
		parkingInfo.setGeoLocation(new GeoPoint(latitude, longitude));

		// get address
		tmp = generalInfo.getJSONObject(XML_ADDRESS_TAG);
		String street = tmp.getString(XML_STREETNAME_TAG);
		parkingInfo.setAddress(street);

		return parkingInfo;
	}

}
