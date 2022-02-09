package com.fairfareindia.ui.intercityviewride

import com.google.gson.annotations.SerializedName

class RazorPayModel {
    @SerializedName("message")
    var message: String ? = null

    @SerializedName("data")
    var data:  DataItem? = null


    inner class DataItem {
        @SerializedName("order_id")
        var order_id: String ? = null

        @SerializedName("entity")
        var entity: String ? = null

        @SerializedName("amount")
        var amount: String ? = null

        @SerializedName("amount_paid")
        var amount_paid: String ? = null

        @SerializedName("amount_due")
        var amount_due: String ? = null

        @SerializedName("currency")
        var currency: String ? = null

        @SerializedName("receipt")
        var receipt: String ? = null

        @SerializedName("status")
        var status: String ? = null

        @SerializedName("attempts")
        var attempts: String ? = null

        @SerializedName("created_at")
        var created_at: String ? = null

        @SerializedName("razorpay_key")
        var razorpay_key: String ? = null

        @SerializedName("razorpay_secret_key")
        var razorpay_secret_key: String ? = null


    }
}