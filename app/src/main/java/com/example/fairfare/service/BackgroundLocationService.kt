package com.example.fairfare.service

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*

class BackgroundLocationService : Service() {
    // Binder given to clients
    private val binder = LocalBinder()
    //location
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationManager: LocationManager? = null

    //For My CurrentLocation Changed
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null

    interface LocationManagerTrackInterface {
        fun onMyLocationChange(currentLocation: MutableList<Location>?, lastLocation: Location?)
    }

    fun getMyCurrentLocationChange(listener: LocationManagerTrackInterface?) {
        if (ActivityCompat.checkSelfPermission(
                this!!,
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
                        LocationServices.getFusedLocationProviderClient(this!!)
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

                LocationServices.getFusedLocationProviderClient(this!!)
                    .requestLocationUpdates(locationRequest, locationCallback, null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            enableGPSAutomatically()
        }
    }

    protected fun isLocationEnabled(): Boolean {
        val le = Context.LOCATION_SERVICE
        locationManager = this!!.getSystemService(le) as LocationManager
        return locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun enableGPSAutomatically() {
        var googleApiClient: GoogleApiClient? = null
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(this!!)
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
                        Toast.makeText(this, "GPS is not on.", Toast.LENGTH_LONG).show()
                        // Location settings are not satisfied. But could be
                        // fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling
                            // startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(this as Activity?, 1000)
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Toast.makeText(
                        this,
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
        var permission: Array<String>?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }else{
            permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (!hasPermissions(this, *permission)) {
            ActivityCompat.requestPermissions(
                (this as Activity?)!!,
                permission,
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

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): BackgroundLocationService = this@BackgroundLocationService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }
}