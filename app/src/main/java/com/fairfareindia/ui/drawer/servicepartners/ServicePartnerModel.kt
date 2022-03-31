package com.fairfareindia.ui.drawer.servicepartners

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class ServicePartnerModel {
    @SerializedName("data")
    val data: ArrayList<DataItem>? = null

    @SerializedName("message")
    val message: String? = null

    class DataItem {

        @SerializedName("id")
        val id: String? = null

        @SerializedName("name")
        val name: String? = null

        @SerializedName("registrationNumber")
        val registrationNumber: String? = null

        @SerializedName("headName")
        val headName: String? = null

        @SerializedName("permitType")
        val permitType: String? = null

        @SerializedName("city")
        val city: String? = null

        @SerializedName("razorpay_key")
        val razorpay_key: String? = null

        @SerializedName("razorpay_secret_key")
        val razorpay_secret_key: String? = null

        @SerializedName("address")
        val address: String? = null

        @SerializedName("email")
        val email: String? = null

        @SerializedName("country_phone_code")
        val country_phone_code: String? = null

        @SerializedName("phone_no")
        val phone_no: String? = null

        @SerializedName("route_served")
        val route_served: String? = null


    }
}

