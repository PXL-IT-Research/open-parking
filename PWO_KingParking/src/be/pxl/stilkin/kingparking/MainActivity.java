package be.pxl.stilkin.kingparking;

import java.util.List;

import org.osmdroid.views.overlay.Overlay;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import be.pxl.it.network.FileReaderAsyncTask;
import be.pxl.it.network.FileWriterAsyncTask;
import be.pxl.it.network.HttpGetAsyncTask;
import be.pxl.it.network.IAsyncCallback;
import be.pxl.parking.antwerpen.AntwerpUtils;
import be.pxl.parking.antwerpen.ParkeerZone;

public class MainActivity extends Activity {
	private MapFragment mapFrag;
	public static final String CACHE_FILENAME_ANTWERP = "parking_cache_antwerp";
	private List<Overlay> zoneOverlays;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.empty_linearlayout);

		this.mapFrag = new MapFragment();

		FragmentManager fragmentManager = getFragmentManager();

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.ll_vertical, this.mapFrag);
		fragmentTransaction.commit();

		// load from file
		FileReaderAsyncTask fileTask = new FileReaderAsyncTask(new FileReadCallbackHandler(), this);
		fileTask.execute(CACHE_FILENAME_ANTWERP);		
		
		//TODO: add refresh button
	}
	

	
	private class FileReadCallbackHandler implements IAsyncCallback<String> {
		@Override
		public void onOperationCompleted(String result) {
			if (result != null && result.length() > 0) {
				parseJsonResult(result);
			} else {
				Log.d("FileReadCallbackHandler","Cache empty");
			}
			// fetch from website
			loadFromSite();
		}
	}
	
	private void loadFromSite(){
		Log.d("loadFromSite","Fetching data from site");
		HttpGetAsyncTask getTask = new HttpGetAsyncTask(new GetCallBackHandler());
		String getUrl = "http://datasets.antwerpen.be/v1/geografie/paparkeertariefzones.json";
		getTask.execute(getUrl);
	}
	
	private void parseJsonResult(String json) {
		Log.d("parseJsonResult","Displaying data");
		if (zoneOverlays != null) {
			for (Overlay zone : zoneOverlays) {
				MainActivity.this.mapFrag.removeOverlay(zone);
			}
			zoneOverlays.clear();
		}
		
		List<ParkeerZone> zones = AntwerpUtils.parseJson(json);
		zoneOverlays = AntwerpUtils.generateParkingzoneOverlays(zones,
				getApplicationContext());
		for (Overlay zone : zoneOverlays) {
			MainActivity.this.mapFrag.addOverlay(zone);
//			break;
		}
	}

	private class GetCallBackHandler implements IAsyncCallback<String> {
		@Override
		public void onOperationCompleted(String result) {
			// save to file?
			FileWriterAsyncTask writeTask = new FileWriterAsyncTask(null, getApplicationContext());
			writeTask.execute(CACHE_FILENAME_ANTWERP, result);
			Log.d("GetCallBackHandler","Saving to cache");
			parseJsonResult(result);
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
