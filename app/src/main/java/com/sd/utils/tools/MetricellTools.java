package com.sd.utils.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

public class MetricellTools {

	/**
	 * Constant for one second in milliseconds
	 */
	public static final long SECOND = 1000;

	/**
	 * Constant for one minute in milliseconds
	 */
	public static final long MINUTE = 60 * SECOND;

	/**
	 * Constant for one hour in milliseconds
	 */
	public static final long HOUR = 60 * MINUTE;	

	/**
	 * Constant for one day in milliseconds
	 */
	public static final long DAY = 24 * HOUR;

	/**
	 * Constant for one year in milliseconds
	 */
	public static final long YEAR = 365 * DAY;
	
	public static long utcRealtimeOffset = 0;
	
	public static long syncTime(long now, long interval) {
		if (interval == 0) return now;
		return (((now / interval) + 1) * interval);
	}
	
	public static void updateUtcRealtimeOffset(long newOffset, Context c) {
		utcRealtimeOffset = newOffset;
		
		try {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
			Editor editor = prefs.edit();
			editor.putLong("utc_realtime_offset", newOffset);
			editor.commit();			
		} catch (Exception e) {}
	}
	
	public static void loadUtcRealtimeOffsetFromSharedPreferences(Context c) {
		try {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
			if (prefs.contains("utc_realtime_offset")) {	
				utcRealtimeOffset = prefs.getLong("utc_realtime_offset", 0);		// should never be 0
			}			
		} catch (Exception e) {
			utcRealtimeOffset = 0;
		}
	}
	
	public static final long currentTimeMillis() {
		return System.currentTimeMillis() + utcRealtimeOffset;
	}	
	
	public static final long applyUtcRealtimeOffset(long t) {
		return t + utcRealtimeOffset;
	}
	
	public final static String md5Hash(final String s) {
		try {
			StringBuilder sb = new StringBuilder();
		    byte[] hash = null;
		    try {
		        MessageDigest md = MessageDigest.getInstance("MD5");
		        hash = md.digest(s.getBytes());
		        
		    } catch (NoSuchAlgorithmException e) {
		    	hash = s.getBytes();
		    }
		    
		    for (int i = 0; i < hash.length; ++i) {
		        String hex = Integer.toHexString(hash[i]);
		        if (hex.length() == 1) {
		            sb.append(0);
		            sb.append(hex.charAt(hex.length() - 1));
		        } else {
		            sb.append(hex.substring(hex.length() - 2));
		        }
		    }
		    
		    return sb.toString();		
		    
		} catch (Exception e) {}
		
		return s;
	}	
	
	/**
	 * Sets the location for the log file
	 * @param dir Directory to store the log file in
	 * @param fn The filename of the log file
	 */
	public static final String setLogFilename(String dir, String fn) {
		return MetricellLogger.getInstance().setLogFilename(dir, fn);
	}

	/**
	 * Returns the full filename of the current log file
	 * @return The filename of the log file
	 */
	public static final String getLogFilename() {
		return MetricellLogger.getInstance().getLogFilename();
	}
	
	/**
	 * Set the logging to pipe log message to a file.
	 * @param val True to append log messages to a log file
	 * @see setLogFilename
	 */
	public static final void setLogToFile(boolean val) {
		MetricellLogger.getInstance().setLogToFile(val);
	}

	/**
	 * Returns the name of the status of file logging.
	 * @return True if the logging is saved to file, false if otherwise
	 */
	public static final boolean getLogToFile() {
		return MetricellLogger.getInstance().getLogToFile();
	}	

	/**
	 * Set the logging to pipe log message to a file.
	 * @param val True to append log messages to a log file
	 * @see setLogFilename
	 */
	public static final void setLogToConsole(boolean val) {
		MetricellLogger.getInstance().setLogToConsole(val);
	}

	/**
	 * Returns the name of the current log file.
	 * @return True if the logging is output to the console, false if otherwise
	 */
	public static final boolean getLogToConsole() {
		return MetricellLogger.getInstance().getLogToConsole();
	}


	/**
	 * Appends a list of strings to the log output as debug messages.
	 * @param tag A string tag to display in the logcat
	 * @param messages A list of Strings to log
	 */
	public static final void log(final String tag, final List<String> messages) {
		MetricellLogger.getInstance().log(tag, messages);
	}

