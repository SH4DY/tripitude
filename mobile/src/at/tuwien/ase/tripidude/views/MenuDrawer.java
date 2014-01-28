package at.tuwien.ase.tripidude.views;

import java.util.List;

import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import at.tuwien.ase.tripidude.MainActivity;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.models.DrawerMenuItem;
import at.tuwien.ase.tripidude.utils.DrawerMenuItemClickListener;
import at.tuwien.ase.tripidude.utils.Log;
import at.tuwien.ase.tripidude.utils.Log.LogSource;
import at.tuwien.ase.tripidude.utils.ModelsAdapter;
import at.tuwien.ase.tripidude.utils.UiQuery;

public class MenuDrawer implements LogSource {

	private MainActivity activity;
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mMenuDrawerLayout;
	private ListView mMenuDrawerList;

	public MenuDrawer(MainActivity activity, UiQuery ui, List<DrawerMenuItem> items, DrawerMenuItemClickListener drawerItemClickListener) {
		this.activity = activity;
		mMenuDrawerLayout = (DrawerLayout) ui.id(R.id.drawer_layout).getView();
		mDrawerToggle = new ActionBarDrawerToggle(activity, mMenuDrawerLayout,
				R.drawable.ic_drawer, 0, 0) {
			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {}
		};

		mMenuDrawerList = ui.id(R.id.menu_drawer).getListView();
		// Set the adapter for the list view
		mMenuDrawerList.setAdapter(getDrawerAdapter(items));
		// Set the list's click listener
		mMenuDrawerList.setOnItemClickListener(drawerItemClickListener);

		// Set the drawer toggle as the DrawerListener
		mMenuDrawerLayout.setDrawerListener(mDrawerToggle);

		// disable when swiping
		// disable the drawer-button (logo)
		mDrawerToggle.setDrawerIndicatorEnabled(true);
	}

	private ModelsAdapter<DrawerMenuItem> getDrawerAdapter(List<DrawerMenuItem> list) {
		return new ModelsAdapter<DrawerMenuItem>(activity, R.layout.list_item_drawer, list) {
			@Override
			protected void fillView(int position, DrawerMenuItem model, View view, UiHolder ui) {
				Log.info(MenuDrawer.this, "fillView " + model.getTitle());
				ui.id(R.id.text).text(model.getTitle());
			}
		};
	}

	public void onSyncState() {
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	public void onConfigurationChanged(Configuration newConfig) {
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void close() {
		mMenuDrawerLayout.closeDrawer(mMenuDrawerList);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		return mDrawerToggle.onOptionsItemSelected(item);
	}

	public boolean isOpen() {
		return mMenuDrawerLayout.isDrawerOpen(mMenuDrawerList);
	}
	
	public ActionBarDrawerToggle getMDrawerToggle() {
		return mDrawerToggle;
	}

	@Override
	public String getLogSourceName() {
		return getClass().getSimpleName();
	}
}
