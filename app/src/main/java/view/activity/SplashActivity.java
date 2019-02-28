package view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Calendar;

import data.App;
import logic.helpers.LangUtils;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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