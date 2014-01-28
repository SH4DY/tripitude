package at.tuwien.ase.tripidude.core;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.http.conn.ConnectTimeoutException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import at.tuwien.ase.tripidude.R;
import at.tuwien.ase.tripidude.utils.Log;
import at.tuwien.ase.tripidude.utils.Utils;

import com.androidquery.util.AQUtility;

/**
 * 
 * @author Matthias Schštta
 *
 */
public abstract class Exceptions {
	
	private static boolean isDebugable;
	private static Activity activity;

	public static class Handler implements Runnable {
		private Throwable e;
		private DialogInterface.OnDismissListener callback;

		public Handler(Throwable e, DialogInterface.OnDismissListener callback) {
			this.e = e;
			this.callback = callback;
		}

		@Override
		public void run() {
			if (e instanceof ConnectTimeoutException || e instanceof SocketTimeoutException || e instanceof ConnectException || e instanceof SocketException || e instanceof UnknownHostException) {
				showException(e, activity.getString(R.string.error), activity.getString(R.string.error_no_internet), callback);
				Log.warn("ERROR", "Connection ERROR!");
			} else {
				String msg = activity.getString(R.string.error_unknown);
				if (!Utils.isNullOrEmpty(e.getMessage()))
					msg = e.getMessage();
				showException(e, activity.getString(R.string.error), msg, callback);
				e.printStackTrace();				
				reportException(e);
			}
		}
	}

	public static void init(Context context, boolean isDebugable, String bugSenseKey) {
		Exceptions.isDebugable = isDebugable;
		AQUtility.setDebug(isDebugable);
	}
	
	public static void activityChanged(Activity activity) {
		Exceptions.activity = activity;
	}

	public static void handle(Throwable e, DialogInterface.OnDismissListener callback) {
		Log.exception(e);
		Handler handler = new Handler(e,callback);
		if (Exceptions.isInMainThread())
			handler.run();
		else if (Exceptions.activity != null)
			Exceptions.activity.runOnUiThread(handler);
	}

	public static void handle(Throwable e) {
		handle(e, null);
	}

	public static void reportException(Throwable e) {
		if (Exceptions.isDebugable)
			return;
		AQUtility.report(e);
	}

	public static void showException(final Throwable e, String caption, String message, DialogInterface.OnDismissListener callback) {
		if (e.getCause() != null)
			Log.exception(e.getCause());
		if (caption == null)
			caption = "Fehler";
		if (Exceptions.activity == null)
			return;
		AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(Exceptions.activity);
		dlgBuilder.setTitle(caption);
		dlgBuilder.setMessage(message);
		dlgBuilder.setCancelable(true);
		dlgBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog dlg = dlgBuilder.create();
		dlg.setOnDismissListener(callback);
		dlg.show();
	}

	public static void showException(final Throwable e, DialogInterface.OnDismissListener callback) {
		String caption;
		String message;
		try {
			message = e.toString();
			message = message.substring(message.indexOf(":")+2, message.length());
			caption = message.substring(0, message.indexOf(":"));
			caption = caption.substring(caption.lastIndexOf(".")+1, caption.length());
		}
		catch (Exception ex2) {
			caption = activity.getString(R.string.error);
			message = e.toString();
		}
		showException(e, caption, message, callback);
	}
	
	private static boolean isInMainThread() {
		long uiId = Looper.getMainLooper().getThread().getId();
		long cId = Thread.currentThread().getId();
		return uiId == cId;
	}
}