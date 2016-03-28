package com.sd.utils.tools;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import android.os.Environment;
import android.util.Log;

public class LogFile {
	
	public static final int NONE = -1;
	public static final int DEBUG = 0;
	public static final int ERROR = 1;
	public static final int INFO  = 2;
	public static final int WARNING = 3;
	private static final char[] LOG_TYPES = {
		'D', 'E', 'I', 'W'
	};
	
	private String mDir;
	private String mFilename;

	public LogFile(String dir, String name) {
		mDir = dir.trim();
		mFilename = name.trim();
	
		if (mFilename.startsWith("/")) {
			mFilename = mFilename.substring(1);
		}
		
		if (mDir.startsWith("/")) mDir = mDir.substring(1);
		if (mDir.endsWith("/")) mDir = mDir.substring(0, mDir.length()-2);
	}
	
	public final String getFullFilename() {
		return Environment.getExternalStorageDirectory() + "/" + mDir + "/" + mFilename;
	}	

	public final boolean exists() {
		try {
			
		    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
		    	final File sdCard = Environment.getExternalStorageDirectory();
		    	final File dir = new File(sdCard.getAbsolutePath() + "/" + mDir);
		    	if (!dir.exists()) return false;
		    	
		    	final File f = new File(dir, mFilename);
		    	if (!f.exists()) return false;
		    	
		    	return true;
		    }
		    
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(getClass().getName(), e.toString());			
		}
	
		return false;
	}
	
	public final void append(final List<String> lines, final int type) {
		try {
			
		    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
		    	final File sdCard = Environment.getExternalStorageDirectory();
		    	final File dir = new File(sdCard.getAbsolutePath() + "/" + mDir);
		    	if (!dir.exists()) dir.mkdirs();
		    	
		    	FileWriter fw = null;
		    	
				try {	
					fw = new FileWriter(new File(dir, mFilename), true);

					String timeStamp = "";
					if (type >= 0) {
						timeStamp = "[" + LOG_TYPES[type] + "] " + MetricellTools.utcToTimestamp(System.currentTimeMillis());							
					}
					
					for (String line : lines) {
						if (type >= 0) fw.append(timeStamp + ": ");
						fw.append(line);
						fw.append('\n');
					}
					
					fw.flush();					
					
				} catch (IOException e) {
					e.printStackTrace();					
					Log.e(getClass().getName(), e.toString());
					
				} finally {
					try {
						if (fw != null) fw.close();
					} catch (Exception e) {}
				}
		    } else {
		    	Log.w(getClass().getName(), "Unable to append to log, no media mounted!");
		    }
		
		} catch (Exception e) {
			Log.e(getClass().getName(), e.toString());
		}
	}
	
	public final void append(final String data, final int type) {
		try {
			
		    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
		    	final File sdCard = Environment.getExternalStorageDirectory();
		    	final File dir = new File(sdCard.getAbsolutePath() + "/" + mDir);
		    	if (!dir.exists()) dir.mkdirs();
		    	
		    	FileWriter fw = null;
		    	
				try {
					fw = new FileWriter(new File(dir, mFilename), true);

					String timeStamp = "";
					if (type >= 0) {
						timeStamp = "[" + LOG_TYPES[type] + "] " + MetricellTools.utcToTimestamp(System.currentTimeMillis());							
					}
					
					if (type >= 0) fw.append(timeStamp + ": ");
					fw.append(data);
					fw.append('\n');
					
					fw.flush();					
					
				} catch (IOException e) {
					Log.e(getClass().getName(), e.toString());
					e.printStackTrace();
					
				} finally {
					try {
						if (fw != null) fw.close();
					} catch (Exception e) {}
				}
				
		    } else {
		    	Log.w(getClass().getName(), "Unable to append to log, no media mounted!");
		    }
		
		} catch (Exception e) {
			Log.e(getClass().getName(), e.toString());
		}
	}
	
	public final long size() {		
		try {
			
		    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
		    	final File sdCard = Environment.getExternalStorageDirectory();
		    	final File dir = new File(sdCard.getAbsolutePath() + "/" + mDir);
		    	if (!dir.exists()) return 0;
		    	
		    	final File f = new File(dir, mFilename);
		    	if (!f.exists()) return 0;
		    	
		    	return f.length();
		    }
		    
		} catch (Exception e) {
		}
		
		return 0;
	}
	
	public final String load() {
		StringBuffer sb = new StringBuffer();
		FileReader fr = null;
		
		try {
		    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
		    	final File sdCard = Environment.getExternalStorageDirectory();
		    	final File dir = new File(sdCard.getAbsolutePath() + "/" + mDir);
		    	
		    	if (dir.exists()) {
			    	final File f = new File(dir, mFilename);
			    	if (f.exists()) {

			    		int len = -1;
			    		char[] buffer = new char[8096];
			    		fr = new FileReader(f);
			    		while((len = fr.read(buffer))!= -1) {
			    			sb.append(buffer, 0, len);
			    		}			    		
			    	}		    		
		    	}
		    }
		} catch (Exception e) {
		} finally {
			try {
				if (fr != null) fr.close();
			} catch (Exception e) {}			
		}
		return sb.toString();
	}
	
}
