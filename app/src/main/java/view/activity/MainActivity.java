package view.activity;

import android.os.Bundle;

import com.SupremeManufacture.weather.R;
import com.SupremeManufacture.weather.databinding.ActivityMainBinding;

import androidx.databinding.DataBindingUtil;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding mActivityBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

    }
}
