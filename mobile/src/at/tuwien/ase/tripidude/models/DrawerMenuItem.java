package at.tuwien.ase.tripidude.models;

import at.tuwien.ase.tripidude.core.App;

public class DrawerMenuItem {

	private int icon;
	private int resourceId;

	public DrawerMenuItem(int iconResource, int titleResource) {
		this.icon = iconResource;
		this.resourceId = titleResource;
	}

	/**
	 * resourceId is used to identify this item
	 * @return id
	 */
	public int getId() {
		return resourceId;
	}

	/**
	 * if there is no title passed,
	 * resourceId is supposed to be an id from a valid string-resource
	 * @return title of the item
	 */
	public String getTitle() {
		return App.resources.getString(resourceId);
	}

	public int getIcon() {
		return icon;
	}

}
