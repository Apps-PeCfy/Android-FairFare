package com.fairfareindia.base

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import java.text.DateFormat
import java.util.*

open class BaseLocationClass : AppCompatActivity(),
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    LocationListener,
    ResultCallback<LocationSettingsResult> {
    /*
     * Provides the entry point to Google Play services.
     */
    var mGoogleApiClient: GoogleApiClient? = null

    /*
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected var mLocationRequest: LocationRequest? = null

    /*
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    protected var mLocationSettingsRequest: LocationSettingsRequest? = null

    /*
     * Represents a geographical location.
     */
    protected var mCurrentLocation: Location? = null

    /*
     * Provides access to the Location Settings API.
     */
    /*
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    var mRequestingLocationUpdates = true

    /*
     * Time when the location was updated represented as a String.
     */
    protected var mLastUpdateTime: String? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRequestingLocationUpdates = true
        mLastUpdateTime = ""
        buildGoogleApiClient()
        createLocationRequest()
        buildLocationSettingsRequest()
        checkPermission()
    }

    fun callLocation(savedInstanceState: Bundle?) {
        // Update values using data stored in the Bundle.
        savedInstanceState?.let { updateValuesFromBundle(it) }

        // Kick off the process of building the GoogleApiClient, LocationRequest, and
        // LocationSettingsRequest objects.
        buildGoogleApiClient()
        createLocationRequest()
        buildLocationSettingsRequest()
        checkPermission()
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION
                )
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION
                )
            }
        } else {
            checkLocationSettings()
        }
    }

    /*
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet()
                    .contains(KEY_REQUESTING_LOCATION_UPDATES)
            ) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                    KEY_REQUESTING_LOCATION_UPDATES
                )
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation =
                    savedInstanceState.getParcelable(KEY_LOCATION)
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet()
                    .contains(KEY_LAST_UPDATED_TIME_STRING)
            ) {
                mLastUpdateTime =
                    savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING)
            }
            onLocationChanged(mCurrentLocation!!)
        }
    }

    /*
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    @Synchronized
    protected fun buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient")
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
    }

    /*
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected fun createLocationRequest() {
        mLocationRequest = LocationRequest()

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest!!.interval = UPDATE_INTERVAL_IN_MILLISECONDS

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest!!.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.smallestDisplacement = 1f
    }

    /*
     * Uses a {@link LocationSettingsRequest.Builder} to build
     * a {@link LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    protected fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        mLocationSettingsRequest = builder.build()
    }

    /*
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     */
    fun checkLocationSettings() {
        val result =
            LocationServices.SettingsApi.checkLocationSettings(
                mGoogleApiClient,
                mLocationSettingsRequest
            )
        result.setResultCallback(this)
    }

    /*
     * The callback invoked when
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} is called. Examines the
     * {@link LocationSettingsResult} object and determines if
     * location settings are adequate. If they are not, begins the process of presenting a location
     * settings dialog to the user.
     */
    override fun onResult(locationSettingsResult: LocationSettingsResult) {
        val status = locationSettingsResult.status
        when (status.statusCode) {
            LocationSettingsStatusCodes.SUCCESS -> {
                Log.i(
                    TAG,
                    "All location settings are satisfied."
                )
                startLocationUpdates()
            }
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                Log.i(
                    TAG,
                    "Location settings are not satisfied. Show the user a dialog to" +
                            "upgrade location settings "
                )
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(
                        this,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (e: SendIntentException) {
                    Log.i(
                        TAG,
                        "PendingIntent unable to execute request."
                    )
                }
            }
            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i(
                TAG,
                "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created."
            )
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> {
                    Log.i(
                        TAG,
                        "User agreed to make required location settings changes."
                    )
                    startLocationUpdates()
                }
                Activity.RESULT_CANCELED -> Log.i(
                    TAG,
                    "User chose not to make required location settings changes."
                )
            }
        }
        return
    }

    /*
     * Requests location updates from the FusedLocationApi.
     */
    protected fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        if (mGoogleApiClient!!.isConnected) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this
            ).setResultCallback(object :
                ResultCallback<Status?> {
                override fun onResult(status: Status) {
                    mRequestingLocationUpdates = true
                }
            })
        } else {
            mGoogleApiClient!!.connect()
            Log.e(
                TAG,
                "startLocationUpdates: NOT CONNECTED AGAIN CONNECT"
            )
        }
    }

    /*
     * Removes location updates from the FusedLocationApi.
     */
    protected fun stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

//        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this
//        ).setResultCallback(new ResultCallback<Status>() {
//            @Override
//            public void onResult(Status status) {
//                mRequestingLocationUpdates = false;
//            }
//        });
    }

    override fun onStart() {
        super.onStart()
        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.connect()
        }
        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected && mRequestingLocationUpdates) {
            startLocationUpdates()
        }
    }

    public override fun onResume() {
        super.onResume()
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.
        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected && mRequestingLocationUpdates) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
//        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
//            stopLocationUpdates();
//        }
    }

    override fun onStop() {
        super.onStop()
        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.disconnect()
        }
    }

    /*
     * Runs when a GoogleApiClient object successfully connects.
     */
    override fun onConnected(connectionHint: Bundle?) {
        Log.i(TAG, "Connected to GoogleApiClient")
        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected && mRequestingLocationUpdates) {
            startLocationUpdates()
        }
        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
            mLastUpdateTime = DateFormat.getTimeInstance().format(Date())
        }
    }

    /*
     * Callback that fires when the location changes.
     */
    override fun onLocationChanged(location: Location) {
        mCurrentLocation = location
        mLastUpdateTime = DateFormat.getTimeInstance().format(Date())
    }

    override fun onConnectionSuspended(i: Int) {
        Log.i(TAG, "Connection suspended")
        val dialog = GooglePlayServicesUtil.getErrorDialog(i, this, 101)
        dialog.setCancelable(false)
        dialog.show()
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        val dialog =
            GooglePlayServicesUtil.getErrorDialog(result.errorCode, this, 101)
        dialog.setCancelable(false)
        dialog.show()
        //  Toast.makeText(this, "", Toast.LENGTH_LONG).show();
        Log.i(
            TAG,
            "Connection failed: ConnectionResult.getErrorCode() = " + result.errorCode
        )
    }

    /*
     * Stores activity data in the Bundle.
     */
    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putBoolean(
            KEY_REQUESTING_LOCATION_UPDATES,
            mRequestingLocationUpdates
        )
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation)
        savedInstanceState.putString(
            KEY_LAST_UPDATED_TIME_STRING,
            mLastUpdateTime
        )
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_FINE_LOCATION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationSettings()
                }
            }
        }
    }


    companion object {
        private const val MY_PERMISSIONS_REQUEST_FINE_LOCATION = 11
        private val TAG = BaseLocationClass::class.java.name

        /*
     * Constant used in the location settings dialog.
     */
        protected const val REQUEST_CHECK_SETTINGS = 0x1

        /*
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
        const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000

        /*
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
        const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2

        // Keys for storing activity state in the Bundle.
        protected const val KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates"
        protected const val KEY_LOCATION = "location"
        protected const val KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string"
    }
}