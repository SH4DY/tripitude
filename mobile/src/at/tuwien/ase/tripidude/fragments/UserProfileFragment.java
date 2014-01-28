package at.tuwien.ase.tripidude.fragments;

import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.adapter.UserViewPagerAdapter;
import at.tuwien.ase.tripidude.api.UserAPI;
import at.tuwien.ase.tripidude.core.App;
import at.tuwien.ase.tripidude.core.FragmentController;
import at.tuwien.ase.tripidude.models.Event;
import at.tuwien.ase.tripidude.models.User;

public class UserProfileFragment extends FragmentController implements TabListener{

	private ViewPager viewPager;
	private UserViewPagerAdapter viewPagerAdapter;
	private ActionBar actionBar;

	private String[] tabs = {"userprofile_title", "userprofile_events"};
	
	private User user = null;
	private List<Event> events = null;

	public interface UserPropertySetter {
		public void setUser(User user);
		public void setUserEvents(List<Event> events);
	}

	@Override
	protected int onDoCreateViewWithId() {
		return R.layout.fragment_userprofile_container;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
	}


	@Override
	protected void onCreate() throws Exception {
		ui.id(R.id.loading).visibility(View.VISIBLE);
		//init pager
		viewPager = ui.id(R.id.userprofile_pager).getViewPager();
		actionBar = getActivity().getActionBar();
		viewPagerAdapter = new UserViewPagerAdapter(getChildFragmentManager());
		viewPager.setOffscreenPageLimit(2);	
		viewPager.setAdapter(viewPagerAdapter);
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
		user = UserAPI.getInstance().getUser();
		events = UserAPI.getInstance().getEvents();
	}

	@Override
	protected void onShow() throws Exception {
		if (user != null) {
			for (Fragment fragment : viewPagerAdapter.getFragments()) {
				UserPropertySetter callback = (UserPropertySetter) fragment;
				callback.setUser(user);
				callback.setUserEvents(events);
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
	public void onAttach(Activity activity) {
		super.onAttach(activity);
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
