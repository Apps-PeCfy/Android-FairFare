package com.fairfareindia.ui.drawer.mydisput.pojo

import com.google.gson.annotations.SerializedName

class GetDisputResponsePOJO {
    @SerializedName("path")
    var path: String? = null

    @SerializedName("lastPageUrl")
    var lastPageUrl: String? = null

    @SerializedName("total")
    var total = 0

    @SerializedName("firstPageUrl")
    var firstPageUrl: String? = null

    @SerializedName("nextPageUrl")
    var nextPageUrl: Any? = null

    @SerializedName("perPage")
    var perPage = 0

    @SerializedName("data")
    var data: List<DataItem>? =
        null

    @SerializedName("lastPage")
    var lastPage = 0

    @SerializedName("message")
    var message: String? = null

    @SerializedName("currentPage")
    var currentPage = 0

    @SerializedName("prevPageUrl")
    var prevPageUrl: Any? = null

    override fun toString(): String {
        return "GetDisputResponsePOJO{" +
                "path = '" + path + '\'' +
                ",lastPageUrl = '" + lastPageUrl + '\'' +
                ",total = '" + total + '\'' +
                ",firstPageUrl = '" + firstPageUrl + '\'' +
                ",nextPageUrl = '" + nextPageUrl + '\'' +
                ",perPage = '" + perPage + '\'' +
                ",data = '" + data + '\'' +
                ",lastPage = '" + lastPage + '\'' +
                ",message = '" + message + '\'' +
                ",currentPage = '" + currentPage + '\'' +
                ",prevPageUrl = '" + prevPageUrl + '\'' +
                "}"
    }

    inner class DataItem {
        @SerializedName("reasonName")
        var reasonName: String? = null

        @SerializedName("dateTime")
        var dateTime: String? = null

        @SerializedName("destinationPlaceLat")
        var destinationPlaceLat: String? = null

        @SerializedName("fare")
        var fare = 0.0

        @SerializedName("images")
        var images: List<Any>? = null

        @SerializedName("vehicleName")
        var vehicleName: String? = null

        @SerializedName("startMeterReading")
        var startMeterReading: String? = null


        @SerializedName("originFullAddress")
        var originFullAddress: String? = null

        @SerializedName("destinationFullAddress")
        var destinationFullAddress: String? = null

        @SerializedName("estimatedDestinationFullAddress")
        var estimatedDestinationFullAddress: String? = null



        @SerializedName("vehicleImageUrl")
        var vehicleImageUrl: String? = null

        @SerializedName("originPlaceLong")
        var originPlaceLong: String? = null

        @SerializedName("actualMeterCharges")
        var actualMeterCharges : String?=null

        @SerializedName("originPlaceId")
        var originPlaceId: String? = null

        @SerializedName("rideId")
        var rideId = 0

        @SerializedName("type")
        var type: String? = null

        @SerializedName("disputeNo")
        var disputeNo: String? = null

        @SerializedName("originPlaceLat")
        var originPlaceLat: String? = null

        @SerializedName("destinationPlaceId")
        var destinationPlaceId: String? = null

        @SerializedName("vehicleNo")
        var vehicleNo: String? = null

        @SerializedName("reasonId")
        var reasonId = 0

        @SerializedName("driverName")
        var driverName: String? = null

        @SerializedName("id")
        var id = 0

        @SerializedName("endMeterReading")
        var endMeterReading: String? = null

        @SerializedName("createdDateTime")
        var createdDateTime: String? = null

        @SerializedName("destinationPlaceLong")
        var destinationPlaceLong: String? = null

        @SerializedName("badgeNo")
        var badgeNo: String? = null

        @SerializedName("status")
        var status: String? = null

        override fun toString(): String {
            return "DataItem{" +
                    "reasonName = '" + reasonName + '\'' +
                    ",dateTime = '" + dateTime + '\'' +
                    ",destinationPlaceLat = '" + destinationPlaceLat + '\'' +
                    ",fare = '" + fare + '\'' +
                    ",images = '" + images + '\'' +
                    ",vehicleName = '" + vehicleName + '\'' +
                    ",startMeterReading = '" + startMeterReading + '\'' +
                    ",vehicleImageUrl = '" + vehicleImageUrl + '\'' +
                    ",originPlaceLong = '" + originPlaceLong + '\'' +
                    ",actualMeterCharges = '" + actualMeterCharges + '\'' +
                    ",originPlaceId = '" + originPlaceId + '\'' +
                    ",rideId = '" + rideId + '\'' +
                    ",type = '" + type + '\'' +
                    ",disputeNo = '" + disputeNo + '\'' +
                    ",originPlaceLat = '" + originPlaceLat + '\'' +
                    ",destinationPlaceId = '" + destinationPlaceId + '\'' +
                    ",vehicleNo = '" + vehicleNo + '\'' +
                    ",reasonId = '" + reasonId + '\'' +
                    ",driverName = '" + driverName + '\'' +
                    ",id = '" + id + '\'' +
                    ",endMeterReading = '" + endMeterReading + '\'' +
                    ",destinationPlaceLong = '" + destinationPlaceLong + '\'' +
                    ",badgeNo = '" + badgeNo + '\'' +
                    ",status = '" + status + '\'' +
                    "}"
        }
    }
}