	/**
	 * Appends a string to the log output as a debug message.
	 * @param tag A string tag to display in the logcat
	 * @param message A String to append to the log
	 */
	public static final void log(final String tag, String message) {
		MetricellLogger.getInstance().log(tag, message);
	}

	public static final void appendToLogFile(String message, String fn) {
		MetricellLogger.getInstance().appendToLogFile(message, fn);
	}

	/**
	 * Appends a string to the log output as an error message.
	 * @param tag A string tag to display in the logcat
	 * @param message A String to append to the log
	 */	
	public static final void logError(final String tag, String message) {
		MetricellLogger.getInstance().logError(tag, message);
	}

	/**
	 * Appends a string to the log output as an info message.
	 * @param tag A string tag to display in the logcat
	 * @param message A String to append to the log
	 */	
	public static final void logInfo(final String tag, String message) {
		MetricellLogger.getInstance().logInfo(tag, message);
	}	

	/**
	 * Appends a string to the log output as a warning message.
	 * @param tag A string tag to display in the logcat
	 * @param message A String to append to the log
	 */
	public static final void logWarning(final String tag, String message) {
		MetricellLogger.getInstance().logWarning(tag, message);
	}	

	/**
	 * Appends an Exception to the log output as a debug message.
	 * @param tag A String tag to display in the logcat
	 * @param message A String to append to the log
	 */	
	public static final void logException(final String tag, final Throwable e) {
		MetricellLogger.getInstance().logException(tag, e);
	}

	public static final String loadLogContents() {
		return MetricellLogger.getInstance().loadLogContents();
	}
	
	/**
	 * Converts a GSM signal to dBm value
	 * @param strength Signal strength to convert 0 - 31
	 * @return The signal strength in dBm or -1 if signal strength is outside valid range.
	 */
	public static final int gsmSignalStrengthToDbm(int strength) {
		// Unknown
		if (strength < 0 || strength > 31) return -1;

		// -113, -111, -109, -107, -105, -103, ... -51
		return -113 + (2 * strength);
	
		// -113, -112, -110, -108, -106, -104 ... -53
		//return -113 + ((strength * 62) / 32); 
	}

