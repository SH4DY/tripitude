package at.tuwien.ase.tripidude.core;


import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.utils.Log;
import at.tuwien.ase.tripidude.utils.Log.LogSource;
import at.tuwien.ase.tripidude.utils.UiQuery;
import at.tuwien.ase.tripidude.utils.Utils;

public abstract class FragmentController extends Fragment implements LogSource {
	private AsyncTask asyncTask;
	private ProgressDialog dialog;

	//protected Progress progress;
	protected UiQuery ui;

	// loading
	private Bitmap loadingBackground;
	protected boolean useLoadingImages = true;
	private Animation loadingAnimation; 
	private Animation loadingAnimationForBack;
	private Animation loadingAnimationForHome;
	private Animation loadingAnimationForReload; 
	private Animation loadingOutAnimationForReload;
	private Animation loadingOutAnimation; 
	private Animation loadingOutAnimationForBack;
	private Animation loadingOutAnimationForHome;
	private Direction currentDirection = Direction.Forward;
	public enum Direction {
		Forward, Back, Home, Reload
	}

	public void setCurrentDirection(Direction direction) {
		currentDirection = direction;
	}

	protected Animation getLoadingInAnimation() {
		switch(currentDirection) {
		case Forward:
			return loadingAnimation;
		case Back: 
			return loadingAnimationForBack != null ? loadingAnimationForBack : loadingAnimation;
		case Home: 
			return loadingAnimationForHome != null ? loadingAnimationForHome : loadingAnimation;
		case Reload: 
			return loadingAnimationForReload != null ? loadingAnimationForReload : loadingAnimation;
		default:
			return loadingAnimation;
		}
	}

	protected Animation getLoadingOutAnimation() {
		switch(currentDirection) {
		case Forward:
			return loadingOutAnimation;
		case Back: 
			return loadingOutAnimationForBack != null ? loadingOutAnimationForBack : loadingOutAnimation;
		case Home: 
			return loadingOutAnimationForHome != null ? loadingOutAnimationForHome : loadingOutAnimation;
		case Reload: 
			return loadingOutAnimationForReload != null ? loadingOutAnimationForReload : loadingAnimation;
		default:
			return loadingOutAnimation;
		}
	}

	protected void setLoadingAnimation(Animation loadingInAnimation, Animation loadingOutAnimation) {
		this.loadingAnimation = loadingInAnimation;
		this.loadingOutAnimation = loadingOutAnimation;
	}

	protected void setLoadingAnimationForBack(Animation loadingAnimationBack, Animation loadingOutAnimationBack) {
		this.loadingAnimationForBack = loadingAnimationBack;
		this.loadingOutAnimationForBack = loadingOutAnimationBack;
	}

	protected void setLoadingAnimationForHome(Animation loadingAnimationHome, Animation loadingOutAnimationHome) {
		this.loadingAnimationForHome = loadingAnimationHome;
		this.loadingOutAnimationForHome = loadingOutAnimationHome;
	}

	protected void setLoadingAnimationForReload(Animation loadingAnimationReload, Animation loadingOutAnimationReload) {
		this.loadingAnimationForReload = loadingAnimationReload;
		this.loadingOutAnimationForReload = loadingOutAnimationReload;
	}
	
	public void setUseLoadingBackground(boolean useLoadingBackground) {
		useLoadingImages = useLoadingBackground;
	}

	protected void setLoadingBackground(Bitmap loadingBackground) {
		if (useLoadingImages)
			this.loadingBackground = loadingBackground;
	}

	protected Bitmap getLoadingBackground() {
		return loadingBackground;
	}

	//	private boolean isQuerying;
	private boolean isQueried;
	private boolean isShowing;

	public boolean isReady = false;

	private boolean isMainFragment;

	/**
	 * LifeCycle:
	 *
	 * --> Fragment is added <--
	 * onAttach()
	 * onCreate()
	 * onCreateView() <-------------------------------------------------to
	 * 		onDoCreateViewWithId() ABSTRACT	--> return layout.id		|
	 *			(ui set) 												|
	 *																	|
	 *																	|
	 * onActivityCreated() 												|
	 * 		onCreate() ABSTRACT											|
	 * 		onActivate();												|
	 * 		if (!isQueried) 											|
	 * 			if (onPreQuery())										|
	 * 				onQuery() ABSTRACT --> ASYNC						|
	 * 		onShow(); ABSTRACT											|
	 *																	|
	 *																	|
	 * onStart()														|
	 * onResume()														|The fragment returns
	 * --> Fragment is active <--										|to the layout
	 * --> Fragment is added to the backstack <--						|from the back stack
	 * onPause()														|
	 * onStop()															|
	 * onDestroyView() ->-----------------------------------------------from
	 * onDestroy()
	 * onDetach()
	 * --> Fragment is destroyed <--
	 *
	 */

