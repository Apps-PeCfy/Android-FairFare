package com.fairfareindia.ui.drawer.ratecard.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RateCardModel : Serializable {
    @SerializedName("rateCards")
    var rateCards: List<RateCardsItem>? = null

    @SerializedName("message")
    var message: String? = null


    inner class RateCardsItem : Serializable {
        @SerializedName("id")
        var id = 0

        @SerializedName("name")
        var name: String? = null

        @SerializedName("category")
        var category: String? = null

        @SerializedName("noOfSeater")
        var noOfSeater = 0


        @SerializedName("image")
        var image: String? = null


        @SerializedName("rateCardsDetails")
        var rateCardsDetails: List<RateCardsDetailItem>? = null


    }

    inner class RateCardsDetailItem : Serializable {
        @SerializedName("title")
        var title: String? = null

        @SerializedName("message")
        var message: String? = null

    }
}