package logic.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;

import androidx.core.util.Pair;

import com.google.android.gms.common.util.Strings;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Locale;

import data.App;
import logic.push_notification.CloudDataObj;

public class DataFormatConverter {


    public static CloudDataObj getObjFromRemoteMsg(RemoteMessage remoteMessage) {
        try {
            String message = remoteMessage.getData().get("msg");
            String tip = remoteMessage.getData().get("tip");
            CloudDataObj cloudDataObj = new CloudDataObj();
            cloudDataObj.setTipText(tip);
            cloudDataObj.setUserNotification(new Gson().fromJson(message, CloudDataObj.MsgNotification.class));
            return cloudDataObj;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static Uri safeConvertUrlToUri(String url) {
        try {
            return Uri.parse(URLDecoder.decode(url, "UTF-8"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
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

    public static Pair<Double, Double> getLatLon(String locCoord) {
        try {
            String[] arr = locCoord.split(",");

            return new Pair<>(Double.parseDouble(arr[0]), Double.parseDouble(arr[1]));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getCityName(String locQuery) {
        Pair<Double, Double> pair = getLatLon(locQuery);
        if (pair != null) {

            try {
                double latitude = pair.first;
                double longitude = pair.second;
                Geocoder geocoder = new Geocoder(App.getAppCtx(), Locale.getDefault());

                List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                if (addressList.size() > 0) {
                    Address address = addressList.get(0);
                    MyLogs.LOG("adas", "sdfsdf", " ==> " + new Gson().toJson(address));

                    return !Strings.isEmptyOrWhitespace(address.getSubAdminArea()) ? address.getSubAdminArea() : address.getAdminArea();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "";
    }
}