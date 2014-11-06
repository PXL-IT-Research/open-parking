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
import be.pxl.parking.antwerpen.AntwerpUtils;
import be.pxl.parking.brussel.BrusselsUtils;
import be.pxl.parking.osmsearch.OsmResult;
import be.pxl.parking.osmsearch.OsmSearchUtils;
import be.pxl.stilkin.kingparking.R;

public class MainActivity extends Activity {
	private MapFragment mapFrag;
	private SearchView searchView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.empty_linearlayout);

		this.mapFrag = new MapFragment();

		FragmentManager fragmentManager = getFragmentManager();

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.ll_vertical, this.mapFrag);
		fragmentTransaction.commit();

		AntwerpUtils antwerp = new AntwerpUtils(mapFrag);
		antwerp.loadParkings(this);
		// TODO: add refresh button?

		BrusselsUtils brussels = new BrusselsUtils(mapFrag);
		brussels.loadParkings(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// super.onNewIntent(intent);
		Log.d("onNewIntent", intent.getAction());

		if (Intent.ACTION_VIEW.equalsIgnoreCase(intent.getAction())) {
			String osmJson = intent.getExtras().getString(SearchManager.EXTRA_DATA_KEY);
			OsmResult result = OsmSearchUtils.convertJsonToOsmResult(osmJson);
			// TODO: add result marker
			
			// scroll to position
			if (result != null) {
				double latitude = Double.parseDouble(result.getLat());
				double longitude = Double.parseDouble(result.getLon());
				this.mapFrag.focusOnPosition(latitude, longitude, 17);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		MenuItem menuItem = menu.findItem(R.id.item_search);
		searchView = (SearchView) menuItem.getActionView();

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
