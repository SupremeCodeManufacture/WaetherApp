package view.activity;

import android.os.Bundle;
import android.view.View;

import com.supreme.manufacture.weather.R;
import com.supreme.manufacture.weather.databinding.ActivityDaysWeatherBinding;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import data.App;
import data.GenericConstants;
import data.model.DataRs;
import data.model.ForecastDayObj;
import data.view_model.WeatherDetailsActivityViewModel;
import logic.adapters.DaysWeatherAdapter;
import logic.async_await.OnAsyncDoneRsObjListener;
import logic.helpers.ThemeColorsHelper;
import logic.listeners.OnFetchDataErrListener;
import logic.network.RequestManager;
import view.custom.WrapLayoutManager;

public class WeatherDetailsActivity extends BaseActivity implements
        OnAsyncDoneRsObjListener,
        View.OnClickListener,
        OnFetchDataErrListener {

    private WeatherDetailsActivityViewModel viewModel;
    private ActivityDaysWeatherBinding mActivityBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(ThemeColorsHelper.getTheme(App.isDAY()), true);
        mActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_days_weather);

        bindViewModel();

        mActivityBinding.toolbarDays.setBackgroundResource(ThemeColorsHelper.getColorPrimary(App.isDAY()));

        String locQuery = getIntent().getStringExtra(GenericConstants.KEY_EXTRA_LOC_COORDONATES);

        onProgressShow(mActivityBinding.progressBar);
        RequestManager.asyncGetForecastWeather(locQuery, "10", WeatherDetailsActivity.this, WeatherDetailsActivity.this);
    }


    private void loadDaysWeather(ForecastDayObj[] arrayj) {
        if (arrayj != null && arrayj.length > 0) {
            mActivityBinding.noContent.rlNoContent.setVisibility(View.GONE);
            mActivityBinding.rvItems.setVisibility(View.VISIBLE);

            mActivityBinding.rvItems.setAdapter(new DaysWeatherAdapter(WeatherDetailsActivity.this, arrayj));
            mActivityBinding.rvItems.setLayoutManager(new WrapLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
            mActivityBinding.rvItems.setHasFixedSize(true);

        } else {
            mActivityBinding.noContent.rlNoContent.setVisibility(View.VISIBLE);
            mActivityBinding.rvItems.setVisibility(View.GONE);
        }
    }

    private void bindViewModel() {
        viewModel = ViewModelProviders.of(WeatherDetailsActivity.this).get(WeatherDetailsActivityViewModel.class);
        viewModel.getForecasts().observe(this, new Observer<ForecastDayObj[]>() {
            @Override
            public void onChanged(ForecastDayObj[] forecastDayObjs) {
                loadDaysWeather(forecastDayObjs);
            }
        });
    }


    @Override
    public <T> void onDone(T obj) {
        onProgressDismiss(mActivityBinding.progressBar);

        DataRs dataRs = (DataRs) obj;
        if (dataRs != null)
            viewModel.setForecasts(dataRs.getForecast().getForecastday());
    }

    @Override
    public void onErrNptify(String errStr) {
        onProgressDismiss(mActivityBinding.progressBar);
        showSnack(mActivityBinding.detCoord, errStr, true);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                WeatherDetailsActivity.this.onBackPressed();
                break;
        }
    }

}
