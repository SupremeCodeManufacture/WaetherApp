package logic.network;


import com.supreme.manufacture.weather.R;

import data.App;
import data.model.DataRs;
import logic.async_await.OnAsyncDoneRsObjListener;
import logic.helpers.MyLogs;
import logic.listeners.OnFetchDataErrListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RequestManager {

    public static void execGetSalons(String q,
                                     final OnAsyncDoneRsObjListener onDoneListener,
                                     final OnFetchDataErrListener errListener) {

        if (NetworkState.isNetworkAvailable()) {
            String key = App.getAppCtx().getResources().getString(R.string.api_key);
            String lang = "en";//todo get user selected lang
            String days = "3";

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
}