package at.tuwien.ase.tripidude;


import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import at.tuwien.ase.tripidude.api.APIException;
import at.tuwien.ase.tripidude.api.UserAPI;
import at.tuwien.ase.tripidude.core.App;
import at.tuwien.ase.tripidude.core.BaseActivity;
import at.tuwien.ase.tripidude.core.FragmentController;
import at.tuwien.ase.tripidude.fragments.CreateMapItemFragment;
import at.tuwien.ase.tripidude.fragments.CreateRouteFragment;
import at.tuwien.ase.tripidude.fragments.EventsFragment;
import at.tuwien.ase.tripidude.fragments.HighscoresFragment;
import at.tuwien.ase.tripidude.fragments.LoginFragment;
import at.tuwien.ase.tripidude.fragments.LoginFragment.OnLoginFragmentListener;
import at.tuwien.ase.tripidude.fragments.MapFragment;
import at.tuwien.ase.tripidude.fragments.RegisterFragment;
import at.tuwien.ase.tripidude.fragments.RegisterFragment.OnRegisterFragmentListener;
import at.tuwien.ase.tripidude.fragments.SearchMapitemFragment;
import at.tuwien.ase.tripidude.fragments.UserProfileFragment;
import at.tuwien.ase.tripidude.models.DrawerMenuItem;
import at.tuwien.ase.tripidude.models.MapItem;
import at.tuwien.ase.tripidude.utils.DrawerMenuItemClickListener;
import at.tuwien.ase.tripidude.utils.Log;
import at.tuwien.ase.tripidude.utils.Utils;
import at.tuwien.ase.tripidude.views.MenuDrawer;

public class MainActivity extends BaseActivity implements OnLoginFragmentListener, OnRegisterFragmentListener {
	
	public static final String MAPFRAGMENT_TAG = "mapfragment_tag";
	//For tutorial
	private static final int SHOW_TUTORIAL_LOGIN = 99;
	private static final int SHOW_TUTORIAL_ACTION = 98;

	public interface OnActivityResultListener {
		public void onActivityResultFromActivity(int requestCode, int resultCode, Intent data);
	}
	
	private MenuDrawer drawer;
	private boolean isLoginView;
	private boolean isSearchView;

	private FragmentController registerFragmentController = null;
	private FragmentController loginFragmentController = null;
	
	private List<OnActivityResultListener> onActivityResultListener;
	
	private MapItem currentMapItem; //for Event
	private MapFragment mapFragment = null;
	
	
	public void addActivityResultListener(OnActivityResultListener listener) {
		if (Utils.isNullOrEmpty(onActivityResultListener)) 
			onActivityResultListener = new ArrayList<MainActivity.OnActivityResultListener>();
		onActivityResultListener.add(listener);
	}
	
	public void addActivityResultListener(OnActivityResultListener listener, int code) {
		if (Utils.isNullOrEmpty(onActivityResultListener)) 
			onActivityResultListener = new ArrayList<MainActivity.OnActivityResultListener>();
		onActivityResultListener.add(listener);
	}
	
	public void removeActivityResultListener(OnActivityResultListener listener) {
		if (Utils.isNullOrEmpty(onActivityResultListener))
			return;
		onActivityResultListener.remove(listener);
	}

	@Override
	public int rootLayoutId() {
		return R.id.drawer_layout;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		isSearchView = false;

		App.resources = getResources();
		App.preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		App.context = getApplicationContext();

		getActionBar().setDisplayHomeAsUpEnabled(true); 
		drawer = new MenuDrawer(this, ui, getMenuItemList(), new DrawerMenuItemClickListener() {
			@Override
			public void onItemSelected(DrawerMenuItem item) {
				if (item.getId() == R.string.map) {
					showMap(true);
				} else {
					FragmentController mfragment = getFragmentByStringId(item.getId());
					addFragmentController(mfragment, R.id.content_frame, false);
				}
				drawer.close();
			}
		});

		// add the map fragment
		mapFragment = new MapFragment();
		App.mapListener = mapFragment;
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.map_frame, mapFragment, MAPFRAGMENT_TAG);
		ft.commit();

