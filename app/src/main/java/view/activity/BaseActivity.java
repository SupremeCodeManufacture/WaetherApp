package view.activity;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.material.snackbar.Snackbar;
import com.soloviof.easyads.AdsRepo;
import com.soloviof.easyads.CustomizeAds;
import com.supreme.manufacture.weather.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.ContentLoadingProgressBar;
import data.App;
import data.GenericConstants;
import logic.helpers.LangUtils;
import logic.helpers.LocationUtils;
import logic.helpers.MyContextWrapper;
import logic.helpers.MyLogs;
import logic.listeners.OnDualSelectionListener;
import logic.listeners.OnPayListener;
import logic.payment.PaymentHelper;
import logic.payment.util.IabHelper;
import logic.payment.util.IabResult;
import logic.payment.util.Inventory;
import logic.payment.util.Purchase;
import view.custom.CustomDialogs;
import view.custom.UpgradeDialog;

public abstract class BaseActivity extends AppCompatActivity implements
        IabHelper.OnIabSetupFinishedListener,
        IabHelper.OnIabPurchaseFinishedListener,
        IabHelper.QueryInventoryFinishedListener {

    protected final int PLACE_PICKER_REQUEST = 11;

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

    //needed to update views status for payments
    abstract void decideDemoOrPro();

    protected void showUpgradeDialog() {
        new UpgradeDialog(BaseActivity.this, new OnPayListener() {
            @Override
            public void onPayNoAds() {
                PaymentHelper.doLifePayment(BaseActivity.this, App.getPaymentHelper(), GenericConstants.KEY_IN_APP_NO_ADS_SKU_ID, BaseActivity.this);
            }

            @Override
            public void onPayUnlock() {
                PaymentHelper.doLifePayment(BaseActivity.this, App.getPaymentHelper(), GenericConstants.KEY_IN_APP_NO_LOCK_SKU_ID, BaseActivity.this);
            }

            @Override
            public void onPayFull() {
                PaymentHelper.doLifePayment(BaseActivity.this, App.getPaymentHelper(), GenericConstants.KEY_IN_APP_FULL_ID, BaseActivity.this);
            }
        }).show();
    }

    protected void setupAdBanner(LinearLayout bannerHolder, Activity activityRef, String bannerName) {
        if (!App.isPaidFull() && !App.isPaidAds()) {
            bannerHolder.setVisibility(View.VISIBLE);

            CustomizeAds.setupAddBanner(
                    activityRef,
                    bannerHolder,
                    AdSize.SMART_BANNER,
                    AdsRepo.getBannerId1(App.getAppCtx(), App.getAppBuilds(), App.getAppCtx().getResources().getString(R.string.banner_id)),
                    bannerName);

        } else {
            bannerHolder.setVisibility(View.GONE);
        }
    }

    //=============================== PAYMENTS FUNCTIONAL ========================================//
    @Override
    public void onIabSetupFinished(IabResult result) {
        //implemented in SettingsesActivity
    }

    @Override
    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
        if (result.isSuccess()) {
            boolean isPaidAds = inventory.hasPurchase(GenericConstants.KEY_IN_APP_NO_ADS_SKU_ID);
            boolean isPaidLock = inventory.hasPurchase(GenericConstants.KEY_IN_APP_NO_LOCK_SKU_ID);
            boolean isPaidFull = inventory.hasPurchase(GenericConstants.KEY_IN_APP_FULL_ID);
            //MyLogs.LOG("TablesActivity", "onQueryInventoryFinished", " isPaidAds: " + isPaidAds + " isPaidLock: " + isPaidLock + " isPaidFull: " + isPaidFull);

            App.setPaidAds(isPaidAds);
            App.setPaidUnlock(isPaidLock);
            App.setPaidFull(isPaidFull);

            decideDemoOrPro();

        } else {
            //MyLogs.LOG("TablesActivity", "onQueryInventoryFinished", "Error query inventory: " + result);
        }
    }

    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
        if (result.isSuccess()) {
            //MyLogs.LOG("SettingsActivity", "onIabPurchaseFinished", "purchese SKU: " + purchase.getSku());

            switch (purchase.getSku()) {
                case GenericConstants.KEY_IN_APP_NO_ADS_SKU_ID:
                    App.setPaidAds(true);
                    break;

                case GenericConstants.KEY_IN_APP_NO_LOCK_SKU_ID:
                    App.setPaidUnlock(true);
                    break;

                case GenericConstants.KEY_IN_APP_FULL_ID:
                    App.setPaidFull(true);
                    break;
            }

            decideDemoOrPro();
            Toast.makeText(BaseActivity.this, App.getAppCtx().getResources().getString(R.string.txt_worning_pro_done), Toast.LENGTH_LONG).show();

        } else {
            //MyLogs.LOG("SettingsActivity", "onIabPurchaseFinished", "Error purchasing: " + result);
        }
    }
}