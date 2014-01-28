package at.tuwien.ase.tripidude.fragments;

import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import at.tuwien.ase.tripidude.MainActivity;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.adapter.MapItemViewPagerAdapter;
import at.tuwien.ase.tripidude.api.MapItemAPI;
import at.tuwien.ase.tripidude.core.App;
import at.tuwien.ase.tripidude.core.FragmentController;
import at.tuwien.ase.tripidude.models.Event;
import at.tuwien.ase.tripidude.models.Hotspot;
import at.tuwien.ase.tripidude.models.MapItem;
import at.tuwien.ase.tripidude.models.Route;
import at.tuwien.ase.tripidude.utils.Log;

public class MapItemFragment extends FragmentController implements TabListener{

	private ViewPager viewPager;
	private MapItemViewPagerAdapter viewPagerAdapter;
	private ActionBar actionBar;

	private String[] tabs = {"overview_title", "event_title", "comment_title"};

	public static final String HOTSPOT_MODE = "hotspotMode";
	public static final String ROUTE_MODE = "routeMode";

	public static final String KEY_TYPE_MODE = "keyTypeMode";
	public static final String KEY_MAPITEM_ID = "keyMapItemId";

	private Bundle args;
	private String typeMode;

	private Hotspot hotspot = null;
	private Route route = null;
	private MapItem mapItem = null;

	private List<Event> events = null; 
	
	public static FragmentController newInstance(long id, boolean isHotspot) {
		Bundle args = new Bundle();
		args.putLong(MapItemFragment.KEY_MAPITEM_ID, id);
		if (isHotspot)
			args.putString(MapItemFragment.KEY_TYPE_MODE, MapItemFragment.HOTSPOT_MODE);
		else 
			args.putString(MapItemFragment.KEY_TYPE_MODE, MapItemFragment.ROUTE_MODE);

		MapItemFragment fragment = new MapItemFragment();
		fragment.setArguments(args);
		return fragment;
	}

	//interface for child fragments
	public interface MapItemPropertySetter {
		public void setMapItem(MapItem mapItem);
		public void setMapItemEvents(List<Event> events);
	}

	@Override
	protected int onDoCreateViewWithId() {
		return R.layout.fragment_mapitem_container;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		args = getArguments();		
		typeMode = args.getString(KEY_TYPE_MODE);
	}

	@Override
	protected void onCreate() throws Exception {
		ui.id(R.id.loading).visibility(View.VISIBLE);

		//init pager
		viewPager = ui.id(R.id.mapitem_pager).getViewPager();
		actionBar = getActivity().getActionBar();
		viewPagerAdapter = new MapItemViewPagerAdapter(getChildFragmentManager());

		viewPager.setOffscreenPageLimit(2);	
		viewPager.setAdapter(viewPagerAdapter);
		//actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Adding Tabs
		for (String tab_name : tabs) {
			tab_name = App.resources.getString(App.resources.getIdentifier(tab_name, "string", App.context.getPackageName()));
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}


		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	protected void onQuery() throws Exception {
		if (typeMode == HOTSPOT_MODE) {
			hotspot = (Hotspot) MapItemAPI.getInstance().getHotspot(args.getLong(KEY_MAPITEM_ID));
			mapItem = (MapItem) hotspot;
		}
		else if (typeMode == ROUTE_MODE) {
			route = (Route) MapItemAPI.getInstance().getRoute(args.getLong(KEY_MAPITEM_ID));
			mapItem = (MapItem) route;
		}
		((MainActivity) getActivity()).setCurrentMapItem(mapItem);
		events = MapItemAPI.getInstance().getEvents(mapItem.getId());
	}

	@Override
	protected void onShow() throws Exception {
		if (hotspot != null || route != null) {
			for (Fragment fragment : viewPagerAdapter.getFragments()) {
				MapItemPropertySetter callback = (MapItemPropertySetter) fragment;
				callback.setMapItem(mapItem);
				callback.setMapItemEvents(events);
			}
		}
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
	public void onDeactivate() throws Exception {
		super.onDeactivate();
		restoreNavigation();
	}
	
	@Override
	public void onDestroyView() {
		restoreNavigation();
		super.onDestroyView();
	}

	private void restoreNavigation() {
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.removeAllTabs();
	}

	@Override
	public void onTabReselected(Tab arg0, android.app.FragmentTransaction arg1) {}

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction arg1) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab arg0, android.app.FragmentTransaction arg1) {}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.debug(this, "onActivityResult");
	}
}
