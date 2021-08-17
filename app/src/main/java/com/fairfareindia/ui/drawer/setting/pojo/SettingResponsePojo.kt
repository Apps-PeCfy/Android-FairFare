package com.fairfareindia.ui.drawer.setting.pojo

import com.google.gson.annotations.SerializedName

class SettingResponsePojo {
    @SerializedName("userSetting")
    var userSetting: UserSetting? =
        null

    @SerializedName("message")
    var message: String? = null

    override fun toString(): String {
        return "SettingResponsePojo{" +
                "userSetting = '" + userSetting + '\'' +
                ",message = '" + message + '\'' +
                "}"
    }

    inner class UserSetting {
        @SerializedName("city")
        var city: String? = null

        @SerializedName("timeFormat")
        var timeFormat: String? = null

        @SerializedName("language")
        var language: String? = null

        @SerializedName("currency")
        var currency: String? = null

        @SerializedName("id")
        var id = 0

        @SerializedName("userId")
        var userId = 0

        @SerializedName("measurementUnit")
        var measurementUnit: String? = null

        override fun toString(): String {
            return "UserSetting{" +
                    "city = '" + city + '\'' +
                    ",timeFormat = '" + timeFormat + '\'' +
                    ",language = '" + language + '\'' +
                    ",currency = '" + currency + '\'' +
                    ",id = '" + id + '\'' +
                    ",userId = '" + userId + '\'' +
                    ",measurementUnit = '" + measurementUnit + '\'' +
                    "}"
        }
    }
}