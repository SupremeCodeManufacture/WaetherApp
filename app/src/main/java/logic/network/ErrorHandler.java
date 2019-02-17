package logic.network;

import com.SupremeManufacture.weather.R;
import com.google.gson.Gson;

import data.App;
import data.model.ErrObjData;
import okhttp3.ResponseBody;
import retrofit2.Response;


public class ErrorHandler {

    public static String getFriendlyError(Throwable throwable) {
        if (throwable != null && throwable.getMessage() != null && throwable.getMessage().contains("Unable to resolve host"))
            return App.getAppCtx().getResources().getString(R.string.txt_upgrade_connectin);

        return App.getAppCtx().getResources().getString(R.string.txt_oops);
    }

    public static String getErrMsgFromRs(Response<ResponseBody> response) {
        if (response != null && response.errorBody() != null) {
            try {
                ErrObjData errObjData = new Gson().fromJson(response.errorBody().string(), ErrObjData.class);
                if (errObjData != null && errObjData.getMessage() != null) return errObjData.getMessage();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return App.getAppCtx().getResources().getString(R.string.txt_oops);
    }
}
