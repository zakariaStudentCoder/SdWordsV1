package com.sd.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;


import java.util.Locale;

public class CommonResources {

	
	private static Typeface mNormalTypeface;
	private static Typeface mBoldTypeface;
	
	public synchronized static void loadResources(Context c) {
		try {

			if (mNormalTypeface == null) {
				mNormalTypeface = Typeface.createFromAsset(c.getAssets(), "fonts/NeoTech-Light.ttf");
				if (mNormalTypeface == null) {
					mNormalTypeface = Typeface.createFromAsset(c.getAssets(), "fonts/NeoTech-Light.ttf");
				}
			}

			if (mBoldTypeface == null) {
				mBoldTypeface = Typeface.createFromAsset(c.getAssets(), "fonts/NeoTec-Med.ttf");
				if (mBoldTypeface == null) {
					mBoldTypeface = Typeface.createFromAsset(c.getAssets(), "fonts/NeoTec-Med.ttf");
				}
			}
			

			
		} catch (Exception e) {

		}
	}
	public synchronized static Typeface getNormalTypeface() {
		return mNormalTypeface;
	}
	
	public static Typeface getBoldTypeface() {
		return mBoldTypeface;
	}	
	
}
