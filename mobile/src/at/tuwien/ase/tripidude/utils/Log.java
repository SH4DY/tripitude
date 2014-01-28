package at.tuwien.ase.tripidude.utils;

/**
 * 
 * @author Matthias Schštta
 *
 */
public class Log {
	
	public interface LogSource {
	    public String getLogSourceName();
	}
	
	private static long timerStart;
	private static boolean isDebugable;
	private static String packageName = "PackageName not set - call Log.init()";
	
	public static void init(String packageName, boolean isDebugable) {
		Log.packageName = packageName;
		Log.isDebugable = isDebugable;
	}

	public static boolean isLoggable(int level) {		
		switch(level) {
		case android.util.Log.WARN:
			return true;
		case android.util.Log.ERROR:
			return true;
		case android.util.Log.INFO:
			return true;
		default: 
			return isDebugable;
		}
	}

	// Generic

	private static void log(int type, String message, Object... args) {
		if (!isLoggable(type))
			return;
		if (message == null || message.length() == 0)
			message = "Unknown";
		if (args.length > 0)
			message = String.format(message, args);
		switch (type) {
		case android.util.Log.VERBOSE:
			android.util.Log.v(packageName, message);
			break;
		case android.util.Log.DEBUG:
			android.util.Log.d(packageName, message);
			break;
		case android.util.Log.INFO:
			android.util.Log.i(packageName, message);
			break;
		case android.util.Log.WARN:
			android.util.Log.w(packageName, message);
			break;
		case android.util.Log.ERROR:
			android.util.Log.e(packageName, message);
			break;
		}
	}

	private static void log(int type, LogSource source, String function, String message, Object... args) {
		log(type, String.format("%s.%s: %s", source.getLogSourceName(), function, message), args);
	}

	private static void log(int type, LogSource source, String function) {
		log(type, String.format("%s.%s", source.getLogSourceName(), function));
	}

	private static void log(int type, String source, String function, String message, Object... args) {
		log(type, String.format("%s.%s: %s", source, function, message), args);
	}

	private static void log(int type, String source, String function) {
		log(type, String.format("%s.%s", source, function));
	}

	// Verbose

	public static void verbose(LogSource source, String function, String message, Object... args) {
		log(android.util.Log.VERBOSE, source, function, message, args);
	}

	public static void verbose(LogSource source, String function) {
		log(android.util.Log.VERBOSE, source, function);
	}

	public static void verbose(String source, String function, String message, Object... args) {
		log(android.util.Log.VERBOSE, source, function, message, args);
	}

	public static void verbose(String source, String function) {
		log(android.util.Log.VERBOSE, source, function);
	}

	// Debug

	public static void debug(LogSource source, String function, String message, Object... args) {
		log(android.util.Log.DEBUG, source, function, message, args);
	}

	public static void debug(LogSource source, String function) {
		log(android.util.Log.DEBUG, source, function);
	}

	public static void debug(String source, String function, String message, Object... args) {
		log(android.util.Log.DEBUG, source, function, message, args);
	}

	public static void debug(String source, String function) {
		log(android.util.Log.DEBUG, source, function);
	}

	// Info

	public static void info(LogSource source, String function, String message, Object... args) {
		log(android.util.Log.INFO, source, function, message, args);
	}

	public static void info(LogSource source, String function) {
		log(android.util.Log.INFO, source, function);
	}

	public static void info(String source, String function, String message, Object... args) {
		log(android.util.Log.INFO, source, function, message, args);
	}

	public static void info(String source, String function) {
		log(android.util.Log.INFO, source, function);
	}

	// Warn

	public static void warn(LogSource source, String function, String message, Object... args) {
		log(android.util.Log.WARN, source, function, message, args);
	}

	public static void warn(LogSource source, String function) {
		log(android.util.Log.WARN, source, function);
	}

	public static void warn(String source, String function, String message, Object... args) {
		log(android.util.Log.WARN, source, function, message, args);
	}

	public static void warn(String source, String function) {
		log(android.util.Log.WARN, source, function);
	}

	// Error

	public static void error(LogSource source, String function, String message, Object... args) {
		log(android.util.Log.ERROR, source, function, message, args);
	}

	public static void error(LogSource source, String function) {
		log(android.util.Log.ERROR, source, function);
	}

	public static void error(String source, String function, String message, Object... args) {
		log(android.util.Log.ERROR, source, function, message, args);
	}

	public static void error(String source, String function) {
		log(android.util.Log.ERROR, source, function);
	}

	// Exception

	public static void exception(LogSource source, String function, Throwable e, String message, Object... args) {
		if (message == null || message.length() == 0)
			message = e.toString();
		log(android.util.Log.ERROR, source, function, message, args);
		e.printStackTrace();
	}

	public static void exception(LogSource source, String function, Throwable e) {
		String message = e.toString();
		log(android.util.Log.ERROR, source, function, message);
		e.printStackTrace();
	}

	public static void exception(String source, String function, Throwable e, String message, Object... args) {
		if (message == null || message.length() == 0)
			message = e.toString();
		log(android.util.Log.ERROR, source, function, message, args);
		e.printStackTrace();
	}

	public static void exception(String source, String function, Throwable e) {
		String message = e.toString();
		log(android.util.Log.ERROR, source, function, message);
		e.printStackTrace();
	}

	public static void exception(Throwable e, String message, Object... args) {
		if (message == null || message.length() == 0)
			message = e.toString();
		log(android.util.Log.ERROR, message, args);
		e.printStackTrace();
	}

	public static void exception(Throwable e) {
		String message = e.toString();
		log(android.util.Log.ERROR, message);
		e.printStackTrace();
	}

	// Timer

	public static long startTimerSafe() {
		if (!isLoggable(android.util.Log.VERBOSE))
			return 0;
		return System.currentTimeMillis();
	}

	public static void startTimer() {
		timerStart = startTimerSafe();
	}

	public static void stopTimerSafe(long timerStart, LogSource source, String function, String message, Object... args) {
		if (!isLoggable(android.util.Log.VERBOSE))
			return;
		log(android.util.Log.VERBOSE, source, function, "%s (%d ms)", String.format(message, args), System.currentTimeMillis()-timerStart);
	}

	public static void stopTimerSafe(long timerStart, LogSource source, String function) {
		if (!isLoggable(android.util.Log.VERBOSE))
			return;
		log(android.util.Log.VERBOSE, source, String.format("%s (%d ms)", function, System.currentTimeMillis()-timerStart));
	}

	public static void stopTimerSafe(long timerStart, String source, String function, String message, Object... args) {
		if (!isLoggable(android.util.Log.VERBOSE))
			return;
		log(android.util.Log.VERBOSE, source, function, "%s (%d ms)", String.format(message, args), System.currentTimeMillis()-timerStart);
	}

	public static void stopTimerSafe(long timerStart, String source, String function) {
		if (!isLoggable(android.util.Log.VERBOSE))
			return;
		log(android.util.Log.VERBOSE, source, String.format("%s (%d ms)", function, System.currentTimeMillis()-timerStart));
	}

	public static void stopTimer(LogSource source, String function, String message, Object... args) {
		stopTimerSafe(timerStart, source, function, message, args);
	}

	public static void stopTimer(LogSource source, String function) {
		stopTimerSafe(timerStart, source, function);
	}

	public static void stopTimer(String source, String function, String message, Object... args) {
		stopTimerSafe(timerStart, source, function, message, args);
	}

	public static void stopTimer(String source, String function) {
		stopTimerSafe(timerStart, source, function);
	}
}
