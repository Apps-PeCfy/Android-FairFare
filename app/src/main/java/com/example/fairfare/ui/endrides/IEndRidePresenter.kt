package com.example.fairfare.ui.endrides

import java.util.ArrayList
import java.util.HashMap

interface IEndRidePresenter {

    fun endRide(

        token: String?,
        rideId: String?,
        distance: String?,
        duration: String?,
        arrWaitTime: ArrayList<HashMap<String, String>>
    )

}