package view.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.soloviof.easyads.InterstitialAddsHelper;
import com.supreme.manufacture.weather.R;
import com.supreme.manufacture.weather.databinding.ActivityPlacesBinding;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import data.App;
import data.DataBase;
import data.GenericConstants;
import data.model.DataRs;
import data.model.LocationObj;
import data.view_model.PlacesActivityViewModel;
import logic.adapters.LocationsAdapter;
import logic.async_await.AsyncTaskWorker;
import logic.async_await.CallableObj;
import logic.async_await.OnAsyncDoneNoRsListener;
import logic.async_await.OnAsyncDoneRsObjListener;
import logic.helpers.DataFormatConverter;
import logic.helpers.ThemeColorsHelper;
import logic.listeners.OnDualSelectionListener;
import logic.listeners.OnLocationSelectedListener;
import logic.network.RequestManager;
import view.custom.CustomDialogs;
import view.custom.WrapLayoutManager;

public class PlacesActivity extends BaseActivity implements
        View.OnClickListener,
        OnLocationSelectedListener {

    private PlacesActivityViewModel placesActivityViewModel;
    private ActivityPlacesBinding mActivityBinding;
    private LocationsAdapter mLocationsAdapter;
    private long mLastPikerStarted;//ignore multiple open


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(ThemeColorsHelper.getTheme(App.isDAY()), true);
        mActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_places);

        bindViewModel();

        mActivityBinding.toolbarPlaces.setBackgroundResource(ThemeColorsHelper.getColorPrimary(App.isDAY()));

        onProgressShow(mActivityBinding.progressBar);
        asyncLoadLocations();

        setupAdBanner(mActivityBinding.zoneBanner.llBanner, PlacesActivity.this, "places screen");

        InterstitialAddsHelper.prepareInterstitialAds(
                PlacesActivity.this,
                App.getAppBuilds(),
                App.getAppCtx().getResources().getString(R.string.banner_id_interstitial));
    }


    private void loadItems(List<LocationObj> locationObjs) {
        if (locationObjs != null && locationObjs.size() > 0) {
            mActivityBinding.noContent.rlNoContent.setVisibility(View.GONE);
            mActivityBinding.rvItems.setVisibility(View.VISIBLE);

            mLocationsAdapter = new LocationsAdapter(PlacesActivity.this, locationObjs, PlacesActivity.this);
            mActivityBinding.rvItems.setAdapter(mLocationsAdapter);
            mActivityBinding.rvItems.setLayoutManager(new WrapLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            mActivityBinding.rvItems.setHasFixedSize(true);

        } else {
            mActivityBinding.noContent.rlNoContent.setVisibility(View.VISIBLE);
            mActivityBinding.rvItems.setVisibility(View.GONE);
        }
    }

    private void bindViewModel() {
        placesActivityViewModel = ViewModelProviders.of(PlacesActivity.this).get(PlacesActivityViewModel.class);
        placesActivityViewModel.getMyLocations().observe(this, new Observer<List<LocationObj>>() {
            @Override
            public void onChanged(final List<LocationObj> locationObjs) {
                loadItems(locationObjs);
            }
        });
    }

    private void asyncLoadLocations() {
        new AsyncTaskWorker(
                new CallableObj<List<LocationObj>>() {
                    public List<LocationObj> call() {
                        List<LocationObj> list = DataBase.getInstance(App.getAppCtx()).selectAllLocations();

                        for (LocationObj locationObj : list) {
                            DataRs dataRs = RequestManager.syncGetCurWeather(locationObj.getLat() + "," + locationObj.getLon());
                            if (dataRs != null) {
                                locationObj.setCurrentWeatherObj(dataRs.getCurrent());
                                locationObj.setForecastObj(dataRs.getForecast());
                            }
                        }

                        return list;
                    }
                },
                new OnAsyncDoneRsObjListener() {
                    @Override
                    public <T> void onDone(T t) {
                        onProgressDismiss(mActivityBinding.progressBar);

                        if (t != null) {
                            placesActivityViewModel.setMyLocations((List<LocationObj>) t);
                        }
                    }
                }
        ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void asyncAddLocation(final Place place) {
        onProgressShow(mActivityBinding.progressBar);

        new AsyncTaskWorker(
                new CallableObj<LocationObj>() {
                    public LocationObj call() {
                        LocationObj locationObj = DataFormatConverter.hetLocationFromPalce(place);

                        if (locationObj != null) {
                            DataBase dataBase = DataBase.getInstance(App.getAppCtx());
                            dataBase.insertUpdateLocation(locationObj);

                            DataRs dataRs = RequestManager.syncGetCurWeather(locationObj.getLat() + "," + locationObj.getLon());
                            if (dataRs != null) {
                                locationObj.setCurrentWeatherObj(dataRs.getCurrent());
                                locationObj.setForecastObj(dataRs.getForecast());
                            }

                            return locationObj;
                        }

                        return null;
                    }
                },
                new OnAsyncDoneRsObjListener() {
                    @Override
                    public <T> void onDone(T t) {
                        if (t != null) {
                            onProgressDismiss(mActivityBinding.progressBar);

                            if (mLocationsAdapter == null) {
                                List<LocationObj> list = new ArrayList<>();
                                list.add((LocationObj) t);
                                placesActivityViewModel.setMyLocations(list);

                            } else {
                                mLocationsAdapter.onAddItem((LocationObj) t);
                                mActivityBinding.rvItems.smoothScrollToPosition(0);

                                mActivityBinding.noContent.rlNoContent.setVisibility(View.GONE);
                                mActivityBinding.rvItems.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
        ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void asyncDellLocation(final String locId, final int deletedPos) {
        new AsyncTaskWorker(
                new CallableObj<Void>() {
                    public Void call() {
                        DataBase.getInstance(App.getAppCtx()).deleteLocation(locId);
                        return null;
                    }
                },
                new OnAsyncDoneNoRsListener() {
                    @Override
                    public void onDone() {
                        mLocationsAdapter.onDeleteLoc(deletedPos);
                    }
                }
        ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void asyncUpdateSelection(final LocationObj locationObj) {
        new AsyncTaskWorker(
                new CallableObj<Void>() {
                    public Void call() {
                        DataBase.getInstance(App.getAppCtx()).updateSelectedLocation(locationObj.getId());
                        return null;
                    }
                },
                new OnAsyncDoneNoRsListener() {
                    @Override
                    public void onDone() {
                        Intent intent = new Intent();
                        intent.putExtra(GenericConstants.KEY_EXTRA_LOC_COORDONATES, locationObj.getLat() + "," + locationObj.getLon());
                        intent.putExtra(GenericConstants.KEY_EXTRA_LOC_NAME, locationObj.getName());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
        ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                PlacesActivity.this.onBackPressed();
                break;

            case R.id.btn_add:
                if (System.currentTimeMillis() > mLastPikerStarted + 2000) {
                    mLastPikerStarted = System.currentTimeMillis();
                    onStartPiker(PlacesActivity.this, mActivityBinding.placesCoord);
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == PLACE_PICKER_REQUEST) {
            asyncAddLocation(PlacePicker.getPlace(this, data));

            if (DataFormatConverter.isPassedAdsFree() && !App.isPaidFull() && !App.isPaidAds())
                InterstitialAddsHelper.tryShowInterstitialAdNow(true);
        }
    }


    @Override
    public void onLocationSelectedListener(LocationObj locationObj) {
        //MyLogs.LOG("PlacesActivity", "onLocationSelectedListener", "locationObj====> " + locationObj.getName());
        asyncUpdateSelection(locationObj);
    }

    @Override
    public void onLocDeletedListener(final LocationObj locationObj, final int pos) {
        CustomDialogs.createSimpleDialog(
                PlacesActivity.this,
                App.getAppCtx().getResources().getString(R.string.txt_warn),
                App.getAppCtx().getResources().getString(R.string.txt_del_loc),
                true,
                App.getAppCtx().getResources().getString(android.R.string.yes),
                App.getAppCtx().getResources().getString(android.R.string.cancel),
                new OnDualSelectionListener() {
                    @Override
                    public void onPositiveButtonClick() {
                        asyncDellLocation(locationObj.getId(), pos);
                    }

                    @Override
                    public void onNegativeButtonClick() {

                    }
                });
    }

    @Override
    public void onEmptyLocations() {
        mActivityBinding.noContent.rlNoContent.setVisibility(View.VISIBLE);
        mActivityBinding.rvItems.setVisibility(View.GONE);
    }


    @Override
    void decideDemoOrPro() {
        //no need to implement
    }
}
