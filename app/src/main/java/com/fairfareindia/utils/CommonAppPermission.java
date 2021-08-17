package com.fairfareindia.utils;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class CommonAppPermission {
    static String[] PERMISSIONS = {Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
            ,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
    };


    static String[] PERMISSION_CAMERA = {Manifest.permission.CAMERA};


    static String[] PERMISSIONS_BACKGROUND_LOCATION = {Manifest.permission.ACCESS_BACKGROUND_LOCATION};


    private static final int PERMISSION_ALL = 1;


    static String[] PERMISSIONS_ANDROID10_ABOVE = { Manifest.permission.ACCESS_BACKGROUND_LOCATION};





    /**
     * PERMISSION SECTION
     **/
    public static boolean requestPermissionGranted(Context context) {


//        if (!hasPermissions(context,Manifest.permission.CAMERA)){
//            ActivityCompat.requestPermissions((Activity) context, PERMISSION_CAMERA, PERMISSION_ALL);
//            return false;
//        }
////        else if (!hasPermissions(context,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
////            ActivityCompat.requestPermissions((Activity) context, {PERMISSIONS}, PERMISSION_ALL);
////            return false;
////        }


        if (!hasPermissions(context, PERMISSIONS)) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {

                ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, PERMISSION_ALL);
                return false;
            } else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, PERMISSION_ALL);

                return false;
            }else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, PERMISSION_ALL);

                return false;
            }

            else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, PERMISSION_ALL);

                return false;
            }
            else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, PERMISSION_ALL);

                return false;
            }
            else {


                // No explanation needed; request the permission
                ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, PERMISSION_ALL);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
                return false;

            }
        } else {
            // Permission has already been granted

            if (!hasPermissions(context, PERMISSIONS_BACKGROUND_LOCATION)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ) {
                    ActivityCompat.requestPermissions((Activity) context, PERMISSIONS_BACKGROUND_LOCATION, PERMISSION_ALL);

                    return false;
                }
            }

            return true;
        }

    }

    public static void openLocationSettings(Context context){
        if (!hasPermissions(context, PERMISSIONS_BACKGROUND_LOCATION)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ) {
                ActivityCompat.requestPermissions((Activity) context, PERMISSIONS_BACKGROUND_LOCATION, PERMISSION_ALL);
            }
        }
    }

    public static boolean hasCameraPermission(Context context){

        return hasPermissionSingle(context,Manifest.permission.CAMERA);
    }
    public static boolean hasExternalStoragePermission(Context context){

        return hasPermissionSingle(context,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && hasPermissionSingle(context,Manifest.permission.READ_EXTERNAL_STORAGE);
    }
    public static boolean hasLocationPermission(Context context){

        return hasPermissionSingle(context,Manifest.permission.ACCESS_FINE_LOCATION)
                && hasPermissionSingle(context,Manifest.permission.ACCESS_COARSE_LOCATION)
                && hasPermissionSingle(context,Manifest.permission.ACCESS_BACKGROUND_LOCATION);
    }

    public static boolean hasAllPermissionGranted(Context context){

        return hasCameraPermission(context)
                && hasExternalStoragePermission(context)
                && hasLocationPermission(context);
    }



    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {

                //check if the permission is only for android 10 and above
                boolean isForAndroid10Above = false;
                for (String permission10 : PERMISSIONS_ANDROID10_ABOVE){
                    if (permission10.equalsIgnoreCase(permission)){
                        isForAndroid10Above = true;
                        break;
                    }
                }

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && isForAndroid10Above){
                    continue;
                }

                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean hasPermissionSingle(Context context, String  permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permission != null) {
//            for (String permission : permissions) {

            //check if the permission is only for android 10 and above
            boolean isForAndroid10Above = false;
            for (String permission10 : PERMISSIONS_ANDROID10_ABOVE){
                if (permission10.equalsIgnoreCase(permission)){
                    isForAndroid10Above = true;
                    break;
                }
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && isForAndroid10Above){
                return true;
            }

            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
//            }
        }
        return true;
    }

}
