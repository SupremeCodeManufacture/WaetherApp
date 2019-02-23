package logic.helpers;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

import data.App;

public class LangUtils {

    public static void changeAppLangForUser() {
        try {
            Resources res = App.getAppCtx().getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            String lang = getselectedLanguage();
            conf.locale = new Locale(lang);
            res.updateConfiguration(conf, dm);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String getselectedLanguage() {
        String userSelectedLang = App.getSelectedLanguage();
        return userSelectedLang != null ? userSelectedLang : Locale.getDefault().getLanguage();
    }

}
