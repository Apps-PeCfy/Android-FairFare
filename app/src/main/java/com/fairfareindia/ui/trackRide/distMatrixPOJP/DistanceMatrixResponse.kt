package com.fairfareindia.ui.trackRide.distMatrixPOJP

import com.google.gson.annotations.SerializedName

class DistanceMatrixResponse {
    @SerializedName("destination_addresses")
    var destinationAddresses: List<String>? = null

    @SerializedName("rows")
    var rows: List<RowsItem>? = null

    @SerializedName("origin_addresses")
    var originAddresses: List<String>? = null

    @SerializedName("status")
    var status: String? = null

    override fun toString(): String {
        return "DistanceMatrixResponse{" +
                "destination_addresses = '" + destinationAddresses + '\'' +
                ",rows = '" + rows + '\'' +
                ",origin_addresses = '" + originAddresses + '\'' +
                ",status = '" + status + '\'' +
                "}"
    }

    inner class RowsItem {
        @SerializedName("elements")
        var elements: List<ElementsItem>? = null

        override fun toString(): String {
            return "RowsItem{" +
                    "elements = '" + elements + '\'' +
                    "}"
        }
    }

    inner class ElementsItem {
        @SerializedName("duration")
        var duration: Duration? =
            null

        @SerializedName("distance")
        var distance: Distance? =
            null

        @SerializedName("status")
        var status: String? = null

        override fun toString(): String {
            return "ElementsItem{" +
                    "duration = '" + duration + '\'' +
                    ",distance = '" + distance + '\'' +
                    ",status = '" + status + '\'' +
                    "}"
        }
    }

    inner class Duration {
        @SerializedName("text")
        var text: String? = null

        @SerializedName("value")
        var value = 0

        override fun toString(): String {
            return "Duration{" +
                    "text = '" + text + '\'' +
                    ",value = '" + value + '\'' +
                    "}"
        }
    }

    inner class Distance {
        @SerializedName("text")
        var text: String? = null

        @SerializedName("value")
        var value = 0

        override fun toString(): String {
            return "Distance{" +
                    "text = '" + text + '\'' +
                    ",value = '" + value + '\'' +
                    "}"
        }
    }
}