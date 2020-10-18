package com.example.fairfare.ui.Login.pojo

import com.google.gson.annotations.SerializedName

class LoginResponsepojo {
    @SerializedName("redirectTo")
    var redirectTo: String? = null

    @SerializedName("token")
    var token: String? = null

    @SerializedName("message")
    var message: String? = null

    override fun toString(): String {
        return "LoginResponsepojo{" +
                "redirectTo = '" + redirectTo + '\'' +
                ",message = '" + message + '\'' +
                ",token = '" + token + '\'' +
                "}"
    }
}