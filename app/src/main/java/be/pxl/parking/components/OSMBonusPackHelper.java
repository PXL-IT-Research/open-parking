package be.pxl.parking.components;

import android.os.AsyncTask;

import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.GeoPoint;

import java.util.List;

import be.pxl.itresearch.io.IAsyncCallback;

/**
 * Created by stilkin on 9/03/15.
 */
public class OSMBonusPackHelper {
    public static final String TYPE_PARKING = "Parking";
    public static final int MAX_RESULTS = 50;
    public static final double RADIUS_IN_DEGREES = 0.02;

    public static void findParkingsNearby(GeoPoint startPoint, IAsyncCallback<List<POI>> callback) {
        // find some parkings nearby
        PoiSearchTask poiSearchTask = new PoiSearchTask(startPoint, callback);
        poiSearchTask.execute(TYPE_PARKING);
    }

    private static class PoiSearchTask extends AsyncTask<String, Void, List<POI>> {
        private final IAsyncCallback<List<POI>> resultListener;
        private final GeoPoint searchPoint;

        public PoiSearchTask(GeoPoint point, IAsyncCallback<List<POI>> uiClass) {
            searchPoint = point;
            resultListener = uiClass;
        }


        @Override
        protected List<POI> doInBackground(String... poiTypes) {
            List<POI> poiList = null;
            if (poiTypes.length > 0) {
                String poiType = poiTypes[0];
                NominatimPOIProvider poiProvider = new NominatimPOIProvider();
                poiList = poiProvider.getPOICloseTo(searchPoint, poiType, MAX_RESULTS, RADIUS_IN_DEGREES);
            }
            return poiList;
        }

        @Override
        protected void onPostExecute(List<POI> pois) {
            if (this.resultListener != null) {
                this.resultListener.onOperationCompleted(pois);
            }
        }
    }
}
