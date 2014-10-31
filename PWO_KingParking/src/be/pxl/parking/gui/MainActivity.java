package be.pxl.parking.gui;

import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import be.pxl.itresearch.io.IAsyncCallback;
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		MenuItem menuItem = menu.findItem(R.id.item_search);
		searchView = (SearchView) menuItem.getActionView();
		searchView.setOnQueryTextListener(new SearchHandler());
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.item_snapToLocation) {
			this.mapFrag.focusOnPosition();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class SearchHandler implements OnQueryTextListener {

		@Override
		public boolean onQueryTextChange(String query) {
			return false;
		}

		@Override
		public boolean onQueryTextSubmit(String query) {
			OsmSearchUtils.searchOsm(query, new SearchCallbackHandler());
	        searchView.setIconified(true);
			searchView.clearFocus();
			return true;
		}
	}

	private class SearchCallbackHandler implements IAsyncCallback<List<OsmResult>> {
		@Override
		public void onOperationCompleted(List<OsmResult> result) {
			Log.d("SearchCallbackHandler", "" + result);
			// TODO Auto-generated method stub
		}
	}
}
