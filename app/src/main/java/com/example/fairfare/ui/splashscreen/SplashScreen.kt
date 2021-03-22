package com.example.fairfare.ui.splashscreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.fairfare.R
import com.example.fairfare.ui.Login.LoginActivity
import com.example.fairfare.ui.home.HomeActivity
import com.example.fairfare.utils.Constants
import com.example.fairfare.utils.PreferencesManager
import com.example.fairfare.utils.ProjectUtilities
import com.google.android.gms.location.FusedLocationProviderClient


class SplashScreen : AppCompatActivity() {
    var isLogin: String? = null
    var mPreferencesManager: PreferencesManager? = null
    var sharedpreferences: SharedPreferences? = null
    var locationManager: LocationManager? = null
    var isGPSEnabled = false
    var isNetworkEnabled = false


    private lateinit var fusedLocationClient: FusedLocationProviderClient


    var callOnResune: String? = "first"
    var checkPopup: String? = "first"


    var isAccepted = false

    var requestCodeq: Int? = 0


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        setStatusBarGradiant(this)

        PreferencesManager.initializeInstance(this@SplashScreen)
        mPreferencesManager = PreferencesManager.instance
        sharedpreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE)
        sharedpreferences!!.edit().clear().commit()
        mPreferencesManager!!.setStringValue(Constants.SHARED_PREFERENCE_PICKUP_AITPORT, "LOCALITY")
        isLogin = mPreferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_ISLOGIN)
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        isAccepted = ProjectUtilities.checkPermission(applicationContext)


        if (isAccepted) {
            checkUpdate()


        } else {
            reuestPermissions()
        }

        requestBatteryOptimisationDisabled()

    }

    private fun requestBatteryOptimisationDisabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent()
            val packageName = packageName
            val pm =
                getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }
    }

    private fun setStatusBarGradiant(activity: SplashScreen) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            val background = activity.resources.getDrawable(R.drawable.app_gradient)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
            window.setBackgroundDrawable(background)
        }

    }


    override fun onResume() {
        isAccepted = ProjectUtilities.checkPermission(applicationContext)
        isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (isAccepted || isGPSEnabled || isNetworkEnabled) {
            if (callOnResune.equals("first")) {
                checkUpdate()
            }

        } else {

        }

        super.onResume()
    }

    private fun checkUpdate() {
        val h = Handler()
        h.postDelayed({

            try {
                if (!isGPSEnabled && !isNetworkEnabled) {
                    val alertDialog =
                        AlertDialog.Builder(this)
                    alertDialog.setTitle("GPS is settings")
                    alertDialog.setCancelable(false)
                    alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?")
                    alertDialog.setPositiveButton("Settings") { dialog, which ->
                        val intent =
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        this.startActivity(intent)
                    }
                    alertDialog.setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    alertDialog.show()

                } else {
                    if (isLogin == "true" && requestCodeq == REQUEST_PERMISSION && isAccepted) {
                        callOnResune = "second"
                        val intent = Intent(applicationContext, HomeActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else if (requestCodeq == REQUEST_PERMISSION && isAccepted) {


                        callOnResune = "second"

                        Log.d("onRequestPermissi", "2")

                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        reuestPermissions()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }, 1000 * 1.toLong())

    }

    private fun reuestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            ActivityCompat.requestPermissions(
                this@SplashScreen, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                REQUEST_PERMISSION
            )
        } else {
            ActivityCompat.requestPermissions(
                this@SplashScreen, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                REQUEST_PERMISSION
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {

        if (!isAccepted && isGPSEnabled && isNetworkEnabled) {
            for (i in permissions.indices) {
                val permission = permissions[i]
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && "android.permission.ACCESS_BACKGROUND_LOCATION".equals(permission, ignoreCase = true)) {
                        continue
                    }
                    val showRationale = shouldShowRequestPermissionRationale(permission)
                    if (!showRationale) {

                        if (checkPopup.equals("first")) {
                            checkPopup = "second"
                            displayNeverAskAgainDialog()


                        }

                    }
                }

            }
            afterAllowedPermissionFunctionality(requestCode)
        } else {
            afterAllowedPermissionFunctionality(requestCode)
        }
    }

    private fun afterAllowedPermissionFunctionality(requestCode: Int) {
        requestCodeq = requestCode
        if (requestCode == REQUEST_PERMISSION) {
            if (isLogin == "true" && isGPSEnabled && isNetworkEnabled && isAccepted) {
                callOnResune = "second"

                val intent = Intent(applicationContext, HomeActivity::class.java)
                startActivity(intent)
                finish()

            } else if (isGPSEnabled && isNetworkEnabled && isAccepted) {
                callOnResune = "second"

                Log.d("onRequestPermissi", "1")

                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                if (callOnResune.equals("first")) {
                    checkUpdate()
                }
            }
        }
    }

    private fun displayNeverAskAgainDialog() {

        val builder =
            AlertDialog.Builder(this)
        builder.setMessage(
            """Please permit the permission through Settings screen.

Select Permissions -> Enable permission
                """.trimIndent()
        )
        builder.setCancelable(false)
        builder.setPositiveButton(
            "Permit Manually"
        ) { dialog, which ->
            dialog.dismiss()
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    companion object {
        private const val REQUEST_PERMISSION = 100
    }


}
