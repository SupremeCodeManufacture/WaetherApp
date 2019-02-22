package view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.supreme.manufacture.weather.R;
import com.supreme.manufacture.weather.databinding.ActivityPlacesBinding;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import logic.helpers.MyLogs;

public class PlacesActivity extends BaseActivity implements View.OnClickListener {

    private ActivityPlacesBinding mActivityBinding;
    private long mLastPikerStarted;//ignore multiple open


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_places);

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
            Place place = PlacePicker.getPlace(this, data);
            String myAdress = place.getAddress() != null ? place.getAddress().toString() : "";
            double myLatitude = place.getLatLng().latitude;
            double myLongLng = place.getLatLng().longitude;

            MyLogs.LOG("PlacesActivity", "onActivityResult", "myAdress: " + myAdress);
        }
    }
}
