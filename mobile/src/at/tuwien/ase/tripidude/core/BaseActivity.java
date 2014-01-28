package at.tuwien.ase.tripidude.core;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.inputmethod.InputMethodManager;
import at.tuwien.ase.tripidude.core.FragmentController.Direction;
import at.tuwien.ase.tripidude.utils.Log;
import at.tuwien.ase.tripidude.utils.Log.LogSource;
import at.tuwien.ase.tripidude.utils.UiQuery;
import at.tuwien.ase.tripidude.utils.Utils;

import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;

public abstract class BaseActivity extends FragmentActivity implements LogSource {
	public static UiQuery ui;
	private ArrayList<String> fragmentBackStack;
	private ArrayList<Integer> ignoredBackStackEntries;
	private int fragmentId;
	private List<WeakReference<Fragment>> mFragments = new ArrayList<WeakReference<Fragment>>();
	public boolean isActive;
	public int backStackFragmentContainerId;
	
	@Override
	public void onLowMemory() {
		// clear all memory cached images when system is in low memory
		// note that you can configure the max image cache count, see
		// CONFIGURATION
		BitmapAjaxCallback.clearCache();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Log
		Log.verbose(this, "create");

		// UI
		ui = new UiQuery(this);
		fragmentBackStack = new ArrayList<String>();
		ignoredBackStackEntries = new ArrayList<Integer>();
		isActive = true;
		App.setActivity(this);
	} 
	
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		// UI
		ui = new UiQuery(findViewById(rootLayoutId()));
	}
	
	public abstract int rootLayoutId(); 
	
	@Override
	public void onAttachFragment(Fragment fragment) {
	    mFragments.add(new WeakReference<Fragment>(fragment));
	}

	@Override
	protected void onDestroy() {
		Log.verbose(this, "destroy");
		ui.cleanUp();
		// clean the file cache with advance option
		long triggerSize = 5000000; // starts cleaning when cache size is larger
		// than 5M
		long targetSize = 2000000; // remove the least recently used files until
		// cache size is less than 2M
		AQUtility.cleanCacheAsync(this, triggerSize, targetSize);		
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		isActive = true;
		super.onStart();
		Log.verbose(this, "start");
	}

	@Override
	protected void onStop() {
		isActive = false;
		Log.verbose(this, "stop");
		super.onStop();
	}

	@Override
	protected void onResume() {
		Log.verbose(this, "resume");
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.verbose(this, "pause");
		super.onPause();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.verbose(this, "onSaveInstanceState");
	}

	@Override
	public String getLogSourceName() {
		return this.getClass().getSimpleName();
	}

	/*
	 * FRAGMENT HANDLING
	 */	

	private FragmentController getLastBackStackEntryFragment() {
		return (FragmentController) getSupportFragmentManager()
				.findFragmentByTag(getFragmentBackStackEntry());
	}

	public void addFragmentController(FragmentController mfragment, final int fragmentContainerId, boolean addToBackStack, final boolean emptyBackStack, final int numberOfItemsToKeepOnBackStack) {
		backStackFragmentContainerId = fragmentContainerId; 
		final boolean finaladdToBackStack = addToBackStack;
		final FragmentController finalFragment = mfragment;
		if (!isUiThread()) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					addFragmentController(finalFragment, fragmentContainerId, finaladdToBackStack, emptyBackStack, numberOfItemsToKeepOnBackStack);
				}
			});
			return;
		}
		if (!isActive)
			return;
		
		// hide the keyboard
		hideTheKeyboard();
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		FragmentController lastFragment = getCurrentFragment();
        
		if (emptyBackStack) {
			if (lastFragment != null && mfragment != null) {
				setFragmentLoadingBackground(mfragment, lastFragment);
				mfragment.setCurrentDirection(Direction.Home);
			}
			emptyFragmentBackStack();
		} else if (mfragment != null && lastFragment != null) {
			setFragmentLoadingBackground(mfragment, lastFragment);
			mfragment.setCurrentDirection(Direction.Forward);
		}

		// hide the currently visible fragment
		Fragment fragmentToHide = getCurrentFragment();
		String backStackName;
		// pop backstack
		if (mfragment == null) {
			popFragmentBackStack();
			mfragment = getCurrentFragment();
			if(mfragment == null) {
				onBackPressed();
				return;
			}
			mfragment.setCurrentDirection(Direction.Back);
			if (lastFragment != null) {
				setFragmentLoadingBackground(mfragment, lastFragment);
			}
			
			backStackName = mfragment.getTag();
			if (fragmentToHide != null)
				ft.remove(fragmentToHide);
		} else {
			if (mfragment.getTag() == null)
				backStackName = mfragment.getClass().getName() + (fragmentId++);
			else
				backStackName = mfragment.getTag();

			int backStackposition = pushTabBackStack(backStackName);
			if (!addToBackStack) {
				mfragment.setCurrentDirection(Direction.Reload);
				ignoredBackStackEntries.add(backStackposition);
			}
		}
		
		// hide the currently visible fragment
		if (fragmentToHide != null) {
			ft.detach(fragmentToHide);
		}

		// add the new fragment
		ft.add(fragmentContainerId, mfragment, backStackName);
		ft.attach(mfragment);
		ft.show(mfragment);
		ft.commit();
	}

	public void hideTheKeyboard() {
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (mgr != null && getWindow().getCurrentFocus() != null)
			mgr.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
	}

	private void setFragmentLoadingBackground(FragmentController mfragment, FragmentController currentFragment) {
		if (currentFragment == null || currentFragment.ui == null || currentFragment.ui.getRootView() == null || !mfragment.useLoadingImages)
			return;
		mfragment.setLoadingBackground(Utils.getBitmapFromView(currentFragment.ui.getRootView()));
	}

	@SuppressWarnings("unchecked")
	public <T extends FragmentController> T getFragmentController(
			Class<T> controllerClass) {
		return (T) getSupportFragmentManager().findFragmentByTag(
				controllerClass.getName());
	}

	public FragmentController getFragmentController(String tag) {
		return (FragmentController) getSupportFragmentManager()
				.findFragmentByTag(tag);
	}
	
	public void addFragmentController(FragmentController mfragment, int fragmentContainerId) {
		addFragmentController(mfragment, fragmentContainerId, false);
	}

	public void addFragmentController(FragmentController mfragment, int fragmentContainerId, boolean emptyBackStack) {
		addFragmentController(mfragment, fragmentContainerId, true, emptyBackStack, 0);
	}
	
	public void addFragmentController(FragmentController mfragment, int fragmentContainerId, boolean emptyBackStack, int numberOfItemsToKeepOnBackStack) {
		addFragmentController(mfragment, fragmentContainerId, true, emptyBackStack, numberOfItemsToKeepOnBackStack);
	}	

	public FragmentController getCurrentFragment() {
		// hide the visible fragment
		if (getFragmentBackStackEntryCount() > 0)
			return (FragmentController) getLastBackStackEntryFragment();
		return null;
	}

	protected void activateCurrentFragment() {
		if (getCurrentFragment() != null)
			getCurrentFragment().activate();
	}

	@Override
	public void onBackPressed() {
		Log.info(this, "onBackPressed()");
		if (getFragmentBackStackEntryCount() < 2) {
			super.onBackPressed();
			return;
		}
		addFragmentController(null, backStackFragmentContainerId, true, false, 0);
	}

	public void popFragmentBackStack() {
		if (fragmentBackStack.size() == 0)
			return;
		int lastEntry = fragmentBackStack.size() - 1;		
		if (ignoredBackStackEntries.contains(lastEntry)) {
			fragmentBackStack.remove(lastEntry);
			ignoredBackStackEntries.remove(Integer.valueOf(lastEntry));
			popFragmentBackStack();
		} else {
			fragmentBackStack.remove(lastEntry);
		}
	}

	private String getFragmentBackStackEntry() {
		return fragmentBackStack.size() != 0 ? fragmentBackStack
				.get(fragmentBackStack.size() - 1) : null;
	}

	protected int getFragmentBackStackEntryCount() {
		return fragmentBackStack.size();
	}

	private int pushTabBackStack(String name) {
		fragmentBackStack.add(name);
		return fragmentBackStack.size()-1;
	}

	/**
	 * clear the backstack until e.g. "home"
	 * @param entryNumber
	 */
	protected void emptyFragmentBackStackUntil(int entryNumber) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		while (fragmentBackStack.size() > entryNumber) {
			Fragment f = getCurrentFragment();
			if (f != null)
				ft.hide(f);
			popFragmentBackStack();
		}
		ft.commit();
	}
	
	protected void emptyFragmentBackStack() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		while (fragmentBackStack.size() > 0) {
			Fragment f = getCurrentFragment();
			if (f != null)
				ft.hide(f);
			popFragmentBackStack();
		}
		ft.commit();
		recycleFragments();
	}

	protected boolean hasBackStackEntries() {
		return getFragmentBackStackEntryCount() > 1;
	}

	public void reloadCurrentFragment() {
		addFragmentController(getCurrentFragment(), backStackFragmentContainerId, true, false, 0);
		popFragmentBackStack();
	}
	
	private static boolean isUiThread() {
		return Thread.currentThread() == Looper.getMainLooper().getThread();
	}
	
	private void recycleFragments() {
	    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

	    for (WeakReference<Fragment> ref : mFragments) {
	        Fragment fragment = ref.get();
	        if (fragment != null) {
	            ft.remove(fragment);
	        }
	    }

	    ft.commit();
	}
}
