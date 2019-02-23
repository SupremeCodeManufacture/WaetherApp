package view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.location.places.ui.PlacePicker;
import com.supreme.manufacture.weather.R;
import com.supreme.manufacture.weather.databinding.ActivityPlacesBinding;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import data.App;
import data.model.LocationObj;
import data.view_model.PlacesActivityViewModel;
import logic.adapters.LocationsAdapter;
import logic.helpers.DataFormatConverter;
import logic.helpers.MyLogs;
import logic.listeners.OnDualSelectionListener;
import logic.listeners.OnLocationSelectedListener;
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
        mActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_places);

        bindViewModel();
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


    private void loadItems(List<LocationObj> locationObjs) {
        if (locationObjs != null) {
            mActivityBinding.noContent.rlNoContent.setVisibility(View.GONE);
            mActivityBinding.rvItems.setVisibility(View.VISIBLE);

            mLocationsAdapter = new LocationsAdapter(locationObjs, PlacesActivity.this);
            mActivityBinding.rvItems.setAdapter(mLocationsAdapter);
            mActivityBinding.rvItems.setLayoutManager(new WrapLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
            mActivityBinding.rvItems.setHasFixedSize(true);

        } else {
            mActivityBinding.noContent.rlNoContent.setVisibility(View.VISIBLE);
            mActivityBinding.rvItems.setVisibility(View.GONE);
        }
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
            LocationObj locationObj = DataFormatConverter.hetLocationFromPalce(PlacePicker.getPlace(this, data));
            placesActivityViewModel.addLocation(locationObj);
        }
    }


    @Override
    public void onLocationSelectedListener(LocationObj locationObj) {
        MyLogs.LOG("PlacesActivity", "onLocationSelectedListener", "locationObj====> " + locationObj.getName());

        // TODO: 23/02/2019
    }

    @Override
    public void onLocDeletedListener(LocationObj locationObj, final int pos) {
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
                        mLocationsAdapter.onDeleteLoc(pos);
                        DataFormatConverter.updateAllLocations(mLocationsAdapter.getAllItems());
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
}
