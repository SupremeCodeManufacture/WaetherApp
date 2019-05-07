package logic.helpers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import data.App;


public class PermissionsHelper {

    public final static int PERMISSION_CODE = 5467;


    public static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };


    public static boolean hasPermissions(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(App.getAppCtx(), permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean areAllPermisionsGranted(int[] resultsPermissions) {
        boolean response = true;

        for (int grantedStatus : resultsPermissions) {
            if (grantedStatus != PackageManager.PERMISSION_GRANTED) {
                response = false;
            }
        }

        return response;
    }

}