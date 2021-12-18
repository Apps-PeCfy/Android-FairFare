package com.fairfareindia.ui.intercitycompareride

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class InterCityCompareRideModel : Serializable {
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

    @SerializedName("originPlaceId")
    var originPlaceId: String? = null

    @SerializedName("destinationPlaceId")
    var destinationPlaceId: String? = null

    @SerializedName("canStartRide")
    var canStartRide: String? = null

    @SerializedName("fromCityId")
    var fromCityId: String? = null

    @SerializedName("toCityId")
    var toCityId: String? = null

    @SerializedName("permitType")
    var permitType: String? = null

    @SerializedName("wayFlag")
    var wayFlag: String? = null

    @SerializedName("message")
    var message: String? = null


    inner class VehiclesItem : Serializable {

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

        @SerializedName("waitChargesPerHour")
        var waitChargesPerHour : Double? = 0.0

        @SerializedName("chargesPerLuggage")
        var chargesPerLuggage: Double? = 0.0

        @SerializedName("fareForAddDist")
        var fareForAddDist: Double? = 0.0

        @SerializedName("baseFare")
        var baseFare: Double? = 0.0

        @SerializedName("distanceType")
        var distanceType: String? = null

        @SerializedName("actualDistance")
        var actualDistance: String? = null

        @SerializedName("baseDistance")
        var baseDistance: String? = null

        @SerializedName("nightCharges")
        var nightCharges: Double? = 0.0

        @SerializedName("convenienceFees")
        var convenienceFees: Double? = 0.0

        @SerializedName("surcharges")
        var surcharges: Double? = 0.0

        @SerializedName("tollCharges")
        var tollCharges: Double? = 0.0

        @SerializedName("chargesLuggage")
        var chargesLuggage: Double? = 0.0

        @SerializedName("totalAdditionalCharges")
        var totalAdditionalCharges: Double? = 0.0

        @SerializedName("totalPayableCharges")
        var totalPayableCharges: Double? = 0.0

        @SerializedName("tolls")
        var tolls: ArrayList<Tolls> ? = ArrayList()

        @SerializedName("rules")
        var rules: String? = null

        @SerializedName("firstRideTotal")
        var firstRideTotal: String? = null

        @SerializedName("secondRideTotal")
        var secondRideTotal: String? = null

        @SerializedName("secondRidePercentageToPay")
        var secondRidePercentageToPay: String? = null

        @SerializedName("amountToCollect")
        var amountToCollect: String? = null

    }

    inner class Tolls : Serializable {
        @SerializedName("id")
        var id: String? = null

        @SerializedName("lat")
        var lat: String? = null

        @SerializedName("lng")
        var lng: String? = null

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

        @SerializedName("tagPrimary")
        var tagPrimary: Array<String>? = null

        @SerializedName("oneWay")
        var oneWay: String? = null

        @SerializedName("tagOneWay")
        var tagOneWay: String? = null

        @SerializedName("monthly")
        var monthly: String? = null

        @SerializedName("tagReturn")
        var tagReturn: String? = null

        @SerializedName("tagMonthly")
        var tagMonthly: String? = null

        @SerializedName("height")
        var height: String? = null

        @SerializedName("nhai")
        var nhai: Boolean? = null

        @SerializedName("latitude")
        var latitude: String? = null

        @SerializedName("longitude")
        var longitude: String? = null

        @SerializedName("charges")
        var charges: String? = null
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