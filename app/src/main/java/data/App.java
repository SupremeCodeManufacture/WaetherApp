package data;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    private static Context mContext;
    public static int APP_BUILDS;
    public static String SELECTED_LANGUAGE;
    public static String SELECTED_LOC;
    public static boolean DAY;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }


    public static Context getAppCtx() {
        return mContext;
    }

    public static int getAppBuilds() {
        return APP_BUILDS != 0 ? APP_BUILDS : SharedPrefs.getSharedPreferencesInt(SharedPrefs.KEY_SP_APP_BUILDS, 0);
    }

    public static void setAppBuilds(int appBuilds) {
        APP_BUILDS = appBuilds;
        SharedPrefs.setSharedPreferencesInt(SharedPrefs.KEY_SP_APP_BUILDS, appBuilds);
    }

    public static String getSelectedLanguage() {
        return SELECTED_LANGUAGE != null ? SELECTED_LANGUAGE : SharedPrefs.getSharedPreferencesString(GenericConstants.KEY_SP_SELECTED_LANGUAGE, null);
    }

    public static void setSelectedLoc(String selectedLoc) {
        SELECTED_LOC = selectedLoc;
    }

    public static String getSelectedLoc() {
        return SELECTED_LOC != null ? SELECTED_LOC : SharedPrefs.getSharedPreferencesString(GenericConstants.KEY_SP_SELECTED_LOC, null);
    }

    public static boolean isDAY() {
        return DAY;
    }

    public static void setDAY(boolean DAY) {
        App.DAY = DAY;
    }

}