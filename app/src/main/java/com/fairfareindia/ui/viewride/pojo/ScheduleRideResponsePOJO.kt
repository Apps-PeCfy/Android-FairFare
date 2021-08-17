package com.fairfareindia.ui.viewride.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ScheduleRideResponsePOJO : Serializable {
   @SerializedName("message")
   var message: String? = null

   @SerializedName("ride")
   var ride: Ride? = null



   class Ride : Serializable{
      @SerializedName("luggageCharges")
      var luggageCharges: String? = null

      @SerializedName("vehicleDetail")
      var vehicleDetail: VehicleDetail? =
         null

      @SerializedName("estimatedTrackRide")
      var estimatedTrackRide: EstimatedTrackRide? =
         null

      @SerializedName("endDate")
      var endDate: Any? = null

      @SerializedName("luggageRuantity")
      var luggageRuantity = 0

      @SerializedName("airportRateCardId")
      var airportRateCardId: Any? = null

      @SerializedName("scheduleDate")
      var scheduleDate: String? = null

      @SerializedName("vehicleRateCardId")
      var vehicleRateCardId = 0

      @SerializedName("id")
      var id = 0

      @SerializedName("userId")
      var userId = 0

      @SerializedName("canToll")
      var canToll : String? = null

      @SerializedName("nightCharges")
      var nightCharges: String? = null

      @SerializedName("status")
      var status: String? = null

      override fun toString(): String {
         return "Ride{" +
                 "luggageCharges = '" + luggageCharges + '\'' +
                 ",vehicleDetail = '" + vehicleDetail + '\'' +
                 ",estimatedTrackRide = '" + estimatedTrackRide + '\'' +
                 ",endDate = '" + endDate + '\'' +
                 ",luggageRuantity = '" + luggageRuantity + '\'' +
                 ",airportRateCardId = '" + airportRateCardId + '\'' +
                 ",scheduleDate = '" + scheduleDate + '\'' +
                 ",vehicleRateCardId = '" + vehicleRateCardId + '\'' +
                 ",id = '" + id + '\'' +
                 ",userId = '" + userId + '\'' +
                 ",nightCharges = '" + nightCharges + '\'' +
                 ",status = '" + status + '\'' +
                 "}"
      }
   }


   class VehicleDetail:Serializable {
      @SerializedName("vehicleNo")
      var vehicleNo: String? = null

      @SerializedName("vehicleNoImages")
      var vehicleNoImages: List<Any>? = null

      @SerializedName("startMeterImages")
      var startMeterImages: List<Any>? = null

      @SerializedName("driverImages")
      var driverImages: List<Any>? = null

      @SerializedName("badgeNoImages")
      var badgeNoImages: List<Any>? = null

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
                 ",vehicleNoImages = '" + vehicleNoImages + '\'' +
                 ",startMeterImages = '" + startMeterImages + '\'' +
                 ",driverImages = '" + driverImages + '\'' +
                 ",badgeNoImages = '" + badgeNoImages + '\'' +
                 ",startMeterReading = '" + startMeterReading + '\'' +
                 ",driverName = '" + driverName + '\'' +
                 ",id = '" + id + '\'' +
                 ",badgeNo = '" + badgeNo + '\'' +
                 "}"
      }
   }

   class EstimatedTrackRide:Serializable {
      @SerializedName("destinationPlaceLat")
      var destinationPlaceLat: String? = null

      @SerializedName("distance")
      var distance: String? = null

      @SerializedName("originPlaceLong")
      var originPlaceLong: String? = null

      @SerializedName("originPlaceId")
      var originPlaceId: String? = null

      @SerializedName("type")
      var type: String? = null

      @SerializedName("rideId")
      var rideId = 0

      @SerializedName("originPlaceLat")
      var originPlaceLat: String? = null

      @SerializedName("destinationPlaceId")
      var destinationPlaceId: String? = null

      @SerializedName("duration")
      var duration: String? = null

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
                 ",type = '" + type + '\'' +
                 ",rideId = '" + rideId + '\'' +
                 ",originPlaceLat = '" + originPlaceLat + '\'' +
                 ",destinationPlaceId = '" + destinationPlaceId + '\'' +
                 ",duration = '" + duration + '\'' +
                 ",overviewPolyline = '" + overviewPolyline + '\'' +
                 ",totalCharges = '" + totalCharges + '\'' +
                 ",id = '" + id + '\'' +
                 ",destinationPlaceLong = '" + destinationPlaceLong + '\'' +
                 ",subTotalCharges = '" + subTotalCharges + '\'' +
                 "}"
      }
   }
}