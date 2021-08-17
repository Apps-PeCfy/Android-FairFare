package com.fairfareindia.ui.Login.pojo

import com.google.gson.annotations.SerializedName

class LoginResponsepojo {
    @SerializedName("redirectTo")
    var redirectTo: String? = null

    @SerializedName("token")
    var token: String? = null

    @SerializedName("user")
    var user: User? = null


    @SerializedName("message")
    var message: String? = null

    override fun toString(): String {
        return "LoginResponsepojo{" +
                "redirectTo = '" + redirectTo + '\'' +
                ",message = '" + message + '\'' +
                ",token = '" + token + '\'' +
                "}"
    }

    inner class User {
        @SerializedName("phone_no")
        var phoneNo: String? = null

        @SerializedName("profession")
        var profession: String? = null


        @SerializedName("gender")
        var gender: String? = null

        @SerializedName("country_phone_code")
        var countryPhoneCode: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("date_of_birth")
        var dateOfBirth: String? = null

        @SerializedName("profile_pic")
        var profilePic: String? = null


        @SerializedName("location")
        val location: String? = null


        @SerializedName("rewards")
        val rewards: String? = null


        @SerializedName("id")
        var id = 0

        @SerializedName("email")
        var email: String? = null

        override fun toString(): String {
            return "User{" +
                    "phone_no = '" + phoneNo + '\'' +
                    ",date_of_birth = '" + dateOfBirth + '\'' +
                    ",profile_pic = '" + profilePic + '\'' +
                    ",gender = '" + gender + '\'' +
                    ",profession = '" + profession + '\'' +
                    ",country_phone_code = '" + countryPhoneCode + '\'' +
                    ",name = '" + name + '\'' +
                    ",id = '" + id + '\'' +
                    ",email = '" + email + '\'' +
                    "}"
        }
    }

}