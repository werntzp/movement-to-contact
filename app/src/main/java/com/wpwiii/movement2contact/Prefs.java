package com.wpwiii.movement2contact;

import android.content.Context;
import android.content.SharedPreferences;

class Prefs {

    static final int SETTINGS_MUSIC = 0;
    static final int SETTINGS_SOUND = 1;
    static final String KEY_MUSIC = "music";
    static final String KEY_SOUND = "sound";
    static final String PREFS_FILE = "M2C_PREFS_FILE";

    public static void setValue(Context c, String k, int v) {

        SharedPreferences.Editor editor = c.getSharedPreferences(Prefs.PREFS_FILE, 0).edit();
        editor.putInt(k, v);
        editor.commit();

    }

    public static int getValue(Context c, String k) {

        return c.getSharedPreferences(Prefs.PREFS_FILE, 0).getInt(k, 1);

    }

}


