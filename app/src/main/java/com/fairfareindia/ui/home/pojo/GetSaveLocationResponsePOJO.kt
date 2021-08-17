package com.fairfareindia.ui.home.pojo

import com.google.gson.annotations.SerializedName

class GetSaveLocationResponsePOJO {
    @SerializedName("first_page_url")
    var firstPageUrl: String? = null

    @SerializedName("path")
    var path: String? = null

    @SerializedName("per_page")
    var perPage = 0

    @SerializedName("total")
    var total = 0

    @SerializedName("data")
    var data: List<DataItem>? =
        null

    @SerializedName("last_page")
    var lastPage = 0

    @SerializedName("last_page_url")
    var lastPageUrl: String? = null

    @SerializedName("next_page_url")
    var nextPageUrl: Any? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("prev_page_url")
    var prevPageUrl: Any? = null

    @SerializedName("current_page")
    var currentPage = 0

    override fun toString(): String {
        return "Response{" +
                "first_page_url = '" + firstPageUrl + '\'' +
                ",path = '" + path + '\'' +
                ",per_page = '" + perPage + '\'' +
                ",total = '" + total + '\'' +
                ",data = '" + data + '\'' +
                ",last_page = '" + lastPage + '\'' +
                ",last_page_url = '" + lastPageUrl + '\'' +
                ",next_page_url = '" + nextPageUrl + '\'' +
                ",message = '" + message + '\'' +
                ",prev_page_url = '" + prevPageUrl + '\'' +
                ",current_page = '" + currentPage + '\'' +
                "}"
    }

    inner class DataItem {
        @SerializedName("country")
        var country: String? = null

        @SerializedName("user_id")
        var userId = 0

        @SerializedName("city")
        var city: String? = null

        @SerializedName("category")
        var category: String? = null

        @SerializedName("id")
        var id = 0

        @SerializedName("state")
        var state: String? = null

        @SerializedName("full_address")
        var fullAddress: String? = null

        @SerializedName("place_id")
        var placeId: String? = null

        override fun toString(): String {
            return "DataItem{" +
                    "country = '" + country + '\'' +
                    ",user_id = '" + userId + '\'' +
                    ",city = '" + city + '\'' +
                    ",id = '" + id + '\'' +
                    ",state = '" + state + '\'' +
                    ",full_address = '" + fullAddress + '\'' +
                    ",place_id = '" + placeId + '\'' +
                    "}"
        }
    }
}