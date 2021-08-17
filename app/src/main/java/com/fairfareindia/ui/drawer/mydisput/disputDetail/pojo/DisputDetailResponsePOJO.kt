package com.fairfareindia.ui.drawer.mydisput.disputDetail.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class DisputDetailResponsePOJO {
    @SerializedName("dispute")
    var dispute: Dispute? = null

    @SerializedName("message")
    var message: String? = null

    override fun toString(): String {
        return "DisputDetailResponsePOJO{" +
                "dispute = '" + dispute + '\'' +
                ",message = '" + message + '\'' +
                "}"
    }

    inner class Dispute {
        @SerializedName("dateTime")
        var dateTime: String? = null


        @SerializedName("comment")
        var comment: String? = null


        @SerializedName("vehicleName")
        var vehicleName: String? = null


        @SerializedName("startMeterReading")
        var startMeterReading: String? = null

        @SerializedName("actualMeterCharges")
        var actualMeterCharges: String? = null

        @SerializedName("rideId")
        var rideId = 0

        @SerializedName("type")
        var type: String? = null

        @SerializedName("originPlaceLat")
        var originPlaceLat: String? = null

        @SerializedName("ride")
        var ride: Ride? =
            null

        @SerializedName("destinationFullAddress")
        var destinationFullAddress: String? = null

        @SerializedName("reasonId")
        var reasonId = 0

        @SerializedName("id")
        var id = 0

        @SerializedName("endMeterReading")
        var endMeterReading: String? = null

        @SerializedName("badgeNo")
        var badgeNo: String? = null

        @SerializedName("reasonName")
        var reasonName: String? = null

        @SerializedName("destinationPlaceLat")
        var destinationPlaceLat: String? = null




        @SerializedName("images")
        var images: ArrayList<String?>? = null

        @SerializedName("vehicleImageUrl")
        var vehicleImageUrl: String? = null


        @SerializedName("originPlaceLong")
        var originPlaceLong: String? = null

        @SerializedName("originPlaceId")
        var originPlaceId: String? = null

        @SerializedName("disputeNo")
        var disputeNo: String? = null

        @SerializedName("reasons")
         val reasons: List<ReasonsItem>? = null


        @SerializedName("destinationPlaceId")
        var destinationPlaceId: String? = null

        @SerializedName("originFullAddress")
        var originFullAddress: String? = null

        @SerializedName("vehicleNo")
        var vehicleNo: String? = null

        @SerializedName("driverName")
        var driverName: String? = null


        @SerializedName("destinationPlaceLong")
        var destinationPlaceLong: String? = null

        @SerializedName("status")
        var status: String? = null

        override fun toString(): String {
            return "Dispute{" +
                    "dateTime = '" + dateTime + '\'' +
                    ",vehicleName = '" + vehicleName + '\'' +
                    ",startMeterReading = '" + startMeterReading + '\'' +
                    ",actualMeterCharges = '" + actualMeterCharges + '\'' +
                    ",rideId = '" + rideId + '\'' +
                    ",type = '" + type + '\'' +
                    ",originPlaceLat = '" + originPlaceLat + '\'' +
                    ",ride = '" + ride + '\'' +
                    ",destinationFullAddress = '" + destinationFullAddress + '\'' +
                    ",reasonId = '" + reasonId + '\'' +
                    ",id = '" + id + '\'' +
                    ",endMeterReading = '" + endMeterReading + '\'' +
                    ",badgeNo = '" + badgeNo + '\'' +
                    ",reasonName = '" + reasonName + '\'' +
                    ",destinationPlaceLat = '" + destinationPlaceLat + '\'' +
                    ",images = '" + images + '\'' +
                    ",vehicleImageUrl = '" + vehicleImageUrl + '\'' +
                    ",originPlaceLong = '" + originPlaceLong + '\'' +
                    ",originPlaceId = '" + originPlaceId + '\'' +
                    ",disputeNo = '" + disputeNo + '\'' +
                    ",destinationPlaceId = '" + destinationPlaceId + '\'' +
                    ",originFullAddress = '" + originFullAddress + '\'' +
                    ",vehicleNo = '" + vehicleNo + '\'' +
                    ",driverName = '" + driverName + '\'' +
                    ",destinationPlaceLong = '" + destinationPlaceLong + '\'' +
                    ",status = '" + status + '\'' +
                    "}"
        }
    }

    inner class Ride {
        @SerializedName("luggageCharges")
        var luggageCharges: String? = null

        @SerializedName("vehicleDetail")
        var vehicleDetail: VehicleDetail? =
            null

        @SerializedName("estimatedTrackRide")
        var estimatedTrackRide: EstimatedTrackRide? =
            null

        @SerializedName("endDate")
        var endDate: String? = null

        @SerializedName("vehicleRateCardId")
        var vehicleRateCardId = 0


        @SerializedName("luggageQuantity")
        var luggageQuantity: String? = null


        @SerializedName("userId")
        var userId = 0

        @SerializedName("nightCharges")
        var nightCharges: String? = null


        @SerializedName("airportRateCardId")
        var airportRateCardId: String? = null

        @SerializedName("scheduleDate")
        var scheduleDate: String? = null

        @SerializedName("actualTrackRide")
        var actualTrackRide: ActualTrackRide? =
            null

        @SerializedName("id")
        var id = 0

        @SerializedName("status")
        var status: String? = null

        override fun toString(): String {
            return "Ride{" +
                    "luggageCharges = '" + luggageCharges + '\'' +
                    ",vehicleDetail = '" + vehicleDetail + '\'' +
                    ",estimatedTrackRide = '" + estimatedTrackRide + '\'' +
                    ",endDate = '" + endDate + '\'' +
                    ",vehicleRateCardId = '" + vehicleRateCardId + '\'' +
                    ",userId = '" + userId + '\'' +
                    ",nightCharges = '" + nightCharges + '\'' +
                    ",airportRateCardId = '" + airportRateCardId + '\'' +
                    ",scheduleDate = '" + scheduleDate + '\'' +
                    ",actualTrackRide = '" + actualTrackRide + '\'' +
                    ",id = '" + id + '\'' +
                    ",status = '" + status + '\'' +
                    "}"
        }
    }

    inner class ActualTrackRide {
        @SerializedName("destinationPlaceLat")
        var destinationPlaceLat: String? = null

        @SerializedName("distance")
        var distance: String? = null



        @SerializedName("tolls")
        var tolls: List<TollsItem>? = null

   @SerializedName("waitings")
        var waitings: List<WaitingsItem1>? = null


        @SerializedName("surCharge")
        var surCharge: String? = null

        @SerializedName("waitingTime")
        var waitingTime: String? = null

        @SerializedName("waitingCharges")
        var waitingCharges: String? = null

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

        @SerializedName("originFullAddress")
        var originFullAddress: Any? = null

        @SerializedName("duration")
        var duration: String? = null

        @SerializedName("destinationFullAddress")
        var destinationFullAddress: Any? = null

        @SerializedName("overviewPolyline")
        var overviewPolyline: String? = null

        @SerializedName("totalCharges")
        var totalCharges:String?=null


        @SerializedName("tollCharges")
        var tollCharges: String? = null

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
                    ",type = '" + type + '\'' +
                    ",rideId = '" + rideId + '\'' +
                    ",originPlaceLat = '" + originPlaceLat + '\'' +
                    ",destinationPlaceId = '" + destinationPlaceId + '\'' +
                    ",originFullAddress = '" + originFullAddress + '\'' +
                    ",duration = '" + duration + '\'' +
                    ",destinationFullAddress = '" + destinationFullAddress + '\'' +
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

        @SerializedName("surCharge")
        var surCharge: String? = null

        @SerializedName("tolls")
        var tolls: List<TollsItem>? = null


        @SerializedName("waitingTime")
        var waitingTime: String? = null

        @SerializedName("waitingCharges")
        var waitingCharges: String? = null


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

        @SerializedName("originFullAddress")
        var originFullAddress: Any? = null

        @SerializedName("duration")
        var duration: String? = null

        @SerializedName("destinationFullAddress")
        var destinationFullAddress: Any? = null

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
                    ",originFullAddress = '" + originFullAddress + '\'' +
                    ",duration = '" + duration + '\'' +
                    ",destinationFullAddress = '" + destinationFullAddress + '\'' +
                    ",overviewPolyline = '" + overviewPolyline + '\'' +
                    ",totalCharges = '" + totalCharges + '\'' +
                    ",id = '" + id + '\'' +
                    ",destinationPlaceLong = '" + destinationPlaceLong + '\'' +
                    ",subTotalCharges = '" + subTotalCharges + '\'' +
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


    class ReasonsItem {
        @SerializedName("reason")
        var reason: String? = null

        @SerializedName("updated_at")
        var updatedAt: String? = null

        @SerializedName("description")
        var description: Any? = null

        @SerializedName("created_at")
        var createdAt: String? = null


        @SerializedName("id")
        var id = 0

        @SerializedName("deleted_at")
        var deletedAt: Any? = null

        override fun toString(): String {
            return "ReasonsItem{" +
                    "reason = '" + reason + '\'' +
                    ",updated_at = '" + updatedAt + '\'' +
                    ",description = '" + description + '\'' +
                    ",created_at = '" + createdAt + '\'' +
                    ",id = '" + id + '\'' +
                    ",deleted_at = '" + deletedAt + '\'' +
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