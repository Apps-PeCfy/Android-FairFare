package com.fairfareindia.ui.splashscreen

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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.fairfareindia.R
import com.fairfareindia.ui.Login.LoginActivity
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.ui.introduction.IntroActivity
import com.fairfareindia.utils.CommonAppPermission
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.fairfareindia.utils.SetLocalLanguage
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
    var isAlertWithPermissionInstructionVisible = false
    var totalPermissionAskedCounter = 0
    private val MY_REQUEST_CODE = 201


    var callOnResune: String? = "first"
    var callOnlyOne: String? = "OnlyOne"
    var appFirst: String? = ""


    var isAccepted = false

    private var handler : Handler = Handler()


    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        setStatusBarGradiant(this)

        PreferencesManager.initializeInstance(this@SplashScreen)
        mPreferencesManager = PreferencesManager.instance
        appSharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        appFirst = appSharedpreferences!!.getString("AppOpen", "")

        sharedpreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE)
        sharedpreferences?.edit()?.clear()?.apply()
        mPreferencesManager!!.setStringValue(Constants.SHARED_PREFERENCE_PICKUP_AITPORT, "LOCALITY")
        isLogin = mPreferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_ISLOGIN)
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager


        if (mPreferencesManager?.getLanguage() == getString(R.string.str_eng_code)) {
            SetLocalLanguage.setLocaleLanguage(this, getString(R.string.str_eng_code))
        }else if (mPreferencesManager?.getLanguage() == getString(R.string.str_hindi_code)) {
            SetLocalLanguage.setLocaleLanguage(this, getString(R.string.str_hindi_code))
        } else if (mPreferencesManager?.getLanguage() == getString(R.string.str_marathi_code)) {
            SetLocalLanguage.setLocaleLanguage(this, getString(R.string.str_marathi_code))
        }

       // requestBatteryOptimisationDisabled()



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
        if (callOnResune.equals("first")) {
            checkAppUpdate()
        }
        super.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                //  checkUpdate()

            }
        }
    }

    @SuppressLint("NewApi")
    private fun checkUpdate() {
        val h = Handler()
        h.postDelayed({

            try {
                if (CommonAppPermission.hasAllPermissionGranted(this@SplashScreen)) {
                    isAccepted = true
                    if (isLogin == "true" && isAccepted) {

                        callOnResune = "second"
                        if (appFirst!!.isEmpty()) {

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

                            if (appFirst!!.isEmpty()) {

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
                    }
                } else {

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


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                alertMessage += "\n\nLocation: Please provide \"Allow all the time\" location permission for accurate pick-up and drop-off locations, availability of autos/taxis in the area and tracking the ride until trip ends."
                            } else {
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

                            if (totalPermissionAskedCounter < 2) {
                                CommonAppPermission.requestPermissionGranted(this@SplashScreen)
                                totalPermissionAskedCounter++;
                            } else {
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
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }, 1000 * 1.toLong())

    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkAppUpdate() {
// Creates instance of the manager.
        val appUpdateManager = AppUpdateManagerFactory.create(this@SplashScreen)
        //    gotoScreen();
// Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE // For a flexible update, use AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                try {
                    appUpdateManager.startUpdateFlowForResult( // Pass the intent that is returned by 'getAppUpdateInfo()'.
                        appUpdateInfo,  // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                        AppUpdateType.IMMEDIATE,  // The current activity making the update request.
                        this@SplashScreen,  // Include a request code to later monitor this update request.
                        MY_REQUEST_CODE
                    )
                } catch (e: IntentSender.SendIntentException) {
                }
            } else {
                //   checkUpdate()
            //    setHandler()
                if (CommonAppPermission.hasAllPermissionGranted(this@SplashScreen)) {
                    moveNextScreen()
                }else{
                    startActivity(Intent(this, PermissionActivity::class.java))
                }

            }
        }

        appUpdateInfoTask.addOnFailureListener {
            // checkUpdate()
           // setHandler()
            if (CommonAppPermission.hasAllPermissionGranted(this@SplashScreen)) {
                moveNextScreen()
            }else{
                startActivity(Intent(this, PermissionActivity::class.java))
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setHandler() {
        handler.postDelayed({
            moveNextScreen()
        }, 1000 * 1.toLong())
    }



    private fun moveNextScreen() {
        if (isLogin == "true") {

            callOnResune = "second"
            if (appFirst!!.isEmpty()) {

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

        }else {


            callOnResune = "second"

            Log.d("onRequestPermissi", "2")
            if (callOnlyOne.equals("OnlyOne")) {

                if (appFirst!!.isEmpty()) {

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
        }
    }
}