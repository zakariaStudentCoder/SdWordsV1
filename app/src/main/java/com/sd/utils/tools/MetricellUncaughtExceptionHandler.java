package com.sd.utils.tools;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;

import android.os.Build;
import android.util.Log;

public class MetricellUncaughtExceptionHandler implements UncaughtExceptionHandler {

	private UncaughtExceptionHandler mDefaultHandler = null;
	private LogFile mLogFile = null;
	
	public MetricellUncaughtExceptionHandler(UncaughtExceptionHandler defaultHandler, String logDir, String logFilename) {
		mDefaultHandler = defaultHandler;
		mLogFile = new LogFile(logDir, logFilename);
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
				
		try {
			final ArrayList<String> lines = new ArrayList<String>();
			lines.add("--- UncaughtException " + ex.toString() + " -----------------");
			lines.add("Device: " + Build.MANUFACTURER + " " + Build.MODEL + " (" + Build.DEVICE + ", " + Build.BRAND + ") " + Build.VERSION.RELEASE);
			//lines.add("Memory: " + "total="+Runtime.getRuntime().totalMemory() + " free="+ Runtime.getRuntime().freeMemory() + " max="+Runtime.getRuntime().maxMemory());

			final StackTraceElement[] stackElements = ex.getStackTrace();
			if (stackElements != null) {
				for (StackTraceElement element : stackElements) {
					lines.add(element.toString());
				}
			}
			
			Log.e(getClass().getName(), "Uncaught Exception", ex);
			mLogFile.append(lines, LogFile.ERROR);
			
		} catch (Exception e) {
			
		} finally {
			if (mDefaultHandler != null) {
				mDefaultHandler.uncaughtException(thread, ex);
			}
		
			// cleanup, don't know if really required
			//thread.getThreadGroup().destroy();
		}
	}
}
