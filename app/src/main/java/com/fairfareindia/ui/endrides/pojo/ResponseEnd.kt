package com.fairfareindia.ui.endrides.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResponseEnd {
    @SerializedName("message")
    var message: String? = null

    @SerializedName("rideRewards")
    var rewards: String? = null

    @SerializedName("rewards")
    var rewardsupdate: String? = null

    @SerializedName("ride")
    var ride: Ride? = null

    override fun toString(): String {
        return "Response{" +
                "message = '" + message + '\'' +
                ",ride = '" + ride + '\'' +
                "}"
    }

    inner class Ride {
        @SerializedName("luggageCharges")
        var luggageCharges: String? = null

        @SerializedName("vehicleDetail")
        var vehicleDetail: VehicleDetail? = null

        @SerializedName("vehicleName")
        var vehicleName: String? = null

        @SerializedName("estimatedTrackRide")
        var estimatedTrackRide: EstimatedTrackRide? =
            null

        @SerializedName("vehicleImageUrl")
        var vehicleImageUrl: String? = null

        @SerializedName("endDate")
        var endDate: String? = null

        @SerializedName("vehicleRateCardId")
        var vehicleRateCardId: String? = null

        @SerializedName("cityId")
        var cityId: String? = null

        @SerializedName("userId")
        var userId = 0

        @SerializedName("nightCharges")
        var nightCharges: String? = null

        @SerializedName("cityName")
        var cityName: String? = null

        @SerializedName("luggageQuantity")
        var luggageQuantity: String? = null

        @SerializedName("airportRateCardId")
        var airportRateCardId: String? = null

        @SerializedName("scheduleDate")
        var scheduleDate: String? = null

        @SerializedName("actualTrackRide")
        var actualTrackRide: ActualTrackRide? = null

        @SerializedName("id")
        var id = 0

        @SerializedName("status")
        var status: String? = null

        override fun toString(): String {
            return "Ride{" +
                    "luggageCharges = '" + luggageCharges + '\'' +
                    ",vehicleDetail = '" + vehicleDetail + '\'' +
                    ",vehicleName = '" + vehicleName + '\'' +
                    ",estimatedTrackRide = '" + estimatedTrackRide + '\'' +
                    ",vehicleImageUrl = '" + vehicleImageUrl + '\'' +
                    ",endDate = '" + endDate + '\'' +
                    ",vehicleRateCardId = '" + vehicleRateCardId + '\'' +
                    ",cityId = '" + cityId + '\'' +
                    ",userId = '" + userId + '\'' +
                    ",nightCharges = '" + nightCharges + '\'' +
                    ",cityName = '" + cityName + '\'' +
                    ",luggageQuantity = '" + luggageQuantity + '\'' +
                    ",airportRateCardId = '" + airportRateCardId + '\'' +
                    ",scheduleDate = '" + scheduleDate + '\'' +
                    ",actualTrackRide = '" + actualTrackRide + '\'' +
                    ",id = '" + id + '\'' +
                    ",status = '" + status + '\'' +
                    "}"
        }
    }

    inner class VehicleDetail {
        @SerializedName("vehicleNo")
        var vehicleNo: String? = null

        @SerializedName("images")
        var images: List<Any>? = null

        @SerializedName("startMeterReading")
        var startMeterReading = 0

        @SerializedName("driverName")
        var driverName: String? = null

        @SerializedName("id")
        var id = 0

        @SerializedName("badgeNo")
        var badgeNo: String? = null

        override fun toString(): String {
            return "VehicleDetail{" +
                    "vehicleNo = '" + vehicleNo + '\'' +
                    ",images = '" + images + '\'' +
                    ",startMeterReading = '" + startMeterReading + '\'' +
                    ",driverName = '" + driverName + '\'' +
                    ",id = '" + id + '\'' +
                    ",badgeNo = '" + badgeNo + '\'' +
                    "}"
        }
    }

    inner class ActualTrackRide {
        @SerializedName("destinationPlaceLat")
        var destinationPlaceLat: String? = null

        @SerializedName("distance")
        var distance: String? = null

        @SerializedName("originPlaceLong")
        var originPlaceLong: String? = null

        @SerializedName("originPlaceId")
        var originPlaceId: String? = null

        @SerializedName("waitingTime")
        var waitingTime: String? = null


        @SerializedName("tollCharges")
        var tollCharges: String? = null



        @SerializedName("type")
        var type: String? = null

        @SerializedName("rideId")
        var rideId = 0

        @SerializedName("originPlaceLat")
        var originPlaceLat: String? = null

        @SerializedName("surCharge")
        var surCharge: String? = null

        @SerializedName("destinationPlaceId")
        var destinationPlaceId: String? = null

        @SerializedName("originFullAddress")
        var originFullAddress: String? = null

        @SerializedName("waitings")
        var waitings: List<WaitingsItem1>? = null

        @SerializedName("tolls")
        var tolls: List<TollsItem>? = null

        @SerializedName("duration")
        var duration: String? = null

        @SerializedName("destinationFullAddress")
        var destinationFullAddress: String? = null

        @SerializedName("waitingCharges")
        var waitingCharges: String? = null

        @SerializedName("overviewPolyline")
        var overviewPolyline: String? = null

        @SerializedName("totalCharges")
        var totalCharges: String? = null

        @SerializedName("id")
        var id = 0

        @SerializedName("destinationPlaceLong")
        var destinationPlaceLong: String? = null

        @SerializedName("subTotalCharges")
        var subTotalCharges: String? = null

        override fun toString(): String {
            return "ActualTrackRide{" +
                    "destinationPlaceLat = '" + destinationPlaceLat + '\'' +
                    ",distance = '" + distance + '\'' +
                    ",originPlaceLong = '" + originPlaceLong + '\'' +
                    ",originPlaceId = '" + originPlaceId + '\'' +
                    ",waitingTime = '" + waitingTime + '\'' +
                    ",type = '" + type + '\'' +
                    ",rideId = '" + rideId + '\'' +
                    ",originPlaceLat = '" + originPlaceLat + '\'' +
                    ",surCharge = '" + surCharge + '\'' +
                    ",destinationPlaceId = '" + destinationPlaceId + '\'' +
                    ",originFullAddress = '" + originFullAddress + '\'' +
                    ",waitings = '" + waitings + '\'' +
                    ",duration = '" + duration + '\'' +
                    ",destinationFullAddress = '" + destinationFullAddress + '\'' +
                    ",waitingCharges = '" + waitingCharges + '\'' +
                    ",overviewPolyline = '" + overviewPolyline + '\'' +
                    ",totalCharges = '" + totalCharges + '\'' +
                    ",id = '" + id + '\'' +
                    ",destinationPlaceLong = '" + destinationPlaceLong + '\'' +
                    ",subTotalCharges = '" + subTotalCharges + '\'' +
                    "}"
        }
    }

    inner class EstimatedTrackRide {
        @SerializedName("destinationPlaceLat")
        var destinationPlaceLat: String? = null

        @SerializedName("distance")
        var distance: String? = null


        @SerializedName("tollCharges")
        var tollCharges: String? = null


        @SerializedName("originPlaceLong")
        var originPlaceLong: String? = null

        @SerializedName("originPlaceId")
        var originPlaceId: String? = null

        @SerializedName("waitingTime")
        var waitingTime: String? = null

        @SerializedName("type")
        var type: String? = null

        @SerializedName("rideId")
        var rideId = 0

        @SerializedName("originPlaceLat")
        var originPlaceLat: String? = null

        @SerializedName("surCharge")
        var surCharge: String? = null

        @SerializedName("destinationPlaceId")
        var destinationPlaceId: String? = null

        @SerializedName("originFullAddress")
        var originFullAddress: String? = null

        @SerializedName("waitings")
        var waitings: List<Any>? = null

        @SerializedName("tolls")
        var tolls: List<TollsItem>? = null


        @SerializedName("duration")
        var duration: String? = null

        @SerializedName("destinationFullAddress")
        var destinationFullAddress: String? = null

        @SerializedName("waitingCharges")
        var waitingCharges: String? = null

        @SerializedName("overviewPolyline")
        var overviewPolyline: String? = null

        @SerializedName("totalCharges")
        var totalCharges: String? = null

        @SerializedName("id")
        var id = 0

        @SerializedName("destinationPlaceLong")
        var destinationPlaceLong: String? = null

        @SerializedName("subTotalCharges")
        var subTotalCharges: String? = null

        override fun toString(): String {
            return "EstimatedTrackRide{" +
                    "destinationPlaceLat = '" + destinationPlaceLat + '\'' +
                    ",distance = '" + distance + '\'' +
                    ",originPlaceLong = '" + originPlaceLong + '\'' +
                    ",originPlaceId = '" + originPlaceId + '\'' +
                    ",waitingTime = '" + waitingTime + '\'' +
                    ",type = '" + type + '\'' +
                    ",rideId = '" + rideId + '\'' +
                    ",originPlaceLat = '" + originPlaceLat + '\'' +
                    ",surCharge = '" + surCharge + '\'' +
                    ",destinationPlaceId = '" + destinationPlaceId + '\'' +
                    ",originFullAddress = '" + originFullAddress + '\'' +
                    ",waitings = '" + waitings + '\'' +
                    ",duration = '" + duration + '\'' +
                    ",destinationFullAddress = '" + destinationFullAddress + '\'' +
                    ",waitingCharges = '" + waitingCharges + '\'' +
                    ",overviewPolyline = '" + overviewPolyline + '\'' +
                    ",totalCharges = '" + totalCharges + '\'' +
                    ",id = '" + id + '\'' +
                    ",destinationPlaceLong = '" + destinationPlaceLong + '\'' +
                    ",subTotalCharges = '" + subTotalCharges + '\'' +
                    "}"
        }
    }

    inner class WaitingsItem1 {
        @SerializedName("waitAt")
        var waitAt: String? = null

        @SerializedName("waitingTimeText")
        var waitingTimeText: String? = null

        @SerializedName("fullAddress")
        var fullAddress: String? = null

        @SerializedName("waitingTime")
        var waitingTime = 0

        @SerializedName("id")
        var id = 0

        @SerializedName("lat")
        var lat: String? = null

        @SerializedName("long")
        var jsonMemberLong: String? = null

        override fun toString(): String {
            return "WaitingsItem{" +
                    "waitAt = '" + waitAt + '\'' +
                    ",waitingTimeText = '" + waitingTimeText + '\'' +
                    ",fullAddress = '" + fullAddress + '\'' +
                    ",waitingTime = '" + waitingTime + '\'' +
                    ",id = '" + id + '\'' +
                    ",lat = '" + lat + '\'' +
                    ",long = '" + jsonMemberLong + '\'' +
                    "}"
        }
    }

    inner class TollsItem : Serializable {
        @SerializedName("name")
        var name: String? = null

        @SerializedName("road")
        var road: String? = null

        @SerializedName("state")
        var state: String? = null

        @SerializedName("country")
        var country: String? = null

        @SerializedName("type")
        var type: String? = null

        @SerializedName("currency")
        var currency: String? = null

        @SerializedName("latitude")
        var latitude: String? = null

        @SerializedName("longitude")
        var longitude: String? = null


        @SerializedName("charges")
        var charges = 0



    }
}