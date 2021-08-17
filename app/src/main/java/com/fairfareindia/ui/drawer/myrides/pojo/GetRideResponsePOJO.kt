package com.fairfareindia.ui.drawer.myrides.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GetRideResponsePOJO  {
	@SerializedName("path")
	var path: String? = null

	@SerializedName("lastPageUrl")
	var lastPageUrl: String? = null

	@SerializedName("total")
	var total = 0

	@SerializedName("firstPageUrl")
	var firstPageUrl: String? = null

	@SerializedName("nextPageUrl")
	var nextPageUrl: String? = null

	@SerializedName("perPage")
	var perPage = 0

	@SerializedName("data")
	var data: List<DataItem>? =
		null

	@SerializedName("lastPage")
	var lastPage = 0

	@SerializedName("message")
	var message: String? = null

	@SerializedName("currentPage")
	var currentPage = 0

	@SerializedName("prevPageUrl")
	var prevPageUrl: Any? = null

	override fun toString(): String {
		return "Response{" +
				"path = '" + path + '\'' +
				",lastPageUrl = '" + lastPageUrl + '\'' +
				",total = '" + total + '\'' +
				",firstPageUrl = '" + firstPageUrl + '\'' +
				",nextPageUrl = '" + nextPageUrl + '\'' +
				",perPage = '" + perPage + '\'' +
				",data = '" + data + '\'' +
				",lastPage = '" + lastPage + '\'' +
				",message = '" + message + '\'' +
				",currentPage = '" + currentPage + '\'' +
				",prevPageUrl = '" + prevPageUrl + '\'' +
				"}"
	}

	inner class DataItem {
		@SerializedName("dateTime")
		var dateTime: String? = null

		@SerializedName("destinationPlaceLat")
		var destinationPlaceLat: String? = null

		@SerializedName("fare")
		var fare:String? = null

		@SerializedName("vehicleName")
		var vehicleName: String? = null

		@SerializedName("vehicleImageUrl")
		var vehicleImageUrl: String? = null

		@SerializedName("originPlaceLong")
		var originPlaceLong: String? = null

		@SerializedName("originPlaceId")
		var originPlaceId: String? = null

		@SerializedName("vehicleRateCardId")
		var vehicleRateCardId: String? = null

		@SerializedName("cityId")
		var cityId: String? = null

		@SerializedName("originPlaceLat")
		var originPlaceLat: String? = null

		@SerializedName("userId")
		var userId = 0

		@SerializedName("destinationPlaceId")
		var destinationPlaceId: String? = null



		@SerializedName("vehicleNo")
		var vehicleNo: String? = null

		@SerializedName("reviewStar")
		var reviewStar = 0.00

		@SerializedName("originFullAddress")
		var originFullAddress: String? = null

		@SerializedName("destinationFullAddress")
		var destinationFullAddress: String? = null

		@SerializedName("cityName")
		var cityName: String? = null

		@SerializedName("overviewPolyline")
		var overviewPolyline: String? = null

		@SerializedName("airportRateCardId")
		var airportRateCardId: String? = null

		@SerializedName("driverName")
		var driverName: String? = null

		@SerializedName("id")
		var id = 0

		@SerializedName("destinationPlaceLong")
		var destinationPlaceLong: String? = null

		@SerializedName("badgeNo")
		var badgeNo: String? = null

		@SerializedName("status")
		var status: String? = null

		@SerializedName("rideStatus")
		var rideStatus: String? = null


		@SerializedName("estimatedTrackRide")
		var estimatedTrackRide: EstimatedTrackRide? =
			null



		override fun toString(): String {
			return "DataItem{" +
					"dateTime = '" + dateTime + '\'' +
					",destinationPlaceLat = '" + destinationPlaceLat + '\'' +
					",fare = '" + fare + '\'' +
					",vehicleName = '" + vehicleName + '\'' +
					",vehicleImageUrl = '" + vehicleImageUrl + '\'' +
					",originPlaceLong = '" + originPlaceLong + '\'' +
					",originPlaceId = '" + originPlaceId + '\'' +
					",vehicleRateCardId = '" + vehicleRateCardId + '\'' +
					",cityId = '" + cityId + '\'' +
					",originPlaceLat = '" + originPlaceLat + '\'' +
					",userId = '" + userId + '\'' +
					",destinationPlaceId = '" + destinationPlaceId + '\'' +
					",originFullAddress = '" + originFullAddress + '\'' +
					",vehicleNo = '" + vehicleNo + '\'' +
					",reviewStar = '" + reviewStar + '\'' +
					",destinationFullAddress = '" + destinationFullAddress + '\'' +
					",cityName = '" + cityName + '\'' +
					",overviewPolyline = '" + overviewPolyline + '\'' +
					",airportRateCardId = '" + airportRateCardId + '\'' +
					",driverName = '" + driverName + '\'' +
					",id = '" + id + '\'' +
					",destinationPlaceLong = '" + destinationPlaceLong + '\'' +
					",badgeNo = '" + badgeNo + '\'' +
					",status = '" + status + '\'' +
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