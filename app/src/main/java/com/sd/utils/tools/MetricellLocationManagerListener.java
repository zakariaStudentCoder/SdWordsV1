package com.sd.utils.tools;

import android.location.Location;

public interface MetricellLocationManagerListener {
	
	public void locationManagerLocationUpdated(MetricellLocationManager manager, Location l);
	public void locationManagerTimedOut(MetricellLocationManager manager);
	public void locationManagerProviderStateChanged(MetricellLocationManager manager, String provider, boolean enabled);
	
}
