package at.tuwien.ase.tripidude.utils;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import at.tuwien.ase.tripidude.models.DrawerMenuItem;
import at.tuwien.ase.tripidude.utils.Log.LogSource;

public abstract class DrawerMenuItemClickListener implements ListView.OnItemClickListener, LogSource {
	@SuppressWarnings("rawtypes")
	@Override
	public void onItemClick(AdapterView parent, View view, int position,
			long id) {
		try {
			onItemSelected((DrawerMenuItem) parent.getAdapter().getItem(position));
		} catch (ClassCastException e) {
			Log.error(this, "Can only handle DrawerMenuItem-objects");
		}
	}
	public abstract void onItemSelected(DrawerMenuItem item);
	
	@Override
	public String getLogSourceName() {
		return getClass().getSimpleName();
	}
}