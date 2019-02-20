package view.activity;

import android.os.Bundle;
import android.view.View;

import com.SupremeManufacture.weather.R;
import com.SupremeManufacture.weather.databinding.ActivityMainBinding;

import androidx.databinding.DataBindingUtil;
import logic.async_await.OnAsyncDoneRsObjListener;
import logic.listeners.OnFetchDataErrListener;
import logic.network.RequestManager;

public class MainActivity extends BaseActivity implements
        View.OnClickListener ,
        OnAsyncDoneRsObjListener,
        OnFetchDataErrListener {

    private ActivityMainBinding mActivityBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_details:
                RequestManager.execGetSalons("Chisinau", MainActivity.this, MainActivity.this);
                break;
        }
    }

    @Override
    public <T> void onDone(T obj) {

    }

    @Override
    public void onErrNptify(String errStr) {

    }
}
