package com.fairfareindia.ui.endrides

import org.json.JSONArray
import java.util.ArrayList
import java.util.HashMap

interface IEndRidePresenter {

    fun endRide(

        token: String?,
        rideId: String?,
        distance: Double?,
        duration: String?,
        arrWaitTime: ArrayList<HashMap<String, String>>,
        endLat: String?,
        endLon: String?,
        endAddress: String?,
        originLat: String?,
        originLon: String?,
        originAddress: String?,
        DistanceNightCharge: String?,
        tollsJSONArrayFromTollGuru: JSONArray

    )

}