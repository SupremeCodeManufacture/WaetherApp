package view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.student.adminweather.BuildConfig;
import com.student.adminweather.R;

import data.App;
import data.GenericConstants;
import logic.helpers.MyLogs;
import logic.helpers.ThemeColorsHelper;

public class SettingsActivity extends BaseActivity {

    public static final String SETTING_TERMS = App.getAppCtx().getResources().getString(R.string.stg_terms);
    public static final String SETTING_ABOUT = App.getAppCtx().getResources().getString(R.string.stg_about);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(ThemeColorsHelper.getTitleTheme(App.isDAY()), true);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        MyLogs.LOG("SettingsActivity", "onCreate", "....");

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getResources().getString(R.string.txt_settings));
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences_settings);

            setupTriggerOptions();
        }

        private void setupTriggerOptions() {
            Preference aboutPref = (Preference) findPreference(SETTING_ABOUT);
            aboutPref.setOnPreferenceClickListener(this);

            Preference termsPref = (Preference) findPreference(SETTING_TERMS);
            termsPref.setOnPreferenceClickListener(this);
        }


        @Override
        public boolean onPreferenceClick(Preference preference) {
            String keyRes = preference.getKey();

            if (keyRes.equals(SETTING_TERMS)) {
                String termsLink = App.getAppCtx().getResources().getString(R.string.app_terms_link);

                Intent intentAbout = new Intent(getActivity(), WebBrowserActivity.class);
                intentAbout.putExtra(GenericConstants.KEY_EXTRA_BROWSER_TITLE, App.getAppCtx().getResources().getString(R.string.txt_terms));
                intentAbout.putExtra(GenericConstants.KEY_EXTRA_BROWSER_LINK, termsLink);
                startActivity(intentAbout);

            } else if (keyRes.equals(SETTING_ABOUT)) {
                Toast.makeText(getActivity(), "v. " + BuildConfig.VERSION_NAME, Toast.LENGTH_LONG).show();
            }

            return false;
        }
    }
}