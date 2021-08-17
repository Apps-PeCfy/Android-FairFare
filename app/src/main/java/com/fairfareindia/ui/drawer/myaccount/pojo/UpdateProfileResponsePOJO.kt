package com.fairfareindia.ui.drawer.myaccount.pojo

import com.google.gson.annotations.SerializedName

class UpdateProfileResponsePOJO {
    @SerializedName("message")
    var message: String? = null

    @SerializedName("user")
    var user: User? =
        null

    override fun toString(): String {
        return "UpdateProfileResponsePOJO{" +
                "message = '" + message + '\'' +
                ",user = '" + user + '\'' +
                "}"
    }

    inner class User {
        @SerializedName("profession")
        var profession: String? = null

        @SerializedName("phone_no")
        var phoneNo: String? = null

        @SerializedName("gender")
        var gender: String? = null

        @SerializedName("country_phone_code")
        var countryPhoneCode: String? = null

        @SerializedName("date_of_birth")
        var dateOfBirth: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("location")
        var location: String? = null

        @SerializedName("profile_pic")
        var profilePic: String? = null

        @SerializedName("id")
        var id = 0

        @SerializedName("email")
        var email: String? = null

        override fun toString(): String {
            return "User{" +
                    "profession = '" + profession + '\'' +
                    ",phone_no = '" + phoneNo + '\'' +
                    ",gender = '" + gender + '\'' +
                    ",country_phone_code = '" + countryPhoneCode + '\'' +
                    ",date_of_birth = '" + dateOfBirth + '\'' +
                    ",name = '" + name + '\'' +
                    ",profile_pic = '" + profilePic + '\'' +
                    ",id = '" + id + '\'' +
                    ",email = '" + email + '\'' +
                    "}"
        }
    }
}