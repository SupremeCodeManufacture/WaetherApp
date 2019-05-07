package data;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsPreferences {

    private static SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getAppCtx());


    public static String getSharedPrefsString(String key, String defVal) {
        return defaultSharedPreferences.getString(key, defVal);
    }

    public static void setSharedPrefsString(String key, String string) {
        defaultSharedPreferences.edit().putString(key, string).apply();
    }

    public static boolean getSharedPrefsBool(String key) {
        return defaultSharedPreferences.getBoolean(key, false);
    }

    public static void setSharedPrefsBool(String key, boolean value) {
        defaultSharedPreferences.edit().putBoolean(key, value).apply();
    }

}