package view.activity;

import android.content.Context;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.ContentLoadingProgressBar;

import com.google.android.material.snackbar.Snackbar;
import com.student.adminweather.R;

import data.App;
import logic.helpers.LangUtils;
import logic.helpers.LocationUtils;
import logic.helpers.MyContextWrapper;
import logic.listeners.OnDualSelectionListener;
import view.custom.CustomDialogs;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MyContextWrapper.wrap(newBase, LangUtils.getselectedLanguage()));
    }

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
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.show();
        }
    }

    protected void onProgressDismiss(ContentLoadingProgressBar mProgressBar) {
        if (mProgressBar != null) {
            mProgressBar.hide();
        }
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
}