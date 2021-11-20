package com.fairfareindia.ui.intercitytrackpickup

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import com.fairfareindia.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowGoogleMap(val context: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(p0: Marker?): View {
        var mInfoView = (context as Activity).layoutInflater.inflate(R.layout.marker_time, null)
        var txtTime : TextView = mInfoView.findViewById(R.id.txt_time)
        var txtTimeUnit : TextView = mInfoView.findViewById(R.id.txt_time_unit)
        txtTime.text = p0?.title
        txtTimeUnit.text = p0?.snippet
        return mInfoView
    }

    override fun getInfoWindow(p0: Marker?): View? {
        return null
    }
}