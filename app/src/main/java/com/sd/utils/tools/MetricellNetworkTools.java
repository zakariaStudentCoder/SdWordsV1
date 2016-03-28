package com.sd.utils.tools;

import java.util.List;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class MetricellNetworkTools {

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
		} catch (Exception e) {}
		
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
		} catch (Exception e) {}
		
		return false;
	}	
	
	/**
	 * Checks if an active data connection (either WIFI or cellular) is available.
	 * @return True if a data connection (either WIFI or cellular) is available and ready to use, false otherwise.
	 */
	public static boolean hasDataConnection(Context c) {		
		try {
			ConnectivityManager cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE); 
	        NetworkInfo netInfo = cm.getActiveNetworkInfo(); 
	        if (netInfo != null && netInfo.isConnected()) { 
	        	return true;        	
	        }
		} catch (Exception e) {}
		
        return false;
	}
	
	/**
	 * Checks if a mobile data connection is available.
	 * @return True if a data connection is available, but not necessarily connected.
	 * @see isMobileDataConnected
	 */	
	public static boolean isMobileDataEnabled(Context c) {
		try {
			ConnectivityManager cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (netInfo != null && netInfo.isAvailable()) {
				return true;
			}
		} catch (Exception e) {}
		
		return false;		
	}

	/**
	 * Checks if a mobile data connection is available and connected.
	 * @return True if a data connection is available and will be used for data traffic
	 * @see isMobileDataEnabled
	 */		
	public static boolean isMobileDataConnected(Context c) {
		try {
			ConnectivityManager cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (netInfo != null && netInfo.isAvailable() && netInfo.isConnected()) {
				return true;
			}
		} catch (Exception e) {}
		
		return false;		
	}		
	
	/**
	 * Checks if a WIFI data connection is available.
	 * @return True if a WIFI connection is available, but not necessarily connected.
	 * @see isWifiConnected
	 */		
	public static boolean isWifiEnabled(Context c) {		
		try {
			ConnectivityManager cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (netInfo != null && netInfo.isAvailable()) {
				return true;
			}
		} catch (Exception e) {}
		
		return false;
	}
	
	/**
	 * Checks if a WIFI connection is available and connected.
	 * @return True if a WIFI connection is available and will be used for data traffic
	 * @see isWifiEnabled
	 */		
	public static boolean isWifiConnected(Context c) {
		try {
			ConnectivityManager cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (netInfo != null && netInfo.isAvailable() && netInfo.isConnected()) {
				return true;
			}
		} catch (Exception e) {}
		
		return false;
	}	

	public static int getMobileDataConnectionType(Context c) {
		try {
			TelephonyManager tm = (TelephonyManager)c.getSystemService(Context.TELEPHONY_SERVICE);
			int networkType = tm.getNetworkType();
			
			//ConnectivityManager cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
			//NetworkInfo netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	
			return networkType;
		} catch (Exception e) {}
		
		return TelephonyManager.NETWORK_TYPE_UNKNOWN;
	}
	

	/**
	 * Checks if one or more location services are available.
	 * @return True if one or more location services are available and ready to use, false otherwise.
	 */
	public static boolean hasLocationServiceAvailable(Context c) {
		
		try {
		
			boolean gpsEnabled = false;
			boolean netEnabled = false;
			
			LocationManager lm = (LocationManager)c.getSystemService(Context.LOCATION_SERVICE);
			List<String> providers = lm.getProviders(false);
			for(String provider : providers) {	
				if (provider.equals(LocationManager.GPS_PROVIDER) && lm.isProviderEnabled(provider)) {
					gpsEnabled = true;
	
				} else if (provider.equals(LocationManager.NETWORK_PROVIDER) && lm.isProviderEnabled(provider)) {
					netEnabled = true;
				}
			}
			
			return (gpsEnabled || netEnabled);
		} catch (Exception e) {}
		
		return false;
	}	
		
}
