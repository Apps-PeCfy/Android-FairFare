package com.fairfareindia.ui.drawer.notifications

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class NotificationModel  {
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


	inner class DataItem {


		@SerializedName("id")
		var id = 0

		@SerializedName("contextType")
		var contextType: String? = null

		@SerializedName("contextId")
		var contextId: String? = null

		@SerializedName("notificationType")
		var notificationType: String? = null

		@SerializedName("type")
		var type: String? = null

		@SerializedName("data")
		var data: InnerData? = null


		@SerializedName("message")
		var message: String? = null

		@SerializedName("created_at")
		var created_at: String? = null
	}

	inner class InnerData {

		@SerializedName("booking_id")
		var booking_id: String? = null

		@SerializedName("user_id")
		var user_id: String? = null

		@SerializedName("driver_id")
		var driver_id: String? = null

		@SerializedName("ride_id")
		var ride_id: String? = null

		@SerializedName("ride_status")
		var ride_status: String? = null

		@SerializedName("screen")
		var screen: String? = null


	}



}