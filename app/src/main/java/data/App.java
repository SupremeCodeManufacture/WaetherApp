package data;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDexApplication;
import logic.payment.util.IabHelper;

public class App extends MultiDexApplication {

    private static Context mContext;
    public static int APP_BUILDS;
    public static String SELECTED_LANGUAGE;
    public static String SELECTED_LOC;
    public static boolean DAY;
    public static IabHelper PAYMENT_HELPER;
    public static boolean PAID_FULL;
    public static boolean PAID_ADS;
    public static boolean PAID_UNLOCK;

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

    public static void setSelectedLanguage(String selectedLanguage) {
        SELECTED_LANGUAGE = selectedLanguage;
        SharedPrefs.setSharedPreferencesString(GenericConstants.KEY_SP_SELECTED_LANGUAGE, selectedLanguage);
    }

    public static String getSelectedLoc() {
        return SELECTED_LOC != null ? SELECTED_LOC : SharedPrefs.getSharedPreferencesString(GenericConstants.KEY_SP_SELECTED_LOC, null);
    }

    public static void setSelectedLoc(String selectedLoc) {
        SELECTED_LOC = selectedLoc;
        SharedPrefs.setSharedPreferencesString(GenericConstants.KEY_SP_SELECTED_LOC, selectedLoc);
    }

    public static boolean isDAY() {
        return DAY;
    }

    public static void setDAY(boolean DAY) {
        App.DAY = DAY;
    }

    public static IabHelper getPaymentHelper() {
        return PAYMENT_HELPER;
    }

    public static void setPaymentHelper(IabHelper paymentHelper) {
        PAYMENT_HELPER = paymentHelper;
    }

    public static boolean isPaidFull() {
        return PAID_FULL || SharedPrefs.getSharedPreferencesBool(SharedPrefs.KEY_SP_IS_PAID_FULL, false);
    }

    public static void setPaidFull(boolean paidFull) {
        PAID_FULL = paidFull;
        SharedPrefs.setSharedPreferencesBool(SharedPrefs.KEY_SP_IS_PAID_FULL, paidFull);
    }

    public static boolean isPaidAds() {
        return PAID_ADS || SharedPrefs.getSharedPreferencesBool(SharedPrefs.KEY_SP_IS_PAID_ADS, false);

    }

    public static void setPaidAds(boolean paidAds) {
        PAID_ADS = paidAds;
        SharedPrefs.setSharedPreferencesBool(SharedPrefs.KEY_SP_IS_PAID_ADS, paidAds);
    }

    public static boolean isPaidUnlock() {
        return PAID_UNLOCK || SharedPrefs.getSharedPreferencesBool(SharedPrefs.KEY_SP_IS_PAID_UNLOCK, false);
    }

    public static void setPaidUnlock(boolean paidUnlock) {
        PAID_UNLOCK = paidUnlock;
        SharedPrefs.setSharedPreferencesBool(SharedPrefs.KEY_SP_IS_PAID_UNLOCK, paidUnlock);
    }
}