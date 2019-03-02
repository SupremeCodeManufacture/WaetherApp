package view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import androidx.appcompat.app.AppCompatActivity;
import io.fabric.sdk.android.Fabric;
import java.util.Calendar;

import data.App;
import logic.helpers.LangUtils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.setAppBuilds(App.getAppBuilds() + 1);
        Fabric.with(this, new Crashlytics());

        LangUtils.changeAppLangForUser();

        detectDayNight();

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