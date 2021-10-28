package com.fairfareindia.ui.intercityviewride

import com.google.gson.annotations.SerializedName

class BookingRequestModel {
    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: DataItem? = null



    inner class DataItem {
        @SerializedName("id")
        var id: Int? = null

        @SerializedName("user_id")
        var user_id: String? = null

        @SerializedName("driver_id")
        var driver_id: String? = null

        @SerializedName("vehicle_rate_card_id")
        var vehicle_rate_card_id: String? = null

        @SerializedName("intercity_rate_card_id")
        var intercity_rate_card_id: String? = null

        @SerializedName("type")
        var type: String? = null

        @SerializedName("shedule_date")
        var shedule_date: String? = null

        @SerializedName("way_flag")
        var way_flag: String? = null

        @SerializedName("origin_address")
        var origin_address: String? = null

        @SerializedName("destination_address")
        var destination_address: String? = null

        @SerializedName("origin_longitude")
        var origin_longitude: String? = null

        @SerializedName("origin_latitude")
        var origin_latitude: String? = null

        @SerializedName("destination_longitude")
        var destination_longitude: String? = null

        @SerializedName("destination_latitude")
        var destination_latitude: String? = null

    }
}
