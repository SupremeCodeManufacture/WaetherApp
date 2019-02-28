package view.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.supreme.manufacture.weather.BuildConfig;
import com.supreme.manufacture.weather.R;

import data.App;
import data.GenericConstants;
import logic.helpers.ThemeColorsHelper;

public class SettingsActivity extends BaseActivity {

    public static final String SETTING_PRO = App.getAppCtx().getResources().getString(R.string.stg_pro);
    public static final String SETTING_NO_ADS = App.getAppCtx().getResources().getString(R.string.stg_ads);
    public static final String SETTING_SUPPORT = App.getAppCtx().getResources().getString(R.string.stg_support);
    public static final String SETTING_RATE = App.getAppCtx().getResources().getString(R.string.stg_rate);
    public static final String SETTING_TERMS = App.getAppCtx().getResources().getString(R.string.stg_terms);
    public static final String SETTING_ABOUT = App.getAppCtx().getResources().getString(R.string.stg_about);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(ThemeColorsHelper.getTitleTheme(App.isDAY()), true);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getResources().getString(R.string.txt_settings));
    }


    public static class MyPreferenceFragment extends PreferenceFragment implements
            Preference.OnPreferenceClickListener {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences_settings);

            setupTriggerOptions();
        }

        private void setupTriggerOptions() {
            Preference prefConnectionsWarn = (Preference) findPreference(SETTING_PRO);
            prefConnectionsWarn.setOnPreferenceClickListener(this);

            Preference prefPush = (Preference) findPreference(SETTING_NO_ADS);
            prefPush.setOnPreferenceClickListener(this);

            Preference prefSyncDataWarn = (Preference) findPreference(SETTING_RATE);
            prefSyncDataWarn.setOnPreferenceClickListener(this);

            Preference prefGuestWarn = (Preference) findPreference(SETTING_SUPPORT);
            prefGuestWarn.setOnPreferenceClickListener(this);

            Preference aboutPref = (Preference) findPreference(SETTING_ABOUT);
            aboutPref.setOnPreferenceClickListener(this);

            Preference termsPref = (Preference) findPreference(SETTING_TERMS);
            termsPref.setOnPreferenceClickListener(this);
        }


        @Override
        public boolean onPreferenceClick(Preference preference) {
            String keyRes = preference.getKey();

            if (keyRes.equals(SETTING_PRO)) {
                // TODO: 27/02/2019  

            } else if (keyRes.equals(SETTING_NO_ADS)) {
                // TODO: 27/02/2019  

            } else if (keyRes.equals(SETTING_SUPPORT)) {
                try {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", GenericConstants.SUPPORT_EMAIL, null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, App.getAppCtx().getResources().getString(R.string.app_name));
                    startActivity(emailIntent);

                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getActivity(), App.getAppCtx().getResources().getString(R.string.error_txt_no_mail), Toast.LENGTH_LONG).show();
                }

            } else if (keyRes.equals(SETTING_RATE)) {
                try {
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + App.getAppCtx().getPackageName());
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));

                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getActivity(), App.getAppCtx().getResources().getString(R.string.error_open_google_play), Toast.LENGTH_LONG).show();
                }

            } else if (keyRes.equals(SETTING_TERMS)) {
                Intent intentAbout = new Intent(getActivity(), WebBrowserActivity.class);
                intentAbout.putExtra(GenericConstants.KEY_EXTRA_BROWSER_TITLE, App.getAppCtx().getResources().getString(R.string.txt_terms));
                intentAbout.putExtra(GenericConstants.KEY_EXTRA_BROWSER_LINK, GenericConstants.TERMS_LINK);
                startActivity(intentAbout);

            } else if (keyRes.equals(SETTING_ABOUT)) {
                Toast.makeText(getActivity(), "v. " + BuildConfig.VERSION_NAME, Toast.LENGTH_LONG).show();
            }

            return false;
        }
    }

}