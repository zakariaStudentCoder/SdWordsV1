package com.sd.utils.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import android.net.TrafficStats;
import android.os.Build;

public class MetricellTrafficStats {

	public static final int BYTES_RECEIVED = 0;
	public static final int BYTES_SENT = 1;
	
	/**
	 * Gets the number of received and sent bytes for the specified process UID.
	 * @param uid The process UID to get traffic stats for.
	 * @return Returns a two element long array, the value first element (at index BYTES_RECEIVED) is the bytes downloaded, and the second (BYTES_SENT) the bytes transmitted. 
	 */
	public static final long[] getUidRxTxBytes(final int uid) {
		
		long[] rxTxBytes = new long[2];
		rxTxBytes[BYTES_RECEIVED] = 0;
		rxTxBytes[BYTES_SENT] = 0;
				
		// 4.3 is buggy and TrafficStats returns 0 for every UID (except for running process UID), this workaround loads the stats
		// directly from the /proc/uid_stats/ files.		
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR2) {

			try {
				File dir = new File("/proc/uid_stat/");
				String[] children = dir.list();
				if (!Arrays.asList(children).contains(String.valueOf(uid))){
				    return rxTxBytes;
				}
			
				File uidFileDir = new File("/proc/uid_stat/" + uid);
				File uidActualFileReceived = new File(uidFileDir, "tcp_rcv");
				File uidActualFileSent = new File(uidFileDir, "tcp_snd");
		
				String textReceived = "0";
				String textSent = "0";

				String receivedLine = "0";
				String sentLine = "0";
				
				BufferedReader brReceived = new BufferedReader(new FileReader(uidActualFileReceived));
				if ((receivedLine = brReceived.readLine()) != null) {
					textReceived = receivedLine;
				}

				BufferedReader brSent = new BufferedReader(new FileReader(uidActualFileSent));					
				if ((sentLine = brSent.readLine()) != null) {
					textSent = sentLine;
				}
				
				 rxTxBytes[BYTES_RECEIVED] = Long.valueOf(textReceived).longValue();
				 rxTxBytes[BYTES_SENT] = Long.valueOf(textSent).longValue();

			} catch (IOException e) {}
			
		// Use TrafficStats on Android 2.2 and above
		} else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1) {
			rxTxBytes[BYTES_RECEIVED] = TrafficStats.getUidRxBytes(uid);
			rxTxBytes[BYTES_SENT] = TrafficStats.getUidTxBytes(uid);
		}

		return rxTxBytes;
	}
	
	/**
	 * Wrapper for TrafficStats.getTotalRxBytes, checks that TrafficStats is supported by the OS.
	 * @return The total bytes received, or -1 if not supported
	 */
	public static final long getTotalRxBytes() {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1) {
			return TrafficStats.getTotalRxBytes();
		}
		return -1;
	}
	
	/**
	 * Wrapper for TrafficStats.getTotalTxBytes, checks that TrafficStats is supported by the OS.
	 * @return The total bytes sent, or -1 if not supported
	 */	
	public static final long getTotalTxBytes() {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1) {
			return TrafficStats.getTotalTxBytes();
		}
		return -1;
	}	
	
	/**
	 * Wrapper for TrafficStats.getMobileRxBytes, checks that TrafficStats is supported by the OS.
	 * @return The mobile bytes received, or -1 if not supported
	 */		
	public static final long getMobileRxBytes() {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1) {
			return TrafficStats.getMobileRxBytes();
		}
		return -1;
	}
	
	/**
	 * Wrapper for TrafficStats.getMobileTxBytes, checks that TrafficStats is supported by the OS.
	 * @return The mobile bytes sent, or -1 if not supported
	 */	
	public static final long getMobileTxBytes() {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1) {
			return TrafficStats.getMobileTxBytes();
		}
		return -1;
	}	

}
