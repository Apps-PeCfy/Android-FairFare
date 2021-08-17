package com.fairfareindia.ui.test

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fairfareindia.R
import com.google.android.gms.location.LocationListener

class test : AppCompatActivity(), LocationListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }

    override fun onLocationChanged(p0: Location?) {
        TODO("Not yet implemented")
    }


}
