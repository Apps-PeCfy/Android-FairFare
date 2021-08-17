package com.fairfareindia.ui.Login.pojo

import com.google.gson.annotations.SerializedName

class ErrorsItem {
    @SerializedName("message")
    var message: String? = null

    @SerializedName("key")
    var key: String? = null

    override fun toString(): String {
        return "ErrorsItem{" +
                "message = '" + message + '\'' +
                ",key = '" + key + '\'' +
                "}"
    }
}