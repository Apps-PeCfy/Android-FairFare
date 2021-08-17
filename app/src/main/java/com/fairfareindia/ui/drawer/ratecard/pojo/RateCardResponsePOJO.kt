package com.fairfareindia.ui.drawer.ratecard.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RateCardResponsePOJO : Serializable {
    @SerializedName("rateCards")
    var rateCards: List<RateCardsItem>? = null

    @SerializedName("message")
    var message: String? = null

    override fun toString(): String {
        return "RateCardResponsePOJO{" +
                "rateCards = '" + rateCards + '\'' +
                ",message = '" + message + '\'' +
                "}"
    }

    inner class RateCardsItem : Serializable {
        @SerializedName("image")
        var image: String? = null

        @SerializedName("rateCards")
        var rateCards: List<RateCardsItem>? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("rateCardType")
        var rateCardType: String? = null

        @SerializedName("noOfSeater")
        var noOfSeater = 0

        @SerializedName("id")
        var id = 0

        @SerializedName("category")
        var category: String? = null

        @SerializedName("surcharge")
        var surcharge: String? = null

        @SerializedName("chargesPerLuggage")
        var chargesPerLuggage: String? = null

        @SerializedName("waitingCharges")
        var waitingCharges: String? = null

        @SerializedName("minBaseDistance")
        var minBaseDistance: String? = null

        @SerializedName("distanceType")
        var distanceType: String? = null

        @SerializedName("distanceSlab")
        var distanceSlab: String? = null

        @SerializedName("type")
        var type: String? = null

        @SerializedName("nightCharges")
        var nightCharges: String? = null

        @SerializedName("nightChargesInPercentage")
        var nightChargesInPercentage: String? = null

        @SerializedName("minBaseFare")
        var minBaseFare: String? = null

        @SerializedName("fareAfterMinbdist")
        var fareAfterMinbdist: String? = null

        override fun toString(): String {
            return "RateCardsItem{" +
                    "image = '" + image + '\'' +
                    ",rateCards = '" + rateCards + '\'' +
                    ",name = '" + name + '\'' +
                    ",noOfSeater = '" + noOfSeater + '\'' +
                    ",id = '" + id + '\'' +
                    ",category = '" + category + '\'' +
                    ",surcharge = '" + surcharge + '\'' +
                    ",chargesPerLuggage = '" + chargesPerLuggage + '\'' +
                    ",waitingCharges = '" + waitingCharges + '\'' +
                    ",minBaseDistance = '" + minBaseDistance + '\'' +
                    ",distanceType = '" + distanceType + '\'' +
                    ",distanceSlab = '" + distanceSlab + '\'' +
                    ",type = '" + type + '\'' +
                    ",nightCharges = '" + nightCharges + '\'' +
                    ",minBaseFare = '" + minBaseFare + '\'' +
                    ",fareAfterMinbdist = '" + fareAfterMinbdist + '\'' +
                    "}"
        }
    }
}