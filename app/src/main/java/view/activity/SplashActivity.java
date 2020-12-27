package view.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import data.App;
import data.GenericConstants;
import logic.helpers.LangUtils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.setAppBuilds(App.getAppBuilds() + 1);

        String loc = "47.0105" + "," + "28.8638";
        App.setSelectedLoc(loc);

        LangUtils.changeAppLangForUser();

        detectDayNight();

        goToNextAtivity();
    }

    public void goToNextAtivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(GenericConstants.EXTRA_TIP_KEY, getIntent().getStringExtra(GenericConstants.EXTRA_TIP_KEY));
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