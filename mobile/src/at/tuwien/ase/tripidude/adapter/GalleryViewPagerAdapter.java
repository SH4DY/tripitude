package at.tuwien.ase.tripidude.adapter;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import at.tuwien.ase.tripidude.fragments.GalleryPictureFragment;

public class GalleryViewPagerAdapter extends FragmentPagerAdapter {
	
	private Fragment[] fragments;
	private int numItems=0;
	
	public GalleryViewPagerAdapter(FragmentManager fm, List<String> imageURls) {
		super(fm);
		
		numItems = imageURls.size();
		fragments = new Fragment[imageURls.size()];

		for (int i = 0; i < imageURls.size(); i++) {
			
			Bundle args = new Bundle();
			args.putString(GalleryPictureFragment.PICTURE_URL, imageURls.get(i));
			GalleryPictureFragment fragment = new GalleryPictureFragment();
			fragment.setArguments(args);
			
			fragments[i] = fragment;
		}
	}

	@Override
	public Fragment getItem(int index) {		
		return fragments[index];
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
