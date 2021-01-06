package com.example.fairfare.ui.compareride.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CompareRideResponsePOJO : Serializable {
    @SerializedName("vehicles")
    var vehicles: List<VehiclesItem>? = null

    @SerializedName("scheduleMinutes")
    var scheduleMinutes: String? = null

    @SerializedName("distance")
    var distance: String? = null

    @SerializedName("travelTime")
    var travelTime: String? = null

    @SerializedName("scheduleDatetime")
    var scheduleDatetime: String? = null

    @SerializedName("luggage")
    var luggage: String? = null

    @SerializedName("canStartRide")
    var canStartRide: String? = null

    @SerializedName("message")
    var message: String? = null

    override fun toString(): String {
        return "CompareRideResponse{" +
                "vehicles = '" + vehicles + '\'' +
                ",message = '" + message + '\'' +
                ",scheduleMinutes = '" + scheduleMinutes + '\'' +
                ",distance = '" + distance + '\'' +
                ",travelTime = '" + travelTime + '\'' +
                ",scheduleDatetime = '" + scheduleDatetime + '\'' +
                ",luggage = '" + luggage + '\'' +
                "}"
    }

    inner class VehiclesItem : Serializable {
        @SerializedName("vehicleName")
        var vehicleName: String? = null

        @SerializedName("vehicleImageUrl")
        var vehicleImageUrl: String? = null

        @SerializedName("providerImageUrl")
        var providerImageUrl: String? = null

        @SerializedName("providerName")
        var providerName: String? = null

        @SerializedName("noOfSeater")
        var noOfSeater = 0

        @SerializedName("fares")
        var fares: List<FaresItem>? =
            null

        override fun toString(): String {
            return "VehiclesItem{" +
                    "vehicleName = '" + vehicleName + '\'' +
                    ",vehicleImageUrl = '" + vehicleImageUrl + '\'' +
                    ",providerImageUrl = '" + providerImageUrl + '\'' +
                    ",providerName = '" + providerName + '\'' +
                    ",fares = '" + fares + '\'' +
                    "}"
        }
    }

    inner class FaresItem : Serializable {
        @SerializedName("tollCharge")
        var tollCharge: String? = null

        @SerializedName("vehicleRateCardId")
        var vehicleRateCardId: String? = null

        @SerializedName("airportRateCardId")
        var airportRateCardId: String? = null

        @SerializedName("total")
        var total: String? = null

        @SerializedName("additionalCharges")
        var additionalCharges: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("luggageCharge")
        var luggageCharge: String? = null

        @SerializedName("nightCharge")
        var nightCharge: String? = null

        @SerializedName("subTotal")
        var subTotal: String? = null

        @SerializedName("surCharge")
        var surCharge: String? = null

        override fun toString(): String {
            return "FaresItem{" +
                    "tollCharge = '" + tollCharge + '\'' +
                    ",total = '" + total + '\'' +
                    ",name = '" + name + '\'' +
                    ",luggageCharge = '" + luggageCharge + '\'' +
                    ",vehicleRateCardId = '" + luggageCharge + '\'' +
                    ",airportRateCardId = '" + luggageCharge + '\'' +
                    ",nightCharge = '" + nightCharge + '\'' +
                    ",subTotal = '" + subTotal + '\'' +
                    ",surCharge = '" + surCharge + '\'' +
                    "}"
        }
    }


}