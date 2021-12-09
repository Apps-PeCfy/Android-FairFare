package com.fairfareindia.ui.home.pojo

import com.google.gson.annotations.SerializedName
import java.util.*

class GeneralSettingModel {
    @SerializedName("data")
    val data: ArrayList<DataItem>? = null

    @SerializedName("message")
    val message: String? = null

    class DataItem {

        @SerializedName("id")
        val id: String? = null

        @SerializedName("key")
        val key: String? = null

        @SerializedName("value")
        val value: String? = null

        @SerializedName("context_type")
        val context_type: String? = null

        @SerializedName("context_id")
        val context_id: String? = null
    }
}