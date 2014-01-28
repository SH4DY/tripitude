package at.tuwien.ase.tripidude.test;

import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import at.tuwien.ase.tripidude.MainActivity;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.fragments.MapFragment;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
		
	private MainActivity testActivity;

	public MainActivityTest() {
		super(MainActivity.class);
	}
	
	@Override
    protected void setUp() throws Exception {
        super.setUp();
        testActivity = getActivity();        
    }
	
	@SmallTest
	public void testPreconditions() {
	    assertNotNull("testActivity is null", testActivity);
	}
	
	@SmallTest
	public void stringRessourceAvailable() {
	    assertNotNull("getActivity().getString() is null", getActivity().getString(R.string.app_name));
	}
	
	@MediumTest
	public void testMapFragmentAvailable() {
		Fragment fragment = waitForFragment(MainActivity.MAPFRAGMENT_TAG, 5000);
		MapFragment mapFragment = null;
		if (fragment instanceof MapFragment)
			mapFragment = (MapFragment) fragment;
	    assertNotNull("mapFragment is null", mapFragment);
	}
	
	
	protected Fragment waitForFragment(String tag, int timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() <= endTime) {
            Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(tag);
            if (fragment != null) {
                return fragment;
            }
        }
        return null;
    }
}
