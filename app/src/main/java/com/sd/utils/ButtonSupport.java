package com.sd.utils;

import android.app.Activity;

/**
 * Created by Admin on 25.02.2016.
 */
public class ButtonSupport {
    public final int buttonId;
    public final long duration;
    public final Class<? extends Activity> clazz;

    public ButtonSupport(int buttonId, long duration, Class<? extends Activity> clazz) {
        this.buttonId = buttonId;
        this.duration = duration;
        this.clazz = clazz;
    }
}
