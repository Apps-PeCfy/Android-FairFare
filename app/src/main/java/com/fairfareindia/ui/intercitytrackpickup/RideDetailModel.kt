package com.fairfareindia.ui.intercitytrackpickup
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RideDetailModel : Serializable {
    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: DataItem? = null

    inner class DataItem: Serializable {

        @SerializedName("id")
        var id = 0

        @SerializedName("userId")
        var userId = 0

        @SerializedName("cityName")
        var cityName: String? = null

        @SerializedName("cityId")
        var cityId: String? = null

        @SerializedName("vehicleRateCardId")
        var vehicleRateCardId: String? = null

        @SerializedName("airportRateCardId")
        var airportRateCardId: String? = null

        @SerializedName("vehicleImageUrl")
        var vehicleImageUrl: String? = null

        @SerializedName("vehicleName")
        var vehicleName: String? = null

        @SerializedName("vehicleType")
        var vehicleType: String? = null

        @SerializedName("luggageQuantity")
        var luggageQuantity: String? = null

        @SerializedName("luggageCharges")
        var luggageCharges: String? = null

        @SerializedName("nightCharges")
        var nightCharges: String? = null

        @SerializedName("scheduleDate")
        var scheduleDate: String? = null

        @SerializedName("endDate")
        var endDate: String? = null

        @SerializedName("status")
        var status: String? = null

        @SerializedName("rewards")
        var rewards: String? = null

        @SerializedName("canToll")
        var canToll: String? = null

        @SerializedName("vehicleDetail")
        var vehicleDetail: VehicleDetails? = null

        @SerializedName("estimatedTrackRide")
        var estimatedTrackRide: Estimated? = null

        @SerializedName("actual")
        var actualTrackRide: Estimated? = null
    }

    inner class VehicleDetails: Serializable{

        @SerializedName("id")
        var id = 0

        @SerializedName("vehicleNo")
        var vehicleNo: String? = null

        @SerializedName("startMeterReading")
        var startMeterReading = 0

        @SerializedName("badgeNo")
        var badgeNo: String? = null

        @SerializedName("driverName")
        var driverName: String? = null
    }

    inner class Estimated: Serializable {

        @SerializedName("id")
        var id = 0

        @SerializedName("type")
        var type: String? = null

        @SerializedName("rideId")
        var rideId = 0

        @SerializedName("waitingCharges")
        var waitingCharges: String? = null

        @SerializedName("waitingTime")
        var waitingTime: String? = null

        @SerializedName("surCharge")
        var surCharge: String? = null

        @SerializedName("originPlaceId")
        var originPlaceId: String? = null

        @SerializedName("originPlaceLat")
        var originPlaceLat: String? = null

        @SerializedName("originPlaceLong")
        var originPlaceLong: String? = null

        @SerializedName("originFullAddress")
        var originFullAddress: String? = null

        @SerializedName("destinationPlaceId")
        var destinationPlaceId: String? = null

        @SerializedName("destinationPlaceLat")
        var destinationPlaceLat: String? = null

        @SerializedName("destinationPlaceLong")
        var destinationPlaceLong: String? = null

        @SerializedName("destinationFullAddress")
        var destinationFullAddress: String? = null

        @SerializedName("overviewPolyline")
        var overviewPolyline: String? = null

        @SerializedName("distance")
        var distance: String? = null

        @SerializedName("duration")
        var duration: String? = null

        @SerializedName("subTotalCharges")
        var subTotalCharges: Int? = null

        @SerializedName("totalCharges")
        var totalCharges: String? = null

        @SerializedName("base_distance")
        var baseDistance: String? = null

        @SerializedName("convenience_fees")
        var convenienceFee: String? = null

        @SerializedName("additional_distance")
        var additionalDistance: String? = null

        @SerializedName("total_duration")
        var total_duration: String? = null

        @SerializedName("basic_fare")
        var basic_fare: Int? = null

        @SerializedName("luggage_charges")
        var luggage_charges: String? = null

        @SerializedName("rideTime")
        var rideTime: String? = null

        @SerializedName("additionalDistanceCharges")
        var additionalDistanceCharges: String? = null

        @SerializedName("tollCharges")
        var tollCharges: String? = null
    }
}