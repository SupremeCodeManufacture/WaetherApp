package logic.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;

import com.google.android.gms.common.util.Strings;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import data.App;
import data.GenericConstants;
import data.model.DayWeatherObj;
import data.model.ForecastObj;
import data.model.HourWeatherObj;
import data.model.LocationObj;
import logic.push_notification.CloudDataObj;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class DataFormatConverter {


    public static CloudDataObj getObjFromJson(String json) {
        try {
            return new Gson().fromJson(json, CloudDataObj.class);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

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

    //from '2019-02-21' to '21 Feb'
    public static String getPrettyDay(String inDate) {
        SimpleDateFormat intFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (inDate != null)
            try {
                Date date = intFormat.parse(inDate);
                return DateFormat.format("MMM dd", date).toString();

            } catch (ParseException e) {
                e.printStackTrace();
            }

        return "";
    }

    //from '2019-02-21' to 'Mon'
    public static String getPrettyWeekDay(String inDate) {
        SimpleDateFormat intFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (inDate != null)
            try {
                Date date = intFormat.parse(inDate);
                return DateFormat.format("EEE", date).toString();

            } catch (ParseException e) {
                e.printStackTrace();
            }

        return "";
    }

    public static String getTodatDate() {
        try {
            Date date = Calendar.getInstance().getTime();
            return DateFormat.format("yyyy-MM-dd", date).toString();

        } catch (Exception e) {
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
                int heightTempDpi = DataFormatConverter.getMinMaxTempDif(forecastObj, hourWeatherObj.getTemp_c(), 90);
                hourWeatherObj.setHeightValDp(heightTempDpi);
                list.add(hourWeatherObj);
            }
        }

        return list;
    }

    public static int getMinMaxTempDif(ForecastObj forecastObj, float curTemp, int heighDp) {
        DayWeatherObj dayWeatherObj = forecastObj.getForecastday()[0].getDay();
        float tempMin = dayWeatherObj.getMintemp_c();
        float tempMax = dayWeatherObj.getMaxtemp_c();
        //MyLogs.LOG("DataFormatConverter", "getMinMaxTempDif", "heighDp: " + heighDp + "  tempMin: " + tempMin + " tempMax: " + tempMax + " curTemp: " + curTemp);

        float calcVal;
        if (tempMin > 0 && tempMax > 0) {
            calcVal = Math.abs(tempMin) * heighDp / Math.abs(tempMax);
            //MyLogs.LOG("DataFormatConverter", "getMinMaxTempDif", "1 - calcVal: " + calcVal);

        } else if (tempMin < 0 && tempMax < 0) {
            calcVal = Math.abs(tempMax) * heighDp / Math.abs(tempMin);
            //MyLogs.LOG("DataFormatConverter", "getMinMaxTempDif", "2 - calcVal: " + calcVal);

        } else {
            float diff = Math.abs(tempMin) + Math.abs(tempMax);
            //MyLogs.LOG("DataFormatConverter", "getMinMaxTempDif", "diff: " + diff);

            if (curTemp > 0) {
                float finalCompareVal = Math.abs(tempMin) + curTemp;
                calcVal = (finalCompareVal * heighDp) / diff;
                //MyLogs.LOG("DataFormatConverter", "getMinMaxTempDif", "3 - finalCompareVal: " + finalCompareVal + " calcVal: " + calcVal);

            } else {
                float finalCompareVal = Math.abs(tempMin) - Math.abs(curTemp);
                calcVal = (Math.abs(finalCompareVal) * heighDp) / diff;
                //MyLogs.LOG("DataFormatConverter", "getMinMaxTempDif", "4 - finalCompareVal: " + finalCompareVal + " calcVal: " + calcVal);
            }
        }

        return (int) Math.ceil(Math.abs(calcVal));
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
            //MyLogs.LOG("DataFormatConverter", "hetLocationFromPalce", "locality: " + locality + " country: " + country);

            LocationObj locationObj = new LocationObj();
            locationObj.setId(getMd5(locality + country));
            locationObj.setName(locality);
            locationObj.setCountry(country);
            locationObj.setLat(myLatitude);
            locationObj.setLon(myLongLng);

            return locationObj;
        }

        return null;
    }

    public static String getMd5(final String inputStr) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(inputStr.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static RelativeLayout.LayoutParams getAudienceViewParams(int heightDpi) {
        //MyLogs.LOG("DataFormatConverter", "getAudienceViewParams", "heightDpi: " + heightDpi);

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float pxw = 20 * (metrics.densityDpi / 160f);
        int w = Math.round(pxw);

        float pxh = Math.round(heightDpi) * (metrics.densityDpi / 160f);
        int h = Math.round(pxh);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        return params;
    }

    @SuppressLint("MissingPermission")
    public static LatLng getMyLocation() {
        if (PermissionsHelper.hasPermissions(PermissionsHelper.PERMISSIONS_LOCATION)) {
            LocationManager lm = (LocationManager) App.getAppCtx().getSystemService(Context.LOCATION_SERVICE);
            Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (myLocation == null) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);

                String provider = lm.getBestProvider(criteria, true);
                myLocation = lm.getLastKnownLocation(provider);
            }

            if (myLocation != null) {
                return new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            }
        }

        return null;
    }

    public static boolean isPassedAdsFree() {
        return System.currentTimeMillis() > (App.getFirstLaunchMilis() + GenericConstants.THREE_DAYS_IN_MILIS);
    }


    public static String getCityName(double latitude, double longitude) {
        Geocoder gcd = new Geocoder(App.getAppCtx(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                //MyLogs.LOG("adas", "sdfsdf", " ==> " + addresses.get(0).getLocality() + " --> " +Locale.getDefault().getCountry());

            } else {
                //MyLogs.LOG("adas", "sdfsdf", " ==> sula");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}