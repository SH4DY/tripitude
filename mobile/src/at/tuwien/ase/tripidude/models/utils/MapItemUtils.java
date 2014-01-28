package at.tuwien.ase.tripidude.models.utils;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import at.tuwien.ase.tripidude.core.App;
import at.tuwien.ase.tripidude.models.Hotspot;
import at.tuwien.ase.tripidude.models.MapItem;
import at.tuwien.ase.tripidude.models.MapItemCategory;

public class MapItemUtils {
	
	public static final String MAPITEM_TYPE_ROUTE = "ac.tuwien.ase08.tripitude.entity.Route";
	public static final String MAPITEM_TYPE_HOTSPOT = "ac.tuwien.ase08.tripitude.entity.Hotspot";
	
	public static String getStringListForMapItemCategoryList(List<MapItemCategory> list) {
		List<String> categories =  new ArrayList<String>();
		
		for (MapItemCategory category : list) {
			
			//retrieve string by name
			String cat = App.resources.getString(App.resources.getIdentifier(category.getName(), "string", App.context.getPackageName()));
			categories.add(cat);
		}
		return TextUtils.join(", ", categories);
	}
	
	public static Hotspot createHotspot(MapItem item) {
		if (item instanceof Hotspot) {
			return (Hotspot) item;
		} else {
			if (item.getType().equals(MAPITEM_TYPE_HOTSPOT))
				return new Hotspot(item);
			else
				return new Hotspot();
		}
	}
}
