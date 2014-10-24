package be.pxl.stilkin.kingparking;

import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import be.pxl.it.network.HttpGetAsyncTask;
import be.pxl.it.network.IAsyncCallback;
import be.pxl.parking.antwerpen.AntwerpUtils;
import be.pxl.parking.antwerpen.ParkeerZone;

public class MainActivity extends Activity {
    private ParkingMapFragment mapFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.empty_linearlayout);

	this.mapFrag = new ParkingMapFragment();

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

	    MainActivity.this.mapFrag.addCustomOverLay(zones);

	}
    }

}
