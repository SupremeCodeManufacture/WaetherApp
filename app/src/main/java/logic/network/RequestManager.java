package logic.network;


import com.student.adminweather.R;

import data.App;
import data.SettingsPreferences;
import data.model.DataRs;
import logic.async_await.OnAsyncDoneRsObjListener;
import logic.helpers.MyLogs;
import logic.listeners.OnFetchDataErrListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RequestManager {

    public static void asyncGetForecastWeather(String q,
                                               final OnAsyncDoneRsObjListener onDoneListener,
                                               final OnFetchDataErrListener errListener) {

        if (NetworkState.isNetworkAvailable()) {
            String key = App.getAppCtx().getResources().getString(R.string.api_key);
            String selectedMetricSystem = SettingsPreferences.getSharedPrefsString(App.getAppCtx().getResources().getString(R.string.stg_sys_mes), "m");

            Call<DataRs> call = RestClient.get().getData(key, q, selectedMetricSystem);
            call.enqueue(new Callback<DataRs>() {
                @Override
                public void onResponse(Call<DataRs> call, final Response<DataRs> response) {
                    if (response.isSuccessful()) {
                        MyLogs.LOG("RequestManager", "execAsyncReq", "isSuccessful");

                        if (onDoneListener != null)
                            onDoneListener.onDone(response.body());

                    } else {
                        MyLogs.LOG("RequestManager", "execAsyncReq", "onResponse --> ERROR not succesfull result");
                        if (errListener != null)
                            errListener.onErrNotify(ErrorHandler.getErrMsgFromRs(response));
                    }
                }

                @Override
                public void onFailure(Call<DataRs> call, Throwable t) {
                    MyLogs.LOG("RequestManager", "execAsyncReq", "onFailure --> ERROR: " + t.getMessage());
                    t.printStackTrace();
                    if (errListener != null)
                        errListener.onErrNotify(ErrorHandler.getFriendlyError(t));
                }
            });

        } else {
            MyLogs.LOG("RequestManager", "execAsyncReq", "onFailure --> WARNING no connection");
            if (errListener != null)
                errListener.onErrNotify(App.getAppCtx().getResources().getString(R.string.txt_err_no_network));
        }
    }
}