package view.activity;

import android.app.Activity;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.material.snackbar.Snackbar;


import com.supreme.manufacture.weather.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import data.App;
import logic.helpers.LocationUtils;
import logic.listeners.OnDualSelectionListener;
import view.custom.CustomDialogs;

public abstract class BaseActivity extends AppCompatActivity {

    protected final int PLACE_PICKER_REQUEST = 11;


    protected boolean isActivityValid() {
        return !isDestroyed() && !isFinishing();
    }

    public void showSnack(CoordinatorLayout coordinator, String txt, boolean isLongShow) {
        if (isActivityValid()) {
            Snackbar.make(coordinator, txt, isLongShow ? Snackbar.LENGTH_SHORT : Snackbar.LENGTH_LONG).show();
        }
    }

    protected void onProgressShow(ContentLoadingProgressBar mProgressBar) {
        if (isActivityValid() && mProgressBar != null && !mProgressBar.isShown()) {
            mProgressBar.show();
        }
    }

    protected void onProgressDismiss(ContentLoadingProgressBar mProgressBar) {
        if (mProgressBar != null) {
            mProgressBar.hide();
        }
    }

    private void rotateView(ImageView ivAroow, int fromDegree, int toDegree) {
        RotateAnimation rotate = new RotateAnimation(fromDegree, toDegree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(200);

        rotate.setFillAfter(true);
        ivAroow.startAnimation(rotate);
    }


    protected void onWarnLocOff() {
        if (!LocationUtils.isLocationEnabled() && isActivityValid()) {
            CustomDialogs.createSimpleDialog(
                    BaseActivity.this,
                    App.getAppCtx().getResources().getString(R.string.txt_warn),
                    App.getAppCtx().getResources().getString(R.string.txt_no_loc),
                    true,
                    App.getAppCtx().getResources().getString(R.string.txt_enable),
                    App.getAppCtx().getResources().getString(R.string.txt_dismiss),
                    new OnDualSelectionListener() {
                        @Override
                        public void onPositiveButtonClick() {
                            LocationUtils.openLocSettings();
                        }

                        @Override
                        public void onNegativeButtonClick() {

                        }
                    });
        }
    }

    protected void switchFrgFromActivity(Fragment fragment, boolean isAddedToBackStack, int idContainer, String frgTag) {
        if (!(BaseActivity.this.isFinishing())) {

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(idContainer, fragment);

            //add frg to system backstack
            if (isAddedToBackStack) {
                fragmentTransaction.addToBackStack(frgTag);
            }

            fragmentTransaction.commitAllowingStateLoss();
        }
    }


    protected void onStartPiker(Activity activityRef, CoordinatorLayout coordinatorLayout) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(activityRef), PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException e) {
            showSnack(coordinatorLayout, App.getAppCtx().getResources().getString(R.string.txt_oops), true);
            e.printStackTrace();

        } catch (GooglePlayServicesNotAvailableException e) {
            showSnack(coordinatorLayout, App.getAppCtx().getResources().getString(R.string.txt_oops), true);
            e.printStackTrace();
        }

    }
}