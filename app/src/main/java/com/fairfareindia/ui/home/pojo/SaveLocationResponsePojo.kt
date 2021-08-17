package com.fairfareindia.ui.home.pojo

import com.google.gson.annotations.SerializedName

class SaveLocationResponsePojo {
    @SerializedName("data")
    var data: Data? = null

    @SerializedName("message")
    var message: String? = null

    override fun toString(): String {
        return "SaveLocationResponsePojo{" +
                "data = '" + data + '\'' +
                ",message = '" + message + '\'' +
                "}"
    }

    inner class Data {
        @SerializedName("country")
        var country: String? = null

        @SerializedName("updated_at")
        var updatedAt: String? = null

        @SerializedName("city")
        var city: String? = null

        @SerializedName("user_id")
        var userId = 0

        @SerializedName("created_at")
        var createdAt: String? = null

        @SerializedName("state")
        var state: String? = null

        @SerializedName("full_address")
        var fullAddress: String? = null

        @SerializedName("id")
        var id = 0

        @SerializedName("type")
        var type: String? = null

        @SerializedName("place_id")
        var placeId: String? = null

        override fun toString(): String {
            return "Data{" +
                    "country = '" + country + '\'' +
                    ",updated_at = '" + updatedAt + '\'' +
                    ",city = '" + city + '\'' +
                    ",user_id = '" + userId + '\'' +
                    ",created_at = '" + createdAt + '\'' +
                    ",state = '" + state + '\'' +
                    ",full_address = '" + fullAddress + '\'' +
                    ",id = '" + id + '\'' +
                    ",type = '" + type + '\'' +
                    ",place_id = '" + placeId + '\'' +
                    "}"
        }
    }
}