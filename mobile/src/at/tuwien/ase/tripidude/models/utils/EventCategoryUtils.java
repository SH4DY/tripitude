package at.tuwien.ase.tripidude.models.utils;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import at.tuwien.ase.tripidude.core.App;
import at.tuwien.ase.tripidude.models.EventCategory;

public class EventCategoryUtils {
	
	public static String getStringListForEventCategoryList(List<EventCategory> list) {
		List<String> categories =  new ArrayList<String>();
		
		for (EventCategory category : list) {
			//retrieve string by name
			String cat = App.resources.getString(App.resources.getIdentifier(category.getName(), "string", App.context.getPackageName()));
			categories.add(cat);
		}
		return TextUtils.join(", ", categories);
	}
}