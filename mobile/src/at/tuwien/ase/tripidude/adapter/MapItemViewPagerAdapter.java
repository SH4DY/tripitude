package at.tuwien.ase.tripidude.adapter;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import at.tuwien.ase.tripidude.fragments.MapItemCommentsFragment;
import at.tuwien.ase.tripidude.fragments.MapItemDescriptionFragment;
import at.tuwien.ase.tripidude.fragments.MapItemEventsFragment;

public class MapItemViewPagerAdapter extends FragmentPagerAdapter {
	
	private Fragment[] fragments;
	private final int numItems = 3;
	
	public MapItemViewPagerAdapter(FragmentManager fm) {
		super(fm);
		
		fragments = new Fragment[numItems];
		fragments[0] = new MapItemDescriptionFragment();
		fragments[1] = new MapItemEventsFragment();
		fragments[2] = new MapItemCommentsFragment();
		
	}

	@Override
	public Fragment getItem(int index) {
		
		switch (index) {
        case 0:
            return fragments[0];
        case 1:
            return fragments[1];
        case 2:
            return fragments[2];
        }
 
        return null;
	}

	@Override
	public int getCount() {
		return numItems;
	}
	
	public Fragment[] getFragments() {
		return fragments;
	}
	
	public Fragment getFragment(int pos) {
		return fragments[pos];
	}
	
	public void destroy(FragmentTransaction fragmentTransaction){
	    for(Fragment fragment : fragments){
	    	if (fragment != null)
	    		fragmentTransaction.remove(fragment);
	    }
	}
}
