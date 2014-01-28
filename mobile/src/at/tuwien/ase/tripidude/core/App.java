package at.tuwien.ase.tripidude.core;

import java.lang.reflect.Field;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.view.ViewConfiguration;
import at.tuwien.ase.tripidude.maputils.MapListener;
import at.tuwien.ase.tripidude.utils.Log;
import at.tuwien.ase.tripidude.utils.Log.LogSource;

/**
 * 
 * @author Matthias Schštta
 *
 */
public class App extends Application implements LogSource {

	public static App instance;
	
	public static Activity activity;
	public static MapListener mapListener;


	public static SharedPreferences preferences;
	public static Resources resources;

	public static Context context;

	public static PackageInfo packageInfo;
	public static ApplicationInfo applicationInfo;
	public static boolean isDebugable;
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;		
		context = getApplicationContext();
		resources = context.getResources();
		preferences = PreferenceManager.getDefaultSharedPreferences(context);

		try {
			applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), 0);
			isDebugable = (App.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE;

			Log.init(getPackageName(), isDebugable);
			Exceptions.init(getApplicationContext(), isDebugable, "");
			Exceptions.activityChanged(activity);
		} catch (NameNotFoundException e) {
	        Log.debug(this, "PackageName not found");
		}
		
		try {
	        ViewConfiguration config = ViewConfiguration.get(this);
	        Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	        if(menuKeyField != null) {
	            menuKeyField.setAccessible(true);
	            menuKeyField.setBoolean(config, false);
	        }
	    } catch (Exception ex) {
	        Log.debug(this, "could not force overflow button");
	    }
	}

	public static void setActivity(Activity baseActivity) {
		activity = baseActivity;
		Exceptions.activityChanged(activity);
	}

	@Override
	public String getLogSourceName() {
		return getClass().getSimpleName();
	}
}