	public FragmentController() {
		Log.verbose(this, "create (default)");
	}

	@Override
	public void onAttach(android.app.Activity activity) {
		Log.verbose(this, "attach");

		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.verbose(this, "create");
		setLoadingAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in), AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out));
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.verbose(this, "createView");
		// View
		View view;
		int viewId = onDoCreateViewWithId();
		if (viewId != 0) {
			view = inflater.inflate(viewId, container, false);
			// initialize nice ui query object.
			ui = new UiQuery(view);
		} else {
			view = super.onCreateView(inflater, container, savedInstanceState);
		}
		// Background
		if (getLoadingBackground() != null && getLoadingView() != null) {
			Utils.setBackground(getLoadingView(), new BitmapDrawable(getActivity().getResources(), getLoadingBackground()));
		}
		return view;
	}

	/**
	 * must return 0 or an LayoutId
	 */
	protected abstract int onDoCreateViewWithId();

	/**
	 * is called by main-activity when this fragment is the mainfragment on the current page
	 */
	public void onGainsFocus() {}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.verbose(this, "activityCreated");

		// Log
		android.util.Log.v(this.toString(), "activityCreated");
		// set Controller activity
		ui.setActivity((Activity)getActivity());

		// Controller Create
		doCreate();

		// Controller Show
		show();

		super.onActivityCreated(savedInstanceState);
	}


	@Override
	public void onStart() {
		Log.verbose(this, "start");

		super.onStart();
	}


	@Override
	public void onResume() {
		Log.verbose(this, "resume");

		super.onResume();
	}


	@Override
	public void onHiddenChanged(boolean hidden) {
		if (hidden) {
			Log.verbose(this, "hide");
			doDeactivate();
		}

		super.onHiddenChanged(hidden);
	}


	@Override
	public void onPause() {
		Log.verbose(this, "pause");
		super.onPause();
	}


	@Override
	public void onStop() {
		Log.verbose(this, "stop");

		super.onStop();
	}


	@Override
	public void onDestroyView() {
		Log.verbose(this, "destroyView");
		if (loadingBackground != null) {
			loadingBackground.recycle();
			loadingBackground = null;
		}
		if (ui != null)
			ui.cleanUp();
		super.onDestroyView();
	}


	@Override
	public void onDestroy() {
		Log.verbose(this, "destroy");
		if (asyncTask != null) { 
			asyncTask.cancel(true);
			asyncTask = null;
		}
		dialog = null;
		if (ui != null)
			ui.cleanUp();
		ui = null;
		super.onDestroy();
	}


	@Override
	public void onDetach() {
		Log.verbose(this, "detach");

		super.onDetach();
	}


	@Override
	protected void finalize() throws Throwable {
		Log.verbose(this, "finalize");

		super.finalize();
	}


	public void forceRequery() {
		isQueried = false;
	}


	/**********************************************
	 *  Create
	 **********************************************/

	public void doCreate() {
		Log.verbose(this, "create");

		isQueried = false;
		isShowing = false;
		isReady = false;

		try {
			onCreate();
		} catch (Exception e) {
			Exceptions.handle(e);
		}
	}


	protected abstract void onCreate() throws Exception;


	/**********************************************
	 *  PreQuery
	 **********************************************/

	protected boolean isQueryNeeded() {
		return !isQueried;
	}


	private boolean doPreQuery() {
		try {
			return onPreQuery();
		} catch (Exception e) {
			doQueryFailure(e);
			return false;
		}
	}


	protected boolean onPreQuery() throws Exception {
		return true;
	}


	/**********************************************
	 *  Query
	 **********************************************/

	private void doQuery() throws Exception {
		Log.verbose(this, "query");
		onQuery();
	}


	protected abstract void onQuery() throws Exception;


	/**********************************************
	 *  QuerySuccess
	 **********************************************/

	private void doQuerySuccess() {
		// notify
		isQueried = true;


		// Dialog
		closeProgressDialog();

		// Success
		try {
			onQuerySuccess();

		} catch (Exception e) {
			Exceptions.handle(e);
		}
	}

	protected void onQuerySuccess() throws Exception {
	}


	/**********************************************
	 *  QueryFailure
	 **********************************************/

	private void doQueryFailure(Exception e) {
		isShowing = false;

		// Dialog
		closeProgressDialog();

		// Handle
		try {
			onQueryFailure(e);
		} catch (Exception e1) {
			Exceptions.handle(e1);
			e1.printStackTrace();
		}

	}


	protected void onQueryFailure(Exception e) throws Exception {
	}


	/**********************************************
	 *  Show
	 **********************************************/

	private static boolean isUiThread() {
		return Thread.currentThread() == Looper.getMainLooper().getThread();
	}


	@SuppressWarnings("unchecked")
	public void show() {
		Log.verbose(this, isShowing ? "isShowing" : "!isShowing");

		if (isShowing) {
			return;
		}

		// if not on UiThread ...
		if (!isUiThread()) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					show();
				}
			});
			return;
		}

		// notify
		isShowing = true;

		// Activate
		doActivate();

		// query if needed
		Log.verbose(this, isShowing ? "isQueryNeeded()" : "!isQueryNeeded()");
		if (isQueryNeeded()) {
			// cancelled?
			if (doPreQuery()) {
				// run thread
				asyncTask = new AsyncTask();
				asyncTask.show = true;
				asyncTask.execute((Object[]) null);
				return;
			}
		}

		// show
		doShow();
	}


	private void doShow() {
		Log.verbose(this, "show");

		// Show
		try {
			onShow();
			hideLoading();
		} catch (Exception e) {
			Exceptions.handle(e);
		}
		isReady = true;

		// notify
		isShowing = false;
	}


	protected abstract void onShow() throws Exception;


	/**********************************************
	 *  Activate
	 **********************************************/

	public void activate() {
		doActivate();
	}


	private void doActivate() {
		Log.verbose(this, "activate");

		// Activate
		try {
			onActivate();
		} catch (Exception e) {
			Exceptions.handle(e);
		}
	}


	protected void onActivate() throws Exception {

	}


	/**********************************************
	 *  Deactivate
	 **********************************************/

	public void deactivate() {
		doDeactivate();
	}

	private void doDeactivate() {
		// Log
		Log.verbose(this, "deactivate");
		// Deactivate
		try {
			onDeactivate();
		} catch (Exception e) {
			Exceptions.handle(e);
		}
	}


	protected void onDeactivate() throws Exception {

	}


	/**********************************************
	 *  Progress Dialog
	 **********************************************/

	protected void showProgressDialog() {
		if (dialog != null)
			return;
		asyncTask.showProgressDialog();
	}


	private void closeProgressDialog() {
		if (dialog == null)
			return;
		try {
			dialog.dismiss();
			dialog = null;
		} catch (Exception e) {
		}
	}

	/**********************************************
	 *  AsyncTask
	 **********************************************/

	@SuppressWarnings("rawtypes")
	private class AsyncTask extends android.os.AsyncTask {
		private boolean show;

		@SuppressWarnings("unchecked")
		private void showProgressDialog() {
			publishProgress();
		}


		@SuppressWarnings("unchecked")
		@Override
		protected void onProgressUpdate(Object... values) {
			super.onProgressUpdate(values);
			FragmentController.this.dialog = ProgressDialog.show(getActivity(), "", "Loading ...", false, false);
		}

		@Override
		protected Object doInBackground(Object... params) {
			try {
				FragmentController.this.doQuery();
				return null;
			} catch (Exception e) {
				return e;
			}
		}


		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			// Failure
			if (result != null) {
				FragmentController.this.doQueryFailure((Exception) result);
				return;
			}
			// Success
			FragmentController.this.doQuerySuccess();
			// AsyncTask
			if (FragmentController.this.asyncTask != null)
				FragmentController.this.asyncTask = null;
			// Show
			if (show)
				FragmentController.this.doShow();
		}
	}

	/**********************************************
	 *  custom methods
	 **********************************************/

	protected boolean isEmpty(String string) {
		return string.trim().equals("");
	}

	public boolean isMainFragment() {
		return isMainFragment;
	}

	public void setMainFragment(boolean isMainFragment) {
		this.isMainFragment = isMainFragment;
	}

	/**********************************************
	 *  Log
	 **********************************************/

	@Override
	public String getLogSourceName() {
		return getClass().getSimpleName();
	}

	public void reload() {
		forceRequery();
		View loading = getLoadingView();
		if (loading != null)
			loading.setVisibility(View.VISIBLE);
		show();
	}

	/**
	 * @category loading
	 */
	private void hideLoading() {
		if (getLoadingView() == null || getRealView() == null)
			return;

		if (getLoadingOutAnimation() == null){
			getLoadingView().setVisibility(View.GONE);
			getRealView().setVisibility(View.VISIBLE);
		}

		else {
			getRealView().setVisibility(View.VISIBLE);
			getLoadingView().startAnimation(getLoadingOutAnimation());
			getLoadingView().setVisibility(View.GONE);
		}

	}

	protected abstract View getLoadingView();
	protected abstract View getRealView();
}
