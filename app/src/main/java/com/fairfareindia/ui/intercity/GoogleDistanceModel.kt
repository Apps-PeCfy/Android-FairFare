package com.fairfareindia.ui.intercity

import com.google.gson.annotations.SerializedName

class GoogleDistanceModel {

    @SerializedName("destination_addresses")
    var destination_addresses: List<Any>? = null

    @SerializedName("origin_addresses")
    var origin_addresses: List<Any>? = null

    @SerializedName("rows")
    var rows: List<RowsItem>? = null

    @SerializedName("status")
    var status: String? = null



    inner class RowsItem {
        @SerializedName("elements")
        var elements: List<ElementItem>? = null
    }

    inner class ElementItem {
        @SerializedName("distance")
        var distance: InnerElementItem? = null

        @SerializedName("duration")
        var duration: InnerElementItem? = null

        @SerializedName("status")
        var status: String? = null
    }

    inner class InnerElementItem {
        @SerializedName("text")
        var text: String? = null

        @SerializedName("value")
        var value:String? = null
    }
}