package view.activity;

import android.content.Intent;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import data.App;
import io.fabric.sdk.android.Fabric;
import logic.helpers.LangUtils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.setAppBuilds(App.getAppBuilds() + 1);
        Fabric.with(this, new Crashlytics());

        LangUtils.changeAppLangForUser();

        detectDayNight();

        if (App.getFirstLaunchMilis() == 0)
            App.setFirstLaunchMilis(System.currentTimeMillis());

        goToNextAtivity();
    }

    public void goToNextAtivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void detectDayNight() {
        int timeOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 6 && timeOfDay < 20) {
            App.setDAY(true);

        } else {
            App.setDAY(false);
        }
    }
}