	/**
	 * Converts a 2G signal strength reading (in dBm) to a signal bar level 0-5.
	 * @param dbm The dBm signal strength to convert
	 * @return A signal bar level 0-5 or -1 if the dbm is -1
	 */
	public static final int dbmToSignalBars2g(final int dbm) {
		if (dbm == -1) return -1;

		if (dbm >= -74) {
			return 5;
		} else if (dbm >= -88) {
			return 4;
		} else if (dbm >= -93) {
			return 3;
		} else if (dbm >= -97) {
			return 2;
		} else if (dbm >= -102) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Converts a signal strength bar value to an approximate dBm. 
	 * @param signalBars The number of signal bars from 0-totalBars
	 * @param totalBars The maximum number of bars signal bars can be
	 * @return The approximate dBm value of the signal bars
	 */
	public static final int gsmSignalBarsToDbm(final int signalBars, final int totalBars) {
		if (signalBars > totalBars || signalBars < 0) return -113;
		double ss = (double)signalBars * ((double)31 / (double)totalBars);
		return gsmSignalStrengthToDbm((int)ss);		
	}

	/**
	 * Converts a 3G signal strength reading (in dBm) to a signal bar level 0-5.
	 * @param dbm The dBm signal strength to convert
	 * @return A signal bar level 0-5 or -1 if the dbm is -1
	 */
	public static final int dbmToSignalBars3g(final int dbm) {
		if (dbm == -1) return -1;

		if (dbm >= -74) {
			return 5;
		} else if (dbm >= -84) {
			return 4;
		} else if (dbm >= -94) {
			return 3;
		} else if (dbm >= -102) {
			return 2;
		} else if (dbm >= -106) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Converts a 4G signal strength reading (in dBm) to a signal bar level 0-5.
	 * @param dbm The dBm signal strength to convert
	 * @return A signal bar level 0-5 or -1 if the dbm is -1
	 */
	public static final int dbmToSignalBars4g(final int dbm) {
		if (dbm == -1) return -1;

		if (dbm >= -83) {
			return 5;
		} else if (dbm >= -94) {
			return 4;
		} else if (dbm >= -104) {
			return 3;
		} else if (dbm >= -115) {
			return 2;
		} else if (dbm >= -125) {
			return 1;
		} else {
			return 0;
		}
	}
	
	/**
	 * Converts a UTC time stamp into a string that is safe to use on a filename. E.g. 20120528-123454
	 * @param time The UTC time stamp to convert
	 * @return A string of the time stamp or simply the long value if time is less than 0
	 */
	public static final String utcToFileTimestamp(final long time) {
		if (time < 0) return Long.toString(time);

		final Formatter fm = new Formatter();
		fm.format("%tY%tm%td-%tH%tM%tS", time, time, time, time, time, time);
		String s = fm.toString();
		fm.close();
		return s;
	}

	/**
	 * Converts a UTC time stamp into a string that contains no colons. E.g. 2012-05-28 12;34;54.432
	 * @param time The UTC time stamp to convert
	 * @return A string of the time stamp or simply the long value if time is less than 0
	 */	
	public static final String utcToSafeTimestamp(final long time) {
		if (time < -1) return Long.toString(time);

		final Formatter fm = new Formatter();
		fm.format("%tY-%tm-%td %tH;%tM;%tS.%tL", time, time, time, time, time, time, time);
		String s = fm.toString();
		fm.close();
		return s;
	}

	/**
	 * Converts a UTC time stamp into a more descriptive string. E.g. Fri. 1 June, 14:34
	 * @param time The UTC time stamp to convert
	 * @return A string of the time stamp or simply the long value if time is less than 0
	 */		
	public static final String utcToWordyTimestamp(final long time) {
		if (time < -1) return Long.toString(time);

		final Formatter fm = new Formatter();
		fm.format("%ta. %te %tB, %tH:%tM", time, time, time, time, time);
		String s = fm.toString();
		fm.close();
		return s;
	}	
	

	/**
	 * Converts a UTC time stamp into a full string. E.g. 23/05/13 3:32:65 pm
	 * @param time The UTC time stamp to convert
	 * @return A string of the time stamp or simply the long value if time is less than 0
	 */	
	public static final String utcToPrettyTimestamp(final long time) {
		if (time < -1) return Long.toString(time);

		final Formatter fm = new Formatter();
		fm.format("%td/%tm %tI:%tM %tp", time, time, time, time, time);
		
		String s = fm.toString();
		fm.close();
		return s;
	}	

	/**
	 * Converts a UTC time stamp into a full string. E.g. 2012-06-23 15:32:65.654
	 * @param time The UTC time stamp to convert
	 * @return A string of the time stamp or simply the long value if time is less than 0
	 */	
	public static final String utcToTimestamp(final long time) {
		if (time < -1) return Long.toString(time);

		final Formatter fm = new Formatter();
		fm.format("%tF %tT.%tL", time, time, time);
		String s = fm.toString();
		fm.close();
		return s;
	}

	
	
	/**
	 * Converts a time in milliseconds to an age string. E.g. 4d 14h 4m 5s
	 * @param age Time in milliseconds to convert
	 * @return The age string
	 */
	public static final String ageToString(long age) {
		final long days = (age / DAY);
		age -= (days * DAY);

		final long hours = (age / HOUR);
		age -= (hours * HOUR);

		final long minutes = (age / MINUTE);
		age -= (minutes * MINUTE);

		final long seconds = (age / SECOND);

		StringBuilder sb = new StringBuilder();
		if (days > 0) sb.append(days).append("d ");
		if (hours > 0) sb.append(hours).append("h ");
		if (minutes > 0) sb.append(minutes).append("m ");
		if (seconds > 0) sb.append(seconds).append("s ");

		return sb.toString().trim();
	}

	/**
	 * Converts a time in milliseconds to an age string. E.g. 4d 14h 4m
	 * @param age Time in milliseconds to convert
	 * @return The age string
	 */
	public static final String ageToStringNoSeconds(long age) {

		final long days = (age / DAY);
		age -= (days * DAY);

		final long hours = (age / HOUR);
		age -= (hours * HOUR);

		final long minutes = (age / MINUTE);
		age -= (minutes * MINUTE);

		StringBuilder sb = new StringBuilder();
		if (days > 0) sb.append(days).append("d ");
		if (hours > 0) sb.append(hours).append("h ");
		if (minutes > 0) sb.append(minutes).append("m ");

		return sb.toString().trim();
	}	

	/**
	 * Converts a time in milliseconds to an age string. E.g. 14:04:05
	 * @param age Time in milliseconds to convert (max 24 hours)
	 * @return The age string
	 */
	public static final String ageToDigitString(long age) {
		final long days = (age / DAY);
		age -= (days * DAY);

		final long hours = (age / HOUR);
		age -= (hours * HOUR);

		final long minutes = (age / MINUTE);
		age -= (minutes * MINUTE);

		final long seconds = (age / SECOND);

		StringBuilder sb = new StringBuilder();
		if (hours > 0) sb.append(hours).append(":");
		
		if (minutes > 0) {
			if (hours > 0 && minutes < 10) {
				sb.append("0");
			}
			sb.append(minutes).append(":");
		} else {
			sb.append("0:");
		}
		
		if (seconds > 0) {
			if (seconds < 10) {
				sb.append("0");
			}
			sb.append(seconds);
		} else {
			sb.append("00");
		}

		return sb.toString().trim();
	}	
	
	/**
	 * Returns a string describing the current device info. E.g. ZTE ZTE-BLADE (blade, ZTE) 2.2
	 * @return Current device info string
	 */
	public static final String getDeviceInfo() {
		final String info = Build.MANUFACTURER + " " + Build.MODEL + " (" + Build.DEVICE + ", " + Build.BRAND + ") " + Build.VERSION.RELEASE;
		return info;
	}

	/**
	 * Returns the application name stored in the current package manifest.
	 * @param c Application context
	 * @param defaultName A default name to return if the application name cannot be found
	 * @return The application name
	 */
	public static final String getAppName(final Context c, final String defaultName) {
		String n = "";
		try {
			final PackageManager pm = c.getPackageManager();
			ApplicationInfo ai;
			try {
				ai = pm.getApplicationInfo(c.getPackageName(), 0);
			} catch (final NameNotFoundException e) {
				ai = null;
			}
			n = (String) (ai != null ? pm.getApplicationLabel(ai) : defaultName);

		} catch (Exception e) {
		}

		return n;
	}

	/**
	 * Returns the application version string. The current theme name is not-appended.
	 * @param c Application context
	 * @return Application version string
	 */
	public static final String getBaseAppVersion(final Context c) {

		// Update the version number text field
		String v = "";
		try {
			final PackageInfo packageInfo = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
			v = packageInfo.versionName;			
		} catch (NameNotFoundException e) {
		}	

		return v;
	}

	/**
	 * Returns the application version code
	 * @param c Application context
	 * @return Application version code
	 */
	public static final int getBaseAppVersionCode(final Context c) {

		// Update the version number text field
		int versionCode = 0;
		try {
			final PackageInfo packageInfo = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
			versionCode = packageInfo.versionCode;			
		} catch (NameNotFoundException e) {
		}

		return versionCode;
	}

	/**
	 * Returns the unique device ID for the current device. This is currently the IMEI
	 * @param c Application context
	 * @return The device ID string
	 */
	public static final String getImei(Context c) {
		try {
			TelephonyManager tm = (TelephonyManager)c.getSystemService(Activity.TELEPHONY_SERVICE);
			return tm.getDeviceId();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns the current SIM MCC and MNC.
	 * @param c Application context
	 * @return A 2 element String array, [0] is the MCC, [1] is the MNC
	 */
	public static final String[] getSimMccMnc(Context c) {
		try {
			TelephonyManager tm = (TelephonyManager)c.getSystemService(Activity.TELEPHONY_SERVICE);

			String mccmnc = tm.getSimOperator();

			String simMcc = "0";
			String simMnc = "0";
			try {
				simMcc = mccmnc.substring(0, 3);
				simMnc = mccmnc.substring(3);
			} catch (Exception e) {
			}			

			return new String[] { simMcc, simMnc };

		} catch (Exception e) {
			return null;
		}
	}	

	/**
	 * Returns the current network MCC and MNC.
	 * @param c Application context
	 * @return A 2 element String array, [0] is the MCC, [1] is the MNC
	 */
	public static final String[] getNetworkMccMnc(Context c) {
		try {
			TelephonyManager tm = (TelephonyManager)c.getSystemService(Activity.TELEPHONY_SERVICE);

			String mccmnc = tm.getNetworkOperator();

			String simMcc = "0";
			String simMnc = "0";
			try {
				simMcc = mccmnc.substring(0, 3);
				simMnc = mccmnc.substring(3);
			} catch (Exception e) {
			}			

			return new String[] { simMcc, simMnc };

		} catch (Exception e) {
			return null;
		}
	}	
	
	/**
	 * Returns the current device language code (e.g. en, fr, it, ar)
	 * @return The device language code
	 */
	public static final String getDeviceLanguage() {
		Locale myLocale = Locale.getDefault();
		String lang = myLocale.getLanguage();
		if (lang == null || lang.length() < 2) {
			lang = "en";
		} else if (lang.length() > 2) {
			lang = lang.substring(0, 2);
		}
		
		return lang.toLowerCase(Locale.getDefault());	
	}

	
	public static final ArrayList<String> getDeviceRadioLog() {

		// Check the logs for 'onDisconnect: cause=...'
		Process mLogcatProc = null;
		BufferedReader reader = null;
		ArrayList<String> logLines = null;

		try {
			mLogcatProc = Runtime.getRuntime().exec("logcat -d -v time -b radio GSM:D *:S");

			reader = new BufferedReader(new InputStreamReader( mLogcatProc.getInputStream())); 				
			logLines = new ArrayList<String>();

			String s = "";
			while((s = reader.readLine()) != null) {
				logLines.add(s);
			}

		} catch (Exception e) {
			MetricellTools.logException("MetricellTools", e);

		} finally {

			try {
				if (reader != null) {
					reader.close();					
				}
			} catch (Exception e) {}

			if (mLogcatProc != null) mLogcatProc.destroy();
		}		

		return logLines;
	}	
	
	/**
	 * Returns a String representation of an integer that has been padded to a specific length with zeros.
	 * @param val The integer to pad
	 * @param length The minimum length to pad to
	 * @return The padded string
	 */
	public static final String pad(final int val, final int length) {
		final Formatter f = new Formatter();
		f.format("%0" + length + "d", val);
		String s = f.toString();
		f.close();
		return s;
	}

	/**
	 * Returns a double value rounded to a specific number of decimal places
	 * @param d The double value to round
	 * @param dp The number of decimal places
	 * @return The rounded double value as a String
	 */
	public static final String round(final double d, final int dp) {
		final Formatter fm = new Formatter();
		fm.format("%." + dp + "f", d);
		String s = fm.toString();
		fm.close();
		return s;
	}

	/**
	 * Converts a string into title case, e.g. This String Is In Title Case
	 * @param s The string to convert
	 * @return The string in Title Case
	 */
	public static final String toTitleCase(String s) {
		final StringBuffer sb = new StringBuffer();		
		s = s.toLowerCase(Locale.getDefault());

		char c = 0;
		char prev = 0;
		for (int i = 0; i < s.length(); i++) {
			c = s.charAt(i);

			if (i == 0 || prev == ' ' || prev == '.' || prev == ',' ||
					prev == '(' || prev == '[' || prev == '{' || prev == ';') sb.append(Character.toUpperCase(c));
			else sb.append(c);

			prev = c;
		}

		return sb.toString();
	}

	/**
	 * Converts a Location object into a readable string.
	 * @param l Location object
	 * @return A string description of the location
	 */
	public static final String locationToString(final Location l) {
		final StringBuffer sb = new StringBuffer();

		sb.append(MetricellTools.round(l.getLatitude(), 6));
		sb.append(", ");
		sb.append(MetricellTools.round(l.getLongitude(), 6));
		sb.append("  ");
		sb.append(MetricellTools.utcToTimestamp(l.getTime()));

		final long age = System.currentTimeMillis() - l.getTime();
		sb.append(" (age:" + MetricellTools.ageToString(age) + ")");

		if (l.hasAccuracy()) {
			sb.append(" Accuracy:");				
			sb.append(l.getAccuracy() + "m");
		}

		sb.append(" Provider:");
		sb.append(l.getProvider());

		return sb.toString();
	}

	/**
	 * Cleans a numeric string removing all non-numeric characters, valid characters are [0-9 . -]
	 * 
	 * @param numberString The string to clean
	 * @return A cleaned version of the supplied string.
	 */
	public static final String cleanNumericString(final String numberString) {
		final StringBuilder sb = new StringBuilder();
		final char[] chars = numberString.toCharArray();
		for (char c : chars) {
			if (c >= '0' && c <= '9' || c == '.' || c == '-') {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	public static final String cleanMsisdn(final String msisdn) {
		final StringBuilder sb = new StringBuilder();
		final char[] chars = msisdn.toCharArray();
		for (char c : chars) {
			if (c >= '0' && c <= '9') {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static final String addCommas(final Number n) {
		NumberFormat formatter = new DecimalFormat("###,###,###");
		return formatter.format(n);
	}

	public static final String createPaddingString(char c, int length) {
		StringBuffer sb = new StringBuffer();
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * Calls the specified method name on the supplied object through reflection. If the method does not exist the returned value will 
	 * be Integer.MAX_VALUE
	 * @param obj The object to call the method on
	 * @param methodName The method name to call
	 * @return The integer returned from the method, or Integer.MAX_VALUE if the method does not exist
	 */
	public static int callMethodThroughReflection(Object obj, String methodName, int minValue, int maxValue) {
		int value = Integer.MAX_VALUE;

		try {			
			if (obj == null || methodName == null) return value;
			Class<?> objectClass = obj.getClass();
			Method reflectedMethod = objectClass.getMethod(methodName, (Class[]) null);

			if (reflectedMethod != null) {
				reflectedMethod.setAccessible(true);
				value = (Integer)reflectedMethod.invoke(obj, (Object[]) null);

				// If the returned value is outside our legal range then treat as garbage
				if (value < minValue || value > maxValue) {
					value = Integer.MAX_VALUE;
				}
			}

		} catch (Exception e) {
			value = Integer.MAX_VALUE;
		}		

		return value;		
	}	

	/**
	 * Calls the specified methods on the supplied object through reflection. Each method is called in turn until a valid return value is received.
	 * If no values are received, Integer.MAX_VALUE is returned.
	 * @param obj The object to call the method on
	 * @param methodNames The method names to call
	 * @return The integer returned from the method, or Integer.MAX_VALUE if the methods do not exist
	 */
	public static int callMethodsThroughReflection(Object obj, String[] methodNames, int minValue, int maxValue) {
		int value = Integer.MAX_VALUE;

		try {			
			if (obj == null) return value;

			for (int i = 0; i < methodNames.length; i++) {
				if (methodNames[i] != null) {
					value = callMethodThroughReflection(obj, methodNames[i], minValue, maxValue);
					if (value != Integer.MAX_VALUE) {
						break;
					}		
				}
			}

		} catch (Exception e) {
			value = Integer.MAX_VALUE;
		}		

		return value;		
	}	

	
	/**
	 * Checks if the GPS hardware is available and enabled.
	 * @return True if enabled, otherwise false
	 */
	public static boolean isGpsEnabled(Context c) {
		try {
			LocationManager lm = (LocationManager)c.getSystemService(Context.LOCATION_SERVICE);
			
			boolean gpsAvailable = false;
			boolean gpsEnabled = false;
			List<String> providers = lm.getProviders(false);
			for(String provider : providers) {
				if (provider.equals(LocationManager.GPS_PROVIDER)) {
					gpsAvailable = true;
					if (lm.isProviderEnabled(provider)) {
						gpsEnabled = true;
					}												
				}
			}
			
			return (gpsAvailable && gpsEnabled);
			
		} catch (Exception e) {			
		}
		
		return false;
	}
	
	/**
	 * Checks if the network location hardware is available and enabled.
	 * @return True if enabled, otherwise false
	 */
	public static boolean isNetworkLocationEnabled(Context c) {
		try {
			LocationManager lm = (LocationManager)c.getSystemService(Context.LOCATION_SERVICE);		
			boolean netAvailable = false;
			boolean netEnabled = false;
			
			List<String> providers = lm.getProviders(false);
			for(String provider : providers) {
				if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
					netAvailable = true;
					if (lm.isProviderEnabled(provider)) {
						netEnabled = true;
					}												
				}
			}
			
			return (netAvailable && netEnabled);		
		} catch (Exception e) {			
		}
		
		return false;
	}	

	/**
	 * Makes a specified string safe to use as an XML parameter or value.
	 * @param s The string to convert
	 * @return The converted string.
	 */
	public static final String makeXmlSafe(final String s) {	
		if (s == null) return "";
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '&') {
				sb.append("&amp;");
			} else if (c == '<') {
				sb.append("&lt;");
			} else if (c == '>') {
				sb.append("&gt;");
			} else if (c == '\'') {
				sb.append("&apos;");
			} else if (c == '\"') {
				sb.append("&quot;");
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}	
	
}
