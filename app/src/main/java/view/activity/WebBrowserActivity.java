package view.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.supreme.manufacture.weather.R;
import com.supreme.manufacture.weather.databinding.ActivityBrowserBinding;

import androidx.databinding.DataBindingUtil;
import data.App;
import data.GenericConstants;
import logic.helpers.MyLogs;
import logic.helpers.ThemeColorsHelper;
import logic.network.NetworkState;


public class WebBrowserActivity extends BaseActivity implements View.OnClickListener {

    private ActivityBrowserBinding mActivityBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(ThemeColorsHelper.getTheme(App.isDAY()), true);
        mActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_browser);

        setupClient();

        mActivityBinding.toolbarWeb.setBackgroundResource(ThemeColorsHelper.getColorPrimary(App.isDAY()));
        mActivityBinding.tvToolbarPlace.setText(getIntent().getStringExtra(GenericConstants.KEY_EXTRA_BROWSER_TITLE));
        loadHtmlData(getIntent().getStringExtra(GenericConstants.KEY_EXTRA_BROWSER_LINK));
    }

    private void setupClient() {
        mActivityBinding.web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
               onProgressShow(mActivityBinding.progressBar);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                MyLogs.LOG("WebBrowserActivity", "setupClient", "onPageFinished");
                onProgressDismiss(mActivityBinding.progressBar);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                MyLogs.LOG("WebBrowserActivity", "setupClient", "error: " + error.toString());
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                MyLogs.LOG("WebBrowserActivity", "setupClient", "error: " + errorResponse.toString());
                super.onReceivedHttpError(view, request, errorResponse);
            }
        });
    }

    private void loadHtmlData(String urlToLoad) {
        MyLogs.LOG("WebBrowserActivity", "loadHtmlData", "urlToLoad: " + urlToLoad);

        if (NetworkState.isNetworkAvailable()) {
            if (urlToLoad != null && URLUtil.isValidUrl(urlToLoad)) {
                mActivityBinding.web.loadUrl(urlToLoad);

            } else {
                loadError(getResources().getString(R.string.txt_oops));
            }

        } else {
            loadError(getResources().getString(R.string.txt_err_no_network));
        }
    }

    private void loadError(String errTxt) {
        onProgressDismiss(mActivityBinding.progressBar);
        mActivityBinding.web.setVisibility(View.GONE);
        mActivityBinding.zoneNoContent.setVisibility(View.VISIBLE);
        showSnack(mActivityBinding.coordinator, errTxt, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                WebBrowserActivity.this.onBackPressed();
            break;
        }
    }
}