package at.tuwien.ase.tripidude.fragments;

import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.adapter.EventViewPagerAdapter;
import at.tuwien.ase.tripidude.api.EventAPI;
import at.tuwien.ase.tripidude.core.App;
import at.tuwien.ase.tripidude.core.FragmentController;
import at.tuwien.ase.tripidude.models.Event;
import at.tuwien.ase.tripidude.models.User;

public class EventFragment extends FragmentController implements TabListener{

	private ViewPager viewPager;
	private EventViewPagerAdapter viewPagerAdapter;
	private ActionBar actionBar;

	private String[] tabs = {"overview_title", "attending_users_title"};

	private Bundle args;
	public static final String KEY_EVENT_ID = "keyEventId";

	private Event event = null;
	private List<User> attendingUsers = null; 

	//interface for child fragments
	public interface EventPropertySetter {
		public void setEvent(Event event);
		public void setAttendingUsers(List<User> attendingUsers);
	}

	@Override
	protected int onDoCreateViewWithId() {
		return R.layout.fragment_event_container;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		args = getArguments();
	}

	@Override
	protected void onCreate() throws Exception {
		ui.id(R.id.loading).visibility(View.VISIBLE);

		//init pager
		viewPager = ui.id(R.id.event_pager).getViewPager();
		actionBar = getActivity().getActionBar();
		viewPagerAdapter = new EventViewPagerAdapter(getChildFragmentManager());

		viewPager.setOffscreenPageLimit(2);	
		viewPager.setAdapter(viewPagerAdapter);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		for (String tab_name : tabs) {
			tab_name = App.resources.getString(App.resources.getIdentifier(tab_name, "string", App.context.getPackageName()));
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
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
		event = (Event) EventAPI.getInstance().getEvent(args.getLong(KEY_EVENT_ID));
		attendingUsers = EventAPI.getInstance().getAttendingUsers(event.getId());
	}

	@Override
	protected void onShow() throws Exception {
		if (event != null) {
			for (Fragment fragment : viewPagerAdapter.getFragments()) {
				EventPropertySetter callback = (EventPropertySetter) fragment;
				callback.setEvent(event);
				callback.setAttendingUsers(attendingUsers);
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
}
