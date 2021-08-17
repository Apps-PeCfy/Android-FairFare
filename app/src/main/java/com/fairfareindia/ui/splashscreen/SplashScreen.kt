package com.fairfareindia.ui.splashscreen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.fairfareindia.BuildConfig
import com.fairfareindia.R
import com.fairfareindia.ui.Login.LoginActivity
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.ui.introduction.IntroActivity
import com.fairfareindia.utils.CommonAppPermission
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability


class SplashScreen : AppCompatActivity() {
    var isLogin: String? = null
    var mPreferencesManager: PreferencesManager? = null
    var sharedpreferences: SharedPreferences? = null
    var appSharedpreferences: SharedPreferences? = null
    var locationManager: LocationManager? = null
    var isGPSEnabled = false
    var isNetworkEnabled = false
    var isAlertWithPermissionInstructionVisible = false
    var totalPermissionAskedCounter = 0
    private val MY_REQUEST_CODE = 201


    private lateinit var fusedLocationClient: FusedLocationProviderClient


    var callOnResune: String? = "first"
    var checkPopup: String? = "first"
    var callOnlyOne: String? = "OnlyOne"
    var appFirst: String? = ""


    var isAccepted = false

    var requestCodeq: Int? = 0


    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        setStatusBarGradiant(this)

        PreferencesManager.initializeInstance(this@SplashScreen)
        mPreferencesManager = PreferencesManager.instance
        appSharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        appFirst = appSharedpreferences!!.getString("AppOpen","")

        sharedpreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE)
        sharedpreferences!!.edit().clear().commit()
        mPreferencesManager!!.setStringValue(Constants.SHARED_PREFERENCE_PICKUP_AITPORT, "LOCALITY")
        isLogin = mPreferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_ISLOGIN)
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//        isAccepted = ProjectUtilities.checkPermission(applicationContext)


