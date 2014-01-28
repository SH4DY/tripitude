package at.tuwien.ase.tripidude.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Utility functions
 */
public class Utils {

	private static final String LOG_SOURCE = "Utils";

	@SuppressLint("InlinedApi")
	public static final int SCREEN_ORIENTATION_SENSOR_PORTRAIT = Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD ? 7 : ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
	@SuppressLint("InlinedApi")
	public static final int SCREEN_ORIENTATION_SENSOR_LANDSCAPE = Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD ? 6 : ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
	@SuppressLint("InlinedApi")
	public static final int SCREEN_ORIENTATION_REVERSE_PORTRAIT = Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD ? 7 : ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
	@SuppressLint("InlinedApi")
	public static final int SCREEN_ORIENTATION_REVERSE_LANDSCAPE = Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD ? 6 : ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;

	/**
	 * Backward compatibility using Reflection
	 */
	private static Method getWidthMethod;
	private static Method getHeightMethod;
	static {
		initCompatibility();
	};
	private static void initCompatibility() {
		try {
			getWidthMethod = Display.class.getMethod("getRawWidth");
			getHeightMethod = Display.class.getMethod("getRawHeight");
			/* success, this is a newer device */
		} catch (NoSuchMethodException nsme) {
			/* failure, must be older device */
		}
		Log.debug(LOG_SOURCE, "initCompatibility done");
	}

	/**
	 * Represents the DeviceType, currently only phone and tablet are supported
	 */
	public static enum DeviceType {
		PHONE,
		TABLET;
	}

	/**
	 * Determines if the current device is a tablet or a handset.
	 *
	 * @return Either DeviceType.TABLET or DeviceType.PHONE
	 */
	@SuppressLint("InlinedApi")
	public static DeviceType getDeviceType(Context context) {
		// Verifies if the Generalized Size of the device is XLARGE to be
		// considered a Tablet
		boolean xlarge = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) && ((context.getResources().getConfiguration().screenLayout & 
				Configuration.SCREENLAYOUT_SIZE_MASK) == 
				Configuration.SCREENLAYOUT_SIZE_XLARGE);

