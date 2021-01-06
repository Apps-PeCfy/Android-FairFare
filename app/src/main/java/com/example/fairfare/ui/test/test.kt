package com.example.fairfare.ui.test

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fairfare.R
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
