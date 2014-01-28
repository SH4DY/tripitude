package at.tuwien.ase.tripidude.fragments;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.adapter.GalleryViewPagerAdapter;
import at.tuwien.ase.tripidude.core.FragmentController;
import at.tuwien.ase.tripidude.models.Event;
import at.tuwien.ase.tripidude.models.MapItem;

public class GalleryFragment extends FragmentController {

	public static final String PICTURE_URLS = "pictureUrls";
	public static final String INITIAL_PAGE = "initialPage";
	
	private ViewPager viewPager;
	private GalleryViewPagerAdapter viewPagerAdapter;
	private Bundle args;
	
	private List<String> urls;
	private Integer initialPage;
	

	//interface for child fragments
	public interface MapItemPropertySetter {
		public void setMapItem(MapItem mapItem);
		public void setMapItemEvents(List<Event> events);
	}

	@Override
	protected int onDoCreateViewWithId() {
		return R.layout.fragment_gallery_container;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		args = getArguments();		
		urls = args.getStringArrayList(PICTURE_URLS);
		initialPage = args.getInt(INITIAL_PAGE, 0);
	}


	@Override
	protected void onCreate() throws Exception {

		ui.id(R.id.loading).visibility(View.VISIBLE);

		//init pager
		viewPager = ui.id(R.id.gallery_pager).getViewPager();
		viewPagerAdapter = new GalleryViewPagerAdapter(getChildFragmentManager(), urls);

		viewPager.setOffscreenPageLimit(urls.size()-1);	
		viewPager.setAdapter(viewPagerAdapter);
	}
	
	@Override
	protected boolean isQueryNeeded() {
		return false;
	}

	@Override
	protected void onQuery() throws Exception {}

	@Override
	protected void onShow() throws Exception {
		viewPager.setCurrentItem(initialPage);
	}

	@Override
	protected View getLoadingView() {
		return null;
	}

	@Override
	protected View getRealView() {
		return null;
	}

	@Override
	public void onPause() {
		cleanUp();
		super.onPause();
	}

	private void cleanUp() {
		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		viewPagerAdapter.destroy(ft);
		ft.commit();
	}

}
