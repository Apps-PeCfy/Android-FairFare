package com.fairfareindia.ui.compareride.pojo

import com.google.gson.annotations.SerializedName

class CompareRideResponse {
    @SerializedName("vehicles")
    var vehicles: List<VehiclesItem>? =
        null

    @SerializedName("message")
    var message: String? = null

    override fun toString(): String {
        return "CompareRideResponse{" +
                "vehicles = '" + vehicles + '\'' +
                ",message = '" + message + '\'' +
                "}"
    }

    inner class VehiclesItem {
        @SerializedName("vehicleName")
        var vehicleName: String? = null

        @SerializedName("vehicleImageUrl")
        var vehicleImageUrl: String? = null

        @SerializedName("providerImageUrl")
        var providerImageUrl: String? = null

        @SerializedName("providerName")
        var providerName: String? = null

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

    inner class FaresItem {
        @SerializedName("tollCharge")
        var tollCharge: String? = null

        @SerializedName("total")
        var total: String? = null

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
                    ",nightCharge = '" + nightCharge + '\'' +
                    ",subTotal = '" + subTotal + '\'' +
                    ",surCharge = '" + surCharge + '\'' +
                    "}"
        }
    }
}