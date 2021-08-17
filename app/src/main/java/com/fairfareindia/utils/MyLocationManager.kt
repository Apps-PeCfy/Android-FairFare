package com.fairfareindia.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*


class MyLocationManager {
    private var context: Context? = null

    //location
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationManager: LocationManager? = null

    //For My CurrentLocation Changed
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null

    constructor(context: Context?) {
        this.context = context
    }


    interface LocationManagerInterface {
        fun onSuccess(location: Location?)
    }

    interface LocationManagerTrackInterface {
        fun onMyLocationChange(currentLocation: MutableList<Location>?, lastLocation: Location?)
    }

    fun getMyCurrentLocationChange(listener: LocationManagerTrackInterface?) {
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
            return
        }
        if (isLocationEnabled()) {
            try {
                if (fusedLocationProviderClient == null){
                    fusedLocationProviderClient =
                        LocationServices.getFusedLocationProviderClient(context!!)
                }

                locationRequest = LocationRequest.create();
                locationRequest?.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest?.setInterval(10 * 1000);

                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        locationResult ?: return

                        if (listener != null && locationResult.locations.isNotEmpty()) {

                            listener.onMyLocationChange(locationResult.locations, locationResult.lastLocation)
                            // get latest location
                            val location = locationResult.lastLocation
                            // use your location object
                            // get latitude , longitude and other info from this
                        }


                    }
                }

                LocationServices.getFusedLocationProviderClient(context!!)
                    .requestLocationUpdates(locationRequest, locationCallback, null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            enableGPSAutomatically()
        }
    }

    fun getCurrentLocation(listener: LocationManagerInterface?) {
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
            return
        }
        if (isLocationEnabled()) {
            try {
                fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(context!!)
                fusedLocationProviderClient?.getLastLocation()?.addOnSuccessListener(
                    (context as Activity?)!!
                ) { location ->
                    if (listener != null && location != null) {
                        listener.onSuccess(location)
                    }
                }
                LocationServices.getFusedLocationProviderClient(context!!)
                    .requestLocationUpdates(LocationRequest(), LocationCallback(), null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            enableGPSAutomatically()
        }
    }


    protected fun isLocationEnabled(): Boolean {
        val le = Context.LOCATION_SERVICE
        locationManager = context!!.getSystemService(le) as LocationManager
        return locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun enableGPSAutomatically() {
        var googleApiClient: GoogleApiClient? = null
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(context!!)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                    override fun onConnected(bundle: Bundle?) {
//                            getCurrentLocation (new LocationManagerInterface () {
//                                @Override
//                                public void onSuccess(Location location) {
//
//
//                                }
//                            });
                    }

                    override fun onConnectionSuspended(i: Int) {}
                })
                .addOnConnectionFailedListener { }.build()
            googleApiClient.connect()
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 30 * 1000.toLong()
            locationRequest.fastestInterval = 5 * 1000.toLong()
            val builder =
                LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

            // **************************
            builder.setAlwaysShow(true) // this is the key ingredient
            // **************************
            val result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
            result.setResultCallback { result ->
                val status = result.status
                val state = result.locationSettingsStates
                when (status.statusCode) {
                    LocationSettingsStatusCodes.SUCCESS -> {
                    }
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Toast.makeText(context, "GPS is not on.", Toast.LENGTH_LONG).show()
                        // Location settings are not satisfied. But could be
                        // fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling
                            // startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(context as Activity?, 1000)
                        } catch (e: SendIntentException) {
                            // Ignore the error.
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Toast.makeText(
                        context,
                        "Setting change not allowed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    /**
     * PERMISSIONS
     */
    private fun requestPermission() {
        val permission =
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (!hasPermissions(context, *permission)) {
            ActivityCompat.requestPermissions(
                (context as Activity?)!!,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            getCurrentLocation(object : LocationManagerInterface {
                override fun onSuccess(location: Location?) {}
            })
        }
    }

    private fun requestPermissionLatLongAvail() {
        val permission =
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (!hasPermissions(context, *permission)) {
            ActivityCompat.requestPermissions(
                (context as Activity?)!!,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }

    fun hasPermissions(
        context: Context?,
        vararg permissions: String?
    ): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission!!
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    fun stopLocationUpdates() {
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
    }

}

