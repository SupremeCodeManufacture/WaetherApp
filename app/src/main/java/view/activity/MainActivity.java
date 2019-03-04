package view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.soloviof.easyads.AdsRepo;
import com.soloviof.easyads.InitApp;
import com.soloviof.easyads.InterstitialAddsHelper;
import com.squareup.picasso.Picasso;
import com.supreme.manufacture.weather.R;
import com.supreme.manufacture.weather.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import data.App;
import data.GenericConstants;
import data.SettingsPreferences;
import data.model.CurrentWeatherObj;
import data.model.DataRs;
import data.model.ForecastDayObj;
import data.model.ForecastObj;
import data.model.HourWeatherObj;
import data.view_model.MainActivityViewModel;
import logic.adapters.HoursWeatherAdapter;
import logic.async_await.AsyncTaskWorker;
import logic.async_await.CallableObj;
import logic.async_await.OnAsyncDoneRsObjListener;
import logic.helpers.DataFormatConverter;
import logic.helpers.PermissionsHelper;
import logic.helpers.ThemeColorsHelper;
import logic.listeners.OnDualSelectionListener;
import logic.listeners.OnFetchDataErrListener;
import logic.network.RequestManager;
import logic.payment.PaymentHelper;
import logic.payment.util.IabResult;
import view.custom.CustomDialogs;
import view.custom.WrapLayoutManager;