		// If XLarge, checks if the Generalized Density is at least MDPI
		// (160dpi)
		if (xlarge) {
			DisplayMetrics metrics = new DisplayMetrics();
			Activity activity = (Activity) context;
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

			// MDPI=160, DEFAULT=160, DENSITY_HIGH=240, DENSITY_MEDIUM=160,
			// DENSITY_TV=213, DENSITY_XHIGH=320
			if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT
					|| metrics.densityDpi == DisplayMetrics.DENSITY_HIGH
					|| metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM
					|| metrics.densityDpi == DisplayMetrics.DENSITY_TV
					|| metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {

				// This is a tablet!
				return DeviceType.TABLET;
			}
		}
		// This is not a tablet!
		return DeviceType.PHONE;
	}

	/**
	 * Returns the screen densityDpi
	 *
	 * @return
	 */
	public static int getDisplayDensityDpi(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.densityDpi;
	}

	/**
	 * Returns the real screen height (the statusbar is ignored)
	 *
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static int getDisplayWidth(Activity activity) {
		if (getWidthMethod != null) {
			Display display = activity.getWindowManager().getDefaultDisplay();
			try {
				int width = (Integer) getWidthMethod.invoke(display);
				return width;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return 0;
		} else {
			return activity.getWindowManager().getDefaultDisplay().getWidth();
		}
	}

	/**
	 * Returns the real screen height (the statusbar is ignored)
	 *
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static int getDisplayHeight(Activity activity) {
		if (getHeightMethod != null) {
			Display display = activity.getWindowManager().getDefaultDisplay();
			try {
				int height = (Integer) getHeightMethod.invoke(display);
				return height;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return 0;
		} else {
			return activity.getWindowManager().getDefaultDisplay().getHeight();
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static void setBackground(View view, Drawable drawable) {
		if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			view.setBackgroundDrawable(drawable);
		} else {
			view.setBackground(drawable);
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static void removeOnGlobalLayoutListener(ViewTreeObserver observer, ViewTreeObserver.OnGlobalLayoutListener listener) {
		if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			observer.removeGlobalOnLayoutListener(listener);
		} else {
			observer.removeOnGlobalLayoutListener(listener);
		}
	}


	/**
	 * Returns the screen orientation
	 *
	 * @return
	 */
	public static int getScreenOrientation(Activity act) {
		int orientation = Configuration.ORIENTATION_UNDEFINED;
		if(getDisplayWidth(act) < getDisplayHeight(act)){
			orientation = Configuration.ORIENTATION_PORTRAIT;
		}else {
			orientation = Configuration.ORIENTATION_LANDSCAPE;
		}
		return orientation;
	}

	/**
	 * String related helper methods
	 */
	public static String oneOf(String... strings) {
		for (String string : strings)
			if (!isNullOrEmpty(string))
				return string;
		return null;
	}

	public static boolean isNullOrEmpty(CharSequence string) {
		return string == null || string.toString().trim().length() == 0;
	}

	public static boolean isNullOrEmpty(List<?> list) {
		return list == null || list.size() == 0;
	}


	public static boolean isNullOrEmpty(HashMap<?, ?> map) {
		return map == null || map.size() == 0;
	}

	public static boolean isNullOrEmpty(Object[] array) {
		return array == null || array.length == 0;
	}

	public static boolean isNullOrEmpty(Set<?> set) {
		return set == null || set.size() == 0;
	}

	public static boolean isNullOrEmpty(int integer) {
		return integer == 0;
	}

	/**
	 * Bytes conversion
	 */
	@SuppressLint("DefaultLocale")
	public static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit) return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public static int getHexColorOutOfRGBAString(String bgColor) {
		int end = bgColor.indexOf(";");
		float red = Float.valueOf(bgColor.substring(0, end));
		bgColor = bgColor.substring(end+1, bgColor.length());
		end = bgColor.indexOf(";");
		float green = Float.valueOf(bgColor.substring(0, end));
		bgColor = bgColor.substring(end+1, bgColor.length());
		end = bgColor.indexOf(";");
		float blue = Float.valueOf(bgColor.substring(0, end));
		bgColor = bgColor.substring(end+1, bgColor.length());
		float alpha = Float.valueOf(bgColor);

		return Color.argb((int) (alpha*255), (int)red, (int)green, (int)blue);
	}

	/**
	 * returns the given view as a bitmap
	 * @param view
	 * @param viewWidth
	 * @param viewHeight
	 * @return
	 */
	public static Bitmap getBitmapFromView(View view) {
		Log.debug(LOG_SOURCE, "getBitmapFromView: "+view.getWidth()+" x "+view.getHeight());
		if (view.getWidth() == 0 || view.getHeight() == 0)
			return null;
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		view.layout(0, 0, view.getWidth(), view.getHeight());
		view.draw(canvas);
		Log.debug(LOG_SOURCE, "getBitmapFromView: "+bitmap.getWidth()+" x "+bitmap.getHeight());
		canvas = null;
		return bitmap;
	}

	public static Bitmap getBitmapFromSDCard(String path) {
		return convertToMutable(BitmapFactory.decodeFile(path));
	}

	public static int getOrientation(Activity act) {
		Display display = act.getWindowManager().getDefaultDisplay();
		return Utils.getScreenOrientation(act, display);
	}

	@SuppressWarnings("deprecation")
	public static int getScreenOrientation(Activity act, Display display){
		int orientation;
		if(getDisplayWidth(act)==getDisplayHeight(act)){
			orientation = Configuration.ORIENTATION_SQUARE;
		}else{ //if width is less than height than it is portrait
			if(getDisplayWidth(act) < getDisplayHeight(act)){
				orientation = Configuration.ORIENTATION_PORTRAIT;
			}else{ // if it is not any of the above it will definitely be landscape
				orientation = Configuration.ORIENTATION_LANDSCAPE;
			}
		}   
		return orientation;
	}

	public static void requestOrientation(Activity activity, int orientation) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
			if (orientation == Utils.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
				orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			if (orientation == Utils.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
				orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
		}
		activity.setRequestedOrientation(orientation);
	}

	/**
	 * Converts a immutable bitmap to a mutable bitmap. This operation doesn't allocates
	 * more memory that there is already allocated.
	 * 
	 * @param imgIn - Source image. It will be released, and should not be used more
	 * @return a copy of imgIn, but muttable.
	 */
	public static Bitmap convertToMutable(Bitmap imgIn) {
		try {
			if (imgIn == null)
				return null;
			//this is the file going to use temporally to save the bytes. 
			// This file will not be a image, it will store the raw image data.
			File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

			//Open an RandomAccessFile
			//Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
			//into AndroidManifest.xml file
			RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

			// get the width and height of the source bitmap.
			int width = imgIn.getWidth();
			int height = imgIn.getHeight();
			Config type = imgIn.getConfig();

			//Copy the byte to the file
			//Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
			FileChannel channel = randomAccessFile.getChannel();
			MappedByteBuffer map = channel.map(MapMode.READ_WRITE, 0, imgIn.getRowBytes()*height);
			imgIn.copyPixelsToBuffer(map);
			//recycle the source bitmap, this will be no longer used.
			imgIn.recycle();
			System.gc();// try to force the bytes from the imgIn to be released

			//Create a new bitmap to load the bitmap again. Probably the memory will be available. 
			imgIn = Bitmap.createBitmap(width, height, type);
			map.position(0);
			//load it back from temporary 
			imgIn.copyPixelsFromBuffer(map);
			//close the temporary file and channel , then delete that also
			channel.close();
			randomAccessFile.close();

			// delete the temp file
			file.delete();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 

		return imgIn;
	}

	public static int getDip(Context context, int pixel) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pixel * scale + 0.5f);
	}
	
	public static double[] getPrimitive(Double[] boundingCircle) {
		double[] tempArray = new double[boundingCircle.length];
		for (int i = 0; i < boundingCircle.length; i++) {
			tempArray[i] = (double) boundingCircle[i];
		}
		return tempArray;
	}
}
