package be.pxl.parking.osmsearch;

import java.util.List;

import android.net.Uri;
import be.pxl.itresearch.io.HttpGetAsyncTask;
import be.pxl.itresearch.io.HttpIO;
import be.pxl.itresearch.io.IAsyncCallback;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class OsmSearchUtils {
	private static final String OSM_SEARCH_URL_FORMAT = "http://nominatim.openstreetmap.org/search.php?format=json&q=%s";
	private static final String LOCALIZED_SEARCH = ", Belgium";

	public static void searchOsmInBackground(String searchString,
			IAsyncCallback<List<OsmResult>> callback) {
		// load from site
		HttpGetAsyncTask getTask = new HttpGetAsyncTask(new HttpGetCallBackHandler(callback));
		searchString = Uri.encode(searchString + LOCALIZED_SEARCH);
		String url = String.format(OSM_SEARCH_URL_FORMAT, searchString);
		getTask.execute(url);
	}

	private static class HttpGetCallBackHandler implements IAsyncCallback<String> {
		private IAsyncCallback<List<OsmResult>> uiCallback;

		public HttpGetCallBackHandler(IAsyncCallback<List<OsmResult>> callback) {
			uiCallback = callback;
		}

		@Override
		public void onOperationCompleted(String result) {
			List<OsmResult> resultList = convertJsonToOsmResult(result);
			if (uiCallback != null) {
				uiCallback.onOperationCompleted(resultList);
			}
		}
	}

	public static List<OsmResult> convertJsonToOsmResult(String json) {
		Gson jsonHelper = new Gson();
		List<OsmResult> resultList = null;
		TypeToken<List<OsmResult>> listType = new TypeToken<List<OsmResult>>() {
		};
		try {
			resultList = jsonHelper.fromJson(json, listType.getType());
		} catch (JsonSyntaxException jse) {
			jse.printStackTrace();
		}
		return resultList;
	}

	public static List<OsmResult> searchOsm(String searchString) {
		searchString = Uri.encode(searchString + LOCALIZED_SEARCH);
		String url = String.format(OSM_SEARCH_URL_FORMAT, searchString);
		String resultJson = HttpIO.getHttpGetRequestContent(url);
		return convertJsonToOsmResult(resultJson);
	}

}
