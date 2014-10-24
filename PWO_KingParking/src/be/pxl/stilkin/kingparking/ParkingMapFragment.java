package be.pxl.stilkin.kingparking;

import java.util.List;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.pxl.parking.antwerpen.AntwerpUtils;
import be.pxl.parking.antwerpen.Geometry;
import be.pxl.parking.antwerpen.ParkeerZone;

public class ParkingMapFragment extends Fragment {
    private static final String PREFS_NAME = "openmaps_prefs";
    private static final String PREFS_SCROLL_X = "xpos";
    private static final String PREFS_SCROLL_Y = "ypos";
    private static final String PREFS_ZOOM_LEVEL = "zoomlevel";
    private static final int DEFAULT_SCROLL_X = 1073930;
    private static final int DEFAULT_SCROLL_Y = 699640;
    private static final int DEFAULT_ZOOM_LEVEL = 13;
    private static final int MIN_ZOOM_LEVEL = 8;

    private SharedPreferences mPrefs;
    private ResourceProxyImpl mResourceProxy;
    private MapView mMapView;
    private MyLocationNewOverlay mLocationOverlay;
    private ItemizedIconOverlay<OverlayItem> mMyLocationOverlay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	this.mResourceProxy = new ResourceProxyImpl(inflater.getContext().getApplicationContext());
	this.mMapView = new MapView(inflater.getContext(), 256, this.mResourceProxy);
	this.mMapView.setMinZoomLevel(MIN_ZOOM_LEVEL);
	return this.mMapView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);

	this.mMapView.setMultiTouchControls(true);

	this.mPrefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	this.mMapView.getController().setZoom(
		this.mPrefs.getInt(PREFS_ZOOM_LEVEL, DEFAULT_ZOOM_LEVEL));

	this.mMapView.setScrollX(this.mPrefs.getInt(PREFS_SCROLL_X, DEFAULT_SCROLL_X));
	this.mMapView.setScrollY(this.mPrefs.getInt(PREFS_SCROLL_Y, DEFAULT_SCROLL_Y));

	// show user on map
	final Context context = getActivity();
	GpsMyLocationProvider locProvider = new GpsMyLocationProvider(context);
	this.mLocationOverlay = new MyLocationNewOverlay(context, locProvider, this.mMapView);
	this.mLocationOverlay.enableMyLocation();
	this.mMapView.getOverlays().add(this.mLocationOverlay);

	// focus on position
	LocationManager mLocMgr = (LocationManager) context
		.getSystemService(Context.LOCATION_SERVICE);
	mLocMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 100,
		new LocationHandler());

	// addCustomOverLay();
    }

    public void addCustomOverLay(List<ParkeerZone> zones) {

	double latit, longit;
	int color;
	Paint paint = new Paint();
	paint.setStyle(Paint.Style.FILL);

	Geometry geom;
	for (ParkeerZone zone : zones) {
	    geom = zone.getParsedGeometry();
	    if (geom != null) {
		color = AntwerpUtils.getColorFromString(zone.getTariefkleur());
		MyPathOverlay zoneOverlay = new MyPathOverlay(color, getActivity());
		paint = new Paint(paint);
		paint.setColor(color);
		paint.setAntiAlias(true);
		paint.setAlpha(90);
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
		this.mMapView.getOverlays().add(zoneOverlay);
	    } else {
		Log.e("ParkeerZone", zone.toString());
	    }
	}

	// // ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
	// GeoPoint point3 = new GeoPoint(50.9, 5.4);
	// // Put overlay icon a little way from map centre
	// items.add(new OverlayItem("Here", "SampleDescription", point3));
	//
	// /* OnTapListener for the Markers, shows a simple Toast. */
	// this.mMyLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items,
	// new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
	// @Override
	// public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
	// Toast.makeText(getActivity(), item.getTitle(), Toast.LENGTH_SHORT).show();
	// return true; // We 'handled' this event.
	// }
	//
	// @Override
	// public boolean onItemLongPress(final int index, final OverlayItem item) {
	// Toast.makeText(getActivity(), item.getTitle(), Toast.LENGTH_LONG).show();
	// return false;
	// }
	// }, this.mResourceProxy);
	// this.mMapView.getOverlays().add(this.mMyLocationOverlay);
	this.mMapView.invalidate();
    }

    @Override
    public void onPause() {
	super.onPause();
	SharedPreferences.Editor edit = this.mPrefs.edit();
	edit.putInt(PREFS_SCROLL_X, this.mMapView.getScrollX());
	edit.putInt(PREFS_SCROLL_Y, this.mMapView.getScrollY());
	edit.putInt(PREFS_ZOOM_LEVEL, this.mMapView.getZoomLevel());
	edit.commit();
    }

    private class LocationHandler implements LocationListener {
	@Override
	public void onLocationChanged(Location location) {
	    // Point p = TileSystem.LatLongToPixelXY(location.getLatitude(), location.getLongitude(),
	    // ParkingMapFragment.this.mMapView.getZoomLevel(), null);
	    // p.x -= ParkingMapFragment.this.mMapView.getWidth() / 2;
	    // p.y -= ParkingMapFragment.this.mMapView.getHeight() / 2;
	    // ParkingMapFragment.this.mMapView.scrollTo(p.x, p.y);
	    // TODO: remove listener
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
    }
}
