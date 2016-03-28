package com.sd.utils.tools;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class MetricellLogger {

	private static MetricellLogger mInstance = null;
	public synchronized static MetricellLogger getInstance() {
		if (mInstance == null) {
			mInstance = new MetricellLogger();
		}
		
		return mInstance;
	}
	

	private LogFile mLogFile = null;
	private boolean mLogToConsole = false;
	private boolean mLogToFile = false;	
	private String mLogFilename = "MetricellLogger.log";
	private String mLogDirectory = "";
	
	protected MetricellLogger() {
		mLogToFile = false;
		mLogToConsole = false;
		
		mLogFile = new LogFile(mLogDirectory, mLogFilename);
	}
	
	/**
	 * Sets the location for the log file
	 * @param dir Directory to store the log file in
	 * @param fn The filename of the log file
	 */
	public final String setLogFilename(String dir, String fn) {
		if (dir == null) dir = "";
		if (fn == null) return "";
		
		mLogFilename = fn;
		mLogDirectory = dir;
		
		mLogFile = new LogFile(mLogDirectory, mLogFilename);
		return mLogFile.getFullFilename();
	}

	/**
	 * Returns the full filename of the current log file
	 * @return The log file filename
	 */
	public final String getLogFilename() {
		if (mLogFile == null) return null;
		
		return mLogFile.getFullFilename();
	}
	
	/**
	 * Set the logging to pipe log message to a file.
	 * @param val True to append log messages to a log file
	 * @see setLogFilename
	 */
	public final void setLogToFile(boolean val) {
		mLogToFile = val;
	}

	/**
	 * Returns the name of the status of file logging.
	 * @return True if the logging is saved to file, false if otherwise
	 */
	public final boolean getLogToFile() {
		return mLogToFile;
	}

	/**
	 * Set the logging to pipe log message to a file.
	 * @param val True to append log messages to a log file
	 * @see setLogFilename
	 */
	public final void setLogToConsole(boolean val) {
		mLogToConsole = val;
	}

	/**
	 * Returns the name of the current log file.
	 * @return True if the logging is output to the console, false if otherwise
	 */
	public final boolean getLogToConsole() {
		return mLogToConsole;
	}	
	
	
	/**
	 * Appends a list of strings to the log output as debug messages.
	 * @param tag A string tag to display in the logcat
	 * @param messages A list of Strings to log
	 */
	public final void log(final String tag, final List<String> messages) {		
		if (messages == null) return;

		if (mLogToConsole) {
			for (String message : messages) {
				if (message == null) {
					message = "null";
				}

				Log.d(tag, message);
			}
		}

		if (mLogToFile) {
			mLogFile.append(messages, LogFile.DEBUG);
		}
	}

	/**
	 * Appends a string to the log output as a debug message.
	 * @param tag A string tag to display in the logcat
	 * @param message A String to append to the log
	 */
	public final void log(final String tag, String message) {
		if (message == null) {
			message = "null";
		}

		if (mLogToConsole) Log.d(tag, message);

		if (mLogToFile) {
			mLogFile.append(message, LogFile.DEBUG);
		}
	}

	public final void appendToLogFile(String message, String fn) {
		if (mLogToFile) {
			mLogFile.append(message, LogFile.NONE);
		}
	}

	public final boolean logFileExists(String fn) {
		return mLogFile.exists();
	}

	/**
	 * Appends a string to the log output as an error message.
	 * @param tag A string tag to display in the logcat
	 * @param message A String to append to the log
	 */	
	public final void logError(final String tag, String message) {
		if (message == null) {
			message = "null";
		}

		if (mLogToConsole) Log.e(tag, message);

		if (mLogToFile) {
			mLogFile.append(message, LogFile.ERROR);
		}		
	}

	/**
	 * Appends a string to the log output as an error message.
	 * @param tag A string tag to display in the logcat
	 * @param message A String to append to the log
	 */	
	public final void logError(final String tag, ArrayList<String> messages) {
		if (messages == null) return;

		if (mLogToConsole) {
			for (String message : messages) {
				if (message == null) {
					message = "null";
				}

				Log.e(tag, message);
			}
		}

		if (mLogToFile) {
			mLogFile.append(messages, LogFile.ERROR);
		}
	}	
	
	/**
	 * Appends a string to the log output as an info message.
	 * @param tag A string tag to display in the logcat
	 * @param message A String to append to the log
	 */	
	public final void logInfo(final String tag, String message) {
		if (message == null) {
			message = "null";
		}		

		if (mLogToConsole) Log.i(tag, message);

		if (mLogToFile) {
			mLogFile.append(message, LogFile.INFO);
		}		
	}	

	/**
	 * Appends a string to the log output as a warning message.
	 * @param tag A string tag to display in the logcat
	 * @param message A String to append to the log
	 */
	public final void logWarning(final String tag, String message) {
		if (message == null) {
			message = "null";
		}		

		if (mLogToConsole) Log.w(tag, message);

		if (mLogToFile) {
			mLogFile.append(message, LogFile.WARNING);
		}		
	}	
	
	/**
	 * Appends an Exception to the log output as a debug message.
	 * @param tag A String tag to display in the logcat
	 * @param message A String to append to the log
	 */	
	public final void logException(final String tag, final Throwable e) {
		try {
			if (e == null) return;

			if (mLogToConsole) Log.e(tag, e.toString());

			final ArrayList<String> lines = new ArrayList<String>();
			lines.add(e.toString());

			final StackTraceElement[] stackElements = e.getStackTrace();
			if (stackElements != null) {
				for (StackTraceElement element : stackElements) {
					lines.add(element.toString());
					if (mLogToConsole) Log.e(tag, element.toString());
				}
			}

			if (mLogToFile) {
				mLogFile.append(lines, LogFile.ERROR);
			}

		} catch (Exception ex) {}
	}

	
	
	
	public final String loadLogContents() {
		return mLogFile.load();
	}
	
}
