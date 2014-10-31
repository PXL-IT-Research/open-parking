package be.pxl.parking.gui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import be.pxl.parking.antwerpen.AntwerpUtils;
import be.pxl.parking.brussel.BrusselsUtils;
import be.pxl.stilkin.kingparking.R;

public class MainActivity extends Activity {
	private MapFragment mapFrag;

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
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.item_snapToLocation) {
			this.mapFrag.focusOnPosition();
		}
		return super.onOptionsItemSelected(item);
	}

}
