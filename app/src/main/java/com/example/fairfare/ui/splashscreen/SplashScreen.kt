package com.example.fairfare.ui.splashscreen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.fairfare.R
import com.example.fairfare.ui.Login.LoginActivity
import com.example.fairfare.ui.home.HomeActivity
import com.example.fairfare.utils.Constants
import com.example.fairfare.utils.PreferencesManager
import com.example.fairfare.utils.ProjectUtilities

class SplashScreen : AppCompatActivity() {
    var isLogin: String? = null
    var mPreferencesManager: PreferencesManager? = null
    var sharedpreferences: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        PreferencesManager.initializeInstance(this@SplashScreen)
        mPreferencesManager = PreferencesManager.instance
        sharedpreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE)
        sharedpreferences!!.edit().clear().commit()
        mPreferencesManager!!.setStringValue(Constants.SHARED_PREFERENCE_PICKUP_AITPORT,"LOCALITY")
        isLogin = mPreferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_ISLOGIN)
        val h = Handler()
        h.postDelayed({
            val isAccepted =
                ProjectUtilities.checkPermission(applicationContext)
            if (isAccepted) {
                checkUpdate()
            } else {
                ActivityCompat.requestPermissions(
                    this@SplashScreen, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    REQUEST_PERMISSION
                )
            }
        }, 1000 * 1.toLong())
    }

    private fun checkUpdate() {
        if (isLogin == "true") {
            val intent = Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION) {
            if (isLogin == "true") {
                val intent = Intent(applicationContext, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    companion object {
        private const val REQUEST_PERMISSION = 100
    }
}