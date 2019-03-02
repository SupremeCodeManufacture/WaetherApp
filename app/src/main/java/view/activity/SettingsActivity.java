package view.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.supreme.manufacture.weather.BuildConfig;
import com.supreme.manufacture.weather.R;

import data.App;
import data.GenericConstants;
import logic.helpers.MyLogs;
import logic.helpers.ThemeColorsHelper;
import logic.payment.PaymentHelper;
import logic.payment.util.IabResult;

public class SettingsActivity extends BaseActivity {

    public static final String SETTING_PRO = App.getAppCtx().getResources().getString(R.string.stg_pro);
    public static final String SETTING_SUPPORT = App.getAppCtx().getResources().getString(R.string.stg_support);
    public static final String SETTING_RATE = App.getAppCtx().getResources().getString(R.string.stg_rate);
    public static final String SETTING_TERMS = App.getAppCtx().getResources().getString(R.string.stg_terms);
    public static final String SETTING_ABOUT = App.getAppCtx().getResources().getString(R.string.stg_about);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(ThemeColorsHelper.getTitleTheme(App.isDAY()), true);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        //MyLogs.LOG("SettingsActivity", "onCreate", "....");

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getResources().getString(R.string.txt_settings));
    }

    public void startPayment() {
        //init here and payment is done when it's initializsed in listener - recreated activity
        PaymentHelper.setUpPayments(SettingsActivity.this, SettingsActivity.this);
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences_settings);

            setupTriggerOptions();
        }

        private void setupTriggerOptions() {
            PreferenceCategory mCategory = (PreferenceCategory) findPreference("interaction");
            Preference prefConnectionsWarn = (Preference) findPreference(SETTING_PRO);
            if (App.isPaidFull() || (App.isPaidUnlock() && App.isPaidAds())) {
                mCategory.removePreference(prefConnectionsWarn);

            } else {
                prefConnectionsWarn.setOnPreferenceClickListener(this);
            }

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
                ((SettingsActivity) getActivity()).startPayment();

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


    @Override
    void decideDemoOrPro() {
        //MyLogs.LOG("SettingsActivity", "decideDemoOrPro", "....");
        recreate();
    }

    //=============================== PAYMENTS FUNCTIONAL ========================================//
    @Override
    public void onIabSetupFinished(IabResult result) {
        if (result.isSuccess()) {
            //MyLogs.LOG("SettingsActivity", "onIabSetupFinished", "Setting up In-app Billing succesfull");
            showUpgradeDialog();

        } else {
            //MyLogs.LOG("SettingsActivity", "onIabSetupFinished", "Problem setting up In-app Billing: " + result);
        }
    }
}