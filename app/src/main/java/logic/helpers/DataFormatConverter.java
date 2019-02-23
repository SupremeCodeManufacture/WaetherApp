package logic.helpers;

import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.format.DateFormat;

import com.google.android.gms.common.util.Strings;
import com.google.android.gms.location.places.Place;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import data.App;
import data.model.ForecastObj;
import data.model.HourWeatherObj;
import data.model.LocationObj;
import logic.async_await.AsyncTaskWorker;
import logic.async_await.CallableObj;
import logic.async_await.OnAsyncDoneNoRsListener;
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

    public static boolean isTrueStr(String isTrue) {
        return isTrue != null && isTrue.equals("1");
    }

    public static double safeParseToDouble(String number) {
        if (!Strings.isEmptyOrWhitespace(number)) {
            try {
                return Double.parseDouble(number);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return 0;
    }


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
            if (curTimeMilis < ((hourWeatherObj.getTime_epoch() * 1000) + 3600000)) {
                list.add(hourWeatherObj);
            }
        }

        return list;
    }

    public static List<LocationObj> getMyStoredLocations() {
        String myLocJson = App.getStorePlaces();
        if (myLocJson != null) {
            return new Gson().fromJson(myLocJson, new TypeToken<List<LocationObj>>() {
            }.getType());
        }

        return null;
    }

    public static LocationObj hetLocationFromPalce(Place place) {
        double myLatitude = place.getLatLng().latitude;
        double myLongLng = place.getLatLng().longitude;
        Geocoder geocoder = new Geocoder(App.getAppCtx(), Locale.getDefault());

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(myLatitude, myLongLng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0) {
            String locality = addresses.get(0).getLocality();
            String country = addresses.get(0).getCountryName();
            MyLogs.LOG("DataFormatConverter", "hetLocationFromPalce", "locality: " + locality + " country: " + country);

            LocationObj locationObj = new LocationObj();
            locationObj.setName(locality);
            locationObj.setCountry(country);
            locationObj.setLat(myLatitude);
            locationObj.setLon(myLongLng);

            return locationObj;
        }

        return null;
    }

    //used after item is deleted
    public static void updateAllLocations(final List<LocationObj> locations) {
        new AsyncTaskWorker(
                new CallableObj<Void>() {
                    public Void call() {
                        if (locations != null && locations.size() > 0) {
                            MyLogs.LOG("DataFormatConverter", "updateAllLocations", "====> " + locations.size());

                            App.setStorePlaces(new Gson().toJson(locations));
                        } else {
                            MyLogs.LOG("DataFormatConverter", "updateAllLocations", "====> no data");
                            App.setStorePlaces("");
                        }
                        return null;
                    }
                },
                new OnAsyncDoneNoRsListener() {
                    @Override
                    public void onDone() {
                    }
                }
        ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}