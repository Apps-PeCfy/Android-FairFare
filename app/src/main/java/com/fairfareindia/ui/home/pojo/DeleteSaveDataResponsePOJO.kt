package com.fairfareindia.ui.home.pojo

import com.google.gson.annotations.SerializedName

class DeleteSaveDataResponsePOJO {
    @SerializedName("data")
    var data: List<Any>? = null

    @SerializedName("message")
    var message: String? = null

    override fun toString(): String {
        return "DeleteSaveDataResponsePOJO{" +
                "data = '" + data + '\'' +
                ",message = '" + message + '\'' +
                "}"
    }
}