public class MainActivity extends BaseActivity implements
        View.OnClickListener,
        OnAsyncDoneRsObjListener,
        OnFetchDataErrListener {

    private ActivityMainBinding mActivityBinding;
    private MainActivityViewModel mViewModel;
    private static final int NEW_LOC_SELECTION_CODE = 567;
    private String mCoordQuery;


    final LocationManager mLocationManager = (LocationManager) App.getAppCtx().getSystemService(Context.LOCATION_SERVICE);
    final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (isActivityValid()) {
                //MyLogs.LOG("MainActivity", "mLocationListener", "location: " + location.toString());
                onLoadLocationWeather("", location.getLatitude() + "," + location.getLongitude());
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
        if (App.getSelectedLoc() != null) onLoadLocationWeather("", App.getSelectedLoc());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(ThemeColorsHelper.getTheme(App.isDAY()), true);
        mActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        manageDayNightColors();

        bindViewModel();

        InitApp.doFirebaseInit(MainActivity.this, AdsRepo.getAppId(App.getAppCtx(), App.getAppBuilds(), App.getAppCtx().getResources().getString(R.string.ads_app_id)));

        if (App.getSelectedLoc() == null)
            CustomDialogs.createSimpleDialog(
                    MainActivity.this,
                    App.getAppCtx().getResources().getString(R.string.txt_warn),
                    App.getAppCtx().getResources().getString(R.string.txt_ask_loc),
                    true,
                    App.getAppCtx().getResources().getString(R.string.txt_allow),
                    App.getAppCtx().getResources().getString(R.string.txt_set_manually),
                    new OnDualSelectionListener() {
                        @Override
                        public void onPositiveButtonClick() {
                            initMyLocation();
                        }

                        @Override
                        public void onNegativeButtonClick() {
                            startActivityForResult(new Intent(MainActivity.this, PlacesActivity.class), NEW_LOC_SELECTION_CODE);
                        }
                    });

        //init here to get status for further game activity
        PaymentHelper.setUpPayments(MainActivity.this, MainActivity.this);

        handleDemoOrProViews();
    }


    private void handleDemoOrProViews() {
        if (App.isPaidFull() || App.isPaidUnlock()) {
            mActivityBinding.zoneTodayHours.setVisibility(View.VISIBLE);

        } else {
            mActivityBinding.zoneTodayHours.setVisibility(View.GONE);
        }

        setupAdBanner(mActivityBinding.zoneBanner.llBanner, MainActivity.this, "home screen");

        InterstitialAddsHelper.prepareInterstitialAds(
                MainActivity.this,
                App.getAppBuilds(),
                App.getAppCtx().getResources().getString(R.string.banner_id_interstitial));
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
        mActivityBinding.tvToolbarPlace.setText(dataRs.getLocation().getName());

        loadCurWeather(dataRs.getCurrent());

        if (App.isPaidFull() || App.isPaidUnlock()) {
            new AsyncTaskWorker(
                    new CallableObj<List<HourWeatherObj>>() {
                        public List<HourWeatherObj> call() {
                            return DataFormatConverter.getTodayHoursWeather(dataRs.getForecast());
                        }
                    },
                    new OnAsyncDoneRsObjListener() {
                        @Override
                        public <T> void onDone(T t) {
                            List<HourWeatherObj> list = t != null ? (List<HourWeatherObj>) t : null;
                            loadTodaysHoursWeather(list);
                        }
                    }
            ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } else {
            mActivityBinding.zoneTodayHours.setVisibility(View.GONE);
        }

        loadDaysWeather(dataRs.getForecast());
    }

    private void loadCurWeather(CurrentWeatherObj currentWeatherObj) {
        mActivityBinding.tvMoodType.setText(currentWeatherObj.getCondition().getText());
        mActivityBinding.tvUvIndex.setText(String.valueOf(currentWeatherObj.getUv()));
        mActivityBinding.tvHum.setText(String.valueOf(currentWeatherObj.getHumidity()) + "%");

        //temp
        String degreeType = SettingsPreferences.getSharedPrefsString(App.getAppCtx().getResources().getString(R.string.stg_temp), "°C");

        String tempCur = degreeType.equals(App.getAppCtx().getResources().getStringArray(R.array.temp_messures_values)[0]) ? String.valueOf(currentWeatherObj.getTemp_c()) : String.valueOf(currentWeatherObj.getTemp_f());
        mActivityBinding.tvTemp.setText(tempCur);
        mActivityBinding.tvDegreeType.setText(degreeType);

        String tempFeels = degreeType.equals(App.getAppCtx().getResources().getStringArray(R.array.temp_messures_values)[0]) ? String.valueOf(currentWeatherObj.getFeelslike_c()) : String.valueOf(currentWeatherObj.getFeelslike_f());
        mActivityBinding.tvFeels.setText(tempFeels + degreeType);

        //wind
        String speedType = SettingsPreferences.getSharedPrefsString(App.getAppCtx().getResources().getString(R.string.stg_wind_speed), "kph");
        String speed = speedType.equals(App.getAppCtx().getResources().getStringArray(R.array.wind_messures_values)[0]) ? String.valueOf(currentWeatherObj.getWind_kph()) : String.valueOf(currentWeatherObj.getWind_mph());
        mActivityBinding.tvWind.setText(speed + " " + speedType);

        String visType = speedType.equals(App.getAppCtx().getResources().getStringArray(R.array.wind_messures_values)[0]) ? "km" : "miles";
        String vis = speedType.equals(App.getAppCtx().getResources().getStringArray(R.array.wind_messures_values)[0]) ? String.valueOf(currentWeatherObj.getVis_km()) : String.valueOf(currentWeatherObj.getVis_miles());
        mActivityBinding.tvVisibility.setText(vis + " " + visType);

        //press
        String pressType = SettingsPreferences.getSharedPrefsString(App.getAppCtx().getResources().getString(R.string.stg_pressure), "mb");
        String press = pressType.equals(App.getAppCtx().getResources().getStringArray(R.array.press_messures_values)[0]) ? String.valueOf(currentWeatherObj.getPressure_mb()) : String.valueOf(currentWeatherObj.getPressure_in());
        mActivityBinding.tvPressure.setText(press + " " + pressType);

    }

    private void loadTodaysHoursWeather(List<HourWeatherObj> list) {
        if (list != null && list.size() > 0) {
            mActivityBinding.zoneTodayHours.setVisibility(View.VISIBLE);

            mActivityBinding.rvWeather24Items.setAdapter(new HoursWeatherAdapter(list));
            mActivityBinding.rvWeather24Items.setLayoutManager(new WrapLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
            mActivityBinding.rvWeather24Items.setHasFixedSize(true);

        } else {
            mActivityBinding.zoneTodayHours.setVisibility(View.GONE);
        }
    }

    private void loadDaysWeather(ForecastObj forecastObj) {
        ForecastDayObj[] forecastdays = forecastObj.getForecastday();
        String degreeType = SettingsPreferences.getSharedPrefsString(App.getAppCtx().getResources().getString(R.string.stg_temp), "°C");

        if (forecastdays.length == 3) {
            String todayMood = App.getAppCtx().getResources().getString(R.string.txt_today) + " • " + forecastdays[0].getDay().getCondition().getText();
            mActivityBinding.tvMoodToday.setText(todayMood);

            String tempMinToday = degreeType.equals(App.getAppCtx().getResources().getStringArray(R.array.temp_messures_values)[0]) ? String.valueOf(forecastdays[0].getDay().getMintemp_c()) : String.valueOf(forecastdays[0].getDay().getMintemp_f());
            mActivityBinding.tvTempMinToday.setText(tempMinToday + degreeType);

            String tempMaxToday = degreeType.equals(App.getAppCtx().getResources().getStringArray(R.array.temp_messures_values)[0]) ? String.valueOf(forecastdays[0].getDay().getMaxtemp_c()) : String.valueOf(forecastdays[0].getDay().getMaxtemp_f());
            mActivityBinding.tvTempMaxToday.setText(tempMaxToday + degreeType);

            Picasso.with(MainActivity.this)
                    .load("http://" + forecastdays[0].getDay().getCondition().getIcon())
                    .fit()
                    .centerCrop()
                    .into(mActivityBinding.ivMmodToday);


            String tomorowMood = App.getAppCtx().getResources().getString(R.string.txt_tomorrow) + " • " + forecastdays[1].getDay().getCondition().getText();
            mActivityBinding.tvMoodTomorrow.setText(tomorowMood);

            String tempMinTomorrow = degreeType.equals(App.getAppCtx().getResources().getStringArray(R.array.temp_messures_values)[0]) ? String.valueOf(forecastdays[1].getDay().getMintemp_c()) : String.valueOf(forecastdays[1].getDay().getMintemp_f());
            mActivityBinding.tvTempMinTomorrow.setText(tempMinTomorrow + degreeType);

            String tempMaxTomorrow = degreeType.equals(App.getAppCtx().getResources().getStringArray(R.array.temp_messures_values)[0]) ? String.valueOf(forecastdays[1].getDay().getMaxtemp_c()) : String.valueOf(forecastdays[1].getDay().getMaxtemp_f());
            mActivityBinding.tvTempMaxTomorrow.setText(tempMaxTomorrow + degreeType);

            Picasso.with(MainActivity.this)
                    .load("http://" + forecastdays[1].getDay().getCondition().getIcon())
                    .fit()
                    .centerCrop()
                    .into(mActivityBinding.ivMmodTomorrow);


            String afterTomWeekDay = DataFormatConverter.getPrettyWeekDay(forecastdays[2].getDate());
            String afterTomorrowMood = afterTomWeekDay + " • " + forecastdays[2].getDay().getCondition().getText();
            mActivityBinding.tvMoodAftTomorrow.setText(afterTomorrowMood);

            String tempMinAftTomorrow = degreeType.equals(App.getAppCtx().getResources().getStringArray(R.array.temp_messures_values)[0]) ? String.valueOf(forecastdays[2].getDay().getMintemp_c()) : String.valueOf(forecastdays[2].getDay().getMintemp_f());
            mActivityBinding.tvTempMinAftTomorrow.setText(tempMinAftTomorrow + degreeType);

            String tempMaxAftTomorrow = degreeType.equals(App.getAppCtx().getResources().getStringArray(R.array.temp_messures_values)[0]) ? String.valueOf(forecastdays[2].getDay().getMaxtemp_c()) : String.valueOf(forecastdays[2].getDay().getMaxtemp_f());
            mActivityBinding.tvTempMaxAftTomorrow.setText(tempMaxAftTomorrow + degreeType);

            Picasso.with(MainActivity.this)
                    .load("http://" + forecastdays[2].getDay().getCondition().getIcon())
                    .fit()
                    .centerCrop()
                    .into(mActivityBinding.ivMmodAftTomorrow);
        }
    }

    private void onLoadLocationWeather(String locName, String locQuery) {
        mActivityBinding.tvToolbarPlace.setText(locName);
        mCoordQuery = locQuery;
        App.setSelectedLoc(locQuery);

        onProgressShow(mActivityBinding.progressBar);
        RequestManager.asyncGetForecastWeather(locQuery, "3", MainActivity.this, MainActivity.this);
    }

    private void initMyLocation() {
        if (!PermissionsHelper.hasPermissions(PermissionsHelper.PERMISSIONS_LOCATION)) {
            ActivityCompat.requestPermissions(MainActivity.this, PermissionsHelper.PERMISSIONS_LOCATION, PermissionsHelper.PERMISSION_CODE);

        } else {
            getDeviceLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        if (PermissionsHelper.hasPermissions(PermissionsHelper.PERMISSIONS_LOCATION)) {
            LatLng myCurLoc = DataFormatConverter.getMyLocation();

            if (myCurLoc != null) {
                onLoadLocationWeather("", myCurLoc.latitude + "," + myCurLoc.longitude);

            } else {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_details:
                Intent intentAbout = new Intent(MainActivity.this, WebBrowserActivity.class);
                intentAbout.putExtra(GenericConstants.KEY_EXTRA_BROWSER_TITLE, App.getAppCtx().getResources().getString(R.string.txt_det_weather));
                intentAbout.putExtra(GenericConstants.KEY_EXTRA_BROWSER_LINK, "https://www.apixu.com/weather/");
                startActivity(intentAbout);
                break;

            case R.id.tv_next_days:
                Intent intent = new Intent(MainActivity.this, WeatherDetailsActivity.class);
                intent.putExtra(GenericConstants.KEY_EXTRA_LOC_COORDONATES, mCoordQuery);
                startActivity(intent);
                break;

            case R.id.btn_add:
                startActivityForResult(new Intent(MainActivity.this, PlacesActivity.class), NEW_LOC_SELECTION_CODE);
                break;

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
    public void onErrNptify(String errStr) {
        onProgressDismiss(mActivityBinding.progressBar);
        showSnack(mActivityBinding.mainCoord, errStr, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == NEW_LOC_SELECTION_CODE && data != null) {
            String name = data.getStringExtra(GenericConstants.KEY_EXTRA_LOC_NAME);
            String coord = data.getStringExtra(GenericConstants.KEY_EXTRA_LOC_COORDONATES);

            if (name != null && coord != null)
                onLoadLocationWeather(name, coord);

            if (!App.isPaidFull() && !App.isPaidAds())
                InterstitialAddsHelper.tryShowInterstitialAdNow(true);
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


    @Override
    void decideDemoOrPro() {
        handleDemoOrProViews();

        //reload to get hourly
        if (App.getSelectedLoc() != null) onLoadLocationWeather("", App.getSelectedLoc());
    }

    //=============================== PAYMENTS FUNCTIONAL ========================================//
    @Override
    public void onIabSetupFinished(IabResult result) {
        if (result.isSuccess()) {
            //MyLogs.LOG("MainActivity", "onIabSetupFinished", "Setting up In-app Billing succesfull");
            PaymentHelper.getLifePaymentStatus(App.getPaymentHelper(), MainActivity.this);

        } else {
            //MyLogs.LOG("MainActivity", "onIabSetupFinished", "Problem setting up In-app Billing: " + result);
        }
    }
}