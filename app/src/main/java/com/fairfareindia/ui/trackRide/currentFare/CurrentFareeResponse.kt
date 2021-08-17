package com.fairfareindia.ui.trackRide.currentFare

import com.google.gson.annotations.SerializedName

data class CurrentFareeResponse(

	@field:SerializedName("rate")
	val rate: Rate? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Rate(

	@field:SerializedName("tollCharge")
	val tollCharge: String? = null,

	@field:SerializedName("total")
	val total: String? = null,

	@field:SerializedName("additionalCharges")
	val additionalCharges: String? = null,

	@field:SerializedName("airportRateCardId")
	val airportRateCardId: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("vehicleRateCardId")
	val vehicleRateCardId: String? = null,

	@field:SerializedName("luggageCharge")
	val luggageCharge: String? = null,

	@field:SerializedName("nightCharge")
	val nightCharge: String? = null,

	@field:SerializedName("subTotal")
	val subTotal: String? = null,

	@field:SerializedName("waitingCharges")
	val waitingCharges: String? = null,

	@field:SerializedName("surCharge")
	val surCharge: String? = null
)