package com.fairfareindia.ui.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog

class GPSTracker @RequiresApi(api = Build.VERSION_CODES.M) constructor(private val mContext: Context) :
    Service(), LocationListener {
    var isGPSEnabled = false
    var isNetworkEnabled = false
    var canGetLocation = false
    var locations: Location? = null
    var locationsChanged: Location? = null
    var latitude = 0.0
    var longitude = 0.0
    protected var locationManager: LocationManager? = null

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    fun getLocation(): Location? {
        try {
            locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled) {
            } else {
                canGetLocation = true
                if (isNetworkEnabled) {
                    locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                    Log.d("Network", "Network")
                    if (locationManager != null) {
                        locations = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (locations != null) {
                            latitude = locations!!.latitude
                            longitude = locations!!.longitude
                        }
                    }
                }
                if (isGPSEnabled) {
                    if (locations == null) {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                            this
                        )
                        Log.d("GPS Enabled", "GPS Enabled")
                        if (locationManager != null) {
                            locations =
                                locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            if (locations != null) {
                                latitude = locations!!.latitude
                                longitude = locations!!.longitude
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return locations
    }

    fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager!!.removeUpdates(this@GPSTracker)
        }
    }

    /**
     * Function to get latitude
     */
    fun getLatitude(): Double? {
        if (locations != null) {
            latitude = locations!!.latitude
        }
        return latitude
    }

    /**
     * Function to get longitude
     */
    fun getLongitude(): Double? {
        if (locations != null) {
            longitude = locations!!.longitude
        }
        // return longitude
        return longitude
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     */
    fun canGetLocation(): Boolean {
        return canGetLocation
    }


    fun getChangeLatitude(): Double? {
        if (locationsChanged != null) {
            latitude = locationsChanged!!.latitude
        }

        Log.d("dsdswdsdsdsget", latitude.toString())

        return latitude
    }



    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(mContext)
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings")
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?")
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings") { dialog, which ->
            val intent =
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            mContext.startActivity(intent)
        }
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        // Showing Alert Message
        alertDialog.show()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onLocationChanged(clocation: Location) {
        locationsChanged = clocation

    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}

    companion object {
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10
        private const val MIN_TIME_BW_UPDATES = 1000 * 60 * 1 // 1 minute
            .toLong()
    }

    init {
        getLocation()
    }
}