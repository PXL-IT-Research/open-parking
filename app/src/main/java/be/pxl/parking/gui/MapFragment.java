package be.pxl.parking.gui;

import java.util.ArrayList;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.util.TileSystem;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
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
import android.widget.Toast;

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
	private ItemizedIconOverlay<OverlayItem> mItemOverlay;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.mResourceProxy = new ResourceProxyImpl(inflater.getContext().getApplicationContext());
		this.mMapView = new MapView(inflater.getContext(), 256, this.mResourceProxy);
		this.mMapView.setMinZoomLevel(MIN_ZOOM_LEVEL);
		// disable openGL to increase texture size
		this.mMapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
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

	/**
	 * Add an item to the default overlay
	 * 
	 * @param item
	 */
	public void addOverlayItem(OverlayItem item) {
		if (mItemOverlay == null) {
			initItemOverlay();
		}
		mItemOverlay.addItem(item);
		// TODO: do we need to refresh this?
	}

	public void removeOverlayItem(OverlayItem item) {
		if (mItemOverlay == null) {
			return;
		}
		mItemOverlay.removeItem(item);
	}

	private void initItemOverlay() {
		ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

		// display new data
		final ResourceProxyImpl resourceProxy = new ResourceProxyImpl(getActivity());
		this.mItemOverlay = new ItemizedIconOverlay<OverlayItem>(items,
				new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
					@Override
					public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
						Toast.makeText(MapFragment.this.getActivity(), item.getSnippet(),
								Toast.LENGTH_LONG).show();
						return true; // We 'handled' this event.
					}

					@Override
					public boolean onItemLongPress(final int index, final OverlayItem item) {
						// TODO: show persistent popup
						return false;
					}
				}, resourceProxy);

		addOverlay(mItemOverlay);
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

	public void focusOnUserPosition() {
		// track user position
		LocationManager mLocMgr = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		mLocMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10,
				new LocationHandler());
	}

	public void focusOnPosition(double latitude, double longitude, int zoomlevel) {
		IMapController controller = MapFragment.this.mMapView.getController();
		if (MapFragment.this.mMapView.getZoomLevel() < zoomlevel) {
			controller.setZoom(zoomlevel);
		}
		Point p = TileSystem.LatLongToPixelXY(latitude, longitude,
				MapFragment.this.mMapView.getZoomLevel(), null);
		p.x -= MapFragment.this.mMapView.getWidth() / 2;
		p.y -= MapFragment.this.mMapView.getHeight() / 2;
		MapFragment.this.mMapView.scrollTo(p.x, p.y);
	}

	public void focusOnPosition(double latitude, double longitude) {
		focusOnPosition(latitude, longitude, DEFAULT_ZOOM_LEVEL);
	}

	private class LocationHandler implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			focusOnPosition(location.getLatitude(), location.getLongitude());
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
