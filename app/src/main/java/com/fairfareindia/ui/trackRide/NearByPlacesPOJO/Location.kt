package com.fairfareindia.ui.trackRide.NearByPlacesPOJO


import com.google.gson.annotations.SerializedName


data class Location(

	@field:SerializedName("lng")
	var lng: Double? = null,

	@field:SerializedName("lat")
	var lat: Double? = null,

	@field:SerializedName("timestamp")
	var timestamp: String? = null
)