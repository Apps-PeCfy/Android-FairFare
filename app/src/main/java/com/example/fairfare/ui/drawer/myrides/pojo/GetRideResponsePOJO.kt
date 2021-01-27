package com.example.fairfare.ui.drawer.myrides.pojo

import com.google.gson.annotations.SerializedName

class GetRideResponsePOJO {
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
		var fare = 0.00

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
}