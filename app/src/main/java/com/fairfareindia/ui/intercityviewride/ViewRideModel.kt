package com.fairfareindia.ui.intercityviewride

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ViewRideModel {
    @SerializedName("message")
    var message: String? = null

    @SerializedName("ride")
    var ride: Rides? = null


    inner class Rides {
        @SerializedName("id")
        var id: String? = null

        @SerializedName("vehicle")
        var vehicle: VehicleDetail? = null

        @SerializedName("fromCity")
        var fromCity: String? = null

        @SerializedName("toCity")
        var toCity: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("baseDistance")
        var baseDistance: Double = 0.0

        @SerializedName("actualDistance")
        var actualDistance: Double = 0.0

        @SerializedName("baseFare")
        var baseFare: Double = 0.0

        @SerializedName("distanceType")
        var distanceType: String? = null

        @SerializedName("fareForAddDist")
        var fareForAddDist: Double = 0.0

        @SerializedName("chargesPerLuggage")
        var chargesPerLuggage: Double = 0.0

        @SerializedName("chargesLuggage")
        var chargesLuggage: Double = 0.0

        @SerializedName("waitChargesPerMinute")
        var waitChargesPerMinute: Double = 0.0

        @SerializedName("baseWaitMinute")
        var baseWaitMinute: Double = 0.0

        @SerializedName("additionalDistCharges")
        var additionalDistCharges: Double = 0.0

        @SerializedName("nightCharges")
        var nightCharges: Double = 0.0

        @SerializedName("surcharges")
        var surcharges: Double = 0.0

        @SerializedName("convenienceFees")
        var convenienceFees: Double = 0.0

        @SerializedName("tollCharges")
        var tollCharges: Double = 0.0

        @SerializedName("totalAdditionalCharges")
        var totalAdditionalCharges: Double = 0.0

        @SerializedName("totalPayableCharges")
        var totalPayableCharges: Double = 0.0

        @SerializedName("rules")
        var rules: String? = null

    }

    inner class VehicleDetail : Serializable {


        @SerializedName("id")
        var id: String? = null

        @SerializedName("union")
        var union: Union? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("category")
        var category: String? = null

        @SerializedName("noOfSeater")
        var noOfSeater: Int? = 0

        @SerializedName("image")
        var image: String? = null

    }

    inner class Union : Serializable {

        @SerializedName("id")
        var id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("registrationNumber")
        var registrationNumber: String? = null

        @SerializedName("headName")
        var headName: String? = null

        @SerializedName("permitType")
        var permitType: String? = null

        @SerializedName("city")
        var city: String? = null

    }


}
