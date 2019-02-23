package view.activity;

import android.content.Intent;
import android.os.Bundle;

import logic.helpers.LangUtils;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LangUtils.changeAppLangForUser();

        goToNextAtivity();
    }

    public void goToNextAtivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}