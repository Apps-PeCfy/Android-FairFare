package com.fairfareindia.fcm

import com.google.gson.annotations.SerializedName

class NotificationDataModel {

    @SerializedName("ride_status")
    var ride_status: String? = null

    @SerializedName("ride_id")
    var ride_id:String? = null
}