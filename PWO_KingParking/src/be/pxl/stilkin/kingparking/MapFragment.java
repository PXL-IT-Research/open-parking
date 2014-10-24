package be.pxl.stilkin.kingparking;

import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.util.TileSystem;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MapFragment extends Fragment {
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
	}

	public void addOverlay(Overlay overlay) {
		this.mMapView.getOverlays().add(overlay);
		this.mMapView.invalidate();
	}
	
	public void removeOverlay(Overlay overlay) {
		this.mMapView.getOverlays().remove(overlay);
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

	public void focusOnPosition() {
		// focus on position
		LocationManager mLocMgr = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		mLocMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 100,
				new LocationHandler());
	}

	private class LocationHandler implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
//			IMapController controller = ParkingMapFragment.this.mMapView.getController();
//			if (ParkingMapFragment.this.mMapView.getZoomLevel() < 17) {
//				controller.setZoom(17);
//			}

			Point p = TileSystem.LatLongToPixelXY(location.getLatitude(), location.getLongitude(),
					MapFragment.this.mMapView.getZoomLevel(), null);
			p.x -= MapFragment.this.mMapView.getWidth() / 2;
			p.y -= MapFragment.this.mMapView.getHeight() / 2;
			MapFragment.this.mMapView.scrollTo(p.x, p.y);
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