//        isAccepted = CommonAppPermission.requestPermissionGranted(this@SplashScreen)
//
//
//
//        if (isAccepted) {
//            checkUpdate()
//
//
//        } else {
//            //reuestPermissions()
//            CommonAppPermission.requestPermissionGranted(this@SplashScreen)
//        }

        // displayNeverAskAgainDialog()
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


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onResume() {
        //   isAccepted = ProjectUtilities.checkPermission(applicationContext)

        // isAccepted =   CommonAppPermission.requestPermissionGranted(this@SplashScreen)
        val versionCode: Int = BuildConfig.VERSION_CODE
        isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        // if (isAccepted || isGPSEnabled || isNetworkEnabled) {
        if (callOnResune.equals("first")) {
           // checkUpdate()
            checkAppUpdate()
        }

//        } else {
//
//        }

        super.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                checkUpdate()
            }
        }
    }

    @SuppressLint("NewApi")
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

                    if(CommonAppPermission.hasAllPermissionGranted(this@SplashScreen)){
                        // if (CommonAppPermission.requestPermissionGranted(this@SplashScreen)) {
                        isAccepted = true
//                        if (isLogin == "true" && requestCodeq == REQUEST_PERMISSION && isAccepted) {
                        if (isLogin == "true"  && isAccepted) {

                            callOnResune = "second"
                            if(appFirst!!.isEmpty()){

                                val editor = appSharedpreferences!!.edit()
                                editor.putString("AppOpen", "appOpenFirst")
                                editor.commit()
                                val intent = Intent(applicationContext, IntroActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                val intent = Intent(applicationContext, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            }

                        } else if (isAccepted) {


                            callOnResune = "second"

                            Log.d("onRequestPermissi", "2")
                            if (callOnlyOne.equals("OnlyOne")) {

                                if(appFirst!!.isEmpty()){

                                    val editor = appSharedpreferences!!.edit()
                                    editor.putString("AppOpen", "appOpenFirst")
                                    editor.commit()
                                    callOnlyOne = "SecondTime"
                                    val intent = Intent(applicationContext, IntroActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    callOnlyOne = "SecondTime"
                                    val intent = Intent(applicationContext, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()

                                }
                            }
                        } else {
                            //reuestPermissions()

                        }
                    }
                    else{

                        if (!isAlertWithPermissionInstructionVisible) {

                            var alertMessage =
                                "Please provide following permissions to smooth working of the application"

                            if (!CommonAppPermission.hasCameraPermission(this)) {
                                alertMessage += "\n\nCamera: Please provide camera permission"
                            }
                            if (!CommonAppPermission.hasExternalStoragePermission(this)) {
                                alertMessage += "\n\nMedia: Please provide Media permission"
                            }
                            if (!CommonAppPermission.hasLocationPermission(this)) {
                                //make the message as per android 10 and above and below


                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ){
                                    alertMessage += "\n\nLocation: Please provide \"Allow all the time\" location permission for accurate pick-up and drop-off locations, availability of autos/taxis in the area and tracking the ride until trip ends."
                                }
                                else{
                                    alertMessage += "\n\nLocation: Please provide location permission"
                                }
                            }

                            val alertDialog =
                                AlertDialog.Builder(this)
                            alertDialog.setTitle("Permissions")
                            alertDialog.setCancelable(false)
                            alertDialog.setMessage(alertMessage)
                            alertDialog.setPositiveButton("Okay") { dialog, which ->

                                dialog.dismiss()

                                if (totalPermissionAskedCounter < 2){
                                    CommonAppPermission.requestPermissionGranted(this@SplashScreen)
                                    totalPermissionAskedCounter ++;
                                }
                                else {
                                    val intent = Intent()
                                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                    val uri = Uri.fromParts("package", packageName, null)
                                    intent.data = uri
                                    startActivity(intent)
                                }
                                isAlertWithPermissionInstructionVisible = false
                            }

                            alertDialog.show()

                            isAlertWithPermissionInstructionVisible = true
                        }

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

    @SuppressLint("MissingSuperCall")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {

        if (!isAccepted && isGPSEnabled && isNetworkEnabled) {
            //displayNeverAskAgainDialog()

//            for (i in permissions.indices) {
//                val permission = permissions[i]
//                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
//                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && "android.permission.ACCESS_BACKGROUND_LOCATION".equals(permission, ignoreCase = true)) {
//                        continue
//                    }
//                    val showRationale = shouldShowRequestPermissionRationale(permission)
//                    if (!showRationale) {
//
//                        if (checkPopup.equals("first")) {
//                            checkPopup = "second"
//                            displayNeverAskAgainDialog()
//
//
//                        }
//
//                    }
//                }
//
//            }
            // afterAllowedPermissionFunctionality(requestCode)
        } else {
            // afterAllowedPermissionFunctionality(requestCode)
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
                    //checkUpdate()
                    checkAppUpdate()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun displayNeverAskAgainDialog() {

        val builder =
            AlertDialog.Builder(this)
        builder.setMessage(
            "Please provide location always allow permission to proceed".trimIndent()
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

            // CommonAppPermission.requestPermissionGranted(this@SplashScreen)
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    companion object {
        private const val REQUEST_PERMISSION = 100
    }

fun checkAppUpdate(){
// Creates instance of the manager.
    val appUpdateManager = AppUpdateManagerFactory.create(this@SplashScreen)
    //    gotoScreen();
// Returns an intent object that you use to check for an update.
    val appUpdateInfoTask = appUpdateManager.appUpdateInfo
    // Checks that the platform will allow the specified type of update.
    appUpdateInfoTask.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE // For a flexible update, use AppUpdateType.FLEXIBLE
            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
            try {
                appUpdateManager.startUpdateFlowForResult( // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,  // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                    AppUpdateType.IMMEDIATE,  // The current activity making the update request.
                    this@SplashScreen,  // Include a request code to later monitor this update request.
                    MY_REQUEST_CODE)
            } catch (e: IntentSender.SendIntentException) {
            }
        } else {
            checkUpdate()
        }
    }
}
}