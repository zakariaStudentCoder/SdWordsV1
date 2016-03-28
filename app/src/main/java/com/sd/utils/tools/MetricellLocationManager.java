package com.sd.utils.tools;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


public class MetricellLocationManager {
	
	public static final int LOCATION_REFRESH_GPS = 1;
	public static final int LOCATION_REFRESH_NETWORK = 2;
	
	private MetricellLocationManagerListener mListener;

	private Context mContext;
	private Timer mLocationTimeoutTimer;
	
	private MccServiceLocationMonitor mGPSLocationMonitor, mNetworkLocationMonitor;
	
	public MetricellLocationManager(Context context) {
		mContext = context;

		mGPSLocationMonitor = new MccServiceLocationMonitor(this, LocationManager.GPS_PROVIDER);
		mNetworkLocationMonitor = new MccServiceLocationMonitor(this, LocationManager.NETWORK_PROVIDER);		
	}
	
	public void setListener(MetricellLocationManagerListener l) {
		mListener = l;
	}
	
	public void shutdown() {
		try {
			stopLocationRefresh();		
		} catch  (Exception e) {
			MetricellTools.logException(getClass().getName(), e);
		}
	}	
	
	/**
	 * Turns on the GPS hardware to initiate a location fix
	 * @return True if the GPS hardware is enabled (or is already enabled) and has been setup to receive location updates
	 */
	public boolean turnOnGps() {
		try {	
			// Only enable GPS if it has the hardware for it and it is enabled
			if (MetricellNetworkTools.isGpsEnabled(mContext)) {
				MetricellTools.log(getClass().getName(), "Activating GPS");	
				
				LocationManager lm = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mGPSLocationMonitor);

				return true;
			}
			
		} catch (Exception e) {
			MetricellTools.logException(getClass().getName(), e);
		}
		
		return false;
	}	
	
	/**
	 * Turns off the GPS hardware.
	 */
	public void turnOffGps() {
		try {									
			MetricellTools.log(getClass().getName(), "De-activating GPS");
			
			LocationManager lm = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
			lm.removeUpdates(mGPSLocationMonitor);
		} catch (Exception e) {
			MetricellTools.logException(getClass().getName(), e);
		}
	}

	/**
	 * Turns on the network location hardware to initiate a location fix
	 * @return True if the network location is enabled (or is already enabled) and has been setup to receive location updates
	 */	
	public void turnOnNetworkLocation() {
		try {
			if (MetricellNetworkTools.isNetworkLocationEnabled(mContext)) {
				MetricellTools.log(getClass().getName(), "Activating Network Location");
				
				LocationManager lm = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
				lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mNetworkLocationMonitor);
			}
			
		} catch (Exception e) {
			MetricellTools.logException(getClass().getName(), e);
		}
	}
	
	/**
	 * Turns off the network location search.
	 */	
	public void turnOffNetworkLocation() {
		try {
			MetricellTools.log(getClass().getName(), "De-activating Network Location");
			
			LocationManager lm = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
			lm.removeUpdates(mNetworkLocationMonitor);
		} catch (Exception e) {
			MetricellTools.logException(getClass().getName(), e);
		}
	}	

	/**
	 * Starts a forced location search with a specified timeout and optional accuracy timeout
	 * @param timeout The timeout in seconds
	 * @param accuracyTimeout The extra timeout to wait to improve the accuracy of the fix, set to 0 to disable.
	 */
	public void refreshLocation(long timeout, int searchProviders) {
				
		// Setup a time-out
		if (mLocationTimeoutTimer != null) {
			mLocationTimeoutTimer.cancel();
		}
		
		// Setup the main timeout
		if (timeout > 0 && searchProviders > 0) {
			mLocationTimeoutTimer = new Timer();
			mLocationTimeoutTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					locationRefreshTimedOut();				
				}
			}, timeout);
		}
		
		if ((searchProviders & LOCATION_REFRESH_GPS) != 0) turnOnGps();
		if ((searchProviders & LOCATION_REFRESH_NETWORK) != 0) turnOnNetworkLocation();
	}	
	
	
	/**
	 * Called when a forced location search has timed out. Sends a LOCATION_SEARCH_TIMEOUT_ACTION broadcast.
	 * @see forceLocationRefreshWithTimeout
	 */
	private void locationRefreshTimedOut() {
		try {
			if (mListener != null) {
				mListener.locationManagerTimedOut(this);
			}
			
			stopLocationRefresh();
			
		} catch (Exception e) {
		}		
	}	
	
	public void stopLocationRefresh() {
		try {
			if (mLocationTimeoutTimer != null) {
				mLocationTimeoutTimer.cancel();
				mLocationTimeoutTimer = null;
			}
		
			turnOffGps();
			turnOffNetworkLocation();

		} catch (Exception e) {
			MetricellTools.logException(getClass().getName(), e);
		}
	}
	
	/*
	 * LocationMonitor
	 */
	public class MccServiceLocationMonitor implements LocationListener {

		private String mLocationProvider;
		private MetricellLocationManager mParent;
		
		public MccServiceLocationMonitor(MetricellLocationManager parent, String provider) {
			mParent = parent;
			mLocationProvider = provider;
		}
		
		@Override
		public void onLocationChanged(Location loc) {
			if (mListener != null) {
				
				// Modify the time offset of the network location (since these are based on phone time, not satellite time)
				if (loc.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
					loc.setTime(MetricellTools.applyUtcRealtimeOffset(loc.getTime()));
				}
				
				mListener.locationManagerLocationUpdated(mParent, loc);
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			if (provider != null && provider.equalsIgnoreCase(mLocationProvider)) {
				MetricellTools.log(getClass().getName(), "Location provider '" + provider + "' disabled.");
				if (mListener != null) mListener.locationManagerProviderStateChanged(mParent, provider, false);
			}
		}

		@Override
		public void onProviderEnabled(String provider) {
			if (provider != null && provider.equalsIgnoreCase(mLocationProvider)) {
				MetricellTools.log(getClass().getName(), "Location provider '" + provider + "' enabled.");
				if (mListener != null) mListener.locationManagerProviderStateChanged(mParent, provider, true);
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			/*
			String state = "Available";
			if (status == LocationProvider.OUT_OF_SERVICE) {
				state = "Out of Service";
			} else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
				state = "Temporarily Unavailable";
			}
			
			MetricellTools.log(getClass().getName(), "Location provider '"+provider+"' " + state);
			*/
		}
	}	
	
}
