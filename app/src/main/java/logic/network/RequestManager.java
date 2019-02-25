package logic.network;


import com.supreme.manufacture.weather.R;

import java.io.IOException;

import data.App;
import data.model.DataRs;
import logic.async_await.OnAsyncDoneRsObjListener;
import logic.helpers.LangUtils;
import logic.helpers.MyLogs;
import logic.listeners.OnFetchDataErrListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RequestManager {

    public static void asyncGetForecastWeather(String q,
                                               String days,
                                               final OnAsyncDoneRsObjListener onDoneListener,
                                               final OnFetchDataErrListener errListener) {

        if (NetworkState.isNetworkAvailable()) {
            String key = App.getAppCtx().getResources().getString(R.string.api_key);
            String lang = LangUtils.getselectedLanguage();

            Call<DataRs> call = RestClient.get().getData(key, q, lang, days);
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
                            errListener.onErrNptify(ErrorHandler.getErrMsgFromRs(response));
                    }
                }

                @Override
                public void onFailure(Call<DataRs> call, Throwable t) {
                    MyLogs.LOG("RequestManager", "execAsyncReq", "onFailure --> ERROR: " + t.getMessage());
                    t.printStackTrace();
                    if (errListener != null)
                        errListener.onErrNptify(ErrorHandler.getFriendlyError(t));
                }
            });

        } else {
            MyLogs.LOG("RequestManager", "execAsyncReq", "onFailure --> WARNING no connection");
            if (errListener != null)
                errListener.onErrNptify(App.getAppCtx().getResources().getString(R.string.txt_err_no_network));
        }
    }


    public static DataRs syncGetCurWeather(String q) {
        if (NetworkState.isNetworkAvailable()) {
            String key = App.getAppCtx().getResources().getString(R.string.api_key);
            String lang = LangUtils.getselectedLanguage();

            try {
                Call<DataRs> call = RestClient.get().getData(key, q, lang, "1");
                Response<DataRs> response = call.execute();

                if (response != null && response.body() != null && response.isSuccessful()) {
                    MyLogs.LOG("RequestManager", "syncGetCurWeather", "onResponse --> code: " + response.code());
                    return response.body();

                } else {
                    MyLogs.LOG("RequestManager", "syncGetCurWeather", "onFailure --> ERROR something wrong with result");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new DataRs();
    }
}