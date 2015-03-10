package be.pxl.parking.gui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import be.pxl.parking.osmsearch.OsmResult;
import be.pxl.parking.osmsearch.OsmSearchUtils;
import be.pxl.parkingdata.antwerpen.AntwerpUtils;
import be.pxl.parkingdata.brussel.BrusselsUtils;
import be.pxl.parkingdata.gent.GentUtils;
import be.pxl.parkingdata.kortrijk.KortrijkUtils;
import be.pxl.stilkin.kingparking.R;

/**
 * @author stilkin
 */
public class MainActivity extends Activity {
    private MapFragment mapFrag;
    private OverlayItem overlayItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_linearlayout);

        if (mapFrag == null) {
            Log.w("onCreate", "creating new map fragment");
            this.mapFrag = new MapFragment();

            FragmentManager fragmentManager = getFragmentManager();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.ll_vertical, this.mapFrag);
            fragmentTransaction.commit();
        }

        loadParkingData();
    }

    private void loadParkingData() {
        AntwerpUtils antwerp = new AntwerpUtils(mapFrag, this);
        antwerp.loadParkings(this);

        BrusselsUtils brussels = new BrusselsUtils(mapFrag, this);
        brussels.loadParkings(this);

        KortrijkUtils kortrijk = new KortrijkUtils(mapFrag, this);
        kortrijk.loadParkings(this);

        GentUtils gent = new GentUtils(mapFrag, this);
        gent.loadParkings(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // super.onNewIntent(intent);
        Log.d("onNewIntent", intent.getAction());

        if (Intent.ACTION_VIEW.equalsIgnoreCase(intent.getAction())) {
            String osmJson = intent.getExtras().getString(SearchManager.EXTRA_DATA_KEY);
            OsmResult result = OsmSearchUtils.convertJsonToOsmResult(osmJson);

            if (result != null) {
                double latitude = Double.parseDouble(result.getLat());
                double longitude = Double.parseDouble(result.getLon());

                if (overlayItem != null) {
                    this.mapFrag.removeOverlayItem(overlayItem);
                }

                // add result marker
                GeoPoint location = new GeoPoint(latitude, longitude);
                overlayItem = new OverlayItem(result.getDisplay_name(), result.getDisplay_name(),
                        location);
                this.mapFrag.addOverlayItem(overlayItem);

                // scroll to position
                this.mapFrag.focusOnPosition(latitude, longitude, 17);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.item_search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_snapToLocation) {
            this.mapFrag.focusOnUserPosition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
