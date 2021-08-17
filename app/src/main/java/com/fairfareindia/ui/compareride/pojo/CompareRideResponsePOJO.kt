package com.fairfareindia.ui.compareride.pojo

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

        @SerializedName("tollCharges")
        var tollCharges: String? = null


        @SerializedName("tolls")
        var tolls: List<TollsItem>? = null


        @SerializedName("providerImageUrl")
        var providerImageUrl: String? = null

        @SerializedName("providerName")
        var providerName: String? = null

        @SerializedName("label")
        var label: String? = null

        @SerializedName("noOfSeater")
        var noOfSeater = 0

        @SerializedName("vehicleRateCardId")
        var vehicleRateCardId: String? = null

        @SerializedName("airportRateCardId")
        var airportRateCardId: String? = null

        @SerializedName("total")
        var total: String? = null


        @SerializedName("tollCharge")
        var tollCharge: String? = null


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
            return "VehiclesItem{" +
                    "vehicleName = '" + vehicleName + '\'' +
                    ",vehicleImageUrl = '" + vehicleImageUrl + '\'' +
                    ",providerImageUrl = '" + providerImageUrl + '\'' +
                    ",providerName = '" + providerName + '\'' +
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