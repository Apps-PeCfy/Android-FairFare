package com.fairfareindia.ui.disputs.pojo

import com.google.gson.annotations.SerializedName

class DisputesReasonResponsePOJO {
    @SerializedName("data")
    var data: List<DataItem>? =
        null

    @SerializedName("message")
    var message: String? = null

    override fun toString(): String {
        return "DisputesReasonResponsePOJO{" +
                "data = '" + data + '\'' +
                ",message = '" + message + '\'' +
                "}"
    }

    inner class DataItem {
        @SerializedName("reason")
        var reason: String? = null

        @SerializedName("id")
        var id = 0

        override fun toString(): String {
            return "DataItem{" +
                    "reason = '" + reason + '\'' +
                    ",id = '" + id + '\'' +
                    "}"
        }
    }
}