		IsLoggedInOperation op = new IsLoggedInOperation();
		op.execute(new String[] {});
	}

	protected FragmentController getFragmentByStringId(int id) {
		FragmentController mfragment = null;
		switch (id) {
		case R.string.events:
			mfragment = EventsFragment.newInstance("x/y");
			getActionBar().setIcon(R.drawable.ic_menu);
			getActionBar().setTitle(getResources().getString(R.string.events));
			break;
		case R.string.hotspot:
			mfragment = new CreateMapItemFragment();
			getActionBar().setTitle(getResources().getString(R.string.hotspot));
			break;
		case R.string.search_button:
			mfragment = new SearchMapitemFragment();
			getActionBar().setTitle(getResources().getString(R.string.search_button));
			break;
		case R.string.create_route:
			mfragment = new CreateRouteFragment();
			getActionBar().setTitle(getResources().getString(R.string.create_route));
			break;
		case R.string.user_profile:
			mfragment = new UserProfileFragment();
			getActionBar().setTitle(getResources().getString(R.string.user_profile));
			break;
		case R.string.highscores:
			mfragment = new HighscoresFragment();
			getActionBar().setTitle(getResources().getString(R.string.highscores));
			break;
		}
		return mfragment;
	}

	private List<DrawerMenuItem> getMenuItemList() {
		List<DrawerMenuItem> list = new ArrayList<DrawerMenuItem>();
		list.add(new DrawerMenuItem(0, R.string.map));
		list.add(new DrawerMenuItem(0, R.string.search_button));
		list.add(new DrawerMenuItem(0, R.string.create_route));
		list.add(new DrawerMenuItem(0, R.string.user_profile));
		list.add(new DrawerMenuItem(0, R.string.highscores));
		return list;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawer.onSyncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawer.onConfigurationChanged(newConfig);
	}

	@Override
	public void onBackPressed() {
		if (isLoginView) 
			return;

		if(getCurrentFragment() != null) { //check needed for tutorial
			Log.info(this, "removing fragment: " + getCurrentFragment().getTag());
		}
		if(isSearchView && getCurrentFragment() instanceof SearchMapitemFragment) {
			Log.info(this, "removing search");
			isSearchView = false;
			App.mapListener.setSearchForMapItems(false);
		}
		
		if (getFragmentBackStackEntryCount() < 2) {
			showMap(false);
			return;
		}
		
		addFragmentController(null, backStackFragmentContainerId, true, false, 0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (!drawer.onOptionsItemSelected(item)) {
			switch (item.getItemId()) {
			case R.id.action_logout:
				//logout
				LogoutOperation op = new LogoutOperation();
				op.execute(new String[]{});	        	
				return true;
			case R.id.action_search:
				//show and hide search
				if(isSearchView) {
					App.mapListener.setSearchForMapItems(false);
					isSearchView = false;
					this.onBackPressed();
					//Refresh Mapitems
					App.mapListener.searchForNewMapItems();
				} else {
					App.mapListener.setSearchForMapItems(true);
					isSearchView = true;
					addFragmentController(new SearchMapitemFragment(), R.id.content_frame);
				}
				return true;
			case R.id.action_tutorial:
				//show the tutorial again
				//start tutorial
				for (OnActivityResultListener listener : onActivityResultListener) {
					listener.onActivityResultFromActivity(SHOW_TUTORIAL_ACTION, -1, null);
					Log.debug(this, "Called listener to start tutorial");
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		} else {
			return true;
		}
	}

	public void showMap(boolean reset) {
		emptyFragmentBackStackUntil(0);
		getActionBar().setTitle(getResources().getString(R.string.app_name));
		if (reset)
			App.mapListener.reset();
	}

	/**
	 * Show login view
	 */
	private void showLoginView() {
		isLoginView = true;
		getActionBar().hide();

		loginFragmentController = new LoginFragment();
		addFragmentController(loginFragmentController , R.id.content_frame);
	}

	@Override
	public void onLoginSuccess() {
		isLoginView = false;

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (loginFragmentController != null)
			ft.remove(loginFragmentController);
		if (registerFragmentController != null)
			ft.remove(registerFragmentController);
		ft.commit();

		getActionBar().show();
		
		// start tutorial
		for (OnActivityResultListener listener : onActivityResultListener) {
			listener.onActivityResultFromActivity(SHOW_TUTORIAL_LOGIN, -1, null);
		}

		// addFragmentController(new MapFragment(), R.id.content_frame);
	}

	@Override
	public void onGoToRegistration() {
		isLoginView = true;
		getActionBar().hide();	
		registerFragmentController = new RegisterFragment();
		addFragmentController(registerFragmentController, R.id.content_frame);	
	}


	@Override
	public void onRegisterSuccess() {
		onLoginSuccess();	
	}

	private class IsLoggedInOperation extends AsyncTask<String, Void, Boolean>  {

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				if (UserAPI.getInstance().isLoggedIn()) {
					return true;
				} else {			
					//try auto login
					return UserAPI.getInstance().doAutoLogin();				
				}
			} catch (APIException e) {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				onLoginSuccess();
			} else {
				showLoginView();
			}
		}		
	}

	private class LogoutOperation extends AsyncTask<String, Void, String>  {

		@Override
		protected String doInBackground(String... params) {
			UserAPI.getInstance().doLogout();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			showLoginView();
		}		
	}

	public void addFragmentController(FragmentController mfragment) {
		addFragmentController(mfragment, R.id.content_frame, false);

	}

	public void addFragmentController(FragmentController mfragment, int numberOfItemsToKeepOnBackStack) {
		addFragmentController(mfragment, R.id.content_frame, false, numberOfItemsToKeepOnBackStack);		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != RESULT_CANCELED){
			if (!Utils.isNullOrEmpty(onActivityResultListener)) {
				Log.info(MainActivity.this, "Activity result for listners");
				for (OnActivityResultListener listener : onActivityResultListener) {
					listener.onActivityResultFromActivity(requestCode, resultCode, data);
				}
				return;
			}
		}
		else{
			Log.info(this, "Result was canceled or data were null");
			return;
		}
	}
	
	public MapFragment getMapFragment() {
		return mapFragment;
	}
	
	public void setCurrentMapItem(MapItem mi){
		currentMapItem = mi;
	}
	
	public MapItem getCurrentMapItem(){
		return currentMapItem;
	}
}
