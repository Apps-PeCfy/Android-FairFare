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

    }
}
