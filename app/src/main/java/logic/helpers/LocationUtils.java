package logic.helpers;

import android.content.Intent;
import android.provider.Settings;

import data.App;

public class LocationUtils {

    public static boolean isLocationEnabled() {
        int mode = Settings.Secure.getInt(App.getAppCtx().getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
        return (mode != Settings.Secure.LOCATION_MODE_OFF);
    }

    public static void openLocSettings() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getAppCtx().startActivity(intent);
    }
}