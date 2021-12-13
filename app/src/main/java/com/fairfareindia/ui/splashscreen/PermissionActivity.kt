package com.fairfareindia.ui.splashscreen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.fairfareindia.R
import com.fairfareindia.databinding.ActivityPermissionBinding
import com.fairfareindia.ui.Login.LoginActivity
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.ui.introduction.IntroActivity
import com.fairfareindia.utils.*
import com.google.android.gms.location.LocationSettingsStates

class PermissionActivity : AppCompatActivity() {
    lateinit var binding: ActivityPermissionBinding
    private var context: Context = this


    private var isLogin: String? = null
    private var appFirst: String? = null
    private var mPreferencesManager: PreferencesManager? = null
    private var appSharedPreferences: SharedPreferences? = null
    private var myLocationManager: MyLocationManager? = MyLocationManager(this)

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun init() {

        PreferencesManager.initializeInstance(context)
        mPreferencesManager = PreferencesManager.instance
        appSharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        appFirst = appSharedPreferences?.getString("AppOpen", "")

        isLogin = mPreferencesManager?.getStringValue(Constants.SHARED_PREFERENCE_ISLOGIN)

        setListeners()
        setData()
        hideSystemUI()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setData() {
        var alertMessage = ""
        if (!CommonAppPermission.hasAllPermissionGranted(context)) {
            binding.txtPermissionTitle.text = getString(R.string.title_welcome_to_fair_fare)

             alertMessage =
                getString(R.string.msg_permission_one)

           /* if (!CommonAppPermission.hasCameraPermission(this)) {
                alertMessage += "\n\nCamera: Please provide camera permission"
            }
            if (!CommonAppPermission.hasExternalStoragePermission(this)) {
                alertMessage += "\n\nMedia: Please provide Media permission"
            }*/
            if (!CommonAppPermission.hasLocationPermission(this)) {
                //make the message as per android 10 and above and below


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    alertMessage += "\n\n " + getString(R.string.msg_all_time_location_permission)
                } else {
                    alertMessage += "\n\n" + getString(R.string.msg_location_permission)
                }
            }

        }else{
            if(!ProjectUtilities.isGPSEnabled(context)){
                binding.txtPermissionTitle.text = getString(R.string.str_gps_turned_off)
                alertMessage = getString(R.string.msg_allow_gps_on)
                binding.btnAllow.text = getString(R.string.str_turn_on_gps)
            }else{
                moveNextScreen()
            }
        }

        binding.txtMessage.text = alertMessage





    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setListeners() {
        binding.apply {
            btnAllow.setOnClickListener {
                if (CommonAppPermission.hasAllPermissionGranted(context)) {
                    if(ProjectUtilities.isGPSEnabled(context)){
                        moveNextScreen()
                    }else{
                        turnOnGPS()
                    }

                }else{
                   showPermissionFlow()
                }
            }
        }
    }

    private fun turnOnGPS() {
        myLocationManager?.getMyCurrentLocationChange(object :
            MyLocationManager.LocationManagerTrackInterface {
            override fun onMyLocationChange(
                currentLocation: MutableList<Location>?,
                lastLocation: Location?
            ) {
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showPermissionFlow() {
        CommonAppPermission.requestPermissionGranted(context)
    }

    private fun moveNextScreen() {
        if (appFirst!!.isEmpty()) {

            val editor = appSharedPreferences?.edit()
            editor?.putString("AppOpen", "appOpenFirst")
            editor?.apply()
            val intent = Intent(applicationContext, IntroActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            if (isLogin == "true") {
                val intent = Intent(applicationContext, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }


        }
    }

    override fun onBackPressed() {

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        CommonAppPermission.requestPermissionGranted(context)
        setData()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        val states: LocationSettingsStates = LocationSettingsStates.fromIntent(data)
        when (requestCode) {
            1000 ->
                if (resultCode == Activity.RESULT_OK){
                    moveNextScreen()
                }else if (resultCode == Activity.RESULT_CANCELED){
                    setData()
                }
        }
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
}