package com.fairfareindia.ui.drawer.myrides.ridedetails

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RideDetailsResponsePOJO {
    @SerializedName("data")
    var data: Data? =
        null

    @SerializedName("message")
    var message: String? = null

    override fun toString(): String {
        return "RideDetailsResponsePOJO{" +
                "data = '" + data + '\'' +
                ",message = '" + message + '\'' +
                "}"
    }

    inner class Data {
        @SerializedName("dateTime")
        var dateTime: String? = null

        @SerializedName("fare")
        var fare = 0.0

        @SerializedName("vehicleName")
        var vehicleName: String? = null

        @SerializedName("cityId")
        var cityId: String? = null

        @SerializedName("rewards")
        var rewards: String? = null

        @SerializedName("originPlaceLat")
        var originPlaceLat: String? = null

        @SerializedName("destinationFullAddress")
        var destinationFullAddress: String? = null

        @SerializedName("cityName")
        var cityName: String? = null

        @SerializedName("overviewPolyline")
        var overviewPolyline: String? = null

        @SerializedName("reviews")
        var reviews: List<ReviewsItem>? = null

        @SerializedName("vehicleNoImages")
        var vehicleNoImages: List<String>? = null

        @SerializedName("startMeterImages")
        var startMeterImages: List<String>? = null

        @SerializedName("driverImages")
        var driverImages: List<String>? = null

        @SerializedName("badgeNoImages")
        var badgeNoImages: List<String>? = null

        @SerializedName("airportRateCardId")
        var airportRateCardId: String? = null

        @SerializedName("id")
        var id = 0

        @SerializedName("badgeNo")
        var badgeNo: String? = null

        @SerializedName("destinationPlaceLat")
        var destinationPlaceLat: String? = null

        @SerializedName("estimatedTrackRide")
        var estimatedTrackRide: EstimatedTrackRide? =
            null

        @SerializedName("vehicleImageUrl")
        var vehicleImageUrl: String? = null

        @SerializedName("originPlaceLong")
        var originPlaceLong: String? = null

        @SerializedName("originPlaceId")
        var originPlaceId: String? = null

        @SerializedName("vehicleRateCardId")
        var vehicleRateCardId: String? = null

        @SerializedName("userId")
        var userId = 0

        @SerializedName("destinationPlaceId")
        var destinationPlaceId: String? = null

        @SerializedName("originFullAddress")
        var originFullAddress: String? = null

        @SerializedName("vehicleNo")
        var vehicleNo: String? = null

        @SerializedName("luggageQuantity")
        var luggageQuantity: String? = null

        @SerializedName("luggageCharges")
        var luggageCharges: String? = null

        @SerializedName("nightCharges")
        var nightCharges: String? = null

        @SerializedName("reviewStar")
        var reviewStar = 0

        @SerializedName("actualTrackRide")
        var actualTrackRide: ActualTrackRide? =
            null

        @SerializedName("driverName")
        var driverName: String? = null

        @SerializedName("destinationPlaceLong")
        var destinationPlaceLong: String? = null

        @SerializedName("status")
        var status: String? = null

        override fun toString(): String {
            return "Data{" +
                    "dateTime = '" + dateTime + '\'' +
                    ",fare = '" + fare + '\'' +
                    ",vehicleName = '" + vehicleName + '\'' +
                    ",cityId = '" + cityId + '\'' +
                    ",originPlaceLat = '" + originPlaceLat + '\'' +
                    ",destinationFullAddress = '" + destinationFullAddress + '\'' +
                    ",cityName = '" + cityName + '\'' +
                    ",overviewPolyline = '" + overviewPolyline + '\'' +
                    ",reviews = '" + reviews + '\'' +
                    ",airportRateCardId = '" + airportRateCardId + '\'' +
                    ",id = '" + id + '\'' +
                    ",badgeNo = '" + badgeNo + '\'' +
                    ",vehicleNoImages = '" + vehicleNoImages + '\'' +
                    ",startMeterImages = '" + startMeterImages + '\'' +
                    ",driverImages = '" + driverImages + '\'' +
                    ",badgeNoImages = '" + badgeNoImages + '\'' +
                    ",destinationPlaceLat = '" + destinationPlaceLat + '\'' +
                    ",estimatedTrackRide = '" + estimatedTrackRide + '\'' +
                    ",vehicleImageUrl = '" + vehicleImageUrl + '\'' +
                    ",originPlaceLong = '" + originPlaceLong + '\'' +
                    ",originPlaceId = '" + originPlaceId + '\'' +
                    ",vehicleRateCardId = '" + vehicleRateCardId + '\'' +
                    ",userId = '" + userId + '\'' +
                    ",destinationPlaceId = '" + destinationPlaceId + '\'' +
                    ",originFullAddress = '" + originFullAddress + '\'' +
                    ",vehicleNo = '" + vehicleNo + '\'' +
                    ",reviewStar = '" + reviewStar + '\'' +
                    ",actualTrackRide = '" + actualTrackRide + '\'' +
                    ",driverName = '" + driverName + '\'' +
                    ",destinationPlaceLong = '" + destinationPlaceLong + '\'' +
                    ",status = '" + status + '\'' +
                    "}"
        }
    }

    inner class ReviewsItem {
        @SerializedName("reviews")
        var reviews: String? = null

        @SerializedName("context_id")
        var contextId = 0

        @SerializedName("id")
        var id = 0

        @SerializedName("stars")
        var stars = 0

        override fun toString(): String {
            return "ReviewsItem{" +
                    "reviews = '" + reviews + '\'' +
                    ",context_id = '" + contextId + '\'' +
                    ",id = '" + id + '\'' +
                    ",stars = '" + stars + '\'' +
                    "}"
        }
    }

    inner class ActualTrackRide {
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
}