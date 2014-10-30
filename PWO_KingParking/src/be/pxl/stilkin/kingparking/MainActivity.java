package be.pxl.stilkin.kingparking;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import be.pxl.parking.antwerpen.AntwerpUtils;

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
		antwerp.loadAntwerpParkingZones(this);

		// TODO: add refresh button?
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
