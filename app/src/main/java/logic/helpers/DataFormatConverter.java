package logic.helpers;

import android.net.Uri;

import java.io.IOException;
import java.net.URLDecoder;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class DataFormatConverter {


//    public static CloudDataObj getObjFromJson(String json) {
//        try {
//            //MyLogs.LOG("DataFormatConverter", "getObjFromJson", "json: " + json);
//            return new Gson().fromJson(json, CloudDataObj.class);
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        return null;
//    }

    public static Uri safeConvertUrlToUri(String url) {
        try {
            return Uri.parse(URLDecoder.decode(url, "UTF-8"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String getJsonRs(Response<ResponseBody> response) {
        if (response != null && response.body() != null) {
            try {
                return response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


}