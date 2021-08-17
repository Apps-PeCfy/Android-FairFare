package com.fairfareindia.ui.trackRide.NearByPlacesPOJO


import com.google.gson.annotations.SerializedName


data class NearByResponse(

	@field:SerializedName("html_attributions")
	val htmlAttributions: List<Any?>? = null,

	@field:SerializedName("results")
	val results: List<ResultsItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)