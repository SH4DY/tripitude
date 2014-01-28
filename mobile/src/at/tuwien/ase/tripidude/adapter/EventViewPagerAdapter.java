package at.tuwien.ase.tripidude.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import at.tuwien.ase.tripidude.fragments.EventDescriptionFragment;
import at.tuwien.ase.tripidude.fragments.EventUserFragment;

public class EventViewPagerAdapter extends FragmentPagerAdapter {
	
	private Fragment[] fragments;
	private final int numItems = 2;
	
	public EventViewPagerAdapter(FragmentManager fm) {
		super(fm);
		fragments = new Fragment[numItems];
		fragments[0] = new EventDescriptionFragment();
		fragments[1] = new EventUserFragment();
	}

	@Override
	public Fragment getItem(int index) {
		switch (index) {
        case 0:
            return fragments[0];
        case 1:
            return fragments[1];
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
