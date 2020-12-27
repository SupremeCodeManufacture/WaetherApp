package view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;
import com.student.adminweather.R;
import com.student.adminweather.databinding.ActivityMainBinding;

import data.App;
import data.GenericConstants;
import data.model.CurrentWeatherObj;
import data.model.DataRs;
import data.view_model.MainActivityViewModel;
import logic.async_await.OnAsyncDoneRsObjListener;
import logic.helpers.DataFormatConverter;
import logic.helpers.EncryptionUtil;
import logic.helpers.MyLogs;
import logic.helpers.PermissionsHelper;
import logic.helpers.ThemeColorsHelper;
import logic.listeners.OnDualSelectionListener;
import logic.listeners.OnFetchDataErrListener;
import logic.network.RequestManager;
import view.custom.CustomDialogs;

public class MainActivity extends BaseActivity implements
        View.OnClickListener,
        OnAsyncDoneRsObjListener,
        OnFetchDataErrListener {

    private ActivityMainBinding mActivityBinding;
    private MainActivityViewModel mViewModel;
    private static final int NEW_LOC_SELECTION_CODE = 567;


    final LocationManager mLocationManager = (LocationManager) App.getAppCtx().getSystemService(Context.LOCATION_SERVICE);
    final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (isActivityValid()) {
                MyLogs.LOG("MainActivity", "mLocationListener", "location: " + location.toString());
                onLoadLocationWeather(location.getLatitude() + "," + location.getLongitude());
            }

            mLocationManager.removeUpdates(mLocationListener);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            onWarnLocOff();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        //if no selected or prevously loaded city then try to auto detect location
        if (App.getSelectedLoc() != null) onLoadLocationWeather(App.getSelectedLoc());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(ThemeColorsHelper.getTheme(App.isDAY()), true);
        mActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        manageDayNightColors();

        bindViewModel();

        if (App.getSelectedLoc() == null)
            CustomDialogs.createSimpleDialog(
                    MainActivity.this,
                    App.getAppCtx().getResources().getString(R.string.txt_warn),
                    App.getAppCtx().getResources().getString(R.string.txt_ask_loc),
                    false,
                    App.getAppCtx().getResources().getString(R.string.txt_allow),
                    null,
                    new OnDualSelectionListener() {
                        @Override
                        public void onPositiveButtonClick() {
                            initMyLocation();
                        }

                        @Override
                        public void onNegativeButtonClick() {
                            goToAppSettings();
                        }
                    });
    }

    private void manageDayNightColors() {
        mActivityBinding.toolbarMain.setBackgroundResource(ThemeColorsHelper.getColorPrimaryDark(App.isDAY()));
        mActivityBinding.zoneMain.setBackgroundResource(ThemeColorsHelper.getColorPrimary(App.isDAY()));
    }

    private void bindViewModel() {
        mViewModel = ViewModelProviders.of(MainActivity.this).get(MainActivityViewModel.class);
        mViewModel.getCurData().observe(this, new Observer<DataRs>() {
            @Override
            public void onChanged(final DataRs dataRs) {
                if (dataRs != null) {
                    onLoadData(dataRs);

                } else {
                    showSnack(mActivityBinding.mainCoord, App.getAppCtx().getResources().getString(R.string.txt_oops), true);
                }
            }
        });
    }

    private void onLoadData(final DataRs dataRs) {
        String loc = DataFormatConverter.getCityName(dataRs.getLocation().getLat() + "," + dataRs.getLocation().getLon());
        mActivityBinding.tvToolbarPlace.setText(loc);

        loadCurWeather(dataRs.getCurrent());
    }

    private void loadCurWeather(CurrentWeatherObj currentWeatherObj) {
        mActivityBinding.tvMoodType.setText(currentWeatherObj.getWeather_descriptions()[0]);
        mActivityBinding.tvUvIndex.setText(String.valueOf(currentWeatherObj.getUv_index()));
        mActivityBinding.tvHum.setText(currentWeatherObj.getHumidity() + " %");
        mActivityBinding.tvTemp.setText(String.valueOf(currentWeatherObj.getTemperature()));
        mActivityBinding.tvDegreeType.setText("°C");
        mActivityBinding.tvFeels.setText(currentWeatherObj.getFeelslike() + " °C");
        mActivityBinding.tvWind.setText(currentWeatherObj.getWind_speed() + " kph");
        mActivityBinding.tvVisibility.setText(currentWeatherObj.getVisibility() + " km");
        mActivityBinding.tvPressure.setText(currentWeatherObj.getPressure() + " mb");

        Picasso.with(MainActivity.this)
                .load(currentWeatherObj.getWeather_icons()[0])
                .fit()
                .centerCrop()
                .into(mActivityBinding.ivMood);

        String tip = getTip();
        MyLogs.LOG("MainActivity", "loadCurWeather", "tip = " + tip);
        if (tip != null) {
            mActivityBinding.tvTip.setVisibility(View.VISIBLE);
            mActivityBinding.tvNoTip.setVisibility(View.GONE);
            mActivityBinding.tvTip.setText(tip);

        } else {
            mActivityBinding.tvTip.setVisibility(View.GONE);
            mActivityBinding.tvNoTip.setVisibility(View.VISIBLE);
        }
    }

    private void onLoadLocationWeather(String locQuery) {
        String loc = DataFormatConverter.getCityName(locQuery);
        mActivityBinding.tvToolbarPlace.setText(loc);

        onProgressShow(mActivityBinding.progressBar);
        RequestManager.asyncGetForecastWeather(locQuery, MainActivity.this, MainActivity.this);
    }

    private void initMyLocation() {
        if (!PermissionsHelper.hasPermissions(PermissionsHelper.PERMISSIONS_LOCATION)) {
            ActivityCompat.requestPermissions(MainActivity.this, PermissionsHelper.PERMISSIONS_LOCATION, PermissionsHelper.PERMISSION_CODE);

        } else {
            getDeviceLocation();
        }
    }

    private void goToAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        if (PermissionsHelper.hasPermissions(PermissionsHelper.PERMISSIONS_LOCATION)) {
            LatLng myCurLoc = DataFormatConverter.getMyLocation();

            if (myCurLoc != null) {
                onLoadLocationWeather(myCurLoc.latitude + "," + myCurLoc.longitude);

            } else {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
            }
        }
    }

    private String getTip() {
        String encryptedTip = getIntent().getStringExtra(GenericConstants.EXTRA_TIP_KEY);
        return EncryptionUtil.safeDecryption(encryptedTip);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_more:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }
    }


    @Override
    public <T> void onDone(T obj) {
        onProgressDismiss(mActivityBinding.progressBar);
        mViewModel.setCurData((DataRs) obj);
    }

    @Override
    public void onErrNotify(String errStr) {
        onProgressDismiss(mActivityBinding.progressBar);
        showSnack(mActivityBinding.mainCoord, errStr, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == NEW_LOC_SELECTION_CODE && data != null) {
            String name = data.getStringExtra(GenericConstants.KEY_EXTRA_LOC_NAME);
            String coord = data.getStringExtra(GenericConstants.KEY_EXTRA_LOC_COORDONATES);

            if (coord != null)
                onLoadLocationWeather(coord);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionsHelper.PERMISSION_CODE) {
            if (PermissionsHelper.areAllPermisionsGranted(grantResults)) {
                initMyLocation();

            } else {
                CustomDialogs.createSimpleDialog(
                        MainActivity.this,
                        App.getAppCtx().getResources().getString(R.string.txt_warn),
                        App.getAppCtx().getResources().getString(R.string.txt_no_loc_permissions),
                        false,
                        App.getAppCtx().getResources().getString(R.string.txt_dismiss),
                        null,
                        null);
            }
        }
    }
}