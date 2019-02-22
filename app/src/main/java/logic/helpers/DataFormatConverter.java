package logic.helpers;

import android.net.Uri;
import android.text.format.DateFormat;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public static List<HourWeatherObj> getTodayHoursWeather(ForecastObj forecastObj) {
        long curTimeMilis = System.currentTimeMillis();
        HourWeatherObj[] todayHours = forecastObj.getForecastday()[0].getHour();
        List<HourWeatherObj> list = new ArrayList<>();

        for (HourWeatherObj hourWeatherObj : todayHours) {
            if (curTimeMilis < ((hourWeatherObj.getTime_epoch()*1000) + 3600000)) {
                list.add(hourWeatherObj);
            }
        }

        return list;
    }

}