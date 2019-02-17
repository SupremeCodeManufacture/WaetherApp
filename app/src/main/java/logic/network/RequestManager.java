package logic.network;


import com.SupremeManufacture.weather.R;

import java.util.List;
import java.util.Map;

import data.App;
import logic.async_await.OnAsyncDoneRsObjListener;
import logic.helpers.DataFormatConverter;
import logic.helpers.MyLogs;
import logic.listeners.OnFetchDataErrListener;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RequestManager {


    public static void execGetSalons(String reqPath,
                                     Map<String, String> queryParams,
                                     List<String> filters,
                                     OnAsyncDoneRsObjListener onDoneListener,
                                     OnFetchDataErrListener errListener) {
        Call<ResponseBody> call = RestClient.get().getSalonsReq(reqPath, queryParams, filters);
        execAsyncReq(call, onDoneListener, errListener);
    }

    public static void execGetSalons(String reqPath,
                                     OnAsyncDoneRsObjListener onDoneListener,
                                     OnFetchDataErrListener errListener) {
        Call<ResponseBody> call = RestClient.get().getReq(reqPath);
        execAsyncReq(call, onDoneListener, errListener);
    }

    public static void execPost(final String reqPath,
                                final Map<String, String> params,
                                final OnAsyncDoneRsObjListener onDoneListener,
                                final OnFetchDataErrListener errListener) {
        Call<ResponseBody> call = RestClient.get().postReqNew(reqPath, params);
        execAsyncReq(call, onDoneListener, errListener);
    }

    private static void execAsyncReq(Call<ResponseBody> call,
                                     final OnAsyncDoneRsObjListener onDoneListener,
                                     final OnFetchDataErrListener errListener) {
        if (NetworkState.isNetworkAvailable()) {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        MyLogs.LOG("RequestManager", "execAsyncReq", "isSuccessful");

//                        DataBindUtil.asyncHandleSuccessReq(response, onDoneListener);
                        String jsonStr = DataFormatConverter.getJsonRs(response);

                        if (onDoneListener != null)
                            onDoneListener.onDone(jsonStr);

                    } else {
                        MyLogs.LOG("RequestManager", "execAsyncReq", "onResponse --> ERROR not succesfull result");
                        if (errListener != null)
                            errListener.onErrNptify(ErrorHandler.getErrMsgFromRs(response));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
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