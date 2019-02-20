package logic.network;

import com.SupremeManufacture.weather.R;
import com.google.gson.Gson;

import data.App;
import data.model.DataRs;
import data.model.ErrObj;
import okhttp3.ResponseBody;
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
                ErrObj errObj = new Gson().fromJson(response.errorBody().string(), ErrObj.class);
                if (errObj != null && errObj.getMessage() != null) return errObj.getMessage();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return App.getAppCtx().getResources().getString(R.string.txt_oops);
    }
}
