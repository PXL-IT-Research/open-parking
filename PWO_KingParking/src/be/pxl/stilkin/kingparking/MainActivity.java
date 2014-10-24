package be.pxl.stilkin.kingparking;

import java.util.List;

import org.osmdroid.views.overlay.Overlay;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import be.pxl.it.network.HttpGetAsyncTask;
import be.pxl.it.network.IAsyncCallback;
import be.pxl.parking.antwerpen.AntwerpUtils;
import be.pxl.parking.antwerpen.ParkeerZone;

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

		HttpGetAsyncTask getTask = new HttpGetAsyncTask(new GetCallBackHandler());
		String getUrl = "http://datasets.antwerpen.be/v1/geografie/paparkeertariefzones.json";
		getTask.execute(getUrl);
	}

	private class GetCallBackHandler implements IAsyncCallback<String> {
		@Override
		public void onOperationCompleted(String result) {
			List<ParkeerZone> zones = AntwerpUtils.parseJson(result);
			List<Overlay> zoneOverlays = AntwerpUtils.generateParkingzoneOverlays(zones,
					getApplicationContext());
			for (Overlay zone : zoneOverlays) {
				MainActivity.this.mapFrag.addOverlay(zone);
			}
		}
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
