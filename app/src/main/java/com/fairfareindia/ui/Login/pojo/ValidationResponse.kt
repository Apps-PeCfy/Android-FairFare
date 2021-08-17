package com.fairfareindia.ui.Login.pojo

import com.google.gson.annotations.SerializedName

class ValidationResponse {
    @SerializedName("message")
    var message: String? = null

    @SerializedName("errors")
    var errors: List<ErrorsItem>? = null

    override fun toString(): String {
        return "ValidationResponse{" +
                "message = '" + message + '\'' +
                ",errors = '" + errors + '\'' +
                "}"
    }
}