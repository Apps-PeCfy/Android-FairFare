package com.fairfareindia.ui.home.pojo

import com.google.gson.annotations.SerializedName
import java.util.*

class GeneralSettingModel {
    @SerializedName("data")
    private val data: ArrayList<DataItem>? = null

    @SerializedName("message")
    private val message: String? = null

    class DataItem {

        @SerializedName("id")
        private val id: String? = null

        @SerializedName("key")
        private val key: String? = null

        @SerializedName("value")
        private val value: String? = null

        @SerializedName("context_type")
        private val context_type: String? = null

        @SerializedName("context_id")
        private val context_id: String? = null
    }
}