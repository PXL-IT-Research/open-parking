package be.pxl.stilkin.kingparking;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class MapActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.empty_linearlayout);

	FragmentManager fragmentManager = getFragmentManager();
	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
	ParkingMapFragment fragment = new ParkingMapFragment();
	fragmentTransaction.add(R.id.ll_vertical, fragment);

	fragmentTransaction.commit();
    }
}
