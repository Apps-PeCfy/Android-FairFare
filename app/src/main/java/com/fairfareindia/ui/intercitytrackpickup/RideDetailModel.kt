package com.fairfareindia.ui.intercitytrackpickup
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RideDetailModel {
    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: DataItem? = null

    inner class DataItem {

        @SerializedName("id")
        var id = 0

        @SerializedName("total_wait_time")
        var total_wait_time : Int = 0

        @SerializedName("totalfare")
        var totalfare : Double = 0.0

        @SerializedName("user")
        var user: User? = null

        @SerializedName("driver")
        var driver: Driver? = null

        @SerializedName("userId")
        var userId = 0

        @SerializedName("permitType")
        var permitType: String? = null

        @SerializedName("rideOtp")
        var rideOtp: String? = null

        @SerializedName("rules")
        var rules: String? = null

        @SerializedName("cityName")
        var cityName: String? = null

        @SerializedName("cityId")
        var cityId: String? = null

        @SerializedName("dateTime")
        var dateTime: String? = null

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

        @SerializedName("originAddress")
        var originAddress: String? = null

        @SerializedName("destinationAddress")
        var destinationAddress: String? = null

        @SerializedName("originLatitude")
        var originLatitude: String? = null

        @SerializedName("originLongitude")
        var originLongitude: String? = null

        @SerializedName("destinationLatitude")
        var destinationLatitude: String? = null

        @SerializedName("destinationLongitude")
        var destinationLongitude: String? = null


        @SerializedName("rewards")
        var rewards: String? = null

        @SerializedName("totalunPaid")
        var totalunPaid: Double = 0.0

        @SerializedName("balancePaymentStatus")
        var balancePaymentStatus: String ?= null


        @SerializedName("totalPaid")
        var totalPaid: Double = 0.0

        @SerializedName("booking_id")
        var booking_id: String ?= null


        @SerializedName("canToll")
        var canToll: String? = null

        @SerializedName("fare")
        var fare: String? = null

        @SerializedName("vehicleNo")
        var vehicleNo: String? = null

        @SerializedName("badgeNo")
        var badgeNo: String? = null

        @SerializedName("totalDistTravelled")
        var totalDistTravelled: String? = null

        @SerializedName("start_date")
        var start_date: String? = null

        @SerializedName("rideStatus")
        var rideStatus: String? = null

        @SerializedName("driverName")
        var driverName: String? = null

        @SerializedName("reviewStar")
        var reviewStar: String? = null


        @SerializedName("estimatedTrackRide")
        var estimatedTrackRide: Estimated? = null

        @SerializedName("actualTrackRide")
        var actualTrackRide: Estimated? = null

        @SerializedName("waitings")
        var waitings: ArrayList<WaitTimeModel> = ArrayList()

    }

    inner class WaitTimeModel {

        @SerializedName("id")
        var id = 0

        @SerializedName("lat")
        var lat: String? = null

        @SerializedName("long")
        var long: String? = null

        @SerializedName("fullAddress")
        var fullAddress: String? = null

        @SerializedName("waitAt")
        var waitAt: String? = null

        @SerializedName("startTime")
        var startTime: String? = null

        @SerializedName("endTime")
        var endTime: String? = null

        @SerializedName("waitingTime")
        var waitingTime: String? = null

    }


    inner class Estimated {

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
        var distance: Double = 0.0

        @SerializedName("duration")
        var duration: String? = null

        @SerializedName("subTotalCharges")
        var subTotalCharges: Int? = null

        @SerializedName("totalCharges")
        var totalCharges: String? = null

        @SerializedName("baseDistance")
        var baseDistance: Double = 0.0

        @SerializedName("convenienceFees")
        var convenienceFees: String? = null

        @SerializedName("additionalDistance")
        var additionalDistance: String? = null

        @SerializedName("totalDuration")
        var totalDuration: String? = null

        @SerializedName("basicFare")
        var basicFare: Int? = null

        @SerializedName("luggageCharges")
        var luggageCharges: String? = null

        @SerializedName("rideTime")
        var rideTime: String? = null

        @SerializedName("totalTime")
        var totalTime: String? = null

        @SerializedName("totalTimeMinute")
        var totalTimeMinute: String? = null

        @SerializedName("additionalDistanceCharges")
        var additionalDistanceCharges: String? = null

        @SerializedName("tollCharges")
        var tollCharges: String? = null

        @SerializedName("tolls")
        var tolls: ArrayList<Tolls> ? = ArrayList()

        @SerializedName("totalAdditionalCharges")
        var totalAdditionalCharges: String? = null

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

    inner class User {

        @SerializedName("id")
        var id = 0

        @SerializedName("name")
        var name: String? = null

        @SerializedName("email")
        var email : String? = null

        @SerializedName("country_phone_code")
        var country_phone_code: String? = null

        @SerializedName("phone_no")
        var phone_no: String? = null

        @SerializedName("gender")
        var gender: String? = null

        @SerializedName("profession")
        var profession: String? = null

        @SerializedName("date_of_birth")
        var date_of_birth: String? = null

        @SerializedName("location")
        var location: String? = null

        @SerializedName("city")
        var city: String? = null

        @SerializedName("profile_pic")
        var profile_pic: String? = null

        @SerializedName("rewards")
        var rewards: String? = null

    }

    inner class Driver {

        @SerializedName("id")
        var id = 0

        @SerializedName("name")
        var name: String? = null

        @SerializedName("email")
        var email : String? = null

        @SerializedName("countryPhoneCode")
        var countryPhoneCode: String? = null

        @SerializedName("phoneNo")
        var phoneNo: String? = null

        @SerializedName("gender")
        var gender: String? = null

        @SerializedName("profession")
        var profession: String? = null

        @SerializedName("dateOfBirth")
        var dateOfBirth: String? = null

        @SerializedName("location")
        var location: String? = null

        @SerializedName("city")
        var city: String? = null

        @SerializedName("profilePic")
        var profilePic: String? = null

        @SerializedName("providerId")
        var providerId: String? = null

        @SerializedName("licenseNumber")
        var licenseNumber: String? = null

        @SerializedName("licenseValidityDate")
        var licenseValidityDate: String? = null

        @SerializedName("licenseImage")
        var licenseImage: String? = null

        @SerializedName("badgeNumber")
        var badgeNumber: String? = null

        @SerializedName("badgeImage")
        var badgeImage: String? = null

        @SerializedName("aadharNumber")
        var aadharNumber: String? = null

        @SerializedName("aadharImage")
        var aadharImage: String? = null

        @SerializedName("panNumber")
        var panNumber: String? = null

        @SerializedName("panCardImage")
        var panCardImage: String? = null

        @SerializedName("driveType")
        var driveType: String? = null

        @SerializedName("longitude")
        var longitude: String? = null

        @SerializedName("bearing")
        var bearing: Float = 0F

        @SerializedName("bearing_accuracy_degrees")
        var bearing_accuracy_degrees: Float = 0F

        @SerializedName("latitude")
        var latitude: String? = null

        @SerializedName("preferredLanguage")
        var preferredLanguage: String? = null

        @SerializedName("isOnline")
        var isOnline: String? = null

    }


}
