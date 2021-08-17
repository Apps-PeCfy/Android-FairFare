package com.fairfareindia.ui.drawer.mydisput.pojo

import com.google.gson.annotations.SerializedName

class DeleteDisputResponsePOJO {
    @SerializedName("message")
    var message: String? = null

    override fun toString(): String {
        return "DeleteDisputResponsePOJO{" +
                "message = '" + message + '\'' +
                "}"
    }
}