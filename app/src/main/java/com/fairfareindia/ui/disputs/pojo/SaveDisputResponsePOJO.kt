package com.fairfareindia.ui.disputs.pojo

import com.google.gson.annotations.SerializedName

class SaveDisputResponsePOJO {
    @SerializedName("message")
    var message: String? = null

    override fun toString(): String {
        return "SaveDisputResponsePOJO{" +
                "message = '" + message + '\'' +
                "}"
    }
}