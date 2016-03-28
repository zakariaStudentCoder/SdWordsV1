package com.sd.utils.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class UITools {
	
	public static final int UNIT_KBPS = 0;
	public static final int UNIT_MBPS = 1;
	public static final int UNIT_KBYTE_SEC = 2;
	public static final int UNIT_MBYTE_SEC = 3;
	
	public static void applyGlobalTypeface(final Activity a, final Typeface font) {
		View v = (View)a.findViewById(android.R.id.content);
		applyTypeface(font, v);	
	}
	
	public static void applyTypeface(final Typeface f, final View v) {
		if (v == null) return;
		
		if (v instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup)v;
			for (int i = 0; i < vg.getChildCount(); i++) {
				applyTypeface(f, vg.getChildAt(i));
			}
			
		} else if (v instanceof TextView) {
			((TextView)v).setTypeface(f);

		}
		
	}
	
	public static ArrayList<String> stringArrayToList(String[] values) {
		final ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < values.length; ++i) {
			list.add(values[i]);
		}	
		return list;
	}
	

	/**
	 * Checks if a WIFI data connection is available.
	 * @return True if a WIFI connection is available, but not necessarily connected.
	 * @see isWifiConnected
	 */		
	public static boolean isWifiEnabled(Context c) {
		ConnectivityManager cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (netInfo != null && netInfo.isAvailable()) {
			return true;
		}

		return false;
	}
	/**
	 * Returns the unique subscriber ID for the current sim. This is currently the IMSI
	 * @param c Application context
	 * @return The device ID string
	 */
	public static String getImsi(Context c) {
		try {
			TelephonyManager tm = (TelephonyManager)c.getSystemService(Activity.TELEPHONY_SERVICE);
			
			String imsi = tm.getSubscriberId();
			/*
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
			if (prefs.contains(c.getString(R.string.key_override_imsi))) {
				String v = prefs.getString(c.getString(R.string.key_override_imsi), "0");
				if (v.length() > 0 && !v.equals("0")) {
					imsi = v;
				}
			}
			*/
			return imsi;
		} catch (Exception e) {
			return null;
		}
	}
	
	
	
	
	public static String getMsisdn(Context c) {
		try {
			TelephonyManager tm = (TelephonyManager)c.getSystemService(Activity.TELEPHONY_SERVICE);
			
			String msisdn = tm.getLine1Number();
			/*
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
			if (prefs.contains(c.getString(R.string.key_override_phonenum))) {
				String v = prefs.getString(c.getString(R.string.key_override_phonenum), "0");
				if (v.length() > 0 && !v.equals("0")) {
					msisdn = v;
				}
			}*/
			
			return msisdn;
		} catch (Exception e) {
			return null;
		}
	}	

	
	
	
	
	/**
	 * Checks if an active data connection (either WIFI or cellular) is available.
	 * @return True if a data connection (either WIFI or cellular) is available and ready to use, false otherwise.
	 */
	public static boolean hasDataConnection(Context c) {
        ConnectivityManager cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) { 
        	return true;
        	
        } else {
            return false;
        } 
	}
	
	
	/**
	 * Converts a speed is bytes/sec into a different unit display
	 * @param speed The data speed in bytes/sec
	 * @param unit The unit to convert to
	 * @return
	 */
	public static String formatSpeed(final double speed, final int unit) {
				
		if (unit == UNIT_KBPS) {
			Long displaySpeed = Long.valueOf((long) ((speed * 8) / 1000));
			NumberFormat formatter = new DecimalFormat("###,###,###");
			return formatter.format(displaySpeed);			
						
		} else if (unit == UNIT_MBPS) {
			Double displaySpeed = Double.valueOf((double) ((speed * 8) / 1000000));
			NumberFormat formatter = new DecimalFormat("###,###,##0.00");
			return formatter.format(displaySpeed);
			
		} else if (unit == UNIT_KBYTE_SEC) {
			Double displaySpeed = Double.valueOf((double) (speed / 1024));
			NumberFormat formatter = new DecimalFormat("###,###,##0.00");
			return formatter.format(displaySpeed);		
			
		} else if (unit == UNIT_MBYTE_SEC) {
			Double displaySpeed = Double.valueOf((double) (speed / 1048576));
			NumberFormat formatter = new DecimalFormat("###,###,##0.00");
			return formatter.format(displaySpeed);
		}
		
		return "" + (long)speed;
	}	
	
	public static final Spanned toSmallCaps(final String s) {
		
		StringBuffer html = new StringBuffer();
		
		try {
			
			String upper = s.toUpperCase(Locale.getDefault());
			
			boolean inSmallTag = false;
			boolean seenFirstChar = false;
			
			for (int i = 0; i < upper.length(); i++) {
				char c = upper.charAt(i);
				
				if (c == ' ' || c == '\t' || c == '\n') {
					if (inSmallTag) {				
						html.append("</small>");
						inSmallTag = false;
						seenFirstChar = false;
					}
				} else {
					if (!seenFirstChar) {
						seenFirstChar = true;
						
					} else if (!inSmallTag) {
						html.append("<small>");
						inSmallTag = true;
					}
				}
				
				html.append(c);
			}
			
			if (inSmallTag) {
				html.append("</small>");
			}
			
		} catch (Exception e) {}

		return Html.fromHtml(html.toString());
	}
	
	
	
}
