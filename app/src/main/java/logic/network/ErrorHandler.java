package logic.network;

import com.google.gson.Gson;
import com.supreme.manufacture.weather.R;

import data.App;
import data.model.DataRs;
import data.model.ErrObj;
import retrofit2.Response;


public class ErrorHandler {

    public static String getFriendlyError(Throwable throwable) {
        if (throwable != null && throwable.getMessage() != null && throwable.getMessage().contains("Unable to resolve host"))
            return App.getAppCtx().getResources().getString(R.string.txt_upgrade_connectin);

        return App.getAppCtx().getResources().getString(R.string.txt_oops);
    }

    public static String getErrMsgFromRs(Response<DataRs> response) {
        if (response != null && response.errorBody() != null) {
            try {
                DataRs dataRs = new Gson().fromJson(response.errorBody().string(), DataRs.class);
                ErrObj errObj = dataRs != null ? dataRs.getError() : null;
                if (errObj != null && errObj.getMessage() != null) return errObj.getMessage();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return App.getAppCtx().getResources().getString(R.string.txt_oops);
    }
}
