package com.fairfareindia.ui.drawer.intercitydispute

import com.fairfareindia.ui.drawer.mydisput.disputDetail.pojo.DisputDetailResponsePOJO
import com.fairfareindia.ui.intercitytrackpickup.RideDetailModel
import com.google.gson.annotations.SerializedName

class DisputeDetailModel {
    @SerializedName("dispute")
    var dispute: Dispute? = null

    @SerializedName("message")
    var message: String? = null


    inner class Dispute {

        @SerializedName("id")
        var id = 0

        @SerializedName("rideId")
        var rideId = 0

        @SerializedName("ride")
        var ride: RideDetailModel.DataItem? = null

        @SerializedName("type")
        var type: String? = null

        @SerializedName("disputeNo")
        var disputeNo: String? = null


        @SerializedName("dateTime")
        var dateTime: String? = null


        @SerializedName("comment")
        var comment: String? = null


        @SerializedName("vehicleName")
        var vehicleName: String? = null


        @SerializedName("startMeterReading")
        var startMeterReading: String? = null

        @SerializedName("endMeterReading")
        var endMeterReading: String? = null

        @SerializedName("actualMeterCharges")
        var actualMeterCharges: String? = null

        @SerializedName("originPlaceLat")
        var originPlaceLat: String? = null

        @SerializedName("destinationFullAddress")
        var destinationFullAddress: String? = null

        @SerializedName("reasonId")
        var reasonId = 0

        @SerializedName("reasons")
        val reasons: List<DisputDetailResponsePOJO.ReasonsItem>? = null

        @SerializedName("badgeNo")
        var badgeNo: String? = null

        @SerializedName("reasonName")
        var reasonName: String? = null

        @SerializedName("destinationPlaceLat")
        var destinationPlaceLat: String? = null


        @SerializedName("images")
        var images: ArrayList<String?>? = null

        @SerializedName("vehicleImageUrl")
        var vehicleImageUrl: String? = null


        @SerializedName("originPlaceLong")
        var originPlaceLong: String? = null

        @SerializedName("originPlaceId")
        var originPlaceId: String? = null


        @SerializedName("destinationPlaceId")
        var destinationPlaceId: String? = null

        @SerializedName("originFullAddress")
        var originFullAddress: String? = null

        @SerializedName("vehicleNo")
        var vehicleNo: String? = null

        @SerializedName("driverName")
        var driverName: String? = null


        @SerializedName("destinationPlaceLong")
        var destinationPlaceLong: String? = null

        @SerializedName("status")
        var status: String? = null

        @SerializedName("fare")
        var fare: String? = null
    }


}
