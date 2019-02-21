package logic.helpers;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.text.format.DateFormat;

import com.google.android.gms.common.util.Strings;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import data.model.ForecastObj;
import data.model.HourWeatherObj;
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

    //from '2019-02-21 11:00' to '11:00'
    public static String getPrettyHour(String inDate) {
        SimpleDateFormat intFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        if (inDate != null)
            try {
                Date date = intFormat.parse(inDate);
                return DateFormat.format("HH:mm", date).toString();

            } catch (ParseException e) {
                e.printStackTrace();
            }

        return "";
    }

    public static HourWeatherObj[] getTodayHoursWeather(ForecastObj forecastObj) {
        if (forecastObj != null && forecastObj.getForecastday()[0] != null && forecastObj.getForecastday()[0].getHour() != null)
            return forecastObj.getForecastday()[0].getHour();

        return null;
    }

}