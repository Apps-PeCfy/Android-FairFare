package com.fairfareindia.ui.intercitytrackpickup

import com.google.gson.annotations.SerializedName

class DriverLocationModel {
    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: DataItem? = null

    inner class DataItem {

        @SerializedName("driver_id")
        var driver_id = 0

        @SerializedName("latitude")
        var latitude: Double = 0.0

        @SerializedName("longitude")
        var longitude: Double = 0.0

        @SerializedName("bearing")
        var bearing: Float = 0F

        @SerializedName("bearing_accuracy_degrees")
        var bearing_accuracy_degrees: Float = 0F

        @SerializedName("status")
        var status: String ?= null

        @SerializedName("permit_type")
        var permit_type: String ?= null

        @SerializedName("total_wait_time")
        var total_wait_time : Int = 0

        @SerializedName("totalfare")
        var totalfare : Double = 0.0

        @SerializedName("totalDistTravelled")
        var totalDistTravelled: String? = null

        @SerializedName("ride_wait_time")
        var ride_wait_time : Int = 0

    